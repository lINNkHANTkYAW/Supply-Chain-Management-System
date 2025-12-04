// Profile Section
document.addEventListener("DOMContentLoaded", async function () {
    // Profile Fetching
    const profileIcon = document.getElementById("profileIcon");
    if (!profileIcon) {
        console.error("Profile icon element not found!");
    } else {
        try {
            const response = await fetch("/api/manufacturers/profile");
            if (!response.ok) {
                throw new Error(`Failed to fetch profile: HTTP ${response.status}`);
            }
            const user = await response.json();
            profileIcon.src = user.profileImage || "default-profile.png";
        } catch (error) {
            console.error("Error fetching profile:", error);
            profileIcon.src = "default-profile.png";
            profileIcon.alt = "Error loading profile image";
        }
    }

    // Logout
    const logoutBtn = document.getElementById("logoutBtn");
    if (!logoutBtn) {
        console.error("Logout button not found!");
    } else {
        logoutBtn.addEventListener("click", async function () {
            try {
                const response = await fetch("/auth/api/logout", {
                    method: "POST",
                    credentials: "include"
                });
                if (!response.ok) {
                    throw new Error(`Logout failed: HTTP ${response.status}`);
                }
                localStorage.removeItem("cart");
                window.location.href = "/";
            } catch (error) {
                console.error("Logout failed:", error);
                alert("Failed to logout. Please try again.");
            }
        });
    }

    // Fetch Categories
    fetchCategories();

});

// Search Section
document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.getElementById("searchForm");
    const searchInput = document.getElementById("searchInput");

    if (!searchForm || !searchInput) {
        console.error("Search form or input not found!");
        return;
    }

    searchForm.addEventListener("submit", function (event) {
        event.preventDefault();
        const query = searchInput.value.trim();
        if (query) {
            searchProducts(query);
        }
    });

    searchInput.addEventListener("input", function () {
        const query = searchInput.value.trim();
        if (query) {
            searchProducts(query);
        }
    });
});

function searchProducts(query) {
    const itemsContainer = document.getElementById("searchItemsContainer");
    const modalElement = document.getElementById("searchItemsModal");

    if (!itemsContainer || !modalElement) {
        console.error("Required DOM elements not found: searchItemsContainer or searchItemsModal");
        alert("Search functionality is not available. Please try again later.");
        return;
    }

    fetch(`/supplier-raw-materials/search?query=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            itemsContainer.innerHTML = ""; // Clear previous results

            if (!Array.isArray(data)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(data));
            }

            if (data.length === 0) {
                itemsContainer.innerHTML = "<p>No items found. Try refining your search.</p>";
            } else {
                data.forEach(item => {
                    if (!item.id || !item.supplierId || !item.companyName || !item.name || !item.description) {
                        console.warn("Skipping item with missing fields:", item);
                        return;
                    }
					console.log("Image url", item.image);

                    const supplierLink = `/manuseesupplier?supplierId=${item.supplierId}`;
                    const itemCard = `
                        <div class="col-md-4">
                            <div class="card mb-3">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
							                 class="card-img-top" 
							                 style="height: 150px; object-fit: cover;" 
							                 alt="${item.name}">                                <div class="card-body">
                                    <h5 class="card-title">${item.companyName}</h5>
                                    <h6 class="card-title">${item.name}</h6>
                                    <button class="btn btn-primary btn-sm" onclick="viewDetails(${item.id})">Details</button>
                                    <button class="btn btn-secondary btn-sm">
                                        <a href="${supplierLink}" style="color: white; text-decoration: none;">Profile</a>
                                    </button>
                                </div>
                            </div>
                        </div>`;
                    itemsContainer.innerHTML += itemCard;
                });
            }

            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        })
        .catch(error => {
            console.error("Error searching products:", error);
            itemsContainer.innerHTML = "<p class='text-danger'>An error occurred while searching for products. Please try again later.</p>";
        });
}

// Categories Section
function fetchCategories() {
    const categoryContainer = document.getElementById("categoryContainer");
    if (!categoryContainer) {
        console.error("Category container not found!");
        return;
    }

    fetch("/api/categories")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            categoryContainer.innerHTML = "";

            if (!Array.isArray(data)) {
                throw new Error("Expected an array of categories, but received: " + JSON.stringify(data));
            }

            data.forEach(category => {
                const categoryCard = `
                    <div class="col-12 col-sm-6 col-md-4 d-flex justify-content-center">
                        <div class="card category-card text-center" data-category="${category.categoryId}" style="width:300px">
                            <img src="${category.imageUrl || 'img/default.jpg'}" class="card-img-top" style="width:300px; height:200px; object-fit: cover;" alt="${category.categoryName}">
                            <div class="card-body">
                                <h6 class="card-title">${category.categoryName}</h6>
                            </div>
                        </div>
                    </div>`;
                categoryContainer.innerHTML += categoryCard;
            });

            document.querySelectorAll('.category-card').forEach(card => {
                card.addEventListener('click', function () {
                    const categoryId = this.getAttribute("data-category");
                    console.log("Clicked Category ID:", categoryId);

                    if (!categoryId) {
                        console.error("No category ID found!");
                        return;
                    }
                    console.log("Fetching products for category ID:", categoryId);
                    fetchProductsByCategory(categoryId);
                });
            });
        })
        .catch(error => {
            console.error("Error fetching categories:", error);
            categoryContainer.innerHTML = "<p class='text-danger text-center'>Failed to load categories. Please try again later.</p>";
        });
}

function fetchProductsByCategory(categoryId) {
    if (!categoryId) {
        console.error("Category ID is missing!");
        return;
    }

    const itemsContainer = document.getElementById("itemsContainer");
    const modalElement = document.getElementById("itemsModal");

    if (!itemsContainer || !modalElement) {
        console.error("Required DOM elements not found: itemsContainer or itemsModal");
        alert("Cannot display products. Please try again later.");
        return;
    }

    fetch(`/supplier-raw-materials/category/${categoryId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(products => {
            console.log("Fetched products:", products);
            itemsContainer.innerHTML = "";

            if (!Array.isArray(products)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(products));
            }

            if (products.length === 0) {
                itemsContainer.innerHTML = "<p>No items found.</p>";
                console.log("No items found for category:", categoryId);
            } else {
                console.log("Rendering products:", products);
                products.forEach(product => {
                    //const imgUrl = product.image || "img/default-product.jpg";
                    const supplierLink = `/manuseesupplier?supplierId=${product.supplierId}`;
                    itemsContainer.innerHTML += `
                        <div class="col-md-4">
                            <div class="card mb-3">
							<img src="${product.image ? '/uploads/' + product.image : 'img/default-product.jpg'}" 
							                 class="card-img-top" 
							                 style="height: 150px; object-fit: cover;" 
							                 alt="${product.name}">
                                <div class="card-body">
                                    <h6 class="card-title">${product.name}</h6>
                                    <p class="card-text">Category: ${product.categoryName}</p>
                                    <p class="card-text">Supplier: 
                                        <a href="${supplierLink}" style="text-decoration: none; color: #007bff;">${product.companyName}</a>
                                    </p>
                                    
                                    <button class="btn btn-primary btn-sm" onclick="viewDetails(${product.productId})">Details</button>
                                </div>
                            </div>
                        </div>`;
                });
            }

            console.log("Showing modal with products...");
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        })
        .catch(error => {
            console.error("Error fetching products:", error);
            itemsContainer.innerHTML = "<p class='text-danger'>Failed to load products. Please try again later.</p>";
        });
}

// Product Details Modal
let isConfirmingClose = false;

async function viewDetails(productId) {
    const modalBody = document.getElementById("productModalBody");
    const modalElement = document.getElementById("productDetailsModal");
    if (!modalBody || !modalElement) {
        console.error("Required DOM elements not found: productModalBody or productDetailsModal");
        alert("Cannot display product details. Please try again later.");
        return;
    }

    try {
        const response = await fetch(`/supplier-raw-materials/${productId}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch product: HTTP ${response.status}`);
        }
        const product = await response.json();
        console.log("Fetched product:", product);

        if (!product.supplier.supplierId) {
            throw new Error("manufacturerId is missing or invalid in the product data");
			
        }
	
        const [supplierResponse, productsResponse] = await Promise.all([
            fetch(`/api/suppliers/${product.supplier.supplierId}`),
            fetch(`/supplier-raw-materials/supplier/${product.supplier.supplierId}`)
        ]);

        if (!supplierResponse.ok) {
            throw new Error(`Failed to fetch distributor: HTTP ${supplierResponse.status}`);
        }
        if (!productsResponse.ok) {
            throw new Error(`Failed to fetch distributor products: HTTP ${productsResponse.status}`);
        }

        const supplier = await supplierResponse.json();
        let products = await productsResponse.json();

        console.log("Fetched supplier:", supplier);
        console.log("Fetched products:", products);

        if (!Array.isArray(products)) {
            console.error("Expected products to be an array, but got:", products);
            products = [];
        }

        products = products.filter(item => item.productId !== product.productId);

        let imgUrl = supplier.profileImg || "img/default-profile.jpg";

       

        modalBody.innerHTML = `
            <!-- Distributor Profile -->
            <div class="d-flex align-items-center mb-3">
                <img src="${imgUrl}" class="rounded-circle me-2" width="50" height="50" 
                     onclick="window.location.href='/manufacturer/${supplier.supplierId}'" style="cursor: pointer;">
                <div>
                    <h6 class="mb-0">${supplier.companyName}</h6>
                    <p class="text-muted mb-0">${supplier.email || 'N/A'}</p>
                </div>
            </div>

            <!-- Navbar with Search -->
            <nav class="navbar navbar-light bg-light px-3 d-flex justify-content-between">
                <input class="form-control me-2" type="search" id="searchManufacturerProducts"
                       placeholder="Search products..." 
                       oninput="filterMarketplace(${supplier.supplierId})">
            </nav>

            <!-- Main Product Details -->
                <div class="col-md-9">
                    <div class="d-flex">
                        <div id="productImage" style="width: 50%;">
						<img src="${product.image ? '/uploads/' + product.image : 'img/default-product.jpg'}" 
						                 class="card-img-top" 
						                 style="height: 150px; object-fit: cover;" 
						                 alt="${product.name}">
                        </div>
                        <div id="productDetails" style="width: 50%;">
                            <h4>${product.name}</h4>
                            <p>${product.description || 'No description available'}</p>
                            
                            <button class="btn btn-success mt-3" data-supplier-user-id="${supplier.supplierUserId}" onclick="openChatWithSupplier(this)">
                                  chat
                            </button>
                        </div>
                    </div>
                </div>
            </div>

                        <!-- Distributor's Marketplace (New Row) -->
            <div class="mt-4">
                <h4 class="text-center">Other Items from ${supplier.companyName}</h4>
                <div class="row" id="marketplace">
                    ${products.length > 0 ? products.map(item => `
                        <div class="col-md-3">
                            <div class="card">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
							                 class="card-img-top" 
							                 style="height: 150px; object-fit: cover;" 
							                 alt="${item.name}">
                                <div class="card-body">
                                    <h6 class="card-title">${item.name}</h6>
                                    <p class="text-muted">$${item.price}</p>
                                    <button class="btn btn-primary btn-sm" onclick="viewDetails(${item.productId})">Details</button>                                    
                                </div>
                            </div>
                        </div>
                    `).join('') : '<p class="text-center">No other items available.</p>'}
                </div>
            </div>
        `;
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    } catch (error) {
        console.error("Error fetching product details:", error);
        modalBody.innerHTML = "<p class='text-danger'>Failed to load product details. Please try again later.</p>";
    }
}

function openChatWithSupplier(button) {
    const supplierUserId = button.getAttribute("data-supplier-user-id");
    console.log("Opening chat with supplier user ID:", supplierUserId);
    window.openChat(supplierUserId, "Supplier"); // Call the global openChat from distriChat.js
}

// Filter Marketplace
function filterMarketplace(supplierId) {
    const searchManufacturerProducts = document.getElementById("searchManufacturerProducts"); // ပြင်ရန်
    const marketplace = document.getElementById("marketplace");

    if (!searchManufacturerProducts || !marketplace) {
        console.error("Required DOM elements are missing:", {
            searchManufacturerProducts: !!searchManufacturerProducts,
            marketplace: !!marketplace
        });
        return;
    }

    const query = searchManufacturerProducts.value.toLowerCase();

    fetch(`/supplier-raw-materials/manufacturer/${manufacturerId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(products => {
            console.log("Fetched products:", products);

            if (!Array.isArray(products)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(products));
            }

            const filteredProducts = products.filter(item => {
                const matchesQuery = !query || item.name.toLowerCase().includes(query);
                return matchesQuery;
            });

            console.log("Filtered products:", filteredProducts);

            marketplace.innerHTML = filteredProducts.length > 0 ?
                filteredProducts.map(item => `
                    <div class="col-md-3">
                        <div class="card">
						<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
						                 class="card-img-top" 
						                 style="height: 150px; object-fit: cover;" 
						                 alt="${item.name}">
                            <div class="card-body">
                                <h6 class="card-title">${item.name}</h6>
                                <p class="text-muted">$${item.price}</p>
								<button class="btn btn-primary btn-sm" onclick="viewDetails(${item.productId})">Details</button>
                                
                            </div>
                        </div>
                    </div>
                `).join('')
                : `<p class="text-center">No products found.</p>`;
        })
        .catch(error => {
            console.error("Error filtering marketplace:", error);
            marketplace.innerHTML = `<p class="text-center text-danger">Error loading products. Please try again later.</p>`;
        });
}


async function getCustomerId() {
    try {
        const response = await fetch("/api/suppliers/session-user");
        if (!response.ok) {
            throw new Error(`Failed to fetch supplier ID: HTTP ${response.status}`);
        }
        const supplierId = await response.json();
        console.log("🔹 Retrieved Supplier ID:", supplierId);
        return supplierId;
    } catch (error) {
        console.error("❌ Error fetching supplier ID:", error);
        return null;
    }
}

// Distributor Profile
function viewProfile(supplierId) {
    const userProfileModalLabel = document.getElementById("userProfileModalLabel");
    const userProfileModalBody = document.getElementById("userProfileModalBody");
    const modalElement = document.getElementById("userProfileModal");

    if (!userProfileModalLabel || !userProfileModalBody || !modalElement) {
        console.error("Required DOM elements not found: userProfileModalLabel, userProfileModalBody, or userProfileModal");
        alert("Cannot display supplier profile. Please try again later.");
        return;
    }

    fetch(`/api/suppliers/${supplierId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(supplier => {
            userProfileModalLabel.textContent = supplier.companyName;
            userProfileModalBody.innerHTML = `
                <img src="${supplier.profileImg || 'img/default-profile.jpg'}" class="img-fluid mb-2" style="height: 150px; border-radius: 50%;" alt="${supplier.companyName}">
                <p><strong>Name:</strong> ${supplier.companyName}</p>
                <p><strong>Email:</strong> ${supplier.email || 'N/A'}</p>
                <p><strong>Phone:</strong> ${supplier.contactInfo || 'N/A'}</p>
                <p><strong>Address:</strong> ${supplier.address || 'N/A'}</p>
            `;
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        })
        .catch(error => {
            console.error("Error fetching distributor profile:", error);
            userProfileModalBody.innerHTML = "<p class='text-danger'>Failed to load distributor profile. Please try again later.</p>";
        });
}

function viewSupplierDetails(supplierId) {
    if (!supplierId) {
        console.error("Supplier ID is missing!");
        return;
    }

    localStorage.setItem('supplierId', supplierId);
    window.location.href = '/supplierprofile.html';
}


// Fetch all products
function fetchAllProducts() {
    const itemsContainer = document.getElementById("itemsContainer2");

    // Check if the container exists
    if (!itemsContainer) {
        console.error("Required DOM element not found: itemsContainer");
        alert("Functionality is not available. Please try again later.");
        return;
    }
    console.log("itemsContainer found:", itemsContainer); // Confirm the element is found

    fetch(`/supplier-raw-materials/all`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Fetched supplier materials: ", data);
            itemsContainer.innerHTML = ""; // Clear previous results

            if (!Array.isArray(data)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(data));
            }

            if (data.length === 0) {
                itemsContainer.innerHTML = "<p>No products found.</p>";
                console.log("No products to display.");
            } else {
                const limitedProducts = data.slice(0, 36); // Limit to 36 products
                console.log("Limited products to render:", limitedProducts);

                limitedProducts.forEach((item, index) => {
                    const rawMaterialSid = item.rawMaterialSid;
                    const supplierId = item.supplier?.supplierId;
                    const supplierName = item.supplier?.companyName;
                    const productName = item.name;
                    const productPrice = item.unitPrice;
                    const productDescription = item.description;
                    // Validation
                    if (!rawMaterialSid || !supplierId || !supplierName || !productName || !productPrice || !productDescription) {
                        console.warn(`Skipping item at index ${index} with missing fields:`, item);
                        return;
						
                    }

                    const supplierLink = `/manuseesupplier?supplierId=${supplierId}`;

                    const itemCard = `
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
							                 class="card-img-top" 
							                 style="height: 150px; object-fit: cover;" 
							                 alt="${item.name}">
                                <div class="card-body">
								
                                    <h5 class="card-title">${supplierName}</h5>
                                    <h6 class="card-title">${productName}</h6>
                                    <p class="card-text">$${productPrice}</p>
                                    <button class="btn btn-primary btn-sm" onclick="viewDetails(${rawMaterialSid})">Details</button>
                                    <button class="btn btn-secondary btn-sm">
                                        <a href="${supplierLink}" style="color: white; text-decoration: none;">Profile</a>
                                    </button>
                                </div>
                            </div>
                        </div>`;
                    
                    // Append each card individually and log it
                    itemsContainer.innerHTML += itemCard;
					
                    console.log(`Rendered product ${index + 1}: ${productName}`);
                });

                console.log("Final itemsContainer content:", itemsContainer.innerHTML);
            }
        })
        .catch(error => {
            console.error("Error fetching products:", error);
            itemsContainer.innerHTML = "<p class='text-danger'>An error occurred while fetching products. Please try again later.</p>";
        });
}

// Ensure the function runs after the DOM is fully loaded
document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM fully loaded, fetching products...");
    fetchAllProducts();
});

