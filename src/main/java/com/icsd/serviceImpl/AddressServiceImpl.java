package com.icsd.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.icsd.model.Address;
import com.icsd.repo.AddressRepo;
import com.icsd.service.AddressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

	
	@Autowired
	AddressRepo addressRepo;
	@Override
	public List<Address> findByAddressLine2IsNull() {
		log.info("inside service Impl address findByAddressLine2IsNull");
		List<Address> list=addressRepo.findByAddressLine2IsNull();
		return list;
	}

	@Override
	public List<Address> findByAddressLine2IsNotNull() {
		log.info("inside service Impl address findByAddressLine2IsNotNull");

		List<Address> list=addressRepo.findByAddressLine2IsNotNull();
		return list;
	}

}
