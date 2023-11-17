fetch('/api/users/myTasks', {
    headers: {
        "Authorization": "Bearer " + Jwt
    },
    method: "GET"
})
    .then(response => response.json())
    .then(map_object => {
        const myTasksList = document.getElementById('myTasksList');
        myTasksList.innerHTML = ''; // 목록 초기화

        for (const element in map_object) {
            let startIndex = element.indexOf("teamName=") + 9;
            let endIndex = element.indexOf(",", startIndex);
            const team_name = element.substring(startIndex, endIndex);

            startIndex = element.indexOf("id=") + 3;
            endIndex = element.indexOf(",", startIndex);
            const team_id = element.substring(startIndex, endIndex);

            const teamHeader = document.createElement('a');
            teamHeader.textContent = '팀이름 : ' + team_name;
            teamHeader.setAttribute('href', '/views/team/' + team_id);
            myTasksList.appendChild(teamHeader);
            const taskList = document.createElement('ul');

            const tasks = map_object[element];
            tasks.forEach(task => {
                const taskItem = document.createElement('a');
                const task_id = task.id;
                console.log(task_id);
                taskItem.textContent = task.taskName;
                taskItem.setAttribute('href', '/views/team/' + team_id + '/tasks/' + task_id);
                taskList.appendChild(document.createElement('br'));
                taskList.appendChild(taskItem);
            });
            myTasksList.appendChild(taskList);
            myTasksList.appendChild(document.createElement('hr'));

        }
    })
    .catch(error => {
        alert(error.method);
        window.location.href="/views/login";
    });