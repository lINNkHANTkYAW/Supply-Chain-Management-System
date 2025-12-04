document.addEventListener("DOMContentLoaded", function() {
    fetch("http://localhost:8080/api/transactions/customer", { credentials: "include" })
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch transactions");
            return response.json();
        })
        .then(data => {
            console.log("Fetched transactions:", data);
            const tableBody = document.getElementById("transactionsTable");
            tableBody.innerHTML = "";

            data.forEach(transaction => {
                const row = `<tr>
                    <td>${transaction.transactionId}</td>
                    <td>${transaction.cusOrder.orderId}</td>
                    <td>${transaction.distributor.companyName}</td>
                    <td>${transaction.status}</td>
                    <td>${transaction.rating}</td>
                </tr>`;
                tableBody.innerHTML += row;
            });
        })
        .catch(error => console.error("Error fetching transactions:", error));
});
