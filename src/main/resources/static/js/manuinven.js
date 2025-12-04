let manufacturerId;
document.addEventListener("DOMContentLoaded", function () {
    loadCompletedOrders();
    fetchCategories();
	fetchEditCategories();
	manufacturerId = document.getElementById("main").getAttribute("data-manufacturer-id");   
	console.log("Manufacturerid is", manufacturerId); 
	if (manufacturerId) {
        fetchManufacturerInventory(manufacturerId);
    }
	
	// Add event listener for the form submission
	    document.getElementById('addItemForm').addEventListener('submit', function(event) {
	        event.preventDefault();
	        
	        const itemName = document.getElementById('newItemName').value;
	        const category = document.getElementById('category').value;
	        const quantity = document.getElementById('newItemQuantity').value;
	        const totalCost = document.getElementById('newItemCost').value;
	        const costPerUnit = document.getElementById('newItemUnitCost').value;
	        const addedDate = document.getElementById('addedDate').value;

			fetch(`/api/manu/products?manufacturerId=${manufacturerId}`, {
			    method: 'POST',
			    headers: { 'Content-Type': 'application/json' },
			    body: JSON.stringify({
			        name: itemName,
			        category: { categoryId: category },
			        quantity: quantity,
			        cost: totalCost,
			        perUnitCost: costPerUnit,
			        addedDate: addedDate
			    })
			})
			.then(response => {
			    if (!response.ok) {
			        // Log the raw response for debugging
			        return response.text().then(text => {
			            console.error("Server returned an error:", text);
			            throw new Error("Server error: " + text);
			        });
			    }
			    return response.json();
			})
			.then(() => {
			    $('#addItemModal').modal('hide'); // Close the modal
			    fetchManufacturerInventory(manufacturerId); // Refresh the table
			})
			.catch(error => console.error('Error adding item:', error));
	    });
});

function loadCompletedOrders() {
    fetch(`http://localhost:8080/api/manu-raw-materials`)
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("completedOrdersBody");
            tableBody.innerHTML = '';
            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.categoryName}</td>
                    <td>
                        <input type="number" value="${item.qtyOnHand}" min="0" max="${item.qtyOnHand}" id="quantity-${item.rawMaterialMid}" onchange="updateQuantity(${item.rawMaterialMid})">
                    </td>
                    <td>${item.unitCost}</td>
                    <td>${item.totalCost}</td>
                    <td>${item.addedDate}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching raw materials:', error));
}

function updateQuantity(itemId) {
    const quantity = document.getElementById(`quantity-${itemId}`).value;
    fetch(`/api/manu-raw-materials/update-quantity?itemId=${itemId}&newQuantity=${quantity}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data);
        loadCompletedOrders();
    })
    .catch(error => console.error('Error updating quantity:', error));
}

function openRawEditModal(id, name, categoryId,categoryName, quantity, perUnitCost, cost) {
    // Populate the form fields
    document.getElementById("editRawId").value = id;
    document.getElementById("editRawName").value = name; // Name is non-editable
    document.getElementById("editRawCategory").value = categoryName; // Show the category name (non-editable)
	document.getElementById("editRawCategoryId").value = categoryId;
    document.getElementById("editRawQuantity").value = quantity;
    document.getElementById("editRawUnitCost").value = perUnitCost;
    document.getElementById("editRawCost").value = cost;
    document.getElementById("editRawAddedDate").value = new Date().toISOString().split("T")[0];

    // Show the modal
    new bootstrap.Modal(document.getElementById("editRawModal")).show();
}

function saveRawEdit() {
    const id = document.getElementById("editRawId").value;
    const updatedData = {
        name: document.getElementById("editRawName").value, // Name is non-editable
        quantity: parseInt(document.getElementById("editRawQuantity").value),
        perUnitCost: parseFloat(document.getElementById("editRawUnitCost").value),
        cost: parseFloat(document.getElementById("editRawCost").value),
        categoryId: parseInt(document.getElementById("editRawCategoryId").value) // Use the hidden categoryId
    };

    console.log("Updated data: ", updatedData);

    fetch(`/api/manu/products/update/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedData)
    })
    .then(() => {
        loadCompletedOrders();
        fetchManufacturerInventory(manufacturerId); // Refresh the inventory table
        bootstrap.Modal.getInstance(document.getElementById("editRawModal")).hide();
    })
    .catch(error => console.error('Error updating raw material:', error));
}

function deleteRawMaterial(id) {
    if (!confirm("Are you sure you want to delete this item?")) return;

    fetch(`/api/manu-raw-materials/${id}`, {
        method: "DELETE"
    })
    .then(() => {
        loadCompletedOrders();
    })
    .catch(error => console.error('Error deleting raw material:', error));
}

function fetchCategories() {
    fetch('/api/categories')
        .then(response => response.json())
        .then(categories => {
            const categorySelect = document.getElementById('category');
            categorySelect.innerHTML = '<option value="" disabled selected>Select Category</option>';
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.categoryId;
                option.textContent = category.categoryName;
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching categories:', error));
}

function fetchEditCategories() {
    fetch('/api/categories')
        .then(response => response.json())
        .then(categories => {
            const categorySelect = document.getElementById('editRawCategory');
            categorySelect.innerHTML = '<option value="" disabled selected>Select Category</option>';
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.categoryId;
                option.textContent = category.categoryName;
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching categories:', error));
}

function fetchManufacturerInventory(manufacturerId) {
	console.log("Fetching inventory for manufacturer ID:", manufacturerId);
    fetch(`/api/manu/products?manufacturerId=${manufacturerId}`)
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('manufacturerInventoryBody');
            tableBody.innerHTML = '';
            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.category.categoryName}</td>
                    <td>${item.quantity}</td>
                    <td>${item.cost}</td>
                    <td>${item.perUnitCost}</td>
                    <td>${item.addedDate}</td>
                    <td>
                        <button class="btn btn-warning" onclick="openRawEditModal(${item.id}, '${item.name}', ${item.category.categoryId},'${item.category.categoryName}', ${item.quantity}, ${item.perUnitCost}, ${item.cost}), ${manufacturerId}">Edit</button>
                        <button class="btn btn-danger" onclick="deleteInventoryItem(${item.id})">Delete</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching inventory items:', error));
}

function deleteInventoryItem(itemId) {
    // Show confirmation dialog
    const confirmed = confirm("Are you sure you want to delete this inventory item?");
    
    if (confirmed) {
        // Proceed with deletion only if confirmed
        fetch(`/api/manu/products/${itemId}`, {
            method: 'DELETE'
        })
        .then(() => {
            fetchManufacturerInventory(manufacturerId); // Refresh the table after deletion
        })
        .catch(error => console.error('Error deleting inventory item:', error));
    } else {
        console.log("Deletion canceled by user");
    }
}

/* document.getElementById('addItemForm').addEventListener('submit', function(event) {
    event.preventDefault();
    
    const itemName = document.getElementById('newItemName').value;
    const category = document.getElementById('category').value;
    const quantity = document.getElementById('newItemQuantity').value;
    const totalCost = document.getElementById('newItemCost').value;
    const costPerUnit = document.getElementById('newItemUnitCost').value;
    const addedDate = document.getElementById('addedDate').value;

    fetch('/api/manu/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            name: itemName,
            categoryId: category,
            stockQuantity: quantity,
            cost: totalCost,
            costPerUnit: costPerUnit,
            addedDate: addedDate
        })
    }).then(response => response.json())
      .then(() => {
          $('#addItemModal').modal('hide');
          fetchManufacturerInventory(manufacturerId);
      }).catch(error => console.error('Error adding item:', error));
}); */

function openEditItemModal(itemId) {
    fetch(`/api/manu/products/new/${itemId}`)
        .then(response => response.json())
        .then(item => {
            document.getElementById('editRawName').value = item.name;
            document.getElementById('editRawCategory').value = item.categoryId;
            document.getElementById('editRawQuantity').value = item.stockQuantity;
            document.getElementById('editRawCost').value = item.cost;
            document.getElementById('editRawUnitCost').value = item.costPerUnit;
            document.getElementById('addedDate').value = item.addedDate;

            document.getElementById('addItemForm').onsubmit = function(event) {
                event.preventDefault();
                const updatedItem = {
                    name: document.getElementById('itemName').value,
                    categoryId: document.getElementById('category').value,
                    stockQuantity: document.getElementById('quantity').value,
                    cost: document.getElementById('totalCost').value,
                    costPerUnit: document.getElementById('costPerUnit').value,
                    addedDate: document.getElementById('addedDate').value
                };

                fetch(`/api/manu/products/${itemId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedItem),
                }).then(() => {
                    $('#addItemModal').modal('hide');
                    fetchManufacturerInventory(manufacturerId);
                }).catch(error => console.error('Error editing item:', error));
            };

            $('#addItemModal').modal('show');
        })
        .catch(error => console.error('Error fetching item details:', error));
} 

document.getElementById('newItemCost').addEventListener('input', calculateCostPerUnit);
document.getElementById('newItemQuantity').addEventListener('input', calculateCostPerUnit);

document.getElementById('editRawCost').addEventListener('input', calculateCostPerUnitRaw);
document.getElementById('editRawQuantity').addEventListener('input', calculateCostPerUnitRaw);

function calculateCostPerUnit() {
    const quantity = document.getElementById('newItemQuantity').value;
    const totalCost = document.getElementById('newItemCost').value;
    if (quantity && totalCost) {
        const costPerUnit = totalCost / quantity;
        document.getElementById('newItemUnitCost').value = costPerUnit.toFixed(2);
    }
}

function calculateCostPerUnitRaw() {
    const quantity = document.getElementById('editRawQuantity').value;
    const totalCost = document.getElementById('editRawCost').value;
    if (quantity && totalCost) {
        const costPerUnit = totalCost / quantity;
        document.getElementById('editRawUnitCost').value = costPerUnit.toFixed(2);
    }
}

