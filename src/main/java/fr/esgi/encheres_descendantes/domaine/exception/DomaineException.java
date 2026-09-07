package fr.esgi.encheres_descendantes.domaine.exception;

public abstract class DomaineException extends RuntimeException {

    protected DomaineException(String message) {
        super(message);
    }
}
