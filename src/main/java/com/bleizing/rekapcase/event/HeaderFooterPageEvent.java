package com.bleizing.rekapcase.event;

import com.bleizing.rekapcase.property.FileStorageProperties;
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

//    private PdfTemplate t;
//    private Image total;

//    public void onOpenDocument(PdfWriter writer, Document document) {
//        t = writer.getDirectContent().createTemplate(30, 16);
//        try {
//            total = Image.getInstance(t);
//            total.setRole(PdfName.ARTIFACT);
//        } catch (DocumentException de) {
//            throw new ExceptionConverter(de);
//        }
//    }
	
	private String monthYear;
	
	@Autowired
	FileStorageProperties fileStorageProperties;
	
	public HeaderFooterPageEvent(String monthYear) {
		// TODO Auto-generated constructor stub
		this.monthYear = monthYear;
	}
    
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    	// TODO Auto-generated method stub
    	addHeader(writer);

    	try {
			document.add(new Paragraph("\n"));
	    	document.add(new Paragraph("\n"));
	    	document.add(new Paragraph("\n"));
	    	document.add(new Paragraph("\n"));
	    	document.add(new Paragraph("\n"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void addHeader(PdfWriter writer){
        PdfPTable header = new PdfPTable(3);
        try {
        	// Set table Width as 100%
        	header.setWidthPercentage(100f);

            // Set Column widths of table
            float[] columnWidths = { 2.7f, 10f, 4f };
            header.setWidths(columnWidths);
            header.setTotalWidth(500);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(30);
            header.getDefaultCell().setBorder(Rectangle.BOX);
            header.getDefaultCell().setBorderColor(BaseColor.BLACK);
            
            Image istLogo = Image.getInstance(Paths.get(".").normalize().toAbsolutePath() + "/assets/logo/ist.jpeg");
            PdfPCell istCell = new PdfPCell();
            istCell.setPaddingRight(15f);
            istCell.setPaddingLeft(8f);
            istCell.setPaddingTop(0);
            istCell.setPaddingBottom(0);
            istCell.addElement(istLogo);
            header.addCell(istCell);
            
            BaseFont base = BaseFont.createFont(Paths.get(".").normalize().toAbsolutePath() + "/assets/font/calibri.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font font = new Font(base, 16f, Font.BOLD);
            
            PdfPCell text = new PdfPCell();
            Paragraph p1 = new Paragraph("Summary Transaksi Gagal Sistem", font);
//            p1.setLeading(25);
            p1.setAlignment(Element.ALIGN_CENTER);
            text.addElement(p1);
            Paragraph p2 = new Paragraph(monthYear, font);
            p2.setLeading(30);
            p2.setAlignment(Element.ALIGN_CENTER);
            text.addElement(p2);
            text.setPaddingBottom(15);
            text.setPaddingLeft(10);
            text.setBorder(Rectangle.BOX);
            text.setBorderColor(BaseColor.BLACK);
            text.setPaddingTop(-2f);
            text.setVerticalAlignment(Element.ALIGN_MIDDLE);
            text.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(text);

            Image briLogo = Image.getInstance(Paths.get(".").normalize().toAbsolutePath() + "/assets/logo/bri.jpeg");
            briLogo.scaleAbsolute(100, 100);
            briLogo.setAlignment(Image.ALIGN_CENTER);
            PdfPCell briCell = new PdfPCell();
            briCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            briCell.addElement(briLogo);
            header.addCell(briCell);

            // write content
            header.writeSelectedRows(0, -1, 40, 800, writer.getDirectContent());
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (MalformedURLException e) {
            throw new ExceptionConverter(e);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

//    @Override
//    public void onEndPage(PdfWriter writer, Document document) {
//        addHeader(writer);
//        addFooter(writer);
//    }
//
//    private void addHeader(PdfWriter writer){
//        PdfPTable header = new PdfPTable(2);
//        try {
//            // set defaults
//            header.setWidths(new int[]{2, 24});
//            header.setTotalWidth(527);
//            header.setLockedWidth(true);
//            header.getDefaultCell().setFixedHeight(40);
//            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
//            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
//
//            // add image
//            Image logo = Image.getInstance(Paths.get(".").normalize().toAbsolutePath() + "/src/main/resources/assets/logo/bri.jpeg");
//            header.addCell(logo);
//
//            // add text
//            PdfPCell text = new PdfPCell();
//            text.setPaddingBottom(15);
//            text.setPaddingLeft(10);
//            text.setBorder(Rectangle.BOTTOM);
//            text.setBorderColor(BaseColor.LIGHT_GRAY);
//            text.addElement(new Phrase("iText PDF Header Footer Example", new Font(Font.FontFamily.HELVETICA, 12)));
//            text.addElement(new Phrase("https://memorynotfound.com", new Font(Font.FontFamily.HELVETICA, 8)));
//            header.addCell(text);
//
//            // write content
//            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
//        } catch(DocumentException de) {
//            throw new ExceptionConverter(de);
//        } catch (MalformedURLException e) {
//            throw new ExceptionConverter(e);
//        } catch (IOException e) {
//            throw new ExceptionConverter(e);
//        }
//    }

//    private void addFooter(PdfWriter writer){
//        PdfPTable footer = new PdfPTable(3);
//        try {
//            // set defaults
//            footer.setWidths(new int[]{24, 2, 1});
//            footer.setTotalWidth(527);
//            footer.setLockedWidth(true);
//            footer.getDefaultCell().setFixedHeight(40);
//            footer.getDefaultCell().setBorder(Rectangle.TOP);
//            footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
//
//            // add copyright
//            footer.addCell(new Phrase("\u00A9 Memorynotfound.com", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
//
//            // add current page count
//            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)));
//
//            // add placeholder for total page count
//            PdfPCell totalPageCount = new PdfPCell(total);
//            totalPageCount.setBorder(Rectangle.TOP);
//            totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
//            footer.addCell(totalPageCount);
//
//            // write page
//            PdfContentByte canvas = writer.getDirectContent();
//            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
//            footer.writeSelectedRows(0, -1, 34, 50, canvas);
//            canvas.endMarkedContentSequence();
//        } catch(DocumentException de) {
//            throw new ExceptionConverter(de);
//        }
//    }
//
//    public void onCloseDocument(PdfWriter writer, Document document) {
//        int totalLength = String.valueOf(writer.getPageNumber()).length();
//        int totalWidth = totalLength * 5;
//        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
//                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
//                totalWidth, 6, 0);
//    }
}
