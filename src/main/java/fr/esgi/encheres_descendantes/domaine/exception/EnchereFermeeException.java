package fr.esgi.encheres_descendantes.domaine.exception;

public class EnchereFermeeException extends DomaineException {

    public EnchereFermeeException(String raison) {
        super("Aucune offre possible : " + raison);
    }
}
