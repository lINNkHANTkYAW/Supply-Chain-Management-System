// Fetch data for both tables when page loads
window.onload = () => {
    fetchDeliveredItems();
    fetchInventoryItems();
};

// Fetch delivered items from the backend
function fetchDeliveredItems() {
	console.log("DistributorId", distributorId);
    fetch(`/api/distri/delivered-items?distributorId=${distributorId}`)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok: ' + response.status);
            return response.json();
        })
        .then(data => {
			console.log("Fetched data:", data);
            const tableBody = document.querySelector('#deliveredItemsTable tbody');
            tableBody.innerHTML = '';  // Clear the table before adding new rows
            data.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name || 'N/A'}</td>
                    <td>${item.quantity || 0}</td>
                    <td>${item.cost || 0.0}</td>
                    <td>${item.addedDate || ''}</td>
                    
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching delivered items:', error));
}

// Fetch inventory items from the backend
function fetchInventoryItems() {
    fetch(`/api/distri/inventory-items?distributorId=${distributorId}`)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok: ' + response.status);
            return response.json();
        })
        .then(data => {
			
            const tableBody = document.querySelector('#savedItemsTable tbody');
            tableBody.innerHTML = '';  // Clear the table before adding new rows
            data.forEach(item => {
				console.log("Item ID", item.itemId);
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.name || 'N/A'}</td>
                    <td>${item.quantity || 0}</td>
                    <td>${item.cost || 0.0}</td>
                    <td>${item.costPerUnit || 0.0}</td>
                    <td>${item.addedDate || ''}</td>
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editItem(${item.itemId})">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteItem(${item.itemId})">Delete</button>
                    </td>
                `;
                tableBody.appendChild(row);
				console.log("Item id in fetching inventory: ", item.itemId);
            });
        })
        .catch(error => console.error('Error fetching inventory items:', error));
}

// Edit an item
function editItem(itemId) {
	console.log("Editing item with id: ", itemId);
    fetch(`/api/distri/item/${itemId}`)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok: ' + response.status);
            return response.json();
        })
        .then(item => {
			console.log('Fetched item for edit:', item.itemId);
            // Populate the form with the item details, with fallback values
            document.getElementById('itemId').value = item.itemId || '';
            document.getElementById('itemName').value = item.name || '';
            document.getElementById('itemQuantity').value = item.quantity || 0;
            document.getElementById('itemCost').value = item.cost || 0.0;
            document.getElementById('itemCostPerUnit').value = item.costPerUnit || 0.0;
            document.getElementById('itemAddedDate').value = item.addedDate ? item.addedDate.split('T')[0] : '';
			document.getElementById('category').value = item.category?.categoryId || '';
            const modal = new bootstrap.Modal(document.getElementById('addItemModal'));
            modal.show();
        })
        .catch(error => console.error('Error fetching item details:', error));
}

// Save or update item
document.addEventListener('DOMContentLoaded', () => {
    const itemForm = document.getElementById('itemForm');
    if (itemForm) {
        itemForm.addEventListener('submit', function (event) {
            event.preventDefault(); // Prevent default form submission

            // Debug: Log form data to check if the event is firing
            console.log('Form submitted. Item ID:', document.getElementById('itemId').value);

            const itemId = document.getElementById('itemId').value;
            const item = {
				itemId: itemId ? parseInt(itemId) : null,
                name: document.getElementById('itemName').value.trim(),
                quantity: parseInt(document.getElementById('itemQuantity').value, 10) || 0,
                cost: parseFloat(document.getElementById('itemCost').value) || 0.0,
                costPerUnit: parseFloat(document.getElementById('itemCostPerUnit').value) || 0.0,
                addedDate: document.getElementById('itemAddedDate').value,
				distributorId: distributorId,
				category: { categoryId: parseInt(document.getElementById('category').value) }
            };

            console.log('Sending data:', item);

			let url = `/api/distri/inventory-items?distributorId=${distributorId}`;
			            let method = 'POST';

			            if (itemId) {
			                url = `/api/distri/inventory-items/${itemId}?distributorId=${distributorId}`;
			                method = 'PUT';
			            }

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(item)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Item saved successfully:', data);
                    const modal = bootstrap.Modal.getInstance(document.getElementById('addItemModal'));
                    if (modal) {
                        modal.hide();
                    }
                    // Reload both tables
                    fetchDeliveredItems();
                    fetchInventoryItems();
                })
                .catch(error => console.error('Error saving item:', error));
        });

        // Ensure the "Save Item" button is not disabled by default
        const saveButton = itemForm.querySelector('button[type="submit"]');
        if (saveButton && saveButton.disabled) {
            saveButton.disabled = false;
            console.log('Save Item button re-enabled.');
        }
    } else {
        console.error('Item form not found in the document.');
    }
});

// Delete an item
function deleteItem(itemId) {
	console.log("Item ID in delete item: ", itemId);
    if (confirm('Are you sure you want to delete this item?')) {
        fetch(`/api/distri/inventory-items/${itemId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    fetchDeliveredItems();
                    fetchInventoryItems();
                } else {
                    console.error('Error deleting item: Status ' + response.status);
                }
            })
            .catch(error => console.error('Error deleting item:', error));
    }
}