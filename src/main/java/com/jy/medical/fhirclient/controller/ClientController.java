package com.jy.medical.fhirclient.controller;

import com.jy.medical.fhirclient.dto.DocumentReferenceDTO;
import com.jy.medical.fhirclient.dto.PatientDTO;
import com.jy.medical.fhirclient.service.FhirService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {

    private final FhirService fhirService;

    public ClientController(FhirService fhirService) {
        this.fhirService = fhirService;
    }

    @GetMapping("/Patient")
    public List<PatientDTO> searchPatientByName(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String family) {
        return fhirService.searchPatientByName(name, family);
    }

    @GetMapping("/DocumentReference")
    public List<DocumentReferenceDTO> getDocumentsByPatient(@RequestParam String patient) {
        return fhirService.getDocumentsByPatient(patient);
    }

    @GetMapping("/Binary/{id}")
    public ResponseEntity<byte[]> downloadBinary(@PathVariable String id) {
        return fhirService.downloadBinary(id)
                .map(download -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(download.contentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                        .body(download.data()))
                .orElse(ResponseEntity.notFound().build());
    }
}
