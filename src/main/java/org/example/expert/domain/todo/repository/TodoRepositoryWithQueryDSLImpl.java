package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryWithQueryDSLImpl implements TodoRepositoryWithQueryDSL {
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional(readOnly = true)
    @Override
    public Optional<TodoResponse> findByIdWithUser2(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(Projections.constructor(TodoResponse.class
                        , todo.id
                        , todo.title
                        , todo.contents
                        , todo.weather
                        , Projections.constructor(UserResponse.class
                                , user.id
                                , user.email
                                , user.nickname
                        )
                        , todo.createdAt
                        , todo.modifiedAt)
                )
                .from(todo)
                .leftJoin(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchOne());
    }
}
