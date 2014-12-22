package ch.hsr.util;

public class DifferenzenQuotient {

	double f(double x) {
		// return Math.cos(x)*Math.exp(Math.tan(x))/(4*Math.pow(x,2));
		return Math.exp(x) - (1.0 / x);
		//return Math.exp(x) - Math.log(x) - 3.0;
		// throw new RuntimeException("implementieren");
	}

	public double differenzenQuotient(double x0, double x1) {
		return (f(x0) - f(x1)) / (x0 - x1);
		// throw new RuntimeException("implementieren");
	}

	public double differentialQuotient(double x) {
		//return differenzenQuotient(x, x + 0.000001);
		return Math.exp(x) + 1.0 / Math.pow(x, 2);
		// throw new RuntimeException("implementieren");
	}

	public double solve(double x0) {
		double m = differentialQuotient(x0);
		double b = f(x0);
		return -b / m + x0;
		// throw new RuntimeException("implementieren");
	}

	public static void main(String[] args) {
		DifferenzenQuotient d = new DifferenzenQuotient();
		if (args.length == 2) {
			double x0 = Double.parseDouble(args[0]);
			double x1 = Double.parseDouble(args[1]);
			System.out
					.println("Der Differenzenquotient der Funktion f zwischen den Stellen "
							+ x0 + " und " + x1 + " beträgt:");
			System.out.println("Wert = " + d.differenzenQuotient(x0, x1));
		} else if (args.length == 1) {
			double x = Double.parseDouble(args[0]);
			for (int i = 0; i < 3; i++){
				x = d.solve(x);
			}
			System.out
					.println("Der Differentialquotient der Funktion f an der Stellen "
							+ x + " ungefähr:");
			System.out.println("Wert = " + d.differentialQuotient(x));
			System.out
					.println("Die Lösung der Gleichung f(x) = 0 liegt ungefähr bei :\nx = "
							+ x);
		} else {
			System.out.println("Das Programm erwartet 1 oder 2 Argumente");
		}
	}
}