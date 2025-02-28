// todo Daum 우편번호 API
function DaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            console.log(data);
            const addrInput = document.querySelector("#address");
            if (data.bname1 === "") addrInput.value = `${data.sido} ${data.sigungu} ${data.bname}`;
            else addrInput.value = `${data.sido} ${data.sigungu} ${data.bname1}`;
        }
    }).open();
}

document.querySelector("#btn-address").addEventListener("click", e => {
    e.preventDefault();
    DaumPostcode();
});
// todo < 전화번호 >
// 입력 시 자동으로 '-' 추가
const phoneInput = document.querySelector("#tel");
phoneInput.addEventListener("input", () => {
    let value = phoneInput.value.replace(/\D/g, ""); // 숫자만 남기기
    // 하이픈 자동 추가 (010-xxxx-xxxx 형태)
    if (value.length >= 4 && value.length <= 7) {
        value = `${value.slice(0, 3)}-${value.slice(3)}`;
    } else if (value.length >= 8) {
        value = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7, 11)}`;
    }
    phoneInput.value = value; // 변환된 값 적용
});
// todo < 관리자 (for MEMBER - ROLE) >
const adminToggle = document.querySelector("#adminToggle"); // 관리자 토글 스위치 (checkbox)
const adminAuthSection = document.querySelector("#adminAuthSection"); // 관리자 인증 영역 (기본 숨김)

const adminAnswerInput = document.querySelector("#adminAnswer"); // 관리자 인증 질문 답변 입력란
const btnAdminVerify = document.querySelector("#btn-admin-verify"); // 관리자 인증 확인 버튼
const adminAuthMsg = document.querySelector("#adminAuthMsg"); // 관리자 인증 상태 메시지
const userRoleField = document.querySelector("#userRole"); // hidden input, 폼 데이터 제출용

let isAdminVerified = false; // 관리자 인증 여부 >> 폼 제출 시 사용

// 관리자 토글 이벤트: 체크되면 관리자 인증 영역을 보임, 해제되면 숨김 처리 및 기본 ROLE_USER로 설정
adminToggle.addEventListener("change", function () {
    if (this.checked) adminAuthSection.classList.remove("blind");
    else {
        adminAuthSection.classList.add("blind");
        //관리자 인증 했다가 일반 회원으로 바꿀 시 유효함
        isAdminVerified = false;
        userRoleField.value = "ROLE_USER";
        // 상태 메시지, 답변 인풋 초기화
        adminAuthMsg.textContent = "답변을 입력해주세요.";
        adminAuthMsg.classList.remove("invalid-feedback");
        adminAuthMsg.classList.remove("valid-feedback");
        adminAnswerInput.value = "";
        adminAnswerInput.classList.remove("is-invalid");
        adminAnswerInput.classList.remove("is-valid");
    }
});

// 마지막으로 인증된 답변 저장
let lastAdminAnswer = "";
adminAnswerInput.addEventListener("beforeinput", () => {
    if (isAdminVerified) lastAdminAnswer = adminAnswerInput.value; // 현재 값을 저장
});

// 실시간 인풋 감지 (답변란을 채웠느냐 채우지 않았느냐)
adminAnswerInput.addEventListener("input", (e) => {
    if (isAdminVerified) {
        const userConfirmed = confirm("답변을 수정하면 관리자 인증을 다시 받아야 합니다. 계속하시겠습니까?");
        if (!userConfirmed) {
            adminAnswerInput.value = lastAdminAnswer; // 취소 시 원래 값으로 복구
            return;
        } else isAdminVerified = false;
    }

    const answer = adminAnswerInput.value.trim();
    if (answer.length > 0) {
        adminAuthMsg.textContent = "관리자 인증이 필요합니다.";
        adminAnswerInput.classList.remove("is-valid");
        adminAuthMsg.classList.remove("valid-feedback");
        btnAdminVerify.disabled = false;
    } else {
        adminAuthMsg.textContent = "답변을 입력해주세요.";
        adminAnswerInput.classList.remove("is-valid");
        adminAuthMsg.classList.remove("valid-feedback");
        btnAdminVerify.disabled = true;
    }
});

// 관리자 인증 확인 버튼: AJAX로 백엔드에 입력한 답변을 검증
btnAdminVerify.addEventListener("click", function (e) {
    e.preventDefault();
    const answer = adminAnswerInput.value.trim();

    // AJAX 요청: 관리자 인증 답변 검증 (예: /member/admin/verify)
    fetch("/member/admin/verify", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({answer: answer})
    })
        .then(response => response.json())
        .then(data => {
            if (data.verified) {
                adminAuthMsg.textContent = "관리자 인증 성공";
                adminAuthMsg.classList.remove("text-secondary");
                adminAuthMsg.classList.remove("invalid-feedback");
                adminAuthMsg.classList.add("valid-feedback");
                adminAuthMsg.style.display = "block";
                adminAnswerInput.classList.remove("is-invalid");
                adminAnswerInput.classList.add("is-valid");
                isAdminVerified = true;
                userRoleField.value = "ROLE_ADMIN";
            } else {
                adminAuthMsg.textContent = "인증 실패: 답변이 일치하지 않습니다.";
                adminAuthMsg.classList.remove("text-secondary");
                adminAuthMsg.classList.add("invalid-feedback");
                adminAuthMsg.style.display = "block";
                adminAnswerInput.classList.remove("is-valid");
                adminAnswerInput.classList.add("is-invalid");
                isAdminVerified = false;
                userRoleField.value = "ROLE_USER";
            }
        })
        .catch(error => {
            console.error("관리자 인증 요청 오류:", error);
            adminAuthMsg.textContent = "서버 오류가 발생했습니다.";
        });
});
// todo < 아이디 & 비밀번호 > 허용 문자 정규식
const noAllowRegex = /^[a-zA-Z0-9~@$#!%*?&]*$/;  //  (한글, 기타 특수문자 제외)
// todo < 아이디 >
let isIdChecked = false; // 아이디 중복 인증 여부 >> 폼 제출 시 사용
const userIdInput = document.querySelector("#userId");
const idAuthMsg = document.querySelector("#idAuthMsg");
const btnDuplicateId = document.querySelector("#btn-duplicate-id");

// 마지막으로 인증된 답변 저장
let lastUserId = "";
userIdInput.addEventListener("beforeinput", () => {
    if (isIdChecked) lastUserId = userIdInput.value; // 현재 값을 저장
});

// 아이디 입력 시 한글 방지 & 4자 이상 유효성 검사
userIdInput.addEventListener("input", function () {
    if (!noAllowRegex.test(userIdInput.value)) {
        userIdInput.value = userIdInput.value.replace(/[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/g, "");
    }
    if (isIdChecked) {
        const userConfirmed = confirm("아이디를 수정하면 중복확인을 다시 받아야 합니다. 계속하시겠습니까?");
        if (!userConfirmed) {
            userIdInput.value = lastUserId; // 취소 시 원래 값으로 복구
            return;
        } else {
            idAuthMsg.classList.remove("valid-feedback");
            userIdInput.classList.remove("is-valid");
            isIdChecked = false;
        }
    }
    if (userIdInput.value.length >= 4) {
        idAuthMsg.textContent = "아이디 중복확인이 필요합니다.";
        btnDuplicateId.disabled = false;
    } else {
        idAuthMsg.textContent = "아이디는 최소 4자 이상 입력해야 합니다.";
        btnDuplicateId.disabled = true;
    }
});

// 아이디 중복 확인
btnDuplicateId.addEventListener("click", (e) => {
    e.preventDefault();
    const userId = userIdInput.value.trim();
    fetch("/member/check-duplicate", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userId)
    })
        .then(resp => {
            if (!resp.ok) throw new Error("서버 응답 오류: " + resp.status);
            return resp.json();
        })
        .then(json => {
            console.log("서버 응답 데이터:", json);
            if (json.isExisted) {
                idAuthMsg.textContent = "이미 사용 중인 아이디입니다.";
                idAuthMsg.classList.remove("text-secondary");
                idAuthMsg.classList.add("invalid-feedback");
                idAuthMsg.style.display = "block";
                userIdInput.classList.add("is-invalid");
                isIdChecked = false;
            } else if (!json.isExisted) {
                idAuthMsg.textContent = "사용 가능한 아이디입니다.";
                idAuthMsg.classList.remove("invalid-feedback", "text-secondary");
                idAuthMsg.classList.add("valid-feedback");
                idAuthMsg.style.display = "block";
                userIdInput.classList.remove("is-invalid");
                userIdInput.classList.add("is-valid");
                isIdChecked = true;
            }
        })
        .catch(error => {
            console.error("아이디 중복 확인 오류:", error);
            alert("서버 오류가 발생했습니다.");
        });
});
// todo < 패스워드 >
// 정규식 (영문, 숫자, 특수문자 포함 8~20자)
const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~@$#!%*?&])[A-Za-z\d~@$#!%*?&]{8,20}$/;

const passwordInput = document.querySelector("#userPw");
const togglePassword = document.querySelector("#togglePassword");
const toggleIcon = document.querySelector("#toggleIcon");
const pwAuthMsg = document.querySelector("#pwAuthMsg");

// 비밀번호 입력 시 한글, 비해당 특수문자 방지 & 실시간 유효성 검사
passwordInput.addEventListener("input", function () {
    if (!noAllowRegex.test(passwordInput.value)) {
        passwordInput.value = passwordInput.value.replace(/[^a-zA-Z0-9~@$#!%*?&]/g, "");
    }
    // 실시간 유효성 검사 적용
    if (passwordRegex.test(passwordInput.value)) {
        pwAuthMsg.style.display = "block";
        pwAuthMsg.textContent = "사용 가능한 패스워드입니다.";
        pwAuthMsg.classList.remove("invalid-feedback", "text-secondary");
        pwAuthMsg.classList.add("valid-feedback");
        passwordInput.classList.remove("is-invalid");
        passwordInput.classList.add("is-valid");
    } else {
        pwAuthMsg.style.display = "block";
        pwAuthMsg.textContent = "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다.";
        pwAuthMsg.classList.remove("valid-feedback");
        passwordInput.classList.remove("is-valid");
    }
});

// 비밀번호 토글 기능
togglePassword.addEventListener("click", () => {
    const type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
    passwordInput.setAttribute("type", type);
    toggleIcon.className = type === "text" ? "bi bi-eye-slash" : "bi bi-eye";
});
// todo < 이름 >
const userNameInput = document.querySelector("#userName");
const nameAuthMsg = document.querySelector("#nameAuthMsg");
userNameInput.addEventListener("input", () => {
    if (userNameInput.value.length > 0) {
        nameAuthMsg.classList.remove("text-secondary", "invalid-feedback");
        nameAuthMsg.classList.add("valid-feedback");
        userNameInput.classList.remove("is-invalid");
        userNameInput.classList.add("is-valid");
        nameAuthMsg.style.display = "block";
    } else {
        nameAuthMsg.classList.remove("valid-feedback");
        userNameInput.classList.remove("is-valid");
        nameAuthMsg.style.display = "block";
    }
});
// todo < 이메일 >
let isAuth = false;         // 이메일 인증 완료 여부

const userEmailLocal = document.querySelector("#userEmailLocal");
const emailDomain = document.querySelector("#emailDomain");
const customDomainInput = document.querySelector("#customEmailDomain");
const btnSendEmail = document.querySelector("#btn-send-email");
const emailConfirmArea = document.querySelector("#emailConfirmArea");
const userEmailConfirm = document.querySelector("#user-email-confirm");
const btnEmailConfirm = document.querySelector("#btn-email-confirm");
const emailAuthMsg = document.querySelector("#emailStatusMsg");
const emailConfirmMsg = document.querySelector("#emailConfirmMsg");

const userEmailField = document.querySelector("#userEmail");

// 이메일 전체 주소 업데이트 함수
function updateEmailField() {
    const dom = getDomain();
    userEmailField.value = `${userEmailLocal.value.trim()}@${dom}`;
}

// 도메인
function getDomain() {
    return customDomainInput.value !== "" ? customDomainInput.value.trim() : emailDomain.value.trim();
}

// 이메일 입력 시 비허용 문자 방지 및 유효성 검사
const emailAllowRegex = /^[a-zA-Z0-9._%+-]*$/;

// 빈 문자 방지
const emailInputs = [userEmailLocal, customDomainInput];
emailInputs.forEach(input => {
    input.addEventListener("input", () => {
        if (!emailAllowRegex.test(input.value)) {
            input.value = input.value.replace(/[^a-zA-Z0-9._%+-]/g, "");
        }
        const domain = getDomain();
        if (input.value.trim()) {
            emailAuthMsg.style.display = "block";
            emailAuthMsg.classList.remove("invalid-feedback", "text-secondary");
            emailAuthMsg.classList.add("valid-feedback");
            input.classList.remove("is-invalid");
            input.classList.add("is-valid");
            console.log(input);
        } else {
            console.log("인풋이 비었습니다", input.value);
            emailAuthMsg.classList.remove("valid-feedback", "invalid-feedback");
            input.classList.remove("is-valid");
        }
        if (userEmailLocal.value.trim() !== "" && domain) {
            btnSendEmail.disabled = false;
        } else {
            btnSendEmail.disabled = true;
            input.classList.remove("is-valid");
        }
    });
});

// 도메인 직접 입력 선택 시
emailDomain.addEventListener("change", () => {
    if (emailDomain.value === "") {
        btnSendEmail.disabled = true;
        customDomainInput.value = "";
        customDomainInput.classList.remove("invalid-feedback", "valid-feedback");
        customDomainInput.style.display = "block";
    } else {
        customDomainInput.style.display = "none";
    }
});

// 이메일 인증번호 요청
btnSendEmail.addEventListener("click", e => {
    updateEmailField();
    fetch("/mail/confirm", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({"email": userEmailField.value})
    })
        .then(response => {
            console.log("응답 상태:", response.status);
            return response.json();
        })
        .then(data => {
            console.log("서버 응답:", data);
            alert("인증 메일이 발송되었습니다. 이메일을 확인하고 6자리 인증번호를 입력해주세요.");
            emailConfirmArea.classList.remove("blind");
            startTimer(300);
            window.serverConfirmNumber = data.confirmNumber;
        })
        .catch(error => {
            console.error("이메일 인증 요청 오류:", error);
            alert("인증 메일 발송 중 오류가 발생했습니다.");
        });
});

let timerInterval; // 이메일 인증 타이머 인터벌
const timerDisplay = document.querySelector("#timer"); // 타이머 표시 영역 (있다면)
// 타이머 시작 함수 (이메일 인증용, 5분 = 300초)
function startTimer(duration) {
    let timer = duration;
    clearInterval(timerInterval);

    // 즉시 남은 시간 표시 (setInterval 실행 전)
    const minutes = String(Math.floor(timer / 60)).padStart(2, "0");
    const seconds = String(timer % 60).padStart(2, "0");
    if (timerDisplay) {
        timerDisplay.textContent = `남은 시간: ${minutes}:${seconds}`;
        timerDisplay.classList.remove("blind"); // 타이머 표시
    }
    // 1초 기다리지 않고 즉시 실행
    timerInterval = setInterval(() => {
        timer--;
        const min = String(Math.floor(timer / 60)).padStart(2, "0");
        const sec = String(timer % 60).padStart(2, "0");

        if (timerDisplay) {
            timerDisplay.textContent = `남은 시간: ${min}:${sec}`;
        }
        if (timer <= 0) {
            clearInterval(timerInterval);
            if (timerDisplay) timerDisplay.textContent = "인증 시간이 만료되었습니다.";
            isAuth = false;
            userEmailConfirm.disabled = true;
            btnEmailConfirm.disabled = true;
        }
    }, 1000);
}

// 이메일 인증번호 확인
btnEmailConfirm.addEventListener("click", e => {
    e.preventDefault();

    const enteredCode = userEmailConfirm.value.trim();
    if (enteredCode === window.serverConfirmNumber) {
        isAuth = true;
        clearInterval(timerInterval);
        emailConfirmMsg.textContent = "이메일 인증 완료";
        emailConfirmMsg.classList.remove("invalid-feedback", "text-secondary");
        emailConfirmMsg.classList.add("valid-feedback");
        emailConfirmMsg.style.display = "block";
        userEmailConfirm.classList.remove("is-invalid");
        userEmailConfirm.classList.add("is-valid");
        timerDisplay.classList.add("blind");
        btnSendEmail.disabled = true;
        btnEmailConfirm.disabled = true;
        userEmailConfirm.disabled = true;
        userEmailLocal.disabled = true;
        customDomainInput.disabled = true;
        emailDomain.disabled = true;
    } else {
        emailConfirmMsg.textContent = "인증번호가 일치하지 않습니다. 다시 시도해주세요.";
        emailConfirmMsg.classList.remove("text-secondary");
        emailConfirmMsg.classList.add("invalid-feedback");
        userEmailConfirm.classList.add("is-invalid");
        isAuth = false;
    }
});
// todo 회원가입 폼 제출 시 유효성 검사
const signupForm = document.querySelector("#signupForm");
signupForm.addEventListener("submit", function (event) {
    let valid = true;
    // todo 관리자검사
    if (adminToggle.checked && !isAdminVerified) {
        adminAuthMsg.classList.remove("text-secondary");
        adminAuthMsg.classList.add("invalid-feedback");
        adminAuthMsg.textContent
            = adminAnswerInput.value === "" ? "답변을 입력해주세요." : "관리자 인증이 필요합니다.";
        adminAuthMsg.style.display = "block";
        adminAnswerInput.classList.add("is-invalid");
        adminAnswerInput.focus();
        valid = false;
    }
    // todo 아이디검사
    if (userIdInput.value.trim().length < 4) {
        idAuthMsg.style.display = "block";
        idAuthMsg.classList.remove("text-secondary");
        idAuthMsg.classList.add("invalid-feedback");
        userIdInput.classList.remove("is-valid");
        userIdInput.classList.add("is-invalid");
        valid = false;
        userIdInput.focus();
    } else if (!isIdChecked) { //중복 체크가 안되어 있으면
        idAuthMsg.style.display = "block";
        idAuthMsg.classList.remove("text-secondary");
        idAuthMsg.classList.add("invalid-feedback");
        userIdInput.classList.add("is-invalid");
        valid = false;
        userIdInput.focus();
    }
    // todo 패스워드 검사
    if (!passwordRegex.test(passwordInput.value.trim())) {
        pwAuthMsg.style.display = "block";
        pwAuthMsg.classList.remove("text-secondary");
        pwAuthMsg.classList.add("invalid-feedback");
        passwordInput.classList.add("is-invalid");
        valid = false;
        passwordInput.focus();
    }
    // todo 이름 검사
    if (userNameInput.value.length < 1) {
        nameAuthMsg.classList.remove("text-secondary");
        nameAuthMsg.classList.add("invalid-feedback");
        userNameInput.classList.add("is-invalid");
        valid = false;
        userNameInput.focus();
    }
    // todo 이메일 검사
    let fullInput = true;
    if (!userEmailLocal.value.trim()) {
        emailAuthMsg.style.display = "block";
        emailAuthMsg.classList.add("invalid-feedback");
        emailAuthMsg.classList.remove("text-secondary");
        userEmailLocal.classList.add("is-invalid");
        valid = false;
        fullInput = false;
        userEmailLocal.focus();
    }
    if (!getDomain()) {
        emailAuthMsg.style.display = "block";
        emailAuthMsg.classList.add("invalid-feedback");
        emailAuthMsg.classList.remove("text-secondary");
        customDomainInput.classList.add("is-invalid");
        valid = false;
        customDomainInput.focus();
        fullInput = false;
    }
    if (fullInput) {
        if (!isAuth) {
            emailConfirmMsg.style.display = "block";
            emailConfirmMsg.classList.add("invalid-feedback");
            emailConfirmMsg.classList.remove("text-secondary");
            userEmailConfirm.classList.add("is-invalid");
            userEmailConfirm.focus();
            valid = false;
        }
    }

    if (!valid) event.preventDefault();
    console.log("회원가입 폼 제출, 이메일:", userEmailField.value);
});