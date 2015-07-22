package xmlCompareUtil;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DOMUtils
{
    public static boolean compareFlag = true;

    public static boolean compare(String id, Node expected, Node actual) throws Exception
    {
        // System.out.println("=================================================================================================================================");
        // reset flag
        compareFlag = true;
        boolean flg = compareNodes(expected, actual);
        // boolean flg = compareNodes(expected.toString(), actual.toString());
        if (!flg)
        {
            System.out.println("<< ID >> : " + id);
            System.out.println("=================================================================================================================================");
        }
        return flg;
    }

    public static boolean compareNodes(Node expected, Node actual) throws Exception
    {

        // boolean flag = true;
        if (actual != null && expected.getNodeType() != actual.getNodeType())
        {
            System.out.println("Different types of nodes: " + expected + " " + actual);
            compareFlag = false;
        }

        if (expected instanceof Document)
        {
            Document expectedDoc = (Document) expected;
            Document actualDoc = (Document) actual;
            compareNodes(expectedDoc.getDocumentElement(), actualDoc.getDocumentElement());
        }
        else if (expected instanceof Element)
        {
            Element expectedElement = (Element) expected;
            Element actualElement = (Element) actual;

            // compare element ns
            String expectedNS = expectedElement.getNamespaceURI();
            String actualNS = (actualElement != null) ? actualElement.getNamespaceURI() : null;
            ;
            if ((expectedNS == null && actualNS != null)
                    || (expectedNS != null && !expectedNS.equals(actualNS)))
            {
                System.out.println("Element namespaces names do not match: " + expectedNS + " " + actualNS);
                compareFlag = false;
            }

            String elementName = "{" + expectedElement.getNamespaceURI() + "}"
                    + ((actualElement != null) ? actualElement.getLocalName() : null);

            // compare attributes
            NamedNodeMap expectedAttrs = expectedElement.getAttributes();
            NamedNodeMap actualAttrs = (actualElement != null) ? actualElement.getAttributes() : null;
            if (countNonNamespaceAttribures(expectedAttrs) != countNonNamespaceAttribures(actualAttrs))
            {
                System.out.println("Number of attributes do not match for Node : <" + expectedElement.getNodeName() + "/> , Expected No Of Attrs="
                        + countNonNamespaceAttribures(expectedAttrs) + ", Actual No of Attrs="
                        + countNonNamespaceAttribures(actualAttrs));
                compareFlag = false;
            }
            for (int i = 0; i < expectedAttrs.getLength(); i++)
            {
                Attr expectedAttr = (Attr) expectedAttrs.item(i);
                if (expectedAttr.getName().startsWith("xmlns"))
                {
                    continue;
                }
                Attr actualAttr = null;
                if (expectedAttr.getNamespaceURI() == null)
                {
                    actualAttr = (actualAttrs != null) ? (Attr) actualAttrs.getNamedItem(expectedAttr.getName()) : null;
                }
                else
                {
                    actualAttr = (Attr) actualAttrs.getNamedItemNS(expectedAttr.getNamespaceURI(),
                            expectedAttr.getLocalName());
                }
                if (actualAttr == null)
                {
                    System.out.println(" >>>>> Missing Attribute: " + expectedAttr);
                    compareFlag = false;
                }
                else
                {
                    if (actualAttr != null && !expectedAttr.getValue().equals(actualAttr.getValue()))
                    {
                        System.out.println("Attribute Difference: Field Name=<" + expectedAttr.getOwnerElement().getNodeName() + ">, Attribute=" + expectedAttr.getName() +
                                ", Expected=" + expectedAttr.getValue() + ", Actual=" + actualAttr.getValue());
                        compareFlag = false;
                    }
                }
            }

            // compare children
            NodeList expectedChildren = expectedElement.getChildNodes();
            NodeList actualChildren = (actualElement != null) ? actualElement.getChildNodes() : null;
            if (actualChildren != null && (expectedChildren.getLength() != actualChildren.getLength()))
            {
                if (expectedChildren.getLength() >= actualChildren.getLength())
                {
                    System.out.println("ChildNode Difference : Missing Child Nodes...Node Element=<" + expectedElement.getNodeName() + "/>, Expected="
                            + expectedChildren.getLength() + ", Actual=" + actualChildren.getLength());

                    for (int i = 0, j = 0; i < expectedChildren.getLength(); i++)
                    {

                        Node expectedChild = expectedChildren.item(i);
                        Node actualChild = actualChildren != null ? actualChildren.item(j) : null;

                        // if (actualChild != null && (expectedChild.getNodeName().equals(actualChild.getNodeName())) &&
                        // !"rate".equals(expectedChild.getNodeName()))
                        if (actualChild != null && (expectedChild.getNodeName().equals(actualChild.getNodeName())))
                        {
                            compareNodes(expectedChild, actualChild);
                            j++;
                        }
                        else
                        {
                            if (actualChild != null && !actualChild.getNodeName().equals("#text"))
                            {
                                System.out.println("        >>>>>> Missing Child Field=<" + expectedChild.getNodeName() + ">");
                            }

                        }

                    }

                }
                else
                {
                    System.out.println("ChildNode Difference : Extra ChildNodes Found...Node Element=<" + actualElement.getNodeName() + "/>, Expected="
                            + actualChildren.getLength() + ", Actual=" + actualChildren.getLength());

                    for (int i = 0, j = 0; i < actualChildren.getLength(); i++)
                    {

                        Node actualChild = actualChildren.item(i);
                        Node expectedChild = expectedChildren != null ? expectedChildren.item(j) : null;

                        // if (expectedChild != null && (actualChild.getNodeName().equals(expectedChild.getNodeName()))
                        // && !"rate".equals(actualChild.getNodeName()))
                        if (expectedChild != null && (actualChild.getNodeName().equals(expectedChild.getNodeName())))
                        {
                            compareNodes(actualChild, expectedChild);
                            j++;
                        }
                        else
                        {
                            if (actualChild != null && !actualChild.getNodeName().equals("#text"))
                            {
                                System.out.println("        >>>>>> Extra Child Field=<" + actualChild.getNodeName() + ">");
                            }

                        }

                    }

                }
                compareFlag = false;
            }
            else
            {
                for (int i = 0; i < expectedChildren.getLength(); i++)
                {

                    Node expectedChild = expectedChildren.item(i);
                    Node actualChild = actualChildren != null ? actualChildren.item(i) : null;

                    // if (actualChild != null && (expectedChild.getNodeName().equals(actualChild.getNodeName())) &&
                    // !"rate".equals(expectedChild.getNodeName()))
                    if (actualChild != null && (expectedChild.getNodeName().equals(actualChild.getNodeName())))
                    {
                        compareNodes(expectedChild, actualChild);
                        // j++;
                    }
                }
            }

        }
        else if (expected instanceof Text)
        {
            String expectedData = ((Text) expected).getData().trim();
            String actualData = (actual != null) ? ((Text) actual).getData().trim() : "";
            if (!expectedData.equals(actualData))
            {
                System.out.println("Field Difference: Field Name=<" + expected.getParentNode().getNodeName() + ">, Expected=" + expectedData + " , Actual=" + actualData);
                compareFlag = false;
            }

        }
        return compareFlag;
    }

    private static int countNonNamespaceAttribures(NamedNodeMap attrs)
    {
        int n = 0;
        if (attrs != null)
        {
            for (int i = 0; i < attrs.getLength(); i++)
            {
                Attr attr = (Attr) attrs.item(i);
                if (!attr.getName().startsWith("xmlns"))
                {
                    n++;
                }
            }
        }

        return n;
    }

}
