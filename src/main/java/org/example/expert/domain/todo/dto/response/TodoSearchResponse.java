package org.example.expert.domain.todo.dto.response;

public record TodoSearchResponse(
        String title
        , Long commentCount
        , Long managerCount
) {
}
