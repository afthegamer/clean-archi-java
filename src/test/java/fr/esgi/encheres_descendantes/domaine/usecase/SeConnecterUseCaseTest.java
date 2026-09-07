package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.SeConnecterCommande;
import fr.esgi.encheres_descendantes.domaine.exception.IdentifiantsInvalidesException;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeConnecterUseCaseTest {

    private static final Participant ALICE =
            new Participant(1L, "alice@esgi.fr", EncodeurDeTest.PREFIXE + "motdepasse123");

    private SeConnecterUseCase useCase;

    @BeforeEach
    void avantChaqueTest() {
        SeConnecterUseCase.OutputPort output = email ->
                "alice@esgi.fr".equals(email) ? Optional.of(ALICE) : Optional.empty();
        useCase = new SeConnecterUseCase(output, new EncodeurDeTest());
    }

    @Test
    @DisplayName("connecte un participant avec les bons identifiants")
    void connexionReussie() {
        Participant participant = useCase.apply(new SeConnecterCommande("alice@esgi.fr", "motdepasse123"));

        assertEquals(1L, participant.getId());
        assertEquals("alice@esgi.fr", participant.getEmail());
    }

    @Test
    @DisplayName("l'email est normalise avant la recherche")
    void emailNormalise() {
        Participant participant = useCase.apply(new SeConnecterCommande("  ALICE@ESGI.fr ", "motdepasse123"));

        assertEquals(1L, participant.getId());
    }

    @Test
    @DisplayName("refuse un mot de passe incorrect")
    void mauvaisMotDePasse() {
        assertThrows(IdentifiantsInvalidesException.class, () ->
                useCase.apply(new SeConnecterCommande("alice@esgi.fr", "mauvais")));
    }

    @Test
    @DisplayName("refuse un email inconnu")
    void emailInconnu() {
        assertThrows(IdentifiantsInvalidesException.class, () ->
                useCase.apply(new SeConnecterCommande("bob@esgi.fr", "motdepasse123")));
    }

    @Test
    @DisplayName("email inconnu et mot de passe faux donnent le MEME message : pas d'enumeration de comptes")
    void memeMessageDansLesDeuxCas() {
        String messageEmailInconnu = assertThrows(IdentifiantsInvalidesException.class, () ->
                useCase.apply(new SeConnecterCommande("bob@esgi.fr", "motdepasse123"))).getMessage();
        String messageMauvaisMotDePasse = assertThrows(IdentifiantsInvalidesException.class, () ->
                useCase.apply(new SeConnecterCommande("alice@esgi.fr", "mauvais"))).getMessage();

        assertEquals(messageEmailInconnu, messageMauvaisMotDePasse);
    }
}
