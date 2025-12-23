package org.isfce.pid.dao;

import java.util.List;

import org.isfce.pid.dto.SectionDto;
import org.isfce.pid.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ISectionDao extends JpaRepository<Section, String> {
	
	// Construction dynamique du DTO pour éviter de charger les relations (listeUE)
    @Query("SELECT new org.isfce.pid.dto.SectionDto(s.code, s.nom) FROM TSECTION s")
    List<SectionDto> findAllSectionDto();
    
}
