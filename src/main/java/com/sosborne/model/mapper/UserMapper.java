package com.sosborne.model.mapper;

import com.sosborne.model.dto.UserDTO;
import com.sosborne.model.entity.Habiter;
import com.sosborne.model.entity.User;
import com.sosborne.repository.HabiterRepository;
import com.sosborne.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
@RequiredArgsConstructor
public class UserMapper {
    private final AddressMapper addressMapper;
    private final AddressService addressService;
    private final HabiterRepository habiterRepository;

    public UserDTO toDto(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        dto.setFirstname(user.getFirstname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        ArrayList<Habiter> lstHabiter= habiterRepository.findByUser(user);
        //On prend l'adresse du premier lien habiter de la liste
        dto.setAddress(addressMapper.addressToDTO(lstHabiter.get(0).getAddress()));
        // On ne renvoie pas user.getMotDePasse() -> dto.setPassword(null)
        dto.setPassword(null);
        return dto;
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setName(dto.getName());
        user.setFirstname(dto.getFirstname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        // On stockera le password haché dans le service
        user.setPassword(dto.getPassword());
        return user;
    }
}
