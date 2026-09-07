package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.persistance.entity.OffreEntity;

public final class OffreMapper {

    private OffreMapper() {
    }

    public static Offre versDomaine(OffreEntity entite) {
        if (entite == null) {
            return null;
        }
        return new Offre(
                entite.getId(),
                EnchereMapper.versDomaine(entite.getEnchere()),
                ParticipantMapper.versDomaine(entite.getParticipant()),
                entite.getMontant(),
                entite.getDateHeure());
    }
}
