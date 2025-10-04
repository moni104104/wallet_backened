package com.icsd.helper;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.AddressDto;
import com.icsd.dto.request.CustomerExcelImportDto;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.request.RowError;
import com.icsd.model.Address;
import com.icsd.model.Customer;
import com.icsd.model.Gender;
import com.icsd.model.SubscriptionStatus;
import com.icsd.repo.AddressRepo;
import com.icsd.service.AddressClientService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data

public class ExcelHelper {

    //	Check excel file format
//	@Autowired
//	static AddressRepo addressRepo;
    @Value("${customer.expiry.days}")
    static int expiryDays;

    @Autowired
    static Validator validator;

    /**
     * This function checks if the file is in excel format or not
     *
     * @param file
     * @return
     */
    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }
    }


    public static List<Customer> convertExcelToListOfCustomers(InputStream is, CustomerExcelImportDto excelDto) {

        log.info("Inside Excel helper");
        AddressRepo addressRepo = excelDto.getAddressRepo();
        int expiryDays = excelDto.getExpiryDays();
        Validator validator = excelDto.getValidator();
        AddressClientService addressClientService = excelDto.getAddressClientService();
        List<RowError> rowErrors = excelDto.getRowErrors();
        Set<String> dbEmails = excelDto.getExistingEmails();
        List<Customer> customerList = new ArrayList<>();
        Set<String> fileEmails = new HashSet<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheet(messages.SHEET);
            int rowNumber = 0;

            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (rowNumber++ == 0) {
                    continue;

                }
                try {
                    CustomerRequestDto dto = new CustomerRequestDto();
                    AddressDto addressDto = new AddressDto();


                    int totalColumns = sheet.getRow(0).getLastCellNum();
                    log.info("get the total columns from excel ");
                    List<String> errorsForThisRow = new ArrayList<>();

                    for (int cid = 0; cid < totalColumns; cid++) {
                        Cell cell = row.getCell(cid, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String cellValue = getCellValueAsString(cell).trim();

                        switch (cid) {
                            case 0:
                                dto.setFirstName(cellValue);
                                break;
                            case 1:
                                dto.setLastName(cellValue);
                                break;
                            case 2:
                                dto.setEmailId(cellValue.toLowerCase());
                                break;
                            case 3:
                                dto.setContactNo(cellValue);
                                break;
                            case 4:
                                dto.setGender(parseGender(cellValue));
                                break;
                            case 5:
                                dto.setPassword(cellValue);
                                break;
                            case 6:
                                dto.setConfirmPassword(cellValue);
                                break;
                            case 7:
                                addressDto.setAddressLine1(cellValue);
                                break;
                            case 8:
                                addressDto.setAddressLine2(cellValue);
                                break;
                            case 9:
                                addressDto.setCity(cellValue);
                                break;
                            case 10:
                                addressDto.setState(cellValue);
                                break;
                            case 11:
                                addressDto.setPincode(cellValue);
                                break;

                            case 12:
                                dto.setStatus(parseStatus(cellValue));
                                break;
                        }
                    }
                    dto.setAddress(addressDto);

                    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                        errorsForThisRow.add(messages.PWD_NOT_MATCH);
                    }

                    Set<ConstraintViolation<CustomerRequestDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        log.info("constraint violations");
                        String errorMsg = violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining("; "));
                        errorsForThisRow.add(messages.DATA_VALIDATION + errorMsg);

                    }

                    String email = dto.getEmailId();
                    if (fileEmails.contains(email)) {
                        errorsForThisRow.add(messages.DUPLICATE_EMAILS_FILE + email);

                    }
                    fileEmails.add(email);

                    if (dbEmails.contains(email)) {
                        errorsForThisRow.add(messages.DUPLICATE_EMAILS + email);

                    }

                    if (!errorsForThisRow.isEmpty()) {
                        String combinedError = String.join(" | ", errorsForThisRow);
                        rowErrors.add(new RowError(rowNumber, combinedError));
                        continue;
                    }


                    AddressDto addressDto2 = AddressDto.builder()
                            .addressLine1(addressDto.getAddressLine1())
                            .addressLine2(addressDto.getAddressLine2())
                            .city(addressDto.getCity())
                            .state(addressDto.getState())
                            .pincode(addressDto.getPincode())
                            .build();


                    Address savedAddress;
                    try {
                        savedAddress = addressClientService.saveAddress(addressDto2);
                        log.info("Address saved successfully with ID: {}", savedAddress.getAddressId());

                    } catch (Exception ex) {
                        String errorMsg = "Address service call failed: " + ex.getMessage();
                        rowErrors.add(new RowError(rowNumber, errorMsg));
                        log.warn("Skipping row {} due to address service error: {}", rowNumber, errorMsg);
                        continue;
                    }


                    log.info("adress dto after saved " + savedAddress.getAddressId());
//					Address savedAddress = addressRepo.save(address);
                    log.info("saved address to database");
                    Customer customer = new Customer();
                    customer.setFirstName(dto.getFirstName());
                    customer.setLastName(dto.getLastName());
                    customer.setEmailId(dto.getEmailId());
                    customer.setContactNo(dto.getContactNo());
                    customer.setGender(dto.getGender());
                    customer.setPassword(dto.getPassword());
                    customer.setConfirmPassword(dto.getConfirmPassword());
                    customer.setStatus(dto.getStatus());
                    customer.setRegistrationDate(LocalDate.now());
                    customer.setExpiryDate(LocalDate.now().plusDays(expiryDays));
                    customer.setAddress(savedAddress);

                    customerList.add(customer);
                    log.info("Customer added from row " + rowNumber + ": " + email);

                } catch (Exception e) {
                    rowErrors.add(new RowError(rowNumber, "Unexpected error: " + e.getMessage()));
                    log.warn("Skip row " + rowNumber + " due to error: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(messages.RUN_TIME_EXCEPTION + e.getMessage(), e);
        }

        return customerList;
    }

    private static Gender parseGender(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return Gender.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static SubscriptionStatus parseStatus(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return SubscriptionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();

            case BLANK:
                return "";
            default:
                return "";
        }
    }

}
