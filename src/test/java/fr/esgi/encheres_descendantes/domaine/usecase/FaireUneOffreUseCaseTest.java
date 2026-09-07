package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.FaireUneOffreCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EnchereFermeeException;
import fr.esgi.encheres_descendantes.domaine.exception.MontantInsuffisantException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaireUneOffreUseCaseTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 3, 1, 10, 3);
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final Clock horloge = Clock.fixed(MAINTENANT.atZone(ZONE).toInstant(), ZONE);

    private final Participant vendeur = new Participant(1L, "vendeur@esgi.fr", "hache");
    private final Participant acheteur = new Participant(2L, "acheteur@esgi.fr", "hache");

    private static class OutputPortDeTest implements FaireUneOffreUseCase.OutputPort {

        private final Enchere enchere;
        private final Participant participant;
        Offre offreEnregistree;
        Enchere enchereEnregistree;

        OutputPortDeTest(Enchere enchere, Participant participant) {
            this.enchere = enchere;
            this.participant = participant;
        }

        @Override
        public Optional<Enchere> trouverEnchereParId(Long enchereId) {
            return enchere != null && enchere.getId().equals(enchereId) ? Optional.of(enchere) : Optional.empty();
        }

        @Override
        public Optional<Participant> trouverParticipantParId(Long participantId) {
            return participant != null && participant.getId().equals(participantId)
                    ? Optional.of(participant) : Optional.empty();
        }

        @Override
        public Offre save(Offre offre) {
            this.offreEnregistree = offre.avecId(100L);
            return this.offreEnregistree;
        }

        @Override
        public Enchere enregistrerEnchere(Enchere enchere) {
            this.enchereEnregistree = enchere;
            return enchere;
        }
    }

    private Enchere enchere(StatutEnchere statut, LocalDateTime debut, LocalDateTime fin) {
        return new Enchere(
                10L,
                "Montre",
                "Une montre",
                List.of(Article.nouveau("Montre", "acier")),
                vendeur,
                debut,
                fin,
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                Duration.ofMinutes(1),
                statut);
    }

    private Enchere enchereOuverte() {
        return enchere(StatutEnchere.OUVERTE, MAINTENANT.minusMinutes(3), MAINTENANT.plusHours(1));
    }

    private OutputPortDeTest output;
    private FaireUneOffreUseCase useCase;

    private void avec(Enchere enchere) {
        output = new OutputPortDeTest(enchere, acheteur);
        useCase = new FaireUneOffreUseCase(output, horloge);
    }

    @BeforeEach
    void avantChaqueTest() {
        avec(enchereOuverte());
    }

    @Test
    @DisplayName("une offre au prix courant emporte l'article")
    void offreAcceptee() {
        Offre offre = useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("970.00")));

        assertNotNull(offre);
        assertEquals(100L, offre.getId());
        assertEquals(0, new BigDecimal("970.00").compareTo(offre.getMontant()));
        assertEquals(MAINTENANT, offre.getDateHeure());
        assertEquals(acheteur.getId(), offre.getParticipant().getId());
    }

    @Test
    @DisplayName("la première offre valide clôt l'enchère et l'état est persisté")
    void adjudicationPersistee() {
        useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("970.00")));

        assertNotNull(output.enchereEnregistree);
        assertTrue(output.enchereEnregistree.estAdjugee());
        assertEquals(StatutEnchere.ADJUGEE, output.enchereEnregistree.getStatut());
    }

    @Test
    @DisplayName("une offre sous le prix courant est refusée et rien n'est enregistré")
    void montantInsuffisant() {
        assertThrows(MontantInsuffisantException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("960.00"))));

        assertNull(output.offreEnregistree);
        assertNull(output.enchereEnregistree);
    }

    @Test
    @DisplayName("le vendeur ne peut pas enchérir sur sa propre enchère")
    void vendeurExclu() {
        output = new OutputPortDeTest(enchereOuverte(), vendeur);
        useCase = new FaireUneOffreUseCase(output, horloge);

        assertThrows(OperationInterditeException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 1L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("une enchère déjà adjugée refuse toute nouvelle offre")
    void dejaAdjugee() {
        avec(enchere(StatutEnchere.ADJUGEE, MAINTENANT.minusMinutes(3), MAINTENANT.plusHours(1)));

        assertThrows(EnchereFermeeException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("pas d'offre sur une enchère pas encore commencée")
    void pasCommencee() {
        avec(enchere(StatutEnchere.OUVERTE, MAINTENANT.plusHours(1), MAINTENANT.plusHours(2)));

        assertThrows(EnchereFermeeException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("1000.00"))));
    }

    @Test
    @DisplayName("pas d'offre sur une enchère terminée")
    void terminee() {
        avec(enchere(StatutEnchere.OUVERTE, MAINTENANT.minusHours(3), MAINTENANT.minusHours(1)));

        assertThrows(EnchereFermeeException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, new BigDecimal("1000.00"))));
    }

    @Test
    @DisplayName("enchère introuvable")
    void enchereIntrouvable() {
        assertThrows(RessourceIntrouvableException.class, () ->
                useCase.apply(new FaireUneOffreCommande(999L, 2L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("participant introuvable")
    void participantIntrouvable() {
        assertThrows(RessourceIntrouvableException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 999L, new BigDecimal("970.00"))));
    }

    @Test
    @DisplayName("un montant nul ou négatif est refusé avant tout accès au dépôt")
    void montantInvalide() {
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, BigDecimal.ZERO)));
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new FaireUneOffreCommande(10L, 2L, null)));

        assertFalse(output.enchereEnregistree != null);
    }
}
