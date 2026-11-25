package com.bradesco.festiva.adapters.out.persistence;

import com.bradesco.festiva.application.port.out.EventRepository;
import com.bradesco.festiva.domain.model.Event;
import com.bradesco.festiva.domain.model.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcEventRepository implements EventRepository {

    private static final RowMapper<Event> EVENT_ROW_MAPPER = (resultSet, rowNumber) -> new Event(
            resultSet.getObject("id", UUID.class),
            resultSet.getString("title"),
            resultSet.getString("description"),
            resultSet.getTimestamp("starts_at").toInstant(),
            resultSet.getTimestamp("ends_at").toInstant(),
            resultSet.getString("venue"),
            resultSet.getInt("capacity"),
            EventStatus.valueOf(resultSet.getString("status")),
            resultSet.getTimestamp("created_at").toInstant(),
            resultSet.getTimestamp("updated_at").toInstant());

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> findAll(EventStatus status, int limit, long offset) {
        if (status == null) {
            return jdbcTemplate.query("""
                    SELECT id, title, description, starts_at, ends_at, venue, capacity, status, created_at, updated_at
                    FROM events
                    ORDER BY starts_at, id
                    LIMIT ? OFFSET ?
                    """, EVENT_ROW_MAPPER, limit, offset);
        }
        return jdbcTemplate.query("""
                SELECT id, title, description, starts_at, ends_at, venue, capacity, status, created_at, updated_at
                FROM events
                WHERE status = ?
                ORDER BY starts_at, id
                LIMIT ? OFFSET ?
                """, EVENT_ROW_MAPPER, status.name(), limit, offset);
    }

    @Override
    public long count(EventStatus status) {
        if (status == null) {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events", Long.class);
            return count == null ? 0 : count;
        }
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events WHERE status = ?",
                Long.class,
                status.name());
        return count == null ? 0 : count;
    }

    @Override
    public Optional<Event> findById(UUID id) {
        List<Event> events = jdbcTemplate.query("""
                SELECT id, title, description, starts_at, ends_at, venue, capacity, status, created_at, updated_at
                FROM events
                WHERE id = ?
                """, EVENT_ROW_MAPPER, id);
        return events.stream().findFirst();
    }

    @Override
    public Event save(Event event) {
        jdbcTemplate.update("""
                INSERT INTO events (
                    id, title, description, starts_at, ends_at, venue, capacity, status, created_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    title = EXCLUDED.title,
                    description = EXCLUDED.description,
                    starts_at = EXCLUDED.starts_at,
                    ends_at = EXCLUDED.ends_at,
                    venue = EXCLUDED.venue,
                    capacity = EXCLUDED.capacity,
                    status = EXCLUDED.status,
                    updated_at = EXCLUDED.updated_at
                """,
                event.id(),
                event.title(),
                event.description(),
                Timestamp.from(event.startsAt()),
                Timestamp.from(event.endsAt()),
                event.venue(),
                event.capacity(),
                event.status().name(),
                Timestamp.from(event.createdAt()),
                Timestamp.from(event.updatedAt()));
        return event;
    }
}
