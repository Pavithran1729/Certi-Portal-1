package com.erp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erp.service.CertificateService;
import com.erp.student.entity.BonafideEntity;
import com.erp.student.repo.BonafideRepository;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for certificate download endpoints.
 */
@Controller
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private BonafideRepository bonafideRepository;

    /**
     * Download Bonafide certificate as PDF.
     */
    @GetMapping("/bonafide/{id}/download")
    public ResponseEntity<byte[]> downloadBonafideCertificate(
            @PathVariable Long id,
            HttpSession session) {

        // Check if admin or student is logged in
        Object admin = session.getAttribute("admin");
        Object student = session.getAttribute("student");

        if (admin == null && student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Find the bonafide certificate
        BonafideEntity bonafide = bonafideRepository.findById(id).orElse(null);

        if (bonafide == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the certificate is approved (adminApproval = 1)
        if (bonafide.getAdminApproval() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Certificate not yet approved".getBytes());
        }

        try {
            // Generate PDF
            byte[] pdfContent = certificateService.generateBonafideCertificate(bonafide);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "bonafide_certificate_" + bonafide.getStudentId() + ".pdf");
            headers.setContentLength(pdfContent.length);

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating certificate: " + e.getMessage()).getBytes());
        }
    }
}
