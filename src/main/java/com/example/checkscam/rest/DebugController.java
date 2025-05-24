package com.example.checkscam.rest;

import com.example.checkscam.entity.Role;
import com.example.checkscam.entity.User;
import com.example.checkscam.repository.RoleRepository;
import com.example.checkscam.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DebugController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> debug = new HashMap<>();
        debug.put("authenticated", auth != null && auth.isAuthenticated());
        debug.put("principal", auth != null ? auth.getPrincipal().toString() : "null");
        debug.put("name", auth != null ? auth.getName() : "null");
        
        if (auth != null) {
            List<String> authorities = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            debug.put("authorities", authorities);
        }
        
        return ResponseEntity.ok(debug);
    }

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> debugDatabase() {
        Map<String, Object> debug = new HashMap<>();
        
        // Check roles
        List<Role> roles = roleRepository.findAll();
        debug.put("roles_count", roles.size());
        debug.put("roles", roles.stream().map(r -> {
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("id", r.getId());
            roleInfo.put("name", r.getName());
            return roleInfo;
        }).toList());
        
        // Check admin user
        Optional<User> adminUser = userRepository.findByEmail("admin@gmail.com");
        if (adminUser.isPresent()) {
            User admin = adminUser.get();
            Map<String, Object> adminInfo = new HashMap<>();
            adminInfo.put("id", admin.getId());
            adminInfo.put("email", admin.getEmail());
            adminInfo.put("roles_count", admin.getRoles().size());
            adminInfo.put("roles", admin.getRoles().stream().map(r -> {
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("id", r.getId());
                roleInfo.put("name", r.getName());
                return roleInfo;
            }).toList());
            debug.put("admin_user", adminInfo);
        } else {
            debug.put("admin_user", "NOT_FOUND");
        }
        
        return ResponseEntity.ok(debug);
    }
}
