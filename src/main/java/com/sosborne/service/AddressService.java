package com.sosborne.service;

import com.sosborne.exception.BusinessLogicException;
import com.sosborne.exception.GeoLocationException;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.dto.GeoFilterDTO;
import com.sosborne.model.entity.Address;
import com.sosborne.model.entity.Habiter;
import com.sosborne.model.entity.User;
import com.sosborne.model.mapper.AddressMapper;
import com.sosborne.repository.AddressRepository;
import com.sosborne.repository.HabiterRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final GeolocationAdrService geolocationAdrService;
    private final HabiterRepository habiterRepository;

    public Address getByAddress(String adresse, String zipCode, String town){
        return addressRepository.findByStreetAndZipcodeAndTown(adresse,zipCode,town);
    }

    public Address getByDTO(AddressDTO addressDTO){
        String adresse;
        String zipCode;
        String town;
        adresse= addressDTO.getStreet();
        zipCode=addressDTO.getZipcode();
        town = addressDTO.getTown();

        return getByAddress(adresse,zipCode,town);
    }

    public ArrayList<Address> getByGeoFilter(GeoFilterDTO geoFilter){
        return addressRepository.findAddressWithinDistance(geoFilter.getUserLat(), geoFilter.getUserlong(), geoFilter.getUserKm());
    }


    public AddressDTO refactortoDTO(Address address){
        return addressMapper.addressToDTO(address);
    }


    public void createAddressUser(AddressDTO address, User user){
        Map<String, Double> coords = geolocationAdrService.addressGeolocation(address);

        Address trouve = getByDTO(address);
        Habiter habiter = new Habiter();

        //L'adresse n'existe pas dans la table
        if (trouve==null) {
            //l'adresse est géolocalisée
            if ((coords.get("latitude") != null) && (coords.get("latitude") != 0)) {
                Address newAddress = new Address();

                newAddress.setStreet(address.getStreet());
                newAddress.setZipcode(address.getZipcode());
                newAddress.setTown(address.getTown());
                newAddress.setAddresslat(coords.get("latitude"));
                newAddress.setAddresslong(coords.get("longitude"));

                Address savedAddress = addressRepository.save(newAddress);

                //je crée le lien entre l'address et le user dans Habiter
                habiter.setAddress(savedAddress);
                habiter.setUser(user);
                habiterRepository.save(habiter);
            }else{
                throw new GeoLocationException("Impossible de géolocaliser l'adresse. Merci de la vérifier.");
            }
        }else{
            //l'adresse existe déjà, je me contente de lier l'utilisateur
            //je crée le lien entre l'address et le user dans Habiter
            habiter.setAddress(trouve);
            habiter.setUser(user);
            habiterRepository.save(habiter);
        }

    }

    public String changeUserAddress(ArrayList<AddressDTO> lstAddressDTO, User user){
        String msgChangeAddress = "";

        Address address1 = getByAddress(lstAddressDTO.get(0).getStreet(),
                                        lstAddressDTO.get(0).getZipcode(),
                                        lstAddressDTO.get(0).getTown());

        Habiter lienUsrAdr =habiterRepository.findByAddress(address1)
                        .orElseThrow(() -> new BusinessLogicException("L'utilisateur dont l'email est "+user.getEmail()+
                                " n'est pas inscrit à l'adresse --> "
                            + lstAddressDTO.get(0).getStreet()+" "+lstAddressDTO.get(0).getZipcode()+" "+lstAddressDTO.get(0).getTown()
                                + " changement d'adresse impossible"));

        if (user == lienUsrAdr.getUser()){
            // Suppression du lien adresse user
            habiterRepository.delete(lienUsrAdr);

            // Création de la nouvelle adresse et création du nouveau lien
            createAddressUser(lstAddressDTO.get(1), user);
        }else{
            throw new BusinessLogicException("L'utilisateur dont l'email est "+user.getEmail() +
                    " n'est pas inscrit à l'adresse --> "
                    + lstAddressDTO.get(0).getStreet()+" "+lstAddressDTO.get(0).getZipcode()+" "+lstAddressDTO.get(0).getTown()
                    + " changement d'adresse impossible");
        }

        if (user.getRole().getName().equals("UOWNER")){
            msgChangeAddress="Votre changement d'adresse a bien été pris en compte. "
                            +"Pensez à créer votre borne pour cette nouvelle adresse.";
        }else{
            msgChangeAddress="Votre changement d'adresse a bien été pris en compte.";
        }
        return msgChangeAddress;
    }


    public String deleteAddress(AddressDTO addressDTO) {
        //Ajouter le contrôle sur habiter, borne, garage avant de faire le delete
        Address address = getByAddress(addressDTO.getStreet(), addressDTO.getZipcode() , addressDTO.getTown());
        addressRepository.delete(address);

        return "adresse supprimée";
    }

}
