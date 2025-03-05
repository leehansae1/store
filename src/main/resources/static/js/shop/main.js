// todo tab 숨기기/보이기
const tabs = document.querySelectorAll(".tab-btn");
const contents = document.querySelectorAll(".tab-content");

function hideAllTabs() {
    contents.forEach(c => c.classList.remove("active"));
}

function showTab(tabId) {
    hideAllTabs();
    document.querySelector(tabId).classList.add("active");
}

tabs.forEach(tab => {
    tab.addEventListener("click", function () {
        hideAllTabs();
        const target = this.getAttribute("data-tab");
        tabs.forEach(t => t.classList.remove("active"));
        this.classList.add("active");
        showTab(target);
    });
});

// 페이지 처음 열리면 기본으로 '상품' 탭 열기
showTab('#products');

// 비회원 접근
const isLoggedIn = document.body.getAttribute('data-logged-in') === 'true';

document.querySelectorAll(".btn-need-login").forEach(b => {
    if (!isLoggedIn) {
        b.addEventListener("click", (e) => {
            if (confirm("로그인 후 사용 가능합니다. 로그인 페이지로 이동하시겠습니까?")) {
                if (!b.classList.contains("nav-link")) window.location.href = "/member/login";
            } else e.preventDefault();
        });
    }
});

// todo 팔로우 기능 처리
const followBtn = document.querySelector("#follow-toggle-btn");
const followerCountElem = document.querySelector("#followerCount");

// 팔로우 관련 요소가 없으면 아예 아무 처리도 안 함
if (followBtn || followerCountElem) {
    if (isLoggedIn) followBtn.classList.add("btn-need-login");
    else followBtn.classList.remove("btn-need-login");

    followBtn.addEventListener("click", e => {
        e.preventDefault();

        const sellerId = followBtn.getAttribute('data-seller-id');
        const currentState = followBtn.textContent.trim();
        const url = `/member/follow/${sellerId}`;
        const method = currentState === "팔로우" ? 'POST' : 'DELETE';

        fetch(url, {method})
            .then(response => {
                if (!response.ok) {
                    throw new Error("네트워크 응답이 정상이 아닙니다.");
                }
                return response.json();
            })
            .then(data => {
                let followCount
                    = parseInt(followerCountElem.textContent.replace("팔로워", "").trim()) || 0;

                if (data.isSaved) {
                    followBtn.classList.remove('btn-follow');
                    followBtn.classList.add('btn-unfollow');
                    followBtn.textContent = "팔로잉";
                    followCount++;
                } else if (data.isDelete) {
                    followBtn.classList.remove('btn-unfollow');
                    followBtn.classList.add('btn-follow');
                    followBtn.textContent = "팔로우";
                    followCount--;
                }

                followerCountElem.textContent = "팔로워 " + followCount;
            })
            .catch(error => {
                console.error("팔로우 처리 중 오류:", error);
                alert("팔로우 처리 중 오류가 발생했습니다.");
            });
    });
}