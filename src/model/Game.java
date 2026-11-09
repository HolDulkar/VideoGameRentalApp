package model;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private int id;
    private String title;

    // Emmagatzema l'estoc disponible de cada plataforma
    private Map<Platform, Integer> stock;

    // Constructor: inicialitza el videojoc i posa l'estoc de totes les plataformes a zero
    public Game(int id, String title) {
        this.id = id;
        this.title = title;
        this.stock = new HashMap<>();

        // Recorre totes les plataformes disponibles i inicialitza el seu estoc
        for (Platform platform : Platform.values()) {
            stock.put(platform, 0);
        }
    }

    // Retorna l'identificador del videojoc
    public int getId() {
        return id;
    }

    // Retorna el títol del videojoc
    public String getTitle() {
        return title;
    }

    // Retorna la quantitat d'unitats disponibles d'una plataforma concreta
    public int getStock(Platform platform) {
        return stock.get(platform);
    }

    // Estableix una nova quantitat d'estoc per a una plataforma
    public void setStock(Platform platform, int quantity) {
        stock.put(platform, quantity);
    }

    // Comprova si hi ha unitats disponibles d'una plataforma
    public boolean hasStock(Platform platform) {
        return getStock(platform) > 0;
    }

    // Redueix l'estoc en una unitat si hi ha disponibilitat
    public void decreaseStock(Platform platform) {
        if (hasStock(platform)) {
            stock.put(platform, getStock(platform) - 1);
        }
    }

    // Augmenta l'estoc en una unitat quan es retorna un videojoc
    public void increaseStock(Platform platform) {
        stock.put(platform, getStock(platform) + 1);
    }

    // Retorna el títol quan l'objecte es mostra com a text
    @Override
    public String toString() {
        return title;
    }
}