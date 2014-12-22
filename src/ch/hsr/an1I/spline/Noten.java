package ch.hsr.an1I.spline;

public class Noten {
    public static void main(String[] args) {
        double xKnoten[] = { 0, 5.5, 12.5, 20.5, 30.5, 45.5, 55 };
        double sKnoten[] = { 1, 1.5, 2.5, 3.5, 4.5, 5.5, 6 };

        LinearSpline noten = new LinearSpline(xKnoten, sKnoten);
        for (int punkte = 0; punkte <= 55; punkte++) {
            double neueNote = 15.0 / 5.0 * (noten.getValueAt(punkte) - 1.0);
            System.out.println("Die Punktzahl " + punkte
                    + " ergibt die neue Note " + Math.round(neueNote));
        }
    }
}