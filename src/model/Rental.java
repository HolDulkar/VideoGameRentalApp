package model;

import java.time.LocalDate;

public class Rental {

    // Identificador únic del lloguer
    private int id;

    // Usuari que ha realitzat el lloguer
    private User user;

    // Videojoc que s'ha llogat
    private Game game;

    // Plataforma del videojoc llogat
    private Platform platform;

    // Data en què s'ha realitzat el lloguer
    private LocalDate startDate;

    // Data prevista per retornar el videojoc
    private LocalDate expectedReturnDate;

    // Indica si el videojoc ja ha estat retornat
    private boolean returned;

    // Constructor: crea un lloguer amb les dades inicials
    public Rental(int id, User user, Game game, Platform platform,
                  LocalDate startDate, LocalDate expectedReturnDate) {

        this.id = id;
        this.user = user;
        this.game = game;
        this.platform = platform;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;

        // Quan es crea un lloguer, inicialment no està retornat
        this.returned = false;
    }

    // Retorna l'identificador del lloguer
    public int getId() {
        return id;
    }

    // Retorna l'usuari que ha fet el lloguer
    public User getUser() {
        return user;
    }

    // Retorna el videojoc llogat
    public Game getGame() {
        return game;
    }

    // Retorna la plataforma seleccionada
    public Platform getPlatform() {
        return platform;
    }

    // Retorna la data d'inici del lloguer
    public LocalDate getStartDate() {
        return startDate;
    }

    // Retorna la data prevista de devolució
    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    // Comprova si el videojoc ja ha estat retornat
    public boolean isReturned() {
        return returned;
    }

    // Marca el lloguer com a retornat
    public void returnGame() {
        this.returned = true;
    }

    // Comprova si el lloguer està vençut i encara no s'ha retornat
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(expectedReturnDate);
    }
}
