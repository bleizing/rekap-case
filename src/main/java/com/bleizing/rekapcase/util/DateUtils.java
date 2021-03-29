package com.bleizing.rekapcase.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

public class DateUtils {
	public static SimpleDateFormat createSimpleDateFormat(String pattern, Locale locale) {
		return new SimpleDateFormat(pattern, locale);
	}
	
	public static SimpleDateFormat createSimpleDateFormat(String pattern) {
		return createSimpleDateFormat(pattern, new Locale("id", "ID"));
	}
	
	public static Date parse(String pattern, String dateString) {
		return parse(pattern, dateString, new Locale("id", "ID"));
	}
	
	public static Date parse(String pattern, String dateString, Locale locale) {
		Date date = null;
		SimpleDateFormat simpleDateFormat = createSimpleDateFormat(pattern, locale);
		
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static String format(String pattern, String dateString) {
		return format(pattern, parse(pattern, dateString));
	}

	public static String format(String pattern, String dateString, Locale locale) {
		return format(pattern, parse(pattern, dateString), locale);
	}

	public static String format(String pattern, Date date) {
		return format(pattern, date, new Locale("id", "ID"));
	}
	
	public static String format(String pattern, Date date, Locale locale) {
		String formatDateString = "";
		
		SimpleDateFormat simpleDateFormat = createSimpleDateFormat(pattern, locale);
		formatDateString = simpleDateFormat.format(date);
		
		return formatDateString;
	}
	
	public static String formatPdf(String dateString) {
		Date date = parse("d-MMM-yy", dateString);
		return format("EEEEE, dd MMMM yyyy", date);
	}
	
	public static String getMonthYear(String dateString) {
		Date date = parse("d-MMM-yy", dateString);
		return format("MMMM yyyy", date);
	}
	
	public static String getStartDate(String dateString) {
		if (dateString.equals("") || dateString.equals(" ")) {
			return dateString;
		}
		
		if (dateString.contains(" - ")) {
			String[] dateStrings = dateString.split(" - ");
			dateString = dateStrings[0];
			
			Date date = parse("dd/mm/yyyy", dateString);
			SimpleDateFormat simpleDateFormat = createSimpleDateFormat("dd-MMM-yy");
			dateString = simpleDateFormat.format(date);
		}
		
		return dateString;
	}
}
