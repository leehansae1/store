// todo < 후기 불러오기 >
const reviewTab = document.querySelector("[data-tab='#reviews']");
const reviewContainer = document.querySelector("#review-container");
let realProductId;

reviewTab.addEventListener("click", function () {
    const userId = reviewContainer.dataset.userId;
    fetch(`/shop/reviews/${userId}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"}
    })
        .then(response => response.json())
        .then(data => {
            reviewContainer.innerHTML = "";

            if (!data.isList) {
                reviewContainer.innerHTML = `<p class='no-review-text'>아직 후기가 없어요! 😃 다음에 다시 확인해 주세요.</p>`;
                return;
            }

            data.reviewList.forEach(review => {
                const formattedDate = new Date(review.reviewDate).toISOString().split("T")[0];
                realProductId = review.productDto.productId;
                const reviewElement = document.createElement("div");
                reviewElement.classList.add("review-item");

                reviewElement.innerHTML = `
        <div class="review-left">
            <img class="review-profile" src="${review.reviewer.userProfile || '/img/unknown.jpg'}" alt="프로필 이미지">
            <div class="review-info">
                <p class="reviewer-name">${review.reviewer.userName}</p>
                <p class="review-rating">${"★".repeat(review.rating)}${"☆".repeat(5 - review.rating)}</p>
                <p class="product-btn">${review.productDto.productName}</p>
            </div>
        </div>
        <div class="review-content-container">
            <span class="review-label">💬 후기</span>
            <p class="review-content">${review.reviewText}</p>
        </div>
        <div class="review-date">
            <span>${formattedDate}</span>
        </div>
    `;
                reviewContainer.appendChild(reviewElement);
            });
        })
        .catch(error => console.error("후기 불러오기 오류:", error));
});
