package fr.esgi.encheres_descendantes.persistance.repository;

import fr.esgi.encheres_descendantes.persistance.entity.ReglementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglementJpaRepository extends JpaRepository<ReglementEntity, Long> {

    boolean existsByOffreId(Long offreId);
}
