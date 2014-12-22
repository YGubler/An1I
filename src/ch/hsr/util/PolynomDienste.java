package ch.hsr.util;

import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

/* Diese Klasse bietet verschiedene Hilfsfunktionen zum Rechnen mit Polynomen. 
 * Das Polynom muss als Liste von Koeffizienten übergeben werden, wobei der Koeffizient 
 * mit dem grössten Index der grössten Potenz zugeordnet ist.
 * Bsp: a[3]*x^3+a[2]*x^2+a[1]*x^1+a[0] */
public class PolynomDienste {

    /*
     * Diese Methode gibt die Nullstellen eines Polynoms zurück, wobei
     * angenommen wird, dass das Polynom nur reelle Nullstellen besitzt.
     */
    static public double[] getNullstellen(double a[]) {
        Complex[] retC = new LaguerreSolver().solveAllComplex(a, 0.0);
        double[] ret = new double[retC.length];
        for (int i = 0; i < retC.length; i++) {
            ret[i] = retC[i].abs();
        }
        return ret;
    }
}
