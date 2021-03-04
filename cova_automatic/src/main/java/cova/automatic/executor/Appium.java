package cova.automatic.executor;

import cova.automatic.data.TestResult;
import cova.automatic.data.TestResultActivity;
import cova.automatic.data.TestResultInput;
import cova.automatic.data.TestResultInputType;
import cova.automatic.results.ConstraintInformation;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Appium {

  private AndroidDriver<MobileElement> driver;
  private List<String> logs = new ArrayList<>();

  private Appium(AndroidDriver<MobileElement> driver) {
    this.driver = driver;
    driver.addLogcatMessagesListener(
        (s) -> {
          if (s.contains(AutomaticRunner.PRE_STRING)) {
            logs.add(s.substring(s.indexOf(AutomaticRunner.PRE_STRING)));
          } else if (s.contains(AutomaticRunner.PRE_INFO_STRING)) {
            logs.add(s.substring(s.indexOf(AutomaticRunner.PRE_INFO_STRING)));
          }
        });
    driver.startLogcatBroadcast();
  }

  public static Appium setUp(Path apkFile) throws MalformedURLException {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
    capabilities.setCapability(MobileCapabilityType.APP, apkFile.toAbsolutePath().toString());
    // capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
    // capabilities.setCapability(MobileCapabilityType.NO_RESET, false);

    AndroidDriver<MobileElement> driver = getDriver(capabilities);
    driver.installApp(apkFile.toAbsolutePath().toString());
    // System.exit(0);
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new Appium(driver);
  }

  public TestResult executeApp(
      Path recordingFile,
      List<ConstraintInformation> path,
      String selectedOutput,
      Map<Integer, String> mapping)
      throws IOException {
    System.out.println();
    System.out.println("Start testing run");
    // driver.closeApp();
    logs.clear();
    driver.launchApp();
    try {
      Thread.sleep(500);
    } catch (InterruptedException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }
    // List<String> logs = new ArrayList<>();

    // driver.startLogcatBroadcast();
    if (recordingFile != null) {
      driver.startRecordingScreen();
    }

    TestResult result = new TestResult();
    result.setSelectedOutput(selectedOutput);
    // Go along defined path
    for (ConstraintInformation activityInfo : path) {
      System.out.println("Send events to " + activityInfo.getClazz());
      Map<String, Object> constraintMap = activityInfo.getConstraintMap();
      // String fullyQualified = driver.getCurrentPackage() + driver.currentActivity();
      // int activityId = activityToId.get(fullyQualified);
      // String layout = layouts.get(activityId);

      TestResultActivity resultActivity =
          new TestResultActivity(activityInfo.getClazz().toString(), constraintMap);
      System.out.println(resultActivity.getConstraints());
      result.getPath().add(resultActivity);

      // Collect actions
      List<AppiumAction> values = new ArrayList<>();
      List<AppiumAction> clicks = new ArrayList<>();
      for (Entry<String, Object> e : new HashSet<>(constraintMap.entrySet())) {
        String elementPaths = e.getKey();
        for (String elementPath : elementPaths.split(";")) {
          String[] pathParts = elementPath.split(":");
          if (pathParts.length == 1) {
            continue;
          }
          // if (!pathParts[0].equals(layout)) {
          // continue;
          // }
          String idStr = pathParts[1];
          MobileElement ele;
          try {
            ele = driver.findElement(By.id(idStr));
          } catch (Exception ex) {
            ex.printStackTrace();
            continue;
          }
          if (!ele.isDisplayed()) {
            continue;
          }
          constraintMap.remove(elementPaths);
          if (pathParts.length == 2) {
            values.add(new AppiumAction(idStr, ele, e.getValue(), null));
          } else {
            clicks.add(new AppiumAction(idStr, ele, e.getValue(), pathParts[2]));
            break;
          }
        }
      }

      // Execute value inputs
      for (AppiumAction a : values) {
        MobileElement ele = a.getElement();
        String clazz = ele.getAttribute("className");

        Object value = a.getValue();
        if (value instanceof String) {
          String s = (String) value;

          TestResultInput resultInput =
              new TestResultInput(TestResultInputType.VALUE, clazz, s, a.getId());

          System.out.println("Send '" + s + "' to input " + ele.getId() + " (" + clazz + ")");
          boolean inputOk = true;
          if (clazz.equals("android.widget.Spinner")) {
            if (s.equals("!0!") || s.equals("!1!")) {
              inputOk = false;
            } else {
              ele.click();

              try {
                Thread.sleep(1000);
              } catch (InterruptedException e1) {
                e1.printStackTrace();
              }
              try {
                driver
                    .findElementByAndroidUIAutomator("new UiSelector().text(\"" + s + "\")")
                    .click();

              } catch (Exception e) {
                inputOk = false;
                e.printStackTrace();
              }
            }

          } else {
            ele.clear();
            ele.sendKeys(s);
          }
          if (inputOk) {
            resultActivity.getInputs().add(resultInput);
          }
        } else if (value instanceof Boolean) {
          Boolean val = (Boolean) value;
          if (clazz.equals("android.widget.ToggleButton")) {
            boolean checked = Boolean.parseBoolean(ele.getAttribute("checked"));
            int count = 0;
            for (AppiumAction a2 : clicks) {
              if (a2.getId().equals(a.getId())) {
                count++;
              }
            }
            if ((val && checked) || (!val && !checked)) {
              // Need two clicks
              if (count == 1) {
                clicks.add(
                    new AppiumAction(a.getId(), a.getElement(), a.getValue(), "onCheckedChanged"));
              }
              if (count == 0) {
                clicks.add(
                    new AppiumAction(a.getId(), a.getElement(), a.getValue(), "onCheckedChanged"));
                clicks.add(
                    new AppiumAction(a.getId(), a.getElement(), a.getValue(), "onCheckedChanged"));
              }
            }
            if ((val && !checked) || (!val && checked)) {
              if (count == 0) {
                clicks.add(
                    new AppiumAction(a.getId(), a.getElement(), a.getValue(), "onCheckedChanged"));
              }
            }
          } else {
            throw new RuntimeException(clazz + " not implemented yet");
          }

        } else if (value instanceof Integer) {
          Integer val = (Integer) value;
          if (clazz.equals("android.widget.RadioGroup")) {
            String selectedKey = mapping.get(val);
            try {
              MobileElement ele2 = driver.findElement(By.id(selectedKey));
              boolean checked = Boolean.parseBoolean(ele2.getAttribute("checked"));

              if (checked) {
                List<MobileElement> eles =
                    ele.findElementsByClassName(ele2.getAttribute("className"));

                for (MobileElement ele3 : eles) {
                  if (!Boolean.parseBoolean(ele3.getAttribute("checked"))) {
                    ele3.click();
                    break;
                  }
                }
                ele2 = driver.findElement(By.id(selectedKey));
              }

              ele2.click();
            } catch (Exception ex) {
              ex.printStackTrace();
              continue;
            }

          } else {
            throw new RuntimeException(clazz + " not implemented yet");
          }

        } else {
          throw new RuntimeException(value.getClass() + " not implemented yet");
        }
      }
      // Execute clicks
      for (AppiumAction a : clicks) {
        MobileElement ele = a.getElement();
        String clazz = ele.getAttribute("className");
        if (a.getActionType().equals("onClick")
            || a.getActionType().equals("onCheckedChanged")
            || a.getActionType().equals("onItemClick")) {

          TestResultInput resultInput =
              new TestResultInput(TestResultInputType.CLICK, clazz, "click", a.getId());
          resultActivity.getInputs().add(resultInput);

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
    // driver.stopLogcatBroadcast();

    if (recordingFile != null) {
      String recordingS = driver.stopRecordingScreen();
      byte[] recordingB = Base64.getDecoder().decode(recordingS);
      Files.write(recordingFile, recordingB);
    }
    // driver.closeApp();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    result.setLogs(new ArrayList<String>(logs));
    if (logs.contains(selectedOutput)) {
      result.setReachedDestination(true);
    } else {
      result.setReachedDestination(false);
    }
    return result;
  }

  private static AndroidDriver<MobileElement> getDriver(Capabilities capabilities)
      throws MalformedURLException {
    while (true) {
      try {
        return new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
      } catch (Exception e) {
        e.printStackTrace();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
  }
}
