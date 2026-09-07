package fr.esgi.encheres_descendantes.adapter.repository;

import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.repository.OffreRepository;
import fr.esgi.encheres_descendantes.persistance.entity.EnchereEntity;
import fr.esgi.encheres_descendantes.persistance.entity.OffreEntity;
import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;
import fr.esgi.encheres_descendantes.persistance.mapper.OffreMapper;
import fr.esgi.encheres_descendantes.persistance.repository.EnchereJpaRepository;
import fr.esgi.encheres_descendantes.persistance.repository.OffreJpaRepository;
import fr.esgi.encheres_descendantes.persistance.repository.ParticipantJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class OffreRepositoryImpl implements OffreRepository {

    private final OffreJpaRepository offreJpaRepository;
    private final EnchereJpaRepository enchereJpaRepository;
    private final ParticipantJpaRepository participantJpaRepository;

    @Override
    @Transactional
    public Offre save(Offre offre) {
        Long enchereId = offre.getEnchere().getId();
        Long participantId = offre.getParticipant().getId();

        EnchereEntity enchere = enchereJpaRepository.findById(enchereId)
                .orElseThrow(() -> new RessourceIntrouvableException("Enchere", enchereId));
        ParticipantEntity participant = participantJpaRepository.findById(participantId)
                .orElseThrow(() -> new RessourceIntrouvableException("Participant", participantId));

        OffreEntity entite = new OffreEntity();
        entite.setId(offre.getId());
        entite.setEnchere(enchere);
        entite.setParticipant(participant);
        entite.setMontant(offre.getMontant());
        entite.setDateHeure(offre.getDateHeure());

        return OffreMapper.versDomaine(offreJpaRepository.save(entite));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offre> trouverParId(Long id) {
        return offreJpaRepository.findById(id).map(OffreMapper::versDomaine);
    }
}
