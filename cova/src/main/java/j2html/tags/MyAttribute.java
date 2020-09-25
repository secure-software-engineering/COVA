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

import j2html.attributes.Attribute;
import java.io.IOException;

/** Custom Attribute to render attribute value with outer {@code '}s instead of {@code "}s. */
public class MyAttribute extends Attribute {

  String value;

  public MyAttribute(String name) {
    super(name);
  }

  public MyAttribute(String name, String value) {
    super(name, value);
    this.value = value;
  }

  @Override
  public void renderModel(Appendable writer, Object model) throws IOException {
    if (getName() == null) {
      return;
    }
    writer.append(" ");
    writer.append(getName());
    if (value != null) {
      writer.append("='");
      writer.append(value);
      writer.append("'");
    }
  }
}
