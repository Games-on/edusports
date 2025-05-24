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
            Role organizerRole = Role.builder().name(RoleName.ORGANIZER).build();
            Role studentRole = Role.builder().name(RoleName.STUDENT).build();
            
            roleRepository.save(adminRole);
            roleRepository.save(organizerRole);
            roleRepository.save(studentRole);
            
            System.out.println("✅ Đã tạo các role ADMIN, ORGANIZER, và STUDENT.");
        }

        // Tạo user admin mặc định nếu chưa tồn tại
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN);
            
            if (adminRole == null) {
                System.out.println("❌ Lỗi: Không tìm thấy ADMIN role!");
                return;
            }
            
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            User adminUser = new User();
            adminUser.setName("Admin User");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("123456"));
            adminUser.setActive(true);
            adminUser.setRoles(adminRoles);
            
            userRepository.save(adminUser);
            System.out.println("✅ Đã tạo tài khoản ADMIN mặc định (admin@gmail.com / 123456).");
        }
        
        // Tạo user organizer mặc định nếu chưa tồn tại
        if (userRepository.findByEmail("organizer@gmail.com").isEmpty()) {
            Role organizerRole = roleRepository.findByName(RoleName.ORGANIZER);
            Set<Role> organizerRoles = new HashSet<>();
            organizerRoles.add(organizerRole);

            User organizerUser = new User();
            organizerUser.setName("Organizer User");
            organizerUser.setEmail("organizer@gmail.com");
            organizerUser.setPassword(passwordEncoder.encode("123456"));
            organizerUser.setActive(true);
            organizerUser.setRoles(organizerRoles);
            userRepository.save(organizerUser);
            System.out.println("✅ Đã tạo tài khoản ORGANIZER mặc định (organizer@gmail.com / 123456).");
        }
    }
}
