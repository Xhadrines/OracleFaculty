// Importuri de alte pachete din proiectul local
package dev;

// Importuri de biblioteci standard
import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// Definirea clasei Interface
public class Interface extends JFrame {
    private JPanel resultPanel;
    private JComboBox<String> tableBox;
    private final Database database;

    public Interface() {
        database = new Database();
        initUI();
    }

    private void initUI() {
        setTitle("Proiect Sandru Alexandru");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // Panou pentru controale
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Lista dropdown pentru numele tabelelor
        String[] tableOptions = {"cursuri", "facultati", "note", "profesori", "studenti"};
        tableBox = new JComboBox<>(tableOptions);

        // Buton pentru trimiterea interogarii
        JButton submitButton = new JButton("Trimite");

        // Buton pentru inserarea datelor
        JButton insertButton = new JButton("Inserare");

        // Buton pentru actualizarea datelor
        JButton updateButton = new JButton("Actualizare");

        // Buton pentru stergerea datelor
        JButton deleteButton = new JButton("Stergere");

        // Buton pentru operatii complexe
        JButton extraButton = new JButton("Extra");

        controlPanel.add(tableBox);
        controlPanel.add(submitButton);
        controlPanel.add(insertButton);
        controlPanel.add(updateButton);
        controlPanel.add(deleteButton);
        controlPanel.add(extraButton);

        // Panou pentru rezultate
        resultPanel = new JPanel(new BorderLayout());

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        add(panel);

        submitButton.addActionListener(_ -> executeQuery());
        insertButton.addActionListener(_ -> openInsertDialog());
        updateButton.addActionListener(_ -> openUpdateDialog());
        deleteButton.addActionListener(_ -> openDeleteDialog());
        extraButton.addActionListener(_ -> openExtraOptionsDialog());
    }

    // Metoda pentru executarea interogarii
    private void executeQuery() {
        String table = (String) tableBox.getSelectedItem();
        String query = String.format("SELECT * FROM %s", table);

        // Clear the result panel and add the JTable with the query results
        resultPanel.removeAll();

        JTable jTable = createTableFromQuery(query);
        JScrollPane scrollPane = new JScrollPane(jTable);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private JTable createTableFromQuery(String query) {
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);

        try {
            ResultSet resultSet = database.executeQueryResultSet(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Adauga numele coloanei
            for (int column = 1; column <= columnCount; column++) {
                tableModel.addColumn(metaData.getColumnName(column));
            }

            // Adauga randurile cu date
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

            resultSet.getStatement().close();
            resultSet.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la executarea interogarii: " + e.getMessage());
        }
        return table;
    }

    // Metoda pentru deschiderea dialogului de inserare
    private void openInsertDialog() {
        String table = (String) tableBox.getSelectedItem();
        if ("studenti".equalsIgnoreCase(table)) {
            openStudentInsertDialog();
        } else if ("profesori".equalsIgnoreCase(table)) {
            openProfesorInsertDialog();
        } else if ("facultati".equalsIgnoreCase(table)) {
            openFacultateInsertDialog();
        } else if ("cursuri".equalsIgnoreCase(table)) {
            openCursInsertDialog();
        } else if ("note".equalsIgnoreCase(table)) {
            openNotaInsertDialog();
        } else {
            JOptionPane.showMessageDialog(this, "Indisponibil");
        }
    }

    // Metoda pentru deschiderea dialogului de actualizare
    private void openUpdateDialog() {
        JTable realTable = (JTable) ((JScrollPane) resultPanel.getComponent(0)).getViewport().getView();

        String table = (String) tableBox.getSelectedItem(); // Obtinem valoarea selectata din dropdown-ul pentru selectarea tabelului

        // Obtinem randul selectat din tabel
        int selectedRow = realTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un rand din tabel pentru a actualiza datele.");
            return; // Iesim din metoda daca nu este selectat niciun rand din tabel
        }

        // Obtinem valorile din randul selectat
        String[] rowData = new String[realTable.getColumnCount()];
        for (int i = 0; i < realTable.getColumnCount(); i++) {
            rowData[i] = realTable.getValueAt(selectedRow, i).toString();
        }

        // Deschidem fereastra de actualizare corespunzatoare tabelului selectat
        if ("studenti".equalsIgnoreCase(table)) {
            openStudentUpdateDialog(rowData);
        } else if ("profesori".equalsIgnoreCase(table)) {
            openProfesorUpdateDialog(rowData);
        } else if ("facultati".equalsIgnoreCase(table)) {
            openFacultateUpdateDialog(rowData);
        } else if ("cursuri".equalsIgnoreCase(table)) {
            openCursUpdateDialog(rowData);
        } else if ("note".equalsIgnoreCase(table)) {
            openNotaUpdateDialog(rowData);
        }
    }

    // Metoda pentru deschiderea dialogului de stergere
    private void openDeleteDialog() {
        JTable realTable = (JTable) ((JScrollPane) resultPanel.getComponent(0)).getViewport().getView();

        String table = (String) tableBox.getSelectedItem(); // Obtinem valoarea selectata din dropdown-ul pentru selectarea tabelului

        // Obtinem randul selectat din tabel
        int selectedRow = realTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selectati un rand din tabel pentru a sterge datele.");
            return; // Iesim din metoda daca nu este selectat niciun rand din tabel
        }

        // Obtinem valorile din randul selectat
        String[] rowData = new String[realTable.getColumnCount()];
        for (int i = 0; i < realTable.getColumnCount(); i++) {
            rowData[i] = realTable.getValueAt(selectedRow, i).toString();
        }

        // Deschidem dialogul de stergere
        JDialog deleteDialog = new JDialog(this, "Stergere", true);
        deleteDialog.setSize(300, 150);
        deleteDialog.setLayout(new GridLayout(3, 1));

        JLabel messageLabel = new JLabel("Alegeti tipul de stergere pentru randul selectat");
        JButton physicalDeleteButton = new JButton("Stergere Fizica");
        JButton logicalDeleteButton = new JButton("Stergere Logica");

        deleteDialog.add(messageLabel);
        deleteDialog.add(physicalDeleteButton);
        deleteDialog.add(logicalDeleteButton);

        // Actiune pentru stergerea fizica
        physicalDeleteButton.addActionListener(_ -> {
            deletePhysical(table, rowData);
            deleteDialog.dispose();
        });

        // Actiune pentru stergerea logica
        logicalDeleteButton.addActionListener(_ -> {
            deleteLogical(table, rowData);
            deleteDialog.dispose();
        });

        deleteDialog.setLocationRelativeTo(this);
        deleteDialog.setVisible(true);
    }

    // Metoda pentru stergerea fizica
    private void deletePhysical(String table, String[] rowData) {
        String result = "";
        if ("studenti".equalsIgnoreCase(table)) {
            result = database.deletePhysicalStudent(Integer.parseInt(rowData[0]));
        } else if ("profesori".equalsIgnoreCase(table)) {
            result = database.deletePhysicalProfesor(Integer.parseInt(rowData[0]));
        } else if ("facultati".equalsIgnoreCase(table)) {
            result = database.deletePhysicalFacultate(rowData[0]);
        } else if ("cursuri".equalsIgnoreCase(table)) {
            result = database.deletePhysicalCurs(rowData[0]);
        } else if ("note".equalsIgnoreCase(table)) {
            result = database.deletePhysicalNota(Integer.parseInt(rowData[0]));
        }
        JOptionPane.showMessageDialog(this, result);
    }

    // Metoda pentru stergerea logica
    private void deleteLogical(String table, String[] rowData) {
        String result = "";
        if ("studenti".equalsIgnoreCase(table)) {
            result = database.deleteLogicalStudent(Integer.parseInt(rowData[0]));
        } else if ("profesori".equalsIgnoreCase(table)) {
            result = database.deleteLogicalProfesor(Integer.parseInt(rowData[0]));
        } else if ("facultati".equalsIgnoreCase(table)) {
            result = database.deleteLogicalFacultate(rowData[0]);
        } else if ("cursuri".equalsIgnoreCase(table)) {
            result = database.deleteLogicalCurs(rowData[0]);
        } else if ("note".equalsIgnoreCase(table)) {
            result = database.deleteLogicalNota(Integer.parseInt(rowData[0]));
        }
        JOptionPane.showMessageDialog(this, result);
    }

    // Metoda pentru deschiderea dialogului de extra
    private void openExtraOptionsDialog() {
        JFrame extraFrame = new JFrame("Operatii Suplimentare");
        extraFrame.setSize(400, 300);
        extraFrame.setLocationRelativeTo(this);

        JPanel buttonPanel = new JPanel(new GridLayout(12, 1));

        JLabel cetinta1Label = new JLabel("1. Care sunt studentii inscrisi la o anumita specializare?");
        JButton cetinta1Button = new JButton("Optiunea 1");
        JLabel cetinta2Label = new JLabel("2. Cati studenti sunt inscrisi la o anumita materie?");
        JButton cetinta2Button = new JButton("Optiunea 2");
        JLabel cetinta3Label = new JLabel("3. Care sunt profesorii care predau intr-un anumit semestru?");
        JButton cetinta3Button = new JButton("Optiunea 3");
        JLabel cetinta4Label = new JLabel("4. Cati studenti sunt inregistrati pentru un anumit curs?");
        JButton cetinta4Button = new JButton("Optiunea 4");
        JLabel cetinta5Label = new JLabel("5. Care sunt cursurile la care un anumit student este inscris?");
        JButton cetinta5Button = new JButton("Optiunea 5");
        JLabel cetinta6Label = new JLabel("6. Cati studenti sunt nascuti intr-un anumit an?");
        JButton cetinta6Button = new JButton("Optiunea 6");

        buttonPanel.add(cetinta1Label);
        buttonPanel.add(cetinta1Button);
        buttonPanel.add(cetinta2Label);
        buttonPanel.add(cetinta2Button);
        buttonPanel.add(cetinta3Label);
        buttonPanel.add(cetinta3Button);
        buttonPanel.add(cetinta4Label);
        buttonPanel.add(cetinta4Button);
        buttonPanel.add(cetinta5Label);
        buttonPanel.add(cetinta5Button);
        buttonPanel.add(cetinta6Label);
        buttonPanel.add(cetinta6Button);

        extraFrame.add(buttonPanel);
        extraFrame.setVisible(true);
    }

    private boolean validateStudentData(String nume, String prenume, String cnp, String dataNasterii, String idFacultate) {
        // Verificare completitudine campuri
        if (nume.isEmpty() || prenume.isEmpty() || cnp.isEmpty() || dataNasterii.isEmpty() || idFacultate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Toate campurile sunt obligatorii!");
            return false;
        }

        // Verificare lungime CNP
        if (cnp.length() != 13) {
            JOptionPane.showMessageDialog(null, "CNP-ul trebuie sa contina exact 13 caractere!");
            return false;
        }

        // Verificare format data de nastere
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.parse(dataNasterii);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Formatul datei de nastere este invalid! Utilizati formatul YYYY-MM-DD.");
            return false;
        }
        return true;
    }

    // Metoda pentru deschiderea dialogului de inserare pentru studenti
    private void openStudentInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Inserare Student", true);
        insertDialog.setSize(400, 300);
        insertDialog.setLayout(new GridLayout(6, 2));

        JLabel numeLabel = new JLabel("Nume:");
        JTextField numeField = new JTextField();
        JLabel prenumeLabel = new JLabel("Prenume:");
        JTextField prenumeField = new JTextField();
        JLabel cnpLabel = new JLabel("CNP:");
        JTextField cnpField = new JTextField();
        JLabel dataNasteriiLabel = new JLabel("Data Nasterii (YYYY-MM-DD):");
        JTextField dataNasteriiField = new JTextField();
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField();

        JButton submitInsertButton = new JButton("Trimite");

        insertDialog.add(numeLabel);
        insertDialog.add(numeField);
        insertDialog.add(prenumeLabel);
        insertDialog.add(prenumeField);
        insertDialog.add(cnpLabel);
        insertDialog.add(cnpField);
        insertDialog.add(dataNasteriiLabel);
        insertDialog.add(dataNasteriiField);
        insertDialog.add(idFacultateLabel);
        insertDialog.add(idFacultateField);
        insertDialog.add(new JLabel()); // Celula goala
        insertDialog.add(submitInsertButton);

        submitInsertButton.addActionListener(_ -> {
            // Validare datelor
            if (validateStudentData(numeField.getText(), prenumeField.getText(), cnpField.getText(),
                    dataNasteriiField.getText(), idFacultateField.getText())) {
                // Daca datele sunt valide, inseram studentul in baza de date
                String result = database.insertStudent(
                        numeField.getText(),
                        prenumeField.getText(),
                        cnpField.getText(),
                        dataNasteriiField.getText(),
                        idFacultateField.getText()
                );
                JOptionPane.showMessageDialog(this, result);
                insertDialog.dispose();
            }
        });

        insertDialog.setLocationRelativeTo(this);
        insertDialog.setVisible(true);
    }

    private void openStudentUpdateDialog(String[] rowData) {
        JDialog updateDialog = new JDialog(this, "Actualizare Student", true);
        updateDialog.setSize(400, 300);
        updateDialog.setLayout(new GridLayout(6, 2));

        // Etichetele si campurile text pentru a afisa si edita datele studentului
        JLabel numeLabel = new JLabel("Nume:");
        JTextField numeField = new JTextField(rowData[1]);
        JLabel prenumeLabel = new JLabel("Prenume:");
        JTextField prenumeField = new JTextField(rowData[2]);
        JLabel cnpLabel = new JLabel("CNP:");
        JTextField cnpField = new JTextField(rowData[3]);
        JLabel dataNasteriiLabel = new JLabel("Data Nasterii (YYYY-MM-DD):");
        JTextField dataNasteriiField = new JTextField(rowData[4]);
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField(rowData[5]);

        JButton submitUpdateButton = new JButton("Trimite");

        updateDialog.add(numeLabel);
        updateDialog.add(numeField);
        updateDialog.add(prenumeLabel);
        updateDialog.add(prenumeField);
        updateDialog.add(cnpLabel);
        updateDialog.add(cnpField);
        updateDialog.add(dataNasteriiLabel);
        updateDialog.add(dataNasteriiField);
        updateDialog.add(idFacultateLabel);
        updateDialog.add(idFacultateField);
        updateDialog.add(new JLabel()); // Celula goala
        updateDialog.add(submitUpdateButton);

        submitUpdateButton.addActionListener(_ -> {
            // Validare datelor
            if (validateStudentData(numeField.getText(), prenumeField.getText(), cnpField.getText(),
                    dataNasteriiField.getText(), idFacultateField.getText())) {
                // Daca datele sunt valide, actualizam studentul in baza de date
                String result = database.updateStudent(
                        numeField.getText(),
                        prenumeField.getText(),
                        cnpField.getText(),
                        dataNasteriiField.getText(),
                        idFacultateField.getText(),
                        Integer.parseInt(rowData[0]) // id_student
                );
                JOptionPane.showMessageDialog(this, result);
                updateDialog.dispose();
            }
        });

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private boolean validateProfesorData(String nume, String prenume, String idFacultate) {
        // Verificare completitudine campuri
        if (nume.isEmpty() || prenume.isEmpty() || idFacultate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Toate campurile sunt obligatorii!");
            return false;
        }
        return true;
    }

    // Metoda pentru deschiderea dialogului de inserare pentru profesori
    private void openProfesorInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Inserare Profesor", true);
        insertDialog.setSize(400, 200);
        insertDialog.setLayout(new GridLayout(4, 2));

        JLabel numeLabel = new JLabel("Nume:");
        JTextField numeField = new JTextField();
        JLabel prenumeLabel = new JLabel("Prenume:");
        JTextField prenumeField = new JTextField();
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField();

        JButton submitInsertButton = new JButton("Trimite");

        insertDialog.add(numeLabel);
        insertDialog.add(numeField);
        insertDialog.add(prenumeLabel);
        insertDialog.add(prenumeField);
        insertDialog.add(idFacultateLabel);
        insertDialog.add(idFacultateField);
        insertDialog.add(new JLabel()); // Celula goala
        insertDialog.add(submitInsertButton);

        submitInsertButton.addActionListener(_ -> {
            // Validare datelor
            if (validateProfesorData(numeField.getText(), prenumeField.getText(), idFacultateField.getText())) {
                // Daca datele sunt valide, inseram profesorul in baza de date
                String result = database.insertProfesor(
                        numeField.getText(),
                        prenumeField.getText(),
                        idFacultateField.getText()
                );
                JOptionPane.showMessageDialog(this, result);
                insertDialog.dispose();
            }
        });

        insertDialog.setLocationRelativeTo(this);
        insertDialog.setVisible(true);
    }

    private void openProfesorUpdateDialog(String[] rowData) {
        JDialog updateDialog = new JDialog(this, "Actualizare Profesor", true);
        updateDialog.setSize(400, 200);
        updateDialog.setLayout(new GridLayout(4, 2));

        JLabel numeLabel = new JLabel("Nume:");
        JTextField numeField = new JTextField(rowData[1]);
        JLabel prenumeLabel = new JLabel("Prenume:");
        JTextField prenumeField = new JTextField(rowData[2]);
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField(rowData[3]);

        JButton submitUpdateButton = new JButton("Trimite");

        updateDialog.add(numeLabel);
        updateDialog.add(numeField);
        updateDialog.add(prenumeLabel);
        updateDialog.add(prenumeField);
        updateDialog.add(idFacultateLabel);
        updateDialog.add(idFacultateField);
        updateDialog.add(new JLabel()); // Celula goala
        updateDialog.add(submitUpdateButton);

        submitUpdateButton.addActionListener(_ -> {
            // Validare datelor
            if (validateProfesorData(numeField.getText(), prenumeField.getText(), idFacultateField.getText())) {
                // Daca datele sunt valide, actualizam profesorul in baza de date
                String result = database.updateProfesor(
                        numeField.getText(),
                        prenumeField.getText(),
                        idFacultateField.getText(),
                        Integer.parseInt(rowData[0]) // id_profesor
                );
                JOptionPane.showMessageDialog(this, result);
                updateDialog.dispose();
            }
        });

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private boolean validateFacultateData(String idFacultate, String numeSpecializare) {
        // Verificare completitudine campuri
        if (idFacultate.isEmpty() || numeSpecializare.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Toate campurile sunt obligatorii!");
            return false;
        }
        return true;
    }

    // Metoda pentru deschiderea dialogului de inserare pentru facultati
    private void openFacultateInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Inserare Facultate", true);
        insertDialog.setSize(400, 200);
        insertDialog.setLayout(new GridLayout(3, 2));

        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField();
        JLabel numeSpecializareLabel = new JLabel("Nume Specializare:");
        JTextField numeSpecializareField = new JTextField();

        JButton submitInsertButton = new JButton("Trimite");

        insertDialog.add(idFacultateLabel);
        insertDialog.add(idFacultateField);
        insertDialog.add(numeSpecializareLabel);
        insertDialog.add(numeSpecializareField);
        insertDialog.add(new JLabel()); // Celula goala
        insertDialog.add(submitInsertButton);

        submitInsertButton.addActionListener(_ -> {
            // Validare datelor
            if (validateFacultateData(idFacultateField.getText(), numeSpecializareField.getText())) {
                // Daca datele sunt valide, inseram facultatea in baza de date
                String result = database.insertFacultate(
                        idFacultateField.getText(),
                        numeSpecializareField.getText()
                );
                JOptionPane.showMessageDialog(this, result);
                insertDialog.dispose();
            }
        });

        insertDialog.setLocationRelativeTo(this);
        insertDialog.setVisible(true);
    }

    private void openFacultateUpdateDialog(String[] rowData) {
        JDialog updateDialog = new JDialog(this, "Actualizare Facultate", true);
        updateDialog.setSize(400, 200);
        updateDialog.setLayout(new GridLayout(3, 2));

        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField(rowData[0]);
        JLabel numeSpecializareLabel = new JLabel("Nume Specializare:");
        JTextField numeSpecializareField = new JTextField(rowData[1]);

        JButton submitUpdateButton = new JButton("Trimite");

        updateDialog.add(idFacultateLabel);
        updateDialog.add(idFacultateField);
        updateDialog.add(numeSpecializareLabel);
        updateDialog.add(numeSpecializareField);
        updateDialog.add(new JLabel()); // Celula goala
        updateDialog.add(submitUpdateButton);

        submitUpdateButton.addActionListener(_ -> {
            // Validare datelor
            if (validateFacultateData(idFacultateField.getText(), numeSpecializareField.getText())) {
                // Daca datele sunt valide, actualizam facultatea in baza de date
                String result = database.updateFacultate(
                        idFacultateField.getText(),
                        numeSpecializareField.getText()
                );
                JOptionPane.showMessageDialog(this, result);
                updateDialog.dispose();
            }
        });
        idFacultateField.setEditable(false);

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private boolean validateCursData(String idCurs, String numeCurs, String idProfesor, String areSeminar, String areLaborator, String areProiect, String idFacultate, String anStudiu, String semestru) {
        // Verificare completitudine campuri
        if (idCurs.isEmpty() || numeCurs.isEmpty() || idProfesor.isEmpty() || areSeminar.isEmpty() || areLaborator.isEmpty() || areProiect.isEmpty() || idFacultate.isEmpty() || anStudiu.isEmpty() || semestru.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Toate campurile sunt obligatorii!");
            return false;
        }

        // Verificare daca areSeminar, areLaborator si areProiect sunt "Da" sau "Nu"
        if (!areSeminar.equalsIgnoreCase("Da") && !areSeminar.equalsIgnoreCase("Nu")) {
            JOptionPane.showMessageDialog(null, "Valoarea pentru Are Seminar trebuie sa fie 'Da' sau 'Nu'!");
            return false;
        }
        if (!areLaborator.equalsIgnoreCase("Da") && !areLaborator.equalsIgnoreCase("Nu")) {
            JOptionPane.showMessageDialog(null, "Valoarea pentru Are Laborator trebuie sa fie 'Da' sau 'Nu'!");
            return false;
        }
        if (!areProiect.equalsIgnoreCase("Da") && !areProiect.equalsIgnoreCase("Nu")) {
            JOptionPane.showMessageDialog(null, "Valoarea pentru Are Proiect trebuie sa fie 'Da' sau 'Nu'!");
            return false;
        }
        return true;
    }

    // Metoda pentru deschiderea dialogului de inserare pentru cursuri
    private void openCursInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Inserare Curs", true);
        insertDialog.setSize(400, 300);
        insertDialog.setLayout(new GridLayout(10, 2));

        JLabel idCursLabel = new JLabel("ID Curs:");
        JTextField idCursField = new JTextField();
        JLabel numeCursLabel = new JLabel("Nume Curs:");
        JTextField numeCursField = new JTextField();
        JLabel idProfesorLabel = new JLabel("ID Profesor:");
        JTextField idProfesorField = new JTextField();
        JLabel areSeminarLabel = new JLabel("Are Seminar (Da/Nu):");
        JTextField areSeminarField = new JTextField();
        JLabel areLaboratorLabel = new JLabel("Are Laborator (Da/Nu):");
        JTextField areLaboratorField = new JTextField();
        JLabel areProiectLabel = new JLabel("Are Proiect (Da/Nu):");
        JTextField areProiectField = new JTextField();
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField();
        JLabel anStudiuLabel = new JLabel("An Studiu:");
        JTextField anStudiuField = new JTextField();
        JLabel semestruLabel = new JLabel("Semestru:");
        JTextField semestruField = new JTextField();

        JButton submitInsertButton = new JButton("Trimite");

        insertDialog.add(idCursLabel);
        insertDialog.add(idCursField);
        insertDialog.add(numeCursLabel);
        insertDialog.add(numeCursField);
        insertDialog.add(idProfesorLabel);
        insertDialog.add(idProfesorField);
        insertDialog.add(areSeminarLabel);
        insertDialog.add(areSeminarField);
        insertDialog.add(areLaboratorLabel);
        insertDialog.add(areLaboratorField);
        insertDialog.add(areProiectLabel);
        insertDialog.add(areProiectField);
        insertDialog.add(idFacultateLabel);
        insertDialog.add(idFacultateField);
        insertDialog.add(anStudiuLabel);
        insertDialog.add(anStudiuField);
        insertDialog.add(semestruLabel);
        insertDialog.add(semestruField);
        insertDialog.add(new JLabel()); // Celula goala
        insertDialog.add(submitInsertButton);

        submitInsertButton.addActionListener(_ -> {
            // Validare datelor
            if (validateCursData(
                    idCursField.getText(),
                    numeCursField.getText(),
                    idProfesorField.getText(),
                    areSeminarField.getText(),
                    areLaboratorField.getText(),
                    areProiectField.getText(),
                    idFacultateField.getText(),
                    anStudiuField.getText(),
                    semestruField.getText())) {

                // Convertire valori "Da" sau "Nu" in valori booleane
                boolean areSeminar = areSeminarField.getText().equalsIgnoreCase("Da");
                boolean areLaborator = areLaboratorField.getText().equalsIgnoreCase("Da");
                boolean areProiect = areProiectField.getText().equalsIgnoreCase("Da");

                // Inserare curs in baza de date
                String result = database.insertCurs(
                        idCursField.getText(),
                        numeCursField.getText(),
                        idProfesorField.getText(),
                        areSeminar,
                        areLaborator,
                        areProiect,
                        idFacultateField.getText(),
                        Integer.parseInt(anStudiuField.getText()),
                        Integer.parseInt(semestruField.getText())
                );
                JOptionPane.showMessageDialog(this, result);
                insertDialog.dispose();
            }
        });

        insertDialog.setLocationRelativeTo(this);
        insertDialog.setVisible(true);
    }

    private void openCursUpdateDialog(String[] rowData) {
        JDialog updateDialog = new JDialog(this, "Actualizare Curs", true);
        updateDialog.setSize(400, 300);
        updateDialog.setLayout(new GridLayout(10, 2));

        JLabel idCursLabel = new JLabel("ID Curs:");
        JTextField idCursField = new JTextField(rowData[0]);
        JLabel numeCursLabel = new JLabel("Nume Curs:");
        JTextField numeCursField = new JTextField(rowData[1]);
        JLabel idProfesorLabel = new JLabel("ID Profesor:");
        JTextField idProfesorField = new JTextField(rowData[2]);
        JLabel areSeminarLabel = new JLabel("Are Seminar (Da/Nu):");
        JTextField areSeminarField = new JTextField(rowData[3]);
        JLabel areLaboratorLabel = new JLabel("Are Laborator (Da/Nu):");
        JTextField areLaboratorField = new JTextField(rowData[4]);
        JLabel areProiectLabel = new JLabel("Are Proiect (Da/Nu):");
        JTextField areProiectField = new JTextField(rowData[5]);
        JLabel idFacultateLabel = new JLabel("ID Facultate:");
        JTextField idFacultateField = new JTextField(rowData[6]);
        JLabel anStudiuLabel = new JLabel("An Studiu:");
        JTextField anStudiuField = new JTextField(rowData[7]);
        JLabel semestruLabel = new JLabel("Semestru:");
        JTextField semestruField = new JTextField(rowData[8]);

        JButton submitUpdateButton = new JButton("Trimite");

        updateDialog.add(idCursLabel);
        updateDialog.add(idCursField);
        updateDialog.add(numeCursLabel);
        updateDialog.add(numeCursField);
        updateDialog.add(idProfesorLabel);
        updateDialog.add(idProfesorField);
        updateDialog.add(areSeminarLabel);
        updateDialog.add(areSeminarField);
        updateDialog.add(areLaboratorLabel);
        updateDialog.add(areLaboratorField);
        updateDialog.add(areProiectLabel);
        updateDialog.add(areProiectField);
        updateDialog.add(idFacultateLabel);
        updateDialog.add(idFacultateField);
        updateDialog.add(anStudiuLabel);
        updateDialog.add(anStudiuField);
        updateDialog.add(semestruLabel);
        updateDialog.add(semestruField);
        updateDialog.add(new JLabel()); // Celula goala
        updateDialog.add(submitUpdateButton);

        submitUpdateButton.addActionListener(_ -> {
            // Validare datelor
            if (validateCursData(
                    idCursField.getText(),
                    numeCursField.getText(),
                    idProfesorField.getText(),
                    areSeminarField.getText(),
                    areLaboratorField.getText(),
                    areProiectField.getText(),
                    idFacultateField.getText(),
                    anStudiuField.getText(),
                    semestruField.getText())) {

                // Convertire valori "Da" sau "Nu" in valori booleane
                boolean areSeminar = areSeminarField.getText().equalsIgnoreCase("Da");
                boolean areLaborator = areLaboratorField.getText().equalsIgnoreCase("Da");
                boolean areProiect = areProiectField.getText().equalsIgnoreCase("Da");

                // Actualizare curs in baza de date
                String result = database.updateCurs(
                        idCursField.getText(),
                        numeCursField.getText(),
                        idProfesorField.getText(),
                        areSeminar,
                        areLaborator,
                        areProiect,
                        idFacultateField.getText(),
                        Integer.parseInt(anStudiuField.getText()),
                        Integer.parseInt(semestruField.getText())
                );
                JOptionPane.showMessageDialog(this, result);
                updateDialog.dispose();
            }
        });

        idCursField.setEditable(false);

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private boolean validateNotaData(String idStudent, String idCurs, String notaFinala, String dataExaminare) {
        // Verificare completitudine campuri
        if (idStudent.isEmpty() || idCurs.isEmpty() || notaFinala.isEmpty() || dataExaminare.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Toate campurile sunt obligatorii!");
            return false;
        }

        // Verificare daca nota finala este un numar valid
        try {
            int nota = Integer.parseInt(notaFinala);
            if (nota < 1 || nota > 10) {
                JOptionPane.showMessageDialog(null, "Nota finala trebuie sa fie un numar intre 1 si 10!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Nota finala trebuie sa fie un numar intreg!");
            return false;
        }
        return true;
    }

    // Metoda pentru deschiderea dialogului de inserare pentru note
    private void openNotaInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Inserare Nota", true);
        insertDialog.setSize(400, 300);
        insertDialog.setLayout(new GridLayout(5, 2));

        JLabel idStudentLabel = new JLabel("ID Student:");
        JTextField idStudentField = new JTextField(15);
        JLabel idCursLabel = new JLabel("ID Curs:");
        JTextField idCursField = new JTextField(15);
        JLabel notaFinalaLabel = new JLabel("Nota Finala:");
        JTextField notaFinalaField = new JTextField(15);
        JLabel dataExaminareLabel = new JLabel("Data Examinarii (YYYY-MM-DD):");
        JTextField dataExaminareField = new JTextField(15);

        JButton submitInsertButton = new JButton("Trimite");

        insertDialog.add(idStudentLabel);
        insertDialog.add(idStudentField);
        insertDialog.add(idCursLabel);
        insertDialog.add(idCursField);
        insertDialog.add(notaFinalaLabel);
        insertDialog.add(notaFinalaField);
        insertDialog.add(dataExaminareLabel);
        insertDialog.add(dataExaminareField);
        insertDialog.add(new JLabel()); // Celula goala
        insertDialog.add(submitInsertButton);

        submitInsertButton.addActionListener(_ -> {
            // Validare datelor
            if (validateNotaData(
                    idStudentField.getText(),
                    idCursField.getText(),
                    notaFinalaField.getText(),
                    dataExaminareField.getText())) {

                // Inserare nota in baza de date
                String result = database.insertNota(
                        Integer.parseInt(idStudentField.getText()),
                        idCursField.getText(),
                        Integer.parseInt(notaFinalaField.getText()),
                        dataExaminareField.getText()
                );
                JOptionPane.showMessageDialog(this, result);
                insertDialog.dispose();
            }
        });

        insertDialog.setLocationRelativeTo(this);
        insertDialog.setVisible(true);
    }

    private void openNotaUpdateDialog(String[] rowData) {
        JDialog updateDialog = new JDialog(this, "Actualizare Nota", true);
        updateDialog.setSize(400, 200);
        updateDialog.setLayout(new GridLayout(5, 2));

        JLabel idStudentLabel = new JLabel("ID Student:");
        JTextField idStudentField = new JTextField(rowData[1]);
        JLabel idCursLabel = new JLabel("ID Curs:");
        JTextField idCursField = new JTextField(rowData[2]);
        JLabel notaFinalaLabel = new JLabel("Nota Finala:");
        JTextField notaFinalaField = new JTextField(rowData[3]);
        JLabel dataExaminareLabel = new JLabel("Data Examinarii (YYYY-MM-DD):");
        JTextField dataExaminareField = new JTextField(rowData[4]);

        JButton submitUpdateButton = new JButton("Trimite");

        updateDialog.add(idStudentLabel);
        updateDialog.add(idStudentField);
        updateDialog.add(idCursLabel);
        updateDialog.add(idCursField);
        updateDialog.add(notaFinalaLabel);
        updateDialog.add(notaFinalaField);
        updateDialog.add(dataExaminareLabel);
        updateDialog.add(dataExaminareField);
        updateDialog.add(new JLabel()); // Celula goala
        updateDialog.add(submitUpdateButton);

        submitUpdateButton.addActionListener(_ -> {
            // Validare datelor
            if (validateNotaData(
                    idStudentField.getText(),
                    idCursField.getText(),
                    notaFinalaField.getText(),
                    dataExaminareField.getText())) {

                // Actualizare nota in baza de date
                String result = database.updateNota(
                        Integer.parseInt(idStudentField.getText()),
                        idCursField.getText(),
                        Integer.parseInt(notaFinalaField.getText()),
                        dataExaminareField.getText(),
                        Integer.parseInt(rowData[0])
                );
                JOptionPane.showMessageDialog(this, result);
                updateDialog.dispose();
            }
        });

        // Faceti campurile ID Student si ID Curs nedetaliabile pentru ca acestea nu ar trebui sa fie editabile
        idStudentField.setEditable(false);
        idCursField.setEditable(false);

        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }
}
