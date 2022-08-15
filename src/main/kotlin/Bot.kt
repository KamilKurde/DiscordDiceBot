import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.events

private val rolls = mutableListOf<Roll>()

suspend fun main() {
	bot(System.getenv("RollBotToken"))
	{
		events {
			onMessageCreate {
				if (it.author.isBot != true) {
					try {
						rolls += Roll(it, rolls.any { roll -> roll.isActive })
					} catch (e: Exception) {
						it.reply(
									"""
									Invalid command format, correct format for rolling is
									`Roll [numberOfDices]d[numberOfSides]` or `Roll [numberOfSides]` for rolling single dice.
									Try `Roll 10d100` or `Roll 6`
									
									*Note:* The maximum number of sides for a single dice and maximum number of dices in single throw is 120.
									""".trimIndent()
						)
						logger.error(e.stackTraceToString())
					}
				}
			}
			onMessageDelete {
				rolls.filter { roll -> roll.initiatorMessage.id == it.id }.forEach {
					it.delete()
					rolls -= it
				}
			}
		}
	}
}