package com.bleizing.rekapcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bleizing.rekapcase.model.MFile;

@Repository
public interface MFileRepository extends JpaRepository<MFile, Long> {

}
