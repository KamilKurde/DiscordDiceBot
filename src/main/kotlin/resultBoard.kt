fun resultBoard(dices: Int, max: Int, result: List<Int>): String {
	val sum = if (result.isEmpty()) "rolling" else result.sum().toString()
	return """
		:game_die: Rolling **${dices}d$max**: ${if (result.size in 1 until dices) result.last() else ""}
		${result.joinToString("+")}
		**Sum**: $sum
	""".trimIndent()
}