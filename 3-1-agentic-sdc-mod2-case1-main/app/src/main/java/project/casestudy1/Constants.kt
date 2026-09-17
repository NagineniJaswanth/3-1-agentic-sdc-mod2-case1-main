package project.casestudy1

object Constants {
    const val BASE_URL = "https://api.x.ai/"

    // The model name for Grok.
    // Options: "grok-3-latest", "grok-2-latest", "grok-3-mini-latest"
    const val CHAT_MODEL = "grok-3-latest"

    // System messages defining the personalities of the assistant
    const val SYSTEM_PROMPT = "You are a helpful, knowledgeable, and friendly AI assistant. Provide detailed, accurate, and well-structured responses. If you don't know something, say so honestly. Keep responses clear and conversational."
    const val CUSTOMER_SUPPORT_PROMPT = "You are a helpful customer support assistant. Provide clear, concise, and friendly responses. Keep responses under 200 words."
}
