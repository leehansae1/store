function sample4_execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      const roadAddr = data.roadAddress || ""; // 도로명 주소
      const jibunAddr = data.jibunAddress || ""; // 지번 주소
      let extraRoadAddr = ""; // 참고 항목

      // -----------------------------
      // 1) 동·가로 끝나는 법정동 명 처리
      // -----------------------------
      if (data.bname?.match(/[동|가]$/)) {
        extraRoadAddr += data.bname;
      }

      if (data.buildingName && data.apartment === "Y") {
        extraRoadAddr += extraRoadAddr ? `, ${data.buildingName}` : data.buildingName;
      }

      if (extraRoadAddr) {
        extraRoadAddr = ` (${extraRoadAddr})`;
      }

      // -----------------------------
      // 2) "뒷주소" (건물번호 등) 제거 처리 추가
      // -----------------------------
      const cleanRoadAddr = roadAddr.replace(/\s+\d+.*$/, ""); // 도로명 주소에서 숫자로 시작하는 부분 제거
      const cleanJibunAddr = jibunAddr.replace(/\s+\d+.*$/, ""); // 지번 주소에서도 같은 처리

      // -----------------------------
      // 3) 화면에 최종 대입
      // -----------------------------
      document.querySelector("#sample4_postcode").value = data.zonecode;
      document.querySelector("#sample4_roadAddress").value = cleanRoadAddr;
      document.querySelector("#sample4_jibunAddress").value = cleanJibunAddr;
      document.querySelector("#sample4_extraAddress").value = cleanRoadAddr ? extraRoadAddr : "";

      const guideTextBox = document.querySelector("#guide");

      if (data.autoRoadAddress) {
        guideTextBox.textContent = `(예상 도로명 주소: ${data.autoRoadAddress}${extraRoadAddr})`;
        guideTextBox.style.display = "block";
      } else if (data.autoJibunAddress) {
        guideTextBox.textContent = `(예상 지번 주소: ${data.autoJibunAddress})`;
        guideTextBox.style.display = "block";
      } else {
        guideTextBox.textContent = "";
        guideTextBox.style.display = "none";
      }
    },
  }).open();
}
