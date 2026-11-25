package com.bradesco.festiva.application.port.in;

import com.bradesco.festiva.domain.model.Event;

import java.util.UUID;

public interface ManageEventsUseCase {

    EventPage list(EventQuery query);

    Event get(UUID id);

    Event create(EventData data);

    Event update(UUID id, EventData data);

    Event cancel(UUID id);
}
