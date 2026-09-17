package project.casestudy1

class IntentDetector {

    // Intents listed in priority order — more specific first
    private val intentKeywords = mapOf(
        "programming_help" to listOf(
            "python", "java", "kotlin", "javascript", "typescript", "c++", "c#",
            "html", "css", "sql", "swift", "rust", "golang", "react", "android",
            "programming", "coding", "code", "algorithm", "function", "variable",
            "loop", "array", "class", "object", "api", "framework", "library",
            "debug", "compile", "runtime", "syntax"
        ),
        "greeting" to listOf(
            "hello", "hi", "hey", "good morning", "good evening", "greetings",
            "howdy", "sup", "what's up", "yo"
        ),
        "goodbye" to listOf(
            "bye", "goodbye", "see you", "take care", "farewell",
            "cya", "gotta go"
        ),
        "product_inquiry" to listOf(
            "product", "products", "features", "specifications",
            "catalog", "service", "services", "offer", "available"
        ),
        "order_status" to listOf(
            "order", "track", "tracking", "shipping", "delivery",
            "shipment", "package", "arrive", "delivered"
        ),
        "support_request" to listOf(
            "support", "assist", "assistance", "issue", "problem",
            "complaint", "unable", "not working", "broken", "crash"
        ),
        "return_policy" to listOf(
            "return", "refund", "exchange", "money back",
            "warranty", "guarantee"
        ),
        "pricing" to listOf(
            "price", "cost", "payment", "pricing",
            "subscription", "billing", "discount"
        )
    )

    fun detectIntent(message: String): String {
        val lowercaseMessage = message.lowercase().trim()

        // Check for exact matches first
        for ((intent, keywords) in intentKeywords) {
            for (keyword in keywords) {
                if (lowercaseMessage == keyword) {
                    return intent
                }
            }
        }

        // Then check for whole-word matches using word boundaries
        for ((intent, keywords) in intentKeywords) {
            for (keyword in keywords) {
                // Use word-boundary regex for single words, direct contains for phrases
                val matched = if (keyword.contains(" ")) {
                    lowercaseMessage.contains(keyword)
                } else {
                    // Match whole words only to avoid false positives
                    Regex("\\b${Regex.escape(keyword)}\\b").containsMatchIn(lowercaseMessage)
                }
                if (matched) return intent
            }
        }

        return "unknown"
    }
}