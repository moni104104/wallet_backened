package com.icsd.repo;

import java.util.List;

import com.icsd.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository<Address, Integer> {

	List<Address> findByAddressLine2IsNull();

	List<Address> findByAddressLine2IsNotNull();

//	Optional<Address> findByCustomerCustomerId(int customerId);

}
