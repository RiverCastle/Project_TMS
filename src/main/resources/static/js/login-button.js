document.getElementById("sign-in-button").addEventListener("click", () => {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    // 로그인 API 엔드포인트로 POST 요청 보내기
    fetch('/api/token', {
        headers: {
            "Content-Type": "application/json"
        },
        method: "POST",
        body: JSON.stringify({
            "username": username,
            "password": password
        })
    })
        .then(response => response.json())
        .then(body => {
            const jwt = body.token;
            localStorage.setItem("JWT", jwt);
            window.location.href = '/views/main';
        })
        .catch(error => {
            alert(error.message)
        })
});