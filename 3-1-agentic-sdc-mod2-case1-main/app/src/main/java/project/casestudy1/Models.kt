package project.casestudy1

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val messages: List<Message>,
    val model: String = Constants.CHAT_MODEL,
    val stream: Boolean = false,
    @SerializedName("max_tokens") val maxTokens: Int? = null,
    val temperature: Double? = null
)

data class Message(
    val role: String, // "system", "user", or "assistant"
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)