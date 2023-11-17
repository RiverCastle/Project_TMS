function create_task() {
    const name = document.getElementById("task_name").value;
    const desc = document.getElementById("task_desc").value;
    const start = document.getElementById("task_start").value;
    const end = document.getElementById("task_end").value;
    const worker = document.getElementById("task_worker").value;


    fetch('/api/team/' + teamId + '/tasks', {
        headers: {
            "Authorization": auth,
            'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({
            "taskName": name,
            "taskDesc": desc,
            "startDate": start,
            "dueDate": end,
            "worker" : worker
        })
    })
        .then(response => {
            if (response.ok) {
                alert("성공적으로 업무가 생성되었습니다.")
                window.location.href = "/views/team/" + teamId;
            }
        })
        .catch(error => {
            alert(error.method);
            window.location.href="/views/login";
        });
}