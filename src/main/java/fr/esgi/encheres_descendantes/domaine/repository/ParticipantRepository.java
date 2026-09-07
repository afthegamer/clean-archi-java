package fr.esgi.encheres_descendantes.domaine.repository;

import fr.esgi.encheres_descendantes.domaine.modele.Participant;

import java.util.Optional;

public interface ParticipantRepository {

    Participant save(Participant participant);

    Optional<Participant> trouverParId(Long id);

    Optional<Participant> trouverParEmail(String email);

    boolean existeParEmail(String email);
}
