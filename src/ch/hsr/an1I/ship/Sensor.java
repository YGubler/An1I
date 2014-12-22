/**
 * 
 */
package ch.hsr.an1I.ship;

/**
 * Enthält alle Daten der Schiffssensoren zum Zeitpunkt der Abfrage.
 * 
 * @author oaugenst
 *
 */
public interface Sensor {
	/* Die Geschwindigkeit des Schiffs in km/h relativ zur Wasseroberfläche gemessen.*/
	public double getSpeedOnSurface();
	/* Die Geschwindigkeit des Schiffs in km/h durch den GPS Sensor gemessen */
	public double getAbsGpsSpeed();
	/* Der gegenwärtige Verbrauch in l/h */
	public double getFuelPerHour();
}
