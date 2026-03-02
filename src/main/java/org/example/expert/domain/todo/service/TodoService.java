package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.PageResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryWithQueryDSL;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoRepositoryWithQueryDSL todoRepositoryWithQueryDSL;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(Long userId, TodoSaveRequest todoSaveRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("user not found"));

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate modified_start, LocalDate modified_end) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime modified_start_at = null;
        LocalDateTime modified_end_at = null;

        if (modified_start != null) {
            modified_start_at = modified_start.atStartOfDay();
        }

        if (modified_end != null) {
            modified_end_at = modified_end.atTime(23, 59, 59);
        }

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDescWithOptions(pageable, weather, modified_start_at, modified_end_at);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    @Transactional(readOnly = true)
    public Page<TodoSearchResponse> getTodosWithQueryDSL(int page, int size, String title, String nickName, LocalDate created_start, LocalDate created_end) {
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime created_start_at = null;
        LocalDateTime created_end_at = null;

        if (created_start != null) {
            created_start_at = created_start.atStartOfDay();
        }

        if (created_end != null) {
            created_end_at = created_end.atTime(23, 59, 59);
        }

        return todoRepositoryWithQueryDSL.findAllByMultiCondition(pageable, title, nickName, created_start_at, created_end_at);
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {
        return todoRepositoryWithQueryDSL.findByIdWithUserWithQueryDSL(todoId).orElseThrow(() -> new InvalidRequestException("Todo not found"));
    }
}
