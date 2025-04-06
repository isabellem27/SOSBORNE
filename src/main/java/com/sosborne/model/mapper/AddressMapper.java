package com.sosborne.model.mapper;

import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressMapper {

    public AddressDTO addressToDTO(Address address){
        AddressDTO addressDTO= new AddressDTO();

        addressDTO.setStreet(address.getStreet());
        addressDTO.setZipcode(address.getZipcode());
        addressDTO.setTown(address.getTown());

        return addressDTO;
    }

    public Address addressDTOToAddress(AddressDTO addressDTO){
        Address address= new Address();

        address.setStreet(addressDTO.getStreet());
        address.setZipcode(addressDTO.getZipcode());
        address.setTown(addressDTO.getTown());

        return address;
    }
}
