// todo < tab 숨기기/보이기 >
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

// todo < 비회원 접근 >
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

// todo < UP 버튼 이벤트 >
document.querySelectorAll('.up-btn').forEach(upBtn => {

    upBtn.addEventListener('click', () => {
        const productRow = upBtn.closest('.product-row');
        const postDateStr = productRow.getAttribute('data-post-date');

        if (isSameDay(postDateStr)) {
            alert('상품은 하루에 한 번만 최신화 할 수 있습니다.\r\n내일 다시 시도해주세요:)');
            return;
        }
        const productId = productRow.getAttribute('data-product-id');
        fetch(`/product/up/${productId}`, {
            method: 'POST'
        }).then(response => {
            if (response.ok) {
                alert('상품이 최신 순으로 정렬되었습니다.');
                location.reload();
            } else alert('업데이트에 실패했습니다.');
        });
    });
});

function isSameDay(postDateStr) {
    const postDate = new Date(postDateStr);
    const now = new Date();

    return (
        postDate.getFullYear() === now.getFullYear() &&
        postDate.getMonth() === now.getMonth() &&
        postDate.getDate() === now.getDate()
    );
}

// todo < 보이기/숨기기 버튼 >
document.querySelectorAll('.hide-btn').forEach(hideBtn => {
    hideBtn.addEventListener('click', () => {
        const hideUrl = hideBtn.getAttribute('data-hide-url');
        const isCurrentlyHidden = hideBtn.getAttribute('data-display') === 'true';

        fetch(hideUrl, {method: 'POST'})
            .then(response => {
                console.log(response);
                if (response.ok) {
                    alert(isCurrentlyHidden ? '상품이 숨겨졌습니다.' : '상품이 다시 공개되었습니다.');
                    location.reload();
                } else alert('상품 상태 변경에 실패했습니다.');
            });
    });
});

// todo < 수정 버튼 >
document.querySelectorAll('.edit-btn').forEach(editBtn => {
    editBtn.addEventListener('click', () => {
        window.location.href = editBtn.getAttribute('data-modify-url');
    });
});

// todo < 팔로우 기능 처리 >
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