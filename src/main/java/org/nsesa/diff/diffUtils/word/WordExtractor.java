/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.diff.diffUtils.word;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.text.BreakIterator;
import java.util.*;

/**
 * <p>
 * This class extracts a list of words(actual words, punctuations, symbols,
 * whitespaces) from a given text. Text can be plain text or html. In case of
 * html the word will contain references to the markup in order to provide a way
 * at a later point in time to reconstruct the html.
 * </p>
 * <p>
 * Applying extractWords to &lt;p&gt;Lorem ipsum dolor sit amet.&lt;/p&gt;
 * generates a result containing a List with the following words:
 * <ul>
 * <li>Lorem</li>
 * <li></li>
 * <li>ipsum</li>
 * <li></li>
 * <li>dolor</li>
 * <li></li>
 * <li>sit</li>
 * <li></li>
 * <li>amet</li>
 * <li>.</li>
 * </ul>
 * <br/>
 * The template will be: &lt;p&gt;{}{}{}{}{}{}{}{}{}{}&lt;/p&gt;. Later this
 * template can be used to construct a result containing the words with their
 * individual diff markup applied, guaranteeing the markup stays unchanged.
 * </p>
 *
 * @author Stefan Koulouris (stefan.koulouris@gmail.com)
 */
public class WordExtractor {

    /**
     * The result returned to the caller, containing a List of real words and a
     * template holding the skeleton of the markup.
     *
     * @author Stefan Koulouris (stefan.koulouris@gmail.com)
     */
    public static class WordExtractionResult {
        public LinkedList<Word> words;
        public String template;
    }

    @SuppressWarnings("serial")
    private static final NodeFilter ACCEPT_ALL_NODE_FILTER = new NodeFilter() {
        @Override
        public boolean accept(Node node) {
            return true;
        }
    };

    /**
     * Extract words from the text.
     *
     * @param text
     * @return
     */
    public WordExtractionResult extractWords(String text) {
        return extractWords(text, Locale.getDefault());
    }

    /**
     * Extract words from the text.
     *
     * @param text
     * @param locale
     * @return
     */
    public WordExtractionResult extractWords(String text, Locale locale) {
        LinkedList<Word> words = new LinkedList<Word>();

        List<Node> nodes = getNodes(text);
        StringBuilder templateBuilder = new StringBuilder();
        for (Node node : nodes) {
            if (node instanceof TagNode) {
                TagNode tagNode = (TagNode) node;
                StringBuffer tagRaw = new StringBuffer();
                Vector<Attribute> attrs = tagNode.getAttributesEx();
                if (attrs != null && attrs.size() > 0) {
                    for (Attribute attribute : attrs) {
                        tagRaw.append(attribute.toString());
                    }
                }
                if (tagRaw.toString().isEmpty()) {
                    tagRaw.append(tagNode.getRawTagName());
                }

                templateBuilder.append("<" + tagRaw.toString() + ">");
            } else if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;

                String source = textNode.getText();
                BreakIterator boundary = BreakIterator.getWordInstance(locale);
                boundary.setText(source);
                int start = boundary.first();
                for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
                    Word word = new Word(source.substring(start, end));
                    templateBuilder.append("{}");
                    words.add(word);
                }
            } else {
                throw new RuntimeException();
            }
        }

        WordExtractionResult extractionResult = new WordExtractionResult();
        extractionResult.words = words;
        extractionResult.template = templateBuilder.toString();

        return extractionResult;
    }

    protected List<Node> getNodes(String text) {
        List<Node> nodes = new ArrayList<Node>();

        // Surround with tags in order to always be sure we have root
        String workingCopy = "<diff>" + text + "</diff>";
        Parser parser;
        try {
            parser = new Parser(workingCopy);
            NodeList extractedNodesThatMatch = parser.extractAllNodesThatMatch(ACCEPT_ALL_NODE_FILTER);
            SimpleNodeIterator elements = extractedNodesThatMatch.elements();
            while (elements.hasMoreNodes()) {
                nodes.add(elements.nextNode());
            }
        } catch (final ParserException e) {
            throw new RuntimeException(e);
        }

        // Remove diff tags
        nodes.remove(0);
        nodes.remove(nodes.size() - 1);

        return nodes;
    }

}
