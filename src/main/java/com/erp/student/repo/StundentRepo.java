package com.erp.student.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.admin.entity.Admission;

@Repository
public interface StundentRepo extends JpaRepository<Admission, Long> {
    // Legacy method - kept for reference
    Admission findByAdmissionIdAndPassword(String admissionId, String password);

    // Secure method - find by admission ID only, verify password separately
    Admission findByAdmissionId(String admissionId);
}
