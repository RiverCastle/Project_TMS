
fetch('/api/team/' + teamId + '/tasks', {
    headers: {
        "Authorization": auth
    },
    method: "GET"
})
    .then(response => response.json())
    .then(data => {
        // 테이블 생성
        const table = document.getElementById('tasks');

        // 미완료 업무와 완료 업무를 나눌 섹션 생성
        const incompleteSection = document.createElement('section');
        incompleteSection.innerHTML = "<h2>미완료 업무</h2>";

        const completeSection = document.createElement('section');
        completeSection.innerHTML = "<h2>완료 업무</h2>";

        // 테이블 헤더 생성
        const headerRow = document.createElement('tr');

        const taskNameHeader = document.createElement('th');
        taskNameHeader.textContent = "작업 이름";
        taskNameHeader.style.width = "35vw";

        const workerHeader = document.createElement('th');
        workerHeader.textContent = "작업자";
        workerHeader.style.width = "7vw";

        const statusHeader = document.createElement('th');
        statusHeader.textContent = "상태";
        statusHeader.style.width = "7vw";

        headerRow.appendChild(taskNameHeader);
        headerRow.appendChild(workerHeader);
        headerRow.appendChild(statusHeader);

        completeSection.appendChild(headerRow);
        incompleteSection.appendChild(headerRow.cloneNode(true)); // TODO 회고록에 작성하기

        data.forEach(task => {
            const task_id = task.id;
            // 새로운 열(행) 생성
            const row = document.createElement('tr');

            // 각 업무의 정보를 표시할 셀(칸) 생성
            const taskNameCell = document.createElement('a');
            taskNameCell.href = '/views/team/' + teamId + '/tasks/' + task_id;
            taskNameCell.textContent = task.taskName;
            taskNameCell.style.width = "35vw";

            const workerCell = document.createElement('td');
            workerCell.textContent = task.worker;
            workerCell.style.width = "7vw";

            const statusCell = document.createElement('td');
            statusCell.textContent = task.status;
            statusCell.style.width = "7vw";


            // 데이터 셀(칸)을 행에 추가
            row.appendChild(taskNameCell);
            row.appendChild(workerCell);
            row.appendChild(statusCell);

            // 미완료 업무와 완료 업무 섹션에 행 추가
            if (task.status === '완료') {
                completeSection.appendChild(row);
            } else {
                incompleteSection.appendChild(row);
            }
        });

        // 섹션을 본문에 추가
        incompleteSection.appendChild(document.createElement('hr'));
        completeSection.appendChild(document.createElement('hr'));
        table.appendChild(incompleteSection);
        table.appendChild(completeSection);
    })
    .catch(error => {
        alert(error.method);
        window.location.href = "/views/login";
    });