document.addEventListener("DOMContentLoaded", function () {
    // Assuming manufacturerId is passed from the backend via a script tag or data attribute
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    loadCompletedOrders(manufacturerId);
	console.log("ManufacturerId ", manufacturerId);
    loadRawMaterials(manufacturerId);
    loadCategories();
});

// Function to fetch completed orders from ManuOrder
function loadCompletedOrders(manufacturerId) {
    fetch(`http://localhost:8080/manufacturermanageinventory/api/manu-orders/completed?manufacturerId=${manufacturerId}`)
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("completedOrdersBody");
            tableBody.innerHTML = "";

            data.forEach(order => {
                const items = order.orderItems.map(item => 
                    `${item.supplierRawMaterial.name} (${item.quantity})`
                ).join(", ");
                const totalCost = order.orderItems.reduce((sum, item) => 
                    sum + (item.quantity * item.supplierRawMaterial.unitCost), 0).toFixed(2);
                tableBody.innerHTML += `
                    <tr>
                        <td>${order.orderId}</td>
                        <td>${order.supplier.companyName}</td>
                        <td>${order.orderDate}</td>
                        <td>${order.deliverDate}</td>
                        <td>${order.status}</td>
                        <td>${items}</td>
                        <td>${totalCost}</td>
                        <td>
                            <button class="btn btn-warning btn-sm" onclick="openOrderEditModal('${order.orderId}', '${order.orderItems[0].quantity}', '${totalCost}')">Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteOrder('${order.orderId}')">Delete</button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(error => console.error("Error loading completed orders:", error));
}

// Function to fetch manufacturer raw materials
function loadRawMaterials(manufacturerId) {
    fetch(`http://localhost:8080/manufacturermanageinventory/api/manu-raw-materials?manufacturerId=${manufacturerId}`)
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("rawMaterialsBody");
            tableBody.innerHTML = "";

            data.forEach(item => {
                tableBody.innerHTML += `
                    <tr>
                        <td>${item.name}</td>
                        <td>${item.supplierRawMaterial.category.categoryName}</td>
                        <td>${item.qtyOnHand}</td>
                        <td>${item.unitCost}</td>
                        <td>${item.unitPrice}</td>
                        <td>${new Date().toISOString().split("T")[0]}</td>
                        <td>
                            <button class="btn btn-warning btn-sm" onclick="openRawEditModal('${item.rawMaterialMid}', '${item.name}', '${item.category.categoryName}', '${item.qtyOnHand}', '${item.unitCost}', '${item.unitPrice}')">Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteRawMaterial('${item.rawMaterialMid}')">Delete</button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(error => console.error("Error loading raw materials:", error));
}

// Function to load categories for the add item form
function loadCategories() {
    fetch("http://localhost:8080/api/categories")
        .then(response => response.json())
        .then(data => {
            const categorySelect = document.getElementById("newItemCategory");
            data.forEach(category => {
                categorySelect.innerHTML += `
                    <option value="${category.categoryId}">${category.categoryName}</option>
                `;
            });
        })
        .catch(error => console.error("Error loading categories:", error));
}

// Function to add new raw material
function addRawMaterial() {
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    const name = document.getElementById("newItemName").value;
    const categoryId = document.getElementById("newItemCategory").value;
    const qtyOnHand = document.getElementById("newItemQuantity").value;
    const unitCost = document.getElementById("newItemUnitCost").value;
    const unitPrice = document.getElementById("newItemCost").value;

    const newRawMaterial = {
        name: name,
        category: { categoryId: categoryId },
        qtyOnHand: parseInt(qtyOnHand),
        unitCost: parseFloat(unitCost),
        unitPrice: parseFloat(unitPrice),
        manufacturer: { manufacturerId: manufacturerId }
    };

    fetch("http://localhost:8080/api/manu-raw-materials", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newRawMaterial)
    })
    .then(() => {
        loadRawMaterials(manufacturerId);
        document.getElementById("addItemForm").reset();
        bootstrap.Modal.getInstance(document.getElementById("addItemModal")).hide();
    })
    .catch(error => console.error("Error adding raw material:", error));
}

// Function to open edit modal for orders
function openOrderEditModal(orderId, quantity, cost) {
    document.getElementById("editOrderId").value = orderId;
    document.getElementById("editOrderQuantity").value = quantity;
    document.getElementById("editOrderCost").value = cost;

    document.getElementById("saveOrderEdit").setAttribute("onclick", `saveOrderEdit('${orderId}')`);
    new bootstrap.Modal(document.getElementById("editOrderModal")).show();
}

// Function to save edited order
function saveOrderEdit(orderId) {
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    const updatedData = {
        orderItems: [{
            quantity: parseInt(document.getElementById("editOrderQuantity").value)
        }]
    };

    fetch(`http://localhost:8080/api/manu-orders/${orderId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedData)
    })
    .then(() => {
        loadCompletedOrders(manufacturerId);
        bootstrap.Modal.getInstance(document.getElementById("editOrderModal")).hide();
    })
    .catch(error => console.error("Error updating order:", error));
}

// Function to delete order
function deleteOrder(orderId) {
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    if (!confirm("Are you sure you want to delete this order?")) return;

    fetch(`http://localhost:8080/api/manu-orders/${orderId}`, {
        method: "DELETE"
    })
    .then(() => loadCompletedOrders(manufacturerId))
    .catch(error => console.error("Error deleting order:", error));
}

// Function to open edit modal for raw materials
function openRawEditModal(id, name, category, qtyOnHand, unitCost, unitPrice) {
    document.getElementById("editRawName").value = name;
    document.getElementById("editRawCategory").value = category;
    document.getElementById("editRawQuantity").value = qtyOnHand;
    document.getElementById("editRawUnitCost").value = unitCost;
    document.getElementById("editRawCost").value = unitPrice;
    document.getElementById("editRawAddedDate").value = new Date().toISOString().split("T")[0];

    document.getElementById("saveRawEdit").setAttribute("onclick", `saveRawEdit('${id}')`);
    new bootstrap.Modal(document.getElementById("editRawModal")).show();
}

// Function to save edited raw material
function saveRawEdit(id) {
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    const updatedData = {
        name: document.getElementById("editRawName").value,
        qtyOnHand: parseInt(document.getElementById("editRawQuantity").value),
        unitCost: parseFloat(document.getElementById("editRawUnitCost").value),
        unitPrice: parseFloat(document.getElementById("editRawCost").value),
        manufacturer: { manufacturerId: manufacturerId }
    };

    fetch(`http://localhost:8080/api/manu-raw-materials/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedData)
    })
    .then(() => {
        loadRawMaterials(manufacturerId);
        bootstrap.Modal.getInstance(document.getElementById("editRawModal")).hide();
    })
    .catch(error => console.error("Error updating raw material:", error));
}

// Function to delete raw material
function deleteRawMaterial(id) {
    const manufacturerId = document.getElementById("main").dataset.manufacturerId;
    if (!confirm("Are you sure you want to delete this raw material?")) return;

    fetch(`http://localhost:8080/api/manu-raw-materials/${id}`, {
        method: "DELETE"
    })
    .then(() => loadRawMaterials(manufacturerId))
    .catch(error => console.error("Error deleting raw material:", error));
}