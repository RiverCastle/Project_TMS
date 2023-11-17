// /api/team/{teamId}/tasks/{taskId} 업무 상세 조회
fetch('/api/team/' + teamId + '/tasks/' + taskId, {
    headers: {
        "Authorization": auth
    },
    method: "GET"
})
    .then(response => response.json())
    .then(data => {
        const taskName = data.taskName;
        const taskDesc = data.taskDesc;
        const startDate = data.startDate;
        const dueDate = data.dueDate;
        const worker = data.worker;
        const status = data.status;

        const task_info = document.getElementById('task_details');

        const task_name_tag = document.createElement('ul');
        task_name_tag.textContent = "업무명 : " + taskName;
        // 수정 버튼
        const taskNameEditButton = document.createElement('button');
        taskNameEditButton.textContent = '수정';
        taskNameEditButton.addEventListener('click', () => {
            const newName = prompt('새로운 업무명을 입력하세요:', taskName);
            if (newName !== null) {
                // @PutMapping("/api/team/{teamId}")
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newName": newName
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.method);
                        window.location.href="/views/login";
                    });
            }
        });
        task_name_tag.appendChild(taskNameEditButton);
        task_info.appendChild(task_name_tag);

        const task_desc_tag = document.createElement('ul');
        task_desc_tag.textContent = "업무 내용 : " + taskDesc;
        // 수정 버튼
        const taskDescEditButton = document.createElement('button');
        taskDescEditButton.textContent = '수정';
        taskDescEditButton.addEventListener('click', () => {
            const newDesc = prompt('새로운 업무 내용을 입력하세요:', taskDesc);
            if (newDesc !== null) {
                // @PutMapping("/api/team/{teamId}")
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newDesc": newDesc
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.message);
                    })
            }
        });
        task_desc_tag.appendChild(taskDescEditButton);
        task_info.appendChild(task_desc_tag);

        const task_start_at_tag = document.createElement('ul');
        task_start_at_tag.textContent = "업무 시작일 : " + startDate;
        // 업무 시작일 수정 버튼
        const taskStartEditButton = document.createElement('button');
        taskStartEditButton.textContent = '수정';
        taskStartEditButton.addEventListener('click', () => {
            const newStart = prompt('새로운 업무 시작일을 입력하세요:', startDate);
            if (newStart !== null) {
                // @PutMapping("/api/team/{teamId}")
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newStartDate": newStart
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.message);
                    })
            }
        });
        task_start_at_tag.appendChild(taskStartEditButton);
        task_info.appendChild(task_start_at_tag);

        const task_end_at_tag = document.createElement('ul');
        task_end_at_tag.textContent = "업무 마감일 : " + dueDate;
        // 업무 종료일 수정 버튼
        const taskEndEditButton = document.createElement('button');
        taskEndEditButton.textContent = '수정';
        taskEndEditButton.addEventListener('click', () => {
            const newDueDate = prompt('새로운 업무 마감일을 입력하세요:', dueDate);
            if (newDueDate !== null) {
                // @PutMapping("/api/team/{teamId}")
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newDueDate": newDueDate
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.message);
                    })
            }
        });
        task_end_at_tag.appendChild(taskEndEditButton);
        task_info.appendChild(task_end_at_tag);

        const task_worker_tag = document.createElement('ul');
        task_worker_tag.textContent = "업무 담당자 : " + worker;
        // 수정 버튼
        const workerEditButton = document.createElement('button');
        workerEditButton.textContent = '수정';
        workerEditButton.addEventListener('click', () => {
            const newWorker = prompt('새로운 업무 업무 담당자를 입력하세요:', worker);
            if (newWorker !== null) {
                // @PutMapping("/api/team/{teamId}") // 업무 담당자 변경 api
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newWorker": newWorker
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.message);
                    })
            }
        });
        task_worker_tag.appendChild(workerEditButton);
        task_info.appendChild(task_worker_tag);

        const task_status_tag = document.createElement('ul');
        task_status_tag.textContent = "업무 진행 상태 : " + status;
        // 업무 상태 수정 버튼
        const taskStatusEditButton = document.createElement('button');
        taskStatusEditButton.textContent = '수정';
        taskStatusEditButton.addEventListener('click', () => {
            const newStatus = prompt('새로운 업무 상태를 입력하세요:', status);
            if (newStatus !== null) {
                // /api/team/{teamId}
                fetch('/api/team/' + teamId + '/tasks/' + taskId, {
                    headers: {
                        "Authorization": auth,
                        'Content-Type': 'application/json'
                    },
                    method: "PUT",
                    body: JSON.stringify({
                        "newStatus": newStatus
                    })
                })
                    .then(response => response.json())
                    .then(responseDto => {
                        alert(responseDto.message);
                        location.reload();
                    })
                    .catch(error => {
                        alert(error.message);
                    })
            }
        });
        task_status_tag.appendChild(taskStatusEditButton);
        task_info.appendChild(task_status_tag);
    })
    .catch(error => {
        alert(error.method);
        window.location.href="/views/login";
    });