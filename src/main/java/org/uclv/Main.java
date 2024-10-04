package org.uclv;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.uclv.exceptions.*;
import org.uclv.models.*;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static Central central;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;

    public static void main(String[] args) {
        // Inicializar datos de ejemplo
        central = new Central("5301", "123 Main St.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try{
            central.addTax(new Tax("+43", "5301", 0.1f));
        } catch (Exception TaxAlreadyExists){
            JOptionPane.showMessageDialog(mainPanel, "Tax Already Exists");
        }
        // Crear la ventana de inicio
        JFrame frame = new JFrame("Sistema de Gestión Telefónica");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar la ventana

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear panel de bienvenida
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome to Phone System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        welcomePanel.add(welcomeLabel, gbc);

        JButton adminLoginButton = new JButton("Admin Login");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        welcomePanel.add(adminLoginButton, gbc);

        JButton clientRegisterButton = new JButton("Client Register");
        gbc.gridx = 1;
        welcomePanel.add(clientRegisterButton, gbc);

        mainPanel.add(welcomePanel, "welcomePanel");

        // Acción del botón de Admin Login
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdminLoginPanel();
            }
        });

        // Acción del botón de Client Register
        clientRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClientRegisterPanel();
            }
        });

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }
    // Mostrar panel de registro de cliente
    private static void showClientRegisterPanel() {
        JPanel clientRegisterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Client Code:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        clientRegisterPanel.add(codeLabel, gbc);

        JTextField codeField = new JTextField(15);
        gbc.gridx = 1;
        clientRegisterPanel.add(codeField, gbc);

        JLabel typeLabel = new JLabel("Client Type:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        clientRegisterPanel.add(typeLabel, gbc);

        String[] clientTypes = {"Estatal", "Particular"};
        JComboBox<String> typeComboBox = new JComboBox<>(clientTypes);
        gbc.gridx = 1;
        clientRegisterPanel.add(typeComboBox, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        clientRegisterPanel.add(registerButton, gbc);

        JLabel loginLabel = new JLabel("¿Ya estás registrado? Inicia sesión aquí");
        loginLabel.setForeground(Color.BLUE.darker());
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        clientRegisterPanel.add(loginLabel, gbc);

        JButton backButton = new JButton("Back");
        gbc.gridy = 4;
        clientRegisterPanel.add(backButton, gbc);

        mainPanel.add(clientRegisterPanel, "clientRegisterPanel");
        cardLayout.show(mainPanel, "clientRegisterPanel");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                char typeChar = type.equals("Estatal") ? 'E' : 'P';
                try {
                    central.verifyClient(code);
                    JOptionPane.showMessageDialog(mainPanel, "El cliente ya existe en el sistema");
                } catch (InvalidCredentialsE ex) {
                    Client newClient = new Client(code, typeChar);
                    central.addClient(newClient);
                    JOptionPane.showMessageDialog(mainPanel, "Cliente registrado exitosamente");
                    showClientPanel(newClient);
                }
            }
        });

        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showClientLoginPanel();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "welcomePanel"); // Volver a la pantalla de bienvenida
            }
        });
    }

    // Mostrar panel de autenticación de cliente
    private static void showClientLoginPanel() {
        JPanel clientLoginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Client Code:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        clientLoginPanel.add(codeLabel, gbc);

        JTextField codeField = new JTextField(15);
        gbc.gridx = 1;
        clientLoginPanel.add(codeField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        clientLoginPanel.add(loginButton, gbc);

        JButton backButton = new JButton("Back");
        gbc.gridy = 2;
        clientLoginPanel.add(backButton, gbc);

        mainPanel.add(clientLoginPanel, "clientLoginPanel");
        cardLayout.show(mainPanel, "clientLoginPanel");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeField.getText();
                try {
                    Client client = central.verifyClient(code);
                    showClientPanel(client);
                } catch (InvalidCredentialsE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Código de cliente incorrecto");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "welcomePanel"); // Volver a la pantalla de bienvenida
            }
        });
    }

    // Mostrar panel de autenticación de administrador
    private static void showAdminLoginPanel() {
        JPanel adminLoginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Admin Code:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        adminLoginPanel.add(codeLabel, gbc);

        JTextField codeField = new JTextField(15);
        gbc.gridx = 1;
        adminLoginPanel.add(codeField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        adminLoginPanel.add(loginButton, gbc);

        JButton backButton = new JButton("Back");
        gbc.gridy = 2;
        adminLoginPanel.add(backButton, gbc);

        mainPanel.add(adminLoginPanel, "adminLoginPanel");
        cardLayout.show(mainPanel, "adminLoginPanel");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeField.getText();
                if (code.equals("admin")) {
                    showAdminDetailsPanel();
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Código de administrador incorrecto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "welcomePanel"); // Volver a la pantalla de bienvenida
            }
        });
    }

    private static void showAdminDetailsPanel() {
        JPanel adminDetailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Header de la administración
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 20, 10); // Añadir margen inferior
        JLabel headerLabel = new JLabel("Administración de la Central: " + central.getName(), JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        adminDetailsPanel.add(headerLabel, gbc);

        gbc.gridy = 1;
        JLabel addressLabel = new JLabel("Dirección: " + central.getAddress(), JLabel.CENTER);
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        adminDetailsPanel.add(addressLabel, gbc);

        // Panel para la lista de clientes
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10); // Restablecer márgenes
        String[] clientColumns = {"Código", "Tipo", "Números de Teléfono"};
        Object[][] clientData = new Object[central.getClients().size()][3];
        int i = 0;
        for (Client client : central.getClients()) {
            clientData[i][0] = client.getCode();
            clientData[i][1] = client.getType() == 'E' ? "Estatal" : "Particular";
            clientData[i][2] = String.join(", ", client.getPhoneNumbers().stream().map(PhoneNumber::toString).toArray(String[]::new));
            i++;
        }
        JTable clientTable = new JTable(clientData, clientColumns);
        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientTable.setFillsViewportHeight(true);

        // Panel para el historial de llamadas
        String[] callColumns = {"Emisor", "Receptor", "Duración (segundos)"};
        Object[][] callData = new Object[central.getCalls().size()][3];
        int j = 0;
        for (Call call : central.getCalls()) {
            callData[j][0] = call.getSenderPhone();
            callData[j][1] = call.getReceiverPhone();
            callData[j][2] = call.getTime();
            j++;
        }

        JTable callTable = new JTable(callData, callColumns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? getBackground() : new Color(240, 240, 240));
                }
                return c;
            }
        };
        JScrollPane callScrollPane = new JScrollPane(callTable);
        callTable.setFillsViewportHeight(true);

        // Agregar encabezados y tablas al panel principal
        gbc.gridx = 0;
        gbc.gridy = 3;
        adminDetailsPanel.add(new JLabel("Clientes", JLabel.CENTER), gbc);
        gbc.gridx = 1;
        adminDetailsPanel.add(new JLabel("Historial de Llamadas", JLabel.CENTER), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        adminDetailsPanel.add(clientScrollPane, gbc);
        gbc.gridx = 1;
        adminDetailsPanel.add(callScrollPane, gbc);

        // Panel para calcular ganancias
        JPanel earningsPanel = new JPanel(new GridBagLayout());
        earningsPanel.setBorder(BorderFactory.createTitledBorder("Calcular Ganancias"));
        GridBagConstraints epGbc = new GridBagConstraints();
        epGbc.insets = new Insets(5, 5, 5, 5);
        epGbc.fill = GridBagConstraints.HORIZONTAL;

        epGbc.gridx = 0;
        epGbc.gridy = 0;
        epGbc.gridwidth = 2;
        earningsPanel.add(new JLabel("Seleccione el mes y la operación para calcular las ganancias."), epGbc);

        epGbc.gridwidth = 1;
        epGbc.gridy = 1;
        earningsPanel.add(new JLabel("Mes:"), epGbc);

        String[] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        epGbc.gridx = 1;
        earningsPanel.add(monthComboBox, epGbc);

        epGbc.gridx = 0;
        epGbc.gridy = 2;
        earningsPanel.add(new JLabel("Operación:"), epGbc);

        String[] operations = {"Nacional", "Internacional", "Total"};
        JComboBox<String> operationComboBox = new JComboBox<>(operations);
        epGbc.gridx = 1;
        earningsPanel.add(operationComboBox, epGbc);

        JButton calculateButton = new JButton("Calcular Ganancias");
        calculateButton.setBackground(new Color(0, 123, 255));
        calculateButton.setForeground(Color.WHITE);
        epGbc.gridx = 0;
        epGbc.gridy = 3;
        epGbc.gridwidth = 2;
        earningsPanel.add(calculateButton, epGbc);

        JLabel earningsLabel = new JLabel();
        epGbc.gridy = 4;
        earningsPanel.add(earningsLabel, epGbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        adminDetailsPanel.add(earningsPanel, gbc);

        // Botón de regreso
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton backButton = new JButton("Atrás");
        backButton.setBackground(new Color(220, 53, 69));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        adminDetailsPanel.add(backButton, gbc);

        // Botón para ver estadísticas
        gbc.gridx = 1;
        gbc.gridy = 5;
        JButton statsButton = new JButton("Ver Estadísticas");
        statsButton.setBackground(new Color(40, 167, 69));
        statsButton.setForeground(Color.WHITE);
        statsButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        adminDetailsPanel.add(statsButton, gbc);

        mainPanel.add(adminDetailsPanel, "adminDetailsPanel");
        cardLayout.show(mainPanel, "adminDetailsPanel");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "adminLoginPanel"); // Volver a la pantalla de autenticación de administrador
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int month = monthComboBox.getSelectedIndex() + 1;
                    int operation = operationComboBox.getSelectedIndex() + 1;
                    float earnings = central.getMonthEarning(operation, month);
                    earningsLabel.setText("Ganancias: " + earnings);
                } catch (InvalidMonthE | InvalidOperationE ex) {
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
                }
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatsPanel();
            }
        });
    }

    private static void showStatsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Encabezado de estadísticas
        JLabel headerLabel = new JLabel("Estadísticas de Llamadas", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statsPanel.add(headerLabel, BorderLayout.NORTH);

        // Panel de control y gráfico
        JPanel controlAndChartPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel de control
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Panel de Control"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Periodo (mm-yyyy):"), gbc);

        JTextField periodField = new JTextField("1-12", 10);
        gbc.gridx = 1;
        controlPanel.add(periodField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("Número mínimo de llamadas:"), gbc);

        JTextField minCallsField = new JTextField("0", 5);
        gbc.gridx = 1;
        controlPanel.add(minCallsField, gbc);

        JButton showChartButton = new JButton("Mostrar Gráfico");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(showChartButton, gbc);

        JButton backButton = new JButton("Atrás");
        backButton.setBackground(new Color(220, 53, 69));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        gbc.gridy = 3;
        controlPanel.add(backButton, gbc);

        // Panel para el gráfico
        JPanel chartContainerPanel = new JPanel(new BorderLayout());
        chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Llamadas"));
        chartContainerPanel.setPreferredSize(new Dimension(800, 600)); // Adjusted size

        // Añadir paneles al panel de control y gráfico
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        controlAndChartPanel.add(controlPanel, gbc);

        gbc.gridx = 1;
        controlAndChartPanel.add(chartContainerPanel, gbc);

        statsPanel.add(controlAndChartPanel, BorderLayout.CENTER);

        mainPanel.add(statsPanel, "statsPanel");
        cardLayout.show(mainPanel, "statsPanel");

        // Initial chart display
        displayChart(chartContainerPanel, "1-12", 0);

        showChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String period = periodField.getText().trim();
                int minCalls = Integer.parseInt(minCallsField.getText().trim());
                displayChart(chartContainerPanel, period, minCalls);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "adminDetailsPanel");
            }
        });
    }

    private static void displayChart(JPanel chartContainerPanel, String period, int minCalls) {
        try {
            List<Map.Entry<String, Integer>> hotCountries = central.getHotCountries(period, minCalls);

            if (hotCountries.isEmpty()) {
                chartContainerPanel.removeAll();
                JLabel noDataLabel = new JLabel("No hay datos que analizar", JLabel.CENTER);
                noDataLabel.setFont(new Font("Arial", Font.BOLD, 18));
                chartContainerPanel.add(noDataLabel, BorderLayout.CENTER);
                chartContainerPanel.revalidate();
                chartContainerPanel.repaint();
                return;
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : hotCountries) {
                dataset.addValue(entry.getValue(), "Llamadas", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    "Hot Countries",
                    "Country Code",
                    "Number of Calls",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE); // Set background to white

            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED); // Use Color.RED
            renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Set line thickness
            plot.setRenderer(renderer);

            chartContainerPanel.removeAll();
            chartContainerPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
            chartContainerPanel.revalidate();
            chartContainerPanel.repaint();
        } catch (WrongPeriodFormatE ex) {
            JOptionPane.showMessageDialog(mainPanel, "Formato de período incorrecto");
        }
    }

    // Mostrar interfaz de cliente

    private static void showClientPanel(Client client) {
        JPanel clientPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Tipo de Cliente: " + (client.getType() == 'E' ? "Estatal" : "Particular"));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        clientPanel.add(typeLabel, gbc);

        if (client.getPhoneNumbers() != null) {
            JLabel phoneHeader = new JLabel("Números de Teléfono:");
            phoneHeader.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridy = 1;
            clientPanel.add(phoneHeader, gbc);

            gbc.gridwidth = 1;
            gbc.gridy = 2;
            for (PhoneNumber phoneNumber : client.getPhoneNumbers()) {
                JLabel phoneLabel = new JLabel(phoneNumber.toString());
                phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 0;
                clientPanel.add(phoneLabel, gbc);

                JButton deleteButton = new JButton("X");
                deleteButton.setPreferredSize(new Dimension(20, 20));
                deleteButton.setMinimumSize(new Dimension(20, 20));
                deleteButton.setMaximumSize(new Dimension(20, 20));
                deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
                deleteButton.setForeground(Color.WHITE);
                deleteButton.setBackground(Color.RED);
                deleteButton.setBorderPainted(false);
                deleteButton.setFocusPainted(false);
                gbc.gridx = 1;
                clientPanel.add(deleteButton, gbc);

                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.removePhoneNumber(phoneNumber);
                            JOptionPane.showMessageDialog(mainPanel, "Número de teléfono eliminado exitosamente");
                            showClientPanel(client); // Refrescar el panel del cliente
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Error al eliminar el número de teléfono");
                        }
                    }
                });

                gbc.gridy++;
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addPhoneNumberButton = new JButton("Agregar Número de Teléfono");
        addPhoneNumberButton.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(addPhoneNumberButton);

        JButton makeCallButton = new JButton("Realizar Llamada");
        makeCallButton.setFont(new Font("Arial", Font.PLAIN, 14));
        makeCallButton.setBackground(Color.GREEN);
        buttonPanel.add(makeCallButton);

        JButton backButton = new JButton("Atrás");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        clientPanel.add(buttonPanel, gbc);

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
                        showClientPanel(client); // Refrescar el panel del cliente
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
                cardLayout.show(mainPanel, "welcomePanel"); // Volver a la pantalla de bienvenida
            }
        });

        mainPanel.add(clientPanel, "clientPanel");
        cardLayout.show(mainPanel, "clientPanel");
    }

    private static void showManageClientsPanel() {
        JPanel manageClientsPanel = new JPanel(new BorderLayout());
        JTextArea clientList = new JTextArea(20, 30);
        clientList.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(clientList);
        JButton backButton = new JButton("Atrás");

        manageClientsPanel.add(scrollPane, BorderLayout.CENTER);
        manageClientsPanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(manageClientsPanel, "manageClientsPanel");
        cardLayout.show(mainPanel, "manageClientsPanel");

        StringBuilder clientsData = new StringBuilder();
        for (Client client : central.getClients()) {
            clientsData.append(client.getCode()).append("\n");
        }
        clientList.setText(clientsData.toString());

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "adminPanel"); // Volver a la pantalla de administrador
            }
        });
    }
}