package cova.automatic.apk.aapt;

import java.util.ArrayList;
import java.util.List;

public class AaptTreeParser {

  public static class Section {
    private String text;
    private int depth;
    private Section parent;
    private List<Section> children;

    public Section(String text, int depth) {
      this(text, depth, null);
    }

    public Section(String text, int depth, Section parent) {
      this.text = text.strip();
      this.depth = depth;
      this.children = new ArrayList<Section>();
      this.parent = parent;
    }

    public int getDepth() {
      return depth;
    }

    public List<Section> getChildren() {
      return children;
    }

    public Section getParent() {
      return parent;
    }

    public String getText() {
      return text;
    }
  }

  public static Section parseLines(List<String> lines) {
    Section root = null;
    Section prev = null;
    for (String line : lines) {
      int depth = line.length() - line.stripLeading().length();
      if (prev == null && depth == 0) {
        root = new Section(line, depth);
        prev = root;
      } else {
        if (depth > prev.getDepth()) {
          Section section = new Section(line, depth, prev);
          prev.getChildren().add(section);
          prev = section;
        } else if (depth == prev.getDepth()) {
          Section section = new Section(line, depth, prev.getParent());
          prev.getParent().getChildren().add(section);
          prev = section;
        } else {
          while (depth < prev.getDepth()) {
            prev = prev.getParent();
          }
          Section section = new Section(line, depth, prev.getParent());
          prev.getParent().getChildren().add(section);
          prev = section;
        }
      }
    }
    return root;
  }
}
