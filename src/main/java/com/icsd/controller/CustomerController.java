package com.icsd.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.CustomerLoginDTO;
//import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.helper.ExcelHelper;
import com.icsd.model.Customer;
import com.icsd.service.CustomerService;
import com.icsd.service.PdfService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(value = "*")
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PdfService pdfService;

	@PostMapping(value = "/customer")
	public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequest) {
		log.info("inside create method of customer controller");

		Integer generatedCustomerId = customerService.createCustomer(customerRequest);

		ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE, generatedCustomerId);
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

	}

//	@GetMapping("customer/id/{customerId}")
//	public ResponseEntity<ApiResponse> getCustomerByCustomerId(@PathVariable("customerId") int customerId) {
//		log.info("inside get method of customer controller");
//		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
//		if (!cust.isPresent()) {
//			log.info("no record found");
//			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
//			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
//		}
//		log.info("customer found for given customer id");
//		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, cust);
//
//		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
//
//	}

//	@GetMapping("customer/pdf/{customerId}")
//	public ResponseEntity<byte[]> getCustomerPdf(@PathVariable("customerId") int customerId) throws IOException {
//		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
//		if (!cust.isPresent()) {
//			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
//		}
//		Customer customer = cust.get();
//		ByteArrayInputStream bis = pdfService.generateCustomerPdf(customer);
//		log.info("customer found for given customer id");
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename= customer_" + customerId + ".pdf");
//
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());
//
//	}

	@GetMapping("customer/pdf/{customerId}")
	public ResponseEntity<byte[]> getCustomerPdf(@PathVariable("customerId") int customerId) throws IOException {
		Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
		if (!cust.isPresent()) {
			log.info("customer not found");
			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
		}
		ByteArrayInputStream bis = pdfService.generateCustomerPdf(customerId);
		log.info("customer found for given customer id");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename= customer_" + customerId + ".pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());

	}

	@PostMapping(value = "/customer/excel")
	public ResponseEntity<ApiResponse> saveCustomersFromExcel(@RequestParam("file") MultipartFile file) {
		log.info("inside saveCustomersFromExcel");

		if (ExcelHelper.checkExcelFormat(file)) {
			log.info("excel file is present");
			customerService.saveCustomersFromExcel(file);
			ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

		} else {
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), "not an excel file");
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}

	}

	@PostMapping("/downloadTemplate")
	public ResponseEntity<byte[]> downloadExcelTemplate(@RequestBody List<String> headers) {
		log.info("inside customer controller");
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(messages.SHEET);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);

			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < headers.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellStyle(headerStyle);

				cell.setCellValue(headers.get(i));
				sheet.autoSizeColumn(i);
			}
			log.info("header created");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			byte[] bytes = out.toByteArray();
			log.info("convreted into byte array");
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			responseHeaders.setContentDisposition(ContentDisposition.attachment().filename("template.xlsx").build());

			return new ResponseEntity<>(bytes, responseHeaders, HttpStatus.OK);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "/customer/isValid")
	public ResponseEntity<ApiResponse> isValidUser(@RequestBody @Valid CustomerLoginDTO customerLogin) {
		log.info("authenticating user - valid or not ");
		System.out.println(customerLogin);
		Customer customer = customerService.isValidCustByEmailidAndPwd(customerLogin);
		ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_USER, customer);

		return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
	}

	@GetMapping("/customer/{emailId}")
	public ResponseEntity<ApiResponse> getCustomerDataByEmailId(@PathVariable("emailId") String emailId) {
		Optional<Customer> cust = customerService.getCustomerDataByEmailId(emailId);
		if (!cust.isPresent()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		log.info("customer found for given email id");
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, cust);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);

	}

	@GetMapping(value = "/customer/lastName/{lastName}")
	public ResponseEntity<ApiResponse> findByLastNameDTOResponse(@PathVariable("lastName") String lnm) {
		log.info("inside controller findByLastName lnm=" + lnm);
		List<CustomerFnmLnmGenderDTO> list = customerService.findByLastName(lnm);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameIgnore/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameIgnoreCase(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameIgnoreCase fn=" + fn);
		List<Customer> list = customerService.findByFirstNameIgnoreCase(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameLike/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameLike(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameLike fn=" + fn);
		List<Customer> list = customerService.findByFirstNameLike(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameContaining/{firstName}")
	public ResponseEntity<ApiResponse> findByFirstNameContaining(@PathVariable("firstName") String fn) {
		log.info("inside controller findByFirstNameContaining fn=" + fn);
		List<Customer> list = customerService.findByFirstNameContaining(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameContains/{firstName}")
	public ResponseEntity<ApiResponse> findByfirstNameContains(@PathVariable("firstName") String fn) {
		log.info("inside controller findByfirstNameContains fn=" + fn);
		List<Customer> list = customerService.findByfirstNameContains(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

	@GetMapping(value = "/customer/firstNameIsContaining/{firstName}")
	public ResponseEntity<ApiResponse> findByfirstNameIsContaining(@PathVariable("firstName") String fn) {
		log.info("inside controller findByfirstNameIsContaining fn=" + fn);
		List<Customer> list = customerService.findByfirstNameIsContaining(fn);
		if (list.isEmpty()) {
			log.info("no record found");
			ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
			return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
		}
		ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, list);

		return new ResponseEntity<>(apiresponse, HttpStatus.OK);
	}

}
