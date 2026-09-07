package fr.esgi.encheres_descendantes.persistance.repository;

import fr.esgi.encheres_descendantes.persistance.entity.OffreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreJpaRepository extends JpaRepository<OffreEntity, Long> {
}
