package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;

public final class ParticipantMapper {

    private ParticipantMapper() {
    }

    public static Participant versDomaine(ParticipantEntity entite) {
        if (entite == null) {
            return null;
        }
        return new Participant(entite.getId(), entite.getEmail(), entite.getMotDePasseHache());
    }

    public static ParticipantEntity versEntite(Participant participant) {
        ParticipantEntity entite = new ParticipantEntity(participant.getEmail(), participant.getMotDePasseHache());
        entite.setId(participant.getId());
        return entite;
    }
}
