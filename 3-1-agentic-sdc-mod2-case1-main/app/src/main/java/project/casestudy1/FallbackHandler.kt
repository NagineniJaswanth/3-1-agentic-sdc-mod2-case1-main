package project.casestudy1

class FallbackHandler {

    private val fallbackResponses = listOf(
        "I'm sorry, I didn't quite understand that. Could you please rephrase your question?",
        "I'm not sure I follow. Can you provide more details about what you're looking for?",
        "Let me redirect you to our support team. They'll be happy to help with your specific question.",
        "I apologize for the confusion. Could you please ask your question in a different way?",
        "I'm here to help with product information, order status, pricing, and general support queries. What specific topic would you like to discuss?"
    )

    private val keywordResponses = mapOf(
        "help" to "I'd be happy to help! Could you please tell me more specifically what you need assistance with?",
        "thank" to "You're welcome! Is there anything else I can help you with?",
        "sorry" to "No need to apologize! How can I assist you today?",
        "apologize" to "No need to apologize! How can I assist you today?",
        "agent" to "I understand you'd like to speak with a human agent. Please hold while I connect you to our support team. You can also reach them directly at 1-800-123-4567.",
        "human" to "I understand you'd like to speak with a human agent. Please hold while I connect you to our support team. You can also reach them directly at 1-800-123-4567.",
        "person" to "I understand you'd like to speak with a human agent. Please hold while I connect you to our support team. You can also reach them directly at 1-800-123-4567.",
        "representative" to "I understand you'd like to speak with a human agent. Please hold while I connect you to our support team. You can also reach them directly at 1-800-123-4567.",
        "yes" to "Great! How can I assist you specifically?",
        "no" to "Okay! Is there anything else I can help you with?"
    )

    fun getFallbackResponse(message: String): String {
        val lowercaseMessage = message.lowercase().trim()

        // Check for keyword matches
        for ((keyword, response) in keywordResponses) {
            if (lowercaseMessage.contains(keyword)) {
                return response
            }
        }

        // Check if message is very short (likely a greeting or simple response)
        if (lowercaseMessage.length < 5) {
            return "I see. How can I help you further?"
        }

        // Random fallback
        return fallbackResponses.random()
    }
}