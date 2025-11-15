package com.taskmanagement.config;

import com.taskmanagement.entity.Role;
import com.taskmanagement.entity.RoleName;
import com.taskmanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initialization component that seeds initial data into the database
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        seedRoles();
        log.info("Data initialization completed");
    }

    /**
     * Seeds initial roles into the database
     */
    private void seedRoles() {
        // Check and create ROLE_USER
        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            userRole.setDescription("Standard user role with basic permissions");
            roleRepository.save(userRole);
            log.info("Created role: ROLE_USER");
        }

        // Check and create ROLE_ADMIN
        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            adminRole.setDescription("Administrator role with full system access");
            roleRepository.save(adminRole);
            log.info("Created role: ROLE_ADMIN");
        }

        // Check and create ROLE_MANAGER
        if (!roleRepository.existsByName(RoleName.ROLE_MANAGER)) {
            Role managerRole = new Role();
            managerRole.setName(RoleName.ROLE_MANAGER);
            managerRole.setDescription("Manager role with project and team management permissions");
            roleRepository.save(managerRole);
            log.info("Created role: ROLE_MANAGER");
        }

        log.info("Role initialization check completed");
    }
}
