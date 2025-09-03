package com.icsd.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface PdfService {

//	ByteArrayInputStream generateCustomerPdf(Customer customer) throws IOException;

	ByteArrayInputStream generateCustomerPdf(int customerId) throws IOException;

}
