import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.on
import dev.kord.gateway.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private class Bot(val kord: Kord) {
	private companion object {
		const val DICE_PATTERN = "(120|1[01]\\d|[1-9]\\d|[1-9])"
		const val INTERACTION_PATTERN = "[Rr]oll "
		val INTERACTION_REGEX = Regex("$INTERACTION_PATTERN.*")
		val ROLL_REGEX = Regex("$INTERACTION_PATTERN(${DICE_PATTERN}d)?$DICE_PATTERN")
	}

	private object REPLIES {
		private val FILE = File(".replies.dicebot")

		operator fun plusAssign(reply: BotReply) {
			val serialized = Json.encodeToString(reply)
			FILE.appendText("$serialized\n")
		}

		operator fun minusAssign(reply: BotReply) {
			val serialized = Json.encodeToString(reply)
			FILE.writeText(FILE.readText().replace("$serialized\n", ""))
		}

		fun forMessage(messageId: Snowflake): BotReply? = FILE.reader().useLines { lines ->
			lines.firstNotNullOfOrNull { line ->
				Json.decodeFromString<BotReply>(line).takeIf { it.initiator == messageId }
			}
		}
	}

	private val runningRolls = mutableSetOf<BotReply>()

	@OptIn(PrivilegedIntent::class)
	suspend fun initialize() {
		kord.on<MessageCreateEvent>(consumer = ::onMessageCreate)
		kord.on<MessageDeleteEvent>(consumer = ::onMessageDelete)
		kord.login {
			intents = Intents(Intent.MessageContent, Intent.GuildMessages, Intent.GuildMessageReactions)
		}
	}

	private suspend fun onMessageCreate(event: MessageCreateEvent) {
		val message = event.message
		if (message.author?.isBot != false) return

		val roll = ROLL_REGEX.matchEntire(message.content)

		when {
			roll != null -> {
				val (dice, max) = roll.groupValues.takeLast(2)
				var reply: BotReply? = null
				try {
					roll(
						initiatorMessage = message,
						dice = dice.ifBlank { "1" }.toInt(),
						max = max.toInt(),
						instantMode = runningRolls.isNotEmpty(),
					) {
						REPLIES += it
						runningRolls.add(it)
						reply = it
					}
				} finally {
					runningRolls.remove(reply)
				}
			}

			INTERACTION_REGEX.matchEntire(message.content) != null -> REPLIES += help(message)
		}
	}

	private suspend fun onMessageDelete(event: MessageDeleteEvent) = REPLIES.forMessage(event.messageId)?.let {
		REPLIES -= it

		kord.rest.channel.deleteMessage(it.channel, it.reply, "Original was deleted")
	}
}

suspend fun main() {
	val botInstance = Bot(Kord(System.getenv("RollBotToken")))

	botInstance.initialize()
}