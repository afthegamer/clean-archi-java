package fr.esgi.encheres_descendantes.domaine.modele;

import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EnchereFermeeException;
import fr.esgi.encheres_descendantes.domaine.exception.MontantInsuffisantException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Enchere {

    private final Long id;
    private final String nom;
    private final String description;
    private final List<Article> articles;
    private final Participant vendeur;
    private final LocalDateTime dateHeureDebut;
    private final LocalDateTime dateHeureFin;
    private final BigDecimal prixDeDepart;
    private final BigDecimal prixPlancher;
    private final BigDecimal pasDecrement;
    private final Duration intervalleDecrement;

    private StatutEnchere statut;

    public Enchere(Long id,
                   String nom,
                   String description,
                   List<Article> articles,
                   Participant vendeur,
                   LocalDateTime dateHeureDebut,
                   LocalDateTime dateHeureFin,
                   BigDecimal prixDeDepart,
                   BigDecimal prixPlancher,
                   BigDecimal pasDecrement,
                   Duration intervalleDecrement,
                   StatutEnchere statut) {

        this.id = id;
        this.nom = Objects.requireNonNull(nom, "nom");
        this.description = description;
        this.articles = articles == null ? List.of() : List.copyOf(articles);
        this.vendeur = Objects.requireNonNull(vendeur, "vendeur");
        this.dateHeureDebut = Objects.requireNonNull(dateHeureDebut, "dateHeureDebut");
        this.dateHeureFin = Objects.requireNonNull(dateHeureFin, "dateHeureFin");
        this.prixDeDepart = Objects.requireNonNull(prixDeDepart, "prixDeDepart");
        this.prixPlancher = Objects.requireNonNull(prixPlancher, "prixPlancher");
        this.pasDecrement = Objects.requireNonNull(pasDecrement, "pasDecrement");
        this.intervalleDecrement = Objects.requireNonNull(intervalleDecrement, "intervalleDecrement");
        this.statut = Objects.requireNonNull(statut, "statut");

        verifierCoherence();
    }

    private void verifierCoherence() {
        if (!dateHeureFin.isAfter(dateHeureDebut)) {
            throw new DonneesInvalidesException("La date de fin doit etre posterieure a la date de debut");
        }
        if (prixDeDepart.signum() <= 0) {
            throw new DonneesInvalidesException("Le prix de depart doit etre strictement positif");
        }
        if (prixPlancher.signum() < 0) {
            throw new DonneesInvalidesException("Le prix plancher ne peut pas etre negatif");
        }
        if (prixPlancher.compareTo(prixDeDepart) > 0) {
            throw new DonneesInvalidesException("Le prix plancher ne peut pas depasser le prix de depart");
        }
        if (pasDecrement.signum() <= 0) {
            throw new DonneesInvalidesException("Le pas de decrement doit etre strictement positif");
        }
        if (intervalleDecrement.isZero() || intervalleDecrement.isNegative()) {
            throw new DonneesInvalidesException("L'intervalle de decrement doit etre strictement positif");
        }
    }

    public BigDecimal prixA(LocalDateTime instant) {
        Objects.requireNonNull(instant, "instant");
        if (instant.isBefore(dateHeureDebut)) {
            return prixDeDepart;
        }
        long secondesEcoulees = Duration.between(dateHeureDebut, instant).getSeconds();
        long paliersFranchis = secondesEcoulees / intervalleDecrement.getSeconds();
        BigDecimal baisse = pasDecrement.multiply(BigDecimal.valueOf(paliersFranchis));
        return prixDeDepart.subtract(baisse).max(prixPlancher);
    }

    public boolean estOuverteA(LocalDateTime instant) {
        return statut == StatutEnchere.OUVERTE
                && !instant.isBefore(dateHeureDebut)
                && !instant.isAfter(dateHeureFin);
    }

    public boolean estAdjugee() {
        return statut == StatutEnchere.ADJUGEE;
    }

    public void verifierOffreRecevable(Participant encherisseur, BigDecimal montant, LocalDateTime instant) {
        if (statut == StatutEnchere.ADJUGEE) {
            throw new EnchereFermeeException("l'enchere a deja ete adjugee");
        }
        if (statut == StatutEnchere.CLOTUREE) {
            throw new EnchereFermeeException("l'enchere est cloturee");
        }
        if (instant.isBefore(dateHeureDebut)) {
            throw new EnchereFermeeException("l'enchere n'a pas encore commence");
        }
        if (instant.isAfter(dateHeureFin)) {
            throw new EnchereFermeeException("l'enchere est terminee");
        }
        if (vendeur.estLeMemeQue(encherisseur)) {
            throw new OperationInterditeException("Le vendeur ne peut pas encherir sur sa propre enchere");
        }
        BigDecimal prixCourant = prixA(instant);
        if (montant.compareTo(prixCourant) < 0) {
            throw new MontantInsuffisantException(montant, prixCourant);
        }
    }

    public void adjuger() {
        if (statut != StatutEnchere.OUVERTE) {
            throw new EnchereFermeeException("l'enchere n'est plus ouverte");
        }
        this.statut = StatutEnchere.ADJUGEE;
    }

    public void cloturerSansAcheteur() {
        if (statut != StatutEnchere.OUVERTE) {
            throw new EnchereFermeeException("l'enchere n'est plus ouverte");
        }
        this.statut = StatutEnchere.CLOTUREE;
    }

    public List<Article> getArticles() {
        return Collections.unmodifiableList(articles);
    }
}
