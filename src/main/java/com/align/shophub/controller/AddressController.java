package com.align.shophub.controller;

import com.align.shophub.dto.AddressDto;
import com.align.shophub.entity.Address;
import com.align.shophub.entity.UserInfo;
import com.align.shophub.repository.AddressRepository;
import com.align.shophub.repository.UserInfoRepository;
import com.align.shophub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired private AddressRepository addressRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private JwtService jwtService;

    @GetMapping
    public List<AddressDto> getUserAddresses(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();

        // 1. Fetch filtered addresses (Entities)
        // Note: It is highly recommended to add 'findByUserId' to your AddressRepository 
        // instead of filtering a 'findAll()' stream for better performance.
        List<Address> addresses = addressRepository.findAll().stream()
                .filter(a -> a.getUser().getId() == user.getId())
                .collect(Collectors.toList());

        // 2. Map Entities to DTOs
        return addresses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Address addAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();
        address.setUser(user);
        return addressRepository.save(address);
    }

    private AddressDto mapToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .pincode(address.getPincode())
                .phone(address.getPhone())
                .notes(address.getNotes())
                .build();
    }
}