package j2html.tags;

import java.io.IOException;

import j2html.attributes.Attribute;

/**
 * Custom Attribute to render attribute value with outer {@code '}s instead of {@code "}s.
 * 
 *
 */
public class MyAttribute extends Attribute{

	String value;
	public MyAttribute(String name) {
		super(name);
	}
	
	public MyAttribute(String name, String value ) {
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
