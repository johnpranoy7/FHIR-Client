package com.jy.medical.fhirclient.mapper;

import com.jy.medical.fhirclient.dto.DocumentReferenceDTO;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.DocumentReference;

public final class DocumentResourceMapper {

    private DocumentResourceMapper() {
    }

    public static DocumentReferenceDTO toSummary(DocumentReference documentReference) {
        Attachment attachment = extractAttachment(documentReference);
        String contentType = attachment != null && attachment.hasContentType()
                ? attachment.getContentType()
                : null;
        String url = attachment != null && attachment.hasUrl() ? attachment.getUrl() : null;
        boolean hasInlineData = attachment != null && attachment.hasData();
        String binaryId = extractBinaryId(url);

        return new DocumentReferenceDTO(
                documentReference.hasId() ? documentReference.getIdElement().getIdPart() : null,
                extractTitle(documentReference),
                contentType,
                url,
                binaryId,
                isDownloadable(contentType, url, hasInlineData)
        );
    }

    private static Attachment extractAttachment(DocumentReference documentReference) {
        if (!documentReference.hasContent()) {
            return null;
        }

        DocumentReference.DocumentReferenceContentComponent content = documentReference.getContentFirstRep();
        return content.hasAttachment() ? content.getAttachment() : null;
    }

    private static String extractTitle(DocumentReference documentReference) {
        if (documentReference.hasDescription()) {
            return documentReference.getDescription();
        }

        if (documentReference.hasType()) {
            if (documentReference.getType().hasText()) {
                return documentReference.getType().getText();
            }
            if (documentReference.getType().hasCoding()) {
                var coding = documentReference.getType().getCodingFirstRep();
                if (coding.hasDisplay()) {
                    return coding.getDisplay();
                }
            }
        }

        return "Untitled Document";
    }

    private static String extractBinaryId(String url) {
        if (url == null) {
            return null;
        }

        int binaryIndex = url.indexOf("Binary/");
        if (binaryIndex < 0) {
            return null;
        }

        String remainder = url.substring(binaryIndex + "Binary/".length());
        for (int i = 0; i < remainder.length(); i++) {
            char character = remainder.charAt(i);
            if (character == '/' || character == '?' || character == '#') {
                return remainder.substring(0, i);
            }
        }
        return remainder;
    }

    private static boolean isDownloadable(String contentType, String url, boolean hasInlineData) {
        if (isFhirDocument(contentType)) {
            return false;
        }

        if (hasInlineData || (url != null && url.contains("/Binary/"))) {
            return true;
        }

        if (contentType == null) {
            return false;
        }

        String lowerContentType = contentType.toLowerCase();
        return lowerContentType.contains("pdf")
                || lowerContentType.startsWith("image/")
                || lowerContentType.startsWith("text/");
    }

    private static boolean isFhirDocument(String contentType) {
        return contentType != null && contentType.toLowerCase().contains("fhir");
    }
}
