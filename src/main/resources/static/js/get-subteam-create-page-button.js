const sub_team_create_button = document.getElementById("sub-team-create-button");
sub_team_create_button.addEventListener('click', () => {
    const url = "/views/team/" + teamId + "/new-subTeam";
    location.href = url;
});