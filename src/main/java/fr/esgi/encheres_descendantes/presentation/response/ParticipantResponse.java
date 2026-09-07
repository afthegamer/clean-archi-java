package fr.esgi.encheres_descendantes.presentation.response;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;

public record ParticipantResponse(Long id, String email) {

    public static ParticipantResponse de(Participant participant) {
        return new ParticipantResponse(participant.getId(), participant.getEmail());
    }
}
