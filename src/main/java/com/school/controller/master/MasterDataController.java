package com.school.controller.master;

import com.school.dto.master.ClassResponseDTO;
import com.school.dto.master.SectionResponseDTO;
import com.school.service.master.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schools/{schoolId}/master")
public class MasterDataController {

    private final MasterDataService masterDataService;

    @GetMapping("/classes")
    public List<ClassResponseDTO> getClasses(
            @PathVariable Long schoolId
    ) {

        return masterDataService.getAllClasses(schoolId);
    }

    @GetMapping("/sections")
    public List<SectionResponseDTO> getSections(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long classId
    ) {

        return masterDataService.getAllSections(schoolId, classId);
    }

}
