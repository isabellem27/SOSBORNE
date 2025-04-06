package com.sosborne.config;

import com.sosborne.model.entity.Day;
import com.sosborne.model.entity.Role;
import com.sosborne.repository.DayRepository;
import com.sosborne.repository.RoleRepository;
import com.sosborne.repository.UserRepository;

import com.sosborne.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.logging.Logger;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DayRepository dayRepository;
   // private static final Logger log = Logger.getLogger(GeolocationService.class.getName());

    @Bean
    CommandLineRunner initData() {

        return args -> {
            String message = new String();
            LocalDate today = LocalDate.now();
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role(null, "ADMIN"));
                roleRepository.save(new Role(null, "USER"));
                roleRepository.save(new Role(null, "UOWNER"));
            }
/*
            if (!userRepository.findByEmail("admin@admin.com")) {
                User admin = new User();
                admin.setPassword(passwordHasher.hashPassword("admin"));
                admin.setName("admin");
                admin.setFirstname("admin");
                admin.setEmail("admin@admin.com");
                admin.setIdpieceurl("mon idpiece");
                admin.setPhone("00-00-00-00-00");
                admin.setCreationdate(today);
                admin.setUpdatedate(today);

                Optional<Role> adminRole = roleRepository.findByName("ADMIN");
                admin.setRole(adminRole.orElse(null));
                userRepository.save(admin);
            }
*/
            if (dayRepository.count() == 0) {
                dayRepository.save(new Day(null, "lundi"));
                dayRepository.save(new Day(null, "mardi"));
                dayRepository.save(new Day(null, "mercredi"));
                dayRepository.save(new Day(null, "jeudi"));
                dayRepository.save(new Day(null, "vendredi"));
                dayRepository.save(new Day(null, "samedi"));
                dayRepository.save(new Day(null, "dimanche"));
            }    
            log.info ("✅ Données initiales insérées avec succès !");
            

        };
    }
}
