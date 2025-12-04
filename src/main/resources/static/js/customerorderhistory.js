document.addEventListener("DOMContentLoaded", function() {
	fetch("http://localhost:8080/api/orders/customer")
	    .then(response => {
	        if (!response.ok) {
	            throw new Error(`HTTP error! Status: ${response.status}`);
	        }
	        return response.json();
	    })
	    .then(data => {
	        console.log("API Data:", data);
	        const tableBody = document.getElementById("orderHistoryTable");
	        if (!tableBody) {
	            console.error("Table body element 'orderHistoryTable' not found!");
	            return;
	        }
	        tableBody.innerHTML = "";
	        if (!data || data.length === 0) {
	            console.log("No orders returned from API");
	            tableBody.innerHTML = "<tr><td colspan='9'>No orders found</td></tr>";
	            return;
	        }
	        data.forEach(order => {
	            console.log("Order:", order);
	            const formattedDate = new Date(order.orderDate).toLocaleDateString();
	            if (!order.items || order.items.length === 0) {
	                console.log("No items in order:", order.id);
	                return;
	            }
	            order.items.forEach(item => {
	                console.log("Item:", item);
	                const row = `<tr>
	                    <td>${order.id || 'N/A'}</td>
	                    <td>${formattedDate}</td>
	                    <td>${item.name || 'N/A'}</td>
	                    <td>${item.quantity || 'N/A'}</td>
	                    <td>MMK ${item.price ? item.price.toLocaleString() : 'N/A'}</td>
	                    <td>MMK ${item.price && item.quantity ? (item.price * item.quantity).toLocaleString() : 'N/A'}</td>
	                    <td>${order.paymentMethod || 'N/A'}</td>
	                    <td>${order.status || 'N/A'}</td>
						<td>${order.deliverStatus || 'N/A'}</td>
						<td>${order.transactionStatus || 'N/A'}</td>
	                    <td>
	                        <button class="btn btn-primary btn-sm" onclick="viewProductDetails(${item.productId || ''})">View Product</button>
	                        <button class="btn btn-info btn-sm" onclick="viewSellerProfile(${item.distributorId || ''})">View Seller</button>
	                    </td>
	                </tr>`;
	                tableBody.innerHTML += row;
	            });
	        });
	    })
	    .catch(error => console.error("Error fetching orders:", error));
});


// Function to view product details
function viewProductDetails(productId) {
    fetch(`/api/products/${productId}`)
        .then(response => response.json())
        .then(product => {
            // Populate product details into the modal
            document.getElementById("productDetails").innerHTML = `
                <img src="${product.image}" class="img-fluid mb-3" alt="Product Image">
                <h5>${product.name}</h5>
                <p>${product.description}</p>
                <p><strong>Price:</strong> $${product.price.toFixed(2)}</p>
			`;
			
            // Show the modal
            new bootstrap.Modal(document.getElementById("productDetailsModal")).show();
        })
        .catch(error => console.error("Error fetching product details:", error));
}

function viewSellerProfile(distributorId) {
    console.log("Fetching seller profile for distributorId:", distributorId); // Debugging

    if (!distributorId) {
        console.error("Invalid distributorId:", distributorId);
        return;
    }

    fetch(`/api/distributors/${distributorId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Distributor not found");
            }
            return response.json();
        })
        .then(seller => {
			console.log("Seller Data:", seller);
            document.getElementById("sellerProfile").innerHTML = `
                <img src="${seller.profileImage || 'default-profile.png'}" class="img-fluid mb-3" alt="Seller Profile Image">
                <h4>${seller.companyName}</h4>
                <p><strong>Email:</strong> ${seller.email}</p>
                <p><strong>Phone:</strong> ${seller.contactInfo}</p>
                <p><strong>Address:</strong> ${seller.address}</p>
            `;
            new bootstrap.Modal(document.getElementById("sellerProfileModal")).show();
        })
        .catch(error => console.error("Error fetching seller profile:", error));
}



