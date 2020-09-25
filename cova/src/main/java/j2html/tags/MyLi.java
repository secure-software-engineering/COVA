/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package j2html.tags;

import j2html.attributes.Attr;
import j2html.attributes.Attr.ShortForm;
import j2html.attributes.Attribute;

/**
 * Custom {@code <li>} Tag implementation to be able to use {@link MyAttribute} and render
 * attributes with single outer quotes
 */
public class MyLi extends ContainerTag {

  public MyLi(ShortForm attrs, DomContent... content) {
    super("li");
    Attr.addTo(this.with(content), attrs);
  }

  public MyLi(ContainerTag... contents) {
    super("li");
    this.with(contents);
  }

  /**
   * Sets an attribute on an element
   *
   * @param name the attribute
   * @param value the attribute value
   */
  @Override
  boolean setAttribute(String name, String value) {
    if (value == null) {
      return getAttributes().add(new Attribute(name));
    }
    for (Attribute attribute : getAttributes()) {
      if (attribute.getName().equals(name)) {
        attribute.setValue(value); // update with new value
        return true;
      }
    }
    return getAttributes().add(new MyAttribute(name, value));
  }
}
