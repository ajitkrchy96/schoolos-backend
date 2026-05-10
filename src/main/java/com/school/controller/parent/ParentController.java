package com.school.controller.parent;

import com.school.dto.parent.ParentRequestDTO;
import com.school.dto.parent.ParentResponseDTO;
import com.school.service.parent.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools/{schoolId}/parents")
@RequiredArgsConstructor
@Slf4j
public class ParentController {

    private final ParentService parentService;

    @PostMapping
    public ResponseEntity<ParentResponseDTO> createParent(
            @PathVariable Long schoolId,
            @Valid @RequestBody ParentRequestDTO requestDTO) {
        log.info("Creating parent for schoolId: {}", schoolId);
        ParentResponseDTO response = parentService.createParent(schoolId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentResponseDTO> getParentById(
            @PathVariable Long schoolId,
            @PathVariable Long id) {
        log.info("Fetching parent by id: {} for schoolId: {}", id, schoolId);
        ParentResponseDTO response = parentService.getParentById(schoolId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ParentResponseDTO> getParentByPhone(
            @PathVariable Long schoolId,
            @RequestParam String phone) {
        log.info("Fetching parent by phone: {} for schoolId: {}", phone, schoolId);
        ParentResponseDTO response = parentService.getParentByPhone(schoolId, phone);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ParentResponseDTO>> getAllParents(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all parents for schoolId: {} with page: {}, size: {}", schoolId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ParentResponseDTO> response = parentService.getAllParents(schoolId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentResponseDTO> updateParent(
            @PathVariable Long schoolId,
            @PathVariable Long id,
            @Valid @RequestBody ParentRequestDTO requestDTO) {
        log.info("Updating parent with id: {} for schoolId: {}", id, schoolId);
        ParentResponseDTO response = parentService.updateParent(schoolId, id, requestDTO);
        return ResponseEntity.ok(response);
    }
}