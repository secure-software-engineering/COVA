package cova.automatic.apk.aapt;

import cova.automatic.apk.aapt.AaptTreeParser.Section;
import cova.automatic.sdk.SDKResolver;
import cova.source.SourceInformation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AaptHelper {

  private Path binary;

  public AaptHelper() throws IOException {
    Path androidPath = SDKResolver.resolve();
    binary = androidPath.resolve("aapt2");
  }

  public List<SourceInformation> listCallbacksOfSection(Section section, int layoutId) {
    List<SourceInformation> infos = new ArrayList<>();
    if (section.getText().contains("http://schemas.android.com/apk/res/android:on")) {
      String secText = section.getText();
      secText = secText.substring(secText.indexOf("schemas.android.com/apk/res/android"));
      secText = secText.substring(secText.indexOf(":") + 1);
      String methodType = secText.substring(0, secText.indexOf("("));
      String methodName = secText.substring(secText.indexOf("=\"") + 2);
      methodName = methodName.substring(0, methodName.indexOf("\""));
      String id = null;
      for (Section attr : section.getParent().getChildren()) {
        String text = attr.getText();
        if (text.contains("http://schemas.android.com/apk/res/android:id")) {
          id = text.substring(text.indexOf("=") + 1);
          if (id.startsWith("@")) {
            id = id.substring(1);
          }
        }
      }
      int buttonId = Integer.decode(id);

      SourceInformation info = new SourceInformation(layoutId, buttonId, methodName, methodType);
      infos.add(info);
    }
    for (Section child : section.getChildren()) {
      infos.addAll(listCallbacksOfSection(child, layoutId));
    }
    return infos;
  }

  public List<SourceInformation> listCallbacksOfFile(Path apkFile, String path, int layoutId)
      throws IOException, InterruptedException {
    Section section = parseXml(apkFile, path);
    return listCallbacksOfSection(section, layoutId);
  }

  public Map<Integer, List<SourceInformation>> listCallbacks(
      Path apkFile, Map<Integer, String> mapping) throws IOException, InterruptedException {
    Map<String, Integer> swappedMapping =
        mapping.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    ZipFile zipFile = new ZipFile(apkFile.toFile());
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    Map<Integer, List<SourceInformation>> allInfos = new HashMap<>();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (!entry.isDirectory()) {

        String path = entry.getName();
        if (path.startsWith("res/layout/")) {
          String key = path.replace("res/layout/", "").replace(".xml", "");
          int layoutId = swappedMapping.get(key);
          List<SourceInformation> infos = listCallbacksOfFile(apkFile, path, layoutId);
          if (!infos.isEmpty()) {

            allInfos.put(layoutId, infos);
          }
        }
      }
    }
    return allInfos;
  }

  public Section parseXml(Path apkFile, String path) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(
        binary.toAbsolutePath().toString(),
        "dump",
        "xmltree",
        apkFile.toAbsolutePath().toString(),
        "--file",
        path);
    Process process = processBuilder.start();

    List<String> lines =
        new BufferedReader(new InputStreamReader(process.getInputStream()))
            .lines()
            .collect(Collectors.toList());
    String error =
        new BufferedReader(new InputStreamReader(process.getErrorStream()))
            .lines()
            .collect(Collectors.joining("\n"));
    int exitVal = process.waitFor();
    if (exitVal != 0) {
      throw new RuntimeException(exitVal + error);
    }
    return AaptTreeParser.parseLines(lines);
  }
}
