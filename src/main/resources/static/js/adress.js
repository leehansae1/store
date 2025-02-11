
//본 예제에서는 도로명 주소 표기 방식에 대한 법령에 따라,
//내려오는 데이터를 조합하여 올바른 주소를 구성하는 방법을 설명합니다.
function sample4_execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      // 팝업에서 검색결과 항목을 클릭했을 때 실행할 코드를 작성하는 부분.
      var roadAddr = data.roadAddress; // 도로명 주소 변수
      var extraRoadAddr = ""; // 참고 항목 변수

      // -----------------------------
      // 1) 동·가로 끝나는 법정동 명 처리 (기존 로직)
      // -----------------------------
      if (data.bname !== "" && /[동|가]$/g.test(data.bname)) {
        extraRoadAddr += data.bname;
      }
      if (data.buildingName !== "" && data.apartment === "Y") {
        extraRoadAddr +=
          extraRoadAddr !== ""
            ? ", " + data.buildingName
            : data.buildingName;
      }
      if (extraRoadAddr !== "") {
        extraRoadAddr = " (" + extraRoadAddr + ")";
      }

      // -----------------------------
      // 2) "뒷주소" (건물번호 등) 제거 처리 추가
      // -----------------------------
      if (roadAddr) {
        // 공백+숫자로 시작하는 빌딩번호 이하 제거
        roadAddr = roadAddr.replace(/\s+\d+.*$/, "");
      }

      // 만약 지번주소에서도 유사 처리 원한다면
      var jibunAddr = data.jibunAddress;
      if (jibunAddr) {
        jibunAddr = jibunAddr.replace(/\s+\d+.*$/, "");
      }

      // -----------------------------
      // 3) 화면에 최종 대입
      // -----------------------------
      document.getElementById("sample4_postcode").value = data.zonecode;
      document.getElementById("sample4_roadAddress").value = roadAddr;
      document.getElementById("sample4_jibunAddress").value = jibunAddr;

      if (roadAddr !== "") {
        document.getElementById("sample4_extraAddress").value =
          extraRoadAddr;
      } else {
        document.getElementById("sample4_extraAddress").value = "";
      }

      var guideTextBox = document.getElementById("guide");

      if (data.autoRoadAddress) {
        // 예상 도로명 주소
        var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
        guideTextBox.innerHTML =
          "(예상 도로명 주소 : " + expRoadAddr + ")";
        guideTextBox.style.display = "block";
      } else if (data.autoJibunAddress) {
        // 예상 지번 주소
        var expJibunAddr = data.autoJibunAddress;
        guideTextBox.innerHTML =
          "(예상 지번 주소 : " + expJibunAddr + ")";
        guideTextBox.style.display = "block";
      } else {
        guideTextBox.innerHTML = "";
        guideTextBox.style.display = "none";
      }
    },
  }).open();
}
