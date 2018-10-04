package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Configuration {
  private boolean featureA;
  private boolean featureB;
  private int featureC;
  private float featureD;
	public static String fieldA = "FA";
	public static String fieldB = "FB";

  public Configuration() {
    try {
      FileReader fileReader = new FileReader(
          "confi.txt");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String line = null;
      int i = 1;
      while ((line = bufferedReader.readLine()) != null) {
        if (i == 1) {
          featureA = Boolean.parseBoolean(line);
        }
        if (i == 2) {
          featureB = Boolean.parseBoolean(line);
          break;
        }
        i++;
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
  public boolean featureA() {
    return featureA;
	}

  public boolean featureB() {
    return featureB;
	}

  public int featureC() {
    return featureC;
	}

  public float featureD() {
    return featureD;
	}
}
