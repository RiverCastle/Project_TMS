const team_leave_button = document.getElementById("leave-button");
team_leave_button.addEventListener('click', () => {
    const decision = prompt("정말 팀을 떠나시려면 \"예\"를 입력해주세요");
    console.log(decision === "예");

    if (decision === "예") {
        fetch('/api/team/' + teamId + '/member', {
                headers: {
                    "Authorization": auth
                },
                method: "DELETE"
            }
        )
            .then(response => {
                if (response.ok) {
                    alert("정상적으로 팀을 탈퇴하셨습니다.")
                    window.location.href = "/views/myTasks"
                }
            })
            .catch(error => {
                alert(error.method);
                window.location.href = "/views/login";
            });
    }
})