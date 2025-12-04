document.addEventListener("DOMContentLoaded", () => {
    loadInventory();
    fetchCategories();
    fetchTypes();

    // Listen for changes to Cost and Quantity fields
    if (document.getElementById("cost") && document.getElementById("quantity")) {
        document.getElementById("cost").addEventListener("input", updatePerUnitCost);
        document.getElementById("quantity").addEventListener("input", updatePerUnitCost);
    }

    if (document.getElementById("editItemCost") && document.getElementById("editItemQuantity")) {
        document.getElementById("editItemCost").addEventListener("input", updateEditPerUnitCost);
        document.getElementById("editItemQuantity").addEventListener("input", updateEditPerUnitCost);
    }
});

/**
 * ✅ Load Inventory Items and Display in the Table
 */
function loadInventory() {
    fetch("/api/inventory")
        .then(response => response.json())
        .then(data => {
            console.log("Fetched inventory:", data);
            let tableBody = document.getElementById("inventoryTableBody");
            if (!tableBody) {
                console.error("Inventory table body not found!");
                return;
            }
            tableBody.innerHTML = "";
            data.forEach(item => {
                let row = `<tr>
                    <td>${item.name}</td>
                    <td>${item.category ? item.category.categoryName : 'No Category'}</td> 
                    <td>${item.date}</td>
                    <td>${item.quantity}</td>
                    <td>${item.itemType ? item.itemType.name : 'No Type'}</td>
                    <td>${item.cost}</td>
                    <td>${item.perUnitCost}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="editItem(${item.id})">Edit</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteItem(${item.id})">Delete</button>
                    </td>
                </tr>`;
                tableBody.innerHTML += row;
            });
        })
        .catch(error => console.error("Error fetching inventory:", error));
}
/**
 * ✅ Fetch and Populate Categories in Dropdowns
 */
function fetchCategories() {
    return fetch("/api/categories")
        .then(response => response.json())
        .then(data => {
            console.log("Fetched categories:", data);  // ✅ Debug log

            if (!Array.isArray(data)) {
                console.error("Expected an array but got:", data);
                return;
            }

            let categorySelect = document.getElementById("category");
            let editCategorySelect = document.getElementById("editCategory");

            let optionsHtml = '<option value="">Select Category</option>' +
                data.map(cat => `<option value="${cat.categoryId}">${cat.categoryName}</option>`).join('');

            if (categorySelect) categorySelect.innerHTML = optionsHtml;
            if (editCategorySelect) editCategorySelect.innerHTML = optionsHtml;
        })
        .catch(error => console.error("Error fetching categories:", error));
}



/**
 * ✅ Fetch and Populate Item Types in Dropdowns
 */
function fetchTypes() {
    return fetch("/api/item-types")
        .then(response => response.json())
        .then(data => {
            console.log("Fetched item types:", data);  // ✅ Debug log

            if (!Array.isArray(data)) {
                console.error("Expected an array but got:", data);
                return;
            }

            let itemTypeSelect = document.getElementById("itemType");
            let editItemTypeSelect = document.getElementById("editItemType");

            let optionsHtml = '<option value="">Select Item Type</option>' +
                data.map(type => `<option value="${type.id}">${type.name}</option>`).join('');

            if (itemTypeSelect) itemTypeSelect.innerHTML = optionsHtml;
            if (editItemTypeSelect) editItemTypeSelect.innerHTML = optionsHtml;
        })
        .catch(error => console.error("Error fetching item types:", error));
}



/**
 * ✅ Dynamically Update Per Unit Cost when Total Cost and Quantity are entered
 */
document.addEventListener("DOMContentLoaded", () => {
    // Add event listeners for dynamic per unit cost calculation
    const costInput = document.getElementById("cost");
    const quantityInput = document.getElementById("quantity");
    const perUnitCostInput = document.getElementById("perUnitCost");
    if (costInput && quantityInput) {
        costInput.addEventListener("input", updatePerUnitCost);
        quantityInput.addEventListener("input", updatePerUnitCost);
    }

    const editCostInput = document.getElementById("editItemCost");
    const editQuantityInput = document.getElementById("editItemQuantity");
    const editPerUnitCostInput = document.getElementById("editPerUnitCost");
    if (editCostInput && editQuantityInput) {
        editCostInput.addEventListener("input", updateEditPerUnitCost);
        editQuantityInput.addEventListener("input", updateEditPerUnitCost);
    }
});

function updatePerUnitCost() {
    let cost = parseFloat(document.getElementById("cost").value);
    let quantity = parseFloat(document.getElementById("quantity").value);
    document.getElementById("perUnitCost").value = (!isNaN(cost) && !isNaN(quantity) && quantity > 0) ? (cost / quantity).toFixed(2) : "";
}

function updateEditPerUnitCost() {
    let cost = parseFloat(document.getElementById("editItemCost").value);
    let quantity = parseFloat(document.getElementById("editItemQuantity").value);
    document.getElementById("editPerUnitCost").value = (!isNaN(cost) && !isNaN(quantity) && quantity > 0) ? (cost / quantity).toFixed(2) : "";
}

function editItem(id) {
    fetch(`/api/inventory/${id}`)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(item => {
            console.log("Editing item:", item);

            document.getElementById("editItemId").value = item.id;
            document.getElementById("editItemName").value = item.name;
            document.getElementById("editItemDate").value = item.date;
            document.getElementById("editItemQuantity").value = item.quantity;
            document.getElementById("editItemCost").value = item.cost;
            document.getElementById("editPerUnitCost").value = item.perUnitCost;
            document.getElementById("editItemType").value = item.itemType;
            document.getElementById("editCategory").value = item.category;

            let editModal = new bootstrap.Modal(document.getElementById("editModal"));
            editModal.show();
        })
        .catch(error => console.error("Error fetching item for edit:", error));
}

/**
 * ✅ Send Update Request for Edited Item
 */
function updateItem() {
    let itemId = document.getElementById("editItemId").value;
    let updatedItem = {
        id: itemId,
        name: document.getElementById("editItemName").value,
        date: document.getElementById("editItemDate").value,
        quantity: parseInt(document.getElementById("editItemQuantity").value),
        itemType: { id: parseInt(document.getElementById("editItemType").value) },
        category: { categoryId: parseInt(document.getElementById("editCategory").value) },
        cost: parseFloat(document.getElementById("editItemCost").value),
        perUnitCost: parseFloat(document.getElementById("editPerUnitCost").value)
    };

    fetch(`/api/inventory/${itemId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedItem)
    })
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.text();
    })
    .then(() => {
        alert("Item updated successfully!");
        location.reload();
    })
    .catch(error => console.error("Error updating item:", error));
}
/**
 * ✅ Delete an Inventory Item
 */
function deleteItem(id) {
    if (!confirm("Are you sure you want to delete this item?")) return;

    fetch(`/api/inventory/${id}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.text();
        })
        .then(() => {
            alert("Item deleted successfully!");
            location.reload();
        })
        .catch(error => console.error("Error deleting item:", error));
}
