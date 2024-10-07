package org.uclv.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.uclv.exceptions.WrongPeriodFormatE;
import org.uclv.models.Central;

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
        setBorder(BorderFactory.createEmptyBorder(70,70,70,70));

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlAndChartPanel.add(controlPanel, gbc);

        JPanel hotProvincePanel = new JPanel(new GridBagLayout());
        hotProvincePanel.setBorder(BorderFactory.createTitledBorder("Provincia más llamada"));
        hotProvincePanel.setBackground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        hotProvincePanel.add(new JLabel("Periodo (mm-mm):"), gbc);

        JTextField hotProvincePeriodField = new JTextField("1-12", 10);
        gbc.gridx = 1;
        hotProvincePanel.add(hotProvincePeriodField, gbc);

        JButton showHotProvinceButton = new JButton("Mostrar");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        hotProvincePanel.add(showHotProvinceButton, gbc);

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
        chartContainerPanel.setBorder(BorderFactory.createTitledBorder("Gráfico de Llamadas"));
        chartContainerPanel.setPreferredSize(new Dimension(800, 400)); // Adjusted size

        add(chartContainerPanel, BorderLayout.CENTER);

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
                cardLayout.show(mainPanel, "adminPanel");
            }
        });

        showHotProvinceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String period = hotProvincePeriodField.getText().trim();
                try {
                    List<Map.Entry<String, Integer>> hotCountries = central.getHotCountries(period, 0);
                    if (hotCountries.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "No hay datos que analizar");
                    } else {
                        // Handle the display of hot countries
                    }
                } catch (WrongPeriodFormatE ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Formato de período incorrecto");
                }
            }
        });

        showOverpricedCallsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float maxPrice = Float.parseFloat(maxPriceField.getText().trim());
                List<String> overpays = central.getOverPays(maxPrice);
                if (overpays.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "No hay llamadas para mostrar");
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Llamadas a sobreprecio: " + String.join(", ", overpays));
                }
            }
        });
    }

    private void displayChart(JPanel chartContainerPanel, String period, int minCalls) {
        try {
            List<Map.Entry<String, Integer>> hotCountries = central.getHotCountries(period, minCalls);

            if (hotCountries.isEmpty()) {
                chartContainerPanel.removeAll();
                JLabel noDataLabel = new JLabel("No hay datos que analizar", JLabel.CENTER);
                noDataLabel.setFont(new Font("Arial", Font.BOLD, 18));
                chartContainerPanel.setBackground(Color.WHITE);
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
}