package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryWithQueryDSL {
    Optional<TodoResponse> findByIdWithUserWithQueryDSL(Long todoId);
    Page<TodoSearchResponse> findAllByMultiCondition(Pageable pageable, String title, String nickName, LocalDateTime created_start_at, LocalDateTime created_end_at);
}
