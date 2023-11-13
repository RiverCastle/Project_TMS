package com.example.todo.domain.repository;

import com.example.todo.domain.entity.TaskApiEntity;
import com.example.todo.domain.entity.TaskCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    Page<TaskCommentEntity> findAllByTaskApiEntityAndDeletedAtIsNull(TaskApiEntity taskApiEntity, Pageable pageable);

}
