package fr.esgi.encheres_descendantes.domaine.exception;

public class RessourceIntrouvableException extends DomaineException {

    public RessourceIntrouvableException(String typeRessource, Long identifiant) {
        super(typeRessource + " introuvable : " + identifiant);
    }
}
