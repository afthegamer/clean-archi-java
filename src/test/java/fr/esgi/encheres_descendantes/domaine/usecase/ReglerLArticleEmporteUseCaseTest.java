package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.ReglerLArticleEmporteCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.modele.Reglement;
import fr.esgi.encheres_descendantes.domaine.modele.StatutEnchere;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReglerLArticleEmporteUseCaseTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 3, 1, 12, 0);
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final Clock horloge = Clock.fixed(MAINTENANT.atZone(ZONE).toInstant(), ZONE);

    private final Participant vendeur = new Participant(1L, "vendeur@esgi.fr", "hache");
    private final Participant gagnant = new Participant(2L, "gagnant@esgi.fr", "hache");

    private static class OutputPortDeTest implements ReglerLArticleEmporteUseCase.OutputPort {

        private final Offre offre;
        private final boolean dejaRegle;
        Reglement reglementEnregistre;

        OutputPortDeTest(Offre offre, boolean dejaRegle) {
            this.offre = offre;
            this.dejaRegle = dejaRegle;
        }

        @Override
        public Optional<Offre> trouverOffreParId(Long offreId) {
            return offre != null && offre.getId().equals(offreId) ? Optional.of(offre) : Optional.empty();
        }

        @Override
        public boolean reglementExistePourOffre(Long offreId) {
            return dejaRegle;
        }

        @Override
        public Reglement save(Reglement reglement) {
            this.reglementEnregistre = new Reglement(
                    500L, reglement.getOffre(), reglement.getMontant(), reglement.getDateHeure());
            return this.reglementEnregistre;
        }
    }

    private Enchere enchere(StatutEnchere statut) {
        return new Enchere(
                10L,
                "Montre",
                "Une montre",
                List.of(Article.nouveau("Montre", "acier")),
                vendeur,
                MAINTENANT.minusHours(2),
                MAINTENANT.plusHours(2),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                Duration.ofMinutes(1),
                statut);
    }

    private Offre offre(StatutEnchere statutEnchere) {
        return new Offre(100L, enchere(statutEnchere), gagnant,
                new BigDecimal("970.00"), MAINTENANT.minusMinutes(10));
    }

    private ReglerLArticleEmporteUseCase useCaseAvec(Offre offre, boolean dejaRegle) {
        return new ReglerLArticleEmporteUseCase(new OutputPortDeTest(offre, dejaRegle), horloge);
    }

    private OutputPortDeTest output;
    private ReglerLArticleEmporteUseCase useCase;

    @BeforeEach
    void avantChaqueTest() {
        output = new OutputPortDeTest(offre(StatutEnchere.ADJUGEE), false);
        useCase = new ReglerLArticleEmporteUseCase(output, horloge);
    }

    @Test
    @DisplayName("le gagnant règle son article au montant exact de son offre")
    void reglementReussi() {
        Reglement reglement = useCase.apply(
                new ReglerLArticleEmporteCommande(100L, 2L, new BigDecimal("970.00")));

        assertNotNull(reglement);
        assertEquals(500L, reglement.getId());
        assertEquals(0, new BigDecimal("970.00").compareTo(reglement.getMontant()));
        assertEquals(MAINTENANT, reglement.getDateHeure());
    }

    @Test
    @DisplayName("on ne règle pas une enchère qui n'est pas adjugée")
    void enchereNonAdjugee() {
        ReglerLArticleEmporteUseCase uc = useCaseAvec(offre(StatutEnchere.OUVERTE), false);

        assertThrows(OperationInterditeException.class, () ->
                uc.apply(new ReglerLArticleEmporteCommande(100L, 2L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("seul le participant qui a emporté l'article peut le régler")
    void mauvaisParticipant() {
        assertThrows(OperationInterditeException.class, () ->
                useCase.apply(new ReglerLArticleEmporteCommande(100L, 1L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("une offre déjà réglée ne peut pas l'être deux fois")
    void dejaRegle() {
        ReglerLArticleEmporteUseCase uc = useCaseAvec(offre(StatutEnchere.ADJUGEE), true);

        assertThrows(OperationInterditeException.class, () ->
                uc.apply(new ReglerLArticleEmporteCommande(100L, 2L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("le montant doit correspondre exactement à celui de l'offre")
    void montantDifferent() {
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new ReglerLArticleEmporteCommande(100L, 2L, new BigDecimal("500.00"))));
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new ReglerLArticleEmporteCommande(100L, 2L, null)));
    }

    @Test
    @DisplayName("offre introuvable")
    void offreIntrouvable() {
        assertThrows(RessourceIntrouvableException.class, () ->
                useCase.apply(new ReglerLArticleEmporteCommande(999L, 2L, new BigDecimal("970.00"))));
    }
}
