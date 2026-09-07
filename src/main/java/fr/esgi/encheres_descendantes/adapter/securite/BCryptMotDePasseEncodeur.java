package fr.esgi.encheres_descendantes.adapter.securite;

import fr.esgi.encheres_descendantes.domaine.service.MotDePasseEncodeur;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BCryptMotDePasseEncodeur implements MotDePasseEncodeur {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encoder(String motDePasseEnClair) {
        return passwordEncoder.encode(motDePasseEnClair);
    }

    @Override
    public boolean correspond(String motDePasseEnClair, String motDePasseHache) {
        if (motDePasseEnClair == null || motDePasseHache == null) {
            return false;
        }
        return passwordEncoder.matches(motDePasseEnClair, motDePasseHache);
    }
}
