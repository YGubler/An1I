package ch.hsr.an1I.ship;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialsUtils;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;

import ch.hsr.util.PolynomDienste;

/* Klasse, in der die Lösungen der Aufgaben implementiert werden sollen. */
public class Solution {

    /************************** WERTETABELLE *******************************/

    /* speedOnSurface Daten aus der Wertetabelle */
    public final double[] speedOnSurfaceTable = { 0, 3, 5, 7.5, 8, 11, 14, 18,
            20, 22 }; // Diese Daten müssen für die Spline
                      // Algorithmen der Grösse nach zu sortieren
    /* absGpsSpeed Daten aus der Wertetabelle */
    public final double[] absGpsSpeedTable = { 0, 3.5, 2.8, 12.3, 11.2, 11.6,
            12.8, 13.2, 21.2, 24.8 };
    /* fuelPerHour Daten aus der Wertetabelle */
    public final double[] fuelPerHourTable = { 80, 90, 105, 140, 131, 162, 197,
            251, 280, 310 };

    /**************************** AUFGABE 1 ********************************/

    /*
     * Gibt den Treibstoffverbrauch pro Stunde zurück, wenn das Schiff relativ
     * zur Wasseroberfläche mit der Geschwindigkeit speedOnSurface fährt. Die
     * Wertetabelle wird in dieser Methode als lineares Spline approximiert.
     */
    public double getFuelPerHourLinear(double speedOnSurface) {
    	 if (speedOnSurface < 0)
             throw new IllegalArgumentException("speedOnSurface ist negativ");
         if (speedOnSurface > speedOnSurfaceTable[speedOnSurfaceTable.length - 1]) {
             throw new IllegalArgumentException("speedOnSurface ist zu gross");
         }

         // suche die Position von speedOnSurface in der Tabelle
         int i;
         for (i = 0; i < speedOnSurfaceTable.length; i++) {
             if (speedOnSurfaceTable[i] > speedOnSurface)
                 break;
         }
         // i zeigt jetzt auf das erste Element, welches grösser als
         // speedOnSurface ist,
         // oder im Fall dass speedOnSurface der Maximalwert ist auf
         // speedOnSurfaceTable.length
         // korrigiere in diesem Fall den Wert von i um eine Einheit nach unten
         if (i == speedOnSurfaceTable.length)
             i = speedOnSurfaceTable.length - 1;

         // bestimme die Randpunkte des Geradensegments
         double sLo = speedOnSurfaceTable[i - 1];
         double sHi = speedOnSurfaceTable[i];
         double fLo = fuelPerHourTable[i - 1];
         double fHi = fuelPerHourTable[i];

         // Löse das Gleichungssystem
         // a*sLo + b = fLo
         // a*sHi + b = fHi
         double a = (fHi - fLo) / (sHi - sLo);
         double b = fLo - a * sLo;

         // Berechne den Funktionswert
         return a * speedOnSurface + b;
    }

    /**************************** AUFGABE 2 ********************************/
    
    /* Liste der Knotenpunkte des Splines */
    private double[] x;

    /* Liste der kubischen Polynomkoeffizienten */
    private double[] a;

    /* Liste der quadratischen Polynomkoeffizienten */
    private double[] b;

    /* Liste der linearen Polynomkoeffizienten */
    private double[] c;

    /* Liste der konstanten Polynomkoeffizienten */
    private double[] d;

    private void calculateCubicSplineParameters(double[] xArg, double[] yArg) {
        // 0. Schritt: Precondition Checks
        if (xArg.length != yArg.length) {
            throw new IllegalArgumentException(
                    "Argument- und Werteliste sind nicht gleich gross.");
        }
        if (xArg.length < 2) {
            throw new IllegalArgumentException(
                    "Die Argument- und Werteliste enthalten weniger als 2 Elemente.");
        }
        // 1. Schritt: Initialisiere die Matrixwerte
        int numPolynoms = xArg.length - 1;
        double[] h = new double[numPolynoms];
        double[] y = new double[numPolynoms];
        for (int i = 0; i < xArg.length - 1; i++) {
            h[i] = xArg[i + 1] - xArg[i];
            y[i] = yArg[i + 1] - yArg[i];
        }
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(3 * numPolynoms,
                3 * numPolynoms);
        for (int row = 0; row < numPolynoms; row++) {
            // a[i]*h[i]^{3}+b[i]*h[i]^{2}+c[i]*h[i] = y[i]
            matrix.setEntry(row, 3 * row, Math.pow(h[row], 3));
            matrix.setEntry(row, 3 * row + 1, Math.pow(h[row], 2));
            matrix.setEntry(row, 3 * row + 2, h[row]);
        }
        for (int row = 0; row < numPolynoms - 1; row++) {
            // 3*a[i]*h[i]^{2}+2*b[i]*h[i]+c[i]-c[i+1]=0
            matrix.setEntry(numPolynoms + row, 3 * row, 3 * Math.pow(h[row], 2));
            matrix.setEntry(numPolynoms + row, 3 * row + 1, 2 * h[row]);
            matrix.setEntry(numPolynoms + row, 3 * row + 2, 1);
            matrix.setEntry(numPolynoms + row, 3 * row + 2 + 3, -1);
        }
        for (int row = 0; row < numPolynoms - 1; row++) {
            // 3*a[i]h[i]+b[i]-b[i+1]=0
            matrix.setEntry(2 * numPolynoms - 1 + row, 3 * row, 3 * h[row]);
            matrix.setEntry(2 * numPolynoms - 1 + row, 3 * row + 1, 1);
            matrix.setEntry(2 * numPolynoms - 1 + row, 3 * row + 1 + 3, -1);
        }
        // b[0] == 0
        matrix.setEntry(3 * numPolynoms - 2, 1, 1);
        // 3a[k-1]*h[k-1]+b[k-1] = 0
        matrix.setEntry(3 * numPolynoms - 1, 3 * numPolynoms - 3,
                3 * h[numPolynoms - 1]);
        matrix.setEntry(3 * numPolynoms - 1, 3 * numPolynoms - 2, 1);

        // 2. Schritt: Löse das Gleichungssystem
        double[] vectorData = new double[3 * numPolynoms];
        for (int i = 0; i < numPolynoms; i++)
            vectorData[i] = y[i];
        RealVector vector = new ArrayRealVector(vectorData);
        LUDecomposition decomposition = new LUDecomposition(matrix);
        vector = decomposition.getSolver().solve(vector);

        // 3. Schritt: Extrahiere die Werte aus vector
        x = xArg;
        d = yArg;
        a = new double[numPolynoms];
        b = new double[numPolynoms];
        c = new double[numPolynoms];
        for (int i = 0; i < numPolynoms; i++) {
            a[i] = vector.getEntry(3 * i);
            b[i] = vector.getEntry(3 * i + 1);
            c[i] = vector.getEntry(3 * i + 2);
        }
        /*
         * x=xArg; y=yArg;
         * 
         * c = new double[x.length]; // die Länge der c Liste ist um 1 kürzer
         * als die Länge der Wertetabelle // 1. Schritt: Bestimmen der
         * Initialwerte c[0] = 3.67561;//2.08033;// (y[1]-y[0])/(x[1]-x[0]); //
         * beliebig c[1] = 3*(y[1]-y[0])/(x[1]-x[0]) - 2*c[0];
         * 
         * // 2. Schritt: Induktive Berechnung der übrigen Werte int i; for
         * (i=1; i < x.length - 2; i++){ double tmp =
         * 3*(y[i]-y[i-1])/Math.pow(x[i]-x[i-1],
         * 2)+3*(y[i+1]-y[i])/Math.pow(x[i+1]-x[i], 2); tmp = tmp - (c[i-1] +
         * 2*c[i])/(x[i]-x[i-1]); c[i+1] = tmp * (x[i+1]-x[i]) - 2 *c[i]; } c[i]
         * = 3*(y[i-1]-y[i-2])/(x[i-1]-x[i-2]) - 2*c[i-1];
         */
    }

    private double calculateCubicSplineValue(double arg) {
        /************************* COPY PASTE CODE VON AUFGABE 1 ********************************/
        /*****
         * IN DER PRAXIS SOLLTE DIESER CODE IN EINE EIGENE FUNKTION AUSGELAGERT
         * WERDEN
         ******/
        if (arg < x[0])
            throw new IllegalArgumentException(
                    "Das Spline-Argument ist kleiner als der Minimalwert");
        if (arg > x[x.length - 1]) {
            throw new IllegalArgumentException(
                    "Das Spline-Argument ist grösser als der Maximalwert");
        }

        // suche die Position von speedOnSurface in der Tabelle
        int i;
        for (i = 0; i < x.length; i++) {
            if (x[i] > arg)
                break;
        }
        // i zeigt jetzt auf das erste Element, welches grösser als arg ist,
        // oder im Fall dass arg der Maximalwert ist auf arg.length
        // korrigiere in diesem Fall den Wert von i um eine Einheit nach unten
        if (i == x.length)
            i = x.length - 1;
        /************************** ENDE DES COPY PASTE CODES ***********************************/

        // an dieser Stelle gilt sicher speedOnSurfaceTable[i] >= speedOnSurface
        i = i - 1; // Der Index muss um 1 nach links korrigiert werden, damit
                   // die Bedingung
                   // speedOnSurfaceTable[i] <= speedOnSurface <=
                   // speedOnSurfaceTable[i+1]
                   // erfüllt ist

        // Berechne den Funktionswert
        return a[i] * Math.pow(arg - x[i], 3) + b[i] * Math.pow(arg - x[i], 2)
                + c[i] * (arg - x[i]) + d[i];

        /*
         * double t = (arg - x[i])/(x[i+1] - x[i]); return
         * Math.pow(1-t,2)*y[i]+Math
         * .pow(t,2)*y[i+1]+t*(1-t)*((1-t)*(2*y[i]+(x[i+
         * 1]-x[i])*c[i])+t*(2*y[i+1]-(x[i+1]-x[i])*c[i+1]));
         */
    }

    public void calculateCubicSplineParameters() {
        // Wir delegieren die Initialisierung des kubischen Splines an eine
        // generische Funktion
        // so dass die Berechnung des kubischen Splines wiederverwertet werden
        // könnte
        calculateCubicSplineParameters(speedOnSurfaceTable, fuelPerHourTable);
    }

    public double getFuelPerHourCubic(double speedOnSurface) {
        // Wir delegieren die Initialisierung des kubischen Splines an eine
        // generische Funktion
        // so dass die Berechnung des kubischen Splines wiederverwertet werden
        // könnte
        return calculateCubicSplineValue(speedOnSurface);
    }


//    /* Liste der Knotenpunkte des Splines */
//    private double[] x;
//
//    /* Liste der kubischen Polynomkoeffizienten */
//    private double[] a;
//
//    /* Liste der quadratischen Polynomkoeffizienten */
//    private double[] b;
//
//    /* Liste der linearen Polynomkoeffizienten */
//    private double[] c;
//
//    private void calculateCubicSplineParameters(double[] xArg, double[] yArg) {
//        // 0. Schritt: Precondition überprüfen
//        // TODO Preconditions
//
//        // 1. Schritt: Initialisiere die Matrixwerte
//        int numPolynoms = xArg.length - 1;
//        double[] h = new double[numPolynoms];
//        double[] y = new double[numPolynoms];
//        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(3 * numPolynoms,
//                3 * numPolynoms);
//        // TODO Initialisiere die Matrixwerte
//
//        // 2. Schritt: Löse das Gleichungssystem LU - Decomposition ist eine
//        // Methode lineare Gleichungssysteme zu lösen
//        double[] vectorData = new double[3 * numPolynoms];
//        for (int i = 0; i < numPolynoms; i++)
//            vectorData[i] = y[i];
//        RealVector vector = new ArrayRealVector(vectorData);
//        LUDecomposition decomposition = new LUDecomposition(matrix);
//        vector = decomposition.getSolver().solve(vector);
//
//        // 3. Schritt: Extrahiere die Werte aus vector
//        // TODO implementieren Sie diese Funktion in Aufgabe 2
//        throw new RuntimeException("Aufgabe 2 ist noch nicht implementiert");
//    }
//
//    /* Initialisierung der Parametertabellen des kubischen Splines */
//    public void calculateCubicSplineParameters() {
//        // Wir delegieren die Initialisierung des kubischen Splines an eine
//        // generische Funktion
//        // so dass die Berechnung des kubischen Splines wiederverwertet werden
//        // könnte
//        calculateCubicSplineParameters(speedOnSurfaceTable, fuelPerHourTable);
//    }
//
//    /*
//     * Gibt den Treibstoffverbrauch pro Stunde zurück, wenn das Schiff relativ
//     * zur Wasseroberfläche mit der Geschwindigkeit speedOnSurface fährt. Die
//     * Wertetabelle wird in dieser Methode als kubisches Spline approximiert.
//     */
//    public double getFuelPerHourCubic(double speedOnSurface) {
//        // TODO implementieren Sie diese Funktion in Aufgabe 2
//        throw new RuntimeException("Aufgabe 2 ist noch nicht implementiert");
//    }

    /**************************** AUFGABE 3 ********************************/

    /**
     * Initialisierung der Spline Daten für diese Aufgabe. Diese Funktion wird
     * einmal aufgerufen bevor onSensorDataAvailable aufgerufen wird.
     */
    public void initialize() {
        // TODO implementieren Sie diese Funktion in Aufgabe 3
        throw new RuntimeException("Aufgabe 3 ist noch nicht implementiert");
    }

    /*
     * Event-Handler Methode, die aufgerufen wird, wenn das fahrende Schiff neue
     * shipSensor Daten zur Verfügung hat. Die Methode soll durch die Klasse
     * shipSteering eine neue Fahrgeschwindigkeit des Schiffs setzten. Diese
     * Geschwindigkeit soll so gewählt werden, dass das Schiff die Fahrstrecke
     * mit möglichst geringem Treibstoffverbrauch zurücklegt.
     */
    public void onSensorDataAvailable(Steering shipSteering, Sensor shipSensor) {
        // TODO implementieren Sie diese Funktion in Aufgabe 3
        throw new RuntimeException("Aufgabe 3 ist noch nicht implementiert");
    }

}
