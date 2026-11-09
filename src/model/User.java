package model;

public class User {

    private int id;
    private String name;

    // Constructor: inicialitza un usuari amb el seu identificador i nom
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Retorna l'identificador de l'usuari
    public int getId() {
        return id;
    }

    // Retorna el nom de l'usuari
    public String getName() {
        return name;
    }

    // Permet modificar el nom de l'usuari
    public void setName(String name) {
        this.name = name;
    }

    // Retorna el nom quan l'objecte es mostra com a text
    @Override
    public String toString() {
        return name;
    }
}