import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.api.common.MessageDelete
import com.jessecorbett.diskord.bot.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private class Bot(private val botScope: CoroutineScope) {
	companion object {
		private const val DICE_PATTERN = "(120|1[01]\\d|[1-9]\\d|[1-9])"
		private const val INTERACTION_PATTERN = "[Rr]oll "
		private val INTERACTION_REGEX = Regex("$INTERACTION_PATTERN.*")
		private val ROLL_REGEX = Regex("$INTERACTION_PATTERN(${DICE_PATTERN}d)?$DICE_PATTERN")

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

			fun forMessage(message: MessageDelete): BotReply? = FILE.reader().useLines { lines ->
				lines.firstOrNull { line ->
					message.id in line
				}?.let(Json::decodeFromString)
			}
		}
	}

	private val runningActions = mutableMapOf<BotReply, CoroutineScope>()
	private val runningRolls = mutableSetOf<BotReply>()

	context (BotContext)
	fun onMessageCreate(message: Message) {
		if (message.author.isBot == true) return

		val roll = ROLL_REGEX.matchEntire(message.content)

		botScope.launch {
			when {
				roll != null -> {
					val (dice, max) = roll.groupValues.takeLast(2)
					var reply: BotReply? = null
					launch {
						roll(
							initiatorMessage = message,
							dice = dice.ifBlank { "1" }.toInt(),
							max = max.toInt(),
							instantMode = runningRolls.isNotEmpty(),
						) {
							REPLIES += it
							runningActions[it] = this
							runningRolls.add(it)
							reply = it
						}
						runningRolls.remove(reply)
					}.invokeOnCompletion {
						reply?.let(runningActions::remove)
					}
				}

				INTERACTION_REGEX.matchEntire(message.content) != null -> REPLIES += help(message)
			}
		}
	}

	context (BotContext)
	fun onMessageDelete(message: MessageDelete) {
		val reply = REPLIES.forMessage(message)
		if (reply != null) {
			REPLIES -= reply
			runningActions.remove(reply)?.cancel()
			runningRolls.remove(reply)
		}
		botScope.launch {
			reply?.delete()
		}
	}
}

suspend fun main() {
	val botInstance = Bot(CoroutineScope(Job()))
	bot(System.getenv("RollBotToken")) {
		events {
			onMessageCreate {
				botInstance.onMessageCreate(it)
			}
			onMessageDelete {
				botInstance.onMessageDelete(it)
			}
		}
	}
}