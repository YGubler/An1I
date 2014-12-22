package ch.hsr.an1I.ship;

/**
 * Interface zur automatischen Steuerung des Schiffes.
 * 
 * @author oaugenst
 *
 */
public interface Steering {
	/* Setzt die Geschwindigkeit des Schiffes auf die Geschwindigkeit speed.
	 * @param speed ein gültiger Geschwindigkeitswert für das Schiff */
	public void setSpeedOnSurface(double speed);
}
