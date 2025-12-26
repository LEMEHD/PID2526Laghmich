package org.isfce.pid.ue;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.isfce.pid.dto.AcquisFullDto;
import org.isfce.pid.dto.UEFullDto;
import org.isfce.pid.mapper.UEMapper;
import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.Acquis.IdAcquis;
import org.isfce.pid.model.UE;
import org.isfce.pid.service.UEService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest // lance le contexte Spring
@AutoConfigureMockMvc // Crée un mock mvc
@ActiveProfiles(profiles = "testU") // active le profile "testU"
//Pas utilisé pour l'instant dans ce test car pas d'accès à la BD
//@Sql(scripts = { "/dataTestU.sql" }, config = @SqlConfig(encoding = "utf-8")
// fichier SQL avec les données pour les tests
//permet de préciser d'autres paramètres de configuration
//,config = @SqlConfig(encoding = "utf-8", transactionMode =TransactionMode.ISOLATED)
public class TestUEController {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UEMapper mapper;

	@MockBean
	private UEService ueServiceMock;

	static UE ipid;

	@BeforeEach
	void setUp() {
		// Programmation du mock
		ipid = creeIPID();

		UEFullDto ipidDto = mapper.toUEFullDto(ipid);
		when(ueServiceMock.existUE("IPID")).thenReturn(true);
		when(ueServiceMock.existUE("TEST")).thenReturn(false);
		when(ueServiceMock.getUE("IPID")).thenReturn(Optional.of(ipidDto));

		when(ueServiceMock.getListeUE()).thenReturn(List.of(mapper.toUELazyDto(ipid)));
	}

	@Test
	void testGetIPID() throws Exception {
		mockMvc.perform(get("/api/ue/detail/IPID")).andExpect(status().isOk())
				.andExpect(jsonPath("code").value(ipid.getCode())).andExpect(jsonPath("ects").value(ipid.getEcts()))
				.andExpect(jsonPath("acquis").isArray());

		verify(ueServiceMock).getUE("IPID");
		verify(ueServiceMock, times(1)).existUE("IPID");

		mockMvc.perform(get("/api/ue/detail/TEST")).andExpect(status().isNotFound());

	}

	@Test
	void testGetListUE() throws Exception {
		// appel Get
		mockMvc.perform(get("/api/ue/liste")).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].code").value("IPID"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].acquis").doesNotExist());
	}

	@Test
	void testPostIPID() throws Exception {
		String jsonITest = """
								{
				  "code": "ITEST",
				  "ref": "TTT",
				  "nom": "Cours Test",
				  "nbPeriodes": 20,
				  "ects": 4,
				  "prgm": "Contenu",
				  "acquis": [
				    {
				      "num": 1,
				      "acquis": "Aq1",
				      "pourcentage": 60
				    },
				    {
				      "num": 2,
				      "acquis": "Aq2",
				      "pourcentage": 40
				    }
				  ]
				}

								""";
		UEFullDto ueDto = new UEFullDto();
		ueDto.setCode("ITEST");
		ueDto.setRef("TTT");
		ueDto.setNom("Cours Test");
		ueDto.setNbPeriodes(20);
		ueDto.setEcts(4);
		ueDto.setPrgm("Contenu");

		AcquisFullDto aq1 = new AcquisFullDto(1, "Aq1", 60);
		AcquisFullDto aq2 = new AcquisFullDto(2, "Aq2", 40);

		ueDto.setAcquis(List.of(aq1, aq2));

		when(ueServiceMock.existUE("TEST")).thenReturn(false);
		when(ueServiceMock.addUE(ueDto)).thenReturn(ueDto);

		mockMvc.perform(post("/api/ue/add").contentType("application/json").content(jsonITest))
				.andExpect(status().isCreated()).andExpect(content().contentType("application/json"))
				// Vérifier que le code renvoyé est correct
				.andExpect(jsonPath("$.code").value("ITEST")).andExpect(jsonPath("$.acquis", Matchers.hasSize(2)))
				.andExpect(jsonPath("$.acquis[0].num").value(1)).andExpect(jsonPath("$.acquis[0].acquis").value("Aq1"))
				.andExpect(jsonPath("$.acquis[0].pourcentage").value(60))
				.andExpect(jsonPath("$.acquis[1].num").value(2)).andExpect(jsonPath("$.acquis[1].acquis").value("Aq2"))
				.andExpect(jsonPath("$.acquis[1].pourcentage").value(40));

	}

	@Test
	void testPostBadUE() throws Exception {
		// erreurs sur nbPériodes, ects,prgm, acquis[0] (num et acquis vide)
		String jsonBad = """
								{
				  "code": "ITEST",
				  "ref": "TTT",
				  "nom": "Cours Test",
				  "nbPeriodes": 0,
				  "ects": 0,
				  "acquis": [
				    {
				      "acquis": "",
				      "pourcentage": 0
				    },
				    {
				      "num": 2,
				      "acquis": "Aq2",
				      "pourcentage": 40
				    }
				  ]
				}
					""";

		mockMvc.perform(
				post("/api/ue/add").contentType("application/json").content(jsonBad).header("Accept-Language", "fr"))// pour
																														// avoir
																														// les
																														// messages
																														// en
																														// Français
				.andExpect(status().isBadRequest()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.nbPeriodes").exists())
				.andExpect(jsonPath("$.nbPeriodes", containsString("Il faut minimum")))// test partie de la chaine
				.andExpect(jsonPath("$.ects").exists()).andExpect(jsonPath("$.ects", containsString("Il faut minimum")))// test
																														// partie
																														// de
																														// la
																														// chaine
				.andExpect(jsonPath("$.prgm").exists()).andExpect(jsonPath("$.prgm").value("ne doit pas être vide"))
				.andExpect(jsonPath("$.['acquis[0].num']").value("ne doit pas être nul"))// Test des acquis
				.andExpect(jsonPath("$.['acquis[0].pourcentage']", containsString("Le pourcentage doit")))// pourcentage
																											// acquis 0
				.andExpect(jsonPath("$.['acquis[0].acquis']").value("ne doit pas être vide"));
	}

	private static UE creeIPID() {
		String code = "7534 35 U32 D2";
		Acquis[] acquis = { new Acquis(new IdAcquis("IPID", 1),
				"de produire et défendre un cahier des charges et son dossier technique par rapport à la proposition du chargé de cours",
				50),
				new Acquis(new IdAcquis("IPID", 2), "d’implémenter une base de données et l’intégrité des données", 30),
				new Acquis(new IdAcquis("IPID", 3),
						"de déployer et de justifier le site répondant aux consignes figurant dans le cahier des charges",
						20) };

		String prgm = """
				* de décrire, de caractériser et de produire le cahier des charges du projet;
				* d’identifier les acteurs (collaborateurs, prestataires de service, etc.) intervenant dans la
				  réalisation d’un projet d’intégration d’une application, de caractériser leurs rôles, leurs
				  droits et leurs responsabilités ;
				* de construire un dossier technique reprenant les différentes étapes;
				* de mettre en oeuvre le projet en développant, parmi les concepts suivants:
					o la gestion des contenus dynamiques au travers d’une interface administrateur sécurisé,
					o la pagination de l’affichage des résultats d’une requête,
					o l’intégration de services internes et tiers,
					o la gestion de sélections, de filtres et de recherches au sein de l’application,
					o la gestion de la sécurisation et des droits d’accès aux contenus (administrateur,
					  utilisateur public, utilisateur enregistré, gestionnaire, etc.),
					o l’affichage différencié des contenus (accessibilité, langue, sécurité,
					  fonctionnalités, disponibilité de l’information, etc.), en fonction des profils utilisateurs,
					o la programmation asynchrone (AJAX…),
					o l’optimisation du code, du cache et des échanges avec la base de données,
					o l’interaction avec un système de gestion de bases de données (récupérer, ajouter, modifier, supprimer des enregistrements, etc.) ;;
					o la programmation orientée objet,
					o l’exploitation d’un framework backend et d’un framework frontend (par exemple React Native),
					o etc.;
				* d’identifier des menaces et de sécuriser le site en exploitant par exemple :
				    o l’utilisation des outils spécifiques de protection et d’identification,
				    o la protection contre des injections SQL, des attaques XSS, des vols de session, par détournement de cookies, etc.,
				    o la réécriture d’url,
				    o les paramétrages et les restrictions d’accès au serveur,
				    o etc. ;
				* de gérer des erreurs de programmation au moyen d’outils ou de techniques de débogage et d’y apporter une solution pertinente ;
				* d’utiliser à bon escient la documentation disponible.
								""";
		List<Acquis> liste = new ArrayList<Acquis>(Arrays.asList(acquis));
		UE pid = UE.builder().code("IPID").ects(9).nbPeriodes(100).nom("PROJET D’INTEGRATION DE DEVELOPPEMENT")
				.prgm(prgm).ref(code).acquis(liste).build();
		return pid;
	}
}
