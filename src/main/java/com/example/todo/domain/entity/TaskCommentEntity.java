package com.example.todo.domain.entity;

import com.example.todo.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class TaskCommentEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MemberEntity writer;
    private String content;
    @OneToMany(mappedBy = "taskCommentEntity", fetch = FetchType.EAGER)
    private List<TaskCommentReplyEntity> replies;
    @ManyToOne
    private TaskApiEntity taskApiEntity;
}
