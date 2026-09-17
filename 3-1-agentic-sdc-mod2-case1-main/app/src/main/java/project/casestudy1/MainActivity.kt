package project.casestudy1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var progressBar: ProgressBar

    private val chatMessages = mutableListOf<ChatMessage>()
    private var grokApiEnabled = false

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupListeners()

        // Debug: Check files
        debugFileLocations()

        // Check API key
        checkApiKey()

        // Add welcome message
        addMessage(ChatMessage("Hello! I'm your AI support assistant. How can I help you today?", true))
    }

    private fun debugFileLocations() {
        Log.d(TAG, "=== DEBUG: File Locations ===")

        // Check root directory
        val rootDir = filesDir.parentFile?.parentFile?.parentFile?.parentFile
        Log.d(TAG, "Root dir: ${rootDir?.absolutePath}")
        if (rootDir != null) {
            val propFile = File(rootDir, "local.properties")
            Log.d(TAG, "Root properties exists: ${propFile.exists()}")
            if (propFile.exists()) {
                Log.d(TAG, "Root properties content: ${propFile.readText()}")
            }
        }

        // Check assets
        try {
            val assetFiles = assets.list("")
            Log.d(TAG, "Assets files: ${assetFiles?.joinToString()}")
        } catch (e: Exception) {
            Log.d(TAG, "Could not list assets: ${e.message}")
        }

        // Check internal storage
        val internalFile = File(filesDir, "local.properties")
        Log.d(TAG, "Internal storage properties exists: ${internalFile.exists()}")

        Log.d(TAG, "=== END DEBUG ===")
    }

    private fun checkApiKey() {
        val apiClient = ApiClient.getInstance(this)
        grokApiEnabled = apiClient.isApiKeyAvailable()

        if (grokApiEnabled) {
            val apiKey = apiClient.getApiKey()
            val maskedKey = if (apiKey.length > 8) {
                apiKey.take(4) + "..." + apiKey.takeLast(4)
            } else {
                "***"
            }
            Log.d(TAG, "✅ Grok API is enabled with key: $maskedKey")
            Toast.makeText(this, "✅ AI Assistant ready!", Toast.LENGTH_SHORT).show()
        } else {
            Log.w(TAG, "❌ Grok API is disabled")
            Toast.makeText(this, "⚠️ API key not configured. Using basic responses.", Toast.LENGTH_LONG).show()
            addMessage(ChatMessage(
                "⚠️ Note: AI features are not available. Using basic responses only. " +
                        "To enable AI, add your Grok API key to local.properties in the project root.",
                true
            ))
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        inputEditText = findViewById(R.id.inputEditText)
        sendButton = findViewById(R.id.sendButton)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        inputEditText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val message = inputEditText.text.toString().trim()
        if (message.isEmpty()) return

        // Add user message
        addMessage(ChatMessage(message, false))
        inputEditText.text.clear()

        // Show loading
        progressBar.visibility = View.VISIBLE
        sendButton.isEnabled = false

        // Process the message
        lifecycleScope.launch {
            val response = processMessage(message)
            withContext(Dispatchers.Main) {
                addMessage(ChatMessage(response, true))
                progressBar.visibility = View.GONE
                sendButton.isEnabled = true
                recyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
        }
    }

    private suspend fun processMessage(message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Try Grok API first if enabled
                if (grokApiEnabled) {
                    Log.d(TAG, "🤖 Using Grok API for: $message")
                    val grokResponse = GrokApiHelper.getGrokResponse(this@MainActivity, message)

                    if (grokResponse.isNotEmpty()) {
                        Log.d(TAG, "✅ Got response from Grok API")
                        return@withContext grokResponse
                    } else {
                        Log.w(TAG, "⚠️ Grok API returned empty, using fallback")
                    }
                }

                // Fallback to intent-based responses
                Log.d(TAG, "📝 Using intent-based response for: $message")
                return@withContext getIntentBasedResponse(message)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing message: ${e.message}", e)
                return@withContext "I apologize, but I'm having trouble processing your request. Please try again later."
            }
        }
    }

    private fun getIntentBasedResponse(message: String): String {
        val intentDetector = IntentDetector()
        val intent = intentDetector.detectIntent(message)

        return when (intent) {
            "greeting" -> {
                listOf(
                    "Hello! How can I assist you today?",
                    "Hi there! What can I help you with?",
                    "Greetings! How may I help you?",
                    "Hello! I'm here to help. What do you need?"
                ).random()
            }
            "goodbye" -> {
                listOf(
                    "Goodbye! Have a great day!",
                    "Take care! Feel free to come back if you need any help.",
                    "Bye! I'm always here if you need assistance."
                ).random()
            }
            "support_request" -> {
                "I'd be happy to help! I can assist with:\n" +
                        "• General questions and information\n" +
                        "• Programming and technical queries\n" +
                        "• Product information and support\n" +
                        "• Any other questions you have!"
            }
            "programming_help" -> {
                val lower = message.lowercase()
                when {
                    lower.contains("python") ->
                        "Python is a high-level, interpreted programming language known for its simplicity.\n" +
                        "Key features:\n" +
                        "\u2022 Easy-to-learn, readable syntax\n" +
                        "\u2022 Large standard library\n" +
                        "\u2022 Cross-platform compatibility\n" +
                        "\u2022 Strong community support\n" +
                        "\u2022 Used in web dev, data science, AI, automation, and more\n\n" +
                        "What specifically about Python would you like to know?"
                    lower.contains("kotlin") ->
                        "Kotlin is a modern, statically-typed language that runs on the JVM.\n" +
                        "Key features:\n" +
                        "\u2022 Concise and expressive syntax\n" +
                        "\u2022 Null safety built-in\n" +
                        "\u2022 Full Java interoperability\n" +
                        "\u2022 Official language for Android development"
                    lower.contains("java") ->
                        "Java is a popular, object-oriented programming language.\n" +
                        "Key features:\n" +
                        "\u2022 Platform independence (Write Once, Run Anywhere via JVM)\n" +
                        "\u2022 Strong memory management\n" +
                        "\u2022 Large ecosystem\n" +
                        "\u2022 Used in enterprise applications and Android development"
                    lower.contains("javascript") || lower.contains(" js") ->
                        "JavaScript is the language of the web, running in browsers and on servers (Node.js).\n" +
                        "Key features:\n" +
                        "\u2022 Dynamic typing\n" +
                        "\u2022 Asynchronous programming (Promises, async/await)\n" +
                        "\u2022 Huge ecosystem via npm\n" +
                        "\u2022 Used for frontend, backend, and mobile apps"
                    else ->
                        "I can help you with programming! I support Python, Java, Kotlin, JavaScript, " +
                        "and many other languages and frameworks. What specific coding question do you have?"
                }
            }
            "product_inquiry" -> {
                "We offer a wide range of software and hardware solutions. " +
                        "Our primary products include enterprise management systems, cloud integration tools, and client support applications. " +
                        "Which product would you like to know more about?"
            }
            "order_status" -> {
                "To track your order, please provide your order ID. " +
                        "You can also check the status of your shipment by logging into your account on our website."
            }
            "return_policy" -> {
                "We offer a 30-day return policy for all unused products. " +
                        "If you are not satisfied with your purchase, you can return it within 30 days for a full refund or exchange. " +
                        "Please keep the receipt and original packaging."
            }
            "pricing" -> {
                "We offer flexible pricing options depending on your needs: " +
                        "Basic (Free trial), Professional ($29/month), and Enterprise (custom pricing). " +
                        "Which plan fits your team best?"
            }
            else -> {
                val lowercaseMessage = message.lowercase().trim()
                when {
                    lowercaseMessage.contains("python") -> {
                        "Python is a high-level, interpreted programming language known for its simplicity and readability. " +
                                "Key features include:\n" +
                                "• Easy-to-learn syntax\n" +
                                "• Large standard library\n" +
                                "• Cross-platform compatibility\n" +
                                "• Strong community support\n" +
                                "• Used in web development, data science, AI, and more\n\n" +
                                "Would you like to know more about Python specifically?"
                    }
                    lowercaseMessage.contains("java") -> {
                        "Java is a popular, object-oriented programming language. " +
                                "Key features include:\n" +
                                "• Platform independence (JVM)\n" +
                                "• Strong memory management\n" +
                                "• Large ecosystem\n" +
                                "• Used in enterprise applications, Android development, and more"
                    }
                    lowercaseMessage.contains("kotlin") -> {
                        "Kotlin is a modern, statically-typed programming language that runs on the JVM. " +
                                "Key features include:\n" +
                                "• Concise syntax\n" +
                                "• Null safety\n" +
                                "• Interoperable with Java\n" +
                                "• Used primarily for Android development"
                    }
                    lowercaseMessage.contains("thank") -> {
                        listOf(
                            "You're welcome! Is there anything else I can help with?",
                            "My pleasure! Let me know if you need anything else.",
                            "Happy to help! What else can I do for you?"
                        ).random()
                    }
                    else -> {
                        val fallbackHandler = FallbackHandler()
                        fallbackHandler.getFallbackResponse(message)
                    }
                }
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.smoothScrollToPosition(chatMessages.size - 1)
    }
}