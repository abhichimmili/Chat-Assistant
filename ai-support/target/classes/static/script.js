const chatForm = document.getElementById("chatForm");
const messageInput = document.getElementById("messageInput");
const chatBody = document.getElementById("chatBody");
const typingIndicator = document.getElementById("typingIndicator");
const sendButton = document.getElementById("sendButton");

// Keep the same session ID during this browser session.
let sessionId = sessionStorage.getItem("chatSessionId");

if (!sessionId) {
    sessionId = crypto.randomUUID();
    sessionStorage.setItem("chatSessionId", sessionId);
}

function addMessage(message, sender) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", `${sender}-message`);

    const content = document.createElement("div");
    content.classList.add("message-content");

    // Display the response as text, not executable HTML.
    content.textContent = message;

    messageElement.appendChild(content);
    chatBody.appendChild(messageElement);

    chatBody.scrollTop = chatBody.scrollHeight;
}

chatForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const message = messageInput.value.trim();

    if (!message) {
        return;
    }

    addMessage(message, "user");

    messageInput.value = "";
    sendButton.disabled = true;
    typingIndicator.style.display = "block";

    try {
        const response = await fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                sessionId: sessionId,
                message: message
            })
        });

        const data = await response.json();

        if (!response.ok || data.error) {
            throw new Error(data.error || "Unable to get a response.");
        }

        addMessage(data.answer, "bot");

    } catch (error) {
        addMessage(
            "Sorry, I couldn't process your message. Please try again.",
            "bot"
        );

        console.error("Chat error:", error);

    } finally {
        typingIndicator.style.display = "none";
        sendButton.disabled = false;
        messageInput.focus();
    }
});

const newChatButton = document.getElementById("newChatButton");

newChatButton.addEventListener("click", () => {
    sessionId = crypto.randomUUID();

    sessionStorage.setItem("chatSessionId", sessionId);

    chatBody.innerHTML = `
        <div class="message bot-message">
            <div class="message-content">
                Hello! 👋 Welcome to AI Support.

                How can I help you today?
            </div>
        </div>
    `;

    messageInput.value = "";
    messageInput.focus();
});