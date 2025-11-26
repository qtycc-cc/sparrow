package com.example.sparrow.configservice.repository;

import com.example.sparrow.configservice.entity.Release;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long>, JpaSpecificationExecutor<Release> {
    Page<Release> findByAppId(Long appId, Pageable pageable);
    Optional<Release> findFirstByAppIdOrderByIdDesc(Long appId);
    Optional<Release> findFirstByIdLessThanOrderByIdDesc(Long id);
    Long deleteByAppId(Long appId);
}