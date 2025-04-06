package com.sosborne.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.logging.Logger;

import com.sosborne.model.dto.UserDTO;
import com.sosborne.exception.ValidationException;
import com.sosborne.config.PasswordHasher;
import com.sosborne.exception.ResourceNotFoundException;
import com.sosborne.model.entity.User;
import com.sosborne.repository.UserRepository;
import com.sosborne.exception.BusinessLogicException;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.entity.Habiter;
import com.sosborne.model.entity.Role;
import com.sosborne.repository.HabiterRepository;
import com.sosborne.repository.RoleRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AddressService addressService;
    private final BorneService borneService;
    private final HabiterRepository habiterRepository;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    private PasswordHasher passwordHasher;
    private final RoleRepository roleRepository;

    public User getByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable pour l'email: --> " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        // Créer l'entité
        User user = new User();

        logger.info("Début de createUser, userDTO=" + userDTO);
        // Vérifier l'unicité de l'email
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            logger.warning("Email déjà utilisé : " + userDTO.getEmail());
            throw new ValidationException("L'email est déjà utilisé");

        }
        try {
           // user.setId(UUID.randomUUID()); //pris en charge par la base
            user.setName(userDTO.getName());
            user.setFirstname(userDTO.getFirstname());
            user.setEmail(userDTO.getEmail());
            // Hachage du mot de passe
            //user.setPassword(passwordHasher.hashPassword(userDTO.getPassword()));
            //Por tests chargement du password sans hashage
            user.setPassword((userDTO.getPassword()));
            user.setPhone(userDTO.getPhone());
            user.setIdpieceurl(userDTO.getIdpieceurl());
            user.setPhotourl(userDTO.getPhotourl());
            Role role = roleRepository.findByName("USER")
                        .orElseThrow(() -> new BusinessLogicException("Role USER introuvable"));
            user.setRole(role);
            user.setCreationdate(LocalDate.now());
            user.setUpdatedate(LocalDate.now());

            // Sauvegarde
            user = userRepository.save(user);

            // Contrôle de l'adresse - [IM] - adaptation à AddressDTO
            if ((userDTO.getAddress().getStreet() == null || userDTO.getAddress().getStreet().isBlank())
                    || (userDTO.getAddress().getZipcode() == null || userDTO.getAddress().getZipcode().isBlank())
                    ||(userDTO.getAddress().getTown() == null || userDTO.getAddress().getTown().isBlank())) {
                throw new ValidationException("L'adresse est obligatoire et doit être complète: rue code postal ville");
            }
            addressService.createAddressUser(userDTO.getAddress(), user);
            logger.info("Fin de createUser, userID=" + user.getId());
        } catch (Exception e) {
            throw new BusinessLogicException("Erreur lors de la création de l'utilisateur' : " + e.getMessage());
        }

        return user;
    }

    @Transactional
    public User updateUser(UUID id, UserDTO userDTO) {
        logger.info("Début de updateUser, id=" + id + ", userDTO=" + userDTO);

        User user = getUserById(id);

        // Vérifier l'unicité de l'email si celui-ci est modifié
        boolean emailExists = userRepository.existsByEmailAndIdNot(userDTO.getEmail(), id);
        logger.info("Vérification email : " + userDTO.getEmail() + ", ID actuel : " + id + ", Existe pour un autre utilisateur : " + emailExists);

        if (emailExists) {
            logger.warning("Email déjà utilisé : " + userDTO.getEmail());
            throw new ValidationException("Cet email est déjà utilisé par un autre utilisateur");
        }


        user.setName(userDTO.getName());
        user.setFirstname(userDTO.getFirstname());
        user.setEmail(userDTO.getEmail());


        // Mettre à jour le mot de passe si fourni
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            //user.setPassword(passwordHasher.hashPassword(userDTO.getPassword()));
            //Por tests chargement du password sans hashage
            user.setPassword((userDTO.getPassword()));
        }

        // Modification de l'adresse si l'adresse a changé
        if (userDTO.getAddress() != null) {
            checkAddress(userDTO.getAddress(), user);
        }

        user.setPhone(userDTO.getPhone());
        user.setUpdatedate(LocalDate.now());

        user = userRepository.save(user);
        logger.info("Fin de updateUser, userID=" + user.getId());
        return user;
    }

    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Début de deleteUser, id=" + id);
        User user = getUserById(id);
        userRepository.delete(user);
        logger.info("Fin de deleteUser, userID=" + user.getId());
    }

    public void checkAddress(AddressDTO addressDTO, User user){
        boolean trouve= false;
        // récupère les adresses de l'utilisateur et vérifie si la nouvelle adresse est déjà dedans
        ArrayList<Habiter> lstHabiter = habiterRepository.findByUser(user);
        for (Habiter habiter: lstHabiter){
            if (habiter.getAddress().getStreet().equals(addressDTO.getStreet())
                    && habiter.getAddress().getZipcode().equals(addressDTO.getZipcode())
                    && habiter.getAddress().getTown().equals(addressDTO.getTown())
            ){
                trouve = true;
            }
        }
        if (!trouve && lstHabiter.size()==1){
            //adresse différente et 1 seule adresse liée à l'utilisateur --> je peux changer l'adresse
            ArrayList<AddressDTO> lstAddressDTO = new ArrayList<>();
            lstAddressDTO.add(addressService.refactortoDTO(lstHabiter.get(0).getAddress()));
            lstAddressDTO.add(addressDTO);
            //on change d'adresse
            addressService.changeUserAddress(lstAddressDTO,user);
            //on désactive les bornes de l'ancienne adresse
            borneService.inactiveAllBornes(lstHabiter.get(0).getAddress());
        }else{
            if (lstHabiter.size()>1){
                throw new BusinessLogicException("plusieures adresses sont liées à l'utilisateur."
                        +" Impossible de connaitre celle qui doit être remplacée");
            }
        }
    }

    public ArrayList<AddressDTO> getAddressesByEmail(String email){
        ArrayList<AddressDTO> lstDTO= new ArrayList<>();

        User user = getByEmail(email);
        ArrayList<Habiter> lstHabiter = habiterRepository.findByUser(user);
        for (Habiter habiter: lstHabiter){
            lstDTO.add(addressService.refactortoDTO(habiter.getAddress()));
        }
        return lstDTO;
    }

    @Transactional
    public String createAddressByEmail(ArrayList<AddressDTO> lstAddress, String email){
        User user = getByEmail(email);
        for(AddressDTO address: lstAddress){
            addressService.createAddressUser(address,user);
        }

        return "Vos adresses ont bien été créées";
    }
}
