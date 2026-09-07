package fr.esgi.encheres_descendantes.domaine.exception;

public class IdentifiantsInvalidesException extends DomaineException {

    public IdentifiantsInvalidesException() {
        super("Email ou mot de passe incorrect");
    }
}
