package com.bradesco.festiva.adapters.in.web.dto;

import com.bradesco.festiva.application.port.in.EventPage;

import java.util.List;

public record EventPageResponse(
        List<EventResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static EventPageResponse from(EventPage page) {
        return new EventPageResponse(
                page.items().stream().map(EventResponse::from).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }
}
