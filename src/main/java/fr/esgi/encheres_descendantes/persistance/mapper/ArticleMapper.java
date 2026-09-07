package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Article;
import fr.esgi.encheres_descendantes.persistance.entity.ArticleEntity;

public final class ArticleMapper {

    private ArticleMapper() {
    }

    public static Article versDomaine(ArticleEntity entite) {
        if (entite == null) {
            return null;
        }
        return new Article(entite.getId(), entite.getNom(), entite.getDescription());
    }

    public static ArticleEntity versEntite(Article article) {
        ArticleEntity entite = new ArticleEntity(article.getNom(), article.getDescription());
        entite.setId(article.getId());
        return entite;
    }
}
