package fr.esgi.encheres_descendantes.persistance.repository;

import fr.esgi.encheres_descendantes.persistance.entity.EnchereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnchereJpaRepository extends JpaRepository<EnchereEntity, Long> {
}
