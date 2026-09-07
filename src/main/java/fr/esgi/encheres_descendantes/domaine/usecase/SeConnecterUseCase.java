package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.SeConnecterCommande;
import fr.esgi.encheres_descendantes.domaine.exception.IdentifiantsInvalidesException;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.service.MotDePasseEncodeur;

import java.util.Objects;
import java.util.Optional;

public class SeConnecterUseCase {

    public interface OutputPort {

        Optional<Participant> trouverParEmail(String email);
    }

    private final OutputPort output;
    private final MotDePasseEncodeur encodeur;

    public SeConnecterUseCase(OutputPort output, MotDePasseEncodeur encodeur) {
        this.output = Objects.requireNonNull(output, "output");
        this.encodeur = Objects.requireNonNull(encodeur, "encodeur");
    }

    public Participant apply(SeConnecterCommande commande) {
        Objects.requireNonNull(commande, "commande");

        String email = commande.email() == null ? null : commande.email().trim().toLowerCase();
        Optional<Participant> trouve = email == null ? Optional.empty() : output.trouverParEmail(email);

        Participant participant = trouve.orElseThrow(IdentifiantsInvalidesException::new);

        if (!encodeur.correspond(commande.motDePasse(), participant.getMotDePasseHache())) {
            throw new IdentifiantsInvalidesException();
        }
        return participant;
    }
}
