/* document.addEventListener("DOMContentLoaded", () => {
    // Handle image preview for supplier raw material image upload
    const imageInput = document.getElementById("imageFile");
    const imagePreviewAdd = document.createElement("div");
   // const imagePreviewEdit = document.createElement("div");
    
    imagePreviewAdd.className = "image-box mb-3";
    //imagePreviewEdit.className = "image-box mb-3";

    const previewContainerAdd = document.querySelector("#supplierRawMaterialForm .mb-3:nth-child(4)");
   // const previewContainerEdit = document.querySelector("#editSupplierRawMaterialForm .mb-3:nth-child(4)");

    // Initialize image previews
    if (previewContainerAdd) {
        previewContainerAdd.insertAdjacentElement("afterend", imagePreviewAdd);
        imagePreviewAdd.innerHTML = "";
    }
   /* if (previewContainerEdit) {
        previewContainerEdit.insertAdjacentElement("afterend", imagePreviewEdit);
        imagePreviewEdit.innerHTML = "";
    }
*/

/* window.openEditModal = function (productMid) {
    console.log("Fetching product with MID:", productMid);

    fetch(`http://localhost:8080/manuproductmanagement/edit/${productMid}`, {
        method: "GET",
        headers: {
            "Accept": "application/json" // Ensure the server knows we expect JSON
        }
    })
        .then(response => {
            console.log("Response status:", response.status);
            console.log("Response headers:", [...response.headers.entries()]);

            // Check if the response is OK (status 200-299)
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            // Log the raw text of the response before parsing
            return response.text().then(text => {
                console.log("Raw response text:", text);
                try {
                    const jsonData = JSON.parse(text);
                    return jsonData;
                } catch (e) {
                    throw new Error(`Failed to parse JSON: ${e.message}\nRaw text: ${text}`);
                }
            });
        })
        .then(product => {
            if (product) {
                console.log("Parsed product data:", product);
                document.getElementById("editProductId").value = product.productMid;
                document.getElementById("editName").value = product.name;
                document.getElementById("editDescription").value = product.description;
                document.getElementById("editPrice").value = product.price;

                // Populate category dropdown
                const categorySelect = document.getElementById("editCategory");
                categorySelect.innerHTML = ""; // Clear existing options

                fetch("/api/categories")
                    .then(response => response.json())
                    .then(categories => {
                        categories.forEach(category => {
                            const option = document.createElement("option");
                            option.value = category.categoryId;
                            option.textContent = category.categoryName;
                            if (product.category && product.category.categoryId === category.categoryId) {
                                option.selected = true;
                            }
                            categorySelect.appendChild(option);
                        });
                    })
                    .catch(error => console.error("Error fetching categories:", error));

                // Show modal using Bootstrap
                const editModal = new bootstrap.Modal(document.getElementById("editProductModal"));
                editModal.show();
            } else {
                console.warn("No product data received.");
            }
        })
        .catch(error => console.error("Error fetching product:", error));
};
    // Function to close the edit modal
    window.closeEditModal = function () {
        document.getElementById("editProductModal").style.display = "none";
    };

    // Handle form submission for editing product
    document.getElementById("editProductForm").addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData();
        formData.append("productMid", document.getElementById("editProductId").value);
        formData.append("name", document.getElementById("editName").value);
        formData.append("description", document.getElementById("editDescription").value);
        formData.append("price", document.getElementById("editPrice").value);
        formData.append("category", document.getElementById("editCategory").value);

        const imageInput = document.getElementById("editImageFile");
        if (imageInput.files.length > 0) {
            formData.append("imageFile", imageInput.files[0]);
        }

		fetch("/manuproductmanagement/update", {
		    method: "POST",
		    body: formData
		})
		.then(response => response.json()) // Parse JSON response
		.then(data => {
		    if (data.error) {
		        alert("Error: " + data.error);
		    } else {
		        alert(data.message || "Product updated successfully!");
		        closeEditModal();
		        location.reload();
		    }
		})
		.catch(error => console.error("Error updating product:", error));
    });
	
    if (imageInput) {
        imageInput.addEventListener("change", function(e) {
            const file = e.target.files[0];
            const isEditForm = document.getElementById("editSupplierRawMaterialForm")?.contains(imageInput);
            const previewTarget = isEditForm ? imagePreviewEdit : imagePreviewAdd;

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewTarget.innerHTML = `<img src="${e.target.result}" alt="Preview" />`;
                    const removeButton = document.createElement("span");
                    removeButton.className = "remove-image";
                    removeButton.textContent = "×";
                    removeButton.addEventListener("click", () => {
                        imageInput.value = ""; // Clear the file input
                        previewTarget.innerHTML = isEditForm ? "Click to upload image (optional for edit)" : "Click to upload image";
                    });
                    previewTarget.appendChild(removeButton);
                };
                reader.readAsDataURL(file);
            } else {
                previewTarget.innerHTML = isEditForm ? "Click to upload image (optional for edit)" : "Click to upload image";
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
                    preview.innerHTML = modal.id === 'addSupplierRawMaterialModal' ? 
                        "Click to upload image" : "Click to upload image (optional for edit)";
                }
                if (imageInput) imageInput.value = ""; // Clear file input on modal close
            }
        });
    });

    // Function to update category based on selected inventory item (only for add form)
	function updateCategoryFromInventory() {
	    const inventoryItemName = document.getElementById("inventoryItemName").value;
	    const isEditForm = document.getElementById("editSupplierRawMaterialForm") !== null;

	    // Only execute for the add form, not the edit form
	    if (!isEditForm && inventoryItemName) {
	        fetch(`/api/inventory/name/${encodeURIComponent(inventoryItemName)}`)
	            .then(response => {
	                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
	                return response.json();
	            })
	            .then(item => {
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

    // Attach the function to the inventory item select (only for add form)
    const inventorySelect = document.getElementById("inventoryItemName");
    if (inventorySelect) {
        inventorySelect.addEventListener("change", updateCategoryFromInventory);
    }
	
	// EDIT FORM
	document.querySelectorAll(".edit-btn").forEach(button => {
	        button.addEventListener("click", function () {
	            let productId = this.getAttribute("data-id");

	            // Fetch product details
	            fetch(`/manuproductmanagement/manuproduct/${productId}`)
	                .then(response => response.json())
	                .then(product => {
	                    document.getElementById("editProductId").value = product.productMid;
	                    document.getElementById("editName").value = product.name;
	                    document.getElementById("editDescription").value = product.description;
	                    document.getElementById("editPrice").value = product.price;
	                    document.getElementById("editCategory").value = product.category.categoryId;

	                    // If product has an image, display it
	                    if (product.image) {
	                        document.getElementById("editImagePreview").src = `/uploads/${product.image}`;
	                        document.getElementById("editImagePreview").style.display = "block";
	                    } else {
	                        document.getElementById("editImagePreview").style.display = "none";
	                    }

	                    // Show modal
	                    let editModal = new bootstrap.Modal(document.getElementById("editProductModal"));
	                    editModal.show();
	                })
	                .catch(error => console.error("Error fetching product:", error));
	        });
	    });

	    // Handle form submission
	    document.getElementById("editProductForm").addEventListener("submit", function (event) {
	        event.preventDefault();

	        let formData = new FormData(this);

	        fetch("/manuproductmanagement/manuproduct/update", {
	            method: "POST",
	            body: formData
	        })
	        .then(response => {
	            if (response.ok) {
	                location.reload(); // Refresh the page after update
	            } else {
	                alert("Error updating product");
	            }
	        })
	        .catch(error => console.error("Error:", error));
	    }); 
		
		
	document.addEventListener("DOMContentLoaded", () => {
	    const imageInput = document.getElementById("imageFile");
	    const imagePreviewEdit = document.querySelector("#editSupplierRawMaterialForm .image-box");

	    if (imageInput) {
	        imageInput.addEventListener("change", function(e) {
	            const file = e.target.files[0];
	            if (file) {
	                const reader = new FileReader();
	                reader.onload = function(e) {
	                    imagePreviewEdit.innerHTML = `<img src="${e.target.result}" alt="Preview" />`;
	                    const removeButton = document.createElement("span");
	                    removeButton.className = "remove-image";
	                    removeButton.textContent = "×";
	                    removeButton.addEventListener("click", () => {
	                        imageInput.value = ""; 
	                        imagePreviewEdit.innerHTML = `<img src="${imagePreviewEdit.dataset.currentImage}" alt="Current Image" />`;
	                    });
	                    imagePreviewEdit.appendChild(removeButton);
	                };
	                reader.readAsDataURL(file);
	            } else {
	                imagePreviewEdit.innerHTML = `<img src="${imagePreviewEdit.dataset.currentImage}" alt="Current Image" />`;
	            }
	        });
	    }
		function refreshInventoryItems(supplierId) {
		        fetch(`/api/supplier-raw-materials/available-inventory?supplierId=${supplierId}`)
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

		    // Call refresh after saving a new raw material
		    const form = document.getElementById("supplierRawMaterialForm");
		    if (form) {
		        form.addEventListener("submit", function(e) {
		            // Assuming supplierId is available in a hidden field or session
		            const supplierId = document.querySelector('input[name="supplierRid"]').value || 
		            setTimeout(() => refreshInventoryItems(supplierId), 1000); 
		        });
		    }

	

	    document.querySelectorAll("#editSupplierRawMaterialModal").forEach(modal => {
	        modal.addEventListener("shown.bs.modal", function() {
	            const currentImage = this.querySelector(".image-box img");
	            if (currentImage) {
	                imagePreviewEdit.dataset.currentImage = currentImage.src;
	            }
	        });
	    });
	});

}); */















// ==================== Global Functions ====================

// Function to open the edit modal
window.openEditModal = function (productMid) {
    console.log("Fetching product with MID:", productMid);

    fetch(`http://localhost:8080/manuproductmanagement/edit/${productMid}`, {
        method: "GET",
        headers: {
            "Accept": "application/json" // Ensure the server knows we expect JSON
        }
    })
        .then(response => {
            console.log("Response status:", response.status);
            console.log("Response headers:", [...response.headers.entries()]);

            // Check if the response is OK (status 200-299)
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            // Log the raw text of the response before parsing
            return response.text().then(text => {
                console.log("Raw response text:", text);
                try {
                    const jsonData = JSON.parse(text);
                    return jsonData;
                } catch (e) {
                    throw new Error(`Failed to parse JSON: ${e.message}\nRaw text: ${text}`);
                }
            });
        })
        .then(product => {
            if (product) {
                console.log("Parsed product data:", product);
                document.getElementById("editProductId").value = product.productMid;
                document.getElementById("editName").value = product.name;
                document.getElementById("editDescription").value = product.description;
                document.getElementById("editPrice").value = product.price;

                // Populate category dropdown
                const categorySelect = document.getElementById("editCategory");
                categorySelect.innerHTML = ""; // Clear existing options

                fetch("/api/categories")
                    .then(response => response.json())
                    .then(categories => {
                        categories.forEach(category => {
                            const option = document.createElement("option");
                            option.value = category.categoryId;
                            option.textContent = category.categoryName;
                            if (product.category && product.category.categoryId === category.categoryId) {
                                option.selected = true;
                            }
                            categorySelect.appendChild(option);
                        });
                    })
                    .catch(error => console.error("Error fetching categories:", error));

                // Show modal using Bootstrap
                const editModal = new bootstrap.Modal(document.getElementById("editProductModal"));
                editModal.show();
            } else {
                console.warn("No product data received.");
            }
        })
        .catch(error => console.error("Error fetching product:", error));
};

// Function to close the edit modal
window.closeEditModal = function () {
    document.getElementById("editProductModal").style.display = "none";
};

// ==================== DOMContentLoaded ====================

document.addEventListener("DOMContentLoaded", () => {
    // Handle image preview for supplier raw material image upload
    const imageInput = document.getElementById("imageFile");
    const imagePreviewAdd = document.createElement("div");
    imagePreviewAdd.className = "image-box mb-3";

    const previewContainerAdd = document.querySelector("#supplierRawMaterialForm .mb-3:nth-child(4)");

    // Initialize image previews
    if (previewContainerAdd) {
        previewContainerAdd.insertAdjacentElement("afterend", imagePreviewAdd);
        imagePreviewAdd.innerHTML = "";
    }

    if (imageInput) {
        imageInput.addEventListener("change", function (e) {
            const file = e.target.files[0];
            const isEditForm = document.getElementById("editProductForm")?.contains(imageInput);
            const previewTarget = isEditForm ? imagePreviewEdit : imagePreviewAdd;

            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewTarget.innerHTML = `<img src="${e.target.result}" alt="Preview" />`;
                    const removeButton = document.createElement("span");
                    removeButton.className = "remove-image";
                    removeButton.textContent = "×";
                    removeButton.addEventListener("click", () => {
                        imageInput.value = ""; // Clear the file input
                        previewTarget.innerHTML = isEditForm ? "Click to upload image (optional for edit)" : "Click to upload image";
                    });
                    previewTarget.appendChild(removeButton);
                };
                reader.readAsDataURL(file);
            } else {
                previewTarget.innerHTML = isEditForm ? "Click to upload image (optional for edit)" : "Click to upload image";
            }
        });
    }

    // Handle search functionality
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener("input", function (e) {
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
        modal.addEventListener('hidden.bs.modal', function () {
            const form = modal.querySelector('form');
            if (form) {
                form.reset();
                const preview = modal.querySelector('.image-box');
                if (preview) {
                    preview.innerHTML = modal.id === 'addSupplierRawMaterialModal' ?
                        "Click to upload image" : "Click to upload image (optional for edit)";
                }
                if (imageInput) imageInput.value = ""; // Clear file input on modal close
            }
        });
    });

    // Function to update category based on selected inventory item (only for add form)
    function updateCategoryFromInventory() {
        const inventoryItemName = document.getElementById("inventoryItemName").value;
        const isEditForm = document.getElementById("editProductForm") !== null;

        // Only execute for the add form, not the edit form
        if (!isEditForm && inventoryItemName) {
            fetch(`/api/manu/inventory/name/${encodeURIComponent(inventoryItemName)}`)
                .then(response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    return response.json();
                })
                .then(item => {
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

    // Attach the function to the inventory item select (only for add form)
    const inventorySelect = document.getElementById("inventoryItemName");
    if (inventorySelect) {
        inventorySelect.addEventListener("change", updateCategoryFromInventory);
    }

    // Handle form submission for editing product
    document.getElementById("editProductForm").addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData();
        formData.append("productMid", document.getElementById("editProductId").value);
        formData.append("name", document.getElementById("editName").value);
        formData.append("description", document.getElementById("editDescription").value);
        formData.append("price", document.getElementById("editPrice").value);
        formData.append("category", document.getElementById("editCategory").value);

        const imageInput = document.getElementById("editImageFile");
        if (imageInput.files.length > 0) {
            formData.append("imageFile", imageInput.files[0]);
        }

        fetch("/manuproductmanagement/update", {
            method: "POST",
            body: formData
        })
            .then(response => response.json()) // Parse JSON response
            .then(data => {
                if (data.error) {
                    alert("Error: " + data.error);
                } else {
                    alert(data.message || "Product updated successfully!");
                    closeEditModal();
                    location.reload();
                }
            })
            .catch(error => console.error("Error updating product:", error));
    });
});