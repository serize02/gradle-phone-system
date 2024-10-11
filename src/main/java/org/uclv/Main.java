package org.uclv;

import org.uclv.exceptions.*;
import org.uclv.models.*;
import org.uclv.ui.ClientLoginPanel;
import org.uclv.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Central central;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    public static void main(String[] args) {
        // Set central's data
        List<Client> clients = new ArrayList<>();
        List<Tax> taxes = new ArrayList<>();
        List<Call> calls = new ArrayList<>();
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        central = new Central("5301", "123 Main St.",clients,calls,taxes);

        try {
            Client c1=new Client("amalis05","0231",'P');
            central.addClient(c1);
            c1.addPhoneNumber(new PhoneNumber("+053","55874212"));
            c1.addPhoneNumber(new PhoneNumber("+053","55678904"));
        }
        catch (WrongCodeFormatE | WrongPhoneNumberFormatE | ClientAlreadyExistsE | PhoneAlreadyExists e) {
            throw new RuntimeException(e);
        }

        // Initialize values
        MainFrame mainFrame = new MainFrame();
        cardLayout = mainFrame.getCardLayout();
        mainPanel = mainFrame.getMainPanel();

        mainPanel.add(new ClientLoginPanel(central, cardLayout, mainPanel), "clientLoginPanel");
        cardLayout.show(mainPanel, "clientLoginPanel");

        mainFrame.setVisible(true);
    }

    public static void exportData() throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("src/main/java/org/uclv/data/data.dat"));
        os.writeObject(central);
        os.close();
    }

    public static Central importData() throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("src/main/java/org/uclv/data.data.dat"));
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