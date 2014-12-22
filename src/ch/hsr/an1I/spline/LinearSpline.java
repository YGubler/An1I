package ch.hsr.an1I.spline;

public class LinearSpline {
    /* x-Werte der Knotenpunkte */
    private double xKnoten[];
    /* y-Werte der Knotenpunkte */
    private double sKnoten[];

    /* Steigungswerte der linearen Spline-Polynome */
    private double m[];
    /* Achsenabschnittswerte der linearen Spline-Polynome */
    private double b[];

    /*
     * @param x x-Werte der Knotenpunkte
     * 
     * @param y y-Werte der Knotenpunkte
     */
    public LinearSpline(double x[], double s[]) {
        if (x.length != s.length) {
            throw new IllegalArgumentException(
                    "Die Listen x und y sind nicht gleich lang");
        }
        xKnoten = x;
        sKnoten = s;
        int anzahlSplinePolynome = x.length - 1;
        m = new double[anzahlSplinePolynome];
        b = new double[anzahlSplinePolynome];

        for (int i = 0; i < anzahlSplinePolynome; i++) {
            m[i] = (s[i + 1] - s[i]) / (x[i + 1] - x[i]);
            b[i] = s[i] - m[i] * x[i];
        }
    }

    public double getValueAt(double x) {
        for (int polynomIndex = 0; polynomIndex < m.length; polynomIndex++) {
            if (xKnoten[polynomIndex] <= x && x <= xKnoten[polynomIndex + 1]) {
                // das passende Polynom ist gefunden
                return m[polynomIndex] * x + b[polynomIndex];
            }
        }
        throw new IllegalArgumentException("Das Argument " + x
                + " liegt nicht zwischen den Knoten");
    }
}
