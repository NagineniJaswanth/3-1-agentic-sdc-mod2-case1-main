package project.casestudy1

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {

    companion object {
        private const val BASE_URL = "https://api.x.ai/"
        private const val TAG = "ApiClient"

        @Volatile
        private var instance: ApiClient? = null

        fun getInstance(context: Context): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    private val apiKeyValue: String by lazy { loadApiKeyFromProperties() }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $apiKeyValue")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            Log.d(TAG, "API Key available: ${apiKeyValue.isNotEmpty()}")
            Log.d(TAG, "API Key length: ${apiKeyValue.length}")
            if (apiKeyValue.isNotEmpty()) {
                Log.d(TAG, "API Key starts with: ${apiKeyValue.take(4)}...")
            }
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val grokApiService: GrokApiService by lazy {
        retrofit.create(GrokApiService::class.java)
    }

    private fun loadApiKeyFromProperties(): String {
        Log.d(TAG, "🔍 Starting API key search...")

        // Strategy 1: Use BuildConfig (most reliable — baked at compile time)
        try {
            val buildConfigKey = BuildConfig.GROK_API_KEY
            if (buildConfigKey.isNotEmpty() && buildConfigKey != "your_grok_api_key_here" && buildConfigKey != "YOUR_GROK_API_KEY") {
                Log.d(TAG, "✅ API key loaded from BuildConfig (length: ${buildConfigKey.length})")
                return buildConfigKey
            } else {
                Log.d(TAG, "⚠️ BuildConfig key is empty or placeholder")
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not read BuildConfig key: ${e.message}")
        }

        // Strategy 2: Try to load from assets
        try {
            context.assets.open("local.properties").use { inputStream ->
                val properties = Properties()
                properties.load(inputStream)
                val key = properties.getProperty("GROK_API_KEY", "")
                if (key.isNotEmpty() && key != "your_grok_api_key_here" && key != "YOUR_GROK_API_KEY") {
                    Log.d(TAG, "✅ API key loaded from assets (length: ${key.length})")
                    return key
                } else {
                    Log.d(TAG, "⚠️ Assets key is empty or placeholder")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not load from assets: ${e.message}")
        }

        // Strategy 2: Try to load from root directory
        try {
            // Get the project root directory
            val rootDir = context.filesDir.parentFile?.parentFile?.parentFile?.parentFile
            if (rootDir != null) {
                val propertiesFile = File(rootDir, "local.properties")
                Log.d(TAG, "🔍 Checking root path: ${propertiesFile.absolutePath}")
                if (propertiesFile.exists()) {
                    FileInputStream(propertiesFile).use { inputStream ->
                        val properties = Properties()
                        properties.load(inputStream)
                        val key = properties.getProperty("GROK_API_KEY", "")
                        if (key.isNotEmpty() && key != "your_grok_api_key_here" && key != "YOUR_GROK_API_KEY") {
                            Log.d(TAG, "✅ API key loaded from root directory (length: ${key.length})")
                            return key
                        } else {
                            Log.d(TAG, "⚠️ Root key is empty or placeholder")
                        }
                    }
                } else {
                    Log.d(TAG, "❌ Root properties file not found")
                }
            } else {
                Log.d(TAG, "❌ Could not determine root directory")
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not load from root: ${e.message}")
        }

        // Strategy 3: Try to load from app module directory
        try {
            val appDir = context.filesDir.parentFile?.parentFile
            if (appDir != null) {
                val propertiesFile = File(appDir, "local.properties")
                Log.d(TAG, "🔍 Checking app path: ${propertiesFile.absolutePath}")
                if (propertiesFile.exists()) {
                    FileInputStream(propertiesFile).use { inputStream ->
                        val properties = Properties()
                        properties.load(inputStream)
                        val key = properties.getProperty("GROK_API_KEY", "")
                        if (key.isNotEmpty() && key != "your_grok_api_key_here" && key != "YOUR_GROK_API_KEY") {
                            Log.d(TAG, "✅ API key loaded from app directory (length: ${key.length})")
                            return key
                        } else {
                            Log.d(TAG, "⚠️ App key is empty or placeholder")
                        }
                    }
                } else {
                    Log.d(TAG, "❌ App properties file not found")
                }
            } else {
                Log.d(TAG, "❌ Could not determine app directory")
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not load from app: ${e.message}")
        }

        // Strategy 4: Try to load from internal storage
        try {
            val propertiesFile = File(context.filesDir, "local.properties")
            Log.d(TAG, "🔍 Checking internal storage: ${propertiesFile.absolutePath}")
            if (propertiesFile.exists()) {
                FileInputStream(propertiesFile).use { inputStream ->
                    val properties = Properties()
                    properties.load(inputStream)
                    val key = properties.getProperty("GROK_API_KEY", "")
                    if (key.isNotEmpty() && key != "your_grok_api_key_here" && key != "YOUR_GROK_API_KEY") {
                        Log.d(TAG, "✅ API key loaded from internal storage (length: ${key.length})")
                        return key
                    } else {
                        Log.d(TAG, "⚠️ Internal storage key is empty or placeholder")
                    }
                }
            } else {
                Log.d(TAG, "❌ Internal storage properties file not found")
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not load from internal storage: ${e.message}")
        }

        // Strategy 5: Try environment variable (for testing)
        try {
            val envKey = System.getenv("GROK_API_KEY")
            if (!envKey.isNullOrEmpty() && envKey != "your_grok_api_key_here" && envKey != "YOUR_GROK_API_KEY") {
                Log.d(TAG, "✅ API key loaded from environment (length: ${envKey.length})")
                return envKey
            } else {
                Log.d(TAG, "⚠️ Environment key is empty or placeholder")
            }
        } catch (e: Exception) {
            Log.d(TAG, "❌ Could not load from environment: ${e.message}")
        }

        Log.e(TAG, "❌❌❌ NO API KEY FOUND! Grok API will be disabled.")
        return ""
    }

    fun isApiKeyAvailable(): Boolean {
        val available = apiKeyValue.isNotEmpty()
        Log.d(TAG, "API Key available: $available")
        return available
    }

    fun getApiKey(): String {
        return apiKeyValue
    }
}