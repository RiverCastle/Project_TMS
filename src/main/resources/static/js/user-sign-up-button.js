const sign_up_button = document.getElementById("sign-in-button");
sign_up_button.addEventListener('click', () => {
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        const password_check = document.getElementById("password-check").value;

        fetch('/api/users', {
                headers: {
                    'Content-Type': 'application/json'
                },
                method: "POST",
                body: JSON.stringify({
                    "username": username,
                    "password": password,
                    "passwordCheck": password_check
                })
            }
        )
            .then(response => {
                if (response.ok) {
                    alert("회원가입이 완료되었습니다.")
                    window.location.href = "/views/login";
                } else {
                    response.json().then(error => {
                        alert(error.message);
                        console.log(error.message);
                    })
                }
            })
    }
)