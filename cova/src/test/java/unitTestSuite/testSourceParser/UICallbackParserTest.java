
package unitTestSuite.testSourceParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import soot.jimple.infoflow.data.SootMethodAndClass;

import cova.source.data.Source;
import cova.source.data.SourceUICallback;
import cova.source.parser.UICallbackParser;
import utils.TestPrivateFields;
import utils.UnitTestFramework;

public class UICallbackParserTest extends UnitTestFramework {

  private UICallbackParser uiCallbackParser = new UICallbackParser();

  @Test
  public void TestreadFile() {
    try {
      uiCallbackParser = new UICallbackParser();

      uiCallbackParser
          .readFile(new File("config/UICallback_APIs.txt").getCanonicalPath());
      int size = uiCallbackParser.getAllCallbacks().size();

      // ASSUMPTION: UICallbacks.txt does not get new lines
      assertEquals(335, size);

      // multiple calls should clear the data container for parsing; add new parsed data to sources
      // set
      uiCallbackParser
          .readFile(new File("config/UICallback_APIs.txt").getCanonicalPath());

      assertEquals(size, uiCallbackParser.getAllCallbacks().size() / 2);


    } catch (IOException e) {
      fail(e.getMessage());
    }

    try {
      uiCallbackParser.readFile("nonexisting-file-path-nonce-35zgsd93wef723pfi9");
      fail("should throw an Exception thrown (or that file really exists)");
    } catch (Exception e) {
    }

    try {
      uiCallbackParser.readFile(null);
      fail("should throw an Exception thrown");
    } catch (Exception e) {
    }

  }

  @Test
  public void testGetAllCallbacks() {

    uiCallbackParser = new UICallbackParser();
    Set<?> set = uiCallbackParser.getAllCallbacks();
    assertEquals(0, set.size());

  }

  @Test
  public void testParseCallback() {

    // forward System.err to variable
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream oldErr = System.err;
    System.setErr(new PrintStream(outContent));

    // reset sourceParser
    uiCallbackParser = new UICallbackParser();

    List<String> data = new ArrayList<String>();
    data.add(
        "<android.view.KeyEvent$Callback: boolean onKeyDown(int,android.view.KeyEvent)>  ID: 3");
    data.add("");
    data.add("%");
    data.add("% title");
    data.add("nonsense");

    TestPrivateFields.invokeMethod(uiCallbackParser, "parseCallbacks", new Object[] {data});
    assertTrue(outContent.size() > 0);

    Set<SourceUICallback> sources = uiCallbackParser.getAllCallbacks();
    assertEquals(1, sources.size());

    for (SourceUICallback source : sources) {

      String line = source.getCallback().toString() + " ID: " + source.getId();

      assertEquals(
          "<android.view.KeyEvent$Callback: boolean onKeyDown(intandroid.view.KeyEvent)> ID: 3",
          line);

    }

    System.setErr(oldErr);


  }


  @Test
  public void testGetRegexCallback() {

    String regex =
        (String) TestPrivateFields.invokeMethod(uiCallbackParser, "getRegexCallback", null);
    Pattern pattern = Pattern.compile(regex);


    assertTrue(pattern
        .matcher(
            "<android.view.KeyEvent$Callback: boolean onKeyDown(int,android.view.KeyEvent)>  ID: 3")
        .find());

  }


  @Test
  public void testParseCallbackWithMatcher() {

    // test single parameter
    // e.g. <android.view.GestureDetector: boolean onTouchEvent(android.view.MotionEvent)> ID: 306
    SootMethodAndClass cb1 = new SootMethodAndClass("onTouchEvent", "android.view.GestureDetector",
        "boolean", Arrays.asList("android.view.MotionEvent"));
    SourceUICallback source1 = new SourceUICallback(cb1, 306);
    helperParseCallbackWithMatcher(source1);

    // test multiple parameter
    // e.g. "<android.view.KeyEvent$Callback: boolean onKeyDown(int,android.view.KeyEvent)> ID: 3"
    SootMethodAndClass cb2 = new SootMethodAndClass("onKeyDown", "android.view.KeyEvent$Callback",
        "boolean", Arrays.asList("int", "android.view.KeyEvent"));
    SourceUICallback source2 = new SourceUICallback(cb2, 3);
    helperParseCallbackWithMatcher(source2);

  }

  public void helperParseCallbackWithMatcher(SourceUICallback source) {

    SootMethodAndClass callback = source.getCallback();

    // reset sourceParser
    uiCallbackParser = new UICallbackParser();
    Set<?> set = uiCallbackParser.getAllCallbacks();
    assertEquals(0, set.size());

    String line = callback.getSignature() + "     ID:   " + source.getId();

    String regex =
        (String) TestPrivateFields.invokeMethod(uiCallbackParser, "getRegexCallback", null);
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);
    if (matcher.find()) {
      TestPrivateFields.invokeMethod(uiCallbackParser, "parseCallback", new Object[] {matcher});
    } else {
      fail("Regex \"" + regex + "\"does not match the given Teststring\n" + line);
    }

    // access last (and only) added element
    set = uiCallbackParser.getAllCallbacks();
    assertEquals(1, set.size());

    for (Source s : uiCallbackParser.getAllCallbacks()) {

      SourceUICallback sm = (SourceUICallback) s;
      SourceParserTest.asserthelperCompareSource(source, s);
      assertEquals(callback, sm.getCallback());

    }

  }


}
