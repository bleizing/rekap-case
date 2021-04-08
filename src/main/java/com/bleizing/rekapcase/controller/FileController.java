package com.bleizing.rekapcase.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bleizing.rekapcase.helper.ExcelHelper;
import com.bleizing.rekapcase.message.ResponseMessage;
import com.bleizing.rekapcase.model.MFile;
import com.bleizing.rekapcase.property.FileStorageProperties;
import com.bleizing.rekapcase.repository.MFileRepository;
import com.bleizing.rekapcase.service.ExcelService;
import com.bleizing.rekapcase.service.FileStorageService;

@RestController
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private ExcelService fileService;
	
	@Autowired
	private MFileRepository mFileRepository;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@PostMapping("/upload")
	public ResponseEntity<ArrayList<ResponseMessage>> uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("process upload file = " + StringUtils.cleanPath(file.getOriginalFilename()));

		String message = "";

		String fileName = fileStorageService.storeFile(file);
		message = "Uploaded the file successfully: " + file.getOriginalFilename() + " with name " + fileName;
		logger.info(message);

		ArrayList<MFile> mFileArrayList = new ArrayList<>();
		ArrayList<ResponseMessage> responseMessages = new ArrayList<>();

		if (ExcelHelper.hasExcelFormat(file)) {
			try {
				fileService.save(file);
				
				mFileArrayList = mFileRepository.getLastData();

				if (mFileArrayList.size() > 0) {
					for (MFile mFile : mFileArrayList) {
						String filename = "downloadFile/" + mFile.getName();
						responseMessages.add(new ResponseMessage(message, filename));
					}
				}

				return ResponseEntity.status(HttpStatus.OK).body(responseMessages);
			} catch (Exception e) {
				message = "Could not upload the file: " + file.getOriginalFilename() + "!";
				logger.error(message, e);
				
				responseMessages.add(new ResponseMessage(message, ""));
				
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseMessages);
			}
		}

		message = "Please upload an excel file!";
		logger.warn(message);

		responseMessages.add(new ResponseMessage(message, ""));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessages);
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		fileName = Paths.get(".").normalize().toAbsolutePath() + fileStorageProperties.getDownloadDir() + "/" + fileName;
		Resource resource = fileStorageService.loadFileAsResource(fileName);
		logger.info("download file = " + fileName);
		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.error("Could not determine file type.", ex);
		}

		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
