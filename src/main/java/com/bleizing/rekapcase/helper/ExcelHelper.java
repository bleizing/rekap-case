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
import org.springframework.web.multipart.MultipartFile;

import com.bleizing.rekapcase.controller.FileController;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class ExcelHelper {
	public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);

	public static boolean hasExcelFormat(MultipartFile file) {
		if (!TYPE.equals(file.getContentType())) {
			return false;
		}

		return true;
	}

	public static ArrayList<HashMap<Integer, ArrayList<String>>> convertExcel(InputStream is) {
		DataFormatter dataFormatter = new DataFormatter(new Locale("id", "ID"));
		try {
			Workbook workbook = new XSSFWorkbook(is);

			ArrayList<HashMap<Integer, ArrayList<String>>> sheetArrayList = new ArrayList<>();

			for (int i = 2; i > 0 ; i--) {
				logger.info("read sheet = -" + i);
				
				Sheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - i);
				Iterator<Row> rows = sheet.iterator();

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
							
						default: rowArrayList.add("");
						}
					}

					rowHashMap.put(rowNumber, rowArrayList);

					rowNumber++;
				}

				sheetArrayList.add(rowHashMap);
				
				logger.info("end of read sheet");
			}

			workbook.close();

			return sheetArrayList;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		}
	}
}
