import dev.kord.common.entity.MessageFlag
import dev.kord.rest.builder.message.*

fun MessageBuilder.resultBoard(dices: Int, max: Int, result: List<Int>) {
	embed {
		title = "Rolling **${dices}d$max**"
		description = result.joinToString("+$ZERO_WIDTH_SPACE")
		footer {
			text = buildString {
				val sum = result.sum().toString()
				if (result.size == dices) {
					append("Sum: $sum")
				} else {
					append("Currently: $sum (${result.size}/$dices)")
				}
			}
		}
		thumbnail {
			url = "https://c.tenor.com/IfbgWLbg_88AAAAC/dice.gif"
		}
	}
	messageFlags {
		+MessageFlag.SuppressNotifications
		if (result.size == dices) {
			-MessageFlag.Loading
		}else{
			+MessageFlag.Loading
		}
	}
}

private const val ZERO_WIDTH_SPACE = '\u200b'
