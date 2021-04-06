package com.bleizing.rekapcase.service;

import com.bleizing.rekapcase.event.HeaderFooterPageEvent;
import com.bleizing.rekapcase.property.FileStorageProperties;
import com.bleizing.rekapcase.service.ExcelService;
import com.bleizing.rekapcase.util.DateUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
	private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

	@Autowired
	FileStorageProperties fileStorageProperties;

	private HashMap<Integer, String> headerHashMap = new HashMap<>();

	public String createPdfDoc(HashMap<Integer, ArrayList<String>> dataHashMap, int type) {
		String filename = "";
		Document document = new Document(PageSize.A4, 40, 50, 30, 30);
		PdfWriter writer;

		initHeader();

		try {
			String dateString = DateUtils.getStartDate(dataHashMap.get(1).get(1).toString());
			String monthYear = DateUtils.getMonthYear(dateString);
			filename = "Rekap Transaksi Gagal Sistem ";
			filename += (type == 2) ? "Native" : "";
			filename += " - " + monthYear;
			filename += ".pdf";

			logger.info("create pdf = " + filename);

			Files.createDirectories(Paths.get(Paths.get(".").normalize().toAbsolutePath() + fileStorageProperties.getDownloadDir())
					.toAbsolutePath().normalize());

			writer = PdfWriter.getInstance(document, new FileOutputStream(Paths.get(".").normalize().toAbsolutePath() + fileStorageProperties.getDownloadDir() + "/" + filename));

			// add header and footer
			HeaderFooterPageEvent event = new HeaderFooterPageEvent(monthYear);
			writer.setPageEvent(event);

			// write to document
			document.open();

			BaseFont base = BaseFont.createFont(Paths.get(".").normalize().toAbsolutePath() + "/assets/font/calibri.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
			Font font = new Font(base, 11.5f, Font.NORMAL);

			int rowIndex = 0;

			// read row
			Set<Entry<Integer, ArrayList<String>>> sets = dataHashMap.entrySet();
			int dataHashMapLength = sets.size() - 1;
			for(Entry<Integer, ArrayList<String>> entry : sets) {
				ArrayList<String> stringArrayList = entry.getValue();

				int colIndex = 0;

				// read col
				for (String string : stringArrayList) {
					if (string.equals("") || string.equals(" ")) {
						continue;
					}

					if (headerHashMap.containsKey(colIndex)) {
						if (colIndex == 1) {
							string = DateUtils.formatPdf(DateUtils.getStartDate(string));
						}

						String title = headerHashMap.get(colIndex);

						PdfPTable table = new PdfPTable(3); // Create 3 columns in table.

						// Set table Width as 100%
						table.setWidthPercentage(100f);

						// Set Column widths of table
						float[] columnWidths = { 2f, 0.25f, 10f };

						table.setWidths(columnWidths);
						table.getDefaultCell().setBorder(0);

						PdfPCell cell1 = new PdfPCell();
						Paragraph p1 = new Paragraph(title, font);
						//		                 p1.setLeading(11);
						cell1.setPaddingTop(-5f);
						cell1.addElement(p1);
						cell1.setBorder(Rectangle.NO_BORDER);

						PdfPCell cell2 = new PdfPCell();
						Paragraph p2 = new Paragraph(":", font);
						//		                 p2.setLeading(11);
						cell2.addElement(p2);
						cell2.setPaddingTop(-5f);
						cell2.setBorder(Rectangle.NO_BORDER);

						PdfPCell cell3 = new PdfPCell();
						Paragraph p3 = new Paragraph(string, font);
						//		                 p3.setLeading(11);
						cell3.setPaddingTop(-5f);
						cell3.addElement(p3);
						cell3.setBorder(Rectangle.NO_BORDER);

						table.addCell(cell1);
						table.addCell(cell2);
						table.addCell(cell3);

						document.add(table);
					}

					colIndex++;
				}
				rowIndex++;

				if (entry.getKey() % 5 == 0) {
					if (dataHashMapLength > rowIndex) {
						document.newPage();
					}
				} else {
					if (dataHashMapLength >= rowIndex) {
						document.add(new Paragraph("\n"));
					}
				}
			}

			document.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
		}

		logger.info("end of create pdf");

		return "downloadFile/" + filename;
	}

	private void initHeader() {
		headerHashMap.put(1, "Hari / Tanggal");
		headerHashMap.put(5, "Problem");
		headerHashMap.put(2, "Time Start");
		headerHashMap.put(3, "Time End");
		headerHashMap.put(6, "Root Cause");
		headerHashMap.put(7, "Action");
		headerHashMap.put(4, "Duration");
		headerHashMap.put(10, "Status");
	}
}
