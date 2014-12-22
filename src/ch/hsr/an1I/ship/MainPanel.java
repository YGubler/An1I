package ch.hsr.an1I.ship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.hsr.util.*;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

    private abstract class Exercise1and2 implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            MainPanel.this.graphics.clear();
            double speedOnWater;
            for (speedOnWater = 0; speedOnWater <= 22; speedOnWater += 0.05) {
                double sol = getFuelPerHourSolution(speedOnWater);
                System.out.println("Der Parameter speedOnWater=" + speedOnWater
                        + " ergibt den Wert " + sol);
                double masterSol = getFuelPerHourSolutionReference(speedOnWater);
                MainPanel.this.graphics
                        .addPoint(Color.green, speedOnWater, sol);
                MainPanel.this.graphics.addPoint(Color.blue, speedOnWater,
                        masterSol);
                if (sol != masterSol) {
                    MainPanel.this.onError("Der Parameter speedOnWater="
                            + speedOnWater + " ergibt den Wert " + sol
                            + " anstelle des Werts " + masterSol);
                }
            }
            MainPanel.this.reportResult();
            for (int i = 0; i < MainPanel.this.masterSolution.speedOnSurfaceTable.length; i++) {
                MainPanel.this.graphics.addPoint(Color.red,
                        MainPanel.this.masterSolution.speedOnSurfaceTable[i],
                        MainPanel.this.masterSolution.fuelPerHourTable[i]);
            }
            MainPanel.this.graphics.show();

        }

        abstract double getFuelPerHourSolution(double speedOnWater);

        abstract double getFuelPerHourSolutionReference(double speedOnWater);
    }

    private class Exercise1 extends Exercise1and2 {

        double getFuelPerHourSolution(double speedOnWater) {
            return MainPanel.this.solution.getFuelPerHourLinear(speedOnWater);
        }

        double getFuelPerHourSolutionReference(double speedOnWater) {
            return MainPanel.this.masterSolution
                    .getFuelPerHourLinear(speedOnWater);
        }

    }

    private class Exercise2 extends Exercise1and2 {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            MainPanel.this.solution.calculateCubicSplineParameters();
            MainPanel.this.masterSolution.calculateCubicSplineParameters();
            super.actionPerformed(arg0);
        }

        double getFuelPerHourSolution(double speedOnWater) {
            return MainPanel.this.solution.getFuelPerHourCubic(speedOnWater);
        }

        double getFuelPerHourSolutionReference(double speedOnWater) {
            return MainPanel.this.masterSolution
                    .getFuelPerHourCubic(speedOnWater);
        }

    }

    private class Exercise3 implements ActionListener, Steering, Sensor {

        private double speedOnSurface;
        private double gpsSpeed;
        private double fuelPerHour;
        private double optSpeed;
        private Random rnd = new Random();

        @Override
        public void actionPerformed(ActionEvent arg0) {
            MainPanel.this.solution.initialize();
            MainPanel.this.masterSolution.initialize();

            speedOnSurface = 22 * rnd.nextDouble();
            gpsSpeed = (2 * rnd.nextDouble() - 1) * 10 + speedOnSurface;
            fuelPerHour = MainPanel.this.masterSolution
                    .getFuelPerHourCubic(speedOnSurface);
            System.out
                    .println("Die Strömungsgeschwindigkeit des Wassers beträgt "
                            + (gpsSpeed - speedOnSurface));
            MainPanel.this.graphics.clear();
            double speedOnWater;
            for (speedOnWater = 0.1; speedOnWater <= 22; speedOnWater += 0.01) {
                double masterSol = MainPanel.this.masterSolution
                        .getFuelPerHourCubic(speedOnWater)
                        / (speedOnWater + gpsSpeed - speedOnSurface);
                MainPanel.this.graphics.addPoint(Color.blue, speedOnWater,
                        masterSol);
            }
            optSpeed = -1;

            MainPanel.this.masterSolution.onSensorDataAvailable(this, this);
            MainPanel.this.solution.onSensorDataAvailable(this, this);
            MainPanel.this.graphics.show();
        }

        @Override
        public double getSpeedOnSurface() {
            return speedOnSurface;
        }

        @Override
        public double getAbsGpsSpeed() {
            return gpsSpeed;
        }

        @Override
        public double getFuelPerHour() {
            return fuelPerHour;
        }

        @Override
        public void setSpeedOnSurface(double speed) {
            if (optSpeed == -1) {
                optSpeed = speed;
                MainPanel.this.hasError = false;
            } else {
                double minFuel = MainPanel.this.masterSolution
                        .getFuelPerHourCubic(speed)
                        / (speed + gpsSpeed - speedOnSurface);
                double minFuelOpt = MainPanel.this.masterSolution
                        .getFuelPerHourCubic(optSpeed)
                        / (optSpeed + gpsSpeed - speedOnSurface);
                if (/* speed != optSpeed */Math.abs(minFuel - minFuelOpt)
                        / minFuelOpt > 0.01) {
                    MainPanel.this
                            .onError("Der optimale Geschwindigkeitswert wird mit "
                                    + speed
                                    + " angegeben. Richtig wäre "
                                    + optSpeed);
                    MainPanel.this
                            .onError("Die zugehörigen Verbrauchswerte sind "
                                    + minFuel + " statt " + minFuelOpt);
                }
                System.out.println("Bei der optimalen Geschwindigkeit " + speed
                        + " beträgt der Verbrauch " + minFuel + " l/km");
                MainPanel.this.graphics.addPoint(Color.red, speed, minFuel);
                MainPanel.this.reportResult();
                optSpeed = -1;
            }
        }
    }

    private int counter = 1;
    private Solution solution;
    private SolutionReference masterSolution;
    private boolean hasError;
    private String report;
    private JTextArea testReport;
    private JTextField testResult;
    private JPanel container;
    private Graph graphics;

    public MainPanel(boolean test) {
        report = new String();
        setLayout(new BorderLayout());
        container = new JPanel();
        add(container, BorderLayout.NORTH);
        if (test)
            add(new JTextField("ACHTUNG: TESTLAUF"), BorderLayout.WEST);

        append(new Exercise1());
        append(new Exercise2());
        append(new Exercise3());

        solution = test ? new SolutionReference() /* SolutionFalsch() */
        : new Solution();
        masterSolution = new SolutionReference();
        hasError = false;
        testReport = new JTextArea(25, 80);
        testResult = new JTextField(85);

        add(new JScrollPane(testReport), BorderLayout.CENTER);
        add(testResult, BorderLayout.SOUTH);
        graphics = new Graph();
        graphics.setPreferredSize(new Dimension(400, 200));
        add(graphics, BorderLayout.EAST);

    }

    public void reportResult() {
        // TODO Auto-generated method stub
        if (hasError)
            testResult.setText("Der Testlauf wurde mit Fehlern beendet");
        else
            testResult.setText("Der Testlauf war erfolgreich");
    }

    public void onError(String string) {
        hasError = true;
        report = report + string + "\n";
        testReport.setText(report);
    }

    private void append(ActionListener exercise) {

        JButton button = new JButton("Aufgabe " + counter++);
        button.addActionListener(exercise);
        container.add(button);
    }

    public static void main(String[] args) {
        boolean test = false;
        if (args.length > 0 && args[0].equals("test"))
            test = true;
        JFrame frame = new JFrame("Analysis 1 für Informatiker - Lastschiff");
        frame.getContentPane().add(new MainPanel(test));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
