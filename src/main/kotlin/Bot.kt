import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.api.common.MessageDelete
import com.jessecorbett.diskord.bot.*
import kotlinx.coroutines.*

private class Bot(private val botScope: CoroutineScope) {
	companion object {
		private const val DICE_PATTERN = "(120|1[01]\\d|[1-9]\\d|[1-9])"
		private const val INTERACTION_PATTERN = "[Rr]oll "
		private val INTERACTION_REGEX = Regex("$INTERACTION_PATTERN.*")
		private val ROLL_REGEX = Regex("$INTERACTION_PATTERN(${DICE_PATTERN}d)?$DICE_PATTERN")
	}

	private val replies = mutableListOf<BotReply>()
	private val runningRolls = mutableMapOf<BotReply, CoroutineScope>()

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
							replyScope = CoroutineScope(coroutineContext)
						) {
							replies += it
							reply = it
							runningRolls[it] = this
						}
						reply?.let(runningRolls::remove)
					}
				}

				INTERACTION_REGEX.matchEntire(message.content) != null -> replies += help(message)
			}
		}
	}

	context (BotContext)
	fun onMessageDelete(message: MessageDelete) {
		val reply = replies.firstOrNull { it.initiatorMessageId == message.id }
		botScope.launch {
			reply?.delete()
		}
		if (reply != null) {
			replies -= reply
			runningRolls[reply]?.cancel()
			runningRolls.remove(reply)
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