import com.jessecorbett.diskord.api.channel.Embed
import com.jessecorbett.diskord.api.channel.MessageEdit
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextInt

suspend fun BotContext.roll(message: Message, dices: Int = 1, max: Int) {
	val rolls = mutableListOf<Int>()
	val reply = message.reply("Rolling...")
	val channel = message.channel
	for (i in 1..dices) {
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