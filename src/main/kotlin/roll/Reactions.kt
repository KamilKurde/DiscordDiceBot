package roll

import com.jessecorbett.diskord.api.common.Message
import com.jessecorbett.diskord.bot.BotContext

context(BotContext)
suspend fun Message.updateReactions(max: Int, points: List<Int>, reactions: MutableSet<String>) {
	if (max != 100) return
	val threshold = 90
	with(reactions) {
		if (points.any { it > threshold }) addReaction("☠️")
		if (points.any { it <= 5 }) addReaction("⭐")
		if (points.any { it == 1 }) addReaction("\uD83C\uDF1F")
		if (points.any { it == max }) addReaction("💥")
	}
}

context(BotContext, MutableSet<String>)
private suspend fun Message.addReaction(emoji: String) {
	if (contains(emoji)) return
	react(emoji)
	add(emoji)
}