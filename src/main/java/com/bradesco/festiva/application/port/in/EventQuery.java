package com.bradesco.festiva.application.port.in;

import com.bradesco.festiva.domain.model.EventStatus;

public record EventQuery(int page, int size, EventStatus status) {
}
