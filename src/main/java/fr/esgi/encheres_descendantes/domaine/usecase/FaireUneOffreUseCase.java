package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.FaireUneOffreCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class FaireUneOffreUseCase {

    public interface OutputPort {

        Optional<Enchere> trouverEnchereParId(Long enchereId);

        Optional<Participant> trouverParticipantParId(Long participantId);

        Offre save(Offre offre);

        Enchere enregistrerEnchere(Enchere enchere);
    }

    private final OutputPort output;
    private final Clock horloge;

    public FaireUneOffreUseCase(OutputPort output, Clock horloge) {
        this.output = Objects.requireNonNull(output, "output");
        this.horloge = Objects.requireNonNull(horloge, "horloge");
    }

    public Offre apply(FaireUneOffreCommande commande) {
        Objects.requireNonNull(commande, "commande");
        if (commande.montant() == null || commande.montant().signum() <= 0) {
            throw new DonneesInvalidesException("Le montant de l'offre doit être strictement positif");
        }

        LocalDateTime maintenant = LocalDateTime.now(horloge);

        Enchere enchere = output.trouverEnchereParId(commande.enchereId())
                .orElseThrow(() -> new RessourceIntrouvableException("Enchère", commande.enchereId()));
        Participant participant = output.trouverParticipantParId(commande.participantId())
                .orElseThrow(() -> new RessourceIntrouvableException("Participant", commande.participantId()));

        enchere.verifierOffreRecevable(participant, commande.montant(), maintenant);

        enchere.adjuger();
        output.enregistrerEnchere(enchere);

        return output.save(Offre.nouvelle(enchere, participant, commande.montant(), maintenant));
    }
}
