const searchBtn = document.querySelector("#search-btn");
const searchInput = document.querySelector("#search-input");

const executeSearch = () => {
    const searchWord = searchInput.value.trim();
    if (searchWord) {
        window.location.href = `/product/list/${encodeURIComponent(searchWord)}`;
    }
};
searchBtn.addEventListener("click", (event) => {
    event.preventDefault();
    executeSearch();
});
searchInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
        executeSearch();
    }
});