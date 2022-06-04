import kotlin.random.Random
import kotlin.random.nextInt

fun roll(dices: Int = 1, max: Int): String {
	val rolls = List(dices) { Random.nextInt(1..max) }
	return """
		:game_die: Rolling **${dices}d$max**:
		${rolls.joinToString("+")}
		**Sum**: ${rolls.sum()}
	""".trimIndent()
}