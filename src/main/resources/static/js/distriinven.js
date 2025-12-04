document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchInput");

    // Search functionality
    if (searchInput) {
        searchInput.addEventListener("input", function(e) {
            const searchTerm = e.target.value.toLowerCase().trim();
            const inventoryCards = document.querySelectorAll("#inventoryItemsList .card");
            inventoryCards.forEach(card => {
                const name = card.querySelector(".card-title").textContent.toLowerCase().trim();
                card.parentElement.style.display = name.includes(searchTerm) ? "block" : "none";
            });
        });
    }

	const modals = document.querySelectorAll('.modal');
	    modals.forEach(modal => {
	        modal.addEventListener('hidden.bs.modal', function() {
	            const form = modal.querySelector('form');
	            if (form) {
	                form.reset();
	                form.action = '/distributor-inventory/save-marketplace';
	                document.getElementById('formMode').value = 'add';
	                const nameField = document.querySelector('.name-field');
	                if (nameField.tagName === 'INPUT') {
	                    const select = document.createElement('select');
	                    select.className = 'form-control name-field';
	                    select.id = 'name';
	                    select.name = 'name';
	                    select.required = true;
	                    select.innerHTML = '<option value="">Select Item</option>' + 
	                        Array.from(document.querySelectorAll('#name option:not(:first-child)'))
	                            .map(opt => `<option value="${opt.value}">${opt.text}</option>`)
	                            .join('');
	                    nameField.parentNode.replaceChild(select, nameField);
	                }
	            }
	        });
	    });
});

function editItem(itemId) {
    fetch(`/api/distributor-inventory/marketplace/${itemId}`)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
            return response.json();
        })
        .then(item => {
            const form = document.getElementById('marketplaceForm');
            form.action = `/distributor-inventory/update-marketplace/${itemId}`;
            document.getElementById('formMode').value = 'edit';

            // Replace select with readonly input for name
            const nameSelect = document.getElementById('name');
            const nameInput = document.createElement('input');
            nameInput.type = 'text';
            nameInput.className = 'form-control name-field';
            nameInput.id = 'name';
            nameInput.name = 'name';
            nameInput.value = item.name || '';
            nameInput.readOnly = true;
            nameSelect.parentNode.replaceChild(nameInput, nameSelect);

            document.getElementById('category').value = item.category.categoryId || '';
            document.getElementById('price').value = item.price || '';
            document.getElementById('description').value = item.description || '';

            const modal = new bootstrap.Modal(document.getElementById('addInventoryModal'));
            modal.show();
        })
        .catch(error => console.error('Error fetching item details:', error));
}