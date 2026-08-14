package com.example.backend.repository;

import com.example.backend.entity.BillGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BillGroupRepository extends JpaRepository<BillGroup, UUID> {
}
