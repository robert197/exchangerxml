<!-- 
Text Encoding Initiative Consortium XSLT stylesheet family
$Date: 2004/11/15 14:47:47 $, $Revision: 1.1 $, $Author: edankert $

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


<xsl:template name="noteN">
  <xsl:choose>
   <xsl:when test="@n">
    <xsl:value-of select="@n"/>
   </xsl:when>
   <xsl:when test="ancestor::front">
    <xsl:number level="any"  count="note[@place='foot']" from="front"/>
   </xsl:when>
   <xsl:when test="ancestor::back">
    <xsl:number level="any"  count="note[@place='foot']" from="back"/>
   </xsl:when>
   <xsl:otherwise>
    <xsl:number level="any" count="note[@place='foot']" from="body"/>
   </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="noteID">
  <xsl:choose>
   <xsl:when test="@id">
    <xsl:value-of select="@id"/>
   </xsl:when>
   <xsl:when test="@n">
    <xsl:value-of select="@n"/>
   </xsl:when>
   <xsl:when test="ancestor::front">
    <xsl:number level="any"  count="note[@place='foot']" from="front"/>
   </xsl:when>
   <xsl:when test="ancestor::back">
    <xsl:number level="any"  count="note[@place='foot']" from="back"/>
   </xsl:when>
   <xsl:otherwise>
    <xsl:number level="any" count="note[@place='foot']" from="body"/>
   </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template match="note">

<xsl:choose>

 <xsl:when test="ancestor::bibl">
  (<xsl:apply-templates/>)
 </xsl:when>

 <xsl:when test="@place='inline'">
   <xsl:text> (</xsl:text>
    <xsl:apply-templates/>
   <xsl:text>)</xsl:text>
 </xsl:when>

 <xsl:when test="@place='display'">
   <blockquote>NOTE:
    <xsl:apply-templates/>
   </blockquote>
 </xsl:when>


 <xsl:when test="@place='foot'">
  <xsl:variable name="identifier">
    <xsl:call-template name="noteID"/>
  </xsl:variable>
  <xsl:choose>
   <xsl:when test="$footnoteFile">
    <a class="notelink" href="{$masterFile}-notes.html#{concat('Note',$identifier)}">
    <sup><xsl:call-template name="noteN"/></sup></a>
   </xsl:when>
   <xsl:otherwise>
    <a class="notelink" href="#{concat('Note',$identifier)}">
    <sup><xsl:call-template name="noteN"/></sup></a>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:when>

 <xsl:otherwise>
   <xsl:text> [Note: </xsl:text>
    <xsl:apply-templates select="." mode="printnotes"/>
   <xsl:text>]</xsl:text>
 </xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template name="printNotes">
<xsl:choose>
<xsl:when test="not($footnoteFile='')">
<xsl:variable name="BaseFile">
  <xsl:value-of select="$masterFile"/>
  <xsl:call-template name="addCorpusID"/>
</xsl:variable>

  <xsl:call-template name="outputChunk">
  <xsl:with-param name="ident">
    <xsl:value-of select="concat($BaseFile,'-notes')"/>
  </xsl:with-param>
  <xsl:with-param name="content">
    <xsl:call-template name="writeNotes"/>
  </xsl:with-param>
  </xsl:call-template>
</xsl:when>

<xsl:otherwise>
  <xsl:apply-templates select="text//note[@place='foot']" mode="printnotes"/>
</xsl:otherwise>

</xsl:choose>
</xsl:template>

<xsl:template name="printDivnotes">
 <xsl:variable name="ident">
   <xsl:apply-templates select="." mode="ident"/>
 </xsl:variable>
<xsl:if test=".//note[@place='foot'] and $footnoteFile=''">
<div class="notes">
 <xsl:apply-templates select=".//note[@place='foot']" mode="printnotes">
    <xsl:with-param name="root" select="$ident"/>
  </xsl:apply-templates>
</div>
</xsl:if>
</xsl:template>

<xsl:template match="note" mode="printnotes">
  <xsl:param name="root"/>
  
  <xsl:if test="not(ancestor::bibl)">
    <xsl:variable name="identifier">
      <xsl:call-template name="noteID"/>
    </xsl:variable>
    <xsl:variable name="parent">
      <xsl:call-template name="locateParentdiv"/>
    </xsl:variable>
    <xsl:if test="$verbose">
      <xsl:message>Note <xsl:value-of select="$identifier"/> with parent <xsl:value-of select="$parent"/> requested in <xsl:value-of select="$root"/></xsl:message>
    </xsl:if>
    <p>
      <a name="{concat('Note',$identifier)}"><xsl:call-template name="noteN"/>. </a>
      <xsl:apply-templates/>
    </p>
  </xsl:if>
  
</xsl:template>


<xsl:template name="writeNotes">
 <html><xsl:call-template name="addLangAtt"/> 
 <head>
 <title>Notes for
    <xsl:apply-templates select="descendant-or-self::text/front//docTitle//text()"/></title>
 <xsl:call-template name="includeCSS"/>
 </head>
 <body>
 <xsl:call-template name="bodyHook"/>
 <xsl:call-template name="bodyJavaScript"/>
 <xsl:call-template name="stdheader">
  <xsl:with-param name="title">
   <xsl:text>Notes for </xsl:text>
    <xsl:apply-templates select="descendant-or-self::text/front//docTitle//text()"/>
  </xsl:with-param>
 </xsl:call-template>

 <xsl:call-template name="processFootnotes"/>

 <xsl:call-template name="stdfooter">
       <xsl:with-param name="date">
         <xsl:choose>
          <xsl:when test="ancestor-or-self::TEI.2/teiHeader/revisionDesc//date[1]">
            <xsl:value-of select="ancestor-or-self::TEI.2/teiHeader/revisionDesc//date[1]"/>
          </xsl:when>
          <xsl:otherwise>
    	   <xsl:value-of select="ancestor-or-self::TEI.2//front//docDate"/>
          </xsl:otherwise>    
         </xsl:choose>
       </xsl:with-param>
       <xsl:with-param name="author">
         <xsl:apply-templates select="ancestor-or-self::TEI.2//front//docAuthor" mode="author"/>
       </xsl:with-param>
 </xsl:call-template>
 </body>
 </html>
</xsl:template>

<xsl:template name="processFootnotes">
  <xsl:apply-templates select="text//note[@place='foot']" mode="printnotes"/>
</xsl:template>
</xsl:stylesheet>
