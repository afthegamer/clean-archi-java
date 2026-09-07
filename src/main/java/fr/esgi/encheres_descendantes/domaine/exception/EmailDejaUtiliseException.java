package fr.esgi.encheres_descendantes.domaine.exception;

public class EmailDejaUtiliseException extends DomaineException {

    public EmailDejaUtiliseException(String email) {
        super("Un participant est déjà inscrit avec l'email : " + email);
    }
}
