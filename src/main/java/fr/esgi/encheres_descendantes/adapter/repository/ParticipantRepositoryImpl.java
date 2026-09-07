package fr.esgi.encheres_descendantes.adapter.repository;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.repository.ParticipantRepository;
import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;
import fr.esgi.encheres_descendantes.persistance.mapper.ParticipantMapper;
import fr.esgi.encheres_descendantes.persistance.repository.ParticipantJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepository {

    private final ParticipantJpaRepository participantJpaRepository;

    @Override
    public Participant save(Participant participant) {
        ParticipantEntity entite;
        if (participant.getId() == null) {
            entite = new ParticipantEntity(participant.getEmail(), participant.getMotDePasseHache());
        } else {
            entite = participantJpaRepository.findById(participant.getId())
                    .orElseGet(ParticipantEntity::new);
            entite.setEmail(participant.getEmail());
            entite.setMotDePasseHache(participant.getMotDePasseHache());
        }
        return ParticipantMapper.versDomaine(participantJpaRepository.save(entite));
    }

    @Override
    public Optional<Participant> trouverParId(Long id) {
        return participantJpaRepository.findById(id).map(ParticipantMapper::versDomaine);
    }

    @Override
    public Optional<Participant> trouverParEmail(String email) {
        return participantJpaRepository.findByEmail(email).map(ParticipantMapper::versDomaine);
    }

    @Override
    public boolean existeParEmail(String email) {
        return participantJpaRepository.existsByEmail(email);
    }
}
