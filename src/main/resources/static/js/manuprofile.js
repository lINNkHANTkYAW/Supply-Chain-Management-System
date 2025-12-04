document.addEventListener("DOMContentLoaded", function () {
    const changeProfileImageButton = document.getElementById("changeProfileImageButton");
    const uploadProfileImageInput = document.getElementById("uploadProfileImage");
    const profileImage = document.getElementById("profileImage");
	const profileImage2 = document.getElementById("mainProfileImage");
	

    // Open file input when button is clicked
    changeProfileImageButton.addEventListener("click", function () {
        uploadProfileImageInput.click();
    });

    // Handle image selection and upload
    uploadProfileImageInput.addEventListener("change", function (event) {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        fetch("/manu/api/profile/upload/profile", {
            method: "POST",
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to upload profile image");
            }
            return response.text();
        })
        .then(fileName => {
            fileName = fileName.trim(); // Ensure no whitespace issues
            alert("Profile image updated successfully!");

            // ✅ Update image with cache busting to reflect the latest upload
            profileImage.src = `/img/${fileName}?timestamp=${new Date().getTime()}`;
			profileImage2.src = `/img/${fileName}?timestamp=${new Date().getTime()}`;
        })
        .catch(error => {
            console.error("Error uploading profile image:", error);
            alert("Failed to upload profile image.");
        });
    });
});

				
    function uploadProfileImage(file) {
        const formData = new FormData();
        formData.append("file", file);

        fetch("/manu/api/profile/upload/profile", {
            method: "POST",
            body: formData
        })
        .then(response => response.text())
        .then(data => {
            alert("Profile image updated successfully!");
            console.log(data);
        })
        .catch(error => {
            console.error("Error uploading profile image:", error);
            alert("Failed to upload profile image.");
        });
    }
	
	document.addEventListener("DOMContentLoaded", function () {
	    const editButton = document.getElementById("editButton");
	    const profileForm = document.getElementById("profileForm");
	    const profileDisplay = document.getElementById("profileDisplay");

	    // Input fields
	    const username = document.getElementById("username");
	    const phone= document.getElementById("phone");
	    const companyName = document.getElementById("companyName");
	    const address = document.getElementById("address");
	    const bio = document.getElementById("bio");

	    // Display fields
	    const displayName = document.getElementById("displayName");
	    const displayPhone = document.getElementById("displayPhone");
	    const displayCompanyName = document.getElementById("displayCompanyName");
	    const displayAddress = document.getElementById("displayAddress");
	    const displayBio = document.getElementById("displayBio");

	    // Toggle Edit Mode
	    window.toggleEditMode = function () {
	        if (profileForm.classList.contains("d-none")) {
	            profileForm.classList.remove("d-none");
	            profileDisplay.classList.add("d-none");
	            editButton.textContent = "View";
	        } else {
	            profileForm.classList.add("d-none");
	            profileDisplay.classList.remove("d-none");
	            editButton.textContent = "Edit";
	        }
	    };

	    // Handle Form Submission
	    profileForm.addEventListener("submit", function (event) {
	        event.preventDefault(); // Prevent default form submission

	        const formData = new FormData(profileForm);

	        fetch("/manu/profile/update", {
	            method: "POST",
	            body: formData
	        })
	        .then(response => {
	            if (!response.ok) {
	                throw new Error("Failed to update profile");
	            }
	            return response.json();
	        })
	        .then(data => {
	            alert("Profile updated successfully!");

	            // ✅ Update displayed values
	            displayName.textContent = data.username;
	            displayPhone.textContent = data.contactInfo;
	            displayCompanyName.textContent = data.companyName;
	            displayAddress.textContent = data.address;
	            displayBio.textContent = data.bio;

	            // ✅ Hide form and switch to display mode
	            toggleEditMode();
	        })
	        .catch(error => {
	            console.error("Error updating profile:", error);
	            alert("Failed to update profile. Please try again.");
	        });
	    });
	});



