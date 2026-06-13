package com.jy.medical.fhirclient.dto;

public record DocumentReferenceDTO(
        String id,
        String title,
        String contentType,
        String url,
        String binaryId,
        boolean downloadable
) {
}
