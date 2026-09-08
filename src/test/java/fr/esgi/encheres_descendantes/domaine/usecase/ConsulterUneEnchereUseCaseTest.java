package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsulterUneEnchereUseCaseTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 3, 1, 10, 3);
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final Clock horloge = Clock.fixed(MAINTENANT.atZone(ZONE).toInstant(), ZONE);

    private final Participant vendeur = new Participant(1L, "vendeur@esgi.fr", "hache");

    private Enchere enchere(Long id, LocalDateTime debut) {
        return new Enchere(
                id,
                "Montre",
                "Une montre",
                List.of(Article.nouveau("Montre", "acier")),
                vendeur,
                debut,
                debut.plusHours(2),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                Duration.ofMinutes(1),
                StatutEnchere.OUVERTE);
    }

    private ConsulterUneEnchereUseCase useCase;

    private void avec(List<Enchere> encheres) {
        ConsulterUneEnchereUseCase.OutputPort output = new ConsulterUneEnchereUseCase.OutputPort() {

            @Override
            public Optional<Enchere> trouverParId(Long enchereId) {
                return encheres.stream().filter(e -> e.getId().equals(enchereId)).findFirst();
            }

            @Override
            public List<Enchere> lister() {
                return encheres;
            }
        };
        useCase = new ConsulterUneEnchereUseCase(output, horloge);
    }

    @BeforeEach
    void avantChaqueTest() {
        avec(List.of(enchere(10L, MAINTENANT.minusMinutes(3))));
    }

    @Test
    @DisplayName("la consultation calcule le prix courant à l'instant de l'horloge")
    void prixCourantCalcule() {
        ConsulterUneEnchereUseCase.Resultat resultat = useCase.apply(10L);

        assertEquals(10L, resultat.enchere().getId());
        assertEquals(MAINTENANT, resultat.instant());
        assertEquals(0, new BigDecimal("970.00").compareTo(resultat.prixCourant()));
    }

    @Test
    @DisplayName("une enchère pas encore ouverte est consultable au prix de départ")
    void avantOuverture() {
        avec(List.of(enchere(10L, MAINTENANT.plusHours(1))));

        ConsulterUneEnchereUseCase.Resultat resultat = useCase.apply(10L);

        assertEquals(0, new BigDecimal("1000.00").compareTo(resultat.prixCourant()));
    }

    @Test
    @DisplayName("le prix courant ne descend jamais sous le plancher")
    void plancherRespecte() {
        avec(List.of(enchere(10L, MAINTENANT.minusDays(1))));

        assertEquals(0, new BigDecimal("200.00").compareTo(useCase.apply(10L).prixCourant()));
    }

    @Test
    @DisplayName("enchère introuvable")
    void introuvable() {
        assertThrows(RessourceIntrouvableException.class, () -> useCase.apply(999L));
    }

    @Test
    @DisplayName("le listing calcule un prix courant pour chaque enchère")
    void listingComplet() {
        avec(List.of(
                enchere(10L, MAINTENANT.minusMinutes(3)),
                enchere(20L, MAINTENANT.minusMinutes(7))));

        List<ConsulterUneEnchereUseCase.Resultat> resultats = useCase.listerToutes();

        assertEquals(2, resultats.size());
        assertEquals(0, new BigDecimal("970.00").compareTo(resultats.get(0).prixCourant()));
        assertEquals(0, new BigDecimal("930.00").compareTo(resultats.get(1).prixCourant()));
        assertTrue(resultats.stream().allMatch(r -> r.instant().equals(MAINTENANT)));
    }

    @Test
    @DisplayName("le listing d'un catalogue vide ne casse pas")
    void listingVide() {
        avec(List.of());

        assertTrue(useCase.listerToutes().isEmpty());
    }
}
