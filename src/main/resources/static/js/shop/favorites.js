// todo < 찜한 상품 불러오기 >
document.addEventListener("DOMContentLoaded", () => {
    const favoritesTab = document.querySelector("[data-tab='#favorites']");
    if (favoritesTab) {
        const favoritesContainer = document.querySelector("#favorites-container");
        const favoritesCount = document.querySelector("#favorites-count");
        favoritesTab.addEventListener("click", function () {
            fetch("/shop/favorites", {
                method: "POST",
                headers: {"Content-Type": "application/json"}
            })
                .then(response => response.json())
                .then(data => {
                    favoritesContainer.innerHTML = "";
                    favoritesCount.innerText = data.count;
                    if (!data.isList) {
                        favoritesContainer.innerHTML = `<p class='no-favorites-text'>아직 찜한 상품이 없어요! 😃</p>`;
                        return;
                    }

                    data.productList.forEach(product => {
                        const formattedDate = new Date(product.postDate).toISOString().split("T")[0];
                        const productElement = document.createElement("div");
                        productElement.classList.add("favorite-item");
                        if (!product.sell){
                            productElement.innerHTML = `
                    <div class="favorite-content d-flex">
                        <a href="/product/detail/${product.productId}">
                            <img class="favorite-image" src="${product.thumbnailUrl}" alt="상품 이미지">
                        </a>
                        <div class="favorite-info">
                            <a class="favorite-name" href="/product/detail/${product.productId}">${product.productName}</a>
                            <p class="favorite-real-price">${new Intl.NumberFormat('ko-KR').format(product.price)}<span class="favorite-price">원</span>
                            </p>
                            <p class="favorite-postDate">${formattedDate}</p>     
                        </div>
                    </div>
                    <button class="remove-favorite-btn" data-product-id="${product.productId}">❌</button>
                `;
                        } else {
                            productElement.innerHTML = `
                    <div class="favorite-content d-flex">
                            <img class="favorite-image" src="${product.thumbnailUrl}" alt="상품 이미지">
                        <div class="favorite-info">
                            <span class="favorite-name">${product.productName}</span>
                            <p class="favorite-real-price">판매완료</p>
                            <p class="favorite-postDate">${formattedDate}</p>     
                        </div>
                    </div>
                    <button class="remove-favorite-btn" data-product-id="${product.productId}">❌</button>
                `;
                        }


                        favoritesContainer.appendChild(productElement);
                    });
                })
                .catch(error => console.error("찜한 상품 불러오기 오류:", error));
        });

        // 이벤트 위임 방식으로 '찜 취소' 버튼 클릭 처리
        favoritesContainer.addEventListener("click", function (event) {
            const target = event.target;

            // 삭제 버튼 클릭 시
            if (target.classList.contains("remove-favorite-btn")) {
                const productId = target.dataset.productId;

                if (!confirm("정말 찜한 상품을 취소하시겠습니까?")) return;

                fetch(`/product/unlike/${productId}`, {
                    method: "DELETE",
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.unlike) {
                            alert("찜한 상품이 취소되었습니다.");
                            target.closest(".favorite-item").remove(); // 삭제된 상품 UI에서 제거
                        } else {
                            alert("찜한 상품 취소에 실패했습니다.");
                        }
                    })
                    .catch(error => {
                        console.error("찜 취소 오류:", error);
                        alert("오류가 발생했습니다. 다시 시도해주세요.");
                    });
            }
        });
    }
});