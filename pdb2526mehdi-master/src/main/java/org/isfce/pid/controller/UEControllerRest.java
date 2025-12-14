package org.isfce.pid.controller;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.isfce.pid.controller.error.DuplicateException;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.dto.UEDto;
import org.isfce.pid.service.UEService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api/ue/", produces = "application/json")
@Slf4j
public class UEControllerRest {

	private UEService ueService;

	private MessageSource bundle;

	public UEControllerRest(UEService ueService, MessageSource bundle) {
		this.ueService = ueService;
		this.bundle = bundle;
	}

	@GetMapping("liste")
	ResponseEntity<List<UEDto>> getListe() {

		return ResponseEntity.ok(ueService.getListeUE());
	}

	@GetMapping("/detail/{code}")
	ResponseEntity<UEFullDto> getUE(@PathVariable(name = "code") String code, Locale locale) {

		if (!ueService.existUE(code))
			throw new NoSuchElementException(bundle.getMessage("err.notFound", new String[] { code }, locale));

		return ResponseEntity.ok(ueService.getUE(code).get());
	}

	@PostMapping(path = "add", consumes = "application/json")
	ResponseEntity<UEFullDto> addUEPost(@Valid @RequestBody UEFullDto ue, Locale locale) {
		if (ueService.existUE(ue.getCode()))
			throw new DuplicateException(bundle.getMessage("err.doublon", new String[] { "UE" }, locale), ue.getCode());

		ue = ueService.addUE(ue);
		log.debug("Ajout d'une UE: " + ue);
		return new ResponseEntity<>(ue, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path = "{code}/delete")
	ResponseEntity<String> deleteUE(@PathVariable(value = "code") String code) {
		if (!ueService.existUE(code))
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
		ueService.deleteUE(code);
		return ResponseEntity.ok(code);
	}
	
	
}
/*
	
		UE creePAP() {
			String code = "7521 05 U32 D3";
			Acquis[] acquis = { new Acquis(new IdAcquis("IPAP",1),"mettre en oeuvre une représentation algorithmique du problème posé", 30),
					new Acquis(new IdAcquis("IPAP",2),"de développer au moins un programme en respectant les spécificités du langage choisi", 30),
					new Acquis(new IdAcquis("IPAP",3),"de mettre en oeuvre des procédures de test", 20),
					new Acquis(new IdAcquis("IPAP",4),"de justifier la démarche mise en oeuvre dans l’élaboration du (ou des) programme(s)", 20) };
	
			String prgm = """
					* d'identifier différents langages de programmation existants ;
					* de mettre en oeuvre une méthodologie de résolution de problème (observation,
					résolution, expérimentation, validation) et de la justifier en fonction de l’objectif
					poursuivi ;
					* de concevoir, construire et représenter des algorithmes, en utilisant :
						o les types de données élémentaires,
						o les figures algorithmiques de base (séquence, alternative et répétitive),
						o les instructions,
						o les portées des variables,
						o les fonctions et procédures,
						o la récursivité,
						o les entrées/sorties,
						o les fichiers,
						o les structures de données de base (tableaux et enregistrements) ;
					* de traduire de manière adéquate des algorithmes en respectant les spécificités du
					langage utilisé (JAVA, PYTHON);
					* de documenter de manière complète et précise les programmes développés ;

					* de produire des tests pour valider les programmes développés.
									""";
			List<Acquis> liste = new ArrayList<Acquis>(Arrays.asList(acquis));
			UE pap =UE.builder().code("IPAP").ects(8).nbPeriodes(120).nom("PRINCIPES ALGORITHMIQUES ET PROGRAMMATION")
					.prgm(prgm).ref(code).acquis(liste).build();
	
			return pap;
		}
*/