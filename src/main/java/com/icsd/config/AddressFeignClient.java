package com.icsd.config;


import com.icsd.dto.request.AddressDto;
import com.icsd.model.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name= "address-client" , url="http://localhost:8901")
public interface AddressFeignClient {

    @PostMapping("api/address/save")
    Address saveAddress(@RequestBody AddressDto addressDto);


}

