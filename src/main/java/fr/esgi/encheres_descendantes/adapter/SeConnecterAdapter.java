package fr.esgi.encheres_descendantes.adapter;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.repository.ParticipantRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.SeConnecterUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SeConnecterAdapter implements SeConnecterUseCase.OutputPort {

    private final ParticipantRepository participantRepository;

    @Override
    public Optional<Participant> trouverParEmail(String email) {
        return participantRepository.trouverParEmail(email);
    }
}
