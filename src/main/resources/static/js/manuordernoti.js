document.addEventListener("DOMContentLoaded", function() {
	fetchOrders();
});

function fetchOrders() {
    fetch("http://localhost:8080/api/distributor/orders/manu")
        .then(response => response.json())
        .then(data => {
			console.log("Orders:", data);
            const tableBody = document.getElementById("orderTableBody");
            tableBody.innerHTML = "";
              data.forEach(order => {
                    const formattedOrderDate = new Date(order.orderDate).toLocaleDateString('en-US', { month: '2-digit', day: '2-digit', year: 'numeric' });
                    const formattedDeliverDate = new Date(order.deliverDate).toLocaleDateString('en-US', { month: '2-digit', day: '2-digit', year: 'numeric' });
                    const row = `
                        <tr>
                            <td>${order.companyName}</td>
                            <td>${order.items.map(item => item.name).join(", ")}</td>
                            <td>${formattedOrderDate}</td>
                            <td>${formattedDeliverDate}</td>
                            <td>${order.deliverStatus}</td>
                            <td>${order.transactionStatus}</td>
                            <td>
                                <button class="btn btn-info btn-sm" onclick="viewOrderDetails(${order.id})">View Details</button>
                                <button class="btn btn-primary btn-sm" onclick="viewDistributorProfile(${order.distributorId})">View Profile</button>
                                <button class="btn btn-secondary btn-sm chat-btn" data-manu-user-id="${order.distributorUserId}" onclick="openChatWithCustomer(this)">Chat</button>
                                <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#editOrderModal" data-order-id="${order.id}" onclick="openEditOrderModal(${order.id})">Edit</button>
                            </td>
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                });
        })
        .catch(error => console.error("Error fetching orders:", error));
}

// Expose a global function to refresh orders
window.updateDistributorOrders = function() {
    fetchOrders();
};

function viewOrderDetails(orderId) {
	fetch(`http://localhost:8080/api/distributor/orders/${orderId}`)
		.then(response => response.json())
		.then(order => {
			document.getElementById("orderDetailsBody").innerHTML = `
				<p><strong>Order ID:</strong> ${order.id}</p>
                <p><strong>Items:</strong> ${order.items.map(item => `${item.name} (x${item.quantity}) - $${item.total}`).join("<br>")}</p>
                <p><strong>Total Amount:</strong> $${order.totalAmount}</p>
                <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
                <p><strong>Address:</strong> ${order.address}</p>
                <p><strong>Order Date:</strong> ${order.orderDate}</p>
                <p><strong>Delivery Date:</strong> ${order.deliveryDate}</p>
            `;
			new bootstrap.Modal(document.getElementById("orderDetailsModal")).show();
		})
		.catch(error => console.error("Error fetching order details:", error));
}

function viewDistributorProfile(distributorId) {
    fetch(`http://localhost:8080/api/distributors/${distributorId}`)
        .then(response => response.json())
        .then(distri => {
            const profileImage = distri.profileImg && distri.profileImg.trim() !== "" 
                ? distri.profileImg 
                : "/img/default-profile.jpg"; // Corrected path

            document.getElementById("distriProfileBody").innerHTML = `
                <img src="${profileImage}" alt="Profile" class="rounded-circle mb-2" width="100">
                <h5>${distri.companyName}</h5>
                <p><strong>Address:</strong> ${distri.address}</p>
                <p><strong>Contact:</strong> ${distri.contactInfo}</p>
            `;
            new bootstrap.Modal(document.getElementById("distriProfileModal")).show();
        })
        .catch(error => console.error("Error fetching manufacturer profile:", error));
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

			document.getElementById("confirmAcceptOrder").onclick = function() {
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

/* function confirmAcceptOrder(orderId) {
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
			if (response.ok) {
				alert("Order accepted and updated successfully!");
				fetchOrders();
				bootstrap.Modal.getInstance(document.getElementById("acceptOrderModal")).hide();
			}
		})
		.catch(error => console.error("Error accepting order:", error));
}

function declineOrder(orderId) {
	fetch(`http://localhost:8080/api/orders/decline/${orderId}`, { method: "DELETE" })
		.then(response => {
			if (response.ok) {
				alert("Order declined!");
				fetchOrders();
			}
		})
		.catch(error => console.error("Error declining order:", error));
} */

function openChatWithCustomer(button) {
    const manuUserId = button.getAttribute("data-manu-user-id");
    console.log("Opening chat with manu user ID:", manuUserId);
    if (!manuUserId || manuUserId === "null" || manuUserId === "") {
        console.error("Invalid manuUserId:", manuUserId);
        alert("Cannot start chat: manu ID is missing or invalid.");
        return;
    }
    window.openChat(manuUserId, "Manufacturer");
}

// New function to open edit order modal and populate it
function openEditOrderModal(orderId) {
    fetch(`http://localhost:8080/api/distributor/orders/${orderId}`)
        .then(response => response.json())
        .then(order => {
            document.getElementById("editOrderId").value = order.id;
            document.getElementById("editDelivered").value = order.deliverStatus;
            document.getElementById("editPaid").value = order.transactionStatus;

            // Handle form submission
            document.getElementById("editOrderForm").onsubmit = function(event) {
                event.preventDefault();
                updateOrderStatus(order.id);
				console.log("order id in open edit modal", order.id);
            };
        })
        .catch(error => console.error("Error fetching order for edit:", error));
}

// New function to update order status
/* function updateOrderStatus(orderId) {
    const deliverStatus = document.getElementById("editDelivered").value;
    const transactionStatus = document.getElementById("editPaid").value;

    const requestData = {
        deliverStatus: deliverStatus,
        transactionStatus: transactionStatus
    };

    fetch(`http://localhost:8080/api/manu/orders/update/${orderId}`, {
        method: "PUT",
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
        alert("Order status updated successfully!");
        fetchOrders();
        bootstrap.Modal.getInstance(document.getElementById("editOrderModal")).hide();
    })
    .catch(error => console.error("Error updating order status:", error));
} */

function updateOrderStatus(orderId) {
    const deliverStatus = document.getElementById("editDelivered").value;
    const transactionStatus = document.getElementById("editPaid").value;
	if (!orderId || orderId === 'undefined' || isNaN(orderId)) {
	        console.error('Invalid orderId:', orderId);
	        return; // Or throw an error/alert the user
	    }

    fetch(`http://localhost:8080/api/distributor/orders/update/${orderId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ deliverStatus, transactionStatus }),
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text); });
        }
        return response.text();
    })
    .then(() => {
        alert("Order status updated successfully!");
        fetchOrders(); // Refresh the orders table
        fetchDashboardStats(); // Refresh the dashboard statistics
    })
    .catch(error => console.error("Error updating order status:", error));
}

function fetchDashboardStats() {
    const manufacturerId = document.getElementById("manufacturerId").value;

    // Fetch updated statistics
    fetch(`http://localhost:8080/api/manufacturer/stats/${manufacturerId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById("productsSold").innerText = data.productsSold || 0;
            document.getElementById("netProfit").innerText = `$${data.netProfit?.toFixed(2) || 0}`;
            document.getElementById("customerSatisfaction").innerText = `${data.customerSatisfaction?.toFixed(1) || 0}%`;
        })
        .catch(error => console.error("Error fetching dashboard stats:", error));
}

