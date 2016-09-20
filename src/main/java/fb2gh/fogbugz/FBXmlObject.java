package fb2gh.fogbugz;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * FogBugz XML object. Used as the base class for common FogBugz elements such
 * as milestones and cases.
 */
abstract class FBXmlObject {

    /**
     * Scan the element for the tag and get its text content.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The text content, or <code>null</code> if not found
     */
    protected static String getTextValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Scan the element for the tag and get its text content, then parse it as
     * an integer.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The parsed integer, or <code>null</code> if parsing failed
     */
    protected static Integer getIntValue(Element element, String tagName) {
        String textValue = getTextValue(element, tagName);
        if (textValue == null) {
            return null;
        }
        try {
            return Integer.parseInt(textValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Scan the element for the tag and get its text content. If the content
     * equals <code>"true"</code>, return <code>true</code>. If the content
     * equals <code>"false"</code>, return <code>false</code>. Otherwise, return
     * <code>null</code>.
     * 
     * @param element
     *            The element
     * @param tagName
     *            The name of the tag
     * @return The boolean value represented by this content, or
     *         <code>null</code>
     */
    protected static Boolean getBooleanValue(Element element, String tagName) {
        String textValue = getTextValue(element, tagName);
        if ("true".equals(textValue)) {
            return true;
        } else if ("false".equals(textValue)) {
            return false;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
