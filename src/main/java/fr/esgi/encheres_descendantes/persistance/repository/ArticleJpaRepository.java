package fr.esgi.encheres_descendantes.persistance.repository;

import fr.esgi.encheres_descendantes.persistance.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, Long> {
}
