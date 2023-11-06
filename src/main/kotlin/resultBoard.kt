import com.jessecorbett.diskord.api.channel.*

fun Embed.resultBoard(dices: Int, max: Int, result: List<Int>): Embed {
	val sum = result.sum().toString()
	title = "Rolling **${dices}d$max**"
	description = result.joinToString("+")
	footer = EmbedFooter(
		buildString {
			if (result.size == dices) {
				append("Sum: $sum")
			} else {
				append("Currently: $sum (${result.size}/$dices)")
			}
		}
	)
	thumbnail = EmbedImage("https://c.tenor.com/IfbgWLbg_88AAAAC/dice.gif")
	return this
}