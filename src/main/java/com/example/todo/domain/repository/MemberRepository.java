package com.example.todo.domain.repository;

import com.example.todo.domain.entity.MemberEntity;
import com.example.todo.domain.entity.TeamEntity;
import com.example.todo.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    List<MemberEntity> findAllByUserId(Long userId);
    Optional<MemberEntity> findByTeamAndUser(TeamEntity team, User user);
    List<MemberEntity> findAllByTeamId(Long teamId);
    MemberEntity findMemberEntityByTeamAndAndRole(TeamEntity team, String role);
}
