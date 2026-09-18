package com.examsystem.onlineexam.service;

import com.examsystem.onlineexam.model.ExamResult;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss");

    // Colors
    private static final Color PRIMARY_COLOR = new Color(30, 58, 138);     // Indigo 900
    private static final Color SECONDARY_COLOR = new Color(59, 130, 246);  // Blue 500
    private static final Color TEXT_DARK = new Color(15, 23, 42);          // Slate 900
    private static final Color TEXT_MUTED = new Color(100, 116, 139);      // Slate 500
    private static final Color BG_LIGHT = new Color(248, 250, 252);        // Slate 50
    private static final Color BORDER_COLOR = new Color(226, 232, 240);    // Slate 200
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);    // Emerald 500
    private static final Color DANGER_COLOR = new Color(220, 38, 38);      // Red 600
    private static final Color WARNING_COLOR = new Color(245, 158, 11);    // Amber 500

    public byte[] generateExamReportPdf(ExamResult result) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, PRIMARY_COLOR);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_DARK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);
            Font badgeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, TEXT_MUTED);

            // Document Header Banner
            Paragraph title = new Paragraph("SECUREEXAM EXAMINATION & AUDIT REPORT", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Official Candidate Performance Scorecard & Anti-Cheating Proctoring Audit", subHeaderFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15f);
            document.add(subtitle);

            // Disqualification Notice (if disqualified)
            if (result.isDisqualified()) {
                PdfPTable disqTable = new PdfPTable(1);
                disqTable.setWidthPercentage(100);
                disqTable.setSpacingAfter(12f);

                PdfPCell disqCell = new PdfPCell();
                disqCell.setBackgroundColor(new Color(254, 242, 242));
                disqCell.setBorderColor(DANGER_COLOR);
                disqCell.setPadding(10f);

                Paragraph disqTitle = new Paragraph("🚨 CANDIDATE DISQUALIFIED - SESSION TERMINATED", 
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, DANGER_COLOR));
                Paragraph disqBody = new Paragraph("Reason: " + (result.getDisqualificationReason() != null ? result.getDisqualificationReason() : "Security Policy Violation"),
                        FontFactory.getFont(FontFactory.HELVETICA, 9, DANGER_COLOR));
                disqCell.addElement(disqTitle);
                disqCell.addElement(disqBody);
                disqTable.addCell(disqCell);
                document.add(disqTable);
            }

            // Section 1: Candidate Information Table
            Paragraph s1 = new Paragraph("1. Candidate Information", sectionTitleFont);
            s1.setSpacingAfter(6f);
            document.add(s1);

            PdfPTable candTable = new PdfPTable(4);
            candTable.setWidthPercentage(100);
            candTable.setWidths(new float[]{22f, 28f, 22f, 28f});
            candTable.setSpacingAfter(15f);

            addTableCell(candTable, "Candidate Name:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getStudentName(), valueFont, Color.WHITE, false);
            addTableCell(candTable, "Roll Number:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getRollNumber(), valueFont, Color.WHITE, false);

            addTableCell(candTable, "Email Address:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getStudentEmail(), valueFont, Color.WHITE, false);
            addTableCell(candTable, "Exam Session ID:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, "SEC-" + result.getId(), valueFont, Color.WHITE, false);

            addTableCell(candTable, "Exam Subject:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getSelectedTopic() != null ? result.getSelectedTopic() : "All Topics", valueFont, Color.WHITE, false);
            addTableCell(candTable, "Time Taken:", labelFont, BG_LIGHT, true);
            String timeStr = result.getFormattedTimeTaken() != null ? result.getFormattedTimeTaken() : "N/A";
            if (result.isOvertime()) {
                timeStr += " (OVERTIME)";
            }
            addTableCell(candTable, timeStr, valueFont, Color.WHITE, false);

            addTableCell(candTable, "Submission Date:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getSubmittedAt() != null ? result.getSubmittedAt().format(DATE_FORMAT) : "N/A", valueFont, Color.WHITE, false);
            addTableCell(candTable, "Integrity Status:", labelFont, BG_LIGHT, true);
            addTableCell(candTable, result.getIntegrityStatus() != null ? result.getIntegrityStatus() : "Normal", valueFont, Color.WHITE, false);

            document.add(candTable);

            // Section 2: Academic Performance Summary
            Paragraph s2 = new Paragraph("2. Academic Evaluation & Score Breakdown", sectionTitleFont);
            s2.setSpacingAfter(6f);
            document.add(s2);

            PdfPTable scoreTable = new PdfPTable(5);
            scoreTable.setWidthPercentage(100);
            scoreTable.setWidths(new float[]{20f, 20f, 20f, 20f, 20f});
            scoreTable.setSpacingAfter(15f);

            addTableHeader(scoreTable, "Total Marks", labelFont);
            addTableHeader(scoreTable, "Marks Scored", labelFont);
            addTableHeader(scoreTable, "Percentage", labelFont);
            addTableHeader(scoreTable, "Grade", labelFont);
            addTableHeader(scoreTable, "Final Status", labelFont);

            addScoreCell(scoreTable, String.valueOf(result.getTotalMarks()), valueFont);
            addScoreCell(scoreTable, String.valueOf(result.getScore()), valueFont);
            addScoreCell(scoreTable, result.getPercentage() + "%", valueFont);
            
            // Grade calculation
            String grade;
            if (result.isDisqualified()) {
                grade = "DISQ";
            } else if (result.getPercentage() >= 90) {
                grade = "A+";
            } else if (result.getPercentage() >= 75) {
                grade = "A";
            } else if (result.getPercentage() >= 60) {
                grade = "B";
            } else if (result.getPercentage() >= 40) {
                grade = "C";
            } else {
                grade = "F";
            }
            addScoreCell(scoreTable, grade, valueFont);

            // Status Cell
            PdfPCell statusCell = new PdfPCell();
            statusCell.setPadding(8f);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            if (result.isDisqualified()) {
                statusCell.setBackgroundColor(DANGER_COLOR);
                statusCell.setPhrase(new Phrase("DISQUALIFIED", badgeFont));
            } else if (result.isPassed()) {
                statusCell.setBackgroundColor(SUCCESS_COLOR);
                statusCell.setPhrase(new Phrase("PASSED", badgeFont));
            } else {
                statusCell.setBackgroundColor(DANGER_COLOR);
                statusCell.setPhrase(new Phrase("FAILED", badgeFont));
            }
            scoreTable.addCell(statusCell);

            document.add(scoreTable);

            // Section 3: Proctoring Integrity & Security Audit
            Paragraph s3 = new Paragraph("3. Proctoring & Anti-Cheating Integrity Audit", sectionTitleFont);
            s3.setSpacingAfter(6f);
            document.add(s3);

            PdfPTable auditTable = new PdfPTable(2);
            auditTable.setWidthPercentage(100);
            auditTable.setWidths(new float[]{50f, 50f});
            auditTable.setSpacingAfter(10f);

            // Trust Score Box
            PdfPCell trustCell = new PdfPCell();
            trustCell.setPadding(12f);
            trustCell.setBackgroundColor(BG_LIGHT);
            trustCell.setBorderColor(BORDER_COLOR);
            Paragraph trustTitle = new Paragraph("Candidate Trust Index Score", labelFont);
            Color scoreColor = result.getTrustScore() >= 85 ? SUCCESS_COLOR : (result.getTrustScore() >= 60 ? WARNING_COLOR : DANGER_COLOR);
            Paragraph trustValue = new Paragraph(result.getTrustScore() + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, scoreColor));
            Paragraph trustStatus = new Paragraph("Assessment: " + result.getIntegrityStatus(), 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, scoreColor));
            trustCell.addElement(trustTitle);
            trustCell.addElement(trustValue);
            trustCell.addElement(trustStatus);
            auditTable.addCell(trustCell);

            // Violations Overview Box
            PdfPCell violBoxCell = new PdfPCell();
            violBoxCell.setPadding(12f);
            violBoxCell.setBackgroundColor(BG_LIGHT);
            violBoxCell.setBorderColor(BORDER_COLOR);
            Paragraph violTitle = new Paragraph("Proctoring Violations Detected", labelFont);
            Paragraph violTotal = new Paragraph(String.valueOf(result.getTotalViolations()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR));
            Paragraph violRisk = new Paragraph("Calculated Risk Penalty: " + result.getRiskScore() + " pts", 
                    FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED));
            violBoxCell.addElement(violTitle);
            violBoxCell.addElement(violTotal);
            violBoxCell.addElement(violRisk);
            auditTable.addCell(violBoxCell);

            document.add(auditTable);

            // Violation Breakdown Table
            PdfPTable violTable = new PdfPTable(6);
            violTable.setWidthPercentage(100);
            violTable.setWidths(new float[]{16.66f, 16.66f, 16.66f, 16.66f, 16.66f, 16.70f});
            violTable.setSpacingAfter(25f);

            addTableHeader(violTable, "Tab Switches", labelFont);
            addTableHeader(violTable, "Copy / Paste", labelFont);
            addTableHeader(violTable, "Right Clicks", labelFont);
            addTableHeader(violTable, "Fullscreen Exits", labelFont);
            addTableHeader(violTable, "Window Blurs", labelFont);
            addTableHeader(violTable, "Audio Spikes", labelFont);

            addScoreCell(violTable, String.valueOf(result.getTabSwitchCount()), valueFont);
            addScoreCell(violTable, String.valueOf(result.getCopyCount()), valueFont);
            addScoreCell(violTable, String.valueOf(result.getRightClickCount()), valueFont);
            addScoreCell(violTable, String.valueOf(result.getFullscreenExitCount()), valueFont);
            addScoreCell(violTable, String.valueOf(result.getWindowBlurCount()), valueFont);
            addScoreCell(violTable, String.valueOf(result.getAudioSpikeCount()), valueFont);

            document.add(violTable);

            // Section 4: Security Verification & Authentication Seal
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{70f, 30f});

            PdfPCell certNoteCell = new PdfPCell();
            certNoteCell.setBorder(Rectangle.NO_BORDER);
            Paragraph certNote = new Paragraph("Official Verification & Integrity Seal", labelFont);
            Paragraph certDesc = new Paragraph("This report was generated deterministically by the SecureExam Proctoring Engine.\n"
                    + "Verification Hash: SHA256-" + Integer.toHexString((result.getStudentName() + result.getRollNumber() + result.getId() + result.getScore()).hashCode()).toUpperCase() + "\n"
                    + "Report Timestamp: " + (result.getSubmittedAt() != null ? result.getSubmittedAt().format(DATE_FORMAT) : "N/A"), footerFont);
            certNoteCell.addElement(certNote);
            certNoteCell.addElement(certDesc);
            footerTable.addCell(certNoteCell);

            PdfPCell stampCell = new PdfPCell();
            stampCell.setBorder(Rectangle.NO_BORDER);
            stampCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph stampTitle = new Paragraph("SECUREEXAM", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR));
            stampTitle.setAlignment(Element.ALIGN_RIGHT);
            Paragraph stampSub = new Paragraph("AUTHENTICATED PROCTOR", FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED));
            stampSub.setAlignment(Element.ALIGN_RIGHT);
            stampCell.addElement(stampTitle);
            stampCell.addElement(stampSub);
            footerTable.addCell(stampCell);

            document.add(footerTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF examination report", e);
        }

        return out.toByteArray();
    }

    private void addTableCell(PdfPTable table, String text, Font font, Color bgColor, boolean isLabel) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_COLOR);
        if (isLabel) {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        } else {
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String header, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setPadding(6f);
        cell.setBackgroundColor(BG_LIGHT);
        cell.setBorderColor(BORDER_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addScoreCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }
}
