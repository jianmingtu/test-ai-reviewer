package ca.bc.gov.open.jag.aireviewerapi.restricteddocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ca.bc.gov.open.jag.aireviewerapi.api.RestrictedDocumentTypesApiDelegate;
import ca.bc.gov.open.jag.aireviewerapi.api.model.DocumentType;
import ca.bc.gov.open.jag.aireviewerapi.api.model.RestrictedDocumentType;
import ca.bc.gov.open.jag.aireviewerapi.document.store.RestrictedDocumentRepository;
import ca.bc.gov.open.jag.aireviewerapi.restricteddocument.mappers.RestrictedDocumentTypeMapper;

import javax.annotation.security.RolesAllowed;

@Service
public class RestrictedDocumentApiDelegateImpl implements RestrictedDocumentTypesApiDelegate {

    private final RestrictedDocumentRepository restrictedDocumentRepository;
    private final RestrictedDocumentTypeMapper restrictedDocumentTypeMapper;

    public RestrictedDocumentApiDelegateImpl(RestrictedDocumentRepository restrictedDocumentRepository,
                                             RestrictedDocumentTypeMapper restrictedDocumentTypeMapper) {
        this.restrictedDocumentRepository = restrictedDocumentRepository;
        this.restrictedDocumentTypeMapper = restrictedDocumentTypeMapper;
    }

    @Override
    @RolesAllowed("ai-reviewer-api-admin")
    public ResponseEntity<RestrictedDocumentType> createRestrictedDocumentType(DocumentType documentType) {

        if(restrictedDocumentRepository.existsByDocumentType(documentType.getType())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType restrictedDocumentType = ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType
                .builder()
                .documentType(documentType.getType())
                .documentTypeDescription(documentType.getDescription())
                .create();

        ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType savedDocumentType = restrictedDocumentRepository.save(restrictedDocumentType);

        return ResponseEntity.ok(restrictedDocumentTypeMapper.toDocumentType(savedDocumentType));

    }

    @Override
    @RolesAllowed("ai-reviewer-api-admin")
    public ResponseEntity<Void> deleteRestrictedDocumentType(UUID id) {

        if(!restrictedDocumentRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        restrictedDocumentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @Override
    @RolesAllowed("ai-reviewer-api-client")
    public ResponseEntity<RestrictedDocumentType> getRestrictedDocumentType(UUID id) {

        Optional<ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType> documentType = restrictedDocumentRepository.findById(id);

        return documentType.map(restrictedDocumentType -> ResponseEntity.ok(restrictedDocumentTypeMapper.toDocumentType(restrictedDocumentType))).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Override
    @RolesAllowed("ai-reviewer-api-client")
    public ResponseEntity<List<RestrictedDocumentType>> getRestrictedDocumentTypes() {

        List<ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType> documentTypes = restrictedDocumentRepository.findAll();

        return ResponseEntity.ok(documentTypes.stream()
                .map(x -> restrictedDocumentTypeMapper.toDocumentType(x))
                .collect(Collectors.toList()));

    }

    @Override
    @RolesAllowed("ai-reviewer-api-admin")
    public ResponseEntity<RestrictedDocumentType> updateRestrictedDocumentType(RestrictedDocumentType restrictedDocumentType) {

        if (restrictedDocumentType.getId() == null || StringUtils.isBlank(restrictedDocumentType.getId().toString())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!restrictedDocumentRepository.existsById(restrictedDocumentType.getId())) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType updateDocument = ca.bc.gov.open.jag.aireviewerapi.document.models.RestrictedDocumentType.builder()
                .documentType(restrictedDocumentType.getDocumentType().getType())
                .documentTypeDescription(restrictedDocumentType.getDocumentType().getDescription())
                .create();
        updateDocument.setId(restrictedDocumentType.getId());

        restrictedDocumentRepository.save(updateDocument);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
