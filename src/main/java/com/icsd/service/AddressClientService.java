package com.icsd.service;

import com.icsd.dto.request.AddressDto;
import com.icsd.model.Address;

public interface AddressClientService {


    public Address saveAddress(AddressDto addressDto) ;

    }
