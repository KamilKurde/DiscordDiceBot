import com.jessecorbett.diskord.bot.BotContext

data class BotReply(val initiatorMessageId: String, val replyId: String, val channelId: String){
	context (BotContext)
	suspend fun delete() = channel(channelId).deleteMessage(replyId)
}