package com.icsd.service;

import com.icsd.dto.request.AddressDto;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.exception.EntityAlreadyExistException;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Address;
import com.icsd.model.Customer;
import com.icsd.model.Gender;
import com.icsd.model.SubscriptionStatus;
import com.icsd.repo.AddressRepo;
import com.icsd.repo.CustomerRepo;
import com.icsd.serviceImpl.CustomerServiceImpl;
import jakarta.validation.Validator;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.icsd.model.Gender.MALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private AddressRepo addressRepo;
    @Mock
    private AddressClientService addressClientService;

    private CustomerRequestDto crDto;
    private CustomerLoginDTO crLoginDto;
    private AddressDto addressDto;
    private Address savedAddress;
    private Customer savedCustomer;
    private List<CustomerFnmLnmGenderDTO> mockList;
    private List<Customer> mockCustomerList;



    @BeforeEach
    void setup() {

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
                .emailId("vnjv12@gmail.com")
                .contactNo("1234567890")
                .gender(MALE)
                .password("Icsd@1234")
                .status(SubscriptionStatus.FREE)
                .address(addressDto)
                .build();

        crLoginDto=CustomerLoginDTO.builder()
                .emailId("vnjv12@gmail.com")
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
                .firstName("vnjv")
                .lastName("xmic")
                .emailId("moni12@gmail.com")
                .contactNo("1234567890")
                .gender(MALE)
                .password("Icsd@1234")
                .address(savedAddress)
                .registrationDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(10))
                .status(SubscriptionStatus.FREE)
                .build();

        mockList = List.of(new CustomerFnmLnmGenderDTO("moni","saini",Gender.FEMALE),new CustomerFnmLnmGenderDTO("annu","saini",Gender.MALE));


            mockCustomerList = List.of(
                    Customer.builder()
                            .customerId(1)
                            .firstName("moni")
                            .lastName("saini")
                            .emailId("moni.saini@gmail.com")
                            .contactNo("1234567890")
                            .gender(Gender.FEMALE)
                            .password("Icsd@1234")
                            .address(savedAddress)
                            .registrationDate(LocalDate.now())
                            .expiryDate(LocalDate.now().plusDays(10))
                            .status(SubscriptionStatus.FREE)
                            .build(),

                    Customer.builder()
                            .customerId(2)
                            .firstName("annu")
                            .lastName("saini")
                            .emailId("annu.saini@gmail.com")
                            .contactNo("0987654321")
                            .gender(Gender.MALE)
                            .password("Icsd@1234")
                            .address(savedAddress)
                            .registrationDate(LocalDate.now())
                            .expiryDate(LocalDate.now().plusDays(5))
                            .status(SubscriptionStatus.PREMIUM)
                            .build()
            );


    }


    @Test
    void createCustomer_shouldThrowException_whenEmailAlreadyExists() {
        when(customerRepo.findByEmailId(crDto.getEmailId())).thenReturn(Optional.of(new Customer()));
        EntityAlreadyExistException exception = assertThrows(EntityAlreadyExistException.class, () -> {
            customerService.createCustomer(crDto);
        });
        assertEquals("Customer email id is already existing", exception.getMessage());
        verify(customerRepo, never()).save(any());
    }



    @Test
    void createCustomer_shouldReturnCustomerId_whenCreatedSuccessfully() {
        when(customerRepo.findByEmailId(crDto.getEmailId())).thenReturn(Optional.empty());
        when(addressClientService.saveAddress(any(AddressDto.class))).thenReturn(savedAddress);
        when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);
        Integer result = customerService.createCustomer(crDto);
        assertEquals(savedCustomer.getCustomerId(), result);
        verify(customerRepo).findByEmailId(crDto.getEmailId());
        verify(addressClientService).saveAddress(any(AddressDto.class));
        verify(customerRepo).save(any(Customer.class));
    }

@Test
    void isValidCustByEmailidAndPwd_shouldThrowException_whenCredentialsInvalid(){
        when(customerRepo.findByEmailIdAndPassword(crLoginDto.getEmailId(), crLoginDto.getPassword())).thenReturn(Optional.empty());
    ResourceNotFoundException exception= assertThrows(ResourceNotFoundException.class ,() ->{
        customerService.isValidCustByEmailidAndPwd(crLoginDto);
    });
    assertEquals("No record found",exception.getMessage());
    verify(customerRepo,never()).save(any());
    }

    @Test
    void isValidCustByEmailIddAndPwd_returnCustomer_whenCredentialsValid() {
        when(customerRepo.findByEmailIdAndPassword(crLoginDto.getEmailId(), crLoginDto.getPassword())).thenReturn(Optional.of(savedCustomer));
        Customer result = customerService.isValidCustByEmailidAndPwd(crLoginDto);
        assertNotNull(result.getEmailId());
        assertEquals(savedCustomer.getEmailId(),result.getEmailId());
        verify(customerRepo).findByEmailIdAndPassword(crLoginDto.getEmailId(), crLoginDto.getPassword());

    }


    @Test
    void findByLastName_shouldReturnListOfMatchingCustomers(){
        when(customerRepo.findByLastName(crDto.getLastName())).thenReturn(mockList);
        List<CustomerFnmLnmGenderDTO> result=customerService.findByLastName(crDto.getLastName());
        assertAll("all assertions",
                () -> assertNotNull(result),
                () ->assertEquals(mockList.get(0).getLastName(),result.get(0).getLastName()),
                () ->assertEquals(mockList.get(1).getLastName(),result.get(1).getLastName())
                );
        verify(customerRepo).findByLastName(crDto.getLastName());
    }


    @Test
    void findByFirstNameContaining_ifNameContainSubstring_shouldReturnCustomers(){
        String text="n";
        when(customerRepo.findByFirstNameContaining(text)).thenReturn(mockCustomerList);
        List<Customer> result=customerService.findByFirstNameContaining(text);
        assertEquals(mockCustomerList,result);
        verify(customerRepo).findByFirstNameContaining(text);

    }

    @Test
    void getCustomerDataByEmailId_shouldReturnCustomer_whenEmailExists(){
        String email="moni12@gmail.com";
        when(customerRepo.findByEmailId(email)).thenReturn(Optional.of(savedCustomer));
        Optional<Customer> optResult= customerService.getCustomerDataByEmailId(email);
        Customer result= optResult.get();
        assertAll("all assertions",
                () -> assertTrue(optResult.isPresent()),
                () -> assertEquals(email,result.getEmailId()));
        verify(customerRepo).findByEmailId(email);
    }

    @Test
    void deleteCustomerByCustomerId_customerDeletedSuccessfully(){
        customerRepo.save(savedCustomer);
        customerService.deleteCustomerByCustomerId(savedCustomer.getCustomerId());
        Optional<Customer> deleted=customerRepo.findById(savedCustomer.getCustomerId());
        assertFalse(deleted.isPresent());
        verify(customerRepo).deleteById(savedCustomer.getCustomerId());
    }





    }



