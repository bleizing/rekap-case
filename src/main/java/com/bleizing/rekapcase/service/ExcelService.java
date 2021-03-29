package com.bleizing.rekapcase.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bleizing.rekapcase.controller.FileController;
import com.bleizing.rekapcase.helper.ExcelHelper;
import com.bleizing.rekapcase.service.PdfService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class ExcelService {
	private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);
	
	@Autowired
	PdfService pdfService;
	
	public ArrayList<String> save(MultipartFile file) {
		ArrayList<String> filenameArrayList = new ArrayList();
	    try {
	      ArrayList<HashMap<Integer, ArrayList<String>>> dataArrayList = ExcelHelper.convertExcel(file.getInputStream());
	      
	      for (int i = 0; i < dataArrayList.size(); i++) {
			  HashMap<Integer, ArrayList<String>> dataHashMap = dataArrayList.get(i);
			  filenameArrayList.add(pdfService.createPdfDoc(dataHashMap, i + 1));
	      }

		   return filenameArrayList;
	      
	    } catch (IOException e) {
			logger.error(e.getMessage(), e);
	      throw new RuntimeException("fail to store excel data: " + e.getMessage());
	    }
	  }
}
