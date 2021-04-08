package com.bleizing.rekapcase.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bleizing.rekapcase.controller.FileController;
import com.bleizing.rekapcase.model.MFile;
import com.bleizing.rekapcase.model.TContent;
import com.bleizing.rekapcase.repository.MFileRepository;
import com.bleizing.rekapcase.repository.TContentRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import net.bytebuddy.asm.Advice.This;

@Service
public class ExcelHelper {
	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);
	
	@Autowired
	MFileRepository mFileRepository;
	
	@Autowired
	TContentRepository tContentRepository;

	public static boolean hasExcelFormat(MultipartFile file) {
		if (!TYPE.equals(file.getContentType())) {
			return false;
		}

		return true;
	}

	public void convertExcel(InputStream is) {
		DataFormatter dataFormatter = new DataFormatter(new Locale("id", "ID"));
		try {
			Workbook workbook = new XSSFWorkbook(is);

			ArrayList<HashMap<Integer, ArrayList<String>>> sheetArrayList = new ArrayList<>();

			for (int i = 2; i > 0 ; i--) {
				logger.info("read sheet = -" + i);
				
				Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - i);
				Iterator<Row> rows = sheet.iterator();
				
				String filename = FileHelper.createFileNameBySheetName(sheet.getSheetName(), i);
				
				MFile mFile = mFileRepository.findByNama(filename);
				
				if (mFile != null) {
					mFileRepository.delete(mFile);
				}
				
				mFile = new MFile();
				mFile.setName(filename);
				mFile.setPeriode(FileHelper.getPeriode(sheet.getSheetName()));
				mFile.setType(i);
				
				mFileRepository.save(mFile);

				int rowNumber = 0;

				HashMap<Integer, ArrayList<String>> rowHashMap = new HashMap<>();

				while (rows.hasNext()) {
					Row currentRow = rows.next();

					// skip header
					if (rowNumber == 0) {
						rowNumber++;
						continue;
					}

					Iterator<Cell> cellsInRow = currentRow.iterator();

					ArrayList<String> rowArrayList = new ArrayList<>();

					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();

						switch (currentCell.getCellType()) {
							case STRING:
								rowArrayList.add(currentCell.getRichStringCellValue().getString());
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(currentCell)) {
									String date = dataFormatter.formatCellValue(currentCell);
									rowArrayList.add(date);
								} else {
									rowArrayList.add(currentCell.getNumericCellValue() + "");
								}
								break;
							case BOOLEAN:
								rowArrayList.add(currentCell.getBooleanCellValue() + "");
								break;
							case FORMULA:
								rowArrayList.add(currentCell.getCellFormula() + "");
								break;
								
							default: rowArrayList.add(" ");
						}
					}

					rowHashMap.put(rowNumber, rowArrayList);
					
					TContent tContent = new TContent();
					
					tContent.setTanggal(rowArrayList.get(1));
					tContent.setTimeStart(rowArrayList.get(2));
					tContent.setTimeEnd(rowArrayList.get(3));
					tContent.setDuration(rowArrayList.get(4));
					tContent.setProblem(rowArrayList.get(5));
					tContent.setRootCause(rowArrayList.get(6));
					tContent.setAction(rowArrayList.get(7));
					tContent.setStatus(rowArrayList.get(10));
					tContent.setMFile(mFile);
					
					tContentRepository.save(tContent);

					rowNumber++;
				}

				sheetArrayList.add(rowHashMap);
				
				logger.info("end of read sheet");
			}

			workbook.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		}
	}
}
