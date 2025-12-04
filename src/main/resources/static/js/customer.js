


// Profile Section
document.addEventListener("DOMContentLoaded", async function () {
    // Profile Fetching
    const profileIcon = document.getElementById("profileIcon");
    if (!profileIcon) {
        console.error("Profile icon element not found!");
    } else {
        try {
            const response = await fetch("/api/customer/profile");
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

    // Fetch Brands
    fetchBrands();

    // Fetch Trending Products
    fetchTrendingProducts();
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

    fetch(`/api/products/search?query=${encodeURIComponent(query)}`)
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
                    if (!item.id || !item.distributorId || !item.distributorName || !item.name || !item.price || !item.rating) {
                        console.warn("Skipping item with missing fields:", item);
                        return;
                    }

                    const distributorLink = `/customerseedistributor?distributorId=${item.distributorId}`;
                    const itemCard = `
                        <div class="col-md-4">
                            <div class="card mb-3">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
														                 class="card-img-top" 
														                 style="height: 150px; object-fit: cover;" 
														                 alt="${item.name}">                                   
								<div class="card-body">
                                    <h5 class="card-title">${item.distributorName}</h5>
                                    <h6 class="card-title">${item.name}</h6>
                                    <p class="card-text">$${item.price}</p>
                                    <div class="d-flex align-items-center">
                                        <span class="stars">${getStars(item.rating)}</span>
                                        <span class="ms-2">${item.rating} ⭐</span>
                                    </div>
                                    <button class="btn btn-primary btn-sm" onclick="viewDetails(${item.id})">Details</button>
                                    <button class="btn btn-secondary btn-sm">
                                        <a href="${distributorLink}" style="color: white; text-decoration: none;">Profile</a>
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
            itemsContainer.innerHTML = "<p class='text-danger'>Product Not Found</p>";
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
				                        <div class="card category-card text-center" data-category="${category.categoryId}" style="width: 300px;">
				                            <img src="${category.imageUrl || 'img/default.jpg'}" class="card-img-top" style="width:300px; height:200px; object-fit: cover;" alt="${category.categoryName}">
				                            <div class="card-body" style="width:300px;">
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

    fetch(`/api/products/category/${categoryId}`)
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
                    // const imgUrl = product.image || "img/default-product.jpg";
                    const distributorLink = `/customerseedistributor?distributorId=${product.distributorId}`;
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
                                    <p class="card-text">Quantity: ${product.stockQuantity}</p>
                                    <p class="card-text">Distributor: 
                                        <a href="${distributorLink}" style="text-decoration: none; color: #007bff;">${product.companyName}</a>
                                    </p>
                                    <div class="d-flex align-items-center">
                                        <span class="stars">${getStars(product.rating)}</span>
                                        <span class="ms-2">${product.rating} ⭐</span>
                                    </div>
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
function handleModalClose() {
    if (cart.length > 0 && !isConfirmingClose) {
        isConfirmingClose = true;
        if (confirm("Are you sure you want to leave? Your cart will be emptied.")) {
            cart = [];
            localStorage.setItem("cart", JSON.stringify(cart));
            updateCart();
        } else {
            isConfirmingClose = false;
            new bootstrap.Modal(document.getElementById("productDetailsModal")).show();
        }
    }
}

async function viewDetails(productId) {
    const modalBody = document.getElementById("productModalBody");
    const modalElement = document.getElementById("productDetailsModal");

    if (!modalBody || !modalElement) {
        console.error("Required DOM elements not found: productModalBody or productDetailsModal");
        alert("Cannot display product details. Please try again later.");
        return;
    }

    try {
        const response = await fetch(`/api/products/${productId}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const product = await response.json();

        const [distributorResponse, productsResponse] = await Promise.all([
            fetch(`/api/distributors/${product.distributorId}`),
            fetch(`/api/products/distributor/${product.distributorId}`)
        ]);

        if (!distributorResponse.ok || !productsResponse.ok) throw new Error("Failed to fetch distributor or products");
        const distributor = await distributorResponse.json();
        let products = await productsResponse.json();

        products = products.filter(item => item.productId !== product.productId);

        if (cart.length > 0 && cart[0].distributorId !== product.distributorId) {
            alert("You can only buy from one distributor per order.");
            return;
        }

        modalBody.innerHTML = `
            <!-- Distributor Profile -->
            <div class="d-flex align-items-center mb-3">
                <img src="${distributor.profileImg || 'img/default-profile.jpg'}" class="rounded-circle me-2" width="50" height="50" 
                     onclick="window.location.href='/distributor/${distributor.distributorId}'" style="cursor: pointer;">
                <div>
                    <h6 class="mb-0">${distributor.companyName}</h6>
                    <p class="text-muted mb-0">${distributor.email || 'N/A'}</p>
                    <div class="d-flex align-items-center">
                        <span class="stars">${getStars(distributor.rating)}</span>
                        <span class="ms-2">${distributor.rating} ⭐️</span>
                    </div>
                </div>
            </div>

            <!-- Navbar with Search and Cart -->
            <nav class="navbar navbar-light bg-light px-3 d-flex justify-content-between">
                <input class="form-control me-2" type="search" id="searchDistributorProducts"
                       placeholder="Search products..." 
                       oninput="filterMarketplace(${distributor.distributorId}, 'All', null)">
                <button class="btn btn-outline-dark position-relative" onclick="toggleCart()">
                    🛒 Cart <span id="cartCount" class="badge bg-danger">${cart.reduce((sum, item) => sum + item.quantity, 0)}</span>
                </button>
            </nav>

            <!-- Main Product Section -->
            <div class="row">
                <div class="col-md-3 border-end">
                    <h5>Filter by Amount</h5>
                    <input type="number" id="amountFilter" class="form-control" placeholder="Max Price" 
                           oninput="filterMarketplace(${distributor.distributorId}, 'All', null)">
                    <h5 class="mt-3">Categories</h5>
                    <ul class="list-group">
                        <li class="list-group-item" onclick="filterMarketplace(${distributor.distributorId}, 'All', this)">All</li>
                        <li class="list-group-item" onclick="filterMarketplace(${distributor.distributorId}, 'Fashion and Apparel', this)">Fashion and Apparel</li>
                        <li class="list-group-item" onclick="filterMarketplace(${distributor.distributorId}, 'Household Items', this)">Household Items</li>
                        <li class="list-group-item" onclick="filterMarketplace(${distributor.distributorId}, 'Food and Beverages', this)">Food and Beverages</li>
                    </ul>
                </div>

				<div class="col-md-9">
				            <div class="d-flex">
				                <div id="productImage" style="width: 50%;">
								<img style="width: 300px; height: 200px; object-fit: cover;" 
								     src="${product.image ? '/uploads/' + product.image : 'img/default-product.jpg'}" 
								     class="img-fluid" 
								     alt="${product.name}">				                
								</div>
				                <div id="productDetails" style="width: 50%;">
				                    <h4>${product.name}</h4>
				                    <p>${product.description || 'No description available'}</p>
				                    <h5>Price: $${product.price}</h5>
				                    <div class="d-flex align-items-center">
				                        <button class="btn btn-outline-primary" onclick="updateMainProductQuantity(${product.productId}, -1)">-</button>
				                        <input type="text" id="quantity-${product.productId}" value="${getCartQuantity(product.productId)}" class="form-control text-center mx-2" style="width: 50px;" readonly>
				                        <button class="btn btn-outline-primary" onclick="updateMainProductQuantity(${product.productId}, 1)">+</button>
				                    </div>
				                    <button class="btn btn-success mt-3" 
				                            onclick="addToCart(${product.productId}, '${product.name}', '${product.image || 'img/default-product.jpg'}', ${product.price}, ${distributor.distributorId})">
				                        Add to Cart
				                    </button>
				                </div>
				            </div>
				        </div>
            </div>

            <!-- Distributor's Marketplace -->
            <div class="mt-4">
                <h4 class="text-center">Other Items from ${distributor.companyName}</h4>
                <div class="row" id="marketplace">
                    ${products.length > 0 ? products.map(item => `
                        <div class="col-md-3">
                            <div class="card">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
																						                 class="card-img-top" 
																						                 style="height: 150px; object-fit: cover;" 
																						                 alt="${item.name}">                                   <div class="card-body">
                                    <h6 class="card-title">${item.name}</h6>
                                    <p class="text-muted">$${item.price}</p>
                                    <div class="d-flex align-items-center">
                                        <span class="stars">${getStars(item.rating)}</span>
                                        <span class="ms-2">${item.rating} ⭐️</span>
                                    </div>
                                    <div class="d-flex align-items-center my-2">
                                        <button class="btn btn-sm btn-outline-secondary" onclick="updateOtherProductQuantity(${item.productId}, -1)">-</button>
                                        <span id="quantity-${item.productId}" class="mx-2">${getCartQuantity(item.productId)}</span>
                                        <button class="btn btn-sm btn-outline-secondary" onclick="updateOtherProductQuantity(${item.productId}, 1)">+</button>
                                    </div>
                                    <button class="btn btn-sm btn-primary" 
                                            onclick="addToCart(${item.productId}, '${item.name}', '${item.image || 'img/default-product.jpg'}', ${item.price}, ${distributor.distributorId})">
                                        Add to Cart
                                    </button>
                                </div>
                            </div>
                        </div>
                    `).join('') : '<p class="text-center">No other items available.</p>'}
                </div>
            </div>

            <!-- Cart Sidebar -->
            <div id="cartSidebar" class="cart-sidebar">
                <h4 class="text-center p-3">🛒 Your Cart</h4>
                <div id="cartItems"></div>
                <div class="cart-footer">
                    <h5>Total: $<span id="cartTotal">0.00</span></h5>
                    <button class="btn btn-success w-100" onclick="openPlaceOrderModal()">Place Order</button>
                </div>
            </div>
        `;

        updateCart(); // Initial cart update
        modalElement.addEventListener('hidden.bs.modal', handleModalClose);
        new bootstrap.Modal(modalElement).show();
    } catch (error) {
        console.error("Error fetching product details:", error);
        modalBody.innerHTML = "<p class='text-danger'>Failed to load product details.</p>";
    }
}


// Supporting Functions (ensure these are present)
function getCartQuantity(productId) {
    const item = cart.find(item => item.productId === productId);
    return item ? item.quantity : 1;
}

function updateMainProductQuantity(productId, change) {
    const quantityElement = document.getElementById(`quantity-${productId}`);
    if (!quantityElement) return;

    let quantity = parseInt(quantityElement.value) || 1;
    quantity += change; // Allow negative or zero
    if (quantity < 0) quantity = 0; // Prevent negative quantities
    quantityElement.value = quantity;

    const cartItem = cart.find(item => item.productId === productId);
    if (cartItem) {
        cartItem.quantity = quantity;
        if (cartItem.quantity <= 0) {
            cart = cart.filter(item => item.productId !== productId);
        }
        localStorage.setItem("cart", JSON.stringify(cart));
        updateCart();
    }
}

function updateOtherProductQuantity(productId, change) {
    const quantityElement = document.getElementById(`quantity-${productId}`);
    if (!quantityElement) return;

    let quantity = parseInt(quantityElement.textContent) || 1;
    quantity += change; // Allow negative or zero
    if (quantity < 0) quantity = 0; // Prevent negative quantities
    quantityElement.textContent = quantity;

    const cartItem = cart.find(item => item.productId === productId);
    if (cartItem) {
        cartItem.quantity = quantity;
        if (cartItem.quantity <= 0) {
            cart = cart.filter(item => item.productId !== productId);
        }
        localStorage.setItem("cart", JSON.stringify(cart));
        updateCart();
    }
}

// Filter Marketplace
function filterMarketplace(distributorId, categoryName, clickedItem) {
    console.log("📌 Distributor ID in filterMarketplace:", distributorId);
    console.log("📌 Category:", categoryName);
    console.log("📌 Clicked Item:", clickedItem);

    if (clickedItem) {
        const categoryItems = document.querySelectorAll('.list-group-item');
        categoryItems.forEach(item => item.classList.remove('active'));
        clickedItem.classList.add('active');
    }

    const searchDistributorProducts = document.getElementById("searchDistributorProducts");
    const amountFilter = document.getElementById("amountFilter");
    const marketplace = document.getElementById("marketplace");

    if (!searchDistributorProducts || !amountFilter || !marketplace) {
        console.error("Required DOM elements are missing:", {
            searchDistributorProducts: !!searchDistributorProducts,
            amountFilter: !!amountFilter,
            marketplace: !!marketplace
        });
        return;
    }

    const query = searchDistributorProducts.value.toLowerCase();
    let maxPrice = amountFilter.value ? parseFloat(amountFilter.value) : null;
    if (maxPrice !== null && (isNaN(maxPrice) || maxPrice < 0)) {
        console.warn("Invalid max price, ignoring filter:", maxPrice);
        maxPrice = null;
    }

    console.log("📌 Search Query:", query);
    console.log("📌 Max Price:", maxPrice);

    fetch(`/api/products/distributor/${distributorId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(products => {
            console.log("📌 Fetched products:", products);

            if (!Array.isArray(products)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(products));
            }

            const filteredProducts = products.filter(item => {
                const matchesQuery = !query || item.name.toLowerCase().includes(query);
                const matchesPrice = !maxPrice || item.price <= maxPrice;
                const matchesCategory = categoryName === 'All' || item.categoryName.toLowerCase() === categoryName.toLowerCase();
                console.log(`📌 Filtering item "${item.name}":`, { matchesQuery, matchesPrice, matchesCategory });
                return matchesQuery && matchesPrice && matchesCategory;
            });

            console.log("📌 Filtered products:", filteredProducts);

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
                                <div class="d-flex align-items-center">
                                    <span class="stars">${getStars(item.rating)}</span>
                                    <span class="ms-2">${item.rating} ⭐</span>
                                </div>
                                <div class="d-flex align-items-center my-2">
                                    <button class="btn btn-sm btn-outline-secondary" onclick="decreaseQuantity(${item.productId})">-</button>
                                    <span id="quantity-${item.productId}" class="mx-2">1</span>
                                    <button class="btn btn-sm btn-outline-secondary" onclick="increaseQuantity(${item.productId})">+</button>
                                </div>
								<button class="btn btn-sm btn-primary" 
								        onclick="addToCart(${item.productId}, '${item.name}', '${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}', ${item.price}, ${distributorId})">
								    Add to Cart
								</button>
							
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

// Newly Arrived Brands
function fetchBrands() {
    const brandCarouselInner = document.querySelector("#brandCarousel .carousel-inner");
    if (!brandCarouselInner) {
        console.error("Brand carousel inner element not found!");
        return;
    }

    fetch("/api/products/brands")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            brandCarouselInner.innerHTML = "";

            if (!Array.isArray(data)) {
                throw new Error("Expected an array of brands, but received: " + JSON.stringify(data));
            }

            for (let i = 0; i < data.length; i += 4) {
                const isActive = i === 0 ? "active" : "";
                const brandsChunk = data.slice(i, i + 4);

                const brandCards = brandsChunk.map(brand => `
                    <div class="col-6 col-md-3 d-flex justify-content-center">
                        <div class="card" style="width: 300px">
						<img src="${brand.image ? '/uploads/' + brand.image : 'img/default-product.jpg'}" 
													                 class="card-img-top" 
													                 style="height: 150px; object-fit: cover;" 
													                 alt="${brand.name}">                               <div class="card-body">
                                <p class="card-text">${brand.name}</p>
                                <p class="card-text">$${brand.price}</p>
                                <div class="d-flex align-items-center">
                                    <span class="stars">${getStars(brand.rating)}</span>
                                    <span class="ms-2">${brand.rating} ⭐️</span>
                                </div>
								<button class="btn btn-primary btn-sm" onclick="viewDetails(${brand.productId})">Details</button>

                            </div>
                        </div>
                    </div>
                `).join('');

                brandCarouselInner.innerHTML += `
                    <div class="carousel-item ${isActive}">
                        <div class="row justify-content-center">
                            ${brandCards.padEnd(4, '<div class="col-6 col-md-3"></div>')}
                        </div>
                    </div>`;
            }
        })
        .catch(error => {
            console.error("Error fetching brands:", error);
            brandCarouselInner.innerHTML = "<p class='text-danger text-center'>Failed to load brands. Please try again later.</p>";
        });
}

// Trending Products
function fetchTrendingProducts() {
    const trendingProductsList = document.getElementById("trendingProductsList");
    if (!trendingProductsList) {
        console.error("Trending products list element not found!");
        return;
    }

    fetch("/api/products/trending")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            trendingProductsList.innerHTML = "";

            if (!Array.isArray(data)) {
                throw new Error("Expected an array of products, but received: " + JSON.stringify(data));
            }

            const limitedProducts = data.slice(0, 16);
            let slides = "";

            for (let i = 0; i < limitedProducts.length; i += 4) {
                const isActive = i === 0 ? "active" : "";
                const productsChunk = limitedProducts.slice(i, i + 4);

                slides += `<div class="carousel-item ${isActive}">
                            <div class="row">`;
                productsChunk.forEach(item => {
                    slides += `
                        <div class="col-md-3">
                            <div class="card mb-3">
							<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
														                 class="card-img-top" 
														                 style="height: 150px; object-fit: cover;" 
														                 alt="${item.name}">                                   <div class="card-body">
                                    <h6 class="card-title">${item.name}</h6>
                                    <p class="text-muted">$${item.price}</p>
                                    <div class="d-flex align-items-center">
                                        <span class="stars">${getStars(item.rating)}</span>
                                        <span class="ms-2">${item.rating} ⭐</span>
                                    </div>
									<button class="btn btn-primary btn-sm" onclick="viewDetails(${item.productId})">Details</button>

                                </div>
                            </div>
                        </div>`;
                });
                slides += `</div></div>`;
            }

            trendingProductsList.innerHTML = slides;
        })
        .catch(error => {
            console.error("Error fetching trending products:", error);
            trendingProductsList.innerHTML = "<p class='text-danger text-center'>Failed to load trending products. Please try again later.</p>";
        });
}

// Cart and Checkout
let cart = JSON.parse(localStorage.getItem("cart")) || [];

function addToCart(productId, name, image, price, distributorId) {
    if (cart.length > 0 && cart[0].distributorId !== distributorId) {
        alert("You can only buy from one distributor per order.");
        return;
    }

    const quantityElement = document.getElementById(`quantity-${productId}`);
    const quantity = quantityElement ? parseInt(quantityElement.value || quantityElement.textContent || 1) : 1;

    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity = quantity;
    } else {
        cart.push({ productId, name, image, price, distributorId, quantity });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    updateCart();
}

function toggleCart() {
    const cartSidebar = document.getElementById("cartSidebar");
    if (!cartSidebar) return;
    cartSidebar.classList.toggle("open");
}

function updateCart() {
    const cartItems = document.getElementById("cartItems");
    const cartTotal = document.getElementById("cartTotal");
    const cartCount = document.getElementById("cartCount");

    if (!cartItems || !cartTotal || !cartCount) return;

    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);

    cartItems.innerHTML = cart.length === 0 ? "<p class='text-center'>Your cart is empty.</p>" : cart.map(item => `
        <div class="cart-item d-flex justify-content-between align-items-center p-2 border-bottom">
		<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
									                 class="card-img-top" 
									                 style="height: 150px; object-fit: cover;" 
									                 alt="${item.name}">               <div>
                <p class="mb-0">${item.name}</p>
                <p class="text-muted">$${item.price.toFixed(2)}</p>
            </div>
            <div class="d-flex align-items-center">
                <button class="btn btn-sm btn-outline-danger" onclick="updateCartQuantity(${item.productId}, -1)">-</button>
                <span class="mx-2" id="quantity-${item.productId}">${item.quantity}</span>
                <button class="btn btn-sm btn-outline-success" onclick="updateCartQuantity(${item.productId}, 1)">+</button>
            </div>
        </div>`).join("");

    cartTotal.textContent = total.toFixed(2);
    cartCount.textContent = count;
}

function updateCartQuantity(productId, change) {
    const item = cart.find(item => item.productId === productId);
    if (!item) return;

    item.quantity += change; // Allow quantity to go to 0 or below
    if (item.quantity <= 0) {
        cart = cart.filter(i => i.productId !== productId); // Remove item if quantity is 0 or less
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    updateCart();

    // Sync quantity in modal if open
    const quantityElement = document.getElementById(`quantity-${productId}`);
    if (quantityElement) {
        if (quantityElement.tagName === "INPUT" && item) quantityElement.value = item.quantity;
        else if (quantityElement.tagName === "SPAN" && item) quantityElement.textContent = item.quantity;
        // If item is removed, no need to update quantityElement as it will be redrawn by updateCart
    }

    // If in place order modal, refresh it
    if (document.getElementById("placeOrderModal")?.classList.contains("show")) {
        openPlaceOrderModal();
    }
}

function increaseQuantity(productId) {
    const quantityElement = document.getElementById(`quantity-${productId}`);
    if (!quantityElement) {
        console.error(`Quantity element for item ${productId} not found!`);
        return;
    }

    let quantity = parseInt(quantityElement.value);
    quantityElement.value = quantity + 1;

    // Update the cart
    const item = cart.find(item => item.productId === productId);
    if (item) {
        item.quantity = quantity + 1;
        localStorage.setItem("cart", JSON.stringify(cart));
        updateCart();
    }
}

function decreaseQuantity(productId) {
    const quantityElement = document.getElementById(`quantity-${productId}`);
    if (!quantityElement) {
        console.error(`Quantity element for item ${productId} not found!`);
        return;
    }

    let quantity = parseInt(quantityElement.value);
    if (quantity > 1) {
        quantityElement.value = quantity - 1;

        // Update the cart
        const item = cart.find(item => item.productId === productId);
        if (item) {
            item.quantity = quantity - 1;
            localStorage.setItem("cart", JSON.stringify(cart));
            updateCart();
        }
    }
}

async function openPlaceOrderModal() {
    console.log("📌 Opening Place Order Modal...");

    const cart = JSON.parse(localStorage.getItem("cart")) || [];
    console.log("📌 Cart Items:", cart);

    if (cart.length === 0) {
        alert("🚨 Your cart is empty!");
        return;
    }

    const distributorId = cart[0].distributorId;
    console.log("📌 Distributor ID:", distributorId);

    const modalBody = document.getElementById("placeOrderModalBody");
    const modalElement = document.getElementById("placeOrderModal");

    if (!modalBody || !modalElement) {
        console.error("Required DOM elements not found: placeOrderModalBody or placeOrderModal");
        alert("Cannot place order. Please try again later.");
        return;
    }

    try {
        const paymentResponse = await fetch(`/api/distributor/${distributorId}/payment-methods`);
        if (!paymentResponse.ok) {
            throw new Error(`Failed to fetch payment methods: HTTP ${paymentResponse.status}`);
        }
        const paymentMethods = await paymentResponse.json();
        console.log("📌 Payment Methods Data:", paymentMethods);

        if (!Array.isArray(paymentMethods)) {
            throw new Error("Expected paymentMethods to be an array, but received: " + JSON.stringify(paymentMethods));
        }

        const addressResponse = await fetch("/api/user/address");
        if (!addressResponse.ok) {
            throw new Error(`Failed to fetch user address: HTTP ${addressResponse.status}`);
        }
        const addressData = await addressResponse.json();
        console.log("📌 Address Data:", addressData);

        const userAddress = addressData.address || "";
        const userAddressElement = document.getElementById("userAddress");
        if (userAddressElement) {
            userAddressElement.value = userAddress;
        }

        const cartItemsHtml = cart.map(item => `
            <div class="d-flex justify-content-between align-items-center border-bottom py-2">
			<img src="${item.image ? '/uploads/' + item.image : 'img/default-product.jpg'}" 
										                 class="card-img-top" 
										                 style="height: 150px; object-fit: cover;" 
										                 alt="${item.name}">                   <span>${item.name}</span>
                <div class="d-flex align-items-center">
                    <button class="btn btn-sm btn-outline-secondary" onclick="updateCartItem(${item.productId}, -1)">-</button>
                    <span id="quantity-${item.productId}" class="mx-2">${item.quantity}</span>
                    <button class="btn btn-sm btn-outline-secondary" onclick="updateCartItem(${item.productId}, 1)">+</button>
                </div>
                <span id="total-price-${item.productId}">$${(item.price * item.quantity).toFixed(2)}</span>
            </div>
        `).join('');

        const paymentOptionsHtml = paymentMethods.map(method => `
            <option value="${method.payMethodId}">${method.payMethodName}</option>
        `).join('');

        modalBody.innerHTML = `
            <h5 class="text-center">Review Your Order</h5>
            <div>${cartItemsHtml}</div>
            <h5 class="mt-3">Total: $<span id="orderTotal">${calculateTotal()}</span></h5>
            <h5 class="mt-3">Select Payment Method</h5>
            <select id="paymentMethod" class="form-control">
                ${paymentOptionsHtml}
            </select>
            <h5 class="mt-3">Shipping Address</h5>
            <textarea id="userAddress" class="form-control">${userAddress}</textarea>
			<textarea id="note" class="form-control" placeholder="Add note for your order..."></textarea>
            <button class="btn btn-primary w-100 mt-3" onclick="realplaceorder()">Checkout</button>
        `;

        console.log("✅ Modal Content Successfully Rendered!");
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    } catch (error) {
        console.error("Error in fetching data for place order modal:", error);
        modalBody.innerHTML = "<p class='text-danger'>Failed to load order details. Please try again later.</p>";
    }
}

function updateCartItem(productId, change) {
    const item = cart.find(item => item.productId === productId);
    if (!item) return;

    item.quantity += change;
    if (item.quantity < 1) {
        cart = cart.filter(i => i.productId !== productId);
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    updateCart();
    if (document.getElementById("placeOrderModal")?.classList.contains("show")) {
        openPlaceOrderModal();
    }
}

function calculateTotal() {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0).toFixed(2);
}

async function realplaceorder() {
    const cartItems = JSON.parse(localStorage.getItem("cart")) || [];
    const paymentMethod = document.getElementById("paymentMethod");
    const userAddress = document.getElementById("userAddress");
    const modalElement = document.getElementById("placeOrderModal");
	const note = document.getElementById("note");

    if (!cartItems.length) {
        alert("🚨 Your cart is empty!");
        return;
    }

    if (!paymentMethod || !userAddress || !modalElement) {
        console.error("Required DOM elements not found for placing order");
        alert("Cannot place order. Please try again later.");
        return;
    }

    const paymentMethodId = paymentMethod.value;
    const customerAddress = userAddress.value;
	const orderNote = note.value;

    try {
        const customerId = await getCustomerId();
        if (!customerId) {
            alert("🚨 Cannot place order. Customer ID is missing.");
            console.log("Cannot place order. Customer ID is missing.");
            return;
        }

        const orderData = {
            customerId,
            orderItems: cartItems.map(item => ({
                distriProduct: { productId: item.productId },
                quantity: item.quantity
            })),
            paymentMethod: { payMethodId: parseInt(paymentMethodId) },
            shippingAddress: customerAddress,
			note: orderNote
        };

        console.log("📌 Order Data Before Sending:", JSON.stringify(orderData, null, 2));

        const response = await fetch("/api/place", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            throw new Error(`Failed to place order: HTTP ${response.status}`);
        }

        const data = await response.json();
        console.log("✅ Order Response:", data);
        alert("✅ Order placed successfully! Waiting for distributor approval.");
        cart = [];
        localStorage.setItem("cart", JSON.stringify(cart));
        updateCart();
        const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        modal.hide();
    } catch (error) {
        console.error("❌ Error placing order:", error);
        alert("❌ Failed to place order. Please try again.");
    }
}

async function getCustomerId() {
    try {
        const response = await fetch("/api/customer/session-user");
        if (!response.ok) {
            throw new Error(`Failed to fetch customer ID: HTTP ${response.status}`);
        }
        const customerId = await response.json();
        console.log("🔹 Retrieved Customer ID:", customerId);
        return customerId;
    } catch (error) {
        console.error("❌ Error fetching customer ID:", error);
        return null;
    }
}

// Distributor Profile
function viewProfile(distributorId) {
    const userProfileModalLabel = document.getElementById("userProfileModalLabel");
    const userProfileModalBody = document.getElementById("userProfileModalBody");
    const modalElement = document.getElementById("userProfileModal");

    if (!userProfileModalLabel || !userProfileModalBody || !modalElement) {
        console.error("Required DOM elements not found: userProfileModalLabel, userProfileModalBody, or userProfileModal");
        alert("Cannot display distributor profile. Please try again later.");
        return;
    }

    fetch(`/api/distributors/${distributorId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(distributor => {
            userProfileModalLabel.textContent = distributor.companyName;
            userProfileModalBody.innerHTML = `
                <img src="${distributor.profileImg || 'img/default-profile.jpg'}" class="img-fluid mb-2" style="height: 150px; border-radius: 50%;" alt="${distributor.companyName}">
                <p><strong>Name:</strong> ${distributor.companyName}</p>
                <p><strong>Email:</strong> ${distributor.email || 'N/A'}</p>
                <p><strong>Phone:</strong> ${distributor.contactInfo || 'N/A'}</p>
                <p><strong>Address:</strong> ${distributor.address || 'N/A'}</p>
            `;
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        })
        .catch(error => {
            console.error("Error fetching distributor profile:", error);
            userProfileModalBody.innerHTML = "<p class='text-danger'>Failed to load distributor profile. Please try again later.</p>";
        });
}

function viewDistributorDetails(distributorId) {
    if (!distributorId) {
        console.error("Distributor ID is missing!");
        return;
    }

    localStorage.setItem('distributorId', distributorId);
    window.location.href = '/customerseedistributor.html';
}

// Utility Functions
function getStars(rating) {
    const fullStar = "★";
    const emptyStar = "☆";
    const maxStars = 5;
    let stars = "";
    for (let i = 1; i <= maxStars; i++) {
        stars += i <= Math.round(rating) ? fullStar : emptyStar;
    }
    return stars;
}