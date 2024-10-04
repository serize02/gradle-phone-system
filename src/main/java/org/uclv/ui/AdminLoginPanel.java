package org.uclv.ui;

import org.uclv.models.Central;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.uclv.Main.*;

public class AdminLoginPanel extends JPanel {

    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AdminLoginPanel(Central central, CardLayout cardLayout, JPanel mainPanel) {
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    public void init() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Admin Code:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(codeLabel, gbc);

        JPasswordField codeField = new JPasswordField(15);
        styleTextField(codeField);
        gbc.gridx = 1;
        add(codeField, gbc);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        JButton backButton = new JButton("Back");
        styleRedButton(backButton);
        gbc.gridy = 2;
        add(backButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica de autenticación de administrador
                if(codeField.getText().equals("admin")) {
                    mainPanel.add(new AdminPanel(central, cardLayout, mainPanel), "adminPanel");
                    cardLayout.show(mainPanel, "adminPanel");
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Invalid Admin Code");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "clientLoginPanel");
            }
        });
    }
}
