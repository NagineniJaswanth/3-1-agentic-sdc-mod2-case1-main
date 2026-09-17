package project.casestudy1

import retrofit2.http.Body
import retrofit2.http.POST

interface GrokApiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Body request: ChatRequest
    ): ChatResponse
}