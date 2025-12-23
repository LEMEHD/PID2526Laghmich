--sections
merge into TSECTION(CODE,NOM) VALUES ('INFO','Bachelier en Informatique Orientation: Développement d''applications');
merge into TSECTION(CODE,NOM) VALUES ('COMPTA','Bachelier en Comptabilité');
merge into TSECTION(CODE,NOM) VALUES ('MARK','Bachelier en Marketing');
merge into TSECTION(CODE,NOM) VALUES ('SECR','Bachelier en Assistant de direction');

--UE et ses acquis
--IPAP
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IPAP',8,120,'PRINCIPES ALGORITHMIQUES ET PROGRAMMATION','752105U32D3',
cast('* d''identifier différents langages de programmation existants ;
 * de mettre en oeuvre une méthodologie de résolution de problème (observation,résolution, expérimentation, validation) et de la justifier en fonction de l’objectif
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
 * de traduire de manière adéquate des algorithmes en respectant les spécificités du langage utilisé (JAVA, PYTHON);
 * de documenter de manière complète et précise les programmes développés ;
 * de produire des tests pour valider les programmes développés.'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',1,'mettre en oeuvre une représentation algorithmique du problème posé',30,'IPAP');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',2,'de développer au moins un programme en respectant les spécificités du langage choisi',30,'IPAP');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',3,'de mettre en oeuvre des procédures de test',20,'IPAP');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',4,'de justifier la démarche mise en oeuvre dans l’élaboration du (ou des) programme(s)',20,'IPAP');
--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IPAP');

--IPID
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IPID',9,100,'PROJET D’INTEGRATION DE DEVELOPPEMENT','7534 35 U32 D2',
cast('* de décrire, de caractériser et de produire le cahier des charges du projet;
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
'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPID',1,'de produire et défendre un cahier des charges et son dossier technique par rapport à la proposition du chargé de cours',50,'IPID');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPID',2,'d’implémenter une base de données et l’intégrité des données',30,'IPID');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPID',3,'de déployer et de justifier le site répondant aux consignes figurant dans le cahier des charges',20,'IPID');
--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IPID');


--IBD
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IIBD',5,60,'INITIATION AUX BASES DE DONNEES','2982 21 U31 D1',
CAST('
  * de définir une base de données ;
  * de présenter les éléments essentiels d''un système de gestion de bases de données (SGBD) ;
  * de créer une table, un index en utilisant différents types de données et de formats d''affichage de
  ces données sur un système de gestion de bases de données relationnelles ;
  * d''expliciter les mécanismes relationnels et le schéma relationnel dans une base de données ;
  * d''implémenter sur des exemples pratiques le schéma relationnel ;
  * d''utiliser une clé primaire et les vues ;
  * d''introduire et d''utiliser des tables à jonctions (jointure) ;
  * d''utiliser les éléments essentiels d''un langage tel que SQL ;
  * de créer des tables à l''aide du langage choisi ;
  * d''effectuer des sélections à l''aide du langage de requête : requêtes, tri simple, tri multiple,
  élimination des doublons, requêtes avec création de champs, jointure, regroupement,…
  applications pratiques sur un système de gestion de bases de données relationnelles ;
  * d''appliquer les opérations ensemblistes en SQL,… ;
  * d''importer et d''exporter des données.
  ' AS CLOB)
);

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IIBD',1,'de développer et de gérer une base de données sur un système de gestion de bases de données
relationnelles et de manipuler des requêtes sous un langage tel que SQL,… dans des cas
simples.',60,'IIBD');
--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IIBD');

--IVTE
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IVTE',4,40,'VEILLE TECHNOLOGIQUE',' 7534 14 U32 D1 ',
cast('* d’identifier les outils et les méthodologies de la veille technologique ; 
 * de mettre en œuvre les principes-clés de la veille technologique ; 
 * de définir les principales étapes d’un processus de veille ; 
 * de mettre en place une cellule de veille ;
 * de résoudre les cas proposés et de rédiger un rapport reprenant : 
    o des critiques pertinentes,
    o des solutions alternatives négociées en mode collaboratif,
    o des améliorations nécessaires à la réussite du projet de veille.'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IVTE',1,'de présenter devant le groupe-classe et via une technique de communication appropriée un rapport 
circonstancié. ',100,'IVTE');

merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IVTE');

--ISO2
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('ISO2',5,60,'STRUCTURE DES ORDINATEURS','7551 01 U32 D4',
CAST('
  * s''approprier le sens du vocabulaire technique et l''utiliser d''une manière rigoureuse et appropriée ;
  * représenter l''information de manière numérique : systèmes décimal, binaire, hexadécimal,
    conversion de nombres, nombres entiers, nombres réels ;
  * identifier l''architecture matérielle et logicielle d''une configuration informatique donnée ;
  * identifier, expliciter le fonctionnement, différencier et choisir :
    * les éléments constitutifs du système central d''une configuration type en tenant
      compte de leur rôle, de leur fonctionnement et de leurs interactions, notamment :
      * le processeur : unité arithmétique et logique, décodage des instructions, bus
        interne, pipeline, processeurs parallèles,
      * la mémoire centrale : mémoire de programme et de données, mémoire cache,
        types de mémoires,
      * les bus : bus d''adresses, bus de données, bus de contrôle, bus interne,
      * les coupleurs : l''interface parallèle/série, le DMA, le temporisateur,
      * les coprocesseurs : mathématiques, de gestion de mémoire, graphiques, etc.,
      * les opérations de base du processeur ;
    * les périphériques courants, en tenant compte des évolution des besoins des
      utilisateurs :
      * les mémoires de masse : types, densité, formatage, temps d''accès, débit, etc.,
      * les imprimantes et traceurs,
      * les écrans, claviers, périphériques de pointage, etc.,
      * les liaisons : types de câbles, cartes et modems,
      * les différents types de réseaux informatiques ;
  * appréhender les éléments clés de sécurité d''un système informatique ainsi que le rôle de tout
    utilisateur et des informaticiens dans la chaîne de garantie de la sécurité.
  ' AS CLOB)
);

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('ISO2',1,'d''expliciter les éléments constitutifs d''une configuration donnée et son fonctionnement',30,'ISO2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('ISO2',2,'de justifier le choix de la mise en œuvre technique et pratique',30,'ISO2');
--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','ISO2');

--XICP: Information et communication professionnelles
merge into TUE(CODE, ECTS, NB_PERIODES, NOM, REF, PRGM) values('XICP',3,40,'INFORMATION ET COMMUNICATION PROFESSIONNELLES',
    '035022U32D2',
    cast('* de prendre des notes selon une combinaison de techniques appropriées ;
    * de mettre en œuvre les différentes méthodes de traitement de l''information :
    	o repérer l''agencement logique de l''argumentation ,
    	o identifier le but essentiel du message (convaincre, séduire, informer) ,
    	o en évaluer le caractère objectif ou subjectif ;
	* de restructurer ses notes ;
	* de reformuler le message en l''adaptant au destinataire dans les perspectives suivantes :
    	o communication externe (à large diffusion ou non) ,
    	o communication interne (confidentielle ou non) ,
    	o rapport d''information ou d''argumentation ,
    	o transmission de données techniques, scientifiques, statistiques ou chiffrées ,
    	o communication de dispositions réglementaires ou juridiques ,
    	o élaboration d''un travail personnel ;
	* d''utiliser l''outil de communication approprié aux circonstances de production et de réception.' as CLOB)
	);

merge into TACQUIS(FKUE, NUM, ACQUIS, POURCENTAGE, FK_UE) values
  ('XICP', 1, 'Produire un rapport de synthèse comprenant un résumé et un commentaire critique argumenté', 50, 'XICP'),
  ('XICP', 2, 'Élaborer et présenter un exposé oral simple', 50, 'XICP');
--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','XICP');


-- ISE2 — Systèmes d'exploitation

merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM)
values('ISE2',8,100,'SYSTEME D''EXPLOITATION','7552 03 U32 D4',
cast('* de mobiliser les connaissances et méthodologies pour :
* s''approprier le vocabulaire technique ;
* appliquer une démarche de résolution de problème pour assurer :
   o installation et configuration,
   o configuration de la sécurité (droits d''accès, protocoles),
   o maintenance préventive et curative ;
* participer à une configuration complète d''un système ;
* mettre en oeuvre des comportements professionnels :
   o respect des standards,
   o utilisation des outils d''administration,
   o documentation cohérente ;
* citer technologies et outils liés à la sécurité : hardening, baselining, listes blanches/noires, virtualisation, FDE, sauvegardes, etc. ;

* de mobiliser les connaissances de manière opérationnelle pour :
   o déterminer besoins et choix techniques,
   o partitionner et formater des disques,
   o installer le système de fichiers,
   o gérer la mémoire,
   o gérer les E/S,
   o gérer les processus,
   o appliquer politique de sécurité,
   o gérer comptes utilisateurs,
   o appliquer procédures de maintenance ;

* de programmer des procédures via langage de commande (shell) avec :
   o jokers, protections,
   o paramètres et variables,
   o formats d''affichage,
   o redirections, tubes,
   o structures de contrôle,
   o exécutions en tâche de fond,
   o sous-programmes et commentaires ;

* d''adapter une installation existante ;
* de remédier à des dysfonctionnements ;
* d''appliquer une procédure de restauration ;
* de communiquer à la maintenance les symptômes et interventions.' as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE)values ('ISE2',1,'rigueur et respect des spécificités du système d''exploitation',20,'ISE2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE)values ('ISE2',2,'comportements professionnels',20,'ISE2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE)values ('ISE2',3,'adéquation de la solution',20,'ISE2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE)values ('ISE2',4,'respect du temps alloué',20,'ISE2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE)values ('ISE2',5,'clarté et précision dans l''utilisation du vocabulaire technique',20,'ISE2');
--association UE Section
merge into TSEC_UE(FKSECTION,FKUE) values ('INFO','ISE2');

--INEB
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values(
  'INEB',
  5,
  80,
  'NOTIONS DE E-BUSINESS',
  '714506U32D1',
  cast('
* appréhender les concepts fondamentaux de l''e-business ;
* analyser les modèles de l''e-business et leurs implications organisationnelles, logistiques et technologiques ;
* élaborer un business plan complet pour un projet e-business ;
* comprendre les bases juridiques liées aux droits de la propriété intellectuelle dans le contexte des TIC ;
* analyser les fondements de la législation relative aux droits intellectuels ;
* analyser les potentialités des solutions e-business en termes de création de valeur pour l''entreprise ;
* identifier et comparer les modèles B2C, B2B et les plateformes d''intermédiation ;
* exploiter des concepts novateurs dans le domaine de l''e-business ;
* expliciter et appliquer les principes fondamentaux des droits applicables aux TIC ;
* analyser des situations juridiques liées aux TIC et identifier les responsabilités impliquées.
' as CLOB)
);

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values
('INEB',1,'Élaborer un business plan complet pour un projet e-business en justifiant les choix : contexte, motivations, solutions logistiques, investissements informatiques, cahier des charges, méthodologie d''implantation.',40,'INEB');

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values
('INEB',2,'Analyser une situation juridique simple liée aux TIC, identifier les règles applicables, déterminer les responsabilités juridiques et en préciser les conséquences.',40,'INEB');

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values
('INEB',3,'Démontrer une argumentation précise et pertinente et utiliser correctement le vocabulaire technique du domaine.',20,'INEB');

--association UE Section
merge into TSEC_UE(FKSECTION,FKUE) values ('INFO','INEB');


--IBR2
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IBR2',6,80,'BASES DES RESEAUX','298310U31D2',
cast('* de décrire et d''utiliser des réseaux informatiques :
	o modèle OSI,
	o réseau Internet,
	o utilité et importance des réseaux de communication au sein des entreprises,
	o principaux périphériques mis en œuvre dans un interréseaux,
	o utilité et importance des protocoles utilisés,
	o structure des équipements réseaux,
	o concepts de la pile TCP/IP,
	o nombres binaires : conversion entre bases (10, 16 et 2),
	o adresses IPv4 et masques de sous-réseau,
	o terminologie des réseaux informatiques : protocoles réseaux, LAN, WAN, MAN, réseaux spécialisés, réseaux VPN,
	o importance de la bande passante son impact sur les applications (transport de son, de vidéo, etc.),
	o différentes topologies des réseaux informatiques : en bus, en étoile, en étoile étendue, en anneau, hiérarchique, … ;
* de réaliser, de décrire et de caractériser des câbles informatiques :
	o grandeurs caractéristiques d''une sinusoïde : amplitude, période, fréquence, utilisation des décibels, …,
	o bande passante analogique et numérique,
	o câblage d''un LAN : analyse au niveau de la couche physique, médias Ethernet et utilisation des connecteurs, autres médias,…,
	o description des connections WAN (câble série, DSL, console, fibre,…) ;
	o de caractériser et d''utiliser la technologie Ethernet :
	o étude et comparaisons des technologies Ethernet (10Base T, architecture du 10Base T, 100Base FX, 1000Base T, multi-gigabits…),
	o caractéristiques de la norme Ethernet 802.3,
	o format d''une adresse MAC,
	o structure d''une trame Ethernet,
	o fonctionnement Ethernet : Média Access Control, Ethernet MAC, LLC, liaisons Half Duplex et Full Duplex, délai de propagation d''une trame Ethernet, types de collision d''une trame Ethernet, erreurs d''un trame Ethernet, auto-négociation d''une trame Ethernet, établissement d''une liaison Full et Half Duplex,
	o domaines de collision et domaines de broadcast,
	o unicast et broadcast en couche 2,
	o segmentation dans un réseau informatique,
	o évolutions possibles de l''Ethernet ;
* de décrire et d''utiliser la commutation (switching) Ethernet :
	o rôle et utilité de la micro-segmentation,
	o latence,
	o modes de commutation (store and forward, cut through, fragment free,...) ;
* de mettre en œuvre et d''utiliser des outils d''analyse du trafic et du fonctionnement de réseaux élémentaires (sniffers, icmp, traceroute …) ;
* de décrire et de caractériser le protocole TCP/IP :
	o adressage IPv4 (types d''adresses publiques et privées, notion de classes),
	o routage avec translation d''adresse (utilisation d''adresses IPv4 privées,
	o notion de mise en place de sous-réseaux,
	o ARP, DHCP et problèmes de résolution d''adresses ;
	o notions d''adressage IPv6,
* d''installer et de configurer un réseau connecté à Internet ;
* de décrire et de caractériser des réseaux interconnectés :
	o protocoles routés et protocoles routables,
	o définition et mise en œuvre de sous-réseaux,
	o tables de routage statique,
	o métriques et algorithmes de routage,
* de décrire et d''utiliser les couches transport et application :
	o sessions (établissement, maintien et fermeture),
	o principaux ports TCP et UDP,
	o principaux protocoles applicatifs (DNS, FTP, TFTP, HTTP, SMTP, SSH,…).'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IBR2',1,'de décrire les principales notions telles que le câblage, l''adressage IP, le modèle TCP/IP,…',40,'IBR2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IBR2',2,'de décrire le fonctionnement d''un commutateur Ethernet et d''un routeur IPv4',20,'IBR2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IBR2',3,'d''établir un plan d''adressage d''un réseau simple sous IPv4 avec un accès vers l''Internet',20,'IBR2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IBR2',4,'de remédier à un dysfonctionnement simple (par ex : erreur d''adressage, câble débranché, …)',20,'IBR2');

--association UE Section
merge into TSEC_UE(FKSECTION,FKUE) values ('INFO','IBR2');

--IWPB
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IWPB',3,40,'WEB: PRINCIPES DE BASE','753429U32D1',
cast('* face à une structure informatique opérationnelle connectée à Internet, disposant des logiciels
appropriés et de la documentation nécessaire, en utilisant le vocabulaire technique et
l’orthographe adéquat, ;\n
	o d’utiliser, d’installer et de configurer des navigateurs différents ;\n
	o d’utiliser les principaux services du Web ;\n
	o d’effectuer des recherches et des sélections pertinentes de l’information sur le Web en
vue :\n
	o d’acquérir et d’utiliser la terminologie de base spécifique au domaine du Web,\n
	o d’expliciter la notion d’URL (structure, principe de redirection, etc.),\n
	o d’expliquer le processus de dépôt et de réservation des noms de domaine,\n
	o d’identifier les normes de standardisation du Web (ex : Consortium du W3C),\n
	o d’identifier les méthodes de « piratage » (virus, grabbing, phishing, hacking,
etc.) ;\n
	o de créer et de structurer une page web (X) HTML en utilisant les balises spécifiques et
leurs attributs, notamment :\n
	o les balises de structuration du document (doctype, en-têtes, etc.)\n
	o les balises de structuration et de hiérarchisation des contenus,\n
	o les balises de contenus textuels (paragraphes, titres, etc.),
UE WEB :\n
	o les balises de contenus multimédia externes (images, vidéo, sons, etc.),\n
	o les balises de formulaires,\n
	o les hyperliens et objets interactifs,\n
	o etc. ;\n
	o de respecter la sémantique lors du choix des balises ;\n
	o de créer et de structurer une feuille de style CSS en déterminant les sélecteurs et en
utilisant les propriétés (spécifications) adéquates ;\n
	o de réaliser la liaison entre les feuilles de styles et les pages web en utilisant les
techniques et méthodologies les plus pertinentes ;\n
	o de réaliser, à l’aide de feuilles de style, des mises en page et des menus ;\n
	o de vérifier la compatibilité et l’apparence des pages web avec les principaux
navigateurs et sur différents médias (smartphones, tablettes, etc.) ;\n
	o de transférer et de mettre à jour les fichiers nécessaires (HTML, CSS, média, etc.) sur
un serveur ;\n
	o de recourir à bon escient à la documentation disponible.'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IWPB',1,'de réaliser des pages statiques, compatibles avec au minimum un navigateur récent du
marché',60,'IWPB');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IWPB',2,'de transférer et de tester ces pages statiques sur un serveur',40,'IWPB');

--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IWPB');

--IPW3
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values ('IPW3',10,100, 'PROJET DE DEVELOPPEMENT WEB', '753430U32D3',
cast('face a un environnement matériel ou virtuel et au logiciel adéquats et face à une structure informatique opérationnelle connectée à Internet, disposant des logiciels appropriés et de la documentation nécessaire, en utilisant le vocabulaire technique et l''orthographe adéquate, et en respectant les normes et standards en vigueur, en appliquant le responsive design, Programmation côté client
- d’identifier, dans une page web, les éléments impliquant l’usage d’un script client ;
- d’analyser un script client en termes de:
	o définition des variables et des objets,
	o structures conditionnelles et itératives,
	o fonctions et de procédures,
	o structures interactives (gestion des évènements,…),
	o etc. ;
- d’exploiter un script client dans une page web ;
- de modifier et de créer un script et de l’intégrer dans une page web ;
- de décrire et de caractériser objets, propriétés et méthodes ;
- de déterminer les événements auxquels les éléments de la page doivent réagir ;
- de documenter sous formes de commentaires, de schémas, de dessins, etc., les éléments nécessaires à la résolution d’un problème posé (structures procédurales, interactives, animations, objets, etc.) ;
- de mettre en œuvre la résolution d’un problème posé au moyen du langage client choisi ;
- d’exploiter le côté orienté objet du langage choisi :
	o les classes prédéfinies et leurs composants (window, document, cookie, etc.),
	o la définition de classes et leur instanciation,
	o etc. ;
- d’utiliser, dans le langage choisi, les variables, les structures conditionnelles, les structures itératives, les tableaux, l''affichage dans une page web, etc. ;
- d’exploiter la notion d’expression régulière (validation de formulaires, etc.) ;
- d’exploiter des données structurées en XML (Extensible Markup Langage), en JSON (JavaScript Object Notation), etc., contenues dans un fichier externe ;
- de décrire et de mettre en œuvre des technologies entrant dans le développement d’applications web dynamiques et animées tel que AJAX (Asynchronous Javascript and XML), ... ;
- de choisir et d’exploiter une bibliothèque tierce, en vue du développement de scripts spécifiques pour RIA (interfaces riches), transmissions asynchrones, ... ;
- d’identifier et de mettre en œuvre la solution cloud appropriée ;
- d’intégrer les solutions DevOps :
	o Logiciels de gestion de version (Git, …),
	o Intégration continue,
	o Déploiement continu ;
- d’identifier des erreurs de programmation au moyen d’outils ou de techniques de débogage et d’y apporter une solution pertinente ;
- d’implémenter, d’utiliser et de sécuriser une API.
Programmation côté serveur
- d’identifier différents langages utilisés pour la programmation côté serveur ;
- d’installer les services nécessaires à l’exécution de scripts côté serveur ;
- d’analyser un script serveur en termes de:
	o définition des variables,
	o structures conditionnelles et itératives,
	o fonctions et de procédures,
	o etc. ;
- d’exploiter le modèle MVC (Model View Controller) ;
- d’exploiter un script serveur dans une page web ;
- d’utiliser, dans le langage choisi, les variables de programmation et d’environnement (session, application, cookies, etc.) ;
- de transférer des données entre pages et scripts (méthodes GET et POST, etc.) ;
- de documenter sous formes de commentaires, de schémas, de dessins, etc. les éléments nécessaires à la mise en œuvre d’une application dynamique (structure procédurale,
transfert et conservation des données, interaction avec des données externes, etc.) ;
- de citer les notions, technologies et outils liés à la sécurité de l''information ainsi que les
conséquences de leurs usages (prérequis, coûts, organisation) : vulnérabilités courantes, OWASP, dépassement de tampon, injection, assainissement d''entrée, principes
fondamentaux de développement web sécurisé, WAF, surveillance ("monitoring") ... ;
- de mettre en œuvre une application web et de la tester (validation des données et validation fonctionnelle) ;
- d’exploiter le système de gestion de fichiers du serveur (se déplacer dans l’arborescence, créer et modifier un fichier, créer un dossier et en gérer les droits d’accès fonctionnels (en ce compris dans l’application) …) ;
- d’identifier et de mettre en œuvre la solution cloud appropriée ;
- d’intégrer les solutions DevOps :
	o Logiciels de gestion de version (Git, …),
	o Intégration continue,
	o Déploiement continu ;
- d’identifier des erreurs de programmation au moyen d’outils ou de techniques de débogage et d’y apporter une solution pertinente ;
- d’implémenter, d’utiliser et de sécuriser une API.
'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',1,'créer et d’exploiter des scripts clients basés sur des classes prédéfinies',10,'IPW3');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',2,'créer et d’exploiter ses propres classes',20,'IPW3');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',3,'créer et d’exploiter des scripts basés sur une bibliothèque tierce',10,'IPW3');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',4,'envoyer des informations venant du client vers le serveur et de les traiter',20,'IPW3');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',5,'générer un ensemble de pages web contenant un système de navigation et un contenu
dynamiques intégrant formulaires et résultats',20,'IPW3');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPW3',6,'implémenter, d’utiliser et de sécuriser une API',20,'IPW3');

--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IPW3');

--IPC2
merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IPC2',10,100,'PROJET D''ANALYSE ET DE CONCEPTION','7512 11 U32 D2',
cast('* de préciser les spécifications du problème proposé afin d''établir un cahier des charges ;
* de mettre en œuvre une méthodologie de résolution de problème au travers d''études de cas (observation, résolution, justification, expérimentation, validation) ;
* de recueillir, avec les différentes parties prenantes, les informations nécessaires à la compréhension du problème et de son contexte (structure de l''organisation, contraintes, etc.) en appliquant des techniques de communication adaptées ;
* de participer à l''étude, la conception, la modélisation et la validation des informations recueillies et des scénarios de solution grâce aux diagrammes standardisés et aux tables de décision ;
* de participer à l''étude, la conception, la modélisation et la validation de solutions techniques en vue de la réalisation et de l''implantation du projet (traduction en architecture logicielle selon des schémas actuels tels que Cloud, Saas, etc.), en élaborant des scénarios de tests et des contraintes de sécurité (rôles) de niveau analyse ;
* de mettre en œuvre les ressources appropriées en vue de constituer la documentation adéquate à chaque étape du projet ;
* de planifier et contrôler l''état d''avancement d''un projet grâce à une méthode de suivi de projet standardisée ;
* de recourir à bon escient à la documentation disponible.'as CLOB));

merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPC2',1,'structurer et modéliser les besoins du client selon une démarche adaptée',35,'IPC2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPC2',2,'construire et modéliser un scénario de solution',30,'IPC2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPC2',3,'traduire en architecture logicielle la solution proposée',25,'IPC2');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPC2',4,'justifier le suivi du projet',10,'IPC2');

--association UE Section
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IPC2');

