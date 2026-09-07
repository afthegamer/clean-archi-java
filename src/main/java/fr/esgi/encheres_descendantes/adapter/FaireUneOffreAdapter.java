package fr.esgi.encheres_descendantes.adapter;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Participant;
import fr.esgi.encheres_descendantes.domaine.repository.EnchereRepository;
import fr.esgi.encheres_descendantes.domaine.repository.OffreRepository;
import fr.esgi.encheres_descendantes.domaine.repository.ParticipantRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.FaireUneOffreUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class FaireUneOffreAdapter implements FaireUneOffreUseCase.OutputPort {

    private final OffreRepository offreRepository;
    private final EnchereRepository enchereRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public Optional<Enchere> trouverEnchereParId(Long enchereId) {
        return enchereRepository.trouverParId(enchereId);
    }

    @Override
    public Optional<Participant> trouverParticipantParId(Long participantId) {
        return participantRepository.trouverParId(participantId);
    }

    @Override
    public Offre save(Offre offre) {
        return offreRepository.save(offre);
    }

    @Override
    public Enchere enregistrerEnchere(Enchere enchere) {
        return enchereRepository.save(enchere);
    }
}
