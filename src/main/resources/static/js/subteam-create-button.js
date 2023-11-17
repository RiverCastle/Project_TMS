function create_subTeam() {
    const name = document.getElementById("team_name").value;
    const desc = document.getElementById("team_desc").value;
    const joinCode = document.getElementById("joinCode").value;
    const max_member_number = document.getElementById("max_member_number").value;
    console.log(name + "  " + desc + "  " + joinCode + "  " + max_member_number)

    fetch('/api/team/' + teamId + '/subTeam', {
        headers: {
            "Authorization": auth,
            'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({
            "name": name,
            "description": desc,
            "joinCode": joinCode,
            "participantNumMax": max_member_number
        })
    })
        .then(response => response.json())
        .then(teamOverviewDto => {
            console.log(teamOverviewDto);
            console.log(teamOverviewDto.id);
            alert("성공적으로 하위 팀이 생성되었습니다.")
            window.location.href = "/views/team/" + teamOverviewDto.id;
        })
        .catch(error => {
            alert(error.method);
            window.location.href="/views/login";
        });
}