package fr.esgi.encheres_descendantes.presentation.controller;

import fr.esgi.encheres_descendantes.domaine.commande.SInscrireCommande;
import fr.esgi.encheres_descendantes.domaine.commande.SeConnecterCommande;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.usecase.SInscrireUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.SeConnecterUseCase;
import fr.esgi.encheres_descendantes.presentation.request.ConnexionRequest;
import fr.esgi.encheres_descendantes.presentation.request.InscriptionRequest;
import fr.esgi.encheres_descendantes.presentation.response.ParticipantResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participants")
@AllArgsConstructor
public class ParticipantController {

    private final SInscrireUseCase sInscrireUseCase;
    private final SeConnecterUseCase seConnecterUseCase;

    @PostMapping
    public ResponseEntity<ParticipantResponse> sInscrire(@Valid @RequestBody InscriptionRequest request) {
        Participant participant = sInscrireUseCase.apply(
                new SInscrireCommande(request.email(), request.motDePasse()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ParticipantResponse.de(participant));
    }

    @PostMapping("/connexion")
    public ResponseEntity<ParticipantResponse> seConnecter(@Valid @RequestBody ConnexionRequest request) {
        Participant participant = seConnecterUseCase.apply(
                new SeConnecterCommande(request.email(), request.motDePasse()));
        return ResponseEntity.ok(ParticipantResponse.de(participant));
    }
}
