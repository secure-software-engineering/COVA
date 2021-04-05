package cova.automatic.executor;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AppiumTest {

  public static void main(String[] args) throws IOException, InterruptedException {
    Path apkFile =
        Paths.get(
            "/home/fynn/Dokumente/masterarbeit/activity_test/app/build/outputs/apk/debug/app-debug.apk");
    String url = "http://127.0.0.1:4723/wd/hub";
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
    capabilities.setCapability(MobileCapabilityType.APP, apkFile.toAbsolutePath().toString());
    // capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
    // capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
    AndroidDriver<MobileElement> driver = new AndroidDriver<>(new URL(url), capabilities);

    driver.installApp(apkFile.toAbsolutePath().toString());

    driver.launchApp();
    driver.startRecordingScreen();
    Thread.sleep(2000);
    driver.findElement(By.id("editText")).sendKeys("aaaaaaaaaaa");
    driver.findElement(By.id("button")).click();
    Thread.sleep(2000);
    String recordingS = driver.stopRecordingScreen();
    byte[] recordingB = Base64.getDecoder().decode(recordingS);
    Files.write(Paths.get("/tmp/recording.mp4"), recordingB);
  }
}
