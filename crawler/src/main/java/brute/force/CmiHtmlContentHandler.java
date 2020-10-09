/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package brute.force;

import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CmiHtmlContentHandler extends DefaultHandler {

    private enum Element {
        DIV
    }

    private static class HtmlFactory {
        private static final Map<String, Element> name2Element;

        static {
            name2Element = new HashMap<>();
            for (Element element : Element.values()) {
                name2Element.put(element.toString().toLowerCase(), element);
            }
        }

        public static Element getElement(String name) {
            return name2Element.get(name);
        }
    }

    private final String base;

    private final List<ExtractedUrlAnchorPair> outgoingUrls;
    private ExtractedUrlAnchorPair curUrl = null;

    public CmiHtmlContentHandler(String domain) {
        base = domain;
        outgoingUrls = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
        Element element = HtmlFactory.getElement(localName);

        if ((element == Element.DIV)) {
            String address = attributes.getValue("fadresse");
            if (address != null && !address.trim().isEmpty()) {
                addFAddressRegister(address, localName);
            }
            address = attributes.getValue("adresse");
            if (address != null && !address.trim().isEmpty()) {
                addAddressRegister(address, localName);
            }
            address = attributes.getValue("badresse");
            if (address != null && !address.trim().isEmpty()) {
                addBAddressRegister(address, localName);
            }
        }
    }

    private void addFAddressRegister(String register, String tag) {
        curUrl = new ExtractedUrlAnchorPair();
        curUrl.setHref(base + "/INCLUDE/devpagex.cgi?pagex2=" + register);
        curUrl.setTag(tag);
        curUrl.setAnchor(register);
        outgoingUrls.add(curUrl);
    }

    private void addAddressRegister(String register, String tag) {
        curUrl = new ExtractedUrlAnchorPair();
        curUrl.setHref(base + "/INCLUDE/changerx2.cgi?sadrx2=" + register);
        curUrl.setTag(tag);
        curUrl.setAnchor(register);
        outgoingUrls.add(curUrl);
    }

    private void addBAddressRegister(String register, String tag) {
        curUrl = new ExtractedUrlAnchorPair();
        curUrl.setHref(base + "/INCLUDE/checkbuttondo.cgi?checkadrx2=" + register);
        curUrl.setTag(tag);
        curUrl.setAnchor(register);
        outgoingUrls.add(curUrl);
    }

    public List<ExtractedUrlAnchorPair> getOutgoingUrls() {
        return outgoingUrls;
    }

    public String getBaseUrl() {
        return base;
    }

}
