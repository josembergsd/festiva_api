package com.bradesco.festiva.application.service;

import com.bradesco.festiva.application.port.in.EventData;
import com.bradesco.festiva.application.port.in.EventPage;
import com.bradesco.festiva.application.port.in.EventQuery;
import com.bradesco.festiva.application.port.in.ManageEventsUseCase;
import com.bradesco.festiva.application.port.out.EventRepository;
import com.bradesco.festiva.domain.model.Event;
import com.bradesco.festiva.domain.model.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageEventsService implements ManageEventsUseCase {

    private final EventRepository eventRepository;
    private final Clock clock;

    @Override
    public EventPage list(EventQuery query) {
        long offset = (long) query.page() * query.size();
        return new EventPage(
                eventRepository.findAll(query.status(), query.size(), offset),
                query.page(),
                query.size(),
                eventRepository.count(query.status()));
    }

    @Override
    public Event get(UUID id) {
        return findEvent(id);
    }

    @Override
    public Event create(EventData data) {
        Event event = Event.create(
                data.title(),
                data.description(),
                data.startsAt(),
                data.endsAt(),
                data.venue(),
                data.capacity(),
                clock.instant());
        return eventRepository.save(event);
    }

    @Override
    public Event update(UUID id, EventData data) {
        Event current = findEvent(id);
        return eventRepository.save(current.update(
                data.title(),
                data.description(),
                data.startsAt(),
                data.endsAt(),
                data.venue(),
                data.capacity(),
                clock.instant()));
    }

    @Override
    public Event cancel(UUID id) {
        return eventRepository.save(findEvent(id).cancel(clock.instant()));
    }

    private Event findEvent(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }
}
