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

  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0">

<xsl:template match="figure">

 <xsl:variable name="File">
  <xsl:choose> 
  <xsl:when test="@file">
   <xsl:value-of select="@file"/>
   <xsl:if test="not(contains(@file,'.'))">
      <xsl:value-of select="$graphicsSuffix"/>
   </xsl:if>
  </xsl:when>
  <xsl:when test="@url">
   <xsl:value-of select="@url"/>
   <xsl:if test="not(contains(@url,'.'))">
      <xsl:value-of select="$graphicsSuffix"/>
   </xsl:if>
  </xsl:when>
  <xsl:otherwise>
    <xsl:variable name="entity">
      <xsl:value-of select="unparsed-entity-uri(@entity)"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($entity,'file:')">
        <xsl:value-of select="substring-after($entity,'file:')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$entity"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:otherwise>
  </xsl:choose>
 </xsl:variable>

<xsl:if test="@id">
     <a name="{@id}"/>
</xsl:if>
<xsl:choose>
  <xsl:when test="$showFigures='true'">
    <span>
    <xsl:if test="@id">
      <xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
    </xsl:if>
   <xsl:if test="string-length(@rend) &gt;0">
    <xsl:attribute name="class"><xsl:value-of select="@rend"/></xsl:attribute>
   </xsl:if>
  <img src="{$graphicsPrefix}{$File}">
   <xsl:if test="@rend='inline'">
    <xsl:attribute name="border">0</xsl:attribute>
   </xsl:if>
   <xsl:if test="@width&gt;0 and not(contains(@width,'in'))">
    <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
   </xsl:if>
   <xsl:if test="@height&gt;0 and not(contains(@height,'in'))">
    <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
   </xsl:if>
   <xsl:attribute name="alt">
   <xsl:choose>
   <xsl:when test="figDesc">
        <xsl:value-of select="figDesc//text()"/>
   </xsl:when>
    <xsl:otherwise>
        <xsl:value-of select="head/text()"/>
    </xsl:otherwise>
     </xsl:choose>
     </xsl:attribute>
   <xsl:call-template name="imgHook"/>
 </img>
</span>
 </xsl:when>
 <xsl:otherwise>
   <hr/>
   <p>Figure <xsl:number level="any"/>
    file <xsl:value-of select="$File"/>
 <xsl:if test="figDesc">
  [<xsl:apply-templates select="figDesc//text()"/>]
 </xsl:if>
   </p>
   <hr/>
  </xsl:otherwise>
  </xsl:choose>
  <xsl:if test="head"><p>
  <xsl:if test="$numberFigures='true'" >
    Figure <xsl:number level="any"/>.<xsl:text> </xsl:text>
  </xsl:if>
  <xsl:apply-templates select="head"/>
   </p>
   </xsl:if>
</xsl:template>

<xsl:template name="imgHook"/>

<xsl:template match="figDesc"/>


</xsl:stylesheet>
