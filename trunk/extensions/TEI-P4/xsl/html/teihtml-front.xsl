<!-- 
Text Encoding Initiative Consortium XSLT stylesheet family
$Date: 2004/11/15 14:47:46 $, $Revision: 1.1 $, $Author: edankert $

XSL stylesheet to format TEI XML documents to HTML or XSL FO

 
Copyright 1999-2003 Sebastian Rahtz / Text Encoding Initiative Consortium
    This is an XSLT stylesheet for transforming TEI (version P4) XML documents

    Version 3.1. Date Thu May  6 23:25:12 BST 2004

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
                                                                                
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
                                                                                
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
                                                                                
    The author may be contacted via the e-mail address

    sebastian.rahtz@computing-services.oxford.ac.uk--> 
<xsl:stylesheet
  xmlns:tei="http://www.tei-c.org/ns/1.0"

  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">



<!-- top-level stuff -->

<xsl:template match="docImprint"/>


<xsl:template match="front|titlePart">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="titlePage">
  <hr/>
  <table>
    <tr><td><b><xsl:apply-templates mode="print" select="docTitle"/></b></td></tr>
    <tr><td><i><xsl:apply-templates mode="print" select="docAuthor"/></i></td></tr>
    <tr><td><xsl:apply-templates mode="print" select="docDate"/></td></tr>
  </table>
  <hr/>
</xsl:template>

<xsl:template match="body|back" mode="split">
  <xsl:for-each select="*">
   <xsl:choose>
    <xsl:when test="starts-with(name(.),'div')">
       <xsl:apply-templates select="." mode="split"/>
    </xsl:when>
    <xsl:otherwise>
       <xsl:apply-templates select="."/>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="teiHeader"/>

<!-- author and title -->
<xsl:template match="docTitle"/>
<xsl:template match="docAuthor"/>
<xsl:template match="docDate"/>

<xsl:template match="docDate" mode="print">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="docAuthor" mode="author">
     <xsl:if test="preceding-sibling::docAuthor">
	<xsl:text>, </xsl:text>
     </xsl:if>
    <xsl:apply-templates/>
</xsl:template>



</xsl:stylesheet>
