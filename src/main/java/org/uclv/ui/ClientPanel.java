package org.uclv.ui;

import org.uclv.exceptions.PhoneAlreadyExistsE;
import org.uclv.exceptions.PhoneNumberDoesNotExistsE;
import org.uclv.exceptions.WrongPhoneNumberFormatE;
import org.uclv.exceptions.WrongTaxCodeE;
import org.uclv.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClientPanel extends JPanel {
    private Client client;
    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel phoneNumbersPanel;
    private JPanel recentCallsPanel;
    private List<Call> recentCalls;

    public ClientPanel(Client client, Central central, CardLayout cardLayout, JPanel mainPanel) {
        this.client = client;
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.recentCalls = new ArrayList<>();
        init();
    }

    private void init() {
        // Crear e inicializar las componentes
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Icono del perfil
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource("profile.jpg");
        ImageIcon profileIconImage = new ImageIcon(url);
        JLabel profileIcon = new JLabel(profileIconImage);
        profileIcon.setPreferredSize(new Dimension(200, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(profileIcon, gbc);

        JLabel usernameLabel = new JLabel("Usuario: " + client.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        add(usernameLabel, gbc);

        JLabel typeLabel = new JLabel("Tipo de Cliente: " + (client.getType() == 'E' ? "Estatal" : "Particular"));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        add(typeLabel, gbc);

        phoneNumbersPanel = new JPanel();
        phoneNumbersPanel.setBackground(Color.WHITE);
        phoneNumbersPanel.setLayout(new BoxLayout(phoneNumbersPanel, BoxLayout.Y_AXIS));
        phoneNumbersPanel.setBorder(BorderFactory.createTitledBorder("Números de Teléfono"));
        updatePhoneNumbersPanel();

        JScrollPane phoneScrollPane = new JScrollPane(phoneNumbersPanel);
        phoneScrollPane.setPreferredSize(new Dimension(200, 250));
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(phoneScrollPane, gbc);

        recentCallsPanel = new JPanel();
        recentCallsPanel.setBackground(Color.WHITE);
        recentCallsPanel.setLayout(new BoxLayout(recentCallsPanel, BoxLayout.Y_AXIS));
        recentCallsPanel.setBorder(BorderFactory.createTitledBorder("Llamadas Recientes"));
        updateRecentCallsPanel();

        JScrollPane callsScrollPane = new JScrollPane(recentCallsPanel);
        callsScrollPane.setPreferredSize(new Dimension(400, 300));
        gbc.gridx = 1;
        add(callsScrollPane, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton addPhoneNumberButton = new JButton("Agregar Número de Teléfono");
        addPhoneNumberButton.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(addPhoneNumberButton);

        JButton makeCallButton = new JButton("Realizar Llamada");
        makeCallButton.setFont(new Font("Arial", Font.PLAIN, 14));
        makeCallButton.setBackground(Color.GREEN);
        buttonPanel.add(makeCallButton);

        JButton logOutButton = new JButton("Cerrar Sesión");
        logOutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(logOutButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Listeners
        addPhoneNumberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField countryCodeField = new JTextField(5);
                JTextField numberField = new JTextField(10);
                JPanel inputPanel = new JPanel(new GridLayout(2, 2));
                inputPanel.add(new JLabel("Código del País:"));
                inputPanel.add(countryCodeField);
                inputPanel.add(new JLabel("Número:"));
                inputPanel.add(numberField);

                String[] options = {"Aceptar", "Cancelar"};
                int result = JOptionPane.showOptionDialog(
                        mainPanel,
                        inputPanel,
                        "Inserte el número de teléfono",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        null
                );
                if (result == JOptionPane.OK_OPTION) {
                    String countryCode = countryCodeField.getText().trim();
                    String number = numberField.getText().trim();
                    if (countryCode.isEmpty() || number.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "El código del país y el número no pueden estar vacíos");
                        return;
                    }

                    try {
                        PhoneNumber phoneNumber = new PhoneNumber(countryCode, number);
                        if(central.phoneFound(phoneNumber)) throw new PhoneAlreadyExistsE();
                        client.addPhoneNumber(phoneNumber);
                        JOptionPane.showMessageDialog(mainPanel, "Número de teléfono agregado exitosamente");
                        updatePhoneNumbersPanel();
                    } catch (WrongPhoneNumberFormatE ex) {
                        JOptionPane.showMessageDialog(mainPanel, "Formato de número de teléfono incorrecto");
                    } catch (PhoneAlreadyExistsE ex) {
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
                    showCallDialog(selectedPhoneNumber);
                }
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Sí", "No"};
                int confirm = JOptionPane.showOptionDialog(
                        mainPanel,
                        "¿Estás seguro de salir? Las llamadas recientes agregadas no serán mostradas más aquí",
                        "Confirmar Cierre",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        null
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    recentCalls.clear();
                    updateRecentCallsPanel();
                    cardLayout.show(mainPanel, "clientLoginPanel");
                }
            }
        });
    }

    private void updatePhoneNumbersPanel() {
        phoneNumbersPanel.removeAll();
        if (client.getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : client.getPhoneNumbers()) {
                JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                JLabel phoneLabel = new JLabel(phoneNumber.getCountryCode() + " " + phoneNumber.getNumber());
                phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                phonePanel.add(phoneLabel);

                phoneLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem callItem = new JMenuItem("Llamar");
                        JMenuItem deleteItem = new JMenuItem("Eliminar");

                        callItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                showCallDialog(phoneNumber);
                            }
                        });

                        deleteItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String[] options = {"Sí", "No"};
                                int confirm = JOptionPane.showOptionDialog(
                                        mainPanel,
                                        "¿Estás seguro de que deseas eliminar este número de teléfono?",
                                        "Confirmar Eliminación",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        null
                                );
                                if (confirm == JOptionPane.YES_OPTION) {
                                    try {
                                        client.removePhoneNumber(phoneNumber);
                                        JOptionPane.showMessageDialog(mainPanel, "Número de teléfono eliminado exitosamente");
                                        // Refrescar el panel de números de teléfono
                                        updatePhoneNumbersPanel();
                                    } catch (PhoneNumberDoesNotExistsE ex) {
                                        JOptionPane.showMessageDialog(mainPanel, "El número de teléfono no existe");
                                    }
                                }
                            }
                        });

                        menu.add(callItem);
                        menu.add(deleteItem);
                        menu.show(phoneLabel, e.getX(), e.getY());
                    }
                });

                phoneNumbersPanel.add(phonePanel);
            }
        }
        phoneNumbersPanel.revalidate();
        phoneNumbersPanel.repaint();
    }

    private void showCallDialog(PhoneNumber selectedPhoneNumber) {
        JTextField senderLocationField = new JTextField(15);
        JTextField receiverCountryCodeField = new JTextField(5);
        JTextField receiverLocationField = new JTextField(15);
        JTextField receiverPhoneField = new JTextField(10);
        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        JTextField timeField = new JTextField(5);
        JPanel callInputPanel = new JPanel(new GridLayout(6, 2));
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

        String[] options = {"Aceptar", "Cancelar"};
        int callResult = JOptionPane.showOptionDialog(
                mainPanel,
                callInputPanel,
                "Datos de la llamada",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null
        );
        if (callResult == JOptionPane.OK_OPTION) {
            try {
                String senderLocation = senderLocationField.getText().trim();
                String receiverCountryCode = receiverCountryCodeField.getText().trim();
                String receiverLocation = receiverLocationField.getText().trim();
                String receiverPhone = receiverPhoneField.getText().trim();
                int month = monthComboBox.getSelectedIndex() + 1;
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

                if (! (receiverCountryCode.equals("+053")  || selectedPhoneNumber.getCountryCode().equals("+053"))  ) {
                    JOptionPane.showMessageDialog(mainPanel, "Al menos uno de los dos contactos debe ser de Cuba");
                    return;
                }

                String internationalCode = !receiverCountryCode.equals("+053") ? receiverCountryCode : selectedPhoneNumber.getCountryCode();

                if(!internationalCode.equals("+053")){
                    // Crear la tarifa si esta no existe
                    List<Tax> taxes = central.getTaxes();

                    int i = 0;
                    while(i < taxes.size() && !taxes.get(i).getCountryCode().equals(internationalCode)){
                        i++;
                    }

                    if(i == taxes.size()){
                        float new_tax = (float)(Math.random()*99 + 2);
                        // Redondear la tarifa a 2 decimales
                        BigDecimal rounded_tax = new BigDecimal(new_tax);
                        new_tax = rounded_tax.setScale(2, RoundingMode.HALF_UP).floatValue();
                        central.addTax(new Tax(receiverCountryCode, central.getName(), new_tax));
                    }
                }

                if (receiverPhone.equals(selectedPhoneNumber.getNumber()) && receiverCountryCode.equals(selectedPhoneNumber.getCountryCode())) {
                    JOptionPane.showMessageDialog(mainPanel, "No se puede llamar al mismo número seleccionado");
                    return;
                }

                PhoneNumber receiverPhoneNumber = new PhoneNumber(receiverCountryCode, receiverPhone);
                Call call = new Call(
                        selectedPhoneNumber.getCountryCode(),
                        senderLocation,
                        selectedPhoneNumber.getNumber(),
                        receiverPhoneNumber.getCountryCode(),
                        receiverLocation,
                        receiverPhoneNumber.getNumber(),
                        month,
                        time
                );

                central.addCall(call);
                recentCalls.add(call);
                updateRecentCallsPanel();
                JOptionPane.showMessageDialog(mainPanel, "Llamada realizada exitosamente");
            } catch (WrongPhoneNumberFormatE ex) {
                JOptionPane.showMessageDialog(mainPanel, "Formato de número de teléfono del receptor incorrecto");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainPanel, "El tiempo debe ser un número entero positivo");
            } catch (WrongTaxCodeE ex) {
                JOptionPane.showMessageDialog(mainPanel, "El código del país debe ser de 3 dígitos");
            }
        }
    }

    private void updateRecentCallsPanel() {
        recentCallsPanel.removeAll();
        for (Call call : recentCalls) {
            float time_m = (float) call.getTime() / 60;
            // Round the time to 2 decimals
            BigDecimal rounded_time = new BigDecimal(time_m);
            rounded_time = rounded_time.setScale(2, RoundingMode.HALF_UP);
            JPanel callPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JLabel callLabel = new JLabel(
                    "De: "  + call.getSenderCountryCode() + " " + call.getSenderPhone() +
                            " A: " + call.getReceiverCountryCode() + " " + call.getReceiverPhone() +
                            " Tiempo: " + rounded_time + " min"
            );
            callLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            callPanel.add(callLabel);
            recentCallsPanel.add(callPanel);
        }
        recentCallsPanel.revalidate();
        recentCallsPanel.repaint();
    }
}