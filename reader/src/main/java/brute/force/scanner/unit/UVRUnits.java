package brute.force.scanner.unit;

import java.util.HashMap;
import java.util.Map;

public class UVRUnits {

  private static Map<Integer, Unit> UNITS = new HashMap<>();

  public static final ScaledUnit DIMENSIONSLOS = new ScaledUnit(0, "", "dimensionslos", "", 1, true);

  static {
    UNITS.put(0, DIMENSIONSLOS);
    UNITS.put(1, new ScaledUnit(1, "Grad Celsius", "Temperatur", "°C", 0.1, true));
    UNITS.put(2, new ScaledUnit(2, "Watt/Quadratmeter", "Globalstrahlung", "W/m²", 1, true));
    UNITS.put(3, new ScaledUnit(3, "Liter/Stunde", "Durchfluss", "l/h", 1, true));
    UNITS.put(4, new ScaledUnit(4, "Sekunden", "Zeit", "Sek", 1, true));
    UNITS.put(5, new ScaledUnit(5, "Minuten", "Zeit", "Min", 1, true));
    UNITS.put(6, new ScaledUnit(6, "Liter/Impuls", "Quotient", "l/Imp", 1, true));
    UNITS.put(7, new ScaledUnit(7, "Kelvin", "Temperatur", "K", 1, true));
    UNITS.put(8, new ScaledUnit(8, "%", "rel. Luftfeute", "%", 1, true));
    UNITS.put(10, new ScaledUnit(10, "Kilowatt", "Leistung", "kW", 0.1, true));
    UNITS.put(11, new ScaledUnit(11, "Kilowattstunden", "Wärmemenge", "kWh", 1, true));
    UNITS.put(12, new ScaledUnit(12, "Megawattstunden", "Wärmemenge", "MWh", 1, true));
    UNITS.put(13, new ScaledUnit(13, "Volt", "Spannung", "V", 0.01, true));
    UNITS.put(14, new ScaledUnit(14, "milli Ampere", "Stromstärke", "mA", 0.1, true));
    UNITS.put(15, new ScaledUnit(15, "Stunden", "Zeit", "Std", 1, true));
    UNITS.put(16, new ScaledUnit(16, "Tage", "Zeit", "Tage", 1, true));
    UNITS.put(17, new ScaledUnit(17, "Impulse", "Impulse", "Imp", 1, true));
    UNITS.put(18, new ScaledUnit(18, "Kilo Ohm", "Widerstand", "kOhm", 0.01, true));
    UNITS.put(19, new ScaledUnit(19, "Liter", "Wassermenge", "l", 1, true));
    UNITS.put(20, new ScaledUnit(20, "Kilometer/Stunde", "Windgeschwindigkeit", "km/h", 1, true));
    UNITS.put(21, new ScaledUnit(21, "Hertz", "Frequenz", "Hz", 1, true));
    UNITS.put(22, new ScaledUnit(22, "Liter/Minute", "Durchfluss", "l/min", 1, true));
    UNITS.put(23, new ScaledUnit(23, "bar", "Druck", "bar", 0.01, true));
    UNITS.put(25, new ScaledUnit(25, "Kilometer", "Distanz", "km", 1, true));
    UNITS.put(26, new ScaledUnit(26, "Meter", "Distanz", "m", 1, true));
    UNITS.put(27, new ScaledUnit(27, "Millimeter", "Distanz", "mm", 1, true));
    UNITS.put(28, new ScaledUnit(28, "Kubikmeter", "Luftmenge", "m³", 1, true));
    UNITS.put(29, new ScaledUnit(29, "Hertz/km/Stunde", "Windgeschwindigkeit", "Hz/km/h", 1, true));
    UNITS.put(30, new ScaledUnit(30, "Hertz/Meter/Sek", "Windgeschwindigkeit", "Hz/m/s", 1, true));
    UNITS.put(31, new ScaledUnit(31, "kWh/Impuls", "Leistung", "kWh/Imp", 1, true));
    UNITS.put(32, new ScaledUnit(32, "Kubikmeter/Impuls", "Luftmenge", "m³/Imp", 1, true));
    UNITS.put(33, new ScaledUnit(33, "Millimeter/Impuls", "Niederschlag", "mm/Imp", 1, true));
    UNITS.put(34, new ScaledUnit(34, "Liter/Impuls (4 Komma)", "Durchfluss", "L/Imp", 1, true));
    UNITS.put(35, new ScaledUnit(35, "Liter/Tag", "Durchfluss", "l/d", 1, true));
    UNITS.put(36, new ScaledUnit(36, "Meter/Sekunde", "Geschwindigkeit", "m/s", 1, true));
    UNITS.put(37, new ScaledUnit(37, "Kubikmeter/Minute", "Durchfluss(Gas/Luft)", "m³/min", 1, true));
    UNITS.put(38, new ScaledUnit(38, "Kubikmeter/Stunde", "Durchfluss(Gas/Luft)", "m³/h", 1, true));
    UNITS.put(39, new ScaledUnit(39, "Kubikmeter/Tag", "Durchfluss(Gas/Luft)", "m³/d", 1, true));
    UNITS.put(40, new ScaledUnit(40, "Millimeter/Minute", "Regen", "mm/min", 1, true));
    UNITS.put(41, new ScaledUnit(41, "Millimeter/Stunde", "Regen", "mm/h", 1, true));
    UNITS.put(42, new ScaledUnit(42, "Millimeter/Tag", "Regen", "mm/d", 1, true));
    UNITS.put(43, new DigitalUnit(43, "Aus/Ein", "", "Aus", "Ein"));
    UNITS.put(44, new DigitalUnit(44, "Nein/Ja", "", "Nein", "Ja"));
    UNITS.put(47, new DigitalUnit(47, "Stopp/Auf/Zu", "Mischerausgang", "Stopp", "Auf", "Zu"));
    UNITS.put(55, new RollerShutterUnit()); // Jalousie Position für Höhe und Neigung bei Lamelle
    UNITS.put(59, new ScaledUnit(59, "Prozent", "Jalousie Position", "%", 1, false)); // "Prozent ohne Komma für Jalousie Pos);
    UNITS.put(60, new TimeUnit()); // Uhrzeit (hh:mm)))
  }

  public static Unit get(int index) {
    return UNITS.get(index);
  }

}
