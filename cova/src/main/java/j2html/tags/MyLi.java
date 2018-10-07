package j2html.tags;

import j2html.attributes.Attr;
import j2html.attributes.Attr.ShortForm;
import j2html.attributes.Attribute;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Custom {@code 
 * <li>} Tag implementation to be able to use {@link MyAttribute} and render attributes with single
 * outer quotes
 * 
 *
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
     * @param name  the attribute
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
