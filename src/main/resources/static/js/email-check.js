
        let isAuth = false;
        let isIdchecked = false;
      
        const btnDuplicateId = document.querySelector("#btn-duplicate-id"); // 아이디 중복 확인
        const btnSendEmail = document.querySelector("#btn-send-email"); // 인증번호 받기 버튼
        const btnEmailConfirm = document.querySelector("#btn-email-confirm"); // 이메일 인증 버튼
        const emailConfirm = document.querySelector("#email-confirm"); // 인증번호 입력 필드 영역
        const userEmail = document.querySelector("#user-email"); // 이메일 입력 필드
        const userEmailConfirm = document.querySelector("#user-email-confirm"); // 이메일 인증번호
        const btnSignUp = document.querySelector("#btn-sign-up"); // 회원가입 버튼
      
        // 1. 아이디 중복 확인
        btnDuplicateId.addEventListener("click", (e) => {
          e.preventDefault();
          const userId = userInput.value.trim();
          if (userId === "") {
            alert("아이디를 입력해주세요.");
            return;
          }
          fetch("/member/check-duplicate", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ userId: userId }),
          })
            .then((response) => response.json())
            .then((isDuplicate) => {
              if (isDuplicate) {
                alert("이미 사용 중인 아이디입니다.");
                isIdchecked = false;
              } else {
                alert("사용 가능한 아이디입니다.");
                isDuplicate = true;
              }
            })
            .catch((error) => {
              console.error("에러 발생:", error);
              alert("서버 오류가 발생했습니다");
            });
        });
      
        // 2.이메일 인증번호 요청
        btnSendEmail.addEventListener("click", (e) => {
          e.preventDefault();
          if (userEmail.value.trim() === "") {
            // 공백이 있으면 실패할 수 있으므로 트림으로 처리
            alert("이메일을 확인해주세요.");
            return;
          }
      
          const sendData = { email: userEmail.value };
      
          console.log(sendData);
          fetch("../mail/confirm", {
            method: "POST",
            headers: {
              "Content-Type": "application/json", // JSON 형식으로 데이터 전송
            },
            body: JSON.stringify(sendData), // 이메일을 JSON으로 변환하여 전송
          })
            .then((response) => response.json()) // 응답을 JSON으로 변환
            .then((json) => {
              alert("인증메일이 발송되었습니다.");
              btnEmailConfirm.addEventListener("click", (e) => {
                e.preventDefault();
      
                if (userEmailConfirm.value.trim() === json.confirmNumber) {
                  alert("인증되었습니다.");
                  isAuth = true;
                }
              });
              console.log(json);
            });
          emailConfirm.classList.remove("blind");
        });
        // 3. 회원사입 버튼 클릭 시 이메일 인증 여부 확인
        btnSignUp.addEventListener("click", (e) => {
          if (!isAuth) {
            e.preventDefault(); // 이메일 인증이 안됐을 시 폼 제출 방지
            alert("이메일 인증이 완료되지 않았습니다.");
          }
        });
      