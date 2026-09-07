package fr.esgi.encheres_descendantes.persistance.mapper;

import fr.esgi.encheres_descendantes.domaine.modele.Enchere;
import fr.esgi.encheres_descendantes.persistance.entity.EnchereEntity;

import java.time.Duration;
import java.util.List;

public final class EnchereMapper {

    private EnchereMapper() {
    }

    public static Enchere versDomaine(EnchereEntity entite) {
        if (entite == null) {
            return null;
        }
        return new Enchere(
                entite.getId(),
                entite.getNom(),
                entite.getDescription(),
                entite.getArticles().stream().map(ArticleMapper::versDomaine).toList(),
                ParticipantMapper.versDomaine(entite.getVendeur()),
                entite.getDateHeureDebut(),
                entite.getDateHeureFin(),
                entite.getPrixDeDepart(),
                entite.getPrixPlancher(),
                entite.getPasDecrement(),
                Duration.ofSeconds(entite.getIntervalleDecrementSecondes()),
                entite.getStatut());
    }

    public static List<Enchere> versDomaine(List<EnchereEntity> entites) {
        return entites.stream().map(EnchereMapper::versDomaine).toList();
    }

    public static void reporterSur(EnchereEntity entite, Enchere enchere) {
        entite.setNom(enchere.getNom());
        entite.setDescription(enchere.getDescription());
        entite.setDateHeureDebut(enchere.getDateHeureDebut());
        entite.setDateHeureFin(enchere.getDateHeureFin());
        entite.setPrixDeDepart(enchere.getPrixDeDepart());
        entite.setPrixPlancher(enchere.getPrixPlancher());
        entite.setPasDecrement(enchere.getPasDecrement());
        entite.setIntervalleDecrementSecondes(enchere.getIntervalleDecrement().getSeconds());
        entite.setStatut(enchere.getStatut());
    }
}
