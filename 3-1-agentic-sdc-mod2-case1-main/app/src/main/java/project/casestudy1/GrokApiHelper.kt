package project.casestudy1

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GrokApiHelper {

    private const val TAG = "GrokApiHelper"

    suspend fun getGrokResponse(context: Context, userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val apiClient = ApiClient.getInstance(context)

                if (!apiClient.isApiKeyAvailable()) {
                    Log.w(TAG, "API key not available - cannot call Grok API")
                    return@withContext ""
                }

                val service = apiClient.grokApiService

                val request = ChatRequest(
                    model = Constants.CHAT_MODEL,
                    messages = listOf(
                        Message(
                            role = "system",
                            content = Constants.SYSTEM_PROMPT
                        ),
                        Message(
                            role = "user",
                            content = userMessage
                        )
                    ),
                    maxTokens = 1000,
                    temperature = 0.7
                )

                Log.d(TAG, "Calling Grok API with message: $userMessage")
                val response = service.getChatCompletion(request)

                response.choices.firstOrNull()?.message?.content?.trim()?.let { content ->
                    if (content.isNotEmpty()) {
                        Log.d(TAG, "✅ Received response from Grok API: ${content.take(50)}...")
                        return@withContext content
                    }
                }

                Log.w(TAG, "Empty response from Grok API")
                ""

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error calling Grok API: ${e.message}", e)
                ""
            }
        }
    }
}