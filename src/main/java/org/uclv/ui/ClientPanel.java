package org.uclv.ui;

import org.uclv.exceptions.PhoneAlreadyExists;
import org.uclv.exceptions.WrongPhoneNumberFormatE;
import org.uclv.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static org.uclv.Main.*;

public class ClientPanel extends JPanel {
    private Client client;
    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel phoneNumbersPanel;

    public ClientPanel(Client client, Central central, CardLayout cardLayout, JPanel mainPanel) {
        this.client = client;
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Tipo de Cliente: " + (client.getType() == 'E' ? "Estatal" : "Particular"));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(typeLabel, gbc);

        phoneNumbersPanel = new JPanel();
        phoneNumbersPanel.setLayout(new BoxLayout(phoneNumbersPanel, BoxLayout.Y_AXIS));
        phoneNumbersPanel.setBorder(BorderFactory.createEmptyBorder()); // Eliminar borde
        updatePhoneNumbersPanel();

        JScrollPane scrollPane = new JScrollPane(phoneNumbersPanel);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addPhoneNumberButton = createIconButton("path/to/add_icon.png", "Agregar");
        buttonPanel.add(addPhoneNumberButton);

        JButton makeCallButton = createIconButton("path/to/call_icon.png", "Llamar");
        makeCallButton.setBackground(Color.GREEN);
        buttonPanel.add(makeCallButton);

        JButton backButton = createIconButton("path/to/back_icon.png", "Atrás");
        buttonPanel.add(backButton);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        addPhoneNumberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField countryCodeField = new JTextField(5);
                JTextField numberField = new JTextField(10);
                JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                inputPanel.add(new JLabel("Código del País:"));
                inputPanel.add(countryCodeField);
                inputPanel.add(new JLabel("Número:"));
                inputPanel.add(numberField);

                int result = JOptionPane.showConfirmDialog(mainPanel, inputPanel, "Inserte el número de teléfono", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String countryCode = countryCodeField.getText().trim();
                    String number = numberField.getText().trim();
                    if (countryCode.isEmpty() || number.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "El código del país y el número no pueden estar vacíos");
                        return;
                    }

                    try {
                        PhoneNumber phoneNumber = new PhoneNumber(countryCode, number);
                        client.addPhoneNumber(phoneNumber);
                        JOptionPane.showMessageDialog(mainPanel, "Número de teléfono agregado exitosamente");
                        updatePhoneNumbersPanel(); // Refrescar el panel del cliente
                    } catch (WrongPhoneNumberFormatE ex) {
                        JOptionPane.showMessageDialog(mainPanel, "Formato de número de teléfono incorrecto");
                    } catch (PhoneAlreadyExists ex) {
                        JOptionPane.showMessageDialog(mainPanel, "El número de teléfono ya existe");
                    }
                }
            }
        });

        makeCallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client.getPhoneNumbers().isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "No hay números de teléfono registrados");
                    return;
                }

                PhoneNumber[] phoneNumbers = client.getPhoneNumbers().toArray(new PhoneNumber[0]);
                PhoneNumber selectedPhoneNumber = (PhoneNumber) JOptionPane.showInputDialog(
                        mainPanel,
                        "Seleccione un número de teléfono",
                        "Realizar Llamada",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        phoneNumbers,
                        phoneNumbers[0]
                );

                if (selectedPhoneNumber != null) {
                    JTextField senderLocationField = new JTextField(15);
                    JTextField receiverCountryCodeField = new JTextField(5);
                    JTextField receiverLocationField = new JTextField(15);
                    JTextField receiverPhoneField = new JTextField(10);
                    String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
                    JComboBox<String> monthComboBox = new JComboBox<>(months);
                    JTextField timeField = new JTextField(5);
                    JPanel callInputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
                    callInputPanel.add(new JLabel("Localización del Emisor:"));
                    callInputPanel.add(senderLocationField);
                    callInputPanel.add(new JLabel("Código del País Receptor:"));
                    callInputPanel.add(receiverCountryCodeField);
                    callInputPanel.add(new JLabel("Localización del Receptor:"));
                    callInputPanel.add(receiverLocationField);
                    callInputPanel.add(new JLabel("Número Receptor:"));
                    callInputPanel.add(receiverPhoneField);
                    callInputPanel.add(new JLabel("Mes:"));
                    callInputPanel.add(monthComboBox);
                    callInputPanel.add(new JLabel("Tiempo (segundos):"));
                    callInputPanel.add(timeField);

                    int callResult = JOptionPane.showConfirmDialog(mainPanel, callInputPanel, "Datos de la Llamada", JOptionPane.OK_CANCEL_OPTION);
                    if (callResult == JOptionPane.OK_OPTION) {
                        try {
                            String senderLocation = senderLocationField.getText().trim();
                            String receiverCountryCode = receiverCountryCodeField.getText().trim();
                            String receiverLocation = receiverLocationField.getText().trim();
                            String receiverPhone = receiverPhoneField.getText().trim();
                            int month = monthComboBox.getSelectedIndex() + 1; // Meses de 1 a 12
                            int time = Integer.parseInt(timeField.getText().trim());

                            if (senderLocation.isBlank() || receiverCountryCode.isBlank() || receiverLocation.isBlank() ||
                                    receiverPhone.isBlank() || timeField.getText().trim().isBlank() || time < 0 ) {
                                JOptionPane.showMessageDialog(mainPanel, "Todos los campos deben estar llenos");
                                return;
                            }

                            try {
                                Integer.parseInt(senderLocation);
                                Integer.parseInt(receiverCountryCode);
                                Integer.parseInt(receiverLocation);
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(mainPanel, "Los códigos de localización deben ser números enteros");
                                return;
                            }

                            if (! (receiverCountryCode.equals("+53")  || selectedPhoneNumber.getCountry_code().equals("+53"))  ) {
                                JOptionPane.showMessageDialog(mainPanel, "Al menos uno de los dos contactos debe ser de Cuba");
                                return;
                            }

                            String internationalCode = !receiverCountryCode.equals("+53") ? receiverCountryCode : selectedPhoneNumber.getCountry_code();

                            if(!internationalCode.equals("+53")){
                                List<Tax> taxes = central.getTaxes();

                                int i = 0;
                                while(i < taxes.size() && !taxes.get(i).getCountryCode().equals(internationalCode)){
                                    i++;
                                }

                                if(i == taxes.size()){
                                    JOptionPane.showMessageDialog(mainPanel, "No se ha encontrado el impuesto para la llamada");
                                    return;
                                }

                            }

                            PhoneNumber receiverPhoneNumber = new PhoneNumber(receiverCountryCode, receiverPhone);
                            Call call = new Call(
                                    selectedPhoneNumber.getCountry_code(),
                                    senderLocation,
                                    selectedPhoneNumber.getNumber(),
                                    receiverPhoneNumber.getCountry_code(),
                                    receiverLocation,
                                    receiverPhoneNumber.getNumber(),
                                    month,
                                    time
                            );

                            // Aquí puedes agregar la lógica para manejar la llamada, como guardarla en un historial
                            central.addCall(call);
                            JOptionPane.showMessageDialog(mainPanel, "Llamada realizada exitosamente");
                        } catch (WrongPhoneNumberFormatE ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Formato de número de teléfono del receptor incorrecto");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Time debe ser un número entero positivo");
                        }
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "clientLoginPanel"); // Volver al panel de login
            }
        });
    }

    private void updatePhoneNumbersPanel() {
        phoneNumbersPanel.removeAll();
        if (client.getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : client.getPhoneNumbers()) {
                JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                JLabel phoneLabel = new JLabel(phoneNumber.getCountry_code() + " " + phoneNumber.getNumber());
                phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                phonePanel.add(phoneLabel);

                JButton deleteButton = createIconButton("path/to/delete_icon.png", "Eliminar");
                deleteButton.setPreferredSize(new Dimension(20, 20));
                phonePanel.add(deleteButton);

                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.removePhoneNumber(phoneNumber);
                            JOptionPane.showMessageDialog(mainPanel, "Número de teléfono eliminado exitosamente");
                            updatePhoneNumbersPanel(); // Refrescar el panel del cliente
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el número de teléfono");
                        }
                    }
                });

                phoneNumbersPanel.add(phonePanel);
            }
        }
        phoneNumbersPanel.revalidate();
        phoneNumbersPanel.repaint();
    }

    private JButton createIconButton(String iconPath, String tooltip) {
        JButton button = new JButton(new ImageIcon(iconPath));
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(30, 30));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }
}