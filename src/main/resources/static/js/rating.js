let currentOrderId = null;

async function fetchNotifications() {
    try {
        const response = await fetch("http://localhost:8080/api/ratings/notifications");
        if (!response.ok) {
            if (response.status === 401) {
                alert("Please log in to view notifications.");
                return;
            }
            throw new Error("Failed to fetch notifications");
        }
        const notifications = await response.json();
        renderNotifications(notifications);
    } catch (error) {
        console.error("Error fetching notifications:", error);
    }
}

function renderNotifications(notifications) {
    const wrapper = document.querySelector("#notificationWrapper .list-group");
    wrapper.innerHTML = notifications
        .map(
            (noti) => `
            <a href="#" class="list-group-item list-group-item-action" onclick="openRatingModal('${noti.cusOrder.orderId}', '${noti.orderTitle}')">
                Rate your recent order #${noti.cusOrder.orderId}
            </a>
            `
        )
        .join("");
}

function openRatingModal(orderId, orderTitle) {
    currentOrderId = orderId;
    const modalTitle = document.getElementById("modalTitle");
    modalTitle.textContent = `Rate Order #${orderId}`;
    new bootstrap.Modal(document.getElementById("ratingModal")).show();
}

function rate(stars) {
    const starElements = document.querySelectorAll(".rating-stars span");
    starElements.forEach((star, index) => {
        star.classList.toggle("active", index < stars);
    });
}

async function submitRating() {
    const selectedStars = document.querySelectorAll(".rating-stars span.active").length;
    try {
        const response = await fetch("http://localhost:8080/api/ratings/submit", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ orderId: currentOrderId, rating: selectedStars })
        });
        if (!response.ok) {
            if (response.status === 400) {
                alert("Invalid rating or order already rated.");
            } else if (response.status === 401) {
                alert("Please log in to submit a rating.");
            } else {
                alert("Failed to submit rating.");
            }
            return;
        }
        alert(`You rated Order #${currentOrderId} with ${selectedStars} stars!`);
        fetchNotifications();
    } catch (error) {
        console.error("Error submitting rating:", error);
    }
    bootstrap.Modal.getInstance(document.getElementById("ratingModal")).hide();
}

function ignoreRating() {
    alert(`You ignored rating for Order #${currentOrderId}`);
    bootstrap.Modal.getInstance(document.getElementById("ratingModal")).hide();
}

function toggleNotifications() {
    const wrapper = document.getElementById("notificationWrapper");
    wrapper.style.display = wrapper.style.display === "block" ? "none" : "block";
}

fetchNotifications();