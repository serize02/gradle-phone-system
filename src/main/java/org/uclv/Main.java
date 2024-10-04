package org.uclv;

import org.uclv.models.*;
import org.uclv.ui.ClientLoginPanel;
import org.uclv.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    private static Central central;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    public static void main(String[] args) {
        // Inicializar datos de ejemplo
        central = new Central("5301", "123 Main St.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            central.addTax(new Tax("+43", "5301", 0.1f));
            central.addTax(new Tax("+01", "5301", 0.1f));
        } catch (Exception TaxAlreadyExists) {
            JOptionPane.showMessageDialog(mainPanel, "Tax Already Exists");
        }

        MainFrame mainFrame = new MainFrame();
        cardLayout = mainFrame.getCardLayout();
        mainPanel = mainFrame.getMainPanel();

        mainPanel.add(new ClientLoginPanel(central, cardLayout, mainPanel), "clientLoginPanel");
        cardLayout.show(mainPanel, "clientLoginPanel");

        mainFrame.setVisible(true);
    }

    public static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Ajustar el padding
        button.setPreferredSize(new Dimension(100, 30)); // Establecer tamaño preferido
    }

    public static void styleRedButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Ajustar el padding
        button.setPreferredSize(new Dimension(100, 30)); // Establecer tamaño preferido
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

}