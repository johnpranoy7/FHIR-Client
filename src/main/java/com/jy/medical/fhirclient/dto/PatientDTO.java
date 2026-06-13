package com.jy.medical.fhirclient.dto;

public record PatientDTO(
        String id,
        String mrn,
        String birthDate,
        String gender,
        String name,
        String familyName
) {
}
