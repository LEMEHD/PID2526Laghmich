package org.isfce.pid.controller.error;

/**
 * 
 * @author Didier Exception générée en cas de création d'un élément en double.
 *         Cette exception hérite de RuntimeException, de ce fait, on n'est pas
 *         obliger d'utiliser un try catch
 */
public class DuplicateException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String champ;

	public DuplicateException(String msg, String champ) {
		super(msg);
		this.champ = champ;
	}

	public String getChamp() {
		return champ;
	}

}
