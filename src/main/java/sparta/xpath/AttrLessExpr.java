package sparta.xpath;

/** A '@attrName < "n"' test. 

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
 @version  $Date: 2002/10/30 16:27:27 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 */
public class AttrLessExpr extends AttrRelationalExpr {

  /**
   * Constructor for AttrLessThanExpr.
   * @param attrName
   * @param attrValue
   */
  public AttrLessExpr(String attrName, int attrValue) {
    super(attrName, attrValue);
  }

  /**
   * @see sparta.xpath.BooleanExpr#accept(BooleanExprVisitor)
   */
  public void accept(BooleanExprVisitor visitor) throws XPathException {
    visitor.visit(this);
  }

  public String toString() {
    return toString("<");
  }
}
