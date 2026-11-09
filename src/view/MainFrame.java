
package view;

import controller.RentalController;
import model.User;
import model.Game;
import model.Rental;
import model.Platform;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ResourceBundle;

public class MainFrame extends JFrame {

    // Controlador que gestiona les dades de l'aplicació
    private RentalController controller;

    // Fitxer que conté els textos en català
    private ResourceBundle bundle;

    // Taula on es mostren els lloguers
    private JTable rentalTable;

    // Model que gestiona les dades de la taula
    private DefaultTableModel tableModel;

    // Constructor de la finestra principal
    public MainFrame() {

        controller = new RentalController();

        // Carrega les traduccions en català
        bundle = ResourceBundle.getBundle(
                "resources.Bundle_ca",
                new java.util.Locale("ca", "ES")
        );

        // Configuració bàsica de la finestra
        setTitle(bundle.getString("app.title"));
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crea les pestanyes principals
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab(bundle.getString("tab.rentals"), createRentalPanel());
        tabs.addTab(bundle.getString("tab.users"), createUsersPanel());
        tabs.addTab(bundle.getString("tab.games"), createGamesPanel());

        add(tabs);

        // Mostra les dades inicials
        refreshRentalTable();
        refreshGameList();
    }

    // Crea el panell de gestió dels lloguers
    private JPanel createRentalPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {
            bundle.getString("column.user"),
            bundle.getString("column.game"),
            bundle.getString("column.platform"),
            bundle.getString("column.startDate"),
            bundle.getString("column.returnDate"),
            bundle.getString("column.status"),
            "ID"
        };

        // Evita que l'usuari pugui modificar les cel·les directament
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentalTable = new JTable(tableModel);

        // Amaga la columna de l'identificador del lloguer
        rentalTable.removeColumn(
                rentalTable.getColumnModel().getColumn(6)
        );

        // Aplica els colors segons l'estat del lloguer
        rentalTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                // Obté l'estat de la fila actual
                int modelRow = table.convertRowIndexToModel(row);
                String status = String.valueOf(
                        tableModel.getValueAt(modelRow, 5)
                );

                // Assigna un color segons l'estat
                if (!isSelected) {

                    if (status.equals(bundle.getString("status.returned"))) {
                        component.setBackground(new Color(190, 235, 190));
                    } else if (status.equals(bundle.getString("status.overdue"))) {
                        component.setBackground(new Color(255, 190, 190));
                    } else {
                        component.setBackground(Color.WHITE);
                    }

                }

                return component;
            }
        });

        panel.add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        // Panell amb els botons d'acció
        JPanel buttonsPanel = new JPanel();

        JButton newRentalButton = new JButton(
                bundle.getString("button.newRental")
        );

        JButton returnButton = new JButton(
                bundle.getString("button.return")
        );

        // Obre el formulari per crear un lloguer
        newRentalButton.addActionListener(e -> showNewRentalDialog());

        // Retorna el videojoc seleccionat
        returnButton.addActionListener(e -> returnSelectedRental());

        buttonsPanel.add(newRentalButton);
        buttonsPanel.add(returnButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Mostra el formulari per registrar un lloguer nou
    private void showNewRentalDialog() {

        JComboBox<User> userCombo = new JComboBox<>();
        JComboBox<Game> gameCombo = new JComboBox<>();
        JComboBox<Platform> platformCombo = new JComboBox<>();

        // Carrega els usuaris disponibles
        for (User user : controller.getUsers()) {
            userCombo.addItem(user);
        }

        // Carrega els videojocs disponibles
        for (Game game : controller.getGames()) {
            gameCombo.addItem(game);
        }

        // Actualitza les plataformes segons el videojoc seleccionat
        Runnable updatePlatforms = () -> {

            platformCombo.removeAllItems();

            Game selectedGame = (Game) gameCombo.getSelectedItem();

            if (selectedGame != null) {

                for (Platform platform : Platform.values()) {

                    if (selectedGame.hasStock(platform)) {
                        platformCombo.addItem(platform);
                    }
                }
            }
        };

        gameCombo.addActionListener(e -> updatePlatforms.run());

        updatePlatforms.run();

        // Organitza els camps del formulari
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Usuari:"));
        form.add(userCombo);

        form.add(new JLabel("Videojoc:"));
        form.add(gameCombo);

        form.add(new JLabel("Plataforma:"));
        form.add(platformCombo);

        form.add(new JLabel("Durada:"));
        form.add(new JLabel("7 dies"));

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Nou lloguer",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            User user = (User) userCombo.getSelectedItem();
            Game game = (Game) gameCombo.getSelectedItem();
            Platform platform = (Platform) platformCombo.getSelectedItem();

            // Comprova que tots els camps tinguin una selecció
            if (user == null || game == null || platform == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "No hi ha cap plataforma disponible per a aquest videojoc."
                );

                return;
            }

            // Intenta registrar el lloguer
            boolean success = controller.rentGame(user, game, platform);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        bundle.getString("message.success")
                );

                refreshRentalTable();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No s'ha pogut crear el lloguer. Comprova l'estoc i els lloguers actius."
                );
            }
        }
    }

    // Retorna el videojoc del lloguer seleccionat a la taula
    private void returnSelectedRental() {

        int selectedRow = rentalTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un lloguer de la taula."
            );

            return;
        }

        // Converteix la fila visible a la fila del model
        int modelRow = rentalTable.convertRowIndexToModel(selectedRow);

        // Recupera l'identificador ocult del lloguer
        int rentalId = (int) tableModel.getValueAt(modelRow, 6);

        // Intenta retornar el videojoc
        boolean success = controller.returnRental(rentalId);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Videojoc retornat correctament."
            );

            refreshRentalTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Aquest lloguer ja ha estat retornat."
            );
        }
    }

   
    // Crea el panell de gestió dels usuaris
    private JPanel createUsersPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Inicialitza el model i la llista d'usuaris
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        // Carrega els usuaris existents
        refreshUserList();

        panel.add(new JScrollPane(userList), BorderLayout.CENTER);

        // Crea els botons d'acció
        JPanel buttonsPanel = new JPanel();

        JButton addButton = new JButton("Afegir usuari");
        JButton deleteButton = new JButton("Eliminar usuari");

        // Accions dels botons
        addButton.addActionListener(e -> addUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Actualitza la llista visual dels usuaris
    private void refreshUserList() {

        userListModel.clear();

        for (User user : controller.getUsers()) {
            userListModel.addElement(user);
        }
    }

    // Mostra el formulari per afegir un usuari
    private void addUserDialog() {

        String name = JOptionPane.showInputDialog(
                this,
                "Introdueix el nom de l'usuari:",
                "Nou usuari",
                JOptionPane.PLAIN_MESSAGE
        );

        // Comprova si s'ha cancel·lat el formulari
        if (name == null) {
            return;
        }

        name = name.trim();

        // Comprova el nom mitjançant la llibreria ValidationAPI
        if (!validation.NameValidator.isValidName(name)) {

            JOptionPane.showMessageDialog(
                    this,
                    validation.NameValidator.getErrorMessage(),
                    "Error de validació",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Intenta afegir l'usuari
        boolean success = controller.addUser(name);

        if (success) {

            refreshUserList();

            JOptionPane.showMessageDialog(
                    this,
                    "Usuari afegit correctament."
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Aquest usuari ja existeix.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // Elimina l'usuari seleccionat si no té lloguers actius
    private void deleteSelectedUser() {

        User selectedUser = userList.getSelectedValue();

        // Comprova que s'hagi seleccionat un usuari
        if (selectedUser == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un usuari de la llista."
            );

            return;
        }

        // Demana confirmació abans d'eliminar
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Vols eliminar l'usuari " + selectedUser.getName() + "?",
                "Confirmar eliminació",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Intenta eliminar l'usuari mitjançant el controlador
        boolean success = controller.deleteUser(selectedUser.getId());

        if (success) {

            refreshUserList();

            JOptionPane.showMessageDialog(
                    this,
                    "Usuari eliminat correctament."
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No es pot eliminar perquè té lloguers actius.",
                    "Operació no permesa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    
    // Crea el panell de gestió dels videojocs
    private JPanel createGamesPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        // Inicialitza el model i la llista de videojocs
        gameListModel = new DefaultListModel<>();
        gameList = new JList<>(gameListModel);
        
        // Mostra el títol i l'estoc de cada plataforma
        gameList.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Game) {

                    Game game = (Game) value;

                    // Construeix el text amb les existències
                    String stockText = "<html><b>" + game.getTitle()
                            + "</b><br>"
                            + "PS5: " + game.getStock(Platform.PS5)
                            + " | Switch 2: " + game.getStock(Platform.SWITCH2)
                            + " | Xbox: " + game.getStock(Platform.XBOX)
                            + " | PC: " + game.getStock(Platform.PC)
                            + "</html>";

                    setText(stockText);
                }

                return this;
            }
        });

        // Augmenta l'espai entre els videojocs de la llista
        gameList.setFixedCellHeight(55);

        // Carrega els videojocs existents
        refreshGameList();

        panel.add(new JScrollPane(gameList), BorderLayout.CENTER);

        // Crea els botons d'acció
        JPanel buttonsPanel = new JPanel();

        JButton addButton = new JButton("Afegir videojoc");
        JButton deleteButton = new JButton("Eliminar videojoc");

        // Assigna les accions als botons
        addButton.addActionListener(e -> addGameDialog());
        deleteButton.addActionListener(e -> deleteSelectedGame());

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Actualitza la llista visual dels videojocs
    private void refreshGameList() {

        gameListModel.clear();

        for (Game game : controller.getGames()) {
            gameListModel.addElement(game);
        }
    }

    // Mostra el formulari per afegir un videojoc
    private void addGameDialog() {

        JTextField titleField = new JTextField(15);

        // Camps per indicar l'estoc de cada plataforma
        JTextField ps5Field = new JTextField("0", 5);
        JTextField switchField = new JTextField("0", 5);
        JTextField xboxField = new JTextField("0", 5);
        JTextField pcField = new JTextField("0", 5);

        // Organitza els camps del formulari
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        form.add(new JLabel("Títol del videojoc:"));
        form.add(titleField);

        form.add(new JLabel("Estoc PS5:"));
        form.add(ps5Field);

        form.add(new JLabel("Estoc Nintendo Switch 2:"));
        form.add(switchField);

        form.add(new JLabel("Estoc Xbox:"));
        form.add(xboxField);

        form.add(new JLabel("Estoc PC:"));
        form.add(pcField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Nou videojoc",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Comprova si s'ha cancel·lat el formulari
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();

        // Comprova que el títol no estigui buit
        if (title.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El títol no pot estar buit."
            );

            return;
        }

        // Evita registrar videojocs amb el mateix títol
        for (Game existingGame : controller.getGames()) {

            if (existingGame.getTitle().equalsIgnoreCase(title)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Aquest videojoc ja existeix."
                );

                return;
            }
        }

        try {

            // Converteix les quantitats introduïdes a nombres enters
            int ps5 = Integer.parseInt(ps5Field.getText().trim());
            int switch2 = Integer.parseInt(switchField.getText().trim());
            int xbox = Integer.parseInt(xboxField.getText().trim());
            int pc = Integer.parseInt(pcField.getText().trim());

            // Comprova que les quantitats no siguin negatives
            if (ps5 < 0 || switch2 < 0 || xbox < 0 || pc < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "L'estoc no pot ser negatiu."
                );

                return;
            }

            // Crea el videojoc mitjançant el controlador
            controller.addGame(title);

            // Recupera el videojoc acabat de crear
            java.util.List<Game> games = controller.getGames();
            Game newGame = games.get(games.size() - 1);

            // Assigna l'estoc de cada plataforma
            newGame.setStock(Platform.PS5, ps5);
            newGame.setStock(Platform.SWITCH2, switch2);
            newGame.setStock(Platform.XBOX, xbox);
            newGame.setStock(Platform.PC, pc);

            // Actualitza la llista visual
            refreshGameList();

            JOptionPane.showMessageDialog(
                    this,
                    "Videojoc afegit correctament."
            );

        } catch (NumberFormatException ex) {

            // Informa si alguna quantitat no és un nombre vàlid
            JOptionPane.showMessageDialog(
                    this,
                    "Introdueix nombres enters vàlids per a l'estoc.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // Elimina el videojoc seleccionat si no té lloguers actius
    private void deleteSelectedGame() {

        Game selectedGame = gameList.getSelectedValue();

        // Comprova que s'hagi seleccionat un videojoc
        if (selectedGame == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un videojoc de la llista."
            );

            return;
        }

        // Demana confirmació abans d'eliminar
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Vols eliminar " + selectedGame.getTitle() + "?",
                "Confirmar eliminació",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Intenta eliminar el videojoc mitjançant el controlador
        boolean success = controller.deleteGame(selectedGame.getId());

        if (success) {

            refreshGameList();

            JOptionPane.showMessageDialog(
                    this,
                    "Videojoc eliminat correctament."
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No es pot eliminar perquè té lloguers actius.",
                    "Operació no permesa",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    // Actualitza la taula amb els lloguers actuals
    private void refreshRentalTable() {

        tableModel.setRowCount(0);

        for (Rental rental : controller.getRentals()) {

            String status;

            // Determina l'estat del lloguer
            if (rental.isReturned()) {
                status = bundle.getString("status.returned");
            } else if (rental.isOverdue()) {
                status = bundle.getString("status.overdue");
            } else {
                status = bundle.getString("status.active");
            }

            // Afegeix les dades del lloguer, incloent-hi l'ID ocult
            tableModel.addRow(new Object[]{
                rental.getUser().getName(),
                rental.getGame().getTitle(),
                rental.getPlatform(),
                rental.getStartDate(),
                rental.getExpectedReturnDate(),
                status,
                rental.getId()
            });
        }
    }
    // Model que emmagatzema els usuaris de la llista
    private DefaultListModel<User> userListModel;

    // Llista visual dels usuaris
    private JList<User> userList;
    
    // Model que emmagatzema els videojocs de la llista
    private DefaultListModel<Game> gameListModel;

    // Llista visual dels videojocs
    private JList<Game> gameList;
}
