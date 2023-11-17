function post_comment(comment) {
    fetch('/api/team/' + teamId + '/tasks/' + taskId + '/comments', {
        headers: {
            "Authorization": auth,
            "Content-Type": "application/json"
        },
        method: "POST",
        body: JSON.stringify({
            "content": comment
        })
    })
        .then(response => {
            if (response.ok) window.location.reload();
        })
        .catch(error => {
            alert(error.method);
            window.location.href="/views/login";
        });
}