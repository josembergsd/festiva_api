package com.bradesco.festiva.application.service;

import com.bradesco.festiva.application.port.in.EventData;
import com.bradesco.festiva.application.port.in.EventQuery;
import com.bradesco.festiva.application.port.out.EventRepository;
import com.bradesco.festiva.domain.model.Event;
import com.bradesco.festiva.domain.model.EventNotFoundException;
import com.bradesco.festiva.domain.model.EventStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageEventsServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ManageEventsService service;

    @Test
    void listUsesRequestedPageAndStatus() {
        when(eventRepository.findAll(EventStatus.SCHEDULED, 20, 40)).thenReturn(java.util.List.of());
        when(eventRepository.count(EventStatus.SCHEDULED)).thenReturn(41L);

        var result = service.list(new EventQuery(2, 20, EventStatus.SCHEDULED));

        assertEquals(2, result.page());
        assertEquals(3, result.totalPages());
        assertEquals(41, result.totalElements());
    }

    @Test
    void createUsesClockAndPersistsScheduledEvent() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        ManageEventsService fixedClockService = new ManageEventsService(eventRepository, clock);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        EventData data = new EventData(
                "Festival",
                null,
                NOW.plusSeconds(60),
                NOW.plusSeconds(3600),
                "Main Hall",
                100);

        Event event = fixedClockService.create(data);

        assertEquals(NOW, event.createdAt());
        assertEquals(EventStatus.SCHEDULED, event.status());
        verify(eventRepository).save(event);
    }

    @Test
    void updateFailsWhenEventDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> service.update(
                id,
                new EventData("Festival", null, NOW, NOW.plusSeconds(60), "Main Hall", 100)));
    }
}
