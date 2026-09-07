package fr.esgi.encheres_descendantes.persistance.repository;

import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantJpaRepository extends JpaRepository<ParticipantEntity, Long> {

    Optional<ParticipantEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
