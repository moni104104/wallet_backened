package com.icsd.serviceImpl;


import com.icsd.config.AddressFeignClient;
import com.icsd.dto.common.messages;
import com.icsd.dto.request.AddressDto;
import com.icsd.model.Address;
import com.icsd.service.AddressClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

//
//@Service
//@RequiredArgsConstructor
//public class AddressClientService {
//
//    private final WebClient webClient;
//    private final RestTemplate restTemplate;
//    private final AddressFeignClient addressFeignClient;
//
//    public enum clientType {
//        WEBCLIENT, RESTTEMPLATE, FEIGN
//    }
//
//
//    public Address saveAddress(AddressDto addressDto, clientType clientType) {
//        switch (clientType) {
//            case WEBCLIENT:
//                return webClient.post()
//                        .uri("/api/address/save")
//                        .bodyValue(addressDto)
//                        .retrieve()
//                        .bodyToMono(Address.class)
//                        .block();
//
//            case RESTTEMPLATE:
//                return restTemplate.postForObject("http://localhost:8901/api/address/save", addressDto, Address.class);
//
//            case FEIGN:
//                return addressFeignClient.saveAddress(addressDto);
//            default:
//                throw new IllegalArgumentException(messages.INVALID_CLIENT);
//        }
//        }
//    }



@Service
@RequiredArgsConstructor
public class AddressClientServiceImpl implements AddressClientService {

    private final WebClient webClient;



    public Address saveAddress(AddressDto addressDto) {

        return webClient.post()
                .uri("/api/address/save")
                .bodyValue(addressDto)
                .retrieve()
                .bodyToMono(Address.class)
                .block();


    }
}

