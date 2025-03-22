const followingTab = document.querySelector("[data-tab='#followings']");
const followerTab = document.querySelector("[data-tab='#followers']");

const followingContainer = document.querySelector("#following-container");
const followerContainer = document.querySelector("#follower-container");

const followingCount = document.querySelector("#following-count");
const followerCount = document.querySelector("#follower-count");

const randomId = followerContainer.dataset.randomId;

function loadFollowData(url, container, countElement, type) {
    fetch(url, {
        method: "POST",
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            container.innerHTML = "";
            countElement.textContent = data.count;

            if (data.isEmpty) {
                if (type !== "팔로잉") {
                    container.innerHTML = `<p class='no-follow-text'>아직 팔로우한 사람이 없어요! 😃</p>`;
                } else container.innerHTML = `<p class='no-follow-text'>아직 ${type}한 사람이 없어요! 😃</p>`;
                return;
            }

            data.memberList.forEach(user => {
                const followElement = document.createElement("a");
                followElement.href = `/shop/products/${user.randomId}`;
                followElement.classList.add("follow-item");

                followElement.innerHTML = `
                    <div class="follow-card">
                        <img class="follow-profile" src="${user.userProfile || '/img/unknown.jpg'}" alt="프로필 이미지">
                        <p class="follow-name">${user.userName}</p>
                        <p class="follow-info">상품 ${user.productCount} | 팔로워 ${user.followCount}</p>
                    </div>
                `;

                container.appendChild(followElement);
            });
        })
        .catch(error => console.error(`${type} 목록 불러오기 오류:`, error));
}

// 팔로잉 탭 클릭 시
followingTab.addEventListener("click", () => {
    console.log("following");
    loadFollowData(`/shop/followings/${randomId}`, followingContainer, followingCount, "팔로잉");
});

// 팔로워 탭 클릭 시
followerTab.addEventListener("click", () => {
    console.log("follower");
    loadFollowData(`/shop/followers/${randomId}`, followerContainer, followerCount, "팔로워");
});
