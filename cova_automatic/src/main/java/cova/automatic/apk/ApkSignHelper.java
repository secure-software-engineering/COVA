package cova.automatic.apk;

import cova.automatic.sdk.SDKResolver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.zip.ZipFile;
import jdk.security.jarsigner.JarSigner;

public class ApkSignHelper {

  public boolean sign(Path input, Path signed, Path aligned)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
          UnrecoverableKeyException, InterruptedException {
    Path androidPath = SDKResolver.resolve();

    String zipalignBinary = androidPath.resolve("zipalign").toString();
    URL fileUrl = getClass().getResource("/demo.jks");
    File ksF = new File(fileUrl.getFile());
    KeyStore ks = KeyStore.getInstance(ksF, "password".toCharArray());
    PrivateKey key = (PrivateKey) ks.getKey("demo", "password".toCharArray());

    KeyStore.PrivateKeyEntry entry =
        new KeyStore.PrivateKeyEntry(key, ks.getCertificateChain("demo"));

    JarSigner signer = new JarSigner.Builder(entry).build();

    ZipFile f = new ZipFile(input.toFile());

    signer.sign(f, Files.newOutputStream(signed));

    ProcessBuilder processBuilder = new ProcessBuilder();

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
