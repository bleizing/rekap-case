package com.bleizing.rekapcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bleizing.rekapcase.model.TContent;

@Repository
public interface TContentRepository extends JpaRepository<TContent, Long> {

}
