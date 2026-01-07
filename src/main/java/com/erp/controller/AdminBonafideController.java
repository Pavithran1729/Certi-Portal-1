package com.erp.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.erp.admin.entity.Admission;
import com.erp.repo.AdmissionRepo;
import com.erp.student.entity.BonafideEntity;
import com.erp.student.entity.StudentDocument;
import com.erp.student.repo.BonafideRepository;
import com.erp.student.repo.StudentDocumentRepository;

import com.erp.service.EmailService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/admin")
public class AdminBonafideController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private BonafideRepository bonafideRepository;

    @Autowired
    private AdmissionRepo admissionRepo;

    @Autowired
    private StudentDocumentRepository studentDocumentRepository;

    @GetMapping("/view_pedding_bonafide")
    public String viewPendingBonafide(Model model) {
        List<BonafideEntity> bonafideEntities = bonafideRepository.findByAdminApproval(0);
        model.addAttribute("bonafides", bonafideEntities);
        return "Admin/view_pending_bonafide";
    }

    @GetMapping("/view_approved_bonafide")
    public String viewApprovedBonafide(Model model) {
        List<BonafideEntity> bonafideEntities = bonafideRepository.findByAdminApproval(1);
        model.addAttribute("bonafides", bonafideEntities);
        return "Admin/view_approved_bonafide";
    }

    @GetMapping("/view_rejected_bonafide")
    public String viewRejectedBonafide(Model model) {
        List<BonafideEntity> bonafideEntities = bonafideRepository.findByAdminApproval(2);
        model.addAttribute("bonafides", bonafideEntities);
        return "Admin/view_rejected_bonafide";
    }

    @GetMapping("/view_bonafide_application/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        Optional<BonafideEntity> application = bonafideRepository.findById(id);

        if (application.isPresent()) {
            model.addAttribute("tc", application.get());
            // Add entity to model
            // System.out.println(application.get());
            return "admin/view_bonafide_application";
        } else {
            return "redirect:/admin/view_pedding_bonafide";
        }
    }

    @GetMapping("/verify_bonafide_certificate/{id}")
    public String approveBonafideCertificate(@PathVariable Long id) {

        bonafideRepository.findById(id).ifPresent(bonafide -> {
            bonafide.setAdminApproval(1); // Set approval to 1 (Approved)
            bonafideRepository.save(bonafide); // Save changes

            Optional<Admission> admission = admissionRepo.findByAdmissionId(bonafide.getStudentId());

            if (admission.isPresent()) {
                Admission student = admission.get();
                try {
                    emailService.sendBonafideApprovalEmail(student.getEmail(), bonafide.getFullName(),
                            bonafide.getId());
                } catch (MessagingException e) {
                    e.printStackTrace(); // Log error if email fails
                }
            }

            // ✅ Send email after approval

        });

        return "redirect:/admin/open_bonafide/" + id; // ✅ Redirect to view page
    }

    @GetMapping("/open_bonafide/{id}")
    public String openBonafide(Model model, @PathVariable Long id) {
        Optional<BonafideEntity> entityOptional = bonafideRepository.findById(id);

        if (entityOptional.isPresent()) {
            BonafideEntity bonafideEntity = entityOptional.get();
            StudentDocument document = studentDocumentRepository.findByStudentId(bonafideEntity.getStudentId())
                    .orElse(null); // Avoids null issues

            model.addAttribute("document", document);
            model.addAttribute("bonafide", bonafideEntity);
        } else {
            model.addAttribute("error", "Bonafide record not found!");
            return "redirect:/admin/bonafide_list"; // Redirect to listing page if not found
        }

        return "Admin/open_bonafide";
    }

    @GetMapping("/reject_bonafide_certificate/{id}")
    public String rejectCertificate(@PathVariable Long id) {

        bonafideRepository.findById(id).ifPresent(bonafide -> {
            bonafide.setAdminApproval(2);
            bonafideRepository.save(bonafide);

            Optional<Admission> admission = admissionRepo.findByAdmissionId(bonafide.getStudentId());

            if (admission.isPresent()) {
                Admission student = admission.get();
                try {
                    emailService.sendBonafideRejectionEmail(student.getEmail(), bonafide.getFullName(),
                            bonafide.getId());
                } catch (MessagingException e) {
                    e.printStackTrace(); // Log error if email fails
                }
            }

        });

        return "redirect:/admin/view_pedding_bonafide";

    }

    @GetMapping("/issue_bonafide/{id}")
    public String issueTC(@PathVariable Long id) {
        bonafideRepository.findById(id).ifPresent(tc -> {
            tc.setIssueDate(new Date()); // Set current date using Date object
            bonafideRepository.save(tc); // Save updated TC directly
        });
        return "redirect:/admin/view_approved_bonafide"; // Redirect back to TC list
    }

    @GetMapping("/delete_bonafide/{id}")
    public String deleteTC(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bonafideRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "TC deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting TC!");
        }
        return "redirect:/admin/view_rejected_bonafide"; // Redirect back to TC list
    }

}
