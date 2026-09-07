package fr.esgi.encheres_descendantes.configuration;

import fr.esgi.encheres_descendantes.domaine.service.MotDePasseEncodeur;
import fr.esgi.encheres_descendantes.domaine.usecase.ConsulterUneEnchereUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.FaireUneOffreUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.ReglerLArticleEmporteUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.SInscrireUseCase;
import fr.esgi.encheres_descendantes.domaine.usecase.SeConnecterUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DomaineConfiguration {

    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public SInscrireUseCase sInscrireUseCase(SInscrireUseCase.OutputPort output, MotDePasseEncodeur encodeur) {
        return new SInscrireUseCase(output, encodeur);
    }

    @Bean
    public SeConnecterUseCase seConnecterUseCase(SeConnecterUseCase.OutputPort output, MotDePasseEncodeur encodeur) {
        return new SeConnecterUseCase(output, encodeur);
    }

    @Bean
    public FaireUneOffreUseCase faireUneOffreUseCase(FaireUneOffreUseCase.OutputPort output, Clock horloge) {
        return new FaireUneOffreUseCase(output, horloge);
    }

    @Bean
    public ReglerLArticleEmporteUseCase reglerLArticleEmporteUseCase(
            ReglerLArticleEmporteUseCase.OutputPort output, Clock horloge) {
        return new ReglerLArticleEmporteUseCase(output, horloge);
    }

    @Bean
    public ConsulterUneEnchereUseCase consulterUneEnchereUseCase(
            ConsulterUneEnchereUseCase.OutputPort output, Clock horloge) {
        return new ConsulterUneEnchereUseCase(output, horloge);
    }
}
