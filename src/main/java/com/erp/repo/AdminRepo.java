package com.erp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.admin.entity.AdminEntity;

@Repository
public interface AdminRepo extends JpaRepository<AdminEntity, Long> {
    // Legacy method - kept for reference, should not be used for login
    AdminEntity findByUsernameAndPasswordAndRole(String username, String password, String role);

    // Secure method - find by username and role, then verify password separately
    AdminEntity findByUsernameAndRole(String username, String role);
}