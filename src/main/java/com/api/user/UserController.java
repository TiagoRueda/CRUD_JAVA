package com.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Obter todos os usuarios
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Obter usuario por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return generateResponse(200, "user", user.get(), null);
        } else {
            return generateResponse(404, "user", new HashMap<>(), "User not found");
        }
    }

    // Criar usuario
    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = userRepository.save(user);
            return generateResponse(201, "user", savedUser, "Successfully created");
        } catch (Exception e) {
            return generateResponse(400, "user", new HashMap<>(), "Error creating user");
        }
    }

    // Atualizar usuario
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userData) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            return generateResponse(404, "user", new HashMap<>(), "User not found");

        try {
            User existingUser = user.get();
            if (userData.getName() != null)
                existingUser.setName(userData.getName());
            if (userData.getEmail() != null)
                existingUser.setEmail(userData.getEmail());

            userRepository.save(existingUser);
            return generateResponse(200, "user", existingUser, "Successfully updated");
        } catch (Exception e) {
            return generateResponse(400, "user", new HashMap<>(), "Error updating user");
        }
    }

    // Deletar usuario
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            return generateResponse(404, "user", new HashMap<>(), "User not found");

        try {
            userRepository.delete(user.get());
            return generateResponse(200, "user", user.get(), "Successfully deleted");
        } catch (Exception e) {
            return generateResponse(400, "user", new HashMap<>(), "Error deleting user");
        }
    }

    private ResponseEntity<?> generateResponse(int status, String contentName, Object content, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put(contentName, content);
        if (message != null) body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
