
const search_button = document.getElementById("search-button");
search_button.addEventListener("click", () => {
    const keyword_input = document.getElementById("search-input").value;
    let teamsTag = document.getElementById("enrolled teams");
    teamsTag.innerHTML = '';
    fetch('/api/team?keyword=' + keyword_input, {
        method: "GET"
    })
        .then(response => response.json())
        .then(list => {
            list.forEach(teamOverviewDto => {
                const teamId = teamOverviewDto.id;
                const teamTag = document.createElement('tr');

                const teamNameTag = document.createElement('th');
                teamNameTag.style.width = "20vw";
                teamNameTag.textContent = teamOverviewDto.teamName;
                teamTag.appendChild(teamNameTag);

                const teamDescTag = document.createElement('th');
                teamDescTag.style.width = "40vw";
                teamDescTag.textContent = teamOverviewDto.teamDesc;
                teamTag.appendChild(teamDescTag);

                // TODO 분리하기 현재 불러오지 못해서 실패
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

                // 추가하기
                teamTag.appendChild(teamJoinButton);
                teamsTag.appendChild(document.createElement('hr'));
                teamsTag.appendChild(teamTag);
            })
        })
})