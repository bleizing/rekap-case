package com.bleizing.rekapcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bleizing.rekapcase.model.MFile;

@Repository
public interface MFileRepository extends JpaRepository<MFile, Long> {
	
	@Query(value = "SELECT * FROM M_FILE WHERE name = ?1", nativeQuery = true)
	MFile findByNama(String filename);
}
