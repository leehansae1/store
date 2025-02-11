document.addEventListener("DOMContentLoaded", () => {
  // Elements
  const categoryBar = document.querySelector(".category-bar");
  const categoryButton = document.querySelector(".category-button");
  const infoButton = document.querySelector(".info-button");
  const infoBar = document.querySelector(".info-bar");
  const validationForm = document.getElementById("validationForm");
  const resetButton = document.getElementById("resetButton");
  const uploadBox = document.getElementById("upload-box");
  const inputFile = document.getElementById("input-file");

  const storeNavItems = document.querySelectorAll(".store-nav div");
  const storeContents = document.getElementById("store-contents");
  
  // Category bar toggle (menu)
  categoryButton.addEventListener("click", () => {
    categoryBar.classList.toggle("show");
  });

  // Close category menu if clicked outside
  document.addEventListener("click", (event) => {
    if (!categoryBar.contains(event.target) && !categoryButton.contains(event.target)) {
      categoryBar.classList.remove("show");
    }
  });

  // Category hover handling (showing subcategories)
  const mainCategories = document.querySelectorAll(".mc div");
  const subCategories = document.querySelectorAll(".subcategory");

  mainCategories.forEach((mainCategory, index) => {
    mainCategory.addEventListener("mouseenter", () => {
      // Hide all subcategories first
      subCategories.forEach(subCategory => subCategory.classList.remove("active"));
      
      // Show the corresponding subcategory based on index
      if (subCategories[index]) {
        subCategories[index].classList.add("active");
      }
    });
  });

  // Hide subcategories when mouse leaves the category bar
  categoryBar.addEventListener("mouseleave", () => {
    subCategories.forEach(subCategory => subCategory.classList.remove("active"));
  });

  // Info bar toggle (right menu)
  infoButton.addEventListener("click", () => {
    infoBar.classList.toggle("show");
  });

  // Close info bar if clicked outside
  document.addEventListener("click", (event) => {
    if (!infoBar.contains(event.target) && !infoButton.contains(event.target)) {
      infoBar.classList.remove("show");
    }
  });

  // Form validation
  if (validationForm) {
    validationForm.addEventListener('submit', (event) => {
      event.preventDefault(); // Prevent form submission
      Array.from(validationForm.elements).forEach((element) => {
        if (element.tagName === 'INPUT') {
          validateField(element);
        }
      });
    });
  }

  // Field validation function
  function validateField(field) {
    const isValid = field.checkValidity();
    field.classList.toggle('is-valid', isValid);
    field.classList.toggle('is-invalid', !isValid);
  }

  // Form reset functionality
  if (resetButton) {
    resetButton.addEventListener('click', () => {
      validationForm.reset(); // Reset form
      Array.from(validationForm.elements).forEach((element) => {
        element.classList.remove('is-valid', 'is-invalid');
      });
    });
  }

  // Swiper initialization
  if (document.querySelector('.swiper')) {
    new Swiper('.swiper', {
      direction: 'vertical',
      loop: true,
      scrollbar: {
        el: '.swiper-scrollbar',
      },
    });
  }

 // File input click functionality
if (uploadBox && inputFile) {
  uploadBox.addEventListener("click", () => {
    inputFile.click();
  });

  // Handle file selection and preview
  inputFile.addEventListener("change", (event) => {
    const files = event.target.files;
    const previewBox = document.getElementById("preview-box"); 

    if (!previewBox) {
      console.error("no preview container!");
      return;
    }

    if (files.length > 0) {
      Array.from(files).forEach((file) => {
        const reader = new FileReader();

        reader.onload = (e) => {
          
          const previewDiv = document.createElement("div");
          previewDiv.classList.add("preview-item");
          
          const imgElement = document.createElement("img");
          imgElement.src = e.target.result;
          imgElement.classList.add("preview-image");

           // delete button
          const deleteBtn = document.createElement("button");
          deleteBtn.innerHTML = "❌";
          deleteBtn.classList.add("delete-btn");

           // delete with div and form
          deleteBtn.addEventListener("click", () => {
            previewDiv.remove(); // div
            removeFileFromForm(file); // form
          });

          // Create a new div for the preview
          previewDiv.appendChild(imgElement);
          previewDiv.appendChild(deleteBtn);
          previewBox.appendChild(previewDiv);
        };

        reader.readAsDataURL(file);
      });

      inputFile.value = "";
    }
  });
}

// form delete function
function removeFileFromForm(fileToRemove) {
  const dt = new DataTransfer();
  const files = inputFile.files;

  Array.from(files).forEach((file) => {
    if (file !== fileToRemove) {
      dt.items.add(file);
    }
  });

  inputFile.files = dt.files;
}


 

 
});
