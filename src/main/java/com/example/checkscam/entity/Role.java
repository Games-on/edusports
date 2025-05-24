package com.example.checkscam.entity;


import com.example.checkscam.constant.RoleName;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role",
        uniqueConstraints = @UniqueConstraint(name = "name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Sử dụng EnumType.STRING để lưu tên role vào database
    @Column(length = 50, nullable = false)
    private RoleName name; // Thay đổi kiểu dữ liệu thành RoleName

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<User> users;
}