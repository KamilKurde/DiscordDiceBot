import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.*
import roll.updateReactions
import util.edit
import kotlin.random.Random
import kotlin.random.nextInt

private const val UPDATE_DELAY = 1_000L

context (BotContext)
@Suppress("SuspendFunctionOnCoroutineScope")
suspend fun CoroutineScope.roll(initiatorMessage: Message, dice: Int, max: Int, instantMode: Boolean, onReplyCreation: (BotReply) -> Unit) {
	val rolls = List(dice) { Random.nextInt(1..max) }

	val reply = initiatorMessage.reply {
		resultBoard(dice, max, rolls.takeIf { instantMode } ?: rolls.take(1))
	}

	onReplyCreation(BotReply(initiatorMessage.id, reply.id, reply.channelId))

	val reactions = mutableSetOf<String>()
	delay(UPDATE_DELAY)

	if (instantMode.not() && dice > 1) {
		for (i in 2..dice) {
			val visibleRolls = rolls.take(i)
			reply.edit {
				resultBoard(dice, max, visibleRolls)
			}
			delay(UPDATE_DELAY)
			launch {
				initiatorMessage.updateReactions(max, visibleRolls, reactions)
			}
		}
	}

	launch {
		initiatorMessage.updateReactions(max, rolls, reactions)
		initiatorMessage.react("✅")
	}
}