package com.bleizing.rekapcase.helper;

import com.bleizing.rekapcase.util.DateUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class FileHelper {
	public static String createFileNameByDate(String monthYear, int type) {
		String filename = "Rekap Transaksi Gagal Sistem ";
		filename += (type == 1) ? "Native" : "";
		filename += " - " + monthYear;
		filename += ".pdf";
		
		return filename;
	}
	
	public static String createFileNameBySheetName(String sheetName, int type) {	
		sheetName = getPeriode(sheetName);
		String filename = "Rekap Transaksi Gagal Sistem ";
		filename += (type == 1) ? "Native" : "";
		filename += " - " + sheetName;
		filename += ".pdf";
		
		return filename;
	}
	
	public static String getPeriode(String sheetName) {
		return DateUtils.convertFromSheetName(sheetName.split(" - ")[0]);
	}
}
