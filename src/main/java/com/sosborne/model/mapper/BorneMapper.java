package com.sosborne.model.mapper;

import com.sosborne.model.dto.BorneDTO;
import com.sosborne.model.entity.Borne;
import com.sosborne.service.AddressService;
import com.sosborne.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BorneMapper {
    private final AvailabilityService availabilityService;
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    public BorneDTO borneToBorneDTO(Borne borne){
        BorneDTO borneDTO = new BorneDTO();

            borneDTO.setTaughtname(borne.getTaughtname());
            borneDTO.setMarketname(borne.getMarketname());
            borneDTO.setAddress(addressMapper.addressToDTO(borne.getAddress()));
            borneDTO.setNumborne(borne.getNumborne());
            borneDTO.setPower(borne.getPower());
            borneDTO.setWorking(borne.getWorking());
            borneDTO.setPublicField(borne.getPublicField());
            borneDTO.setType(borne.getType());
            borneDTO.setNbpoint(borne.getNbpoint());
            borneDTO.setLstAvailability(availabilityService.getAvailabilityByBorne(borne));

            return borneDTO;
    }

    public Borne borneDTOToBorne(BorneDTO borneDTO){
        Borne borne = new Borne();

        borne.setTaughtname(borneDTO.getTaughtname());
        borne.setMarketname(borneDTO.getMarketname());
        borne.setAddress(addressService.getByDTO(borneDTO.getAddress()));
        borne.setNumborne(borneDTO.getNumborne());
        borne.setPower(borneDTO.getPower());
        borne.setPublicField(false);
        if (borneDTO.getWorking()==null){borne.setWorking(true);}
        else {borne.setWorking(borneDTO.getWorking());}
        borne.setType(borneDTO.getType());
        borne.setNbpoint(borneDTO.getNbpoint());

        return borne;
    }


}
