package unitTestSuite.testSourceParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import cova.source.data.Field;
import cova.source.data.Method;
import cova.source.data.Source;
import cova.source.data.SourceField;
import cova.source.data.SourceMethod;
import cova.source.data.SourceType;
import cova.source.parser.SourceParser;
import utils.TestPrivateFields;
import utils.UnitTestFramework;

public class SourceParserTest extends UnitTestFramework {

  SourceParser sourceParser = new SourceParser();

  @Test
  public void testReadFile() {

    try {
      sourceParser = new SourceParser();

      sourceParser.readFile(
          new File("config/Configuration_APIs.txt").getCanonicalPath());
      int size = sourceParser.getAllSources().size();

      // ASSUMPTION: ConfigurationSources.txt does not get new lines
      assertEquals(448, size);

      // multiple calls should clear the container for parsing; add new parsed data to sources set
      sourceParser.readFile(new File("config/Configuration_APIs.txt").getCanonicalPath());

      assertEquals(size, sourceParser.getAllSources().size() / 2);

    } catch (IOException e) {
      fail(e.getMessage());
    }

    try {
      sourceParser.readFile("nonexisting-file-path-nonce-35zgsd93wef723pfi9");
      fail("should throw an Exception thrown (or that file really exists)");
    } catch (Exception e) {
    }

    try {
      sourceParser.readFile(null);
      fail("should throw an Exception thrown");
    } catch (Exception e) {
    }

  }

  @Test
  public void testGetAllSources() {

    // reset sourceParser
    sourceParser = new SourceParser();
    assertEquals(0, sourceParser.getAllSources().size());

    testParseMethod();
    assertEquals(1, sourceParser.getAllSources().size());

  }

  @Test
  public void testParseSource() {

    // forward System.err to variable
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream oldErr = System.err;
    System.setErr(new PrintStream(outContent));

    // reset sourceParser
    sourceParser = new SourceParser();

    Set<String> data = new HashSet<String>();
    data.add("nonsense");
    data.add("");
    data.add("%");
    data.add("<utils.Configuration: java.lang.String fieldC> -> C: FC ID: 509");
    data.add("<android.content.res.Configuration: int colorMode> -> C: COLORMODE ID: 26");
    data.add("<android.os.Build: java.lang.String getRadioVersion()> -> C: RADIO ID: 37");
    data.add(
        "<android.content.Context: java.lang.Object getSystemService(java.lang.String)>(\\\"wifi\\\") -> C: WIFI ID: 103");
    data.add(
        "<android.provider.Settings$Secure: int getInt(android.content.ContentResolver,java.lang.String.*)>(.+, \\\"data_roaming\\\".*) -> C: DATA_ROAMING ID: 270");

    TestPrivateFields.invokeMethod(sourceParser, "parseSource", new Object[] {data});
    assertTrue(outContent.size() > 0);

    Set<Source> sources = sourceParser.getAllSources();
    assertEquals(5, sources.size());

    for (Source source : sources) {

      String line =
          ((source instanceof SourceMethod) ? ((SourceMethod) source).getMethod().getSignature()
              : ((SourceField) source).getField().getSignature());
      line += " -> " + source.getType() + ": " + source.getName() + " ID: " + source.getId();

      assertTrue(data.contains(line));

    }


    System.setErr(oldErr);

  }

  @Test
  public void testParseMethod() {

    // test single parameter
    Method method1 = new Method(".+", "java.lang.Object", "getSystemService",
        Arrays.asList("java.lang.String"), Arrays.asList("\\\"alarm\\\""));
    SourceMethod source1 = new SourceMethod(method1, SourceType.C, "ALARM", 108);
    helperParseMethod(source1);

    // test multiple parameter
    Method method2 = new Method("android.provider.Settings$Secure", "int", "getInt",
        Arrays.asList("android.content.ContentResolver", "java.lang.String.*"),
        Arrays.asList(".+", "\\\"adb_enabled\\\".*"));
    SourceMethod source2 = new SourceMethod(method2, SourceType.C, "ADB", 264);
    helperParseMethod(source2);

  }

  public void helperParseMethod(SourceMethod source) {

    Method method = source.getMethod();

    // reset sourceParser
    sourceParser = new SourceParser();
    Set<?> set = sourceParser.getAllSources();
    assertEquals(0, set.size());

    // e.g. "<.+: java.lang.Object getSystemService(java.lang.String)>(\"alarm\") -> C: ALARM ID:
    // 108";
    String line = method.getSignature() + " -> " + source.getType() + ": " + source.getName()
        + " ID: " + source.getId();

    String regex = (String) TestPrivateFields.invokeMethod(sourceParser, "getRegexMethod", null);
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);
    if (matcher.find()) {
      TestPrivateFields.invokeMethod(sourceParser, "parseMethod", new Object[] {matcher});
    } else {
      fail("Regex \"" + regex + "\"does not match the given Teststring\n" + line);
    }

    // access last (and only) added element
    set = sourceParser.getAllSources();
    assertEquals(1, set.size());

    for (Source s : sourceParser.getAllSources()) {

      SourceMethod sm = (SourceMethod) s;
      asserthelperCompareSource(source, s);
      assertEquals(method, sm.getMethod());

    }

  }

  @Test
  public void testParseField() {

    // reset sourceParser
    sourceParser = new SourceParser();
    Set<?> set = sourceParser.getAllSources();
    assertEquals(0, set.size());

    Field field = new Field("utils.Configuration", "java.lang.String", "fieldC");
    SourceField source = new SourceField(field, SourceType.C, "FC", 509);

    // e.g. "<utils.Configuration: java.lang.String fieldC> -> C: FC ID: 509";
    String line = field.getSignature() + "   ->   " + source.getType() + "  :     "
        + source.getName() + "  ID:      " + source.getId();

    String regex = (String) TestPrivateFields.invokeMethod(sourceParser, "getRegexField", null);
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);
    if (matcher.find()) {
      TestPrivateFields.invokeMethod(sourceParser, "parseField", new Object[] {matcher});
    } else {
      fail("Regex does not match the given Teststring");
    }

    // access last (and only) added element
    set = sourceParser.getAllSources();
    assertEquals(1, set.size());

    for (Source s : sourceParser.getAllSources()) {

      SourceField sf = (SourceField) s;
      asserthelperCompareSource(source, s);
      assertEquals(field, sf.getField());

    }

  }


  public static void asserthelperCompareSource(Source a, Source b) {

    assertEquals(a.getId(), b.getId());
    assertEquals(a.getName(), b.getName());
    assertEquals(a.getType(), b.getType());
    assertEquals(a.getUniqueName(), b.getUniqueName());

  }

  @Test
  public void testGetRegexField() {

    String regex = (String) TestPrivateFields.invokeMethod(sourceParser, "getRegexField", null);
    Pattern pattern = Pattern.compile(regex);

    // %Configuration element
    assertTrue(
        pattern.matcher("<utils.Configuration: java.lang.String fieldC> -> C: FC ID: 509").find());
    // %fieldRefs element
    assertTrue(
        pattern.matcher("<android.content.res.Configuration: int colorMode> -> C: COLORMODE ID: 26")
            .find());

    assertFalse(
        pattern.matcher("<android.os.Build: java.lang.String getRadioVersion()> -> C: RADIO ID: 37")
            .find());

    assertFalse(pattern.matcher(
        "<android.content.Context: java.lang.Object getSystemService(java.lang.String)>(\\\"wifi\\\") -> C: WIFI ID: 103")
        .find());

    assertFalse(pattern.matcher(
        "<android.provider.Settings$Secure: int getInt(android.content.ContentResolver,java.lang.String.*)>(.+, \\\"data_roaming\\\".*) -> C: DATA_ROAMING ID: 270")
        .find());

  }

  @Test
  public void testGetRegexMethod() {

    String regex = (String) TestPrivateFields.invokeMethod(sourceParser, "getRegexMethod", null);
    Pattern pattern = Pattern.compile(regex);

    // %Configuration element
    assertFalse(
        pattern.matcher("<utils.Configuration: java.lang.String fieldC> -> C: FC ID: 509").find());
    // %fieldRefs element
    assertFalse(
        pattern.matcher("<android.content.res.Configuration: int colorMode> -> C: COLORMODE ID: 26")
            .find());

    // %methods element
    assertTrue(
        pattern.matcher("<android.os.Build: java.lang.String getRadioVersion()> -> C: RADIO ID: 37")
            .find());

    assertTrue(pattern.matcher(
        "<android.content.Context: java.lang.Object getSystemService(java.lang.String)>(\\\"wifi\\\") -> C: WIFI ID: 103")
        .find());

    assertTrue(pattern.matcher(
        "<android.provider.Settings$Secure: int getInt(android.content.ContentResolver,java.lang.String.*)>(.+, \\\"data_roaming\\\".*) -> C: DATA_ROAMING ID: 270")
        .find());

  }



}
