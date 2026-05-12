package com.school.service.impl;

import com.school.dto.master.ClassResponseDTO;
import com.school.dto.master.SectionResponseDTO;
import com.school.model.ClassEntity;
import com.school.model.Section;
import com.school.repository.ClassEntityRepository;
import com.school.repository.SectionRepository;
import com.school.service.master.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final ClassEntityRepository classRepository;

    private final SectionRepository sectionRepository;

    @Override
    public List<ClassResponseDTO> getAllClasses(Long schoolId) {

        List<ClassEntity> classes =
                classRepository.findBySchoolIdOrderByNameAsc(schoolId);

        return classes.stream()
                .map(c -> ClassResponseDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .toList();
    }

    @Override
    public List<SectionResponseDTO> getAllSections(Long schoolId) {

        List<Section> sections =
                sectionRepository.findBySchoolIdOrderByNameAsc(schoolId);

        return sections.stream()
                .map(s -> SectionResponseDTO.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build())
                .toList();
    }

}
