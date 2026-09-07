package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.SInscrireCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EmailDejaUtiliseException;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SInscrireUseCaseTest {

    private static class OutputPortEnMemoire implements SInscrireUseCase.OutputPort {

        private final Map<String, Participant> parEmail = new HashMap<>();
        private long sequence = 1L;

        @Override
        public boolean existeParEmail(String email) {
            return parEmail.containsKey(email);
        }

        @Override
        public Participant save(Participant participant) {
            Participant persiste = participant.avecId(sequence++);
            parEmail.put(persiste.getEmail(), persiste);
            return persiste;
        }
    }

    private OutputPortEnMemoire output;
    private SInscrireUseCase useCase;

    @BeforeEach
    void avantChaqueTest() {
        output = new OutputPortEnMemoire();
        useCase = new SInscrireUseCase(output, new EncodeurDeTest());
    }

    @Test
    @DisplayName("inscrit un participant et lui attribue un identifiant")
    void inscriptionReussie() {
        Participant participant = useCase.apply(new SInscrireCommande("alice@esgi.fr", "motdepasse123"));

        assertEquals(1L, participant.getId());
        assertEquals("alice@esgi.fr", participant.getEmail());
        assertTrue(output.existeParEmail("alice@esgi.fr"));
    }

    @Test
    @DisplayName("le mot de passe est haché, jamais stocké en clair")
    void motDePasseHache() {
        Participant participant = useCase.apply(new SInscrireCommande("alice@esgi.fr", "motdepasse123"));

        assertNotEquals("motdepasse123", participant.getMotDePasseHache());
        assertEquals(EncodeurDeTest.PREFIXE + "motdepasse123", participant.getMotDePasseHache());
    }

    @Test
    @DisplayName("l'email est normalisé en minuscules et débarrassé des espaces")
    void emailNormalise() {
        Participant participant = useCase.apply(new SInscrireCommande("  Alice@ESGI.fr  ", "motdepasse123"));

        assertEquals("alice@esgi.fr", participant.getEmail());
    }

    @Test
    @DisplayName("refuse un email déjà inscrit")
    void emailDejaPris() {
        useCase.apply(new SInscrireCommande("alice@esgi.fr", "motdepasse123"));

        assertThrows(EmailDejaUtiliseException.class, () ->
                useCase.apply(new SInscrireCommande("alice@esgi.fr", "unautremdp1")));
    }

    @Test
    @DisplayName("refuse un email au format invalide")
    void emailInvalide() {
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new SInscrireCommande("pas-un-email", "motdepasse123")));
    }

    @Test
    @DisplayName("refuse un email vide")
    void emailVide() {
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new SInscrireCommande("   ", "motdepasse123")));
    }

    @Test
    @DisplayName("refuse un mot de passe trop court")
    void motDePasseTropCourt() {
        assertThrows(DonneesInvalidesException.class, () ->
                useCase.apply(new SInscrireCommande("alice@esgi.fr", "court")));
    }
}
