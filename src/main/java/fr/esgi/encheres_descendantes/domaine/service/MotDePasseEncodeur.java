package fr.esgi.encheres_descendantes.domaine.service;

public interface MotDePasseEncodeur {

    String encoder(String motDePasseEnClair);

    boolean correspond(String motDePasseEnClair, String motDePasseHache);
}
