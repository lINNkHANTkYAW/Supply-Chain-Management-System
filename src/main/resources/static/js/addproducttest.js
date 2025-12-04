document.addEventListener("DOMContentLoaded", () => {
    const addItemBtn = document.getElementById("add-item-btn");
    const modal = document.getElementById("add-item-modal");
    const form = document.getElementById("add-item-form");
    const marketplace = document.getElementById("marketplace");
    const imageUpload = document.getElementById("image-upload");
    const addImageBox = document.getElementById("add-image-box");
    const searchInput = document.getElementById("search-input");
    const categoryFilter = document.getElementById("category-filter");
    const dateFilter = document.getElementById("date-filter");

    let images = [];
    let editCard = null; // Store the card being edited

    // Open modal for adding a new item
    addItemBtn.addEventListener("click", () => {
        modal.style.display = "flex";
        editCard = null; // Reset editCard to null
    });

    // Close modal on outside click
    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    // Handle image uploads
    addImageBox.addEventListener("click", () => {
        if (images.length >= 4) return alert("Maximum of 4 images allowed.");

        const input = document.createElement("input");
        input.type = "file";
        input.accept = "image/*";
        input.style.display = "none";

        input.addEventListener("change", () => {
            const file = input.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = () => {
                    images.push(reader.result);
                    updateImagePreview();
                };
                reader.readAsDataURL(file);
            }
        });

        document.body.appendChild(input);
        input.click();
        document.body.removeChild(input);
    });

    function updateImagePreview() {
        imageUpload.innerHTML = "";
        images.forEach((src, index) => {
            const box = document.createElement("div");
            box.className = "image-box";

            const img = document.createElement("img");
            img.src = src;

            const removeBtn = document.createElement("button");
            removeBtn.className = "remove";
            removeBtn.textContent = "x";
            removeBtn.addEventListener("click", () => {
                images.splice(index, 1);
                updateImagePreview();
            });

            box.appendChild(img);
            box.appendChild(removeBtn);
            imageUpload.appendChild(box);
        });

        const addBox = document.createElement("div");
        addBox.className = "image-box";
        addBox.id = "add-image-box";
        addBox.textContent = "+";
        addBox.addEventListener("click", () => {
            addImageBox.click();
        });

        imageUpload.appendChild(addBox);
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (images.length === 0) {
            alert("Please upload at least one image.");
            return;
        }

        const name = document.getElementById("product-name").value;
        const description = document.getElementById("product-description").value;
        const price = document.getElementById("product-price").value;
        const quantity = document.getElementById("product-quantity").value;
        const category = document.getElementById("product-category").value;
        const datetime = new Date().toISOString(); // Capture current datetime

        const productData = {
            name,
            description,
            price,
            quantity,
            category,
            images, // Send images (you might want to handle image storage on the server)
            datetime
        };

        if (editCard) {
            // Update the existing product on the backend
            await updateProduct(productData);
        } else {
            // Send new product to the backend
            await addProduct(productData);
        }

        closeModal();
    });

    function closeModal() {
        modal.style.display = "none";
        form.reset();
        images = [];
        updateImagePreview();
    }

    function attachCardActions(info) {
        const editBtn = info.querySelector(".edit-button");
        const deleteBtn = info.querySelector(".delete-button");

        editBtn.addEventListener("click", () => {
            modal.style.display = "flex";

            const card = editBtn.closest(".card");
            editCard = card;

            const name = card.querySelector("h4").textContent;
            const description = card.querySelector("p:nth-child(2)").textContent;
            const price = card.querySelector("p:nth-child(3)").textContent.split("$")[1];
            const quantity = card.querySelector("p:nth-child(4)").textContent.split(": ")[1];
            const category = card.querySelector("p:nth-child(5)").textContent.split(": ")[1];
            const imgSrc = card.querySelector("img").src;

            document.getElementById("product-name").value = name;
            document.getElementById("product-description").value = description;
            document.getElementById("product-price").value = price;
            document.getElementById("product-quantity").value = quantity;
            document.getElementById("product-category").value = category;

            images = [imgSrc];
            updateImagePreview();
        });

        deleteBtn.addEventListener("click", () => {
            const card = deleteBtn.closest(".card");
            marketplace.removeChild(card);
        });
    }

    // Helper function to send a request to the backend to add a new product
    async function addProduct(productData) {
        try {
            const formData = new FormData();
            formData.append("name", productData.name);
            formData.append("description", productData.description);
            formData.append("price", productData.price);
            formData.append("quantity", productData.quantity);
            formData.append("category", productData.category);
    
            // Append images to the formData
            productData.images.forEach((image, index) => {
                const imageBlob = dataURLtoBlob(image); // Convert data URL to Blob
                formData.append("images", imageBlob, `image_${index}.jpg`);
            });
    
            const response = await fetch('/api/products', {
                method: 'POST',
                body: formData
            });
    
            if (response.ok) {
                const product = await response.json();
                createProductCard(product);
            } else {
                alert("Failed to add product.");
            }
        } catch (error) {
            console.error("Error adding product:", error);
        }
    }
    
    function dataURLtoBlob(dataURL) {
        const byteString = atob(dataURL.split(',')[1]);
        const arrayBuffer = new ArrayBuffer(byteString.length);
        const uintArray = new Uint8Array(arrayBuffer);
        for (let i = 0; i < byteString.length; i++) {
            uintArray[i] = byteString.charCodeAt(i);
        }
        return new Blob([arrayBuffer], { type: 'image/jpeg' });
    }
    

    // Helper function to send a request to update an existing product
    async function updateProduct(productData) {
        try {
            const response = await fetch('/api/products/' + editCard.id, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(productData)
            });

            if (response.ok) {
                const updatedProduct = await response.json();
                updateProductCard(updatedProduct);
            } else {
                alert("Failed to update product.");
            }
        } catch (error) {
            console.error("Error updating product:", error);
        }
    }

    // Function to create a new product card in the marketplace
    function createProductCard(product) {
        const card = document.createElement("div");
        card.className = "card";
        card.id = product.id;
        const img = document.createElement("img");
        img.src = product.images[0];

        const info = document.createElement("div");
        info.innerHTML = `
            <h4>${product.name}</h4>
            <p>${product.description}</p>
            <p>Price: $${product.price}</p>
            <p>Quantity: ${product.quantity}</p>
            <p>Category: ${product.category}</p>
            <div class="actions">
                <button class="edit-button">Edit</button>
                <button class="delete-button">Delete</button>
            </div>
        `;
        attachCardActions(info);

        card.appendChild(img);
        card.appendChild(info);
        marketplace.appendChild(card);
    }

    // Function to update an existing product card in the marketplace
    function updateProductCard(product) {
        const card = document.getElementById(product.id);
        const img = card.querySelector("img");
        const info = card.querySelector("div");

        img.src = product.images[0];
        info.innerHTML = `
            <h4>${product.name}</h4>
            <p>${product.description}</p>
            <p>Price: $${product.price}</p>
            <p>Quantity: ${product.quantity}</p>
            <p>Category: ${product.category}</p>
            <div class="actions">
                <button class="edit-button">Edit</button>
                <button class="delete-button">Delete</button>
            </div>
        `;
        attachCardActions(info);
    }

    // Search and filter functions
    async function fetchFilteredProducts() {
        const query = searchInput.value;
        const category = categoryFilter.value;
        const date = dateFilter.value;

        try {
            const response = await fetch(`/api/products?search=${query}&category=${category}&date=${date}`);
            const products = await response.json();
            marketplace.innerHTML = ''; // Clear the current marketplace
            products.forEach(product => createProductCard(product)); // Render new filtered products
        } catch (error) {
            console.error("Error fetching filtered products:", error);
        }
    }

    // Event listeners for search and filter
    searchInput.addEventListener("input", fetchFilteredProducts);
    categoryFilter.addEventListener("change", fetchFilteredProducts);
    dateFilter.addEventListener("change", fetchFilteredProducts);
});
