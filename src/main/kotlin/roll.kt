import com.jessecorbett.diskord.api.channel.MessageEdit
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextInt

suspend fun BotContext.roll(message: Message, dices: Int = 1, max: Int) {
	val rolls = mutableListOf<Int>()
	val reply = message.reply(resultBoard(dices, max, rolls))
	val channel = message.channel
	for (i in 1..dices) {
		delay(500L)
		rolls.add(Random.nextInt(1..max))
		channel.editMessage(reply.id, MessageEdit(resultBoard(dices, max, rolls)))
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