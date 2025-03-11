document.addEventListener("DOMContentLoaded", function () {
    const editModal = document.querySelector("#editAccountModal");
    const saveBtn = document.querySelector(".save-btn");

    // todo < 각 모달마다 다른 값 >
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", function () {
            let category = this.getAttribute("data-category");
            let field = this.getAttribute("data-field");
            let currentValue = this.previousElementSibling.innerText;

            let modalBody = document.querySelector("#modal-body-content");
            let inputType = "text";

            // 안내 문구 추가
            let guideText = "";

            if (field === "tel") {
                inputType = "tel";
                guideText = `<small class="text-muted">예: 010-1234-5678 (자동 하이픈 완성)</small>`;
            } else if (field === "userName") guideText = `<small class="text-muted">변경할 이름을 입력해주세요.</small>`;

            modalBody.innerHTML = `
                <input type="${inputType}" class="form-control" id="modal-input" value="${currentValue}" placeholder="수정할 값을 입력하세요.">
                ${guideText}
`;
            document.querySelector(".modal-category").value = category;
        });
    });

    // todo 각 모달 마다 유효성 검사
    editModal.addEventListener("shown.bs.modal", () => {
        let modalInput = document.querySelector("#modal-input");
        modalInput.focus();
        if (!modalInput) return;
        modalInput.addEventListener("input", function (e) {
            let value = modalInput.value.trim();
            let category = document.querySelector(".modal-category").value;

            if (category === "2") saveBtn.disabled = value.length < 1;
            else if (category === "4") formatPhoneNumber(e);
        });
    });

    // todo < 테스트 완료 > 전화번호 하이픈 자동완성
    function formatPhoneNumber(event) {
        let input = event.target;
        let numbers = input.value.replace(/\D/g, ""); // 숫자만 추출

        // 최대 11자리까지만 유지 (010-XXXX-XXXX 형식)
        if (numbers.length > 11) numbers = numbers.slice(0, 11);

        let formattedNumber;
        if (numbers.length > 10) formattedNumber = numbers.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
        else if (numbers.length > 6) formattedNumber = numbers.replace(/(\d{3})(\d{3,4})/, "$1-$2");
        else if (numbers.length > 3) formattedNumber = numbers.replace(/(\d{3})(\d{1,4})/, "$1-$2");
        else formattedNumber = numbers;
        input.value = formattedNumber;
    }

    // todo < 테스트 완료 >
    saveBtn.addEventListener("click", () => {
        console.log("saveBtn 클릭됨!"); // 콘솔 확인

        let updateTarget = document.querySelector("#modal-input").value;
        let category = document.querySelector(".modal-category").value;

        let requestData = {
            updateTarget: updateTarget,
            category: parseInt(category)
        };

        fetch("/member/update-info", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(requestData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.updated) {
                    alert("정보가 업데이트되었습니다.");
                    window.location.reload();
                }
            })
            .catch(error => console.error("Error:", error));
    });

    // todo < 비밀번호 검증 >
    const verifyModal = document.querySelector("#verifyPwModal");
    const verifyForm = document.querySelector("#verifyPasswordForm");
    const verifyInput = document.querySelector("#verify-password-input");
    const verifyBtn = document.querySelector(".verify-btn");

    const deleteAccountModal = document.querySelector("#deleteAccountModal");

    let actionType; // 회원 탈퇴 or 비밀번호 변경 구분

    // 모달이 열릴 때 초기화
    verifyModal.addEventListener("shown.bs.modal", () => {
        verifyInput.value = "";
        verifyInput.focus();
        verifyBtn.disabled = true;
    });

    // 입력 감지하여 버튼 활성화/비활성화
    verifyInput.addEventListener("input", () => {
        verifyBtn.disabled = verifyInput.value.trim() === "";
    });

    // 폼 제출 이벤트 리스너 추가
    verifyForm.addEventListener("submit", e => {
        e.preventDefault(); // 기본 제출 방지
        let password = verifyInput.value.trim();
        fetch("/member/verify-password", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({password: password})
        })
            .then(response => response.json())
            .then(data => {
                verifyInput.value = "";
                if (data.matched) {
                    bootstrap.Modal.getInstance(verifyModal).hide(); // 모달 닫기
                    if (actionType === "delete") bootstrap.Modal.getOrCreateInstance(deleteAccountModal).show(); // 회원 탈퇴 실행
                    else if (actionType === "changePassword") openChangePasswordModal(); // 비밀번호 변경 실행
                } else {
                    alert("비밀번호가 일치하지 않습니다.");
                    verifyInput.focus();
                    verifyBtn.disabled = true;
                }
            })
            .catch(error => console.error("Error:", error));
    });

    const deleteBtn = document.querySelector(".hide-btn");
    const passwordBtn = document.querySelector(".btn-warning");
    // 회원 탈퇴 버튼 클릭 시 비밀번호 검증 모달 띄우기
    deleteBtn.addEventListener("click", () => {
        actionType = "delete"; // 탈퇴 기능 수행
        bootstrap.Modal.getOrCreateInstance(verifyModal).show();
    });
    // 비밀번호 변경 버튼 클릭 시 비밀번호 검증 모달 띄우기
    passwordBtn.addEventListener("click", () => {
        actionType = "changePassword"; // 비밀번호 변경 수행
        bootstrap.Modal.getOrCreateInstance(verifyModal).show();
    });

    // todo 비밀번호 변경 시 모달 띄우기
    function openChangePasswordModal() {
        const changePwModal = new bootstrap.Modal(document.querySelector("#changePwModal"));
        newPasswordInput.focus();
        changePwModal.show();
    }

    // todo 비밀번호 변경 모달
    const newPasswordInput = document.querySelector("#new-password");
    const confirmPasswordInput = document.querySelector("#confirm-password");
    const saveNewPasswordBtn = document.querySelector("#saveNewPasswordBtn");
    const passwordError = document.querySelector("#password-error");
    const passwordSuccess = document.querySelector("#password-success");
    const guideText = document.querySelector("#guide-text");
    const changePasswordForm = document.querySelector("#changePasswordForm");

    // 입력값 변경 시 검증
    function validatePasswords() {
        const newPassword = newPasswordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        let passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~@$#!%*?&])[A-Za-z\d~@$#!%*?&]{8,20}$/;

        if (!passwordPattern.test(newPassword)) {
            saveNewPasswordBtn.disabled = true;
            return;
        }

        if (newPassword !== confirmPassword) {
            guideText.classList.add("d-none");
            passwordSuccess.classList.add("d-none");
            passwordError.classList.remove("d-none"); // 에러 메시지 표시
            saveNewPasswordBtn.disabled = true;
        } else {
            guideText.classList.add("d-none");
            passwordSuccess.classList.remove("d-none");
            passwordError.classList.add("d-none"); // 에러 메시지 숨김
            saveNewPasswordBtn.disabled = false;
        }
    }

    // 비밀번호 입력 시 실시간 검증
    newPasswordInput.addEventListener("input", validatePasswords);
    confirmPasswordInput.addEventListener("input", validatePasswords);

    // 저장 버튼 클릭 시 서버로 전송
    changePasswordForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지
        const newPassword = newPasswordInput.value.trim();

        fetch("/member/update-info", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({updateTarget: newPassword, category: 1})
        })
            .then(response => response.json())
            .then(json => {
                console.log(json);
                if (json.updated) {
                    alert("비밀번호가 변경되었습니다. 다시 로그인 해주세요.");
                    window.location.href = "/member/logout"; // 로그아웃 후 다시 로그인 요구
                } else {
                    alert("비밀번호 변경에 실패했습니다. 다시 시도해주세요.");
                    newPasswordInput.focus();
                }
            })
            .catch(error => console.error("Error:", error));
    });

    // todo 탈퇴처리
    const deleteConfirmBtn = document.querySelector(".delete-account-btn");
    deleteConfirmBtn.addEventListener("click", () => {
        fetch("/member/delete", {method: "POST",})
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if (data) {
                    alert("이용해주셔서 감사합니다.");
                    window.location.href = "/member/logout"; // 홈으로 리디렉트
                } else alert("회원 탈퇴 실패. 잠시후 다시 시도해주세요.");
            })
            .catch(error => console.error("Error:", error));
    });

    // todo 탈퇴철회
    document.querySelector(".rollback-btn").addEventListener("click", () =>
        bootstrap.Modal.getInstance(deleteAccountModal).hide()
    );

    // 모달이 숨겨질 때(닫힐 때) 포커스를 다른 곳으로 이동
    editModal.addEventListener("hidden.bs.modal", () => document.body.focus());
    verifyModal.addEventListener("hidden.bs.modal", () => document.body.focus());
    deleteAccountModal.addEventListener("hidden.bs.modal", () => document.body.focus());
});