package com.icsd.controller;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.AdminLoginDto;
import com.icsd.dto.request.AdminRequestDto;
import com.icsd.dto.response.LoginResponseDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.helper.ExcelHelper;
import com.icsd.model.Admin;
import com.icsd.security.AdminUserDetailsService;
import com.icsd.security.JwtUtils;
import com.icsd.service.AdminService;
import com.icsd.service.PdfService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j

@RestController
@RequestMapping(value = "/api")
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    PdfService pdfService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminUserDetailsService adminUserDetailsService;

//    @PostMapping(value = "/admin/isValid")
//    public ResponseEntity<ApiResponse> isValidAdmin(@RequestBody @Valid AdminLoginDto adminLogin) {
//        log.info("authenticating admin - valid or not ");
//        Admin admin = adminService.isValidAdminByEmailidAndPwd(adminLogin);
//        ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_ADMIN, admin);
//        return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.OK);
//    }

    @PostMapping(value = "/admin/isValid")
    public ResponseEntity<ApiResponse> isValidUser(@RequestBody @Valid AdminLoginDto adminLogin) {
        log.info("Authenticating admin via JWT security");

        try {
//            var authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            adminLogin.getEmailId(),
//                            adminLogin.getPassword()
//                    )
//            );
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            UserDetails userDetails = adminUserDetailsService.loadUserByUsername(adminLogin.getEmailId());

            if (!passwordEncoder.matches(adminLogin.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid admin credentials");
            }

            String jwtToken = jwtUtils.generateToken(userDetails.getUsername());

            Admin admin = adminService.getAdminDataByEmailId(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND));


            LoginResponseDTO responseBody = new LoginResponseDTO();
            responseBody.setToken(jwtToken);
            responseBody.setAdmin(admin);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_ADMIN, responseBody);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception ex) {
            log.error("Login failed: ", ex);
            ApiResponse errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED.value(), messages.INVALID_USER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping(value = "/admin/bulkUpload")
    public ResponseEntity<ApiResponse> saveCustomersFromExcel(@RequestParam("file") MultipartFile file) {
        log.info("inside saveCustomersFromExcel");
        if (ExcelHelper.checkExcelFormat(file)) {
            log.info("excel file is present");
            adminService.saveCustomersFromExcel(file);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE);
            return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
        } else {
            ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), "not an excel file");
            return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/admin/downloadTemplate")
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

    @PostMapping(value = "/admin")
    public ResponseEntity<ApiResponse> createAdmin(@RequestBody @Valid AdminRequestDto adminRequest) {
        log.info("inside create method of admin controller");

        boolean res = adminService.createAdmin(adminRequest);

        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.ADMIN_CREATE, res);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

    }

    @GetMapping("admin/pdf")
    public ResponseEntity<byte[]> getCustomersPdf() throws IOException {
//		List<Customer> customers = adminService.getAllCustomersFromExcel();
//		if (customers.isEmpty()) {
//			log.info("customers not found");
//			throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
//		}
        ByteArrayInputStream bis = pdfService.allCustomersPdf();
        log.info("customers found");
        byte[] pdfBytes = bis.readAllBytes();
        if (pdfBytes.length == 0) {
            log.error("Generated PDF is empty");
            throw new RuntimeException("PDF generation failed");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=customerList" + ".pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
    }


}
