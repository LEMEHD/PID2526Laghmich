package org.isfce.pid.ue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.isfce.pid.dao.IAcquisDao;
import org.isfce.pid.dao.IUeDao;
import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.Acquis.IdAcquis;
import org.isfce.pid.model.UE;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

@ActiveProfiles(value = "testU")
//@Sql({ "/dataTestU.sql" })
@SpringBootTest
class TestDaoUE {

	@Autowired
	IUeDao daoUe;
	@Autowired
	IAcquisDao daoAcquis;

	@Test
	@Transactional
	void getSaveUE() {
		String code = "7534 35 U32 D2";
		Acquis[] acquis = { new Acquis(new IdAcquis("IPID", 1),
				"de produire et défendre un cahier des charges et son dossier technique par rapport à la proposition du chargé de cours",
				50),
				new Acquis(new IdAcquis("IPID", 2), "d’implémenter une base de données et l’intégrité des données", 30),
				new Acquis(new IdAcquis("IPID", 3),	"de déployer et de justifier le site répondant aux consignes figurant dans le cahier des charges",
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

		// sauvegarde
		daoUe.save(pid);
		assertEquals(3, daoAcquis.countByIdFkUE("IPID"));
		// est-il présent en BD?
		var oPID = daoUe.findById("IPID");
		assertTrue(oPID.isPresent());
		UE pid2 = oPID.get();
		assertEquals(pid, pid2);
		assertFalse(pid == pid2);
		assertEquals(3, pid2.getAcquis().size());

		// ajout d'un acquis
		Acquis newAcquis = new Acquis(new IdAcquis("IPID", 4),"Test", 5);
		pid2.getAcquis().add(newAcquis);
		pid2 = daoUe.save(pid2);
		// vérifie si Acquis est bien en BD
		assertTrue(daoAcquis.existsById(newAcquis.getId()));
		assertEquals(4, daoAcquis.countByIdFkUE("IPID"));

		// suppression d'un acquis
		pid2.getAcquis().removeIf(a -> a.getId().equals(newAcquis.getId()));
		assertEquals(3, daoAcquis.countByIdFkUE("IPID"));
		pid2 = daoUe.save(pid2);
		// vérifie que l'enfant est supprimé en BD
		assertEquals(3, daoAcquis.countByIdFkUE("IPID"));

		// test de suppression
		daoUe.delete(pid2);
		assertFalse(daoUe.findById("IPID").isPresent());
		assertEquals(0, daoAcquis.countByIdFkUE("IPID"));
	}

}
