import com.jessecorbett.diskord.api.channel.*

fun Embed.resultBoard(dices: Int, max: Int, result: List<Int>): Embed {
	val sum = if (result.isEmpty()) "rolling" else result.sum().toString()
	title = "Rolling **${dices}d$max**"
	description = result.joinToString("+")
	footer = EmbedFooter(
		buildString {
			if (result.size == dices) {
				append("Sum")
			} else {
				append("Currently")
			}
			append(": $sum")
		}
	)
	thumbnail = EmbedImage("https://c.tenor.com/IfbgWLbg_88AAAAC/dice.gif")
	return this
}