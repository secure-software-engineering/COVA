
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

import cova.source.data.Field;
import cova.source.data.Method;
import cova.source.data.Source;
import cova.source.data.SourceField;
import cova.source.data.SourceMethod;
import cova.source.data.SourceType;

/**
 * The Class SourceParser parses ConfigurationSources.txt and InputSources.txt.
 *
 * @date 05.09.2017
 */
public class SourceParser {

  /** The sources. */
  private Set<Source> sources;

  private String regexField;
  private String regexMethod;

  /**
   * Instantiates a new source parser.
   */
  public SourceParser() {
    sources = new HashSet<Source>();
    regexField = getRegexField();
    regexMethod = getRegexMethod();
  }

  /**
   * This function reads a file line by line and parses the lines by using java regex.
   *
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void readFile(String fileName) throws IOException {

    Set<String> data = new HashSet<String>();

    FileReader fr = new FileReader(fileName);
    BufferedReader br = new BufferedReader(fr);
    String line;
    while ((line = br.readLine()) != null) {
      data.add(line);
    }
    fr.close();
    br.close();
    if (!data.isEmpty()) {
      Logger logger = LoggerFactory.getLogger(getClass());
      logger.info("Read " + data.size() + " APIs from " + fileName);
    }
    parseSource(data);
  }

  /**
   * Gets the all sources.
   *
   * @return the all sources
   */
  public Set<Source> getAllSources() {
    return sources;
  }

  /**
   * Gets the regex for a field.
   *
   * @return the regex for a field source
   */
  private String getRegexField() {
    if (regexField == null) {
      String group1 = "(.+)";// class name
      String group2 = "(.+)";// field type
      String group3 = "([^\\(\\)]+)";// field name
      String group4 = "(.+)";// source type
      String group5 = "(.+)";// option
      String group6 = "(.+)";// id
      StringBuilder sb = new StringBuilder("^<");
      sb.append(group1);
      sb.append(":");
      sb.append("\\s+");
      sb.append(group2);
      sb.append("\\s+");
      sb.append(group3);
      sb.append(">");
      sb.append("\\s+");
      sb.append("->");
      sb.append("\\s+");
      sb.append(group4);
      sb.append(":");
      sb.append("\\s+");
      sb.append(group5);
      sb.append("\\s+ID:\\s+");
      sb.append(group6);
      sb.append("$");
      regexField = sb.toString();
    }
    return regexField;
  }

  /**
   * Gets the regex for a method.
   *
   * @return the regex for a method source
   */
  private String getRegexMethod() {
    if (regexMethod == null) {
      String group1 = "(.+)";// class name
      String group2 = "(.+)";// return type
      String group3 = "(.+)";// method name
      String group4 = "(.*?)";// parameter types
      String group5 = "(.*?)";// parameter values
      String group6 = "(.+)";// source type
      String group7 = "(.+)";// option
      String group8 = "(.+)";// id
      StringBuilder sb = new StringBuilder("^<");
      sb.append(group1);
      sb.append(":");
      sb.append("\\s+");
      sb.append(group2);
      sb.append("\\s+");
      sb.append(group3);
      sb.append("\\(");
      sb.append(group4);
      sb.append("\\)");
      sb.append(">");
      sb.append("\\(?");
      sb.append(group5);
      sb.append("\\)?");
      sb.append("\\s+");
      sb.append("->");
      sb.append("\\s+");
      sb.append(group6);
      sb.append(":");
      sb.append("\\s+");
      sb.append(group7);
      sb.append("\\s+ID:\\s+");
      sb.append(group8);
      sb.append("$");
      regexMethod = sb.toString();
    }
    return regexMethod;
  }

  /**
   * This function parses the lines one by one by using a regex for field source and a regex for
   * method source.
   */
  private void parseSource(Set<String> data) {
    Pattern patternField = Pattern.compile(regexField);
    Pattern patternMethod = Pattern.compile(regexMethod);
    for (String line : data) {
      if (line.isEmpty() || line.startsWith("%")) {
        continue;
      }
      Matcher matcherField = patternField.matcher(line);
      Matcher matcherMethod = patternMethod.matcher(line);
      if (matcherField.find()) {
        parseField(matcherField);
      } else if (matcherMethod.find()) {
        parseMethod(matcherMethod);
      } else {
        System.err.println("SourceParser can not parse source " + line);
      }
    }
  }

  /**
   * This method parses a field source and generates a {@link SourceField} object.
   *
   * @param matcher the matcher
   */
  private void parseField(Matcher matcher) {
    String className = matcher.group(1).trim();
    String fieldType = matcher.group(2).trim();
    String fieldName = matcher.group(3).trim();
    String sourceType = matcher.group(4).trim();
    String option = matcher.group(5).trim();
    String id = matcher.group(6).trim();
    Field field = new Field(className, fieldType, fieldName);
    SourceField source =
        new SourceField(field, SourceType.valueOf(sourceType), option, Integer.parseInt(id));
    sources.add(source);
  }

  /**
   * This method parses a method source and generates a {@link SourceMethod} object.
   *
   * @param matcher the matcher
   */
  private void parseMethod(Matcher matcher) {
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
    String parameterValues = matcher.group(5).trim();
    List<String> paraValues = new ArrayList<String>();
    if (!parameterValues.isEmpty()) {
      for (String value : parameterValues.split(",")) {
        paraValues.add(value.trim());
      }
    }
    String sourceType = matcher.group(6).trim();
    String option = matcher.group(7).trim();
    String id = matcher.group(8).trim();
    Method method = new Method(className, returnType, methodName, paraTypes, paraValues);
    SourceMethod source =
        new SourceMethod(method, SourceType.valueOf(sourceType), option, Integer.parseInt(id));
    sources.add(source);
  }
}
