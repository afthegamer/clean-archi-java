package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.commande.SInscrireCommande;
import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EmailDejaUtiliseException;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.service.MotDePasseEncodeur;

import java.util.Objects;
import java.util.regex.Pattern;

public class SInscrireUseCase {

    private static final Pattern FORMAT_EMAIL = Pattern.compile("^[^@ ]+@[^@ ]+[.][^@ ]+$");
    private static final int LONGUEUR_MINIMALE_MOT_DE_PASSE = 8;

    public interface OutputPort {

        boolean existeParEmail(String email);

        Participant save(Participant participant);
    }

    private final OutputPort output;
    private final MotDePasseEncodeur encodeur;

    public SInscrireUseCase(OutputPort output, MotDePasseEncodeur encodeur) {
        this.output = Objects.requireNonNull(output, "output");
        this.encodeur = Objects.requireNonNull(encodeur, "encodeur");
    }

    public Participant apply(SInscrireCommande commande) {
        Objects.requireNonNull(commande, "commande");

        String email = normaliser(commande.email());
        verifierEmail(email);
        verifierMotDePasse(commande.motDePasse());

        if (output.existeParEmail(email)) {
            throw new EmailDejaUtiliseException(email);
        }

        String hache = encodeur.encoder(commande.motDePasse());
        return output.save(Participant.nouveau(email, hache));
    }

    private String normaliser(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void verifierEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DonneesInvalidesException("L'email est obligatoire");
        }
        if (!FORMAT_EMAIL.matcher(email).matches()) {
            throw new DonneesInvalidesException("Format d'email invalide : " + email);
        }
    }

    private void verifierMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.length() < LONGUEUR_MINIMALE_MOT_DE_PASSE) {
            throw new DonneesInvalidesException(
                    "Le mot de passe doit faire au moins " + LONGUEUR_MINIMALE_MOT_DE_PASSE + " caracteres");
        }
    }
}
