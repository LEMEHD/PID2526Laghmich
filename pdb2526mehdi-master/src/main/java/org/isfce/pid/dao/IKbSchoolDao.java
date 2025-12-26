package org.isfce.pid.dao;

import org.isfce.pid.model.KbSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IKbSchoolDao extends JpaRepository<KbSchool, UUID> {
    // Pour retrouver une école par son code (ex: "HE2B")
    Optional<KbSchool> findByCodeIgnoreCase(String code);
}