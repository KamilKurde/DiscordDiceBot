import com.jessecorbett.diskord.api.channel.Embed
import com.jessecorbett.diskord.api.channel.MessageEdit
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextInt

class Roll private constructor(
	val initiatorMessage: Message,
	val dices: Int,
	val max: Int,
	private val scope: CoroutineScope,
	private val botContext: BotContext
) {

	companion object {
		private val numberPattern = "(120|1[01]\\d|[1-9]\\d|[1-9])"

		context(BotContext)
		operator fun invoke(message: Message): Roll {
			val content = message.content.removePrefix("Roll ")
			val (dices: Int, max: Int) = when {
				content.matches(Regex(numberPattern)) -> listOf(1, content.toInt())
				content.matches(Regex("${numberPattern}d$numberPattern")) -> content.split("d").map { parts -> parts.toInt() }
				else -> throw IllegalArgumentException("Invalid message format")
			}
			return Roll(message, dices, max, CoroutineScope(Job() + Dispatchers.IO), this@BotContext)
		}
	}

	init {
		scope.launch {
			with(botContext) {
				val rolls = mutableListOf<Int>()
				reply = initiatorMessage.reply("Rolling...")
				val channel = initiatorMessage.channel
				for (i in 1..dices) {
					ensureActive()
					rolls.add(Random.nextInt(1..max))
					channel.editMessage(reply.id, MessageEdit(null, Embed().resultBoard(dices, max, rolls)))
					delay(1000L)
					if (max != 100) continue

					// 100 sided dice
					val points = rolls.last()
					val threshold = 95
					if (points > threshold) reply.react("☠️")
					if (points <= 5) reply.react("⭐")
					if (points == 1) reply.react("\uD83C\uDF1F")
					if (points == max) reply.react("💥")
				}
			}
		}
	}

	fun delete() {
		scope.cancel()
		CoroutineScope(Job() + Dispatchers.IO).launch {
			with(botContext) {
				reply.delete()
			}
		}
	}

	private lateinit var reply: Message
}