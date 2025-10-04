package com.icsd.controller;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.AddressDto;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Address;
import com.icsd.model.Customer;
import com.icsd.model.SubscriptionStatus;
import com.icsd.service.CustomerService;
import com.icsd.service.PdfService;
import com.icsd.serviceImpl.DynamicSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.icsd.model.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    @Mock
    private DynamicSchedulerService schedulerService;

    @Mock
    private PdfService pdfService;

    @Mock
    private messages messages;
    private CustomerRequestDto crDto;
    private CustomerLoginDTO crLoginDto;
    private AddressDto addressDto;
    private Address savedAddress;
    private Customer savedCustomer;

    @BeforeEach
    void setup() {
        messages = new messages();
        customerController.setMessages(messages);
        addressDto = AddressDto.builder()
                .addressLine1("shodapur")
                .addressLine2("assandh road")
                .city("panipat")
                .state("haryana")
                .pincode("132103")
                .build();

        crDto = CustomerRequestDto.builder()
                .firstName("moni")
                .lastName("saini")
                .emailId("moni104104@gmail.com")
                .contactNo("1234567890")
                .gender(MALE)
                .password("Icsd@1234")
                .status(SubscriptionStatus.FREE)
                .address(addressDto)
                .build();

        crLoginDto = CustomerLoginDTO.builder()
                .emailId("moni104104@gmail.com")
                .password("Icsd@1234").build();

        savedAddress = Address.builder()
                .addressId(1)
                .addressLine1("shodapur")
                .addressLine2("assandh road")
                .city("panipat")
                .state("panipat")
                .pincode("132103")
                .build();

        savedCustomer = Customer.builder()
                .customerId(1)
                .firstName("moni")
                .lastName("xmic")
                .emailId("moni104104@gmail.com")
                .contactNo("1234567890")
                .gender(MALE)
                .password("Icsd@1234")
                .address(savedAddress)
                .registrationDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(10))
                .status(SubscriptionStatus.FREE)
                .build();
    }


    @Test
    void testIsValidUser_Success() throws SchedulerException {
        when(customerService.isValidCustByEmailidAndPwd(crLoginDto)).thenReturn(savedCustomer);
        ResponseEntity<ApiResponse> response = customerController.isValidUser(crLoginDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages.VALID_USER, response.getBody().getMessage());
        assertEquals(savedCustomer, response.getBody().getData());
        verify(schedulerService).scheduleWelcomeEmail(crDto.getEmailId(), crDto.getFirstName());
    }

    @Test
    void testIsValidUser_SchedulerFails() throws SchedulerException {
        when(customerService.isValidCustByEmailidAndPwd(crLoginDto)).thenReturn(savedCustomer);
        doThrow(new SchedulerException(messages.SCHEDULER_FAILED)).when(schedulerService)
                .scheduleWelcomeEmail(anyString(), anyString());
        ResponseEntity<ApiResponse> response = customerController.isValidUser(crLoginDto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(messages.SCHEDULER_FAILED, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void testCreateCustomer_Success() {
        Integer mockCustomerId = 1;
        String successMessage = "Customer Created Successfully";
        when(customerService.createCustomer(any(CustomerRequestDto.class))).thenReturn(mockCustomerId);
        ResponseEntity<ApiResponse> response = customerController.createCustomer(crDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(successMessage, response.getBody().getMessage());
        assertEquals(mockCustomerId, response.getBody().getData());
        verify(customerService, times(1)).createCustomer(any(CustomerRequestDto.class));
    }


    @Test
    void testGetCustomerByCustomerId_WhenCustomerExists_ReturnsCustomer() {
        int customerId = 1;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(savedCustomer));
        ResponseEntity<ApiResponse> response = customerController.getCustomerByCustomerId(customerId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages.CUSTOMER_FOUND, response.getBody().getMessage());
        assertEquals(savedCustomer, response.getBody().getData());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
    }

    @Test
    void testGetCustomerByCustomerId_WhenCustomerDoesNotExist_ReturnsNotFound() {
        int customerId = 22;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = customerController.getCustomerByCustomerId(customerId);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(messages.CUSTOMER_NOT_FOUND, response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
    }


    @Test
    void testDeleteCustomerByCustomerId_WhenCustomerExists_DeletesSuccessfully() {
        int customerId = 1;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(savedCustomer));
        doNothing().when(customerService).deleteCustomerByCustomerId(customerId);
        ResponseEntity<ApiResponse> response = customerController.deleteCustomerByCustomerId(customerId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages.CUSTOMER_DELETE, response.getBody().getMessage());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
        verify(customerService, times(1)).deleteCustomerByCustomerId(customerId);
    }

    @Test
    void testDeleteCustomerByCustomerId_WhenCustomerDoesNotExist_ThrowsResourceNotFoundException() {
        int customerId = 22;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.empty());
        ResourceNotFoundException error = assertThrows(ResourceNotFoundException.class,
                () -> customerController.deleteCustomerByCustomerId(customerId));
        assertEquals(messages.RESOURCE_NOT_FOUND, error.getMessage());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
        verify(customerService, never()).deleteCustomerByCustomerId(anyInt());
    }


    @Test
    void testGetCustomerPdf_WhenCustomerExists_ReturnsPdf() throws IOException {
        int customerId = 1;
        ByteArrayInputStream pdfStream = new ByteArrayInputStream("PDF content".getBytes());
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(savedCustomer));
        when(pdfService.generateCustomerPdf(customerId)).thenReturn(pdfStream);
        ResponseEntity<byte[]> response = customerController.getCustomerPdf(customerId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("inline; filename= customer_" + customerId + ".pdf"));
        assertArrayEquals("PDF content".getBytes(), response.getBody());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
        verify(pdfService, times(1)).generateCustomerPdf(customerId);
    }


    @Test
    void testGetCustomerPdf_WhenCustomerDoesNotExist_ThrowsResourceNotFoundException() throws IOException {
        int customerId = 22;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.empty());
        ResourceNotFoundException error = assertThrows(ResourceNotFoundException.class,
                () -> customerController.getCustomerPdf(customerId));
        assertEquals(messages.RESOURCE_NOT_FOUND, error.getMessage());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
        verify(pdfService, never()).generateCustomerPdf(anyInt());
    }


    @Test
    void testGetCustomerPdf_WhenPdfGenerationThrowsIOException_PropagatesException() throws IOException {
        int customerId = 1;
        when(customerService.getCustomerByCustomerId(customerId)).thenReturn(Optional.of(savedCustomer));
        when(pdfService.generateCustomerPdf(customerId)).thenThrow(new IOException("PDF generation failed"));
        IOException error = assertThrows(IOException.class, () -> customerController.getCustomerPdf(customerId));
        assertEquals("PDF generation failed", error.getMessage());
        verify(customerService, times(1)).getCustomerByCustomerId(customerId);
        verify(pdfService, times(1)).generateCustomerPdf(customerId);
    }


    @Test
    void testGetCustomers_DefaultParameters() {
        Page<Customer> page = new PageImpl<>(List.of(savedCustomer));
        when(customerService.getAllCustomer(any(Pageable.class))).thenReturn(page);
        Page<Customer> result = customerController.getCustomers(0, 2, "customerId", "asc", null);
        assertThat(result.getContent()).hasSize(1);
        verify(customerService, times(1)).getAllCustomer(any(Pageable.class));
        verify(customerService, never()).searchCustomersByFirstNameAndEmailId(anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void testGetCustomers_WithKeyword() {
        Page<Customer> page = new PageImpl<>(List.of(savedCustomer));
        when(customerService.searchCustomersByFirstNameAndEmailId(eq("moni"), eq("moni"), any(Pageable.class))).thenReturn(page);
        Page<Customer> result = customerController.getCustomers(0, 2, "customerId", "asc", "moni");
        assertThat(result.getContent()).hasSize(1);
        verify(customerService, never()).getAllCustomer(any(Pageable.class));
        verify(customerService, times(1)).searchCustomersByFirstNameAndEmailId(eq("moni"), eq("moni"), any(Pageable.class));
    }

    @Test
    void testGetCustomers_SortingDesc() {
        Page<Customer> page = new PageImpl<>(List.of(savedCustomer));
        when(customerService.getAllCustomer(any(Pageable.class))).thenReturn(page);
        Page<Customer> result = customerController.getCustomers(0, 2, "firstName", "desc", null);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerService).getAllCustomer(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        Sort.Order order = pageable.getSort().getOrderFor("firstName");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getPageNumber()).isEqualTo(0);
    }

    @Test
    void testGetCustomers_EmptyKeyword() {
        Page<Customer> page = new PageImpl<>(List.of(savedCustomer));
        when(customerService.getAllCustomer(any(Pageable.class))).thenReturn(page);
        Page<Customer> result = customerController.getCustomers(0, 2, "customerId", "asc", "");
        verify(customerService, times(1)).getAllCustomer(any(Pageable.class));
        verify(customerService, never()).searchCustomersByFirstNameAndEmailId(anyString(), anyString(), any(Pageable.class));
    }
}






