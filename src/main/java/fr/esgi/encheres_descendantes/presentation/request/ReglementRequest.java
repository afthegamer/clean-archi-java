package fr.esgi.encheres_descendantes.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReglementRequest(

        @NotNull(message = "L'identifiant de l'offre est obligatoire")
        Long offreId,

        @NotNull(message = "Le montant est obligatoire")
        @Positive(message = "Le montant doit être strictement positif")
        BigDecimal montant) {
}
