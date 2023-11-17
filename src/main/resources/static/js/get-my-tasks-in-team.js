fetch('/api/team/' + teamId + '/tasks/myTasks',
    {
        headers: {
            "Authorization": auth
        },
        method: "GET"
    })
    .then(response => response.json())
    .then(data => {
        const table = document.getElementById('tasks');


        const my_tasks_section = document.createElement('section');
        my_tasks_section.innerHTML = "<h2>내 업무</h2>";


        // 테이블 헤더 생성
        const headerRow = document.createElement('tr');

        const taskNameHeader = document.createElement('th');
        taskNameHeader.textContent = "작업 이름";

        const workerHeader = document.createElement('th');
        workerHeader.textContent = "작업자";

        const statusHeader = document.createElement('th');
        statusHeader.textContent = "상태";

        headerRow.appendChild(taskNameHeader);
        headerRow.appendChild(workerHeader);
        headerRow.appendChild(statusHeader);

        my_tasks_section.appendChild(headerRow);

        data.forEach(task => {
            const task_id = task.id;
            // 각 업무에 대한 행 생성
            const row = document.createElement('tr');

            const taskNameCell = document.createElement('a');
            taskNameCell.href = '/views/team/' + teamId + '/tasks/' + task_id;
            taskNameCell.textContent = task.taskName;

            // 작업자를 나타내는 셀(칸) 생성
            const workerCell = document.createElement('td');
            workerCell.textContent = task.worker;

            // 상태를 나타내는 셀(칸) 생성
            const statusCell = document.createElement('td');
            statusCell.textContent = task.status;

            // 데이터 셀(칸)을 행에 추가
            row.appendChild(taskNameCell);
            row.appendChild(workerCell);
            row.appendChild(statusCell);

            // 행을 테이블에 추가
            my_tasks_section.appendChild(row);
        });

        // 테이블을 해당 요소에 추가
        my_tasks_section.appendChild(document.createElement('hr'));
        table.appendChild(my_tasks_section);
    })
    .catch(error => {
        alert(error.method);
        window.location.href = "/views/login";
    });