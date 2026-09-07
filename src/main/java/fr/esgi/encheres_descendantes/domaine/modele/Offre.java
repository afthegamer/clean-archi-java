package fr.esgi.encheres_descendantes.domaine.modele;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Offre {

    private final Long id;
    private final Enchere enchere;
    private final Participant participant;
    private final BigDecimal montant;
    private final LocalDateTime dateHeure;

    public Offre(Long id, Enchere enchere, Participant participant, BigDecimal montant, LocalDateTime dateHeure) {
        this.id = id;
        this.enchere = Objects.requireNonNull(enchere, "enchere");
        this.participant = Objects.requireNonNull(participant, "participant");
        this.montant = Objects.requireNonNull(montant, "montant");
        this.dateHeure = Objects.requireNonNull(dateHeure, "dateHeure");
    }

    public static Offre nouvelle(Enchere enchere, Participant participant, BigDecimal montant, LocalDateTime dateHeure) {
        return new Offre(null, enchere, participant, montant, dateHeure);
    }

    public Offre avecId(Long id) {
        return new Offre(id, enchere, participant, montant, dateHeure);
    }
}
