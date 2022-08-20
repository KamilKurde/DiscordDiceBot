import com.jessecorbett.diskord.api.channel.Embed
import com.jessecorbett.diskord.api.channel.MessageEdit
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextInt

class Roll private constructor(
	override val initiatorMessage: Message,
	private val dices: Int,
	private val max: Int,
	instantMode: Boolean,
	private val reply: Message,
	private val scope: CoroutineScope,
	private val botContext: BotContext,
): Action {

	companion object {
		private const val numberPattern = "(120|1[01]\\d|[1-9]\\d|[1-9])"

		context(BotContext)
		operator fun invoke(message: Message, instantMode: Boolean): Roll {
			val content = message.content.removePrefix("Roll ")
			val (dices: Int, max: Int) = when {
				content.matches(Regex(numberPattern)) -> listOf(1, content.toInt())
				content.matches(Regex("${numberPattern}d$numberPattern")) -> content.split("d").map { parts -> parts.toInt() }
				else -> throw IllegalArgumentException("Invalid message format")
			}
			return Roll(
				message,
				dices,
				max,
				instantMode,
				runBlocking { message.reply("Rolling...") },
				CoroutineScope(Job() + Dispatchers.IO),
				this@BotContext,
			)
		}
	}

	init {
		scope.launch {
			with(botContext) {
				val rolls = List(dices) { Random.nextInt(1..max) }
				val channel = initiatorMessage.channel
				when (instantMode) {
					true -> channel.editMessage(reply.id, MessageEdit(null, Embed().resultBoard(dices, max, rolls)))
					false -> {
						for (i in 1..dices) {
							val visibleRolls = rolls.take(i)
							updateReply(visibleRolls)
							delay(1000)
							updateReaction(visibleRolls)
						}
					}
				}
				updateReaction(rolls)
				reactions.clear()
				scope.cancel()
			}
		}
	}

	context(BotContext)
	private suspend fun updateReply(rolls: List<Int>) {
		initiatorMessage.channel.editMessage(reply.id, MessageEdit(null, Embed().resultBoard(dices, max, rolls)))
	}

	context(BotContext)
	private suspend fun updateReaction(points: List<Int>) {
		if (max != 100) return
		val threshold = 95
		if (points.any { it > threshold }) react("☠️")
		if (points.any { it <= 5 }) react("⭐")
		if (points.any { it == 1 }) react("\uD83C\uDF1F")
		if (points.any { it == max }) react("💥")
	}

	context(BotContext)
	private suspend fun react(emoji: String) {
		if (emoji in reactions) return
		reply.react(emoji)
		reactions += emoji
	}

	override fun delete() {
		scope.cancel()
		CoroutineScope(Job() + Dispatchers.IO).launch {
			with(botContext) {
				reply.delete()
			}
		}
	}

	private val reactions = mutableListOf<String>()

	override val isActive: Boolean get() = scope.isActive
}