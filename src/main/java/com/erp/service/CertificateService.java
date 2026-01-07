package com.erp.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.erp.student.entity.BonafideEntity;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Service for generating PDF certificates.
 */
@Service
public class CertificateService {

    private static final String COLLEGE_NAME = "St. Peter's College of Engineering and Technology";
    private static final String COLLEGE_ADDRESS = "Avadi, Chennai - 600 062";
    private static final String COLLEGE_PHONE = "020-67656000";
    private static final String COLLEGE_EMAIL = "principal@stpeters.edu";

    /**
     * Generate a Bonafide Certificate PDF.
     */
    public byte[] generateBonafideCertificate(BonafideEntity bonafide) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Add college header
        addCollegeHeader(document);

        // Add certificate title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("BONAFIDE CERTIFICATE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(30);
        document.add(title);

        // Add certificate number and date
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        String certNo = "BF/" + LocalDate.now().getYear() + "/" + bonafide.getId();
        Paragraph certInfo = new Paragraph("Certificate No: " + certNo, normalFont);
        certInfo.setAlignment(Element.ALIGN_LEFT);
        document.add(certInfo);

        Paragraph dateInfo = new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                normalFont);
        dateInfo.setAlignment(Element.ALIGN_RIGHT);
        dateInfo.setSpacingAfter(20);
        document.add(dateInfo);

        // Add certificate body
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        String bodyText = String.format(
                "This is to certify that %s bearing Student ID: %s " +
                        "is a bonafide student of this institution studying in %s, " +
                        "Department of %s, Division %s for the academic year %s.\n\n" +
                        "Date of Birth: %s (%s)\n\n" +
                        "Gender: %s\n\n" +
                        "Address: %s\n\n" +
                        "This certificate is issued upon the request for the purpose of %s.",
                bonafide.getFullName(),
                bonafide.getStudentId(),
                bonafide.getCourse(),
                bonafide.getDepartment(),
                bonafide.getDivision(),
                bonafide.getYear(),
                bonafide.getDob(),
                bonafide.getDobInWords() != null ? bonafide.getDobInWords() : "Not Available",
                bonafide.getGender(),
                bonafide.getPermanentAddress(),
                bonafide.getCertificateReason() != null ? bonafide.getCertificateReason() : "General Purpose");

        Paragraph body = new Paragraph(bodyText, bodyFont);
        body.setAlignment(Element.ALIGN_JUSTIFIED);
        body.setSpacingAfter(40);
        body.setLeading(18f);
        document.add(body);

        // Add signature section
        addSignatureSection(document);

        // Add footer
        addFooter(document);

        document.close();

        return baos.toByteArray();
    }

    private void addCollegeHeader(Document document) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph collegeName = new Paragraph(COLLEGE_NAME, headerFont);
        collegeName.setAlignment(Element.ALIGN_CENTER);
        document.add(collegeName);

        Paragraph collegeAddress = new Paragraph(COLLEGE_ADDRESS, subHeaderFont);
        collegeAddress.setAlignment(Element.ALIGN_CENTER);
        document.add(collegeAddress);

        Paragraph collegeContact = new Paragraph("Phone: " + COLLEGE_PHONE + " | Email: " + COLLEGE_EMAIL,
                subHeaderFont);
        collegeContact.setAlignment(Element.ALIGN_CENTER);
        collegeContact.setSpacingAfter(10);
        document.add(collegeContact);

        // Add horizontal line
        Paragraph line = new Paragraph("_".repeat(80));
        line.setAlignment(Element.ALIGN_CENTER);
        document.add(line);
    }

    private void addSignatureSection(Document document) throws DocumentException {
        Font signFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(40);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(0);
        leftCell.addElement(new Phrase("", signFont));

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(0);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(new Phrase("\n\n\n_____________________", signFont));
        rightCell.addElement(new Phrase("\nPrincipal", signFont));
        rightCell.addElement(new Phrase("\n" + COLLEGE_NAME, FontFactory.getFont(FontFactory.HELVETICA, 9)));

        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);

        Paragraph footer = new Paragraph(
                "\n\n\nThis is a computer-generated document. " +
                        "For verification, please contact the college office.",
                footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
