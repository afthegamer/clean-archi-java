package fr.esgi.encheres_descendantes.domaine.usecase;

import fr.esgi.encheres_descendantes.domaine.service.MotDePasseEncodeur;

class EncodeurDeTest implements MotDePasseEncodeur {

    static final String PREFIXE = "hache::";

    @Override
    public String encoder(String motDePasseEnClair) {
        return PREFIXE + motDePasseEnClair;
    }

    @Override
    public boolean correspond(String motDePasseEnClair, String motDePasseHache) {
        if (motDePasseEnClair == null || motDePasseHache == null) {
            return false;
        }
        return motDePasseHache.equals(PREFIXE + motDePasseEnClair);
    }
}
