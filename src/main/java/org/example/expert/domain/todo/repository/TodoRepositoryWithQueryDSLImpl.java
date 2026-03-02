package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryWithQueryDSLImpl implements TodoRepositoryWithQueryDSL {
    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression titleContains(String title) {
        return (title != null && !title.isBlank()) ? todo.title.contains(title) : null;
    }

    private BooleanExpression nickNameContains(String nickName) {
        return (nickName != null && !nickName.isBlank()) ? user.nickname.contains(nickName) : null;
    }

    private BooleanExpression createAtGoe(LocalDateTime createdAt) {
        return createdAt != null ? todo.createdAt.goe(createdAt) : null;
    }

    private BooleanExpression createAtLoe(LocalDateTime createdAt) {
        return createdAt != null ? todo.createdAt.loe(createdAt) : null;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TodoResponse> findByIdWithUserWithQueryDSL(Long todoId) {
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

    @Override
    public Page<TodoSearchResponse> findAllByMultiCondition(Pageable pageable, String title, String nickName, LocalDateTime created_start_at, LocalDateTime created_end_at) {
        List<TodoSearchResponse> todoSearchResponseList = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class
                                , todo.title
                                , comment.count()
                                , manager.count()
                        )
                )
                .from(todo)
                .leftJoin(todo.user, user)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.managers, manager)
                .where(titleContains(title)
                        , nickNameContains(nickName)
                        , createAtGoe(created_start_at)
                        , createAtLoe(created_end_at))
                .groupBy(todo.title, todo.createdAt)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(todo.id.count())
                .from(todo)
                .leftJoin(todo.user, user)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.managers, manager)
                .where(titleContains(title)
                        , nickNameContains(nickName)
                        , createAtGoe(created_start_at)
                        , createAtLoe(created_end_at))
                .groupBy(todo.id)
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        return new PageImpl<>(todoSearchResponseList, pageable, totalCount);
    }
}
