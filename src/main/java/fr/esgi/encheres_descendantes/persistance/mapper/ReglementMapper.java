package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Reglement;
import fr.esgi.encheres_descendantes.persistance.entity.ReglementEntity;

public final class ReglementMapper {

    private ReglementMapper() {
    }

    public static Reglement versDomaine(ReglementEntity entite) {
        if (entite == null) {
            return null;
        }
        return new Reglement(
                entite.getId(),
                OffreMapper.versDomaine(entite.getOffre()),
                entite.getMontant(),
                entite.getDateHeure());
    }
}
