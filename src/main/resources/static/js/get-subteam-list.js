const subteam_tags = document.getElementById("enrolled subteams");
fetch('/api/team/' + teamId + '/subTeam', {
    headers: {
        "Authorization" : auth
    },
    method: "GET"
})
    .then(response => response.json())
    .then(data => {
        data.forEach(teamOverviewDto => {
            const teamId = teamOverviewDto.id;
            const teamTag = document.createElement('tr');

            const teamNameTag = document.createElement('th');
            teamNameTag.style.width = "35vw";
            teamNameTag.textContent = teamOverviewDto.teamName;
            teamTag.appendChild(teamNameTag);

            const teamDescTag = document.createElement('th');
            teamDescTag.style.width = "45vw";
            teamDescTag.textContent = teamOverviewDto.teamDesc;
            teamTag.appendChild(teamDescTag);

            const teamJoinButton = document.createElement('button');
            teamJoinButton.type = "button";
            teamJoinButton.className = "btn btn-info";
            teamJoinButton.name = "가입하기";
            // 팀 가입 api
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
            // 추가하기
            teamTag.appendChild(teamJoinButton);
            subteam_tags.appendChild(document.createElement('hr'));
            subteam_tags.appendChild(teamTag);
        })
    })