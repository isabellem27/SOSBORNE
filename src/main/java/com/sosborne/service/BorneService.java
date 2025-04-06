package com.sosborne.service;

import com.sosborne.exception.BusinessLogicException;
import com.sosborne.exception.ResourceNotFoundException;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.dto.BorneDTO;
import com.sosborne.model.dto.GeoBorneDTO;
import com.sosborne.model.dto.GeoFilterDTO;
import com.sosborne.model.entity.Address;
import com.sosborne.model.entity.Borne;
import com.sosborne.model.mapper.BorneMapper;
import com.sosborne.repository.BorneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@RequiredArgsConstructor
@Slf4j
@Service
public class BorneService {
    private final BorneRepository borneRepository;
    private final BorneMapper borneMapper;
    private final AvailabilityService availabilityService;
    private final AddressService addressService;

    public ArrayList<GeoBorneDTO> getByGeoFilter(GeoFilterDTO geoFilter, ArrayList<GeoBorneDTO> lstGeoBorne){
        //extraire de la base les bornes privées actives dont les coordonnées correspondent à la demande de l'utilisateur
        //d'abord la liste des address correspondante
        ArrayList<Address> lstAddress =addressService.getByGeoFilter(geoFilter);
        //ensuite la liste des bornes rattachées à cette liste d'adresses
        ArrayList<Borne> lstBorne= borneRepository.findByActiveTrueAndWorkingTrueAndAddressIn(lstAddress);
        //On ajoute les bornes extraites dans la liste des bornes publiques, au format geoborneDTO
        for (Borne item: lstBorne){
            lstGeoBorne.add(new GeoBorneDTO(
                    item.getTaughtname(), item.getMarketname(), addressService.refactortoDTO(item.getAddress()),
                    item.getNumborne(),item.getAddress().getAddresslat(), item.getAddress().getAddresslong(),
                    item.getPower(), item.getType(),true,false,item.getNbpoint(),true,
                    null,availabilityService.getAvailabilityByBorne(item)
                    ));
        }
        System.out.println("Nombre total de bornes à géolocaliser: "+Integer.toString(lstGeoBorne.size()));
        return lstGeoBorne;
    }

    public ArrayList<BorneDTO> getAllBorneDTO(AddressDTO addressDTO){
        ArrayList<BorneDTO> lstDTO = new ArrayList<>();
        Address address =addressService.getByDTO(addressDTO);
        System.out.println(address);
        ArrayList<Borne> lstBorne = borneRepository.findByAddressAndActiveTrue(addressService.getByDTO(addressDTO));
        for (Borne item: lstBorne){
           lstDTO.add(borneMapper.borneToBorneDTO(item));
        }
        return lstDTO;
    }

    public BorneDTO getBorneDTO(AddressDTO addressDTO,int numBorne){
        Borne borne = borneRepository.findByAddressAndActiveTrueAndNumborne(addressService.getByDTO(addressDTO), numBorne)
                .orElseThrow(() -> new ResourceNotFoundException("Borne introuvable pour l'adresse: --> "
                        + addressDTO.getStreet()+" "+addressDTO.getZipcode()+" "+addressDTO.getTown()));

        return borneMapper.borneToBorneDTO(borne);
    }

    public Borne getBorne(AddressDTO addressDTO, int numBorne){
        Borne borne = borneRepository.findByAddressAndActiveTrueAndNumborne(addressService.getByDTO(addressDTO),numBorne)
                .orElseThrow(() -> new ResourceNotFoundException("Borne introuvable pour l'adresse: --> "
                        + addressDTO.getStreet()+" "+addressDTO.getZipcode()+" "+addressDTO.getTown()));
        return borne;
    }

    @Transactional
    public String createBorne(BorneDTO borneDTO){
        try {
            //je crèe ma borne
            Borne borne =borneMapper.borneDTOToBorne(borneDTO);
            borne.setNumborne(borneRepository.countByAddress(borne.getAddress()) + 1);
            Borne savedBorne = borneRepository.save(borne);
            //Je crée mes availability
            availabilityService.addAvailabilitiesFromBorneDTO(borneDTO.getLstAvailability(), savedBorne);
        } catch (Exception e) {
            throw new BusinessLogicException("Erreur lors de la création de la borne : " + e.getMessage());
        }

        return "Votre borne a bien été créée";
    }

    @Transactional
    public BorneDTO updateBorne(BorneDTO borneDTO){
        Borne savedBorne;
        try {
            Borne borne = borneRepository.findByAddressAndActiveTrueAndNumborne(addressService.getByDTO(borneDTO.getAddress()),borneDTO.getNumborne())
                    .orElseThrow(() -> new ResourceNotFoundException("Borne à modifier introuvable ou non active pour l'adresse: --> "
                            + borneDTO.getAddress().getStreet()+" "+borneDTO.getAddress().getZipcode()
                            +" "+borneDTO.getAddress().getTown()));
            if (borneDTO.getPower() !=0) {
                borne.setPower(borneDTO.getPower());
            }
            if (borneDTO.getType() !=null) {
                borne.setType(borneDTO.getType());
            }
            if (borneDTO.getWorking() !=null) {
                if (borneDTO.getWorking() != borne.getWorking()) {
                    borne.setWorking(borneDTO.getWorking());
                }
            }
            savedBorne = borneRepository.save(borne);
            if (borneDTO.getLstAvailability() != null){
                // Annule et remplace les availability
                availabilityService.replaceLstByBorne(savedBorne,borneDTO.getLstAvailability());
            }
        } catch (Exception e) {
            throw new BusinessLogicException("Erreur lors de la mise à jour de la borne : " + e.getMessage());
        }

        return borneMapper.borneToBorneDTO(savedBorne);
    }

    public void inactiveAllBornes (Address address){
        ArrayList<Borne> lstBorne = borneRepository.findAllByAddress(address);
        for(Borne borne: lstBorne){
            borne.setActive(false);
            borneRepository.save(borne);
        }
    }

    public String inactiveBorne(AddressDTO addressDTO,int numBorne){
        Borne borne = borneRepository.findByAddressAndActiveTrueAndNumborne(addressService.getByDTO(addressDTO), numBorne)
                .orElseThrow(() -> new ResourceNotFoundException("Borne à supprimer introuvable pour l'adresse: --> "
                        + addressDTO.getStreet()+" "+addressDTO.getZipcode()+" "+addressDTO.getTown()));

        borne.setActive(false);
        borneRepository.save(borne);
        return "Succès de la suppression de votre borne";
    }


    public String deleteBorne(AddressDTO addressDTO, int numBorne){
        Borne borne = borneRepository.findByAddressAndActiveTrueAndNumborne(addressService.getByDTO(addressDTO), numBorne)
                .orElseThrow(() -> new ResourceNotFoundException("Borne à supprimer introuvable pour l'adresse: --> "
                        + addressDTO.getStreet()+" "+addressDTO.getZipcode()+" "+addressDTO.getTown()));

        availabilityService.deleteByBorne(borne);
        borneRepository.delete(borne);
        return "Succès de la suppression de votre borne";
    }




}
