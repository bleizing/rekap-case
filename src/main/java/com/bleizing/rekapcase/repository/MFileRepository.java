package com.bleizing.rekapcase.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bleizing.rekapcase.model.MFile;

@Repository
public interface MFileRepository extends JpaRepository<MFile, Long> {
	
	@Query(value = "SELECT * FROM M_FILE WHERE name = ?1", nativeQuery = true)
	MFile findByNama(String filename);
	
	@Query(value = "SELECT TOP 2 * FROM M_FILE ORDER BY created_at DESC", nativeQuery = true)
	ArrayList<MFile> getLastData();
}
