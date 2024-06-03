// Importuri de alte pachete din proiectul local
package dev;

// Importuri de biblioteci standard
import javax.swing.*;

// Definirea clasei Main
public class Main {
    public static void main(String[] args) {
        // Utilizam SwingUtilities.invokeLater pentru a asigura ca crearea si afisarea
        // interfetei grafice sunt gestionate in firul de gestionare al evenimentelor Swing.
        SwingUtilities.invokeLater(() -> {
            // Cream o instanta a clasei Interface (interfata grafica) si o facem vizibila.
            Interface ex = new Interface();
            ex.setVisible(true);
        });
    }
}
