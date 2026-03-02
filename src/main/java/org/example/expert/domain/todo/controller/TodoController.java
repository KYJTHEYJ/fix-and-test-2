package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.PageResponse;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(userId, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) LocalDate modified_start,
            @RequestParam(required = false) LocalDate modified_end
            ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, modified_start, modified_end));
    }

    @GetMapping("/v2/todos")
    public ResponseEntity<PageResponse<TodoSearchResponse>> getTodosWithQueryDSL(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) LocalDate created_start,
            @RequestParam(required = false) LocalDate created_end
    ) {
        return ResponseEntity.ok(PageResponse.register(todoService.getTodosWithQueryDSL(page, size, title, nickName, created_start, created_end)));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }
}
