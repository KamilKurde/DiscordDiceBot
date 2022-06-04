import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.events

suspend fun main() {
	bot(System.getenv("RollBotToken"))
	{
		events {
			onMessageCreate {
				try {
					if (it.content.startsWith("Roll ")) {
						val message = it.content.removePrefix("Roll ")
						val numberPattern = "[1-9]\\d*"
						when {
							message.matches(Regex(numberPattern)) -> {
								it.reply(roll(max = message.toInt()))
							}
							message.matches(Regex("${numberPattern}d$numberPattern")) -> {
								val (dices, max) = message.split("d").map { it.toInt() }
								it.reply(roll(dices, max))
							}
						}
					}
				} catch (e: Exception) {
					logger.error(e.stackTraceToString())
				}
			}
		}
	}
}