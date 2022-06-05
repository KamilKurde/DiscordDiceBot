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
						val numberPattern = "(120|1[01]\\d|[1-9]\\d|[1-9])"
						when {
							message.matches(Regex(numberPattern)) -> {
								roll(it, max = message.toInt())
							}
							message.matches(Regex("${numberPattern}d$numberPattern")) -> {
								val (dices, max) = message.split("d").map { parts -> parts.toInt() }
								roll(it, dices, max)
							}
							else -> {
								it.reply(
									"""
									Invalid command format, correct format for rolling is
									`Roll [numberOfDices]d[numberOfSides]` or `Roll [numberOfSides]` for rolling single dice.
									Try `Roll 10d100` or `Roll 6`
									
									*Note:* The maximum number of sides for a single dice and maximum number of dices in single throw is 120.
									""".trimIndent()
								)
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