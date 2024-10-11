package org.uclv;

import org.uclv.models.*;
import org.uclv.ui.ClientLoginPanel;
import org.uclv.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Main {

    private static Central central = new Central("5301", "123 Main St.");
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    public static void main(String[] args) {
        try{
            central= importData();

            for( Client client : central.getClients()){
                System.out.println(client.getUsername());
            }
//             Initialize values
            MainFrame mainFrame = new MainFrame();
            cardLayout = mainFrame.getCardLayout();
            mainPanel = mainFrame.getMainPanel();

            mainPanel.add(new ClientLoginPanel(central, cardLayout, mainPanel), "clientLoginPanel");
            cardLayout.show(mainPanel, "clientLoginPanel");

            mainFrame.setVisible(true);
        }
        catch (IOException | ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportData() throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("src/main/java/org/uclv/data/data.dat"));
        os.writeObject(central);
        os.close();
    }

    public static Central importData() throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("src\\main\\java\\org\\uclv\\data\\data.dat"));
        Central central = (Central) is.readObject();
        is.close();
        return central;
    }

    /**
     * Styles a button
     * @param button The button to be styled
     *
     */
    public static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setPreferredSize(new Dimension(150, 30));
    }

    /**
     * Styles a button (Red)
     * @param button The button to be styled
     */
    public static void styleRedButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Ajustar el padding
        button.setPreferredSize(new Dimension(100, 30)); // Establecer tamaño preferido
    }

    /**
     * Styles a text field
     * @param textField The textField to be styled
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

}