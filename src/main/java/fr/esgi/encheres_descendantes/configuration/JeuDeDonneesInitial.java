package fr.esgi.encheres_descendantes.configuration;

import fr.esgi.encheres_descendantes.domaine.commande.SInscrireCommande;
import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.modele.StatutEnchere;
import fr.esgi.encheres_descendantes.domaine.repository.EnchereRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.SInscrireUseCase;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "application.jeu-de-donnees.actif", havingValue = "true")
public class JeuDeDonneesInitial implements CommandLineRunner {

    private static final String MOT_DE_PASSE = "motdepasse123";

    private final SInscrireUseCase sInscrireUseCase;
    private final EnchereRepository enchereRepository;
    private final Clock horloge;

    @Override
    public void run(String... args) {
        if (!enchereRepository.lister().isEmpty()) {
            return;
        }

        Participant vendeur = sInscrireUseCase.apply(new SInscrireCommande("vendeur@esgi.fr", MOT_DE_PASSE));
        sInscrireUseCase.apply(new SInscrireCommande("acheteur@esgi.fr", MOT_DE_PASSE));

        LocalDateTime maintenant = LocalDateTime.now(horloge);

        enchereRepository.save(new Enchere(
                null,
                "Vente flash - montre ancienne",
                "Le prix baisse de 10 EUR toutes les 30 secondes",
                List.of(Article.nouveau("Montre mecanique", "Boitier acier, revisee")),
                vendeur,
                maintenant.minusMinutes(1),
                maintenant.plusHours(2),
                new BigDecimal("1000.00"),
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                Duration.ofSeconds(30),
                StatutEnchere.OUVERTE));

        enchereRepository.save(new Enchere(
                null,
                "Vente a venir - vase Art Deco",
                "Ouvre dans une heure",
                List.of(Article.nouveau("Vase Art Deco", "Verre presse, 1930")),
                vendeur,
                maintenant.plusHours(1),
                maintenant.plusHours(3),
                new BigDecimal("500.00"),
                new BigDecimal("100.00"),
                new BigDecimal("25.00"),
                Duration.ofMinutes(1),
                StatutEnchere.OUVERTE));
    }
}
