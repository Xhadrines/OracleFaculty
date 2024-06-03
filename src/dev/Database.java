// Importuri de alte pachete din proiectul local
package dev;

// Importuri de biblioteci standard
import oracle.jdbc.pool.OracleDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

// Definirea clasei Database
public class Database {
    private final OracleDataSource dataSource;

    // Constructorul clasei Database
    public Database() {
        Properties props = new Properties();
        try {
            // Incarcarea datelor de configurare din fisierul "faculty.properties"
            props.load(Files.newInputStream(Path.of("faculty.properties"), StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            // Configurarea surselor de date Oracle cu informatiile din fisierul de proprietati
            dataSource = new OracleDataSource();
            String url = String.format("jdbc:oracle:thin:@//%s:%s/%s",
                    props.getProperty("serverName"),
                    props.getProperty("port"),
                    props.getProperty("serviceName"));
            dataSource.setURL(url);
            dataSource.setUser(props.getProperty("user"));
            dataSource.setPassword(System.getenv("ORACLE_PASS")); // Obtinerea parolei de la mediu pentru securitate
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQueryResultSet(String query) throws SQLException {
        var connection = dataSource.getConnection();
        var statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    // Metoda pentru inserarea unui student in baza de date
    public String insertStudent(String nume, String prenume, String cnp, String dataNasterii, String idFacultate) {
        String query = String.format(
                "INSERT INTO studenti (nume, prenume, cnp, data_nasterii, id_facultate) " +
                        "VALUES ('%s', '%s', '%s', TO_DATE('%s', 'YYYY-MM-DD'), '%s')",
                nume, prenume, cnp, dataNasterii, idFacultate
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru studentul " + nume + " " + prenume;
        } catch (SQLException e) {
            return "Eroare la inserare: " + e.getMessage();
        }
    }

    // Metoda pentru actualizarea informatiilor despre un student
    public String updateStudent(String nume, String prenume, String cnp, String dataNasterii, String idFacultate, int idStudent) {
        String query = String.format(
                "UPDATE studenti SET nume = '%s', prenume = '%s', cnp = '%s', data_nasterii = TO_DATE('%s', 'YYYY-MM-DD'), id_facultate = '%s' WHERE id_student = %d",
                nume, prenume, cnp, dataNasterii, idFacultate, idStudent
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Actualizare reusita pentru studentul " + nume + " " + prenume;
        } catch (SQLException e) {
            return "Eroare la actualizare: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea fizica a unui student
    public String deletePhysicalStudent(int idStudent) {
        String query = String.format(
                "DELETE FROM studenti WHERE id_student = %d",
                idStudent
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere fIzica reusita pentru studentul cu ID-ul " + idStudent;
        } catch (SQLException e) {
            return "Eroare la stergere fizica: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea logica a unui student
    public String deleteLogicalStudent(int idStudent) {
        String query = String.format(
                "UPDATE studenti SET sters = 'Da' WHERE id_student = %d",
                idStudent
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere logica reusita pentru studentul cu ID-ul " + idStudent;
        } catch (SQLException e) {
            return "Eroare la stergere logica: " + e.getMessage();
        }
    }

    // Metoda pentru inserarea unui profesor in baza de date
    public String insertProfesor(String nume, String prenume, String idFacultate) {
        String query = String.format(
                "INSERT INTO profesori (nume, prenume, id_facultate) " +
                        "VALUES ('%s', '%s', '%s')",
                nume, prenume, idFacultate
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru profesorul " + nume + " " + prenume;
        } catch (SQLException e) {
            return "Eroare la inserare: " + e.getMessage();
        }
    }

    // Metoda pentru actualizarea informatiilor despre un profesor
    public String updateProfesor(String nume, String prenume, String idFacultate, int idProfesor) {
        String query = String.format(
                "UPDATE profesori SET nume = '%s', prenume = '%s', id_facultate = '%s' WHERE id_profesor = '%d'",
                nume, prenume, idFacultate, idProfesor
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Actualizare reusita pentru profesor " + nume + " " + prenume;
        } catch (SQLException e) {
            return "Eroare la actualizare: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea fizica a unui profesor
    public String deletePhysicalProfesor(int idProfesor) {
        String query = String.format(
                "DELETE FROM profesori WHERE id_profesor = %d",
                idProfesor
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere fizica reusita pentru profesorul cu ID-ul " + idProfesor;
        } catch (SQLException e) {
            return "Eroare la stergere fizica: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea logica a unui profesor
    public String deleteLogicalProfesor(int idProfesor) {
        String query = String.format(
                "UPDATE profesori SET sters = 'Da' WHERE id_profesor = %d",
                idProfesor
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere logica reusita pentru profesorul cu ID-ul " + idProfesor;
        } catch (SQLException e) {
            return "Eroare la stergere logica: " + e.getMessage();
        }
    }

    // Metoda pentru inserarea unei facultati in baza de date
    public String insertFacultate(String idFacultate, String numeSpecializare) {
        String query = String.format(
                "INSERT INTO facultati (id_facultate, nume_specializare) " +
                        "VALUES ('%s', '%s')",
                idFacultate, numeSpecializare
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru facultatea cu ID-ul " + idFacultate;
        } catch (SQLException e) {
            return "Eroare la inserare: " + e.getMessage();
        }
    }

    // Metoda pentru actualizarea informatiilor despre o facultate
    public String updateFacultate(String idFacultate, String numeSpecializare) {
        String query = String.format(
                "UPDATE facultati SET nume_specializare = '%s' WHERE id_facultate = '%s'",
                numeSpecializare, idFacultate
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru facultatea cu ID-ul " + idFacultate;
        } catch (SQLException e) {
            return "Eroare la actualizare: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea fizica a unei facultati
    public String deletePhysicalFacultate(String idFacultate) {
        String query = String.format(
                "DELETE FROM facultati WHERE id_facultate = '%s'",
                idFacultate
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere fizica reusita pentru facultatea cu ID-ul " + idFacultate;
        } catch (SQLException e) {
            return "Eroare la stergere fizica: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea logica a unei facultati
    public String deleteLogicalFacultate(String idFacultate) {
        String query = String.format(
                "UPDATE facultati SET sters = 'Da' WHERE id_facultate = '%s'",
                idFacultate
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere logica reusita pentru facultatea cu ID-ul " + idFacultate;
        } catch (SQLException e) {
            return "Eroare la stergere logica: " + e.getMessage();
        }
    }

    // Metoda pentru inserarea unui curs in baza de date
    public String insertCurs(String idCurs, String numeCurs, String idProfesor, boolean areSeminar, boolean areLaborator, boolean areProiect, String idFacultate, int anStudiu, int semestru) {
        String query = String.format(
                "INSERT INTO cursuri (id_curs, nume_curs, id_profesor, are_seminar, are_laborator, are_proiect, id_facultate, an_studiu, semestru) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, %d)",
                idCurs, numeCurs, idProfesor, areSeminar ? "Da" : "Nu", areLaborator ? "Da" : "Nu", areProiect ? "Da" : "Nu", idFacultate, anStudiu, semestru
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru cursul cu ID-ul " + idCurs;
        } catch (SQLException e) {
            return "Eroare la inserare: " + e.getMessage();
        }
    }

    // Metoda pentru actualizarea informatiilor despre un curs
    public String updateCurs(String idCurs, String numeCurs, String idProfesor, boolean areSeminar, boolean areLaborator, boolean areProiect, String idFacultate, int anStudiu, int semestru) {
        String query = String.format(
                "UPDATE cursuri SET nume_curs = '%s', id_profesor = '%s', are_seminar = '%s', are_laborator = '%s', are_proiect = '%s', id_facultate = '%s', an_studiu = %d, semestru = %d WHERE id_curs = '%s'",
                numeCurs, idProfesor, areSeminar ? "Da" : "Nu", areLaborator ? "Da" : "Nu", areProiect ? "Da" : "Nu", idFacultate, anStudiu, semestru, idCurs
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru cursul cu ID-ul " + idCurs;
        } catch (SQLException e) {
            return "Eroare la actualizare: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea fizica a unui curs
    public String deletePhysicalCurs(String idCurs) {
        String query = String.format(
                "DELETE FROM cursuri WHERE id_curs = '%s'",
                idCurs
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere fizica reusita pentru cursul cu ID-ul " + idCurs;
        } catch (SQLException e) {
            return "Eroare la stergere fizica: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea logica a unui curs
    public String deleteLogicalCurs(String idCurs) {
        String query = String.format(
                "UPDATE cursuri SET sters = 'Da' WHERE id_curs = '%s'",
                idCurs
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere logica reusita pentru cursul cu ID-ul " + idCurs;
        } catch (SQLException e) {
            return "Eroare la stergere logica: " + e.getMessage();
        }
    }

    // Metoda pentru inserarea unei note in baza de date
    public String insertNota(int idStudent, String idCurs, int notaFinala, String dataExaminare) {
        String query = String.format(
                "INSERT INTO note (id_student, id_curs, nota_finala, data_examinare) " +
                        "VALUES (%d, '%s', %d, TO_DATE('%s', 'YYYY-MM-DD'))",
                idStudent, idCurs, notaFinala, dataExaminare
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru studentul cu ID-il " + idStudent;
        } catch (SQLException e) {
            return "Eroare la inserare: " + e.getMessage();
        }
    }

    // Metoda pentru actualizarea informatiilor despre o nota
    public String updateNota(int idStudent, String idCurs, int notaFinala, String dataExaminare, int idNota) {
        String query = String.format(
                "UPDATE note SET nota_finala = %d, data_examinare = TO_DATE('%s', 'YYYY-MM-DD') WHERE id_student = %d AND id_curs = '%s' AND id_nota = %d",
                notaFinala, dataExaminare, idStudent, idCurs, idNota
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Inserare reusita pentru studentul cu ID-il " + idStudent;
        } catch (SQLException e) {
            return "Eroare la actualizare: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea fizica a unei note
    public String deletePhysicalNota(int idNota) {
        String query = String.format(
                "DELETE FROM note WHERE id_nota = %d",
                idNota
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere fizica reusita pentru nota cu ID-ul " + idNota;
        } catch (SQLException e) {
            return "Eroare la stergere fizica: " + e.getMessage();
        }
    }

    // Metoda pentru stergerea logica a unei note
    public String deleteLogicalNota(int idNota) {
        String query = String.format(
                "UPDATE note SET sters = 'Da' WHERE id_nota = %d",
                idNota
        );

        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query);
            return "Stergere logica reusita pentru nota cu ID-ul " + idNota;
        } catch (SQLException e) {
            return "Eroare la stergere logica: " + e.getMessage();
        }
    }
}
