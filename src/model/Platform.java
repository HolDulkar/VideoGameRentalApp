package model;

public enum Platform {

    // Plataformes disponibles per als videojocs
    PS5("PlayStation 5"),
    SWITCH2("Nintendo Switch 2"),
    XBOX("Xbox"),
    PC("PC");

    // Nom visible de cada plataforma
    private final String displayName;

    // Constructor de l'enumeració
    Platform(String displayName) {
        this.displayName = displayName;
    }

    // Retorna el nom complet de la plataforma
    @Override
    public String toString() {
        return displayName;
    }
}