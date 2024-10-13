package org.uclv.ui;

import org.uclv.exceptions.ClientAlreadyExistsE;
import org.uclv.exceptions.WrongCodeFormatE;
import org.uclv.models.Central;
import org.uclv.models.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.uclv.Main.*;

public class ClientRegisterPanel extends JPanel {

    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ClientRegisterPanel(Central central, CardLayout cardLayout, JPanel mainPanel){
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    public void init(){
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Nombre de usuario:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        styleTextField(usernameField);
        gbc.gridx = 1;
        add(usernameField, gbc);

        JLabel codeLabel = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(codeLabel, gbc);

        JPasswordField codeField = new JPasswordField(15);
        styleTextField(codeField);
        gbc.gridx = 1;
        add(codeField, gbc);

        JLabel typeLabel = new JLabel("Tipo de Cliente:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(typeLabel, gbc);

        String[] clientTypes = {"Estatal", "Particular"};
        JComboBox<String> typeComboBox = new JComboBox<>(clientTypes);
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        add(typeComboBox, gbc);

        JButton registerButton = new JButton("Registrarse");
        styleButton(registerButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(registerButton, gbc);

        JButton backButton = new JButton("Atrás");
        styleRedButton(backButton);
        gbc.gridy = 4;
        add(backButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(codeField.getPassword());
                char type = typeComboBox.getSelectedIndex() == 0 ? 'E' : 'P';

                try {
                    central.addClient(new Client(username, password, type));
                    JOptionPane.showMessageDialog(mainPanel, "Cliente registrado exitosamente");
                    Client client = central.getClients().getLast();
                    mainPanel.add( new ClientPanel(client, central,  cardLayout, mainPanel), "clientPanel" );
                    cardLayout.show(mainPanel, "clientPanel");
                } catch (ClientAlreadyExistsE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "El cliente ya existe");
                } catch (WrongCodeFormatE ex) {
                    if (password.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "La contraseña no puede estar vacía");
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "El nombre de usuario deben ser 6 caraceteres alfanuméricos");
                    }
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
