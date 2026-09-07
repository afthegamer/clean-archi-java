package fr.esgi.encheres_descendantes.presentation.response;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.StatutEnchere;
import fr.esgi.encheres_descendantes.domaine.usecase.ConsulterUneEnchereUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EnchereResponse(
        Long id,
        String nom,
        String description,
        List<ArticleResponse> articles,
        ParticipantResponse vendeur,
        LocalDateTime dateHeureDebut,
        LocalDateTime dateHeureFin,
        BigDecimal prixDeDepart,
        BigDecimal prixPlancher,
        BigDecimal pasDecrement,
        long intervalleDecrementSecondes,
        StatutEnchere statut,
        BigDecimal prixCourant,
        LocalDateTime evalueA) {

    public static EnchereResponse de(ConsulterUneEnchereUseCase.Resultat resultat) {
        Enchere enchere = resultat.enchere();
        return new EnchereResponse(
                enchere.getId(),
                enchere.getNom(),
                enchere.getDescription(),
                enchere.getArticles().stream().map(ArticleResponse::de).toList(),
                ParticipantResponse.de(enchere.getVendeur()),
                enchere.getDateHeureDebut(),
                enchere.getDateHeureFin(),
                enchere.getPrixDeDepart(),
                enchere.getPrixPlancher(),
                enchere.getPasDecrement(),
                enchere.getIntervalleDecrement().getSeconds(),
                enchere.getStatut(),
                resultat.prixCourant(),
                resultat.instant());
    }
}
