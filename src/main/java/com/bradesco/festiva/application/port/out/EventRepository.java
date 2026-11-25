package com.bradesco.festiva.application.port.out;

import com.bradesco.festiva.domain.model.Event;
import com.bradesco.festiva.domain.model.EventStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    List<Event> findAll(EventStatus status, int limit, long offset);

    long count(EventStatus status);

    Optional<Event> findById(UUID id);

    Event save(Event event);
}
