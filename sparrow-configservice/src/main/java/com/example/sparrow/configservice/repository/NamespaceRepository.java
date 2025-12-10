package com.example.sparrow.configservice.repository;

import com.example.sparrow.configservice.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NamespaceRepository extends JpaRepository<Namespace, Long> , JpaSpecificationExecutor<Namespace> {
    Optional<Namespace> findByName(String name);
}