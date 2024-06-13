import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class BotReply(val initiator: Snowflake, val reply: Snowflake, val channel: Snowflake)