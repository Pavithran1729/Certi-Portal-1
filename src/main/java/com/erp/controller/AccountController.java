package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.erp.repo.AdmissionRepo;
import com.erp.student.entity.TCEntity;
import com.erp.student.repo.TCRepository;

import com.erp.service.EmailService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private TCRepository repository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TCRepository tcRepository;

	@Autowired
	private AdmissionRepo admissionRepo;

	@GetMapping({ "", "/" })
	public String openAccount(Model model) {

		long totalStudent = admissionRepo.count();

		long totalTC = tcRepository.count();
		long totalA = tcRepository.countByAccountApproval(1);
		long totalP = tcRepository.countByAccountApproval(0);
		long totalR = tcRepository.countByAccountApproval(2);

		model.addAttribute("totalStudent", totalStudent);
		model.addAttribute("totalA", totalA);
		model.addAttribute("totalP", totalP);
		model.addAttribute("totalR", totalR);
		model.addAttribute("totalTC", totalTC);

		return "Account/index";
	}

	@GetMapping("/new_request_certificate")
	public String openTCPage(Model model) {
		List<TCEntity> tcEntities = repository.findByAccountApproval(0);
		model.addAttribute("tcEntities", tcEntities);

		return "Account/new_request_certificate";
	}

	@GetMapping("/approved_certificate")
	public String openTCPage1(Model model) {
		List<TCEntity> tcEntities = repository.findByAccountApproval(1);
		model.addAttribute("tcEntities", tcEntities);

		return "Account/approved_certificate";
	}

	@GetMapping("/rejected_certificate")
	public String openTCPage2(Model model) {
		List<TCEntity> tcEntities = repository.findByAccountApproval(2);
		model.addAttribute("tcEntities", tcEntities);

		return "Account/reject_certificate";
	}

	// main operation here

	@GetMapping("/approved_tc/{id}")
	public String approveTc(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		repository.findById(id).ifPresent(tc -> {
			tc.setAccountApproval(1); // Set approval to 1 (Approved)
			repository.save(tc); // Save changes
		});

		redirectAttributes.addFlashAttribute("message", "Transfer Certificate Approved Successfully!");
		return "redirect:/account/new_request_certificate";
	}

	@GetMapping("/reject_tc/{id}")
	public String rejectTc(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		repository.findById(id).ifPresent(tc -> {
			tc.setAccountApproval(2); // Set approval to 2 (Rejected)
			repository.save(tc); // Save changes

			// Send email notification about rejection due to account dues
			try {
				emailService.sendAccountDuesEmail(tc.getEmailId(), tc.getFirstName(), tc.getLastName());
			} catch (MessagingException e) {
				e.printStackTrace(); // Log the error for debugging
			}
		});

		redirectAttributes.addFlashAttribute("message", "Transfer Certificate Rejected Successfully!");
		return "redirect:/account/new_request_certificate";
	}

}
