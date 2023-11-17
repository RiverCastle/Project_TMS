const logout_button = document.getElementById("logout_button");
logout_button.addEventListener("click", () => {
    localStorage.removeItem("JWT")
    window.location.href = "/views/main";
})

// TODO api 호출하는 식으로 바꾸기
