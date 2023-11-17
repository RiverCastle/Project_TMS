// 팀 가입 api
const teamJoinButton = document.createElement('button');
teamJoinButton.type = "button";
teamJoinButton.className = "btn btn-info";
teamJoinButton.name = "가입하기";
teamJoinButton.addEventListener('click', () => {
    const joinCode = prompt('해당 팀의 참여코드를 입력하세요 :');
    fetch('/api/team/' + teamId + '/member', {
        headers: {
            "Authorization": auth,
            'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({
            "joinCode": joinCode
        })
    })
        .then(response => {
            if (response.ok) window.location.href = '/views/team/' + teamId;
        })
        .catch(error => {
            alert(error.message);
        })
})