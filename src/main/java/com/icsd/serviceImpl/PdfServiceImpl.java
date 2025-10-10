package com.icsd.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icsd.model.Account;
import com.icsd.model.Customer;
import com.icsd.model.Transaction;
import com.icsd.service.AccountService;
import com.icsd.service.CustomerService;
import com.icsd.service.DocumentService;
import com.icsd.service.PdfService;
import com.icsd.service.TransactionService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private CustomerService customerService;

	@Autowired
	private DocumentService documentService;

	@Override
	public ByteArrayInputStream generateCustomerPdf(int customerId) throws IOException {
		log.info("inside pdf service implementation");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(out);
		PdfDocument pdfDoc = new PdfDocument(writer);
		Document document = new Document(pdfDoc);

		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		// if (!cust.isEmpty()) {
		log.info("Customer present");
		Customer customer = cust.get();
		Paragraph basic_title = new Paragraph("Basic Details");
		basic_title.setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
		basic_title.setUnderline();
		document.add(basic_title);

		Table customerTable = new Table(2);
		log.info("Create customer table to add customer data");

		customerTable.setWidth(UnitValue.createPercentValue(100));
		Cell leftCell1 = new Cell().add(new Paragraph("Customer Id : " + customer.getCustomerId())).setBorder(null)
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);
		Cell leftCell2 = new Cell().add(new Paragraph("First Name : " + customer.getFirstName())).setBorder(null)
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);
		Cell leftCell3 = new Cell().add(new Paragraph("Contact No. : " + customer.getContactNo())).setBorder(null)
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);
		Cell leftCell4 = new Cell().add(new Paragraph("Subscription Status : " + customer.getStatus())).setBorder(null)
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);

		Cell rightCell1 = new Cell().add(new Paragraph("Email Id: " + customer.getEmailId())).setBorder(null)
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);
		Cell rightCell2 = new Cell().add(new Paragraph("Last Name: " + customer.getLastName())).setBorder(null)
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);
		Cell rightCell3 = new Cell().add(new Paragraph("Registration Date : " + customer.getRegisterationDate()))
				.setBorder(null).setTextAlignment(TextAlignment.RIGHT).setFontSize(13);
		Cell rightCell4 = new Cell().add(new Paragraph("Valid Upto : " + customer.getExpiryDate())).setBorder(null)
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);

		customerTable.addCell(leftCell1);
		customerTable.addCell(rightCell1);
		customerTable.addCell(leftCell2);
		customerTable.addCell(rightCell2);
		customerTable.addCell(leftCell3);
		customerTable.addCell(rightCell3);
		customerTable.addCell(leftCell4);
		customerTable.addCell(rightCell4);

		customerTable.setMargin(15f);

		document.add(customerTable);

		// Address Details

		Paragraph address_title = new Paragraph("Address Details");
		address_title.setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
		address_title.setUnderline();
		document.add(address_title);

		Table address_table = new Table(2);
		address_table.setWidth(UnitValue.createPercentValue(100));
		log.info("Create address table to add address data");

		Cell addLeftCell1 = new Cell().add(new Paragraph("Address Id : " + customer.getAddress().getAddressId()))
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);
		Cell addLeftCell2 = new Cell().add(new Paragraph("Address Line2 : " + customer.getAddress().getAddressId()))
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);
		Cell addLeftCell3 = new Cell().add(new Paragraph("State : " + customer.getAddress().getState()))
				.setTextAlignment(TextAlignment.LEFT).setFontSize(13);

		Cell addRightCell1 = new Cell().add(new Paragraph("Address Line1 : " + customer.getAddress().getAddressLine1()))
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);
		Cell addRightCell2 = new Cell().add(new Paragraph("City : " + customer.getAddress().getCity()))
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);
		Cell addRightCell3 = new Cell().add(new Paragraph("Pincode : " + customer.getAddress().getPincode()))
				.setTextAlignment(TextAlignment.RIGHT).setFontSize(13);

		address_table.addCell(addLeftCell1);
		address_table.addCell(addRightCell1);
		address_table.addCell(addLeftCell2);
		address_table.addCell(addRightCell2);
		address_table.addCell(addLeftCell3);
		address_table.addCell(addRightCell3);

		address_table.setMargin(15f);

		document.add(address_table);

		// }

		// Account Details

		List<Account> acc = accountService.getAccountsByCustId(customerId);
		// if (!acc.isEmpty()) {
		log.info("accounts found for given customer id");

		Paragraph account_title = new Paragraph("Account Details");
		account_title.setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
		account_title.setUnderline();
		document.add(account_title);
		Table account_table = new Table(4);
		account_table.setWidth(UnitValue.createPercentValue(100));
		log.info("Create account table to add accounts data");

		account_table.addHeaderCell(new Cell().add(new Paragraph("Account Number")).setBold());
		account_table.addHeaderCell(new Cell().add(new Paragraph("Account Type")).setBold());
		account_table.addHeaderCell(new Cell().add(new Paragraph("Balance")).setBold());
		account_table.addHeaderCell(new Cell().add(new Paragraph("Opening Date")).setBold());
		log.info("data inside the table");
		for (Account account : acc) {
			account_table.addCell(new Cell().add(new Paragraph(String.valueOf(account.getAccountNumber()))));
			account_table.addCell(new Cell().add(new Paragraph(String.valueOf(account.getAccountType()))));
			account_table.addCell(new Cell().add(new Paragraph(String.valueOf(account.getOpeningBalance()))));
			account_table.addCell(new Cell().add(new Paragraph(String.valueOf(account.getOpeningDate()))));

		}
		account_table.setMargin(15f);

		document.add(account_table);
		// }

		// Document Details

		List<com.icsd.model.Document> docs = documentService.getDocumentsByCustomerId(customerId);
		// if (!docs.isEmpty()) {
		Paragraph document_title = new Paragraph("Document Details");
		document_title.setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
		document_title.setUnderline();
		document.add(document_title);
		Table document_table = new Table(5);
		document_table.setWidth(UnitValue.createPercentValue(100));
		log.info("Create document table to add document data");

		document_table.addHeaderCell(new Cell().add(new Paragraph("Document Id")).setBold());
		document_table.addHeaderCell(new Cell().add(new Paragraph("Document Type")).setBold());
		document_table.addHeaderCell(new Cell().add(new Paragraph("File Type")).setBold());
		document_table.addHeaderCell(new Cell().add(new Paragraph("File Size(in KB)")).setBold());
		document_table.addHeaderCell(new Cell().add(new Paragraph("File Path")).setBold());

		for (com.icsd.model.Document docx : docs) {
			document_table.addCell(new Cell().add(new Paragraph(String.valueOf(docx.getDocumentId()))));
			document_table.addCell(new Cell().add(new Paragraph(String.valueOf(docx.getDocumentType()))));
			document_table.addCell(new Cell().add(new Paragraph(String.valueOf(docx.getFileType()))));
			document_table.addCell(new Cell().add(new Paragraph(String.valueOf(docx.getFileSize()))));

			String url = String.valueOf(docx.getFilePath());
			Link link = (Link) new Link(url, PdfAction.createURI(url)).setFontColor(ColorConstants.BLUE).setUnderline();

			Paragraph paragraph = new Paragraph(link);

			document_table.addCell(new Cell().add(paragraph));

		}

		document_table.setMargin(15f);

		document.add(document_table);
		// }

//      Transaction Details

		List<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
		// if (!transactions.isEmpty()) {
		Paragraph transaction_title = new Paragraph("Transaction Details");
		transaction_title.setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
		transaction_title.setUnderline();
		document.add(transaction_title);
		Table transaction_table = new Table(5);
		log.info("Create transaction table to add transaction data");

		transaction_table.setWidth(UnitValue.createPercentValue(100));
		transaction_table.addHeaderCell(new Cell().add(new Paragraph("Transaction Id")).setBold());
		transaction_table.addHeaderCell(new Cell().add(new Paragraph(" Date")).setBold());
		transaction_table.addHeaderCell(new Cell().add(new Paragraph("Account Number")).setBold());
		transaction_table.addHeaderCell(new Cell().add(new Paragraph("Amount")).setBold());
		transaction_table.addHeaderCell(new Cell().add(new Paragraph("Transaction Type")).setBold());

		for (Transaction transaction : transactions) {
			transaction_table.addCell(new Cell().add(new Paragraph(String.valueOf(transaction.getTransactionId()))));
			transaction_table.addCell(new Cell().add(new Paragraph(String.valueOf(transaction.getTransactionDate()))));

		//	Account toAcc = transaction.getToAccountNumber();
//			if (toAcc == null) {
//				transaction_table.addCell(new Cell()
//						.add(new Paragraph(String.valueOf(transaction.getFromAccountNumber().getAccountNumber()))));
//
//			} else {
//				transaction_table.addCell(new Cell()
//						.add(new Paragraph(String.valueOf(transaction.getToAccountNumber().getAccountNumber()))));
//			}

			Account fromAcc = transaction.getFromAccountNumber();
			if (fromAcc == null) {
				transaction_table.addCell(new Cell()
						.add(new Paragraph(String.valueOf(transaction.getToAccountNumber().getAccountNumber()))));


			} else {
				transaction_table.addCell(new Cell()
				.add(new Paragraph(String.valueOf(transaction.getFromAccountNumber().getAccountNumber()))));

			}
			transaction_table.addCell(new Cell().add(new Paragraph(String.valueOf(transaction.getAmount()))));
			transaction_table.addCell(new Cell().add(new Paragraph(String.valueOf(transaction.getTransactionType()))));

			transaction_table.setMargin(15f);
		}
		document.add(transaction_table);
		// }

		document.close();

		return new ByteArrayInputStream(out.toByteArray());
	}

}
