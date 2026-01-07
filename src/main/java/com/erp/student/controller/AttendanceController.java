package com.erp.student.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.erp.student.entity.AttendanceEntity;
import com.erp.student.repo.AttendanceEntityRepo;

@Controller
@RequestMapping("/student")
public class AttendanceController {

	private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);
	private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

	@Autowired
	private AttendanceEntityRepo attendanceEntityRepo;

	@PostMapping("/save_attendance_certificate")
	String saveAttendanceCertificate(@ModelAttribute AttendanceEntity attendanceEntity,
			@RequestParam("identityProofFile") MultipartFile identityProofFile,
			@RequestParam("feeReciptFile") MultipartFile feeReciptFile,
			@RequestParam("verificationLetterFile") MultipartFile verificationLetterFile) {

		try {
			attendanceEntity.setIdentityProofPath(saveFile(identityProofFile));
			attendanceEntity.setFeeReciptPath(saveFile(feeReciptFile));
			attendanceEntity.setVerificationLetterPath(saveFile(verificationLetterFile));
			attendanceEntityRepo.save(attendanceEntity);

		} catch (Exception e) {
			return "redirect:/";
		}
		return "redirect:/student/request_certificate";
	}

	private String saveFile(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			log.warn("File is null or empty!");
			return null;
		}

		long startTime = System.currentTimeMillis(); // Start timing

		// Create directory if not exists
		Path uploadPath = Paths.get(UPLOAD_DIR);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Generate unique file name
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		Path filePath = uploadPath.resolve(fileName);

		// Use BufferedOutputStream for faster write
		try (BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(filePath))) {
			stream.write(file.getBytes());
		}

		long endTime = System.currentTimeMillis(); // End timing
		log.debug("File saved in: {}ms", (endTime - startTime));

		return fileName;
	}

}
