document.addEventListener("DOMContentLoaded", () => {

    const chatTime = document.querySelectorAll(".chat-time");
    if (chatTime) {
        chatTime.forEach(time => {
            time.innerText = formatChatTime(time.innerText);
        });
    }

    const sendChatDiv = document.querySelector(".chat-input");
    const chatHeader = document.querySelector(".chat-header");

    // 챗헤더와 채팅입력창 동적 display
    const pathSegments = window.location.pathname.split("/");
    const productId = pathSegments[pathSegments.length - 1]; // 마지막 값 가져오기
    if (productId > 0) {
        chatHeader.classList.remove("blind");
    }
    if (!chatHeader.classList.contains("blind")) {
        sendChatDiv.classList.remove("blind");
        const noChat = document.querySelector(".no-chat");
        if (noChat) noChat.classList.add("blind");
    }

    const chatInput = document.querySelector("#chatInput");
    chatInput.addEventListener("keydown", e => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    const chatRooms = document.querySelectorAll(".chat-room");

    chatRooms.forEach(room => {
        if (productId == room.dataset.productId) {
            room.classList.add("selected");
            const chatUnread = room.querySelector(".chat-unread");
            if (chatUnread) chatUnread.classList.add("blind");
        }
        // 채팅방 클릭 이벤트 (채팅 내역 로드)
        room.addEventListener("click", e => {
            // 클릭된 요소가 프로필 링크나 휴지통 아이콘이면 이벤트 전파 중단
            if (e.target.closest(".profile-link") || e.target.closest(".bi-trash")) {
                e.stopPropagation();
                return;
            }
            loadChatMessages(room);
        });
    });

    // 휴지통 버튼 클릭 이벤트
    const deleteIcons = document.querySelectorAll(".bi-trash");
    deleteIcons.forEach(icon => {
        icon.addEventListener("click", e => {
            e.stopPropagation(); // 이벤트 전파 방지 (채팅방 클릭 방지)

            const roomId = icon.dataset.roomId;
            if (confirm("채팅방을 삭제하시겠습니까?")) {
                leaveChat(roomId);
            }
        });
    });
    // 메시지 전송
    document.querySelector("#sendBtn").addEventListener("click", () => sendMessage());
});

// 상품 상세화면 이동
document.querySelector(".chat-header").addEventListener("click", e => {
    let chatProductCard = e.target.closest(".chat-product-card");

    let isSell = chatProductCard.dataset.sellStatus;
    let sellerId = chatProductCard.dataset.sellerId;

    if (e.target.closest(".chat-buy-btn") && isSell === "true") {
        e.preventDefault();
        alert("이미 판매 된 상품입니다.");
        return;
    }

    if (e.target.closest(".chat-buy-btn") && sellerId == userRandomId) {
        e.preventDefault();
        return;
    }

    if (!chatProductCard) return; // 상품 카드가 아닌 곳 클릭 시 무시

    let productId = chatProductCard.dataset.productId;
    if (productId && isSell === "false") {
        window.location.href = `/product/detail/${productId}`;
    }
});

// 채팅 내역 컨테이너
const chatMessages = document.querySelector("#chatMessages");
// 스크롤 항상 최하단
chatMessages.scrollTop = chatMessages.scrollHeight;
// 로그인 된 사용자 randomId
const userRandomId = document.querySelector("body").dataset.randomId;

function formatChatTime(timestamp) {
    const date = new Date(timestamp);
    const hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, "0");
    const ampm = hours >= 12 ? "오후" : "오전";
    const formattedHours = hours % 12 || 12; // 12시간제로 변환

    return `${ampm} ${formattedHours}:${minutes}`;
}

function formatChatDate(timestamp) {
    const date = new Date(timestamp);
    return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
}

function loadChatMessages(element) {
    document.querySelectorAll(".chat-room").forEach(room =>
        room.classList.remove("selected")
    );
    element.classList.add("selected");
    let roomId = element.querySelector(".chat-room-info").dataset.roomId;
    let userName = element.querySelector(".chat-user").textContent;
    const chatUnread = element.querySelector(".chat-unread");
    if (chatUnread) chatUnread.classList.add("blind");

    fetch(`/chatRoom/product/${roomId}`, {method: "POST"})
        .then(response => response.json())
        .then(json => {
            if (json.isProduct) {
                let productData = json["product"];
                const chatHeader = document.querySelector(".chat-header");

                // 기존 상품 카드 제거 (중복 방지)
                let oldProductCard = document.querySelector(".chat-product-card");
                if (oldProductCard) oldProductCard.remove();

                // 새로운 상품 카드 생성
                let productCard = document.createElement("div");
                productCard.className = "chat-product-card";
                productCard.dataset.productId = productData.productId;
                productCard.dataset.sellStatus = productData.sell;
                productCard.dataset.sellerId = productData.seller.randomId;

                productCard.innerHTML = `
                <img src="${productData.thumbnailUrl}" alt="상품 이미지" class="chat-product-image">
                <div class="chat-product-info">
                    <p class="chat-product-name">${productData.productName}</p>
                    <p class="chat-product-price">${productData.price.toLocaleString()}원</p>
                </div>
                <a href="/product/payment/checkout/${productData.productId}/chatting" class="chat-buy-btn">구매하기</a>`;

                // 채팅 헤더에 상품 카드 추가
                chatHeader.appendChild(productCard);
            }
        })
        .catch(error => console.error("상품정보 업데이트 실패", error));

    fetch(`/chat/list/${roomId}`, {method: "POST"})
        .then(response => response.json())
        .then(data => {
            const chatTitle = document.querySelector("#chatTitle");
            chatTitle.textContent = userName;
            document.querySelector(".chat-header").classList.remove("blind");
            document.querySelector("#chatTitle").dataset.roomId = roomId;

            let lastDate = null;
            chatMessages.innerHTML = "";

            data.chatList.forEach(chat => {
                let formattedDate = formatChatDate(chat.chatDate);
                let formattedTime = formatChatTime(chat.chatDate);
                // 하루가 바뀔 때만 날짜 출력
                if (lastDate !== formattedDate) {
                    let dateDiv = document.createElement("div");
                    dateDiv.className = "chat-date";
                    dateDiv.textContent = formattedDate;
                    chatMessages.appendChild(dateDiv);
                    lastDate = formattedDate;
                }

                if (chat.productImgUrl !== null) {
                    let img = document.createElement("img");
                    img.classList.add(chat.writer.randomId == userRandomId ? "chat-right" : "chat-left");
                    img.classList.add("chat-img");
                    img.src = chat.productImgUrl;
                    img.style.width = "193px";
                    img.style.height = "193px";
                    img.style.margin = "0";
                    chatMessages.appendChild(img);
                }

                let unreadIndicator = chat.read
                    ? "<span class='chat-unread-indicator'></span>"
                    : "<span class='chat-unread-indicator'>1</span>";


                let div = document.createElement("div");
                div.className = chat.writer.randomId == userRandomId ? "chat-right" : "chat-left";
                div.innerHTML = chat.writer.randomId == userRandomId
                    ? `<div class="chat-info">${unreadIndicator}<span class='chat-time'>${formattedTime}</span></div>
                        <span class="chat-content">${chat.content}</span>`
                    : `<span class="chat-content">${chat.content}</span>
                        <div class="chat-info">${unreadIndicator}<span class='chat-time'>${formattedTime}</span></div>`;
                chatMessages.appendChild(div);
                chatMessages.scrollTop = chatMessages.scrollHeight;
            });
        })
        .catch(error => console.error("채팅 내역 로드 실패:", error));
    document.querySelector(".chat-input").classList.remove("blind");
}

function sendMessage() {
    const chatInput = document.querySelector("#chatInput");
    const message = chatInput.value.trim();
    const roomId = document.querySelector("#chatTitle").dataset.roomId;
    if (!message || !roomId) return;

    fetch(`/chatRoom/writeChat/${roomId}`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({content: message})
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 새 메시지 UI에 추가 (전체 reload 없이)
                appendNewMessage(data.chat);
                chatInput.value = "";

                updateChatRoomList(roomId, data.chat.content, data.chat.chatDate);
            }
        })
        .catch(error => console.error("메시지 전송 실패:", error));
}

function appendNewMessage(chat) {

    let formattedDate = formatChatDate(chat.chatDate);
    let formattedTime = formatChatTime(chat.chatDate);

    let lastDateDivs = chatMessages.querySelectorAll(".chat-date");
    let lastDateDiv = lastDateDivs.length > 0 ? lastDateDivs[lastDateDivs.length - 1] : null;

    if (!lastDateDiv || lastDateDiv.innerText !== formattedDate) {
        let dateDiv = document.createElement("div");
        dateDiv.className = "chat-date";
        dateDiv.textContent = formattedDate;
        chatMessages.appendChild(dateDiv);
    }

    // 새 메시지 추가
    let div = document.createElement("div");
    div.className = "chat-right";
    div.innerHTML = `<div class="chat-info"><span class='chat-unread-indicator'>1</span>
<span class='chat-time'>${formattedTime}</span></div>
<span class="chat-content">${chat.content}</span>`

    chatMessages.appendChild(div);

    // 스크롤을 최신 메시지로 이동
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function leaveChat(roomId) {
    if (!roomId) return;

    fetch(`/chatRoom/delete/${roomId}`, {
        method: "DELETE"
    })
        .then(response => response.json())
        .then(data => {
            if (data.isDelete) {
                document.querySelectorAll(".chat-room-info").forEach(room => {
                    if (roomId == room.dataset.roomId) room.parentElement.remove();
                });
                window.location.href = "/chatRoom";
            } else alert("잠시 후 다시 시도해주세요.");
        })
        .catch(error => console.error("채팅방 삭제 실패:", error));
}

function updateChatRoomList(roomId, lastMessage, chatDate) {
    const chatRoomItem = document.querySelector(`[data-room-id='${roomId}']`);
    if (chatRoomItem) {
        // 최근 메시지 업데이트
        const lastMsgElem = chatRoomItem.querySelector(".chat-last-msg");
        if (lastMsgElem) lastMsgElem.textContent = lastMessage;

        // 최근 시간 업데이트
        const lastTimeElem = chatRoomItem.querySelector(".chat-last-time");
        if (lastTimeElem) lastTimeElem.textContent = getTimeAgo(chatDate);

        // 채팅방을 맨 위로 이동
        const chatRoomList = document.querySelector("#chatRoomList");
        chatRoomList.prepend(chatRoomItem.closest("li"));
    }
}

function getTimeAgo(dateString) {
    const date = new Date(dateString);
    const now = new Date();

    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInDays > 0) {
        return `${diffInDays}일 전`;
    } else if (diffInHours > 0) {
        return `${diffInHours}시간 전`;
    } else if (diffInMinutes > 0) {
        return `${diffInMinutes}분 전`;
    } else {
        return "방금 전";
    }
}

