
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

        DefaultListModel<User> listModel = new DefaultListModel<>();

        for (User user : controller.getUsers()) {
            listModel.addElement(user);
        }

        JList<User> userList = new JList<>(listModel);

        panel.add(new JScrollPane(userList), BorderLayout.CENTER);

        return panel;
    }

    // Crea el panell de gestió dels videojocs
    private JPanel createGamesPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultListModel<Game> listModel = new DefaultListModel<>();

        for (Game game : controller.getGames()) {
            listModel.addElement(game);
        }

        JList<Game> gameList = new JList<>(listModel);

        panel.add(new JScrollPane(gameList), BorderLayout.CENTER);

        return panel;
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
}
