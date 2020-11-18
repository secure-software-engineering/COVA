package cova.automatic.apk;

import brut.androlib.AndrolibException;
import brut.androlib.res.AndrolibResources;
import brut.androlib.res.data.ResPackage;
import brut.androlib.res.data.ResResSpec;
import brut.androlib.res.data.ResTable;
import brut.directory.DirectoryException;
import brut.directory.ExtFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ApktoolMapper {
  public static Map<String, Map<Integer, String>> getMapping(Path apkFile)
      throws AndrolibException, DirectoryException, IOException {
    AndrolibResources decoder = new AndrolibResources();

    ResTable resTable = decoder.getResTable(new ExtFile(apkFile.toFile()), true);
    Map<Integer, String> mapping = new HashMap<>();
    Map<Integer, String> layoutMapping = new HashMap<>();
    Map<Integer, String> idMapping = new HashMap<>();
    Map<String, Map<Integer, String>> baseMapping = new HashMap<>();
    baseMapping.put("mapping", mapping);
    baseMapping.put("layoutMapping", layoutMapping);
    baseMapping.put("idMapping", idMapping);
    for (ResPackage pkg : resTable.listMainPackages()) {
      for (ResResSpec spec : pkg.listResSpecs()) {
        if (spec.getType().getName().equals("id")) {
          idMapping.put(spec.getId().id, spec.getName());
        }
        if (spec.getType().getName().equals("layout")) {
          layoutMapping.put(spec.getId().id, spec.getName());
        }
        mapping.put(spec.getId().id, spec.getName());
      }
    }
    return baseMapping;
  }
}
