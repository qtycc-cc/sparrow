package com.example.sparrow.configservice.repository;

import com.example.sparrow.configservice.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<App, Long> , JpaSpecificationExecutor<App> {
    Optional<App> findByName(String name);
}