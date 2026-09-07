package fr.esgi.encheres_descendantes.presentation.response;

import fr.esgi.encheres_descendantes.domaine.modele.Article;

public record ArticleResponse(Long id, String nom, String description) {

    public static ArticleResponse de(Article article) {
        return new ArticleResponse(article.getId(), article.getNom(), article.getDescription());
    }
}
