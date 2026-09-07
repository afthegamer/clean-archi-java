package fr.esgi.encheres_descendantes.domaine.modele;

import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EnchereFermeeException;
import fr.esgi.encheres_descendantes.domaine.exception.MontantInsuffisantException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchereTest {

    private static final LocalDateTime DEBUT = LocalDateTime.of(2026, 3, 1, 10, 0);
    private static final LocalDateTime FIN = DEBUT.plusHours(2);

    private final Participant vendeur = new Participant(1L, "vendeur@esgi.fr", "hache");
    private final Participant acheteur = new Participant(2L, "acheteur@esgi.fr", "hache");

    private Enchere enchere(StatutEnchere statut) {
        return new Enchere(
                10L,
                "Montre",
                "Une montre",
                List.of(Article.nouveau("Montre", "acier")),
                vendeur,
                DEBUT,
                FIN,
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                Duration.ofMinutes(1),
                statut);
    }

    @Nested
    @DisplayName("Décroissance du prix")
    class Prix {

        @Test
        @DisplayName("avant le début, le prix est le prix de départ")
        void avantLeDebut() {
            assertEquals(0, new BigDecimal("1000.00").compareTo(
                    enchere(StatutEnchere.OUVERTE).prixA(DEBUT.minusMinutes(5))));
        }

        @Test
        @DisplayName("à l'instant du début, aucun palier n'est franchi")
        void auDebut() {
            assertEquals(0, new BigDecimal("1000.00").compareTo(
                    enchere(StatutEnchere.OUVERTE).prixA(DEBUT)));
        }

        @Test
        @DisplayName("le prix baisse d'un pas par intervalle écoulé")
        void baisseParPalier() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            assertEquals(0, new BigDecimal("990.00").compareTo(enchere.prixA(DEBUT.plusMinutes(1))));
            assertEquals(0, new BigDecimal("970.00").compareTo(enchere.prixA(DEBUT.plusMinutes(3))));
        }

        @Test
        @DisplayName("un palier entamé ne compte pas : la baisse se fait à intervalle révolu")
        void palierIncomplet() {
            assertEquals(0, new BigDecimal("990.00").compareTo(
                    enchere(StatutEnchere.OUVERTE).prixA(DEBUT.plusSeconds(119))));
        }

        @Test
        @DisplayName("le prix ne descend jamais sous le plancher")
        void plancher() {
            assertEquals(0, new BigDecimal("200.00").compareTo(
                    enchere(StatutEnchere.OUVERTE).prixA(DEBUT.plusDays(1))));
        }
    }

    @Nested
    @DisplayName("Ouverture")
    class Ouverture {

        @Test
        void ouverteEntreDebutEtFin() {
            assertTrue(enchere(StatutEnchere.OUVERTE).estOuverteA(DEBUT.plusMinutes(30)));
        }

        @Test
        void fermeeAvantLeDebut() {
            assertFalse(enchere(StatutEnchere.OUVERTE).estOuverteA(DEBUT.minusSeconds(1)));
        }

        @Test
        void fermeeApresLaFin() {
            assertFalse(enchere(StatutEnchere.OUVERTE).estOuverteA(FIN.plusSeconds(1)));
        }

        @Test
        void fermeeSiDejaAdjugee() {
            assertFalse(enchere(StatutEnchere.ADJUGEE).estOuverteA(DEBUT.plusMinutes(30)));
        }
    }

    @Nested
    @DisplayName("Recevabilité d'une offre")
    class Recevabilite {

        private final LocalDateTime instant = DEBUT.plusMinutes(3);

        @Test
        @DisplayName("une offre au prix courant est acceptée")
        void auPrixCourant() {
            enchere(StatutEnchere.OUVERTE)
                    .verifierOffreRecevable(acheteur, new BigDecimal("970.00"), instant);
        }

        @Test
        @DisplayName("une offre au-dessus du prix courant est acceptée")
        void auDessus() {
            enchere(StatutEnchere.OUVERTE)
                    .verifierOffreRecevable(acheteur, new BigDecimal("980.00"), instant);
        }

        @Test
        @DisplayName("une offre sous le prix courant est refusée")
        void enDessous() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            assertThrows(MontantInsuffisantException.class, () ->
                    enchere.verifierOffreRecevable(acheteur, new BigDecimal("969.99"), instant));
        }

        @Test
        @DisplayName("le vendeur ne peut pas enchérir sur sa propre enchère")
        void vendeurExclu() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            assertThrows(OperationInterditeException.class, () ->
                    enchere.verifierOffreRecevable(vendeur, new BigDecimal("970.00"), instant));
        }

        @Test
        @DisplayName("une enchère déjà adjugée n'accepte plus d'offre")
        void dejaAdjugee() {
            Enchere enchere = enchere(StatutEnchere.ADJUGEE);
            assertThrows(EnchereFermeeException.class, () ->
                    enchere.verifierOffreRecevable(acheteur, new BigDecimal("970.00"), instant));
        }

        @Test
        @DisplayName("pas d'offre avant l'ouverture")
        void avantOuverture() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            assertThrows(EnchereFermeeException.class, () ->
                    enchere.verifierOffreRecevable(acheteur, new BigDecimal("1000.00"), DEBUT.minusSeconds(1)));
        }

        @Test
        @DisplayName("pas d'offre après la fin")
        void apresFin() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            assertThrows(EnchereFermeeException.class, () ->
                    enchere.verifierOffreRecevable(acheteur, new BigDecimal("1000.00"), FIN.plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("Adjudication")
    class Adjudication {

        @Test
        @DisplayName("la première offre valide clôt la vente")
        void adjuge() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            enchere.adjuger();
            assertEquals(StatutEnchere.ADJUGEE, enchere.getStatut());
            assertTrue(enchere.estAdjugee());
        }

        @Test
        @DisplayName("on ne peut pas adjuger deux fois")
        void pasDeuxFois() {
            Enchere enchere = enchere(StatutEnchere.OUVERTE);
            enchere.adjuger();
            assertThrows(EnchereFermeeException.class, enchere::adjuger);
        }
    }

    @Nested
    @DisplayName("Invariants à la construction")
    class Invariants {

        @Test
        void finAvantDebutRefusee() {
            assertThrows(DonneesInvalidesException.class, () -> new Enchere(
                    null, "x", null, List.of(), vendeur, FIN, DEBUT,
                    new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("1"),
                    Duration.ofMinutes(1), StatutEnchere.OUVERTE));
        }

        @Test
        void plancherAuDessusDuDepartRefuse() {
            assertThrows(DonneesInvalidesException.class, () -> new Enchere(
                    null, "x", null, List.of(), vendeur, DEBUT, FIN,
                    new BigDecimal("100"), new BigDecimal("500"), new BigDecimal("1"),
                    Duration.ofMinutes(1), StatutEnchere.OUVERTE));
        }

        @Test
        void intervalleNulRefuse() {
            assertThrows(DonneesInvalidesException.class, () -> new Enchere(
                    null, "x", null, List.of(), vendeur, DEBUT, FIN,
                    new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("1"),
                    Duration.ZERO, StatutEnchere.OUVERTE));
        }
    }
}
