/* Generated by Together */

package sparta.xpath;

import java.io.*;
import java.util.*;

/** The root of the parse tree for an XPath expression.  This is a
 * participant in the Visitor Pattern [Gamma et al, #331].  You create
 * an XPath object (which parses an XPath expression into a parse
 * tree), create a Vistor object, and pass the visitor to the
 * XPath.accept method.

 <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
 This file is part of Sparta, an XML Parser, DOM, and XPath library.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the <a href="doc-files/LGPL.txt">GNU
 Lesser General Public License</a> as published by the Free Software
 Foundation; either version 2.1 of the License, or (at your option)
 any later version.  This library is distributed in the hope that it
 will be useful, but WITHOUT ANY WARRANTY; without even the implied
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE. </small></blockquote>
 @version  $Date: 2003/07/18 00:01:42 $  $Revision: 1.10 $
 @author Eamonn O'Brien-Strain
 */
public class XPath {

  static private final int ASSERTION = 0;

  /** Create an XPath from some steps. steps.lenght must be >= 1 */
  private XPath(boolean isAbsolute, Step[] steps) {
    for (int i = 0; i < steps.length; ++i)
      steps_.addElement(steps[i]);
    absolute_ = isAbsolute;
    string_ = null;
  }

  /** Parse an XPath from a string. */
  private XPath(String s) throws XPathException /*, IOException*/{
    //StringReader not supported in J2ME
    this(s, new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
  }

  /** Parse an XPath from a character stream. */
  private XPath(String s, Reader reader) throws XPathException {
    try {
      string_ = s;
      SimpleStreamTokenizer toks = new SimpleStreamTokenizer(reader);
      toks.ordinaryChar('/'); // '/' is not a comment
      toks.ordinaryChar('.'); // '.' is not a part of a number
      toks.wordChars(':', ':'); //Allow namespaces
      toks.wordChars('_', '_'); //Allow namespaces

      boolean multiLevel;
      if (toks.nextToken() == '/') {
        absolute_ = true;
        if (toks.nextToken() == '/') {
          multiLevel = true;
          toks.nextToken();
        } else
          multiLevel = false;
      } else {
        absolute_ = false;
        multiLevel = false;
      }
      //current token is first token of node_test production
      steps_.push(new Step(this, multiLevel, toks));
      //current token is token after last token of step production

      while (toks.ttype == '/') {
        if (toks.nextToken() == '/') {
          multiLevel = true;
          toks.nextToken();
        } else
          multiLevel = false;
        //current token is first token of node_test production
        steps_.push(new Step(this, multiLevel, toks));
        //current token is token after last token of step production
      }

      if (toks.ttype != SimpleStreamTokenizer.TT_EOF)
        throw new XPathException(this, "at end of XPATH expression", toks, "end of expression");
    } catch (IOException e) {
      throw new XPathException(this, e);
    }
    if (ASSERTION >= 2)
      if (!toString().equals(generateString())) throw new Error("Postcondition failed");
  }

  public String toString() {
    if (string_ == null) string_ = generateString();
    return string_;
  }

  private String generateString() {
    StringBuffer result = new StringBuffer();
    boolean first = true;
    for (Enumeration i = steps_.elements(); i.hasMoreElements();) {
      Step step = (Step) i.nextElement();
      if (!first || absolute_) {
        result.append('/');
        if (step.isMultiLevel()) result.append('/');
      }
      result.append(step.toString());
      first = false;
    }
    return result.toString();
  }

  /** Does this path begin with a '/' or '//' ? */
  public boolean isAbsolute() {
    return absolute_;
  }

  /** Does xpath evaluate to a string values (attribute values or
      text() nodes)*/
  public boolean isStringValue() {
    Step lastStep = (Step) steps_.peek();
    return lastStep.isStringValue();
  }

  public Enumeration getSteps() {
    return steps_.elements();
  }

  /** Return the attribute name in a trailing [@attrName] predicate.
   *  For example if the Xpath expression was "/a/b[@p='pp']/c[@q]"
   *  then the indexing attribute name would be "q"*/
  public String getIndexingAttrName() throws XPathException {
    Step step = (Step) steps_.peek();
    BooleanExpr predicate = step.getPredicate();
    if (!(predicate instanceof AttrExistsExpr))
      throw new XPathException(this,
          "has no indexing attribute name (must end with predicate of the form [@attrName]");
    return ((AttrExistsExpr) predicate).getAttrName();
  }

  /** Return the attribute name in a trailing [@attrName='attrValue'] predicate
   * or null if it someother type of predicate..
   *  For example if the Xpath expression was "/a/b[@p='pp']/c[@q='qqq']"
   *  then the indexing attribute name would be "q"*/
  public String getIndexingAttrNameOfEquals() throws XPathException {
    Step step = (Step) steps_.peek();
    BooleanExpr predicate = step.getPredicate();
    if (predicate instanceof AttrEqualsExpr)
      return ((AttrEqualsExpr) predicate).getAttrName();
    else
      return null;
  }

  /** A one-level clone in which the steps list is cloned but not the steps objects themselves.
   * It is OK for different XPaths to share Steps because they are immutable. */
  public Object clone() {
    Step[] steps = new Step[steps_.size()];
    Enumeration step = steps_.elements();
    for (int i = 0; i < steps.length; ++i)
      steps[i] = (Step) step.nextElement();
    return new XPath(absolute_, steps);
  }

  /**
   *@link aggregation
   *     @associates <{Step}>
   */
  private Stack steps_ = new Stack();

  private/*final (JDK11 problems)*/
  boolean absolute_;

  private String string_;

  ////////////////////////////////////////////////////////////////

  /** Return the xpath parse-tree object for this expression.
   *  This may be either a newly
   *   created object or a cached copy. */
  static public XPath get(String xpathString) throws XPathException //, IOException
  {
    synchronized (cache_) {
      XPath result = (XPath) cache_.get(xpathString);
      if (result == null) {
        result = new XPath(xpathString);
        cache_.put(xpathString, result);
      }
      return result;
    }
  }

  /** Create an XPath from some steps.
      The value of steps.length must be >= 1 */
  static public XPath get(boolean isAbsolute, Step[] steps) {
    XPath created = new XPath(isAbsolute, steps);
    String xpathString = created.toString();
    synchronized (cache_) {
      XPath inCache = (XPath) cache_.get(xpathString);
      if (inCache == null) {
        cache_.put(xpathString, created);
        return created;
      } else
        return inCache;

    }

  }

  /** Convenience function equivalent to get(xpathString).isStringValue() */
  static public boolean isStringValue(String xpathString) throws XPathException, IOException {
    return get(xpathString).isStringValue();
  }

  /** @associates XPath
   *  Warning grows without bound, no eviction.
   *  TODO: make LRU
   */
  static private Hashtable cache_ = new Hashtable();

}

// $Log: XPath.java,v $
// Revision 1.10  2003/07/18 00:01:42  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.9  2003/06/19 20:29:04  eobrain
// Add monitoring (in debug mode) to detect when indexing could optimize.
//
// Revision 1.8  2003/05/12 20:08:10  eobrain
// Inconsequential code change to avoid eclipse warning.
//
// Revision 1.7  2003/03/21 00:21:25  eobrain
// Allow underscores in attribute names.
//
// Revision 1.6  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.5  2003/01/09 01:17:14  yuhongx
// Use JDK1.1 API to make code work with PersonalJava (use addElement()).
//
// Revision 1.4  2002/12/13 22:42:22  eobrain
// Fix javadoc.
//
// Revision 1.3  2002/12/13 18:09:34  eobrain
// Create XPath from a sequence of steps.
//
// Revision 1.2  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.1.1.1  2002/08/19 05:04:02  eobrain
// import from HP Labs internal CVS
//
// Revision 1.9  2002/08/18 23:39:27  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.8  2002/08/15 05:11:35  eob
// getIndexingAttrName
//
// Revision 1.7  2002/06/21 00:35:19  eob
// Make work with old JDK 1.1.*
//
// Revision 1.6  2002/06/04 05:27:08  eob
// Simplify use of visitor pattern to make code easier to understand.
//
// Revision 1.5  2002/05/23 21:14:51  eob
// Better error reporting.
//
// Revision 1.4  2002/05/10 19:42:48  eob
// Add static isStringValue
//
// Revision 1.3  2002/03/26 05:31:49  eob
// Making contructor private.  Adding static factory method to manage
// cache of XPath objects.
//
// Revision 1.2  2002/02/04 22:12:59  eob
// Add boolean property to test whether this xpath returns a string.
//
// Revision 1.1  2002/02/01 01:59:09  eob
// initial
