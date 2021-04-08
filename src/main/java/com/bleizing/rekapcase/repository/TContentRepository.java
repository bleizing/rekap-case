package com.bleizing.rekapcase.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bleizing.rekapcase.model.TContent;

@Repository
public interface TContentRepository extends JpaRepository<TContent, Long> {
	
	ArrayList<TContent> findByMFileId(Long mFileId);
}
