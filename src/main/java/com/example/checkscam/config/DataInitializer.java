package com.example.checkscam.config;

import com.example.checkscam.constant.RoleName;
import com.example.checkscam.entity.Role;
import com.example.checkscam.entity.User;
import com.example.checkscam.repository.RoleRepository;
import com.example.checkscam.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional  // 👈 Thêm annotation này để đảm bảo transaction được commit
    public void init() {

        // Tạo các role nếu chưa tồn tại
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder().name(RoleName.ADMIN).build();
            Role collabRole = Role.builder().name(RoleName.COLLAB).build();
            roleRepository.save(adminRole);
            roleRepository.save(collabRole);
            System.out.println("✅ Đã tạo các role ADMIN và COLLAB.");
        }

        // Tạo user admin và collab mặc định nếu chưa tồn tại
        if (userRepository.count() == 0) {
            // Tạo user admin
            Role adminRole = roleRepository.findByName(RoleName.ADMIN);
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("123456")); // Thay đổi password mặc định
            adminUser.setRoles(adminRoles);
            userRepository.save(adminUser);
            System.out.println("✅ Đã tạo tài khoản ADMIN mặc định.");

            // Tạo user collab
            Role collabRole = roleRepository.findByName(RoleName.COLLAB);
            Set<Role> collabRoles = new HashSet<>();
            collabRoles.add(collabRole);

            User collabUser = new User();
            collabUser.setName("Collab User");
            collabUser.setEmail("collab@gmail.com");
            collabUser.setPassword(passwordEncoder.encode("123456")); // Thay đổi password mặc định
            collabUser.setRoles(collabRoles);
            userRepository.save(collabUser);
            System.out.println("✅ Đã tạo tài khoản COLLAB mặc định.");
        }
    }
}
