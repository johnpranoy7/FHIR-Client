package com.jy.medical.fhirclient.service;

import com.jy.medical.fhirclient.api.FHIRRestClient;
import com.jy.medical.fhirclient.dto.DocumentDownloadDTO;
import com.jy.medical.fhirclient.dto.DocumentReferenceDTO;
import com.jy.medical.fhirclient.dto.PatientDTO;
import com.jy.medical.fhirclient.mapper.DocumentResourceMapper;
import com.jy.medical.fhirclient.mapper.PatientMapper;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FhirService {

    private final FHIRRestClient restClient;

    public FhirService(FHIRRestClient restClient) {
        this.restClient = restClient;
    }

    public List<PatientDTO> searchPatientByName(String name, String family) {
        List<Patient> patients = restClient.searchPatientByName(name, family);

        if (patients == null) {
            return Collections.emptyList();
        }

        return patients.stream()
                .map(PatientMapper::toSummary)
                .toList();
    }

    public List<DocumentReferenceDTO> getDocumentsByPatient(String patientId) {
        List<DocumentReference> documents = restClient.getDocumentsByPatient(patientId);

        if (documents == null) {
            return Collections.emptyList();
        }

        return documents.stream()
                .map(DocumentResourceMapper::toSummary)
                .toList();
    }

    public Optional<DocumentDownloadDTO> downloadBinary(String binaryId) {
        return restClient.getBinaryContent(binaryId);
    }

}
