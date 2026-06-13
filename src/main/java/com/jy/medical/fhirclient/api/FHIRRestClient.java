package com.jy.medical.fhirclient.api;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.jy.medical.fhirclient.dto.DocumentDownloadDTO;

import ca.uhn.fhir.context.FhirContext;

@Component
public class FHIRRestClient {
    private final RestClient restClient;
    private final FhirContext fhirContext;

    private static final String HOSPITAL_A_FHIR_BASE_URL = "http://hapi.fhir.org/baseR4/";

    public FHIRRestClient(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
        this.restClient = RestClient.builder()
                .baseUrl(HOSPITAL_A_FHIR_BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<Patient> searchPatientByName(String name, String family) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/Patient");

        if (name != null && !name.isBlank()) {
            uriBuilder.queryParam("name", name);
        }
        if (family != null && !family.isBlank()) {
            uriBuilder.queryParam("family", family);
        }
        uriBuilder.queryParam("_count", "10");

        //TODO: Add Pagination for the UI

        String json = restClient.get()
                .uri(uriBuilder.build().toUriString())
                .retrieve()
                .body(String.class);

        Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, json);

        if (bundle.getEntry() == null || bundle.getEntry().isEmpty()) {
            return null;
        }

        return bundle.getEntry().stream()
                .map(entry -> (Patient) entry.getResource())
                .toList();
    }


    public List<DocumentReference> getDocumentsByPatient(String id) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/DocumentReference");

        if (id != null && !id.isBlank()) {
            uriBuilder.queryParam("patient", id);
        }

        String json = restClient.get()
                .uri(uriBuilder.build().toUriString())
                .retrieve()
                .body(String.class);

        Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, json);

        if (bundle.getEntry() == null || bundle.getEntry().isEmpty()) {
            return null;
        }

        return bundle.getEntry().stream()
                .map(entry -> (DocumentReference) entry.getResource())
                .toList();
    }

    public Optional<DocumentDownloadDTO> getBinaryContent(String binaryId) {
        return restClient.get()
                .uri("/Binary/{id}", binaryId)
                .accept(MediaType.parseMediaType("application/fhir+json"), MediaType.ALL)
                .exchange((request, response) -> {
                    byte[] body = response.bodyTo(byte[].class);
                    if (body == null || body.length == 0) {
                        return Optional.empty();
                    }

                    if (body[0] == '{') {
                        String json = new String(body, StandardCharsets.UTF_8);
                        Binary binary = fhirContext.newJsonParser().parseResource(Binary.class, json);
                        if (!binary.hasData()) {
                            return Optional.empty();
                        }

                        String contentType = binary.hasContentType()
                                ? binary.getContentType()
                                : "application/octet-stream";
                        return Optional.of(new DocumentDownloadDTO(contentType, binary.getData()));
                    }

                    MediaType responseType = response.getHeaders().getContentType();
                    String contentType = responseType != null
                            ? responseType.toString()
                            : "application/octet-stream";
                    return Optional.of(new DocumentDownloadDTO(contentType, body));
                });
    }

}
