package fr.esgi.encheres_descendantes.domaine.commande;

import java.math.BigDecimal;

public record FaireUneOffreCommande(Long enchereId, Long participantId, BigDecimal montant) {
}
