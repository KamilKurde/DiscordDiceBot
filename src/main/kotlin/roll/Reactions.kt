package roll

import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji

suspend fun Message.updateReactions(max: Int, points: List<Int>, reactions: MutableSet<ReactionEmoji>) {
	if (max != 100) return

	suspend fun addReaction(emoji: String) {
		val discordEmoji = ReactionEmoji.Unicode(emoji)
		if (discordEmoji in reactions) return
		reactions += discordEmoji
		addReaction(discordEmoji)
	}

	val threshold = 90
	if (points.any { it > threshold }) addReaction("☠️")
	if (points.any { it <= 5 }) addReaction("⭐")
	if (points.any { it == 1 }) addReaction("\uD83C\uDF1F")
	if (points.any { it == max }) addReaction("💥")
}