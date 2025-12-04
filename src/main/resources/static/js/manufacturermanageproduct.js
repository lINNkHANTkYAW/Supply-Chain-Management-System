document.addEventListener("DOMContentLoaded", () => {
    const marketplace = document.getElementById("marketplace");
    const addItemBtn = document.getElementById("add-item-btn");
    const modal = document.getElementById("add-item-modal");
    const form = document.getElementById("add-item-form");
    const imageUpload = document.getElementById("image-upload");
    const addImageBox = document.getElementById("add-image-box");
    let images = [];
    let editCard = null; // Store the card being edited

    // Fetch products from API when the page loads
    fetchProducts();

    function fetchProducts() {
        console.log("Fetching products from API...");
        fetch('/api/products') // API endpoint to fetch products
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(products => {
                console.log("Products fetched:", products);
                products.forEach(product => {
                    createProductCard(product);
                });
            })
            .catch(error => console.error('Error fetching products:', error));
    }

    function createProductCard(product) {
        console.log("Creating product card for:", product);
        // Create a product card
        const card = document.createElement("div");
        card.className = "card";
        const img = document.createElement("img");
        img.src = product.image; // Assuming `image` is a URL or base64 string

        const info = document.createElement("div");
        info.innerHTML = `
            <h4>${product.name}</h4>
            <p>${product.description}</p>
            <p>Price: $${product.price}</p>
            <p>Quantity: ${product.stockQuantity}</p>
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

    function attachCardActions(info) {
        const editBtn = info.querySelector(".edit-button");
        const deleteBtn = info.querySelector(".delete-button");

        editBtn.addEventListener("click", () => {
            console.log("Editing product...");
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
            console.log("Deleting product...");
            const card = deleteBtn.closest(".card");
            marketplace.removeChild(card);
        });
    }

    // Open modal for adding a new item
    addItemBtn.addEventListener("click", () => {
        console.log("Opening modal to add new product...");
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
                    console.log("Image uploaded:", reader.result);
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
        console.log("Updating image preview...");
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
                console.log("Removing image at index:", index);
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

    form.addEventListener("submit", (e) => {
        e.preventDefault();
        console.log("Form submitted with images:", images);

        if (images.length === 0) {
            alert("Please upload at least one image.");
            return;
        }

        const name = document.getElementById("product-name").value;
        const description = document.getElementById("product-description").value;
        const price = document.getElementById("product-price").value;
        const quantity = document.getElementById("product-quantity").value;
        const category = document.getElementById("product-category").value;

        // Create a new card if no editCard exists
        if (editCard) {
            console.log("Updating existing product card...");
            const img = editCard.querySelector("img");
            const info = editCard.querySelector("div");
            img.src = images[0];
            info.innerHTML = `
                <h4>${name}</h4>
                <p>${description}</p>
                <p>Price: $${price}</p>
                <p>Quantity: ${quantity}</p>
                <p>Category: ${category}</p>
                <div class="actions">
                    <button class="edit-button">Edit</button>
                    <button class="delete-button">Delete</button>
                </div>
            `;
            attachCardActions(info);
        } else {
            console.log("Creating new product card...");
            const card = document.createElement("div");
            card.className = "card";
            const img = document.createElement("img");
            img.src = images[0];

            const info = document.createElement("div");
            info.innerHTML = `
                <h4>${name}</h4>
                <p>${description}</p>
                <p>Price: $${price}</p>
                <p>Quantity: ${quantity}</p>
                <p>Category: ${category}</p>
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

        closeModal();
    });

    function closeModal() {
        console.log("Closing modal...");
        modal.style.display = "none";
        form.reset();
        images = [];
        updateImagePreview();
    }
});