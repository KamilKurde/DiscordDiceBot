import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message

suspend fun help(initiatorMessage: Message): BotReply {
	val reply = initiatorMessage.reply {
		content = """
		Invalid command format, correct format for rolling is
		`Roll [numberOfDices]d[numberOfSides]` or `Roll [numberOfSides]` for rolling single dice.
		Try `Roll 10d100` or `Roll 6`
		
		*Note:* The maximum number of sides for a single dice and maximum number of dices in single throw is 120.
		""".trimIndent()
	}
	return BotReply(initiatorMessage.id, reply.id, reply.channelId)
}