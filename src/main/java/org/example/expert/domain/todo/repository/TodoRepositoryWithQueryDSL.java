package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;

import java.util.Optional;

public interface TodoRepositoryWithQueryDSL {
    Optional<TodoResponse> findByIdWithUser2(Long todoId);
}
