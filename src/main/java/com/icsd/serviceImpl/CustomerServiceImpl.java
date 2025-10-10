package com.icsd.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.icsd.annotation.ValidEmailAOP;
import com.icsd.dto.request.AddressDto;
import com.icsd.model.Address;
import com.icsd.service.AddressClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.icsd.dto.common.messages;
import com.icsd.dto.request.AddressDto;
import com.icsd.dto.request.CustomerLoginDTO;
import com.icsd.dto.request.CustomerRequestDto;
import com.icsd.dto.response.CustomerFnmLnmGenderDTO;
import com.icsd.exception.EntityAlreadyExistException;
import com.icsd.exception.ResourceNotFoundException;
import com.icsd.model.Address;
import com.icsd.model.Customer;
import com.icsd.repo.AddressRepo;
import com.icsd.repo.CustomerRepo;
import com.icsd.service.AddressClientService;
import com.icsd.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AddressRepo addressRepo;
    @Value("${customer.expiry.days}")
    int expiryDays;

    @Autowired
    AddressClientService addressClientService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Integer createCustomer(@Valid CustomerRequestDto crDto) {
        log.info("Inside save customer of service with given request" + crDto);
        Optional<Customer> optCust = customerRepo.findByEmailId(crDto.getEmailId());
        if (optCust.isPresent()) {
            throw new EntityAlreadyExistException(messages.EMAIL_ALREADY_EXIST);
        }

        AddressDto addressDto = AddressDto.builder().addressLine1(crDto.getAddress().getAddressLine1()).addressLine2(crDto.getAddress().getAddressLine2())
                .city(crDto.getAddress().getCity()).state(crDto.getAddress().getState()).pincode(crDto.getAddress().getPincode()).build();
        log.info("adress dto in customer" + addressDto);

        Address savedAddress = addressClientService.saveAddress(addressDto);

        log.info("adress dto after saved " + savedAddress.getAddressId());

        Customer cust = Customer.builder().firstName(crDto.getFirstName()).lastName(crDto.getLastName())
                .emailId(crDto.getEmailId()).contactNo(crDto.getContactNo()).address(savedAddress)
                .gender(crDto.getGender())
                .password(passwordEncoder.encode(crDto.getPassword()))
                .registrationDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(expiryDays)).status(crDto.getStatus()).build();
        Customer custCreated = customerRepo.save(cust);
        log.info("cust entity is now from builder : " + custCreated);

        return custCreated.getCustomerId();
    }

    @Override
    public Customer isValidCustByEmailidAndPwd(@Valid CustomerLoginDTO customerLogin) {
        log.info("Inside customer service to chek whether user is valid or not" + customerLogin);

        Optional<Customer> optCust = customerRepo.findByEmailIdAndPassword(customerLogin.getEmailId(),
                customerLogin.getPassword());
        if (optCust.isEmpty()) {
            log.info("customer is not existing for email id " + customerLogin.getEmailId() + " and pwd= "
                    + customerLogin.getPassword());
            throw new ResourceNotFoundException(messages.RESOURCE_NOT_FOUND);
        }
        Customer customer = optCust.get();
        return customer;
    }

    @Override
    public List<CustomerFnmLnmGenderDTO> findByLastName(String lnm) {
        log.info("inside customer service return matching list of Customers");
        List<CustomerFnmLnmGenderDTO> lst = customerRepo.findByLastName(lnm);
        return lst;
    }

    @Override
    public List<Customer> findByFirstNameIgnoreCase(String fn) {
        List<Customer> lst = customerRepo.findByFirstNameIgnoreCase(fn);
        return lst;
    }

    @Override
    public List<Customer> findByFirstNameLike(String fn) {
        List<Customer> list = customerRepo.findByFirstNameLike(fn);
        return list;
    }

    @Override
    public List<Customer> findByFirstNameContaining(String fn) {
        log.info("inside customer service return Customers having substring ");
        List<Customer> list = customerRepo.findByFirstNameContaining(fn);
        return list;
    }

    @Override
    public List<Customer> findByfirstNameContains(String fn) {
        List<Customer> list = customerRepo.findByfirstNameContains(fn);
        return list;
    }

    @Override
    public List<Customer> findByfirstNameIsContaining(String fn) {
        List<Customer> list = customerRepo.findByfirstNameIsContaining(fn);
        return list;
    }

    @Override
    public Optional<Customer> getCustomerDataByEmailId(String emailId) {
        log.info("inside service to getCustomerDataByEmailId" + emailId);
        Optional<Customer> cust = customerRepo.findByEmailId(emailId);
        return cust;
    }

    @Override
    public Optional<Customer> getCustomerByCustomerId(int customerId) {
        log.info("inside service to getCustomerByCustomerId" + customerId);
        Optional<Customer> cust = customerRepo.findById(customerId);
        return cust;
    }

    @Override
    public void deleteCustomerByCustomerId(int customerId) {
        log.info("inside service to deleteCustomerByCustomerId" + customerId);
        customerRepo.deleteById(customerId);

    }

    @Override
    public Customer updateCustomerByCustomerId(int customerId, @Valid CustomerRequestDto customerRequest) {
        log.info("inside service to updateCustomerByCustomerId" + customerId);
        Optional<Customer> existingCustomer = customerRepo.findById(customerId);
        Customer updatedCustomer = existingCustomer.get();

        if (customerRequest.getFirstName() != null) {
            updatedCustomer.setFirstName(customerRequest.getFirstName());
        }
        if (customerRequest.getLastName() != null) {
            updatedCustomer.setLastName(customerRequest.getLastName());
        }
        if (customerRequest.getEmailId() != null) {
            updatedCustomer.setEmailId(customerRequest.getEmailId());
        }
        if (customerRequest.getContactNo() != null) {
            updatedCustomer.setContactNo(customerRequest.getContactNo());
        }
        if (customerRequest.getGender() != null) {
            updatedCustomer.setGender(customerRequest.getGender());

        }
        if (customerRequest.getPassword() != null) {
            updatedCustomer.setPassword(customerRequest.getPassword());
        }

        Address address = updatedCustomer.getAddress();
        if (address == null) {
            address = new Address();
        }
        if (customerRequest.getAddress().getAddressLine1() != null) {
            address.setAddressLine1(customerRequest.getAddress().getAddressLine1());
        }
        if (customerRequest.getAddress().getAddressLine2() != null) {
            address.setAddressLine2(customerRequest.getAddress().getAddressLine2());
        }
        if (customerRequest.getAddress().getCity() != null) {
            address.setCity(customerRequest.getAddress().getCity());
        }
        if (customerRequest.getAddress().getState() != null) {
            address.setState(customerRequest.getAddress().getState());
        }
        if (customerRequest.getAddress().getPincode() != null) {
            address.setPincode(customerRequest.getAddress().getPincode());
        }

        updatedCustomer.setAddress(address);

        if (customerRequest.getStatus() != null) {
            updatedCustomer.setStatus(customerRequest.getStatus());
        }
        return customerRepo.save(updatedCustomer);

    }


    @Override
    public Page<Customer> getAllCustomer(Pageable pageable) {
        return customerRepo.findAll(pageable);
    }

    @Override
    public Page<Customer> searchCustomersByFirstNameAndEmailId(String keyword, String keyword2, Pageable pageable) {
        return customerRepo.findByFirstNameContainingIgnoreCaseOrEmailIdContainingIgnoreCase(
                keyword, keyword, pageable);
    }


}