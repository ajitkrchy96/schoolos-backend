package com.school.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import com.school.exception.ResourceNotFoundException;
import com.school.model.FeePayment;
import com.school.repository.FeePaymentRepository;

import com.school.service.recept.PdfReceiptService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfReceiptServiceImpl implements PdfReceiptService {

    private final FeePaymentRepository feePaymentRepository;

    @Transactional(readOnly = true)
    @Override
    public ByteArrayInputStream generateFeeReceipt(Long schoolId, Long paymentId) {

        FeePayment payment = feePaymentRepository.findReceiptDetails(paymentId, schoolId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("FeePayment", "id", paymentId));

        if (!payment.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("Invalid school access");
        }

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            Paragraph title = new Paragraph("School Fee Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Receipt No: " + payment.getReceiptNumber()));
            document.add(new Paragraph("Student: "
                    + payment.getStudentFee().getStudent().getFirstName()
                    + " "
                    + payment.getStudentFee().getStudent().getLastName()));

            document.add(new Paragraph("Amount Paid: Rs. " + payment.getAmount()));

            document.add(new Paragraph("Payment Mode: "
                    + payment.getPaymentMode()));

            document.add(new Paragraph("Payment Date: "
                    + payment.getPaymentDate()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for payment."));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}