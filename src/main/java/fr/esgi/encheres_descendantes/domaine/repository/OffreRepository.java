package fr.esgi.encheres_descendantes.domaine.repository;

import fr.esgi.encheres_descendantes.domaine.modele.Offre;

import java.util.Optional;

public interface OffreRepository {

    Offre save(Offre offre);

    Optional<Offre> trouverParId(Long id);
}
