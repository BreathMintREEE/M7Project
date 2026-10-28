// UserController.java
// D. Singletary
// 9/24/25
// Controller for User entity, returns DTOs with task titles

package edu.fscj.cen3024c.taskmanager.controllers;

import edu.fscj.cen3024c.taskmanager.dto.UserDTO;
import edu.fscj.cen3024c.taskmanager.entities.User;
import edu.fscj.cen3024c.taskmanager.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // ===== READ endpoints return DTOs =====

    @CrossOrigin(origins = {"http://example.com", "http://localhost"})
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    // ===== WRITE endpoints accept entities but return DTOs =====

    @PostMapping
    public UserDTO createUser(@Valid @RequestBody UserDTO user) {
        Profiler profiler = new Profiler("createUser");
        profiler.start("Create User");
        try {
            User savedUser = userService.save(user);
            UserDTO dto = userService.convertToDTO(savedUser);
            logger.info("User created successfully: {}", dto.getUsername());
            return dto;
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print(); // logs timing info even if an exception occurs
        }
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO user) {
        try {
            UserDTO updatedUser = userService.updateUser(id, user);
            logger.info("User updated successfully: {}", updatedUser.getUsername());
            return updatedUser;
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
