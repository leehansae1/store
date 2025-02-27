// Daum 우편번호 API
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
// ====================================================================================================================
// 전화번호 입력 시 자동으로 '-' 추가
document.querySelector("#tel").addEventListener("input", function (event) {
    let value = phoneInput.value.replace(/\D/g, ""); // 숫자만 남기기
    // 하이픈 자동 추가 (010-xxxx-xxxx 또는 010-xxx-xxxx 형태)
    if (value.length >= 4 && value.length <= 7) {
        value = `${value.slice(0, 3)}-${value.slice(3)}`;
    } else if (value.length >= 8) {
        value = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7, 11)}`;
    }
    phoneInput.value = value; // 변환된 값 적용
});
// ====================================================================================================================
// 관리자 관련 요소(for MEMBER - ROLE)
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
// ====================================================================================================================
// 아이디 & 비밀번호 영역
const noKoreanRegex = /^[a-zA-Z0-9@!%*?&]*$/; // 한글 입력 방지 정규식

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
    if (!noKoreanRegex.test(userIdInput.value)) {
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
                idAuthMsg.classList.remove("invalid-feedback");
                idAuthMsg.classList.remove("text-secondary");
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
// ====================================================================================================================
// 정규식 (영문, 숫자, 특수문자 포함 8~20자)
const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~@$#!%*?&])[A-Za-z\d~@$!%*?&]{8,20}$/;