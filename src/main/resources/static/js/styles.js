// category-bar

document.addEventListener("DOMContentLoaded", () => {
  const categoryBar = document.querySelector(".category-bar");
  const categoryButton = document.querySelector(".category-button");

  // category bar toggle
  categoryButton.addEventListener("click", () => {
    categoryBar.classList.toggle("show");
  });

  // 메뉴 외부 클릭 시 메뉴 닫기
  document.addEventListener("click", (event) => {
    if (!categoryBar.contains(event.target) && !categoryButton.contains(event.target)) {
      categoryBar.classList.remove("show");
    }
  });

  // 카테고리 hover 처리
  const mainCategories = document.querySelectorAll(".mc div");
  const subCategories = document.querySelectorAll(".sc div");

  mainCategories.forEach((mainCategory, index) => {
    mainCategory.addEventListener("mouseenter", () => {
      // 모든 하위 카테고리를 숨김
      subCategories.forEach((subCategory) => subCategory.classList.remove("active"));
      
      // 현재 인덱스에 해당하는 하위 카테고리만 표시
      if (subCategories[index]) {
        subCategories[index].classList.add("active");
      }
    });
    
//     mainCategory.addEventListener("mouseleave", () => {
//       // 마우스가 빠져나가면 서브 카테고리 숨김
//       subCategories.forEach((subCategory) => subCategory.classList.remove("active"));
//     });
//   });
// });
  

  // 카테고리 전체에서 마우스가 벗어났을 때만 숨김
  categoryBar.addEventListener("mouseleave", () => {
    subCategories.forEach((subCategory) => subCategory.classList.remove("active"));
  });});});



document.addEventListener("DOMContentLoaded", () => {
  const mainCategories = document.querySelectorAll(".mc li");
  const subCategories = document.querySelectorAll(".sc");

  mainCategories.forEach((mainCategory, index) => {
    mainCategory.addEventListener("mouseenter", () => {
      // 모든 하위 카테고리를 숨김
      subCategories.forEach((subCategory) => subCategory.classList.remove("active"));
      
      // 현재 인덱스에 해당하는 하위 카테고리만 표시
      if (subCategories[index]) {
        subCategories[index].classList.add("active");
      }
    });

    mainCategory.addEventListener("mouseleave", () => {
      // 마우스가 빠져나가면 서브 카테고리 숨김
      subCategories.forEach((subCategory) => subCategory.classList.remove("active"));
    });
  });


    

  /*

  // 사이드바 열기
  toggleBtn.addEventListener("click", () => {
    sidebar.classList.add("show");
  });

  // 사이드바 닫기
  sidebarToggle.addEventListener("click", () => {
    sidebar.classList.remove("show");
  });


*/
 
});

// info-bar

document.addEventListener("DOMContentLoaded", () => {
  const infoButton = document.querySelector(".info-button"); // 오른쪽 상단 버튼
  const infoBar = document.querySelector(".info-bar"); // 오른쪽 메뉴

  // 버튼 클릭 시 슬라이드 메뉴 표시/숨김 토글
  infoButton.addEventListener("click", () => {
    infoBar.classList.toggle("show");
  });

  // 메뉴 외부 클릭 시 메뉴 닫기
  document.addEventListener("click", (event) => {
    if (!infoBar.contains(event.target) && !infoButton.contains(event.target)) {
      infoBar.classList.remove("show");
    }
  });
});



// from controller

document.getElementById('validationForm').addEventListener('submit', function (event) {
  event.preventDefault(); // 폼 제출 방지
  const form = event.target; // 현재 폼
  
  // 모든 입력 필드에 대해 유효성 검사 수행
  Array.from(form.elements).forEach((element) => {
    if (element.tagName === 'INPUT') {
      validateField(element);
    }
  });
});

// 필드 유효성 검사 함수
function validateField(field) {
  const isValid = field.checkValidity(); // 필드 유효성 확인
  if (isValid) {
    field.classList.remove('is-invalid'); // 유효하지 않은 클래스 제거
    field.classList.add('is-valid'); // 유효한 클래스 추가
  } else {
    field.classList.remove('is-valid'); // 유효한 클래스 제거
    field.classList.add('is-invalid'); // 유효하지 않은 클래스 추가
  }
}

// form 리셋

document.getElementById('resetButton').addEventListener('click', function () {
  const form = document.getElementById('validationForm');
  form.reset(); // 폼 초기화
  Array.from(form.elements).forEach((element) => {
    element.classList.remove('is-valid', 'is-invalid');
  });
});


// swiper

const swiper = new Swiper('.swiper', {
  // Optional parameters
  direction: 'vertical',
  loop: true,



  // And if we need scrollbar
  scrollbar: {
    el: '.swiper-scrollbar',
  },
});

