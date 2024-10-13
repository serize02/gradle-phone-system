package org.uclv.ui;

import org.uclv.exceptions.InvalidCredentialsE;
import org.uclv.models.Central;
import org.uclv.models.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.uclv.Main.styleButton;
import static org.uclv.Main.styleTextField;

public class ClientLoginPanel extends JPanel {

    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ClientLoginPanel(Central central, CardLayout cardLayout, JPanel mainPanel){
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    public void init() {
        // Crear e inicializar las componentes
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;

        JLabel welcomeLabel = new JLabel("Bienvenido a Elecsa", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(welcomeLabel, gbc);

        JLabel usernameLabel = new JLabel("Nombre de usuario:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        styleTextField(usernameField);
        gbc.gridx = 1;
        add(usernameField, gbc);

        JLabel codeLabel = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(codeLabel, gbc);

        JPasswordField codeField = new JPasswordField(15);
        styleTextField(codeField);
        gbc.gridx = 1;
        add(codeField, gbc);

        JButton loginButton = new JButton("Iniciar Sesión");
        styleButton(loginButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        JLabel createAccountLabel = new JLabel("Crear cuenta");
        createAccountLabel.setForeground(Color.BLUE.darker());
        createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        add(createAccountLabel, gbc);

        JLabel adminLoginLabel = new JLabel("Iniciar Sesión como Administrador");
        adminLoginLabel.setForeground(Color.BLUE.darker());
        adminLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        add(adminLoginLabel, gbc);

        // Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String code = new String (codeField.getPassword());
                try {
                    Client client = central.verifyClient(username, code);
                    mainPanel.add( new ClientPanel(client, central,  cardLayout, mainPanel), "clientPanel" );
                    cardLayout.show(mainPanel, "clientPanel");
                    // Limpiar campos
                    usernameField.setText("");
                    codeField.setText("");
                } catch (InvalidCredentialsE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Credenciales incorrectas");
                }
            }
        });

        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.add( new ClientRegisterPanel(central, cardLayout, mainPanel), "clientRegisterPanel" );
                cardLayout.show(mainPanel, "clientRegisterPanel");
                // Limpiar campos
                usernameField.setText("");
                codeField.setText("");
            }
        });

        adminLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.add( new AdminLoginPanel(central, cardLayout, mainPanel), "adminLoginPanel" );
                cardLayout.show(mainPanel, "adminLoginPanel");
                // Limpiar campos
                usernameField.setText("");
                codeField.setText("");
            }
        });

    }
}
