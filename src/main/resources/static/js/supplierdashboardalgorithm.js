// Fetch stats data from Spring Boot backend
function fetchStatsData() {
    const supplierId = document.getElementById('supplierId')?.value;
    if (!supplierId) {
        console.error("❌ Supplier ID is missing.");
        return;
    }

    fetch(`/supplier-dashboard/api/data?supplierId=${supplierId}`)
        .then(response => response.json())
        .then(data => {
            if (!data.stats) return;

            document.getElementById('productsSold').textContent = data.stats.productsSold || 0;
            document.getElementById('netProfit').textContent = `$${data.stats.netProfit || 0}`;
            document.getElementById('customerSatisfaction').textContent = `${data.stats.customerSatisfaction || 0}%`;

            if (data.productSalesData && data.productSalesLabels) {
                initializeBarChart(data.productSalesLabels, data.productSalesData);
            }

            if (data.orderCompletionRate) {
                initializePieChart(data.orderCompletionRate);
            }
        })
        .catch(error => console.error('❌ Error fetching stats data:', error));
}

// Initialize stats on page load
document.addEventListener("DOMContentLoaded", function () {
    fetchStatsData();
});

// Bar Chart for Sales
function initializeBarChart(labels, data) {
    const ctx = document.getElementById("productSalesChart")?.getContext("2d");
    if (!ctx) {
        console.error("❌ Chart canvas not found.");
        return;
    }

    new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Product Sales",
                data: data,
                backgroundColor: "rgba(54, 162, 235, 0.5)",
                borderColor: "rgba(54, 162, 235, 1)",
                borderWidth: 1
            }]
        },
        options: { responsive: true }
    });
}

// Pie Chart for Order Completion
function initializePieChart(data) {
    const ctx = document.getElementById("orderCompletionChart")?.getContext("2d");
    if (!ctx) {
        console.error("❌ Pie chart canvas not found.");
        return;
    }

    new Chart(ctx, {
        type: "pie",
        data: {
            labels: ["Completed Orders", "Pending Orders"],
            datasets: [{
                label: "Order Completion Rate",
                data: [data["COMPLETED"] || 0, data["PENDING"] || 0],
                backgroundColor: ["#36A2EB", "#FF6384"]
            }]
        },
        options: { responsive: true }
    });
}
