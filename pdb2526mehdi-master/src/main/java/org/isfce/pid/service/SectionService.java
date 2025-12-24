package org.isfce.pid.service;

import java.util.List;

import org.isfce.pid.dao.ISectionDao;
import org.isfce.pid.dto.SectionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {
	
	private final ISectionDao sectionDao;
	
	/**
	 * Récupère la liste complète des sections disponibles.
	 */
	public List<SectionDto> getListeSections() {
        return sectionDao.findAllSectionDto();
    }
	
	
	
	
}
