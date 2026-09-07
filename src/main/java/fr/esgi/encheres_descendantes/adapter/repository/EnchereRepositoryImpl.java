package fr.esgi.encheres_descendantes.adapter.repository;

import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.domaine.repository.EnchereRepository;
import fr.esgi.encheres_descendantes.persistance.entity.ArticleEntity;
import fr.esgi.encheres_descendantes.persistance.entity.EnchereEntity;
import fr.esgi.encheres_descendantes.persistance.entity.ParticipantEntity;
import fr.esgi.encheres_descendantes.persistance.mapper.ArticleMapper;
import fr.esgi.encheres_descendantes.persistance.mapper.EnchereMapper;
import fr.esgi.encheres_descendantes.persistance.repository.EnchereJpaRepository;
import fr.esgi.encheres_descendantes.persistance.repository.ParticipantJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class EnchereRepositoryImpl implements EnchereRepository {

    private final EnchereJpaRepository enchereJpaRepository;
    private final ParticipantJpaRepository participantJpaRepository;

    @Override
    @Transactional
    public Enchere save(Enchere enchere) {
        EnchereEntity entite = enchere.getId() == null
                ? creerEntite(enchere)
                : chargerEntite(enchere.getId());

        EnchereMapper.reporterSur(entite, enchere);
        return EnchereMapper.versDomaine(enchereJpaRepository.save(entite));
    }

    private EnchereEntity creerEntite(Enchere enchere) {
        EnchereEntity entite = new EnchereEntity();
        Long vendeurId = enchere.getVendeur().getId();
        ParticipantEntity vendeur = participantJpaRepository.findById(vendeurId)
                .orElseThrow(() -> new RessourceIntrouvableException("Participant", vendeurId));
        entite.setVendeur(vendeur);
        for (Article article : enchere.getArticles()) {
            ArticleEntity articleEntite = ArticleMapper.versEntite(article);
            articleEntite.setId(null);
            entite.ajouterArticle(articleEntite);
        }
        return entite;
    }

    private EnchereEntity chargerEntite(Long enchereId) {
        return enchereJpaRepository.findById(enchereId)
                .orElseThrow(() -> new RessourceIntrouvableException("Enchere", enchereId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enchere> trouverParId(Long id) {
        return enchereJpaRepository.findById(id).map(EnchereMapper::versDomaine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enchere> lister() {
        return EnchereMapper.versDomaine(enchereJpaRepository.findAll());
    }
}
