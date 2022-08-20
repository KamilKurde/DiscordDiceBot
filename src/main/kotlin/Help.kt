import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext
import kotlinx.coroutines.runBlocking

class Help(
	override val initiatorMessage: Message,
	private val botContext: BotContext,
) : Action {
	private val reply = runBlocking {
		with(botContext) {
			initiatorMessage.reply(
				"""
				Invalid command format, correct format for rolling is
				`Roll [numberOfDices]d[numberOfSides]` or `Roll [numberOfSides]` for rolling single dice.
				Try `Roll 10d100` or `Roll 6`
				
				*Note:* The maximum number of sides for a single dice and maximum number of dices in single throw is 120.
				""".trimIndent()
			)
		}
	}

	override fun delete() {
		runBlocking {
			with(botContext) {
				reply.delete()
			}
		}
	}

	override val isActive: Boolean = false
}