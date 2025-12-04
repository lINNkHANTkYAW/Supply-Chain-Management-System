document.addEventListener("DOMContentLoaded", () => {
    // Handle image preview for supplier raw material image upload
    const imageInput = document.getElementById("imageFile");
    const imagePreviewAdd = document.createElement("div");
    imagePreviewAdd.className = "image-box mb-3";

    const previewContainerAdd = document.querySelector("#supplierRawMaterialForm .mb-3:nth-child(4)");
    if (previewContainerAdd) {
        previewContainerAdd.insertAdjacentElement("afterend", imagePreviewAdd);
        imagePreviewAdd.innerHTML = "";
    }

    if (imageInput) {
        imageInput.addEventListener("change", function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    imagePreviewAdd.innerHTML = `<img src="${e.target.result}" alt="Preview" />`;
                    const removeButton = document.createElement("span");
                    removeButton.className = "remove-image";
                    removeButton.textContent = "×";
                    removeButton.addEventListener("click", () => {
                        imageInput.value = "";
                        imagePreviewAdd.innerHTML = "";
                    });
                    imagePreviewAdd.appendChild(removeButton);
                };
                reader.readAsDataURL(file);
            } else {
                imagePreviewAdd.innerHTML = "e";
            }
        });
    }

    // Handle search functionality
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener("input", function(e) {
            const searchTerm = e.target.value.toLowerCase().trim();
            const rawMaterialCards = document.querySelectorAll("#supplierRawMaterialsList .card, #supplierRawMaterialsList .col-md-4");
            rawMaterialCards.forEach(card => {
                const nameElement = card.querySelector(".card-title");
                if (nameElement) {
                    const name = nameElement.textContent.toLowerCase().trim();
                    card.style.display = name.includes(searchTerm) ? "block" : "none";
                } else {
                    card.style.display = "none";
                }
            });
        });
    }

    // Handle modal close to reset forms
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.addEventListener('hidden.bs.modal', function() {
            const form = modal.querySelector('form');
            if (form) {
                form.reset();
                const preview = modal.querySelector('.image-box');
                if (preview) {
                    preview.innerHTML = "";
                }
                if (imageInput) imageInput.value = ""; // Clear file input on modal close
            }
        });
    });

    // Function to update category based on selected inventory item (only for add form)
    function updateCategoryFromInventory() {
        const inventoryItemName = document.getElementById("inventoryItemName").value;
        const isEditForm = document.getElementById("editSupplierRawMaterialForm") !== null;

        if (!isEditForm && inventoryItemName) {
            // Fetch category from inventory item (implement backend endpoint if needed)
            fetch(`/supplier-raw-materials/available-inventory?supplierId=${getSupplierId()}`)
                .then(response => response.json())
                .then(items => {
                    const item = items.find(i => i.name === inventoryItemName);
                    if (item && item.category && item.category.categoryId) {
                        document.getElementById("category").value = item.category.categoryId;
                    } else {
                        document.getElementById("category").value = "";
                        console.warn("No category found for inventory item:", inventoryItemName);
                    }
                })
                .catch(error => console.error("Error fetching inventory item category:", error));
        }
    }

    // Helper function to get supplierId (assuming it's in a hidden field or session)
    function getSupplierId() {
        return document.querySelector('input[name="supplierRid"]')?.value || null;
    }

    // Attach the function to the inventory item select (only for add form)
    const inventorySelect = document.getElementById("inventoryItemName");
    if (inventorySelect) {
        inventorySelect.addEventListener("change", updateCategoryFromInventory);
    }

    // Refresh inventory items after saving
    const form = document.getElementById("supplierRawMaterialForm");
    if (form) {
        form.addEventListener("submit", function(e) {
            const supplierId = getSupplierId();
            if (supplierId) {
                setTimeout(() => refreshInventoryItems(supplierId), 1000); // Delay to allow server redirect
            }
        });
    }

    function refreshInventoryItems(supplierId) {
        fetch(`/supplier-raw-materials/available-inventory?supplierId=${supplierId}`)
            .then(response => {
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                return response.json();
            })
            .then(availableItems => {
                const select = document.getElementById("inventoryItemName");
                if (select) {
                    select.innerHTML = '<option value="">Select Inventory Item Name</option>';
                    availableItems.forEach(item => {
                        const option = document.createElement("option");
                        option.value = item.name;
                        option.text = item.name;
                        select.appendChild(option);
                    });
                }
            })
            .catch(error => console.error("Error refreshing inventory items:", error));
    }
});