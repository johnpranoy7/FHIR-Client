package com.jy.medical.fhirclient.dto;

public record DocumentDownloadDTO(
        String contentType,
        byte[] data
) {
}
