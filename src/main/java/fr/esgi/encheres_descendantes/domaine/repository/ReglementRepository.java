package fr.esgi.encheres_descendantes.domaine.repository;

import fr.esgi.encheres_descendantes.domaine.modele.Reglement;

public interface ReglementRepository {

    Reglement save(Reglement reglement);

    boolean existePourOffre(Long offreId);
}
