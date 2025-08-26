package com.icsd.service;

import java.util.List;

import com.icsd.model.Address;

public interface AddressService {

	public List<Address> findByAddressLine2IsNull();

	public List<Address>findByAddressLine2IsNotNull();


}
