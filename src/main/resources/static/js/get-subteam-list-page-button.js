const sub_team_list_button = document.getElementById("sub-team-list-button");
sub_team_list_button.addEventListener('click', () => {
    const url = "/views/team/" + teamId + "/subteam"
    window.location.href = url;
})