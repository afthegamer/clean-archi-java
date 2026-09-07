package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.ReglerLArticleEmporteCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Reglement;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ReglerLArticleEmporteUseCase {

    public interface OutputPort {

        Optional<Offre> trouverOffreParId(Long offreId);

        boolean reglementExistePourOffre(Long offreId);

        Reglement save(Reglement reglement);
    }

    private final OutputPort output;
    private final Clock horloge;

    public ReglerLArticleEmporteUseCase(OutputPort output, Clock horloge) {
        this.output = Objects.requireNonNull(output, "output");
        this.horloge = Objects.requireNonNull(horloge, "horloge");
    }

    public Reglement apply(ReglerLArticleEmporteCommande commande) {
        Objects.requireNonNull(commande, "commande");

        Offre offre = output.trouverOffreParId(commande.offreId())
                .orElseThrow(() -> new RessourceIntrouvableException("Offre", commande.offreId()));

        if (!offre.getEnchere().estAdjugee()) {
            throw new OperationInterditeException(
                    "L'enchere n'est pas adjugee : il n'y a rien a regler");
        }
        if (offre.getParticipant().getId() == null
                || !offre.getParticipant().getId().equals(commande.participantId())) {
            throw new OperationInterditeException(
                    "Seul le participant qui a emporte l'article peut le regler");
        }
        if (output.reglementExistePourOffre(offre.getId())) {
            throw new OperationInterditeException("Cette offre a deja ete reglee");
        }
        if (commande.montant() == null || commande.montant().compareTo(offre.getMontant()) != 0) {
            throw new DonneesInvalidesException(
                    "Le reglement doit correspondre exactement au montant de l'offre : " + offre.getMontant());
        }

        return output.save(Reglement.nouveau(offre, commande.montant(), LocalDateTime.now(horloge)));
    }
}
