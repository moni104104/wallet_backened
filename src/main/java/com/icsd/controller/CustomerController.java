package com.icsd.controller;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.dto.response.LoginResponseDTO;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Customer;
import com.icsd.security.CustomUserDetailsService;
import com.icsd.security.JwtUtils;
import com.icsd.service.CustomerService;
import com.icsd.service.PdfService;
import com.icsd.serviceImpl.DynamicSchedulerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class CustomerController {

    private messages messages;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    DynamicSchedulerService schedulerService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    public void setMessages(messages messages) {
        this.messages = messages;
    }

    @PostMapping(value = "/customer")
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequest) {
        log.info("inside create method of customer controller");
        Integer generatedCustomerId = customerService.createCustomer(customerRequest);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_CREATE, generatedCustomerId);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("customer/{customerId}")
    public ResponseEntity<ApiResponse> getCustomerByCustomerId(@PathVariable("customerId") int customerId) {
        log.info("inside get method of customer controller");
        Optional<Customer> optCust = customerService.getCustomerByCustomerId(customerId);
        if (!optCust.isPresent()) {
            log.info("no record found");
            ApiResponse apiresponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), messages.CUSTOMER_NOT_FOUND);
            return new ResponseEntity<ApiResponse>(apiresponse, HttpStatus.NOT_FOUND);
        }
        log.info("customer found for given customer id");
        Customer cust = optCust.get();
        ApiResponse apiresponse = new ApiResponse(HttpStatus.FOUND.value(), messages.CUSTOMER_FOUND, cust);
        return new ResponseEntity<>(apiresponse, HttpStatus.OK);

    }

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
        log.info("pdf generated successfully");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());
    }


    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse> deleteCustomerByCustomerId(@PathVariable("customerId") int customerId) {
        Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
        if (!cust.isPresent()) {
            log.info("customer not found");
            throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
        }
        customerService.deleteCustomerByCustomerId(customerId);
        log.info("Customer deleted successfully");
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_DELETE);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }


    @PutMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse> updateCustomerByCustomerId(@PathVariable("customerId") int customerId,
                                                                  @RequestBody CustomerRequestDto customerRequest) {
        log.info("inside PUt method of customer controller");

        Optional<Customer> cust = customerService.getCustomerByCustomerId(customerId);
        if (!cust.isPresent()) {
            log.info("customer not found");
            throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
        }
        Customer updatedCustomer = customerService.updateCustomerByCustomerId(customerId, customerRequest);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.CUSTOMER_UPDATE, updatedCustomer);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }


//    @PostMapping(value = "/customer/isValid")
//    public ResponseEntity<ApiResponse> isValidUser(@RequestBody @Valid CustomerLoginDTO customerLogin) {
//        log.info("Authenticating user - valid or not");
//
//        try {
//            Customer customer = customerService.isValidCustByEmailidAndPwd(customerLogin);
//            log.info("Valid User");
//            log.info("call dynamic scheduler service form controllr");
//            schedulerService.scheduleWelcomeEmail(customer.getEmailId(), customer.getFirstName());
//
//            ApiResponse apiresponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_USER, customer);
//            return new ResponseEntity<>(apiresponse, HttpStatus.OK);
//        } catch (SchedulerException e) {
//            log.error("Failed to schedule welcome email", e);
//            return new ResponseEntity<>(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    messages.SCHEDULER_FAILED), HttpStatus.INTERNAL_SERVER_ERROR);
//
//        }
//    }


    @PostMapping(value = "/customer/isValid")
    public ResponseEntity<ApiResponse> isValidUser(@RequestBody @Valid CustomerLoginDTO customerLogin) {
        log.info("Authenticating user via JWT security");

        try {
//            var authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            customerLogin.getEmailId(),
//                            customerLogin.getPassword()
//                    )
//            );
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(customerLogin.getEmailId());

            if (!passwordEncoder.matches(customerLogin.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException(messages.INVALID_USER);
            }
            String jwtToken = jwtUtils.generateToken(userDetails.getUsername());

            Customer customer = customerService.getCustomerDataByEmailId(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND));

            schedulerService.scheduleWelcomeEmail(customer.getEmailId(), customer.getFirstName());

            LoginResponseDTO responseBody = new LoginResponseDTO();
            responseBody.setToken(jwtToken);
            responseBody.setCustomer(customer);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.value(), messages.VALID_USER, responseBody);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception ex) {
            log.error("Login failed: ", ex);
            ApiResponse errorResponse = new ApiResponse(HttpStatus.UNAUTHORIZED.value(), messages.INVALID_USER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/customer/email/{emailId}")
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


    @GetMapping("/customers")
    public Page<Customer> getCustomers(@RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "2", required = false) int pageSize, @RequestParam(name = "sortBy", defaultValue = "customerId", required = false) String sortBy,
                                       @RequestParam(defaultValue = "asc", name = "sortDir", required = false) String sortDir, @RequestParam(name = "keyword", required = false) String keyword) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        if (keyword != null && !keyword.isEmpty()) {
            return customerService.searchCustomersByFirstNameAndEmailId(keyword, keyword, pageable);
        } else {
            return customerService.getAllCustomer(pageable);
        }

    }

}
