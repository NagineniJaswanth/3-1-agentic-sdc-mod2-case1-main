import json
import re
from typing import Dict, List

class IntentTrainer:
    def __init__(self):
        self.intents = {
            "greeting": {
                "patterns": [
                    r"\b(hello|hi|hey|greetings|good morning|good evening)\b",
                    r"\b(howdy|what's up|sup)\b"
                ],
                "responses": [
                    "Hello! How can I assist you today?",
                    "Hi there! What can I help you with?",
                    "Greetings! How may I help you?"
                ]
            },
            "product_inquiry": {
                "patterns": [
                    r"\b(product|features|specifications|details|about)\b",
                    r"\b(tell me about|what is|describe)\b.*\b(product|service)\b"
                ],
                "responses": [
                    "Our products include... [Detailed product information]",
                    "I'd be happy to tell you about our products..."
                ]
            },
            "order_status": {
                "patterns": [
                    r"\b(order|track|shipping|delivery|status)\b",
                    r"\b(where is my|when will)\b.*\b(order|package)\b"
                ],
                "responses": [
                    "To check your order status, please provide your order number...",
                    "I can help you track your order..."
                ]
            },
            "support_request": {
                "patterns": [
                    r"\b(support|help|assist|issue|problem|complaint)\b",
                    r"\b(can't|unable|not working)\b"
                ],
                "responses": [
                    "I'll connect you with our support team...",
                    "Let me help you with your issue..."
                ]
            }
        }

    def detect_intent(self, message: str) -> str:
        """Detect intent from user message using regex patterns"""
        message_lower = message.lower()

        for intent_name, intent_data in self.intents.items():
            for pattern in intent_data["patterns"]:
                if re.search(pattern, message_lower):
                    return intent_name

        return "unknown"

    def get_response(self, intent: str) -> str:
        """Get a response for the detected intent"""
        if intent in self.intents:
            responses = self.intents[intent]["responses"]
            # In a real implementation, you might use random selection
            # or more sophisticated logic
            return responses[0]
        return "I'm not sure how to help with that. Could you please rephrase your question?"

# Example usage
if __name__ == "__main__":
    trainer = IntentTrainer()

    test_messages = [
        "Hello, I need help",
        "What products do you have?",
        "Where is my order?",
        "I want to return an item"
    ]

    for msg in test_messages:
        intent = trainer.detect_intent(msg)
        response = trainer.get_response(intent)
        print(f"Message: {msg}")
        print(f"Intent: {intent}")
        print(f"Response: {response}")
        print("-" * 50)