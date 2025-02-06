document.addEventListener("DOMContentLoaded", function () {
  const emailSelect = document.getElementById("email_1");
  const emailBackDiv = document.getElementById("email_back");
  
  let customInput = null; // 직접 입력할 input 요소

  emailSelect.addEventListener("change", function () {
      if (emailSelect.value === "text") {
          // 기존 select 숨기기
          emailSelect.style.display = "none";
          
          // 새로운 input 생성
          customInput = document.createElement("input");
          customInput.type = "text";
          customInput.name = "email[1]";
          customInput.className = "n-ipt";
          customInput.required = true;
          customInput.placeholder = "직접 입력";
          
          // input을 back 영역에 추가
          emailBackDiv.appendChild(customInput);
          
          // 포커스 이동
          customInput.focus();
      }
  });

  document.querySelector(".email-box").addEventListener("input", function () {
      const frontValue = document.querySelector("input[name='email[0]']").value;
      const backValue = customInput ? customInput.value : emailSelect.value;
      
      if (frontValue && backValue) {
          console.log(`완성된 이메일: ${frontValue}@${backValue}`); // 여기에 실제 값을 할당하는 로직 추가 가능
      }
  });
});
