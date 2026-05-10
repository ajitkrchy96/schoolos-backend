package com.school.controller.recept;


import com.school.service.recept.PdfReceiptService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools/{schoolId}/fees")
@RequiredArgsConstructor
public class PdfReceiptController {

    private final PdfReceiptService pdfReceiptService;

    @GetMapping("/receipt/{paymentId}")
    public ResponseEntity<InputStreamResource> downloadReceipt(
            @PathVariable Long schoolId,
            @PathVariable Long paymentId) {

        InputStreamResource file =
                new InputStreamResource(
                        pdfReceiptService.generateFeeReceipt(schoolId, paymentId)
                );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=receipt.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}