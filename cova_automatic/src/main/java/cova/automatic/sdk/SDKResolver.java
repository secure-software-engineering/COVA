package cova.automatic.sdk;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SDKResolver {
  public static Path resolve() throws IOException {
    Path basePath = Paths.get("/opt/android-sdk/build-tools");
    String max = "0";
    Path androidPath = null;
    try (DirectoryStream<Path> dirs = Files.newDirectoryStream(basePath)) {
      for (Path dir : dirs) {
        if (dir.getFileName().toString().compareTo(max) > 0) {
          max = dir.getFileName().toString();
          androidPath = dir;
        }
      }
    }
    if (androidPath == null) {
      throw new RuntimeException("No android sdk found");
    }
    return androidPath;
  }
}
