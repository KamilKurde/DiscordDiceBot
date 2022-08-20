import com.jessecorbett.diskord.api.common.Message

interface Action {
	fun delete()

	val isActive: Boolean

	val initiatorMessage: Message
}