
package cova.source.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.data.SootMethodAndClass;

import cova.source.data.SourceField;
import cova.source.data.SourceUICallback;

/**
 * The Class UICallbackParser parses UICallbacks.txt.
 *
 * @date 05.09.2017
 */
public class UICallbackParser {

  /** The callbacks. */
  private Set<SourceUICallback> callbacks;

  /**
   * Instantiates a new UI callback parser.
   */
  public UICallbackParser() {
    callbacks = new HashSet<SourceUICallback>();
  }

  /**
   * This function reads a file line by line and parses the lines by using java regex.
   *
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void readFile(String fileName) throws IOException {
    List<String> data = new ArrayList<String>();

    String line;
    FileReader fr = new FileReader(fileName);
    BufferedReader br = new BufferedReader(fr);
    while ((line = br.readLine()) != null) {
      data.add(line);
    }
    fr.close();
    br.close();
    if (!data.isEmpty()) {
      Logger logger = LoggerFactory.getLogger(getClass());
      logger.info("Read " + data.size() + " APIs from " + fileName);
    }
    parseCallbacks(data);
  }

  /**
   * Gets the all callbacks.
   *
   * @return the all callbacks
   */
  public Set<SourceUICallback> getAllCallbacks() {
    return callbacks;
  }

  /**
   * Gets the regex for a callback.
   *
   * @return the regex for a callback method
   */
  private String getRegexCallback() {
    String group1 = "(.+)";// class name
    String group2 = "(.+)";// return type
    String group3 = "(.+)";// method name
    String group4 = "(.*?)";// parameter types
    String group5 = "(.+)";// id
    String regexCallback = "^<" + group1 + ":" + "\\s+" + group2 + "\\s+" + group3 + "\\(" + group4
        + "\\)" + ">" + "\\s+ID:\\s+" + group5 + "$";
    return regexCallback;
  }

  /**
   * Parses the callback.
   */
  private void parseCallbacks(List<String> data) {
    String regexCallback = getRegexCallback();
    Pattern patternCallback = Pattern.compile(regexCallback);
    for (String line : data) {
      if (line.isEmpty() || line.startsWith("%")) {
        continue;
      }
      Matcher matcherCallback = patternCallback.matcher(line);
      if (matcherCallback.find()) {
        parseCallback(matcherCallback);
      } else {
        System.err.println("SourceParser can not parse callback " + line);
      }
    }
  }

  /**
   * This method parses a method source and generates a {@link SourceField} object.
   *
   * @param matcher the matcher
   */
  private void parseCallback(Matcher matcher) {
    String className = matcher.group(1).trim();
    String returnType = matcher.group(2).trim();
    String methodName = matcher.group(3).trim();
    String parameterTypes = matcher.group(4).trim();
    List<String> paraTypes = new ArrayList<String>();
    if (!parameterTypes.isEmpty()) {
      for (String parameter : parameterTypes.split(",")) {
        paraTypes.add(parameter.trim());
      }
    }
    String id = matcher.group(5).trim();
    SourceUICallback callback = new SourceUICallback(
        new SootMethodAndClass(methodName, className, returnType, paraTypes), Integer.parseInt(id));
    callbacks.add(callback);
  }
}
