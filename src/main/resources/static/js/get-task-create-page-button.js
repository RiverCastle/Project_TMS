const task_create_button = document.getElementById("task-create-button");
task_create_button.addEventListener('click', () => {
    const url = "/views/team/" + teamId + "/tasks";
    location.href = url;
});