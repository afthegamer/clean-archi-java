package fr.esgi.encheres_descendantes.presentation.controller;

import fr.esgi.encheres_descendantes.adapter.securite.ParticipantAuthentifie;
import fr.esgi.encheres_descendantes.domaine.commande.ReglerLArticleEmporteCommande;
import fr.esgi.encheres_descendantes.domaine.modele.Reglement;
import fr.esgi.encheres_descendantes.domaine.usecase.ReglerLArticleEmporteUseCase;
import fr.esgi.encheres_descendantes.presentation.request.ReglementRequest;
import fr.esgi.encheres_descendantes.presentation.response.ReglementResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reglements")
@AllArgsConstructor
public class ReglementController {

    private final ReglerLArticleEmporteUseCase reglerLArticleEmporteUseCase;

    @PostMapping
    public ResponseEntity<ReglementResponse> regler(
            @Valid @RequestBody ReglementRequest request,
            @AuthenticationPrincipal ParticipantAuthentifie appelant) {

        Reglement reglement = reglerLArticleEmporteUseCase.apply(
                new ReglerLArticleEmporteCommande(request.offreId(), appelant.getId(), request.montant()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReglementResponse.de(reglement));
    }
}
