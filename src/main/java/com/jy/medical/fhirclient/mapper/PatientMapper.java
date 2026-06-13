package com.jy.medical.fhirclient.mapper;

import com.jy.medical.fhirclient.dto.PatientDTO;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

public final class PatientMapper {

    private PatientMapper() {
    }

    public static PatientDTO toSummary(Patient patient) {
        HumanName humanName = patient.hasName() ? patient.getNameFirstRep() : null;

        return new PatientDTO(
                patient.hasId() ? patient.getIdElement().getIdPart() : null,
                extractMrn(patient),
                patient.hasBirthDate() ? patient.getBirthDateElement().getValueAsString() : null,
                patient.hasGender() ? patient.getGenderElement().getValueAsString() : null,
                humanName != null && humanName.hasGiven() ? humanName.getGivenAsSingleString() : null,
                humanName != null && humanName.hasFamily() ? humanName.getFamily() : null
        );
    }

    private static String extractMrn(Patient patient) {
        return patient.getIdentifier().stream()
                .filter(PatientMapper::isMrnIdentifier)
                .map(Identifier::getValue)
                .findFirst()
                .orElse(null);
    }

    private static boolean isMrnIdentifier(Identifier identifier) {
        if (!identifier.hasType()) {
            return false;
        } else
            return identifier.getType().getText() != null && "MRN".equalsIgnoreCase(identifier.getType().getText());

    }
}
