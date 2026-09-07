package fr.esgi.encheres_descendantes.presentation.response;

import fr.esgi.encheres_descendantes.domaine.modele.Reglement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReglementResponse(Long id, Long offreId, BigDecimal montant, LocalDateTime dateHeure) {

    public static ReglementResponse de(Reglement reglement) {
        return new ReglementResponse(
                reglement.getId(),
                reglement.getOffre().getId(),
                reglement.getMontant(),
                reglement.getDateHeure());
    }
}
