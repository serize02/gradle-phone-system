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

        JLabel codeLabel = new JLabel("Código de acceso:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(codeLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Iniciar Sesión");
        styleButton(loginButton);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        JButton backButton = new JButton("Atrás");
        styleRedButton(backButton);
        gbc.gridy = 2;
        add(backButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            //Verifica que la contraseña de inicio de sesión sea "admin" , de lo contrario denega la entrada
            public void actionPerformed(ActionEvent e) {
                if(passwordField.getText().equals("admin")){
                    mainPanel.add(new AdminPanel(central, cardLayout, mainPanel), "adminPanel");
                    cardLayout.show(mainPanel, "adminPanel");
                    // Reset value
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Código de acceso erróneo");
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
