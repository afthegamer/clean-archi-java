package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConsulterUneEnchereUseCase {

    public interface OutputPort {

        Optional<Enchere> trouverParId(Long enchereId);

        List<Enchere> lister();
    }

    public record Resultat(Enchere enchere, BigDecimal prixCourant, LocalDateTime instant) {
    }

    private final OutputPort output;
    private final Clock horloge;

    public ConsulterUneEnchereUseCase(OutputPort output, Clock horloge) {
        this.output = Objects.requireNonNull(output, "output");
        this.horloge = Objects.requireNonNull(horloge, "horloge");
    }

    public Resultat apply(Long enchereId) {
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        Enchere enchere = output.trouverParId(enchereId)
                .orElseThrow(() -> new RessourceIntrouvableException("Enchère", enchereId));
        return new Resultat(enchere, enchere.prixA(maintenant), maintenant);
    }

    public List<Resultat> listerToutes() {
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        return output.lister().stream()
                .map(enchere -> new Resultat(enchere, enchere.prixA(maintenant), maintenant))
                .toList();
    }
}
