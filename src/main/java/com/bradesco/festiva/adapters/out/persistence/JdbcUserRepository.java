package com.bradesco.festiva.adapters.out.persistence;

import com.bradesco.festiva.application.port.out.UserRepository;
import com.bradesco.festiva.domain.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        List<UserAccount> accounts = jdbcTemplate.query("""
                SELECT id, email, password_hash, created_at
                FROM app_user
                WHERE email = ?
                """,
                (resultSet, rowNumber) -> new UserAccount(
                        resultSet.getObject("id", java.util.UUID.class),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash"),
                        resultSet.getTimestamp("created_at").toInstant()),
                email);
        return accounts.stream().findFirst();
    }

    @Override
    public UserAccount save(UserAccount account) {
        jdbcTemplate.update("""
                INSERT INTO app_user (id, email, password_hash, created_at)
                VALUES (?, ?, ?, ?)
                """,
                account.id(),
                account.email(),
                account.passwordHash(),
                Timestamp.from(account.createdAt()));
        return account;
    }
}
