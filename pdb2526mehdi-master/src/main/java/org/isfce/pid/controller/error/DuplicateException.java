package org.isfce.pid.controller.error;

import java.io.Serial;

/**
 * 
 * @author Didier Exception générée en cas de création d'un élément en double.
 *         Cette exception hérite de RuntimeException, de ce fait, on n'est pas
 *         obliger d'utiliser un try catch
 */
public class DuplicateException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Le nom du champ technique responsable du conflit (ex: "code", "email").
     */
    private final String champ;

    /**
     * Construit une nouvelle exception de doublon.
     *
     * @param msg   Le message d'erreur détaillé.
     * @param champ Le nom du champ ou de la propriété en conflit.
     */
    public DuplicateException(String msg, String champ) {
        super(msg);
        this.champ = champ;
    }

    /**
     * Récupère le nom du champ en conflit.
     *
     * @return Le nom du champ associé à l'erreur.
     */
    public String getChamp() {
        return champ;
    }

}