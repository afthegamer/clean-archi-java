package fr.esgi.encheres_descendantes.presentation;

import fr.esgi.encheres_descendantes.domaine.exception.DonneesInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.EmailDejaUtiliseException;
import fr.esgi.encheres_descendantes.domaine.exception.EnchereFermeeException;
import fr.esgi.encheres_descendantes.domaine.exception.IdentifiantsInvalidesException;
import fr.esgi.encheres_descendantes.domaine.exception.MontantInsuffisantException;
import fr.esgi.encheres_descendantes.domaine.exception.OperationInterditeException;
import fr.esgi.encheres_descendantes.domaine.exception.RessourceIntrouvableException;
import fr.esgi.encheres_descendantes.presentation.response.ErreurResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GestionnaireExceptions {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurResponse> introuvable(RessourceIntrouvableException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ErreurResponse> identifiants(IdentifiantsInvalidesException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(OperationInterditeException.class)
    public ResponseEntity<ErreurResponse> interdit(OperationInterditeException exception) {
        return reponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({EmailDejaUtiliseException.class, EnchereFermeeException.class})
    public ResponseEntity<ErreurResponse> conflit(RuntimeException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({DonneesInvalidesException.class, MontantInsuffisantException.class})
    public ResponseEntity<ErreurResponse> donneesInvalides(RuntimeException exception) {
        return reponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> validation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(this::decrire)
                .toList();
        return ResponseEntity.badRequest()
                .body(ErreurResponse.de(HttpStatus.BAD_REQUEST.value(), "Requete invalide", details));
    }

    private String decrire(FieldError erreur) {
        return erreur.getField() + " : " + erreur.getDefaultMessage();
    }

    private ResponseEntity<ErreurResponse> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut)
                .body(ErreurResponse.de(statut.value(), message, List.of()));
    }
}
