package fr.esgi.encheres_descendantes.domaine.exception;

import java.math.BigDecimal;

public class MontantInsuffisantException extends DomaineException {

    public MontantInsuffisantException(BigDecimal montantPropose, BigDecimal prixCourant) {
        super("Offre de " + montantPropose + " refusée : le prix courant est de " + prixCourant);
    }
}
