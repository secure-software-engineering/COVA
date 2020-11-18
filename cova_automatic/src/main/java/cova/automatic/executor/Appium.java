package cova.automatic.executor;

import cova.automatic.AutomaticRunner;
import cova.automatic.results.ConstraintInformation;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Appium {

  public static List<String> executeApp(
      Path apkFile,
      Path recordingFile,
      List<ConstraintInformation> path,
      Map<String, Integer> activityToId,
      Map<Integer, String> layouts)
      throws IOException {

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
    capabilities.setCapability(MobileCapabilityType.APP, apkFile.toAbsolutePath().toString());
    capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
    capabilities.setCapability(MobileCapabilityType.NO_RESET, false);

    AndroidDriver<MobileElement> driver =
        new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    List<String> logs = new ArrayList<>();
    driver.addLogcatMessagesListener(
        (s) -> {
          if (s.contains(AutomaticRunner.PRE_STRING)) {
            logs.add(s.substring(s.indexOf(AutomaticRunner.PRE_STRING)));
          }
        });
    driver.startLogcatBroadcast();
    driver.startRecordingScreen();
    // Go along defined path
    for (ConstraintInformation activityInfo : path) {
      System.out.println("Send events to " + activityInfo.getClazz());
      Map<String, Object> constraintMap = activityInfo.getConstraintMap();
      // String fullyQualified = driver.getCurrentPackage() + driver.currentActivity();
      // int activityId = activityToId.get(fullyQualified);
      // String layout = layouts.get(activityId);

      // Collect actions
      List<AppiumAction> values = new ArrayList<>();
      List<AppiumAction> clicks = new ArrayList<>();
      for (Entry<String, Object> e : new HashSet<>(constraintMap.entrySet())) {
        String elementPaths = e.getKey();
        for (String elementPath : elementPaths.split(";")) {
          String[] pathParts = elementPath.split(":");
          // if (!pathParts[0].equals(layout)) {
          //	continue;
          // }
          MobileElement ele = driver.findElement(By.id(pathParts[1]));
          if (!ele.isDisplayed()) {
            continue;
          }
          constraintMap.remove(elementPaths);
          if (pathParts.length == 2) {
            values.add(new AppiumAction(ele, e.getValue(), null));
          } else {
            clicks.add(new AppiumAction(ele, e.getValue(), pathParts[2]));
            break;
          }
        }
      }

      // Execute value inputs
      for (AppiumAction a : values) {
        MobileElement ele = a.getElement();
        Object value = a.getValue();
        if (value instanceof String) {
          String s = (String) value;
          ele.clear();
          ele.sendKeys(s);
          System.out.println("Send '" + s + "' to input " + ele.getId());
        } else {
          throw new RuntimeException(value.getClass() + " not implemented yet");
        }
      }
      // Execute clicks
      for (AppiumAction a : clicks) {
        MobileElement ele = a.getElement();
        if (a.getActionType().equals("onClick")) {
          ele.click();
          System.out.println("Click on input " + ele.getId());
        } else {
          throw new RuntimeException(a.getActionType() + " not implemented yet");
        }
      }
      System.out.println("Remaining constraints (not found in app):");
      System.out.println(constraintMap);
      System.out.println();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    driver.stopLogcatBroadcast();
    String recordingS = driver.stopRecordingScreen();
    byte[] recordingB = Base64.getDecoder().decode(recordingS);
    Files.write(recordingFile, recordingB);

    return logs;
  }
}
