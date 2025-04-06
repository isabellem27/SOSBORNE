package com.sosborne.controller;

import com.sosborne.config.MessageConstants;
import com.sosborne.config.MessageResponse;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.dto.UserDTO;
import com.sosborne.model.entity.User;
import com.sosborne.model.mapper.UserMapper;
import com.sosborne.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private UserMapper userMapper;


    @GetMapping("/adrs/get/{email}")
    public ArrayList<AddressDTO> addressesByEmail(@PathVariable String email){
        return userService.getAddressesByEmail(email);
    }

    @GetMapping("/all")
    public ResponseEntity<MessageResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> dtos = userService.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        MessageResponse<List<UserDTO>> response = new MessageResponse<>(
                MessageConstants.FETCH_SUCCESS,
                dtos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MessageResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        MessageResponse<UserDTO> response = new MessageResponse<>(
                MessageConstants.FETCH_SUCCESS,
                userMapper.toDto(user)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adrs/add/{email}")
    public String newAddresses(@PathVariable String email, @RequestBody ArrayList<AddressDTO> addressDTO){
        return userService.createAddressByEmail(addressDTO,email);
    }

    @PostMapping("/add")
    public ResponseEntity<MessageResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        User created = userService.createUser(userDTO);
        MessageResponse<UserDTO> response = new MessageResponse<>(
                MessageConstants.CREATION_SUCCESS,
                userMapper.toDto(created)

        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MessageResponse<UserDTO>> updateUser(@PathVariable UUID id,
                                                               @Valid @RequestBody UserDTO userDTO) {
        User updated = userService.updateUser(id, userDTO);
        MessageResponse<UserDTO> response = new MessageResponse<>(
                MessageConstants.UPDATE_SUCCESS,
                userMapper.toDto(updated)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        MessageResponse<Void> response = new MessageResponse<>(
                MessageConstants.DELETION_SUCCESS,
                null
        );
        return ResponseEntity.ok(response);
    }
}
