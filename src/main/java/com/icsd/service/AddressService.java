package com.icsd.service;

import com.icsd.model.Address;

import java.util.List;

public interface AddressService {

	public List<Address> findByAddressLine2IsNull();

	public List<Address> findByAddressLine2IsNotNull();

//	public Optional<Address> getAddressByCustomerId(int customerId);

}
