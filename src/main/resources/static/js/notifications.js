document.addEventListener("DOMContentLoaded", function () {
    fetchOrders();

});

function fetchOrders() {
	
    fetch("http://localhost:8080/api/orders")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("ordersTable");
            tableBody.innerHTML = "";
			
            data.filter(order => order.status === "Pending") // Only show non-accepted orders
			                .forEach(order => {
                    const row = `
                        <tr data-order-id="${order.id}">
                            <td>${order.customerName}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.items.map(item => item.name).join(", ")}</td>
                            <td>
                                <button class="btn btn-info btn-sm" onclick="viewOrderDetails(${order.id})">View Details</button>
                                <button class="btn btn-primary btn-sm" onclick="viewCustomerProfile(${order.customerId})">View Profile</button>
                                <button class="btn btn-secondary btn-sm chat-btn" data-customer-user-id="${order.customerUserId}" onclick="openChatWithCustomer(this)">Chat</button>
                                <button class="btn btn-success btn-sm" onclick="openAcceptOrderModal(${order.id})">Accept</button>
                                <button class="btn btn-danger btn-sm" onclick="declineOrder(${order.id})">Decline</button>
                            </td>
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                });
        })
        .catch(error => console.error("Error fetching orders:", error));
}

function openChatWithCustomer(button) {
    const customerUserId = button.getAttribute("data-customer-user-id");
    console.log("Opening chat with customer user ID:", customerUserId);
    window.openChat(customerUserId, "Customer"); // Call the global openChat from distriChat.js
}

function viewOrderDetails(orderId) {
    fetch(`http://localhost:8080/api/orders/${orderId}`)
        .then(response => response.json())
        .then(order => {
            document.getElementById("orderDetailsBody").innerHTML = `
				<p><strong>Order ID:</strong> ${order.id}</p>
                <p><strong>Items:</strong> ${order.items.map(item => `${item.name} (x${item.quantity}) - $${item.total}`).join("<br>")}</p>
                <p><strong>Total Amount:</strong> $${order.totalAmount}</p>
                <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
                <p><strong>Address:</strong> ${order.address}</p>
                <p><strong>Note:</strong> ${order.note}</p>
                <p><strong>Order Date:</strong> ${order.orderDate}</p>
                <p><strong>Delivery Date:</strong> ${order.deliveryDate}</p>
            `;
            new bootstrap.Modal(document.getElementById("orderDetailsModal")).show();
        })
        .catch(error => console.error("Error fetching order details:", error));
}

function viewCustomerProfile(customerId) {
    fetch(`http://localhost:8080/api/customer/${customerId}`)
        .then(response => response.json())
        .then(customer => {
            document.getElementById("customerProfileBody").innerHTML = `
                <img src="${customer.profileImage || 'img/default-profile.jpg'}" alt="Profile" class="rounded-circle mb-2" width="100">
                <h5>${customer.name}</h5>
                <p><strong>Address:</strong> ${customer.address}</p>
                <p><strong>Contact:</strong> ${customer.contactInfo}</p>
            `;
            new bootstrap.Modal(document.getElementById("customerProfileModal")).show();
        })
        .catch(error => console.error("Error fetching customer profile:", error));
}

function openAcceptOrderModal(orderId) {
    fetch(`http://localhost:8080/api/orders/${orderId}`)
        .then(response => response.json())
        .then(order => {
            let orderHTML = "";
            order.items.forEach(item => {
                orderHTML += `
                    <div class="mb-2">
                        <strong>${item.name}</strong> (Stock: ${item.availableStock})
                        <input type="number" class="form-control mt-1" id="qty-${item.id}" value="${item.quantity}" 
                               min="1" max="${item.availableStock}" oninput="validateStock(${item.id}, ${item.availableStock})">
                    </div>
                `;
            });

            document.getElementById("acceptOrderBody").innerHTML = orderHTML;
            document.getElementById("stockWarning").innerText = "";

            document.getElementById("confirmAcceptOrder").onclick = function () {
                confirmAcceptOrder(order.id);
            };

            new bootstrap.Modal(document.getElementById("acceptOrderModal")).show();
        })
        .catch(error => console.error("Error fetching order:", error));
}

function validateStock(itemId, availableStock) {
    const inputField = document.getElementById(`qty-${itemId}`);
    const warningDiv = document.getElementById("stockWarning");

    if (inputField.value > availableStock) {
        warningDiv.innerText = `Warning: Only ${availableStock} left in stock!`;
        inputField.value = availableStock; 
    } else {
        warningDiv.innerText = "";
    }
}

function confirmAcceptOrder(orderId) {
    const deliveryDate = document.getElementById("deliveryDate").value;
    let updatedItems = [];

    document.querySelectorAll("#acceptOrderBody input").forEach(input => {
        const itemId = input.id.split("-")[1];
        updatedItems.push({ id: itemId, quantity: input.value });
    });

    const requestData = { deliveryDate, items: updatedItems };

    fetch(`http://localhost:8080/api/orders/accept/${orderId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData),
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text); });
        }
        return response.text();
    })
    .then(() => {
        alert("Order accepted successfully!");
        // Remove the order from the table
        const row = document.querySelector(`tr[data-order-id="${orderId}"]`);
        if (row) row.remove();
        // Trigger update in distributororderfromcustomer.js
        if (window.updateDistributorOrders) {
            window.updateDistributorOrders();
        }
        bootstrap.Modal.getInstance(document.getElementById("acceptOrderModal")).hide();
    })
    .catch(error => console.error("Error accepting order:", error));
}

// No need to modify sendNotification here since backend handles it

function declineOrder(orderId, customerUserId) {
    const reason = prompt("Please provide a reason for declining the order:");
    if (!reason) {
        alert("A reason is required to decline the order.");
        return;
    }

    fetch(`http://localhost:8080/api/orders/decline/${orderId}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ reason })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text); });
        }
        return response.text();
    })
    .then(() => {
        alert("Order declined successfully!");
        sendNotification(customerUserId, `Your order #${orderId} has been declined. Reason: ${reason}`);
        fetchOrders();
    })
    .catch(error => console.error("Error declining order:", error));
}

// Notification function using chat system
function sendNotification(customerUserId, message) {
    const url = `http://localhost:8080/api/chat/send?senderId=${userId}&receiverId=${customerUserId}&text=${encodeURIComponent(message)}`;
    fetch(url, {
        method: "POST"
    })
    .then(response => {
        if (!response.ok) throw new Error("Failed to send notification");
    })
    .catch(error => console.error("Error sending notification:", error));
}


