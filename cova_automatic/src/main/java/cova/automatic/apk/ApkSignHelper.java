package cova.automatic.apk;

import cova.automatic.sdk.SDKResolver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SignJar;

public class ApkSignHelper {

  public boolean sign(Path input, Path signed, Path aligned)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
          UnrecoverableKeyException, InterruptedException {
    Path androidPath = SDKResolver.resolve();

    String zipalignBinary = androidPath.resolve("zipalign").toString();
    URL fileUrl = getClass().getResource("/demo.jks");
    File ksF = new File(fileUrl.getFile());


    Project project = new Project();
    project.init();
    SignJar sign = new SignJar();
    sign.setProject(project);
    sign.setAlias("demo");
    sign.setKeystore(ksF.toString());
    sign.setStorepass("password");
    sign.setJar(input.toFile());
    sign.setSignedjar(signed.toFile());
    sign.execute();

    ProcessBuilder processBuilder = new ProcessBuilder().inheritIO();

    processBuilder.command(
        zipalignBinary,
        "-f",
        "-v",
        "4",
        signed.toAbsolutePath().toString(),
        aligned.toAbsolutePath().toString());

    Process process = processBuilder.start();
    int result = process.waitFor();
    return result == 0;
  }
}
