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
import com.erp.student.entity.AttendanceEntity;
import com.erp.student.repo.AttendanceEntityRepo;

import com.erp.service.EmailService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/admin")
public class AdminAttendanceController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AttendanceEntityRepo attendanceEntityRepo;

    @Autowired
    private AdmissionRepo admissionRepo;

    @GetMapping("/view_pedding_attendance")
    public String viewPendingBonafide(Model model) {
        List<AttendanceEntity> attendanceEntities = attendanceEntityRepo.findByAdminApproval(0);
        model.addAttribute("attendanceEntities", attendanceEntities);
        return "Admin/view_pending_attendance";
    }

    @GetMapping("/view_approved_attendance")
    public String viewApprovedBonafide(Model model) {
        List<AttendanceEntity> attendanceEntities = attendanceEntityRepo.findByAdminApproval(1);
        model.addAttribute("attendanceEntities", attendanceEntities);
        return "Admin/view_approved_attendance";
    }

    @GetMapping("/view_rejected_attendance")
    public String viewRejectedBonafide(Model model) {
        List<AttendanceEntity> attendanceEntities = attendanceEntityRepo.findByAdminApproval(2);
        model.addAttribute("attendanceEntities", attendanceEntities);
        return "Admin/view_rejected_attendance";
    }

    @GetMapping("/view_attendance_application/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        Optional<AttendanceEntity> application = attendanceEntityRepo.findById(id);

        if (application.isPresent()) {
            model.addAttribute("tc", application.get());
            // Add entity to model
            // System.out.println(application.get());
            return "admin/view_attendance_application";
        } else {
            return "redirect:/admin/view_pedding_attendance";
        }
    }

    @GetMapping("/reject_attendance_certificate/{id}")
    public String rejectCertificate(@PathVariable Long id) {

        attendanceEntityRepo.findById(id).ifPresent(tc -> {
            tc.setAdminApproval(2); // Set approval to 1 (Approved)
            attendanceEntityRepo.save(tc); // Save changes

            Optional<Admission> admission = admissionRepo.findByAdmissionId(tc.getStudentId());

            if (admission.isPresent()) {
                Admission student = admission.get();
                try {
                    emailService.sendAttendanceRejectionEmail(student.getEmail(), tc.getFullName(), tc.getId());
                } catch (MessagingException e) {
                    e.printStackTrace(); // Log error if email fails
                }
            }

        });

        return "redirect:/admin/view_pedding_attendance"; // ✅ Redirect to view page
    }

    @GetMapping("/issue_attendance/{id}")
    public String issueBonafide(@PathVariable Long id) {
        attendanceEntityRepo.findById(id).ifPresent(tc -> {
            tc.setIssueDate(new Date()); // Set current date using Date object
            attendanceEntityRepo.save(tc); // Save updated TC directly
        });
        return "redirect:/admin/view_approved_attendance"; // Redirect back to TC list
    }

    @GetMapping("/delete_attendance/{id}")
    public String deleteTC(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            attendanceEntityRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Attendance deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting Attendance!");
        }
        return "redirect:/admin/view_rejected_attendance"; // Redirect back to TC list
    }

    @GetMapping("/verify_attendance_certificate/{id}")
    public String approveAttendanceCertificate(@PathVariable Long id) {

        attendanceEntityRepo.findById(id).ifPresent(bonafide -> {
            bonafide.setAdminApproval(1); // Set approval to 1 (Approved)
            attendanceEntityRepo.save(bonafide); // Save changes

            Optional<Admission> admission = admissionRepo.findByAdmissionId(bonafide.getStudentId());

            if (admission.isPresent()) {
                Admission student = admission.get();
                try {
                    emailService.sendAttendanceCertificateEmail(student.getEmail(), bonafide.getFullName(),
                            bonafide.getId());
                } catch (MessagingException e) {
                    e.printStackTrace(); // Log error if email fails
                }
            }

            // ✅ Send email after approval

        });

        return "redirect:/admin/open_attendance/" + id; // ✅ Redirect to view page
    }

    @GetMapping("/open_attendance/{id}")
    public String openAttendance(Model model, @PathVariable Long id) {
        Optional<AttendanceEntity> entityOptional = attendanceEntityRepo.findById(id);

        if (entityOptional.isPresent()) {
            AttendanceEntity attendanceEntity = entityOptional.get();

            model.addAttribute("tc", attendanceEntity);
        } else {
            model.addAttribute("error", "Attendance record not found!");
            return "redirect:/admin/view_pedding_attendance"; // Redirect to listing page if not found
        }

        return "Admin/open_attendance";
    }

}
