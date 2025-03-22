const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("#search-input");

const sanitizeSearch = (input) => {
    return input.replace(/[<>`"'(){}[\];%\\/.]/g, "");
};

// 실시간 필터링 적용 (특수문자 입력 방지)
searchInput.addEventListener("input", (e) => {
    const sanitized = sanitizeSearch(e.target.value); // 입력값 필터링
    if (e.target.value !== sanitized) {
        e.target.value = sanitized; // 변경된 값 적용
    }
});

const executeSearch = () => {
    const searchWord = searchInput.value.trim();
    if (searchWord) {
        window.location.href = `/product/list/${encodeURIComponent(searchWord)}`;
    }
};

searchBtn.addEventListener("click", event => {
    event.preventDefault();
    executeSearch();
});

searchInput.addEventListener("keypress", event => {
    if (event.key === "Enter") {
        executeSearch();
    }
});