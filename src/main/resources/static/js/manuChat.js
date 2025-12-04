document.addEventListener("DOMContentLoaded", function() {
	const chatIcon = document.getElementById("chat-icon");
	const chatBox = document.getElementById("chat-box");
	const chatList = document.getElementById("chat-list");
	const chatModal = new bootstrap.Modal(document.getElementById("chatModal"));
	const chatMessages = document.getElementById("chat-messages");
	const messageInput = document.getElementById("message-input");
	const imageInput = document.getElementById("image-input");
	const sendMessageBtn = document.getElementById("send-message");
	const createInvoiceBtn = document.getElementById("create-invoice");
	const invoiceModal = new bootstrap.Modal(document.getElementById("invoiceModal"));
	// const productselect = document.getElementById("product-select");
	const invoiceForm = document.getElementById("invoice-form");
	const productContainer = document.getElementById("product-container");
	const addProductBtn = document.getElementById("add-product");
	const paymentMethodSelect = document.getElementById("payment-method");
	// Ensure userId from Thymeleaf is assigned to currentUserId
	const currentUserId = typeof userId !== "undefined" && userId ? userId : null;

	if (!currentUserId) {
		console.error("❌ currentUserId is not set. Please check session.loggedInUser.userId in Thymeleaf!");
	} else {
		console.log("✅ Current User ID:", currentUserId);
	}


	let activeChatUserId = null;
	let lastMessageTime = null;

	const API_BASE_URL = "/api/chat/manu"; // Replace with your actual backend URL


	// RECORD TIME
	function formatDateTime(timestamp) {
		const date = new Date(timestamp);
		return date.toLocaleString(); // Format as per user's locale
	}

	function addMessage(messageText) {
		if (!messageText.trim()) return;

		const currentTime = new Date().getTime();
		let showTimestamp = false;

		if (!lastMessageTime || (currentTime - lastMessageTime) >= 3600000) { // 1 hour = 3600000 ms
			showTimestamp = true;
			lastMessageTime = currentTime;
		}

		const messageWrapper = document.createElement("div");
		messageWrapper.classList.add("message-wrapper");

		if (showTimestamp) {
			const timestampDiv = document.createElement("div");
			timestampDiv.classList.add("message-timestamp");
			timestampDiv.textContent = formatDateTime(currentTime);
			chatMessages.appendChild(timestampDiv);
		}

		const messageDiv = document.createElement("div");
		messageDiv.classList.add("chat-message", "bg-primary", "text-white", "p-2", "rounded", "mb-1");
		messageDiv.textContent = messageText;

		messageWrapper.appendChild(messageDiv);
		chatMessages.appendChild(messageWrapper);
		chatMessages.scrollTop = chatMessages.scrollHeight; // Auto-scroll to the latest message
	}

	/* sendMessageBtn.addEventListener("click", function () {
		addMessage(messageInput.value);
		messageInput.value = ""; // Clear input field
	}); */

	// Send message on Enter keypress
	messageInput.addEventListener("keypress", async function(event) {
		if (event.key === "Enter") {
			const messageText = messageInput.value.trim();
			const imageFile = imageInput.files[0];
			if (messageText || imageFile) {
				await sendMessage(messageText, imageFile);
			}
		}
	});



	// Draging Animation
	// Variable to store the offset of the mouse when dragging starts
	let offsetX, offsetY, isDragging = false;



	// Mouse down event to start dragging
	chatIcon.addEventListener('mousedown', function(event) {
		isDragging = true;
		// Calculate the initial mouse offset relative to the top-left corner of the icon
		offsetX = event.clientX - chatIcon.getBoundingClientRect().left;
		offsetY = event.clientY - chatIcon.getBoundingClientRect().top;
		// Change cursor to indicate dragging
		chatIcon.style.cursor = 'grabbing';
	});

	// Mouse move event to move the icon when dragging
	document.addEventListener('mousemove', function(event) {
		if (isDragging) {
			// Update the position of the chat icon based on mouse movement
			chatIcon.style.left = `${event.clientX - offsetX}px`;
			chatIcon.style.top = `${event.clientY - offsetY}px`;
		}
	});

	// Mouse up event to stop dragging
	document.addEventListener('mouseup', function() {
		isDragging = false;
		// Change cursor back to default
		chatIcon.style.cursor = 'pointer';
	});




	// Toggle chat box visibility
	chatIcon.addEventListener("click", function() {
		chatBox.classList.toggle("d-none");
		fetchChatNotifications(); // Fetch latest chat notifications
		fetchChatList();
	});

	async function fetchChatNotifications() {
		try {
			// const userId = 14; // Replace with the actual user ID (e.g., from session or input)
			const response = await fetch(`${API_BASE_URL}/notifications?userId=${currentUserId}`, {
				headers: {
					"Authorization": `Bearer ${localStorage.getItem("authToken")}`, // Include token if required
					"Content-Type": "application/json"
				}
			});

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			const chats = await response.json();
			console.log("Chats response:", chats); // Log the response
			if (!Array.isArray(chats)) {
				throw new Error("Expected an array of chats, but received: " + JSON.stringify(chats));
			}

			renderChatNotifications(chats);
		} catch (error) {
			console.error("Error fetching chat notifications:", error);
		}
	}

	function renderChatNotifications(chats) {
		if (!Array.isArray(chats)) {
			console.error("Expected an array of chats, but received:", chats);
			return;
		}

		// const chatList = document.getElementById("chat-list");
		chatList.innerHTML = ""; // Clear previous notifications

		chats.forEach(chat => {
			const chatItem = document.createElement("div");
			chatItem.classList.add("chat-item", "d-flex", "align-items-center", "p-2", "border-bottom");

			chatItem.innerHTML = `
	            <img src="${chat.senderProfileImage || 'img/default-profile.jpg'}" class="chat-profile rounded-circle me-2" alt="Profile" width="40" height="40" />
	            <div class="chat-content flex-grow-1">
	                <span class="chat-name fw-bold">${chat.senderName || 'Unknown User'}</span>
	                <p class="chat-preview text-muted small mb-0">${chat.text || 'No message'}</p>
	            </div>
	            ${chat.unreadCount > 0 ? `<span class="chat-badge badge bg-danger rounded-pill ms-2">${chat.unreadCount}</span>` : ""}
	        `;

			chatItem.addEventListener("click", () => openChat(chat.senderId, chat.senderName));
			chatList.appendChild(chatItem);
		});
	}

	// Open chat function (fetch messages from backend)
	async function openChat(senderId, senderName) {
		if (!senderId) {
			console.error("❌ Sender ID is undefined!");
			return;
		}

		if (!currentUserId) {
			console.error("❌ Current User ID is not set! Cannot fetch chat.");
			return;
		}

		// Set the active chat user
		activeChatUserId = senderId;
		console.log("✅ Active chat set to user:", activeChatUserId);

		// Mark messages as read
		await markMessagesAsRead(senderId, currentUserId);

		// Fetch and render messages
		console.log(`📨 Fetching messages for senderId: ${senderId}, receiverId: ${currentUserId}`);

		try {
			const response = await fetch(`${API_BASE_URL}/messages?senderId=${senderId}&receiverId=${currentUserId}`, {
				headers: {
					"Authorization": `Bearer ${localStorage.getItem("authToken")}`,
					"Content-Type": "application/json"
				}
			});

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			const messages = await response.json();
			console.log("✅ Messages response:", messages);

			if (!Array.isArray(messages)) {
				throw new Error("Expected an array of messages, but received: " + JSON.stringify(messages));
			}

			renderChatMessages(messages);
		} catch (error) {
			console.error("Error fetching messages:", error);
		}

		// Refresh chat notifications to update unread counts
		await fetchChatNotifications();

		chatModal.show();
	}

	async function fetchChatList(userId) {
		try {
			const response = await fetch(`${API_BASE_URL}/list?userId=${userId}`, {
				headers: {
					"Authorization": `Bearer ${localStorage.getItem("authToken")}`,
					"Content-Type": "application/json"
				}
			});

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			const chatList = await response.json();
			console.log("Chat list:", chatList);
			renderChatList(chatList); // Render the chat list in the UI
		} catch (error) {
			console.error("Error fetching chat list:", error);
		}
	}

	function renderChatList(chatList) {
		chatList.innerHTML = ""; // Clear the chat list

		chatList.forEach(chat => {
			const chatItem = document.createElement("div");
			chatItem.classList.add("chat-item", "p-2", "border", "rounded", "mb-2");

			// Determine the last message
			const lastMessage = chat.messages[chat.messages.length - 1];
			const lastMessageText = lastMessage ? lastMessage.text : "No messages yet";
			const lastMessageSender = lastMessage ? (lastMessage.senderId === currentUserId ? "You" : chat.senderName) : "";
			const lastMessageTime = lastMessage ? formatDateTime(lastMessage.timestamp) : "";

			chatItem.innerHTML = `
	            <div class="d-flex justify-content-between align-items-center">
	                <div>
	                    <strong>${chat.senderName}</strong>
	                    <p class="mb-0">${lastMessageSender}: ${lastMessageText}</p>
	                    <small class="text-muted">${lastMessageTime}</small>
	                </div>
	                <button class="btn btn-primary btn-sm" onclick="openChat('${chat.senderId}', '${chat.senderName}')">Open Chat</button>
	            </div>
	        `;

			chatList.appendChild(chatItem); // Use chatList instead of chatListContainer
		});
	}

	async function markMessagesAsRead(senderId, receiverId) {
		try {
			const response = await fetch(`${API_BASE_URL}/markAsRead?senderId=${senderId}&receiverId=${receiverId}`, {
				method: "PUT",
				headers: {
					"Authorization": `Bearer ${localStorage.getItem("authToken")}`,
					"Content-Type": "application/json"
				}
			});

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			console.log("✅ Messages marked as read.");
		} catch (error) {
			console.error("Error marking messages as read:", error);
		}
	}



	function renderChatMessages(messages) {
		chatMessages.innerHTML = ""; // Clear the chat container

		// Ensure messages are sorted by timestamp (oldest to newest)
		messages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

		messages.forEach(msg => {
			console.log("Rendered messages: " + msg);
			const messageDiv = document.createElement("div");

			// Determine if the message was sent by the current user
			const isSentByUser = msg.senderId === currentUserId;

			messageDiv.classList.add("message", isSentByUser ? "sent" : "received", "p-2", "border", "rounded", "mb-2");

			messageDiv.dataset.id = msg.messageId;
			if (msg.text) {
				messageDiv.innerHTML = `<p class="mb-0">${msg.text}</p>`;
			}

			if (msg.imageUrl) {
				const imageElement = document.createElement("img");
				console.log("Image URL received: ", msg.imageUrl);
				imageElement.src = msg.imageUrl;
				imageElement.alt = "Sent Image";
				imageElement.classList.add("img-fluid", "rounded");
				imageElement.style.maxWidth = "250px";

				// Add error handling to debug loading issues
				imageElement.onerror = () => {
					console.error("Failed to load image from URL: ", msg.imageUrl);
					imageElement.alt = "Image failed to load";
				};
				imageElement.onload = () => {
					console.log("Image loaded successfully from URL: ", msg.imageUrl);
				};

				messageDiv.appendChild(imageElement);
			}

			messageDiv.addEventListener("contextmenu", function(event) {
				event.preventDefault();
				showContextMenu(event, messageDiv);
			});

			chatMessages.appendChild(messageDiv);
		});

		// Auto-scroll to the latest message
		chatMessages.scrollTop = chatMessages.scrollHeight;
	}


	// Send message on button click
	sendMessageBtn.addEventListener("click", async () => {
		const messageText = messageInput.value.trim();
		console.log("📌 messageInput:", messageInput);
		console.log("Message text is: ", messageText);
		const imageFile = imageInput.files[0];
		console.log("📩 messageInput.value:", messageInput.value);

		if (messageText || imageFile) {
			console.log("🔥 Calling sendMessage() now...");
			await sendMessage(messageText, imageFile);
		}
	});

	async function sendMessage(text, imageFile) {
		const formData = new FormData();
		formData.append("senderId", currentUserId);
		formData.append("receiverId", activeChatUserId);
		if (text) formData.append("text", text);
		if (imageFile) formData.append("image", imageFile);

		try {
			console.log("Sending message with formData:", [...formData.entries()]);
			const response = await fetch(`${API_BASE_URL}/send`, {
				method: "POST",
				body: formData,
			});

			if (!response.ok) {
				const errorText = await response.text();
				throw new Error(`Failed to send message: ${response.status} - ${errorText}`);
			}

			const sentMessage = await response.json();
			console.log("Sent message:", sentMessage);
			appendMessage(sentMessage);
			messageInput.value = "";
			imageInput.value = "";
			await fetchChatNotifications();
		} catch (error) {
			console.error("Error sending message:", error);
		}
	}

	function appendMessage(message) {
		const messageDiv = document.createElement("div");
		const isSentByUser = message.senderId === currentUserId;
		console.log("Appending message - senderId:", message.senderId, "currentUserId:", currentUserId, "isSentByUser:", isSentByUser);
		messageDiv.classList.add("message", isSentByUser ? "sent" : "received", "p-2", "rounded", "mb-2");
		if (isSentByUser) {
			messageDiv.classList.add("bg-primary", "text-white", "ms-auto"); // Right side, blue
		} else {
			messageDiv.classList.add("bg-white", "text-dark", "me-auto"); // Left side, white
		}
		messageDiv.style.maxWidth = "75%"; // Limit width for alignment

		if (message.text) {
			messageDiv.innerHTML = `<p class="mb-0">${message.text}</p>`;
		}
		if (message.imageUrl) {
			const imageElement = document.createElement("img");
			imageElement.src = message.imageUrl;
			imageElement.alt = "Sent Image";
			imageElement.classList.add("img-fluid", "rounded");
			imageElement.style.maxWidth = "250px";
			messageDiv.appendChild(imageElement);
		}

		chatMessages.appendChild(messageDiv);
		chatMessages.scrollTop = chatMessages.scrollHeight;
	}

	function showContextMenu(event, messageElement) {
		document.querySelectorAll(".context-menu").forEach(menu => menu.remove());

		const menu = document.createElement("div");
		menu.classList.add("context-menu", "p-2", "bg-white", "shadow", "rounded", "border");
		menu.style.position = "absolute";
		menu.style.top = `${event.clientY}px`;
		menu.style.left = `${event.clientX}px`;
		menu.style.zIndex = "1000";

		// Create buttons and attach event listeners
		const editButton = document.createElement("button");
		editButton.classList.add("dropdown-item");
		editButton.textContent = "✏️ Edit";
		editButton.addEventListener("click", () => editMessage(event, messageElement));

		const pinButton = document.createElement("button");
		pinButton.classList.add("dropdown-item");
		pinButton.textContent = "📌 Pin";
		pinButton.addEventListener("click", () => pinMessage(event, messageElement));

		const deleteButton = document.createElement("button");
		deleteButton.classList.add("dropdown-item", "text-danger");
		deleteButton.textContent = "🗑 Delete";
		deleteButton.addEventListener("click", () => deleteMessage(event, messageElement));

		// Append buttons to the menu
		menu.appendChild(editButton);
		menu.appendChild(pinButton);
		menu.appendChild(deleteButton);

		// Append the menu to the body
		document.body.appendChild(menu);

		// Remove the menu when clicking outside
		document.addEventListener("click", function removeMenu() {
			menu.remove();
			document.removeEventListener("click", removeMenu);
		}, { once: true });
	}

	// Edit Message
	window.editMessage = async function(event, messageElement) {
		const newText = prompt("Edit your message:", messageElement.innerText);
		if (!newText) return;

		try {
			const payload = { messageId: messageElement.dataset.id, text: newText };
			console.log("Sending payload:", payload);

			const response = await fetch(`${API_BASE_URL}/edit`, {
				method: "PUT",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(payload)
			});

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			messageElement.innerHTML = `<p class="mb-0">${newText}</p>`;
		} catch (error) {
			console.error("Error editing message:", error);
		}
	};

	// Pin Message
	window.pinMessage = function(event, messageElement) {
		messageElement.classList.toggle("border-primary");
		messageElement.classList.toggle("fw-bold");
	};

	// Delete Message
	window.deleteMessage = async function(event, messageElement) {
		try {
			await fetch(`${API_BASE_URL}/delete`, {
				method: "DELETE",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ messageId: messageElement.dataset.id })
			});

			messageElement.remove();
		} catch (error) {
			console.error("Error deleting message:", error);
		}
	};

	async function fetchPaymentMethods() {
		try {
			console.log("Fetching payment methods...");
			console.log("Current user id in fetchPaymentMethods is: ", currentUserId);
			const response = await fetch(`${API_BASE_URL}/payment-methods/user/${currentUserId}`);
			const paymentMethods = await response.json();
			console.log("Fetched payment methods:", paymentMethods);

			paymentMethodSelect.innerHTML = ""; // Clear previous options

			paymentMethods.forEach(method => {
				const option = document.createElement("option");
				option.value = method.payMethodId;
				option.textContent = method.payMethodName;
				paymentMethodSelect.appendChild(option);
			});
		} catch (error) {
			console.error("Error fetching payment methods:", error);
		}
	}

	// Function to create a new product selection row
	function addProductRow() {
		const productRow = document.createElement("div");
		productRow.classList.add("product-row", "d-flex", "gap-2", "mb-2");

		productRow.innerHTML = `
        <select class="form-select product-select" required></select>
        <input type="text" class="form-control quantity" placeholder="Qty" required>
        <input type="text" class="form-control unit-price" placeholder="Unit Price" required>
        <button type="button" class="btn btn-danger remove-product">✖</button>
    `;

		productContainer.appendChild(productRow);
		fetchProductOptions(productRow.querySelector(".product-select"));

		// Remove product row on click
		productRow.querySelector(".remove-product").addEventListener("click", function() {
			productRow.remove();
		});
	}

	// Fetch products the seller sells from the backend
	async function fetchProductOptions(selectElement) {

		try {
			console.log("Fetching products for user ID:", activeChatUserId);
			const response = await fetch(`${API_BASE_URL}/seller-products?userId=${currentUserId}`);
			const products = await response.json();
			console.log("Fetched products:", response);

			selectElement.innerHTML = '<option value="">Select Product</option>';
			products.forEach(product => {
				console.log("Products:", product);
				const option = document.createElement("option");
				option.value = product.productMid; // Use rawMaterialSid as the value
				option.textContent = product.name; // Use the product name as the display text
				selectElement.appendChild(option);
			});
		} catch (error) {
			console.error("Error fetching products:", error);
		}
	}


	// Event listener to add new product row
	addProductBtn.addEventListener("click", addProductRow);

	// Open invoice modal
	createInvoiceBtn.addEventListener("click", function() {
		invoiceModal.show();
		console.log("Active chat user ID before fetching products:", activeChatUserId);
		productContainer.innerHTML = ""; // Clear previous products
		addProductRow(); // Add initial product selection

		fetchPaymentMethods(); // Fetch payment methods
	});

	// Handle invoice form submission
	invoiceForm.addEventListener("submit", async function(event) {
		event.preventDefault();

		const products = [];
		document.querySelectorAll(".product-row").forEach(row => {
			const manuProductId = row.querySelector(".product-select").value;
			console.log("Selected manuProductId:", manuProductId);
			const quantity = row.querySelector(".quantity").value;
			const unitPrice = row.querySelector(".unit-price").value;

			if (manuProductId && quantity && unitPrice) {
				products.push({ manuProductId, quantity, unitPrice });
			}
		});

		if (products.length === 0) {
			alert("Please add at least one product.");
			return;
		}

		const invoiceData = {
			orderDate: document.getElementById("ordered-date").value, // Format: yyyy-MM-dd
			deliverDate: document.getElementById("delivered-date").value, // Format: yyyy-MM-dd
			paymentMethodId: paymentMethodSelect.value,
			sellerId: currentUserId,
			buyerId: activeChatUserId,
			products: products,
			totalAmount: document.getElementById("total-amount").value
		};

		try {
			console.log("Sending invoice data:", invoiceData);

			const response = await fetch(`/api/invoices/manu`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(invoiceData)
			});

			console.log("Response status:", response.status);

			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}

			const responseText = await response.text(); // Log the raw response
			console.log("Raw response:", responseText);

			const invoice = JSON.parse(responseText); // Parse the response as JSON
			console.log("Parsed invoice response:", invoice);

			console.log("Invoice image URL:", invoice.imageUrl);

			if (invoice && invoice.imageUrl) {
				sendInvoiceToBuyer(invoice.imageUrl, activeChatUserId);
			} else {
				console.error("Error generating invoice image.");
			}

			invoiceModal.hide();
		} catch (error) {
			console.error("Error creating invoice:", error);
		}
	});

	async function sendInvoiceToBuyer(invoiceImageUrl, receiverId, text = "Invoice Voucher") {
		try {
			const response = await fetch(`${API_BASE_URL}/sendInvoice`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({
					senderId: currentUserId,
					receiverId: receiverId,
					imageUrl: invoiceImageUrl,
					text: text
				})
			});

			if (!response.ok) {
				const errorResponse = await response.json();
				throw new Error(`Failed to send invoice: ${errorResponse.error}`);
			}

			const result = await response.json();
			console.log("Invoice sent successfully:", result);
			alert("Invoice sent successfully!");
		} catch (error) {
			console.error("Error sending invoice:", error);
			alert("Failed to send invoice. Please try again.");
		}
	}


	// Load chat notifications on page load
	fetchChatNotifications();


});

