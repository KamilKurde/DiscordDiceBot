import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext

interface Action {

	context (BotContext)
	fun delete()

	val isActive: Boolean

	val initiatorMessage: Message
}