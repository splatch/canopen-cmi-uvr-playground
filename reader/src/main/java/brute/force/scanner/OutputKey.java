package brute.force.scanner;

import java.util.Objects;

public class OutputKey {

  private final String type;
  private final int index;

  OutputKey(String type, int index) {
    this.type = type;
    this.index = index;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OutputKey)) {
      return false;
    }
    OutputKey ioKey = (OutputKey) o;
    return index == ioKey.index && Objects.equals(type, ioKey.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, index);
  }

  public static OutputKey analog(int index) {
    return new OutputKey("analog", index);
  }
  public static OutputKey digital(int index) {
    return new OutputKey("digital", index);
  }

  public String toString() {
    return "Output Key[" + type + "@" + index + "]";
  }

}
