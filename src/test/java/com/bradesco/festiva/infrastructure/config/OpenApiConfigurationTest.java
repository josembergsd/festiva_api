package com.bradesco.festiva.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigurationTest {

    @Test
    void definesApiMetadataTagsAndBearerScheme() {
        var openApi = new OpenApiConfiguration().festivaOpenApi();

        assertEquals("Festiva Event API", openApi.getInfo().getTitle());
        assertEquals("1.0.0", openApi.getInfo().getVersion());
        assertEquals(2, openApi.getTags().size());
        assertNotNull(openApi.getComponents().getSecuritySchemes().get("bearerAuth"));
        assertEquals(
                "bearer",
                openApi.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
    }
}
