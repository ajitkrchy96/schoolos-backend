package com.school.service.impl;

import com.school.dto.parent.ParentRequestDTO;
import com.school.dto.parent.ParentResponseDTO;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.ValidationException;
import com.school.mapper.ParentMapper;
import com.school.model.Parent;
import com.school.model.School;
import com.school.repository.ParentRepository;
import com.school.repository.SchoolRepository;
import com.school.service.parent.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final SchoolRepository schoolRepository;
    private final ParentMapper parentMapper;

    @Override
    @Transactional
    public ParentResponseDTO createParent(Long schoolId, ParentRequestDTO requestDTO) {
        log.info("Creating parent for schoolId: {}, phone: {}", schoolId, requestDTO.getPhone());

        // Validate school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Check if parent already exists with this phone in the school
        Optional<Parent> existingParent = parentRepository.findByPhoneAndSchoolId(requestDTO.getPhone(), schoolId);
        if (existingParent.isPresent()) {
            log.info("Parent already exists with phone: {} for schoolId: {}, returning existing", requestDTO.getPhone(), schoolId);
            return parentMapper.toResponseDTO(existingParent.get());
        }

        // Create new parent
        Parent parent = parentMapper.toEntity(requestDTO);
        parent.setSchool(school);
        parent = parentRepository.save(parent);

        log.info("Parent created successfully with id: {} for schoolId: {}", parent.getId(), schoolId);
        return parentMapper.toResponseDTO(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentResponseDTO getParentById(Long schoolId, Long parentId) {
        log.info("Fetching parent by id: {} for schoolId: {}", parentId, schoolId);

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }

        Parent parent = parentRepository.findByIdAndSchoolId(parentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + parentId + " for school: " + schoolId));

        return parentMapper.toResponseDTO(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentResponseDTO getParentByPhone(Long schoolId, String phone) {
        log.info("Fetching parent by phone: {} for schoolId: {}", phone, schoolId);

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }

        Parent parent = parentRepository.findByPhoneAndSchoolId(phone, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with phone: " + phone + " for school: " + schoolId));

        return parentMapper.toResponseDTO(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParentResponseDTO> getAllParents(Long schoolId, Pageable pageable) {
        log.info("Fetching all parents for schoolId: {} with pageable: {}", schoolId, pageable);

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }

        Page<Parent> parents = parentRepository.findBySchoolId(schoolId, pageable);
        return parents.map(parentMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public ParentResponseDTO updateParent(Long schoolId, Long parentId, ParentRequestDTO requestDTO) {
        log.info("Updating parent with id: {} for schoolId: {}", parentId, schoolId);

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }

        Parent parent = parentRepository.findByIdAndSchoolId(parentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with id: " + parentId + " for school: " + schoolId));

        // Check if phone is being changed and if it conflicts with another parent
        if (!parent.getPhone().equals(requestDTO.getPhone())) {
            Optional<Parent> existingParent = parentRepository.findByPhoneAndSchoolId(requestDTO.getPhone(), schoolId);
            if (existingParent.isPresent() && !existingParent.get().getId().equals(parentId)) {
                throw new ValidationException("Phone number already exists for another parent in this school");
            }
        }

        parentMapper.updateEntityFromDTO(parent, requestDTO);
        parent = parentRepository.save(parent);

        log.info("Parent updated successfully with id: {} for schoolId: {}", parent.getId(), schoolId);
        return parentMapper.toResponseDTO(parent);
    }
}