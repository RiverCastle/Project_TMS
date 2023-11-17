fetch('/api/team/' + teamId, {
    headers: {
        "Authorization": auth
    },
    method: "GET"
})
    .then(response => response.json())
    .then(data => {
        // Team Info
        const team_info = document.getElementById('team_info');

        // 팀 이름
        const team_name = document.createElement('h2');
        team_name.textContent = '팀이름 : ' + data.name;
        team_info.appendChild(team_name);

        // 수정 버튼
        const team_nameEditButton = document.createElement('button');
        team_nameEditButton.textContent = '수정';
        team_nameEditButton.addEventListener('click', () => {
            const newName = prompt('새로운 팀 이름을 입력하세요:', data.name);
            if (newName !== null) {
                // 팀 이름 수정 api
                fetch('/api/team/' + teamId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "name": newName
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.method);
                        window.location.href = "/views/login";
                    });
            }
        });
        team_info.appendChild(team_nameEditButton);

        // 팀 설명
        const team_desc = document.createElement('p');
        team_desc.textContent = '팀설명 : ' + data.desc;
        team_info.appendChild(team_desc);

        // 수정 버튼
        const team_descEditButton = document.createElement('button');
        team_descEditButton.textContent = '수정';
        team_descEditButton.addEventListener('click', () => {
            const newDesc = prompt('새로운 팀 설명을 입력하세요:', data.desc);
            if (newDesc !== null) {
                // 팀 소개 수정 api
                fetch('/api/team/' + teamId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "description": newDesc
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.method);
                        window.location.href = "/views/login";
                    });
            }
        });
        team_info.appendChild(team_descEditButton);

        // 팀 최대인원 항목
        const team_limit = document.createElement('p');
        team_limit.textContent = '팀 최대인원 : ' + data.memberLimit;
        team_info.appendChild(team_limit);
    })
    .catch(error => {
        alert(error.method);
        window.location.href = "/views/login";
    });