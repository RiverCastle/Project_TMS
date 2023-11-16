package com.example.todo.domain.repository.token;

import com.example.todo.domain.entity.token.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
}
