package com.school.service.master;

import com.school.dto.master.ClassResponseDTO;
import com.school.dto.master.SectionResponseDTO;

import java.util.List;

public interface MasterDataService {

List<ClassResponseDTO> getAllClasses(Long schoolId);

List<SectionResponseDTO> getAllSections(Long schoolId);

}
