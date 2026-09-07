package fr.esgi.encheres_descendantes.presentation.response;

import fr.esgi.encheres_descendantes.domaine.modele.Offre;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OffreResponse(
        Long id,
        Long enchereId,
        ParticipantResponse participant,
        BigDecimal montant,
        LocalDateTime dateHeure) {

    public static OffreResponse de(Offre offre) {
        return new OffreResponse(
                offre.getId(),
                offre.getEnchere().getId(),
                ParticipantResponse.de(offre.getParticipant()),
                offre.getMontant(),
                offre.getDateHeure());
    }
}
