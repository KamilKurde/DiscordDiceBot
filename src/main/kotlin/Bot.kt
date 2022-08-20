import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.events

private val actions = mutableListOf<Action>()

suspend fun main() {
	bot(System.getenv("RollBotToken"))
	{
		events {
			onMessageCreate {
				if (it.author.isBot != true && it.content.startsWith("Roll")) {
					try {
						actions += Roll(it, actions.any { roll -> roll.isActive })
					} catch (e: Exception) {
						actions += Help(
							it,
							this,
						)
						logger.error(e.stackTraceToString())
					}
				}
			}
			onMessageDelete {
				actions.filter { roll -> roll.initiatorMessage.id == it.id }.forEach {
					it.delete()
					actions -= it
				}
			}
		}
	}
}