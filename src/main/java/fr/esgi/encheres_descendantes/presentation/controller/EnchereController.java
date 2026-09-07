package fr.esgi.encheres_descendantes.presentation.controller;

import fr.esgi.encheres_descendantes.adapter.securite.ParticipantAuthentifie;
import fr.esgi.encheres_descendantes.domaine.commande.FaireUneOffreCommande;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.usecase.ConsulterUneEnchereUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.FaireUneOffreUseCase;
import fr.esgi.encheres_descendantes.presentation.request.FaireUneOffreRequest;
import fr.esgi.encheres_descendantes.presentation.response.EnchereResponse;
import fr.esgi.encheres_descendantes.presentation.response.OffreResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/encheres")
@AllArgsConstructor
public class EnchereController {

    private final ConsulterUneEnchereUseCase consulterUneEnchereUseCase;
    private final FaireUneOffreUseCase faireUneOffreUseCase;

    @GetMapping
    public List<EnchereResponse> lister() {
        return consulterUneEnchereUseCase.listerToutes().stream()
                .map(EnchereResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public EnchereResponse consulter(@PathVariable Long id) {
        return EnchereResponse.de(consulterUneEnchereUseCase.apply(id));
    }

    @PostMapping("/{id}/offres")
    public ResponseEntity<OffreResponse> faireUneOffre(
            @PathVariable Long id,
            @Valid @RequestBody FaireUneOffreRequest request,
            @AuthenticationPrincipal ParticipantAuthentifie appelant) {

        Offre offre = faireUneOffreUseCase.apply(
                new FaireUneOffreCommande(id, appelant.getId(), request.montant()));
        return ResponseEntity.status(HttpStatus.CREATED).body(OffreResponse.de(offre));
    }
}
