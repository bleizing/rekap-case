package com.bleizing.rekapcase.service;

import com.bleizing.rekapcase.event.HeaderFooterPageEvent;
import com.bleizing.rekapcase.helper.FileHelper;
import com.bleizing.rekapcase.model.MFile;
import com.bleizing.rekapcase.model.TContent;
import com.bleizing.rekapcase.property.FileStorageProperties;
import com.bleizing.rekapcase.repository.MFileRepository;
import com.bleizing.rekapcase.repository.TContentRepository;
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
import java.util.Map;
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
	
	@Autowired
	MFileRepository mFileRepository;
	
	@Autowired
	TContentRepository tContentRepository; 

	private HashMap<String, String> headerHashMap = new HashMap<>();

	public void createPdfDoc() {		
		HashMap<MFile, ArrayList<TContent>> dataHashMap = getData();
		
		if (dataHashMap != null && dataHashMap.size() == 2) {
			try {
				for (Entry<MFile, ArrayList<TContent>> entry : dataHashMap.entrySet()) {
					MFile mFile = entry.getKey();
					ArrayList<TContent> tContentArrayList = entry.getValue();
					
					String filename = "";
					Document document = new Document(PageSize.A4, 40, 50, 30, 30);
					PdfWriter writer;
					
					filename = FileHelper.createFileNameByDate(mFile.getPeriode(), mFile.getType());
		
					logger.info("create pdf file = " + filename);
		
					Files.createDirectories(Paths.get(Paths.get(".").normalize().toAbsolutePath() + fileStorageProperties.getDownloadDir())
							.toAbsolutePath().normalize());
		
					writer = PdfWriter.getInstance(document, new FileOutputStream(Paths.get(".").normalize().toAbsolutePath() + fileStorageProperties.getDownloadDir() + "/" + filename));
		
					// add header and footer
					HeaderFooterPageEvent event = new HeaderFooterPageEvent(mFile.getPeriode());
					writer.setPageEvent(event);
		
					// write to document
					document.open();
		
					BaseFont base = BaseFont.createFont(Paths.get(".").normalize().toAbsolutePath() + "/assets/font/calibri.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
					Font font = new Font(base, 11.5f, Font.NORMAL);
		
					int rowIndex = 0;
					
					int contentLength = tContentArrayList.size() - 1;
					for(TContent tContent : tContentArrayList) {
						putData(tContent);
						
						for(Entry<String, String> entryData : headerHashMap.entrySet()) {
							String title = entryData.getKey();
							String content = entryData.getValue();
	
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
							Paragraph p3 = new Paragraph(content, font);
							//		                 p3.setLeading(11);
							cell3.setPaddingTop(-5f);
							cell3.addElement(p3);
							cell3.setBorder(Rectangle.NO_BORDER);
	
							table.addCell(cell1);
							table.addCell(cell2);
							table.addCell(cell3);
	
							document.add(table);
						}
						rowIndex++;
		
						if (rowIndex % 5 == 0) {
							if (contentLength > rowIndex) {
								document.newPage();
							}
						} else {
							if (contentLength >= rowIndex) {
								document.add(new Paragraph("\n"));
							}
						}
					}
		
					document.close();
				}
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
		}

		logger.info("end of create pdf");
	}

	private void putData(TContent tContent) {
		headerHashMap.put("Hari / Tanggal", DateUtils.formatPdf(DateUtils.getStartDate(tContent.getTanggal())));
		headerHashMap.put("Problem", tContent.getProblem());
		headerHashMap.put("Time Start", tContent.getTimeStart());
		headerHashMap.put("Time End", tContent.getTimeEnd());
		headerHashMap.put("Root Cause", tContent.getRootCause());
		headerHashMap.put("Action", tContent.getAction());
		headerHashMap.put("Duration", tContent.getDuration());
		headerHashMap.put("Status", tContent.getStatus());
	}
	
	public HashMap<MFile, ArrayList<TContent>> getData() {
		HashMap<MFile, ArrayList<TContent>> dataHashMap = new HashMap<>();
		
		ArrayList<MFile> mFileArrayList = mFileRepository.getLastData();
		ArrayList<TContent> tContentArrayList;
		
		if (mFileArrayList != null && mFileArrayList.size() == 2) {
			for (MFile mFile : mFileArrayList) {
				tContentArrayList = tContentRepository.findByMFileId(mFile.getId());
				dataHashMap.put(mFile, tContentArrayList);
			}
		}
		
		return dataHashMap;
	}
}
