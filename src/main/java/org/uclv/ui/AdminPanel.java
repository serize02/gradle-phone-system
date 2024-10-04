package org.uclv.ui;

import org.uclv.models.Call;
import org.uclv.models.Central;
import org.uclv.models.Client;
import org.uclv.models.PhoneNumber;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPanel extends JPanel {

    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public AdminPanel(Central central, CardLayout cardLayout, JPanel mainPanel){
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    public void init() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Header de la administración
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 20, 10); // Añadir margen inferior
        JLabel headerLabel = new JLabel("Administración de la Central: " + central.getName(), JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, gbc);

        gbc.gridy = 1;
        JLabel addressLabel = new JLabel("Dirección: " + central.getAddress(), JLabel.CENTER);
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(addressLabel, gbc);

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
                if (isRowSelected(row)) {
                    c.setBackground(getSelectionBackground());
                } else {
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
        add(new JLabel("Clientes", JLabel.CENTER), gbc);
        gbc.gridx = 1;
        add(new JLabel("Historial de Llamadas", JLabel.CENTER), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(clientScrollPane, gbc);
        gbc.gridx = 1;
        add(callScrollPane, gbc);

        // Panel para calcular ganancias
        JPanel earningsPanel = new JPanel(new GridBagLayout());
        earningsPanel.setBackground(Color.WHITE);
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
        add(earningsPanel, gbc);

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
        add(backButton, gbc);

        // Botón para ver estadísticas
        gbc.gridx = 1;
        gbc.gridy = 5;
        JButton statsButton = new JButton("Ver Estadísticas");
        statsButton.setBackground(new Color(40, 167, 69));
        statsButton.setForeground(Color.WHITE);
        statsButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        add(statsButton, gbc);

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

                    System.out.println(month + " " + operation + "    " + earnings);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Error al calcular las ganancias: " + ex.getMessage());
                }
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.add(new StatsPanel(central, cardLayout, mainPanel), "statsPanel");
                cardLayout.show(mainPanel, "statsPanel");
            }
        });
    }
}
