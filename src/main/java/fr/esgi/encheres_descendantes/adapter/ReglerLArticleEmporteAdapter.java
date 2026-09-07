package fr.esgi.encheres_descendantes.adapter;

import fr.esgi.encheres_descendantes.domaine.modele.Offre;
import fr.esgi.encheres_descendantes.domaine.modele.Reglement;
import fr.esgi.encheres_descendantes.domaine.repository.OffreRepository;
import fr.esgi.encheres_descendantes.domaine.repository.ReglementRepository;
import fr.esgi.encheres_descendantes.domaine.usecase.ReglerLArticleEmporteUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ReglerLArticleEmporteAdapter implements ReglerLArticleEmporteUseCase.OutputPort {

    private final OffreRepository offreRepository;
    private final ReglementRepository reglementRepository;

    @Override
    public Optional<Offre> trouverOffreParId(Long offreId) {
        return offreRepository.trouverParId(offreId);
    }

    @Override
    public boolean reglementExistePourOffre(Long offreId) {
        return reglementRepository.existePourOffre(offreId);
    }

    @Override
    public Reglement save(Reglement reglement) {
        return reglementRepository.save(reglement);
    }
}
