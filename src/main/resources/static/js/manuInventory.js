// Set today's date as default for "Added Date" field in the Add Item modal
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0];
    document.getElementById("addedDate").value = today;
});

// Function to fetch Delivered Items
function fetchDeliveredItems(manufacturerId) {
    fetch(`/api/manu-raw-materials?manufacturerId=${manufacturerId}`)
        .then(response => response.json())
        .then(data => {
            let tableBody = document.querySelector('#deliveredItemsBody');
            tableBody.innerHTML = '';
            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.type}</td>
                    <td>${item.category.categoryName}</td>
                    <td>
                        <input type="number" value="${item.quantity}" min="0" max="${item.originalQuantity}" id="quantity-${item.rawMaterialMid}" onchange="updateQuantity(${item.rawMaterialMid}, ${manufacturerId})">
                    </td>
                    <td>${item.cost}</td>
                    <td>${item.addedDate}</td>
                    <td>
                        <button class="btn btn-danger" onclick="deleteDeliveredItem(${item.rawMaterialMid}, ${manufacturerId})">Delete</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching delivered items:', error));
}

// Function to update the quantity in the delivered items table
function updateQuantity(itemId, manufacturerId) {
    const quantity = document.getElementById(`quantity-${itemId}`).value;
    fetch(`/api/manu-raw-materials/update-quantity?itemId=${itemId}&newQuantity=${quantity}&manufacturerId=${manufacturerId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        alert(data); // Show success or error message
    })
    .catch(error => console.error('Error updating quantity:', error));
}

// Function to delete an item from the delivered items table
function deleteDeliveredItem(itemId, manufacturerId) {
    fetch(`/api/manu-raw-materials/${itemId}?manufacturerId=${manufacturerId}`, {
        method: 'DELETE'
    })
    .then(() => {
        fetchDeliveredItems(manufacturerId); // Refresh the table after deletion
    })
    .catch(error => console.error('Error deleting item:', error));
}

// Function to fetch available categories from the backend
function fetchCategories() {
    fetch('/api/categories') // Endpoint to fetch categories
        .then(response => response.json())
        .then(categories => {
            const categorySelect = document.getElementById('category');
            categorySelect.innerHTML = ''; // Clear any existing options

            // Add a default "Select Category" option
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = 'Select Category';
            categorySelect.appendChild(defaultOption);

            // Add categories from the backend
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching categories:', error));
}

// Function to fetch Inventory Items (Manufacturer Inventory)
function fetchManufacturerInventory() {
    fetch('/api/inventory-items')
        .then(response => response.json())
        .then(data => {
            let tableBody = document.querySelector('#manufacturerInventoryBody');
            tableBody.innerHTML = '';
            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name}</td>
                    <td>${item.category}</td>
                    <td>${item.quantity}</td>
                    <td>${item.cost}</td>
                    <td>${item.costPerUnit}</td>
                    <td>${item.addedDate}</td>
                    <td>
                        <button class="btn btn-warning" onclick="openEditItemModal(${item.id})">Edit</button>
                        <button class="btn btn-danger" onclick="deleteInventoryItem(${item.id})">Delete</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching inventory items:', error));
}

// Function to open the Add Item Modal (for adding new items)
function openAddItemModal() {
    // Reset the modal fields
    document.getElementById('addItemForm').reset();
    document.getElementById('costPerUnit').value = '';

    // Fetch categories to populate the select box
    fetchCategories();
}

// Function to handle the "Add New Item" form submission
document.getElementById('addItemForm').addEventListener('submit', function(event) {
    event.preventDefault();
    
    const itemName = document.getElementById('itemName').value;
    const category = document.getElementById('category').value;
    const quantity = document.getElementById('quantity').value;
    const totalCost = document.getElementById('totalCost').value;
    const costPerUnit = document.getElementById('costPerUnit').value;
    const addedDate = document.getElementById('addedDate').value;

    // Send the new item data to the backend
    fetch('/api/inventory-items', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            name: itemName,
            category: category,
            quantity: quantity,
            cost: totalCost,
            costPerUnit: costPerUnit,
            addedDate: addedDate,
        })
    }).then(response => response.json())
      .then(data => {
          // Close the modal and refresh the table
          $('#addItemModal').modal('hide');
          fetchManufacturerInventory();
      }).catch(error => console.error('Error adding item:', error));
});

// Function to automatically calculate cost per unit based on total cost and quantity
document.getElementById('totalCost').addEventListener('input', calculateCostPerUnit);
document.getElementById('quantity').addEventListener('input', calculateCostPerUnit);

function calculateCostPerUnit() {
    const quantity = document.getElementById('quantity').value;
    const totalCost = document.getElementById('totalCost').value;
    if (quantity && totalCost) {
        const costPerUnit = totalCost / quantity;
        document.getElementById('costPerUnit').value = costPerUnit.toFixed(2);
    }
}

// Function to delete an item from the inventory items table
function deleteInventoryItem(itemId) {
    fetch(`/api/inventory-items/${itemId}`, {
        method: 'DELETE'
    })
    .then(() => {
        fetchManufacturerInventory(); // Refresh the table after deletion
    })
    .catch(error => console.error('Error deleting inventory item:', error));
}

// Function to open the Edit Item Modal (for editing an existing item)
function openEditItemModal(itemId) {
    fetch(`/api/inventory-items/${itemId}`)
        .then(response => response.json())
        .then(item => {
            document.getElementById('itemName').value = item.name;
            document.getElementById('category').value = item.categoryId; // Use categoryId for editing
            document.getElementById('quantity').value = item.quantity;
            document.getElementById('totalCost').value = item.cost;
            document.getElementById('costPerUnit').value = item.costPerUnit;
            document.getElementById('addedDate').value = item.addedDate;

            // Update the modal action to edit the item
            document.getElementById('addItemForm').onsubmit = function(event) {
                event.preventDefault();

                const updatedItem = {
                    id: itemId,
                    name: document.getElementById('itemName').value,
                    category: document.getElementById('category').value,
                    quantity: document.getElementById('quantity').value,
                    cost: document.getElementById('totalCost').value,
                    costPerUnit: document.getElementById('costPerUnit').value,
                    addedDate: document.getElementById('addedDate').value
                };

                // Send the updated item data to the backend
                fetch(`/api/inventory-items/${itemId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(updatedItem),
                })
                .then(response => response.json())
                .then(() => {
                    $('#addItemModal').modal('hide');
                    fetchManufacturerInventory(); // Refresh the inventory items table
                }).catch(error => console.error('Error editing item:', error));
            };

            $('#addItemModal').modal('show');
        })
        .catch(error => console.error('Error fetching item details:', error));
}

// Initialize by fetching all data
fetchDeliveredItems();
fetchManufacturerInventory();
