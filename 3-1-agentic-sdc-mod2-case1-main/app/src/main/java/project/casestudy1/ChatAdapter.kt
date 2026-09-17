package project.casestudy1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userMessageTextView: TextView = itemView.findViewById(R.id.userMessageTextView)
        private val botMessageTextView: TextView = itemView.findViewById(R.id.botMessageTextView)
        private val userLayout: View = itemView.findViewById(R.id.userLayout)
        private val botLayout: View = itemView.findViewById(R.id.botLayout)

        fun bind(message: ChatMessage) {
            if (message.isBot) {
                userLayout.visibility = View.GONE
                botLayout.visibility = View.VISIBLE
                botMessageTextView.text = message.text
            } else {
                userLayout.visibility = View.VISIBLE
                botLayout.visibility = View.GONE
                userMessageTextView.text = message.text
            }
        }
    }
}