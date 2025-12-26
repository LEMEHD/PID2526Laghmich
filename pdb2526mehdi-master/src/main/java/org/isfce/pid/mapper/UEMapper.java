package org.isfce.pid.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.isfce.pid.dto.AcquisDto;
import org.isfce.pid.dto.AcquisFullDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.UE;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//componentModel = "spring" permet de dire à Spring de générer un Bean pour permettre l'Autowired
//Attention l'implémentation générée sera dans un dossier qui doit être visible dans les sources du projet (STS pas automatique)
//Chemin de la classe générée: build\generated\sources\annotationProcessor\java\main\
@Mapper(componentModel = "spring")
public interface UEMapper {

	/************************* Mapping UE vers DTO ********************************************/
	/***** sans aquis  */
	// UE ==> DTO Lazy
	UEDto toUELazyDto(UE ue);

	// list<UE> ==> List Lazy Dto
	List<UEDto> toListUELazyDto(List<UE> liste);

	/***** avec aquis sans avoir la FKUE */
	@Mapping(source = "acquis", target = "acquis")
	UEFullDto toUEFullDto(UE ue);

	List<UEFullDto> toListUEFullDto(List<UE> liste);

/************************* Mapping DTO vers UE******************************************/
	/***** sans aquis  */
	@Mapping(target = "acquis", ignore = true)
	UE fromUEDto(UEDto ueDto);

	/***** avec aquis  sur base d'un full DTO*/
	@Mapping(target = "acquis", expression = "java(mapAcquisList(ueDto))")
	UE fromUEFullDto(UEFullDto ueDto);

	// Crée un acquis sur base d'un acquisFullDto sans le code de l'UE
	default Acquis toAcquis(AcquisFullDto dto, String codeUE) {
		if (dto == null)
			return null;
		return new Acquis(new Acquis.IdAcquis(codeUE, dto.getNum()), dto.getAcquis(), dto.getPourcentage());
	}

	// Map AcquisList from dto
	default List<Acquis> mapAcquisList(UEFullDto dto) {
		if (dto.getAcquis() == null)
			return List.of();
		return dto.getAcquis().stream().map(a -> toAcquis(a, dto.getCode())) // utilisation automatique du codeUE
				.collect(Collectors.toList());
	}

	/************************ Mapping Acquis  vers DTO avec FKE **************************************/
	// Acquis ==> DTO
	@Mapping(source = "id.fkUE", target = "fkUE")
	@Mapping(source = "id.num", target = "num")
	AcquisDto toAcquisDto(Acquis acquis);

	/************************ Mapping DTO avec FKE vers Acquis  **************************************/
	// DTO==>ACQUIS
	@Mapping(target = "id", expression = "java(new Acquis.IdAcquis(acquisDto.getFkUE(), acquisDto.getNum()))")
	Acquis toAcquis(AcquisDto acquisDto);
	
	/***** MAPPING des List<Acquis> ********/
	// List<DTO> ==> List<Acquis>
	/* doit définir la méthode à cause de l'expression pour construire l'id */
	default List<Acquis> toListAcquis(List<AcquisDto> dtos) {
		if (dtos == null)
			return null;
		return dtos.stream().map(this::toAcquis).collect(Collectors.toList());// pour avoir une liste modifiable
	}

	// List<Acquis> ==> List<DTO>
	List<AcquisDto> toListAcquisDto(List<Acquis> acquis);
	
	/************************ Mapping Acquis vers AcquisDto sans FK  (AcquisFullDto)  **************************************/
	@Mapping(source = "id.num", target = "num")
	@Mapping(source = "acquis", target = "acquis")
	@Mapping(source = "pourcentage", target = "pourcentage")
	AcquisFullDto toAcquisFullDto(Acquis acquis);

}
