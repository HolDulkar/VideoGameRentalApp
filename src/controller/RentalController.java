package controller;

import model.User;
import model.Game;
import model.Rental;
import model.Platform;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalController {

    // Llistes per emmagatzemar els usuaris, videojocs i lloguers
    private List<User> users;
    private List<Game> games;
    private List<Rental> rentals;

    // Constructor: inicialitza les llistes i carrega les dades inicials
    public RentalController() {
        users = new ArrayList<>();
        games = new ArrayList<>();
        rentals = new ArrayList<>();

        loadInitialData();
    }

    // Carrega les dades inicials necessàries per provar l'aplicació
    private void loadInitialData() {

        // Creació dels dos usuaris inicials
        User user1 = new User(1, "Joan");
        User user2 = new User(2, "Susana");

        users.add(user1);
        users.add(user2);

        // Creació dels dos videojocs inicials
        Game game1 = new Game(1, "Spider-Man 2");
        Game game2 = new Game(2, "Forza Horizon 5");

        // Assignació de l'estoc per plataforma
        game1.setStock(Platform.PS5, 5);
        game1.setStock(Platform.SWITCH2, 3);

        game2.setStock(Platform.XBOX, 4);
        game2.setStock(Platform.PC, 6);

        games.add(game1);
        games.add(game2);

        // Creació dels dos lloguers inicials
        LocalDate today = LocalDate.now();

        Rental rental1 = new Rental(
                1, user1, game1, Platform.PS5,
                today, today.plusDays(7)
        );

        Rental rental2 = new Rental(
                2, user2, game2, Platform.XBOX,
                today, today.plusDays(7)
        );

        rentals.add(rental1);
        rentals.add(rental2);

        // Descompte de l'estoc dels videojocs que ja estan llogats
        game1.decreaseStock(Platform.PS5);
        game2.decreaseStock(Platform.XBOX);
    }

    // Retorna una còpia de la llista d'usuaris
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // Retorna una còpia de la llista de videojocs
    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

    // Retorna una còpia de la llista de lloguers
    public List<Rental> getRentals() {
        return new ArrayList<>(rentals);
    }
    
    // Afegeix un usuari nou si el nom és vàlid i no existeix
    public boolean addUser(String name) {

        // Comprova que el nom comenci amb majúscula
        if (!validation.NameValidator.isValidName(name)) {
            return false;
        }

        // Evita registrar dos usuaris amb el mateix nom
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {  // Aquesta funció compara dos textes sense distinguir entre majúscules i minúscules
                return false;
            }
        }

        // Genera un identificador nou
        int newId = users.size() + 1;

        users.add(new User(newId, name));

        return true;
    }

    // Afegeix un videojoc nou amb les plataformes inicialitzades a zero
    public void addGame(String title) {

        int newId = games.size() + 1;

        Game game = new Game(newId, title);

        games.add(game);
    }

    // Registra un lloguer si hi ha estoc i l'usuari no té el mateix joc llogat
    public boolean rentGame(User user, Game game, Platform platform) {

        // Comprova que hi hagi unitats disponibles
        if (!game.hasStock(platform)) {
            return false;
        }

        // Comprova que l'usuari no tingui ja aquest joc actiu
        for (Rental rental : rentals) {

            if (rental.getUser().getId() == user.getId()
                    && rental.getGame().getId() == game.getId()
                    && !rental.isReturned()) {

                return false;
            }
        }

        // Defineix les dates del lloguer
        LocalDate today = LocalDate.now();
        LocalDate returnDate = today.plusDays(7);

        // Genera un identificador nou per al lloguer
        int newId = rentals.size() + 1;  // "sice" retorna el número d'elements d'una llista

        // Crea i guarda el nou lloguer
        Rental rental = new Rental(
                newId, user, game, platform, today, returnDate
        );

        rentals.add(rental);

        // Descompta una unitat de l'estoc
        game.decreaseStock(platform);

        return true;
    }

    // Retorna un videojoc i recupera una unitat de l'estoc
    public boolean returnRental(int rentalId) {

        for (Rental rental : rentals) {

            // Busca el lloguer pel seu identificador
            if (rental.getId() == rentalId && !rental.isReturned()) {

                // Marca el lloguer com a retornat
                rental.returnGame();

                // Recupera una unitat de l'estoc
                rental.getGame().increaseStock(rental.getPlatform());

                return true;
            }
        }

        // Retorna false si el lloguer no existeix o ja està retornat
        return false;
    }
    
    // Elimina un usuari només si no té cap lloguer actiu
    public boolean deleteUser(int userId) {

        // Recorre tots els lloguers per comprovar si l'usuari en té algun d'actiu
        for (Rental rental : rentals) {

            if (rental.getUser().getId() == userId
                    && !rental.isReturned()) {

                // No permet eliminar l'usuari si té lloguers pendents
                return false;
            }
        }

        // Busca i elimina l'usuari de la llista
        return users.removeIf(user -> user.getId() == userId);
    }

    // Elimina un videojoc només si no té cap lloguer actiu
    public boolean deleteGame(int gameId) {

        // Comprova si el videojoc està relacionat amb algun lloguer actiu
        for (Rental rental : rentals) {

            if (rental.getGame().getId() == gameId
                    && !rental.isReturned()) {

                // No permet eliminar el videojoc si està llogat
                return false;
            }
        }

        // Busca i elimina el videojoc de la llista
        return games.removeIf(game -> game.getId() == gameId);
    }
}
