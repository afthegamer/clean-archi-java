package fr.esgi.encheres_descendantes.adapter.repository;

import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Reglement;
import fr.esgi.encheres_descendantes.domaine.repository.ReglementRepository;
import fr.esgi.encheres_descendantes.persistance.entity.OffreEntity;
import fr.esgi.encheres_descendantes.persistance.entity.ReglementEntity;
import fr.esgi.encheres_descendantes.persistance.mapper.ReglementMapper;
import fr.esgi.encheres_descendantes.persistance.repository.OffreJpaRepository;
import fr.esgi.encheres_descendantes.persistance.repository.ReglementJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class ReglementRepositoryImpl implements ReglementRepository {

    private final ReglementJpaRepository reglementJpaRepository;
    private final OffreJpaRepository offreJpaRepository;

    @Override
    @Transactional
    public Reglement save(Reglement reglement) {
        Long offreId = reglement.getOffre().getId();
        OffreEntity offre = offreJpaRepository.findById(offreId)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre", offreId));

        ReglementEntity entite = new ReglementEntity();
        entite.setOffre(offre);
        entite.setMontant(reglement.getMontant());
        entite.setDateHeure(reglement.getDateHeure());

        return ReglementMapper.versDomaine(reglementJpaRepository.save(entite));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePourOffre(Long offreId) {
        return reglementJpaRepository.existsByOffreId(offreId);
    }
}
