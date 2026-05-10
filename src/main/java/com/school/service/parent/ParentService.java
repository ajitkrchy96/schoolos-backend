package com.school.service.parent;

import com.school.dto.parent.ParentRequestDTO;
import com.school.dto.parent.ParentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParentService {

    ParentResponseDTO createParent(Long schoolId, ParentRequestDTO requestDTO);

    ParentResponseDTO getParentById(Long schoolId, Long parentId);

    ParentResponseDTO getParentByPhone(Long schoolId, String phone);

    Page<ParentResponseDTO> getAllParents(Long schoolId, Pageable pageable);

    ParentResponseDTO updateParent(Long schoolId, Long parentId, ParentRequestDTO requestDTO);
}