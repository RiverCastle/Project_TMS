// /api/team/{teamId}/task/{taskId}/comments 업무 댓글 조회
fetch('/api/team/' + teamId + '/tasks/' + taskId + '/comments', {
    headers: {
        "Authorization": auth
    },
    method: "GET"
})
    .then(response => response.json())
    .then(data => {
        const comment_section = document.getElementById('comments');

        // 여기서 data는 페이지 객체를 나타냅니다.

        // 페이지 데이터에서 TaskCommentReadDto 객체 목록을 가져옵니다.
        const taskComments = data.content;

        // TaskCommentReadDto 객체들을 반복하여 표시 또는 처리합니다.
        taskComments.forEach(comment => {
            // 작성자 이름과 내용을 추출
            const comment_id = comment.id;
            const data_created_at = new Date(comment.createdAt);
            const comment_created_at = data_created_at.getMonth() + 1 + "/" + data_created_at.getDay() + "  " + data_created_at.getHours() + ":" + data_created_at.getMinutes();
            const writerName = comment.writerName;
            const content = comment.content;

            // 작성자 이름과 내용을 HTML 엘리먼트로 만들어 추가하거나 표시
            const commentElement = document.createElement('ul');
            const comment_delete_button = document.createElement('button');
            commentElement.innerHTML = `<p>${comment_created_at} | <strong>${writerName}:</strong> ${content}</p>`;
            // 삭제 버튼
            // TODO 삭제버튼이 댓글 내용 오른쪽 옆에 붙었으면 좋겠음.
            comment_delete_button.textContent = "댓글삭제";
            comment_delete_button.id = "delete_button"
            comment_delete_button.addEventListener('click', () => {
                alert("정말로 댓글을 삭제하시겠습니까?");
                fetch('/api/team/' + teamId + '/tasks/' + taskId + '/comments/' + comment_id, {
                    headers: {
                        "Authorization" : auth
                    },
                    method: "DELETE"
                })
                    .then(response => {
                        if (response.ok) {
                            alert("댓글이 삭제되었습니다.");
                            window.location.reload();
                        }

                    })
                    .catch(error => {
                        alert(error.message);
                    })
            })
            commentElement.appendChild(comment_delete_button);

            // 만약 댓글에 대한 답글도 표시해야 한다면, replies를 처리할 수 있습니다.
            if (comment.replies) {
                comment.replies.forEach(reply => {
                    const replyId = reply.id;
                    const data_created_at = new Date(reply.createdAt);
                    const reply_created_at = data_created_at.getMonth() + 1 + "/" + data_created_at.getDay() + "  " + data_created_at.getHours() + ":" + data_created_at.getMinutes();
                    const replyElement = document.createElement('ul');
                    replyElement.innerHTML = `<p>└ ${reply_created_at} | <strong>${reply.writerName}:</strong> ${reply.reply}</p>`;
                    const reply_delete_button = document.createElement('button');
                    reply_delete_button.textContent = "답글삭제";
                    reply_delete_button.id = "delete_button"
                    reply_delete_button.addEventListener('click', () => {
                        alert("정말로 답글을 삭제하시겠습니까?");
                        fetch('/api/team/' + teamId + '/tasks/' + taskId + '/comments/' + comment_id + '/reply/' + replyId, {
                            headers: {
                                "Authorization" : auth
                            },
                            method: "DELETE"
                        })
                            .then(response => {
                                if (response.ok) {
                                    alert("답글이 삭제되었습니다.");
                                    window.location.reload();
                                }
                            })
                            .catch(error => {
                                alert(error.message);
                            })
                    })
                    replyElement.appendChild(reply_delete_button);
                    // 답글을 댓글 아래에 표시하거나 원하는 위치에 추가
                    commentElement.appendChild(replyElement);
                });
            }
            const reply_add_button = document.createElement('button');
            reply_add_button.id = "new_reply_button"
            reply_add_button.textContent = "답글달기";
            reply_add_button.addEventListener('click', () => {
                const new_reply_content = prompt("답글을 입력해주세요.");
                if (new_reply_content !== null) {
                    const commentId = comment.id;
                    // 답글 추가 api
                    fetch('/api/team/' + teamId + '/tasks/' + taskId + '/comments/' + commentId + '/reply', {
                        headers: {
                            "Authorization": auth,
                            "Content-Type": "application/json"
                        },
                        method: "POST",
                        body: JSON.stringify({
                            "content": new_reply_content
                        })
                    })
                        .then(response => {
                            window.location.reload()
                        })
                        .catch(error => {
                            alert(error.method);
                            window.location.href="/views/login";
                        });
                }
            })
            commentElement.appendChild(reply_add_button);
            // 표시할 요소에 추가하거나 DOM에 직접 추가
            comment_section.appendChild(commentElement);
        });
    })
    .catch(error => {
        alert(error.method);
        window.location.href="/views/login";
    });