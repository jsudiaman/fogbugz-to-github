package com.sudicode.fb2gh.fogbugz;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * An {@link Iterable} which iterates over the {@link Element} nodes of a
 * {@link NodeList}.
 * </p>
 * 
 * <p>
 * This provides an alternative to the traditional strategy, which is somewhat
 * verbose:
 * </p>
 * 
 * <pre>
 * NodeList nList = doc.getElementsByTagName("tagname");
 * for (int temp = 0; temp < nList.getLength(); temp++) {
 *     Node nNode = nList.item(temp);
 *     if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 *         Element e = (Element) nNode;
 *         // do stuff with the element
 *     }
 * }
 * </pre>
 * 
 * <p>
 * Using this class, the above can be simplified to:
 * </p>
 * 
 * <pre>
 * for (Element e : new FBXmlElements(doc.getElementsByTagName("tagname"))) {
 *     // do stuff with the element
 * }
 * </pre>
 */
class FBXmlElements implements Iterable<Element> {

    private final NodeList nodeList;

    /**
     * Constructor.
     * 
     * @param nodeList
     *            The {@link NodeList} to iterate over.
     */
    FBXmlElements(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public Iterator<Element> iterator() {
        return new Iterator<Element>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                while (i < nodeList.getLength()) {
                    if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        return true;
                    }
                    i++;
                }
                return false;
            }

            @Override
            public Element next() {
                return (Element) nodeList.item(i++);
            }
        };
    }

}
