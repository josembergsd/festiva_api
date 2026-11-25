package com.bradesco.festiva.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void createTrimsTextAndStartsScheduled() {
        Event event = Event.create(
                "  Festival  ",
                null,
                NOW.plusSeconds(60),
                NOW.plusSeconds(3600),
                "  Main Hall ",
                250,
                NOW);

        assertEquals("Festival", event.title());
        assertEquals("Main Hall", event.venue());
        assertEquals(EventStatus.SCHEDULED, event.status());
        assertEquals(NOW, event.createdAt());
    }

    @Test
    void rejectsEndAtOrBeforeStartAt() {
        assertThrows(InvalidEventException.class, () -> Event.create(
                "Festival",
                null,
                NOW,
                NOW,
                "Main Hall",
                250,
                NOW));
    }

    @Test
    void cancelPreservesEventDataAndUpdatesStatus() {
        Event event = Event.create(
                "Festival",
                "Music",
                NOW.plusSeconds(60),
                NOW.plusSeconds(3600),
                "Main Hall",
                250,
                NOW);

        Event cancelled = event.cancel(NOW.plusSeconds(10));

        assertEquals(event.id(), cancelled.id());
        assertEquals(event.title(), cancelled.title());
        assertEquals(EventStatus.CANCELLED, cancelled.status());
        assertEquals(NOW.plusSeconds(10), cancelled.updatedAt());
    }

    @Test
    void rejectsBlankTextAndNonPositiveCapacity() {
        assertThrows(InvalidEventException.class, () -> Event.create(
                " ",
                null,
                NOW,
                NOW.plusSeconds(60),
                "Main Hall",
                1,
                NOW));

        assertThrows(InvalidEventException.class, () -> Event.create(
                "Festival",
                null,
                NOW,
                NOW.plusSeconds(60),
                "Main Hall",
                0,
                NOW));
    }
}
