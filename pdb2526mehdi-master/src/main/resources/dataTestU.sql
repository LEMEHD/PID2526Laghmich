merge into TUE(CODE,ECTS,NB_PERIODES,NOM,REF,PRGM) values('IPAP',8,120,'Principes Algorithmiques et Programmation','752105U32D3',
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
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',2,'de développer au moins un programme en respectant les spécificités du langage choisi',30,'IPAP')
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',3,'de mettre en oeuvre des procédures de test',20,'IPAP');
merge into TACQUIS(FKUE,NUM,ACQUIS,POURCENTAGE,FK_UE) values ('IPAP',4,'de justifier la démarche mise en oeuvre dans l’élaboration du (ou des) programme(s)',20,'IPAP');


merge into TSECTION(CODE,NOM) VALUES ('INFO','Informatique Orientation: Développement d''applications');
merge into TSEC_UE (FKSECTION,FKUE) values ('INFO','IPAP')