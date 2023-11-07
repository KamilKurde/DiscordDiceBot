package util

import com.jessecorbett.diskord.api.channel.Embed
import com.jessecorbett.diskord.api.channel.MessageEdit
import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext

context (BotContext)
suspend fun Message.edit(block: Embed.() -> Unit) = edit(
	MessageEdit(
		content = null,
		embeds = listOf(Embed().apply(block))
	)
)