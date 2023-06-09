/* Generated by Together */

package sparta.xpath;

/** an expression that returns a boolean value.

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
 @version  $Date: 2002/12/13 18:08:33 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 */
public abstract class BooleanExpr {
  public abstract void accept(BooleanExprVisitor visitor) throws XPathException;

}

// $Log: BooleanExpr.java,v $
// Revision 1.2  2002/12/13 18:08:33  eobrain
// Factor Visitor out into separate visitors for node tests and predicates.
//
// Revision 1.1.1.1  2002/08/19 05:04:05  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/19 00:41:46  eob
// Tweak javadoc comment -- add period (full stop) so that Javadoc knows
// where is end of summary.
//
// Revision 1.2  2002/08/18 23:38:30  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.1  2002/02/01 01:17:38  eob
// initial
