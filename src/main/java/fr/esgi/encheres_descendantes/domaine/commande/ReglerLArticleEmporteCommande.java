package fr.esgi.encheres_descendantes.domaine.commande;

import java.math.BigDecimal;

public record ReglerLArticleEmporteCommande(Long offreId, Long participantId, BigDecimal montant) {
}
