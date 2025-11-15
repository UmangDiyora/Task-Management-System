package com.taskmanagement.repository;

import com.taskmanagement.entity.Role;
import com.taskmanagement.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * @param name the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(RoleName name);

    /**
     * Check if role exists by name
     * @param name the role name
     * @return true if role exists, false otherwise
     */
    boolean existsByName(RoleName name);
}
