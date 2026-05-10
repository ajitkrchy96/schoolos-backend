package com.school.service.recept;

import java.io.ByteArrayInputStream;

public interface PdfReceiptService {

    ByteArrayInputStream generateFeeReceipt(Long schoolId, Long paymentId);
}