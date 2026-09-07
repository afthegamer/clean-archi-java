package fr.esgi.encheres_descendantes.adapter;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.repository.ParticipantRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.SInscrireUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SInscrireAdapter implements SInscrireUseCase.OutputPort {

    private final ParticipantRepository participantRepository;

    @Override
    public boolean existeParEmail(String email) {
        return participantRepository.existeParEmail(email);
    }

    @Override
    public Participant save(Participant participant) {
        return participantRepository.save(participant);
    }
}
