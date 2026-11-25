package com.bradesco.festiva.application.port.in;

import com.bradesco.festiva.domain.model.Event;

import java.util.List;

public record EventPage(List<Event> items, int page, int size, long totalElements) {

    public EventPage {
        items = List.copyOf(items);
    }

    public int totalPages() {
        return totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
    }
}
