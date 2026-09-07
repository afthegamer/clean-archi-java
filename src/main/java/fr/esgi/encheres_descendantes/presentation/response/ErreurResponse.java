package fr.esgi.encheres_descendantes.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErreurResponse(int statut, String erreur, List<String> details, LocalDateTime instant) {

    public static ErreurResponse de(int statut, String erreur, List<String> details) {
        return new ErreurResponse(statut, erreur, details, LocalDateTime.now());
    }
}
