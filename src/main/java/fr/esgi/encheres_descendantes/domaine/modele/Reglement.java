package fr.esgi.encheres_descendantes.domaine.modele;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Reglement {

    private final Long id;
    private final Offre offre;
    private final BigDecimal montant;
    private final LocalDateTime dateHeure;

    public Reglement(Long id, Offre offre, BigDecimal montant, LocalDateTime dateHeure) {
        this.id = id;
        this.offre = Objects.requireNonNull(offre, "offre");
        this.montant = Objects.requireNonNull(montant, "montant");
        this.dateHeure = Objects.requireNonNull(dateHeure, "dateHeure");
    }

    public static Reglement nouveau(Offre offre, BigDecimal montant, LocalDateTime dateHeure) {
        return new Reglement(null, offre, montant, dateHeure);
    }
}
