package com.example.sparrow.configservice.repository;

import com.example.sparrow.configservice.entity.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long>, JpaSpecificationExecutor<Config> {
    List<Config> findByAppId(Long appId);
    Page<Config> findByAppId(Long appId, Pageable pageable);
    Long deleteByAppId(Long appId);
}