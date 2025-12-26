package org.isfce.pid.service;

import java.util.List;
import java.util.Optional;

import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.mapper.UEMapper;
import org.isfce.pid.model.UE;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UEService {

	private final IUeDao UeDao;

	private final UEMapper mapper;

	public List<UE> getListe() {
		return UeDao.findAll();
	}

	// @Transactional
	public Optional<UEFullDto> getUE(String id) {
		var oUe = UeDao.findById(id);
		//oUe.ifPresent(ue -> ue.getAcquis());
		if (oUe.isEmpty())return Optional.empty();
		return Optional.ofNullable(mapper.toUEFullDto(UeDao.findById(id).get()));

	}
	//sauve une nouvelle UE à oartir d'un FullDTO
	public UEFullDto addUE(@Valid UEFullDto ueAcquisDto) {
		return mapper.toUEFullDto( UeDao.save(mapper.fromUEFullDto(ueAcquisDto)));
	}

	public boolean existUE(String code) {
		return UeDao.existsById(code);
	}

	public void deleteUE(String code) {
		UeDao.deleteById(code);
	}

	/**
	 * Retourne la liste des UE sans les acquis (via un DTO)
	 * @return
	 */
	public List<UEDto> getListeUE() {
		//return mapper.toListUEDto(daoUE.findAll());
		 return UeDao.findAllUE_Dto();
	}

	
}
