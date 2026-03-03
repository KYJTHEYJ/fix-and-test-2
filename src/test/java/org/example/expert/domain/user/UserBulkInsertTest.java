package org.example.expert.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2 대신 실제 DB로
@Commit
@TestPropertySource(properties = {
        "spring.datasource.url=${DB_URL}",
        "spring.datasource.username=${DB_USER_NAME}",
        "spring.datasource.password=${DB_PASSWORD}",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver"
})
public class UserBulkInsertTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void five_million_data() {
        String sql = "INSERT INTO users (email, password, nickname, user_role, created_at, modified_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        int totalDataCount = 5_000_000;
        int batchSize = 100_000;
        int batchCount = totalDataCount / batchSize;

        for (int index = 0; index < batchCount; index++) {
            int batchIndex = index;
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                    LocalDateTime now = LocalDateTime.now();

                    int individualIndex = batchIndex * batchSize + i + 1;
                    ps.setString(1, "user-" + individualIndex + "@test.com");
                    ps.setString(2, "test1234");
                    ps.setString(3, "nick-" + individualIndex);
                    ps.setString(4, "ADMIN");
                    ps.setObject(5, now);
                    ps.setObject(6, now);
                }

                @Override
                public int getBatchSize() {
                    return batchSize;
                }
            });
        }
    }
}
