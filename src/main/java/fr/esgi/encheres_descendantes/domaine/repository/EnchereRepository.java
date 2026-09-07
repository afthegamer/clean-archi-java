package fr.esgi.encheres_descendantes.domaine.repository;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;

import java.util.List;
import java.util.Optional;

public interface EnchereRepository {

    Enchere save(Enchere enchere);

    Optional<Enchere> trouverParId(Long id);

    List<Enchere> lister();
}
