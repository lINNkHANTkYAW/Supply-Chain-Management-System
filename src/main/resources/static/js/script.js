document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("container");
    const registerBtn = document.getElementById("register");
    const loginBtn = document.getElementById("login");

    // Show the correct form on page load based on URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const formType = urlParams.get("form");
    container.classList.toggle("active", formType === "signup");

    // Toggle between signup and login
    registerBtn.addEventListener("click", () => container.classList.add("active"));
    loginBtn.addEventListener("click", () => container.classList.remove("active"));
	
	

    /** --------------------------
     * ROLE SELECTION (SIGNUP & LOGIN)
     * -------------------------- */
    const roleButtons = document.querySelectorAll(".role-btn");
    const selectedRoleSignup = document.getElementById("selectedRoleSignup");
    const selectedRoleLogin = document.getElementById("selectedRole");

    roleButtons.forEach(button => {
        button.addEventListener("click", () => {
            roleButtons.forEach(btn => btn.classList.remove("selected")); // Remove selection from all
            button.classList.add("selected"); // Highlight the selected role
            selectedRoleSignup.value = button.dataset.role; // Set hidden input for signup
            selectedRoleLogin.value = button.dataset.role; // Set hidden input for login
        });
    });

    /** --------------------------
     * OPEN MODAL FOR ROLE-SPECIFIC SIGNUP
     * -------------------------- */
    document.querySelectorAll("#supplierBtn, #manufacturerBtn, #distributorBtn, #customerBtn").forEach(button => {
        button.addEventListener("click", (event) => {
            event.preventDefault();
            openModal(button.innerText.trim());
        });
    });

    function openModal(role) {
        document.getElementById("modalTitle").innerText = `${role} Information`;
        selectedRoleSignup.value = role; // Ensure role is set

        let extraFields = document.getElementById("extraFields");
        extraFields.innerHTML = ""; // Clear previous fields

        let additionalInputs = "";
        if (role === "Supplier" || role === "Manufacturer" || role === "Distributor") {
            additionalInputs = `
                <label for="company_name" class="form-label">Company Name</label>
                <input type="text" class="form-control" id="company_name" name="company_name" placeholder="Enter company name">
                <div class="invalid-feedback">Please enter the company name.</div>`;
        } else if (role === "Customer") {
            additionalInputs = `
                <label for="name" class="form-label">Username</label>
                <input type="text" class="form-control" id="name" name="name" placeholder="Enter username">
                <div class="invalid-feedback">Please enter username.</div>`;
        }

        extraFields.innerHTML = additionalInputs;
        new bootstrap.Modal(document.getElementById("roleModal")).show(); // Open modal
    }

    /** --------------------------
     * FORM VALIDATION
     * -------------------------- */
    function validateField(id) {
        let field = document.getElementById(id);
        if (!field || field.value.trim() === "") {
            field.classList.add("is-invalid");
            return false;
        } else {
            field.classList.remove("is-invalid");
            return true;
        }
    }

    function validateEmail(id) {
        let field = document.getElementById(id);
        let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!field || !emailPattern.test(field.value)) {
            field.classList.add("is-invalid");
            return false;
        } else {
            field.classList.remove("is-invalid");
            return true;
        }
    }

    function validatePassword(id) {
        let field = document.getElementById(id);
        const password = field.value;
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*_|])[A-Za-z\d!@#$%^&*_|]{8,}$/;
        if (!regex.test(password)) {
            field.classList.add("is-invalid");
            return false;
        } else {
            field.classList.remove("is-invalid");
            return true;
        }
    }
	
	function validateContactInfo(id) {
	    let field = document.getElementById(id);
	    const contactInfo = field.value;
	    const regex = /^\+959\d{7,9}$/; // Matches +959 followed by 7-9 digits
	    if (!regex.test(contactInfo)) {
	        field.classList.add("is-invalid");
	        return false;
	    } else {
	        field.classList.remove("is-invalid");
	        return true;
	    }
	}

    function validateForm() {
        let isValid = true;
        // isValid &= validateField("contact_info");
        isValid &= validateField("address");
        isValid &= validatePassword("password");
        isValid &= validateEmail("email");
		isValid &= validateContactInfo("contact_info");

        let role = document.getElementById("modalTitle").innerText.split(" ")[0];
        if (role === "Supplier" || role === "Manufacturer" || role === "Distributor") {
            isValid &= validateField("company_name");
        } else if (role === "Customer") {
            isValid &= validateField("name");
        }

        return isValid;
    }

    /** --------------------------
     * SIGNUP FORM SUBMISSION
     * -------------------------- */
    document.getElementById("submitBtn").addEventListener("click", function () {
        if (validateForm()) {
            document.getElementById("signupForm").submit();
        } else {
            alert("Please fill in all required fields.");
        }
    });

    /** --------------------------
     * LOGIN FORM SUBMISSION
     * -------------------------- */
    document.getElementById("loginForm").addEventListener("submit", (event) => {
        if (!selectedRoleLogin.value) {
            event.preventDefault();
            alert("Please select a role before logging in.");
        }
    });
});
