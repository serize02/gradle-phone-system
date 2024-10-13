package org.uclv.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.uclv.exceptions.WrongPeriodFormatE;
import org.uclv.exceptions.WrongPhoneNumberFormatE;
import org.uclv.models.Call;
import org.uclv.models.Central;
import org.uclv.models.PhoneNumber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class StatsPanel extends JPanel {
    private Central central;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public StatsPanel(Central central, CardLayout cardLayout, JPanel mainPanel) {
        this.central = central;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(70, 70, 70, 70));

        JLabel headerLabel = new JLabel("Estadísticas de Llamadas", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);

        JPanel controlAndChartPanel = new JPanel(new GridBagLayout());
        controlAndChartPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Panel de Control"));
        controlPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Periodo (mm-mm):"), gbc);

        JTextField periodField = new JTextField("1-12", 10);
        gbc.gridx = 1;
        controlPanel.add(periodField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("Número mínimo de llamadas:"), gbc);

        JTextField minCallsField = new JTextField("0", 5);
        gbc.gridx = 1;
        controlPanel.add(minCallsField, gbc);

        JButton showHotCountriesButton = new JButton("Mostrar Gráfico");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlPanel.add(showHotCountriesButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlAndChartPanel.add(controlPanel, gbc);

        JPanel hotProvincePanel = new JPanel(new GridBagLayout());
        hotProvincePanel.setBorder(BorderFactory.createTitledBorder("Provincia más llamada"));
        hotProvincePanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        hotProvincePanel.add(new JLabel("Período (mm-mm):"), gbc);

        JTextField hotProvincePeriodField = new JTextField("1-12", 10);
        gbc.gridx = 1;
        hotProvincePanel.add(hotProvincePeriodField, gbc);

        JButton showHotProvincesButton = new JButton("Mostrar");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        hotProvincePanel.add(showHotProvincesButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlAndChartPanel.add(hotProvincePanel, gbc);

        JPanel overpricedCallsPanel = new JPanel(new GridBagLayout());
        overpricedCallsPanel.setBorder(BorderFactory.createTitledBorder("Llamadas a Sobreprecio"));
        overpricedCallsPanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        overpricedCallsPanel.add(new JLabel("Precio máximo:"), gbc);

        JTextField maxPriceField = new JTextField("0", 5);
        gbc.gridx = 1;
        overpricedCallsPanel.add(maxPriceField, gbc);

        JButton showOverpricedCallsButton = new JButton("Mostrar");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        overpricedCallsPanel.add(showOverpricedCallsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        controlAndChartPanel.add(overpricedCallsPanel, gbc);

        JButton backButton = new JButton("Atrás");
        backButton.setBackground(new Color(220, 53, 69));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlAndChartPanel.add(backButton, gbc);

        add(controlAndChartPanel, BorderLayout.WEST);

        JPanel chartContainerPanel = new JPanel(new BorderLayout());
        chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Países más Llamados"));
        chartContainerPanel.setPreferredSize(new Dimension(800, 400)); // Adjusted size

        add(chartContainerPanel, BorderLayout.CENTER);

        displayHotCountriesChart(chartContainerPanel, "1-12", 0);

        showHotCountriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String period = periodField.getText().trim();
                int minCalls = Integer.parseInt(minCallsField.getText().trim());
                displayHotCountriesChart(chartContainerPanel, period, minCalls);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "adminPanel");
            }
        });

        showHotProvincesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String period = hotProvincePeriodField.getText().trim();
                try {
                    List<Map.Entry<String, Integer>> hotProvinces = central.getProvinces(period);
                    displayHotProvincesChart(chartContainerPanel, hotProvinces);
                } catch (WrongPeriodFormatE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Formato de período incorrecto");
                }
            }
        });

        showOverpricedCallsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float maxPrice = Float.parseFloat(maxPriceField.getText().trim());
                List<Call> overpays = central.getOverPays(maxPrice);
                displayOverpricedCallsTable(chartContainerPanel, overpays);
            }
        });
    }

    private void displayOverpricedCallsTable(JPanel chartContainerPanel, List<Call> overpays) {
        chartContainerPanel.removeAll();
        chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Tabla de Llamadas a Sobreprecio"));

        if (overpays.isEmpty()) {
            displayNoData(chartContainerPanel);
            return;
        } else {
            String[] columnNames = {"Cliente","Número del Cliente", "País del Receptor", "Localización del Receptor", "Costo de la Llamada"};
            Object[][] data = new Object[overpays.size()][5];
            for (int i = 0; i < overpays.size(); i++) {
                Call call = overpays.get(i);
                try {
                    PhoneNumber phone = new PhoneNumber(call.getSenderCountryCode(), call.getSenderPhone());
                    data[i][0] = central.getClientByPhoneNumber(phone);
                    data[i][1] = phone;
                } catch (WrongPhoneNumberFormatE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Formato de número telefónico inválido");
                }
                data[i][2] = call.getReceiverCountryCode();
                data[i][3] = call.getReceiverLocationCode();
                data[i][4] = call.getCost(central.getTaxValue(call.getReceiverCountryCode()));
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            table.setFillsViewportHeight(true);

            chartContainerPanel.add(scrollPane, BorderLayout.CENTER);
        }

        chartContainerPanel.revalidate();
        chartContainerPanel.repaint();
    }

    private void displayHotCountriesChart(JPanel chartContainerPanel, String period, int minCalls) {
        try {
            chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Países más Llamados"));
            List<Map.Entry<String, Integer>> hotCountries = central.getHotCountries(period, minCalls);

            if (hotCountries.isEmpty()) {
                displayNoData(chartContainerPanel);
                return;
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<String, Integer> entry : hotCountries) {
                dataset.addValue(entry.getValue(), "Llamadas", entry.getKey());
            }

            JFreeChart chart = ChartFactory.createLineChart(
                    "Países más llamados",
                    "Código de país",
                    "Número de llamadas",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);

            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

            chartContainerPanel.removeAll();
            chartContainerPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
            chartContainerPanel.revalidate();
            chartContainerPanel.repaint();
        } catch (WrongPeriodFormatE ex) {
            JOptionPane.showMessageDialog(mainPanel, "Formato de período incorrecto");
        }
    }

    private void displayHotProvincesChart(JPanel chartContainerPanel, List<Map.Entry<String, Integer>> hotProvinces) {
        chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Provincias más Llamadas"));
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (hotProvinces.isEmpty()) {
            displayNoData(chartContainerPanel);
            return;
        }

        for (Map.Entry<String, Integer> entry : hotProvinces) {
            dataset.addValue(entry.getValue(), "Llamadas", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Provincias más llamadas",
                "Provincia",
                "Número de llamadas",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        chartContainerPanel.removeAll();
        chartContainerPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartContainerPanel.revalidate();
        chartContainerPanel.repaint();
    }

    private void displayNoData(JPanel chartContainerPanel) {
        chartContainerPanel.removeAll();
        JLabel noDataLabel = new JLabel("No hay datos que analizar", JLabel.CENTER);
        noDataLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chartContainerPanel.setBackground(Color.WHITE);
        chartContainerPanel.add(noDataLabel, BorderLayout.CENTER);
        chartContainerPanel.revalidate();
        chartContainerPanel.repaint();
    }

}