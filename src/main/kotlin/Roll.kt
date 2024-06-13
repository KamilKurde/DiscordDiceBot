import dev.kord.core.behavior.edit
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.request.KtorRequestException
import io.ktor.http.*
import kotlinx.coroutines.delay
import roll.updateReactions
import kotlin.random.Random
import kotlin.random.nextInt

private const val UPDATE_DELAY = 1_000L

suspend fun roll(
	initiatorMessage: Message, dice: Int, max: Int, instantMode: Boolean, onReplyCreation: (BotReply) -> Unit
) {
	val rolls = List(dice) { Random.nextInt(1..max) }

	val reply = initiatorMessage.reply {
		resultBoard(dice, max, rolls.takeIf { instantMode } ?: rolls.take(1))
	}

	onReplyCreation(BotReply(initiatorMessage.id, reply.id, reply.channelId))

	val reactions = mutableSetOf<ReactionEmoji>()
	delay(UPDATE_DELAY)

	if (instantMode.not() && dice > 1) {
		for (i in 2..dice) {
			val visibleRolls = rolls.take(i)
			try {
				reply.edit {
					resultBoard(dice, max, visibleRolls)
				}
			} catch (e: KtorRequestException) {
				if (e.httpResponse.status == HttpStatusCode.NotFound) {
					return
				}
			}
			delay(UPDATE_DELAY)
			initiatorMessage.updateReactions(max, visibleRolls, reactions)
		}
	}

	initiatorMessage.updateReactions(max, rolls, reactions)
	initiatorMessage.addReaction(ReactionEmoji.Unicode("✅"))
}