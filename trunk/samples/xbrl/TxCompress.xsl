<!-- 
TxCompress.xsl
XBRL Version 2 Taxonomy compression

This XSLT code, when applied to a version 2 XBRL taxonomy schema 
document, will output a nodeset containing various pieces of 
information about the taxonomy and its linkbases.  It will find the 
linkbaseRef elements (those which tell the reader where the linkbases 
are located) and follow them through, loading information from each 
linkbase as it goes.  

It is intended to be used in conjunction with InstView.xsl, and on its
own, does not output anything useful. 

Before using this code, please see the <Disclaimer> element below. 

The templates below are divided up by different kinds of XLink extended
links used by XBRL.  They output nodesets of proprietary (non-XBRL-compliant)
elements.

The namespace prefix "tempLink" is used in places where this code defines its
own elements and/or attributes.  Where possible, standard prefixes (such as 
xlink) are used during the transformation.  Wherever this transformation 
changes the meaning or datatype of an element or attribute, or defines its own, 
the tempLink namespace is used.  

In many cases, the output will contain such values as "taxonomy.xsd#myElement."
This code doesn't resolve these types of names into any further detail.

If you get a message such as "The system cannot locate the object specified" it is 
probably the case that one or more linkbaseRef elements in your schema file are 
not pointing to a valid file(s). Check the names and locations of the files and 
try it again.
  
<Disclaimer>
  This code was tested on two sample file sets.  As such, it should not be considered 
  production-quality or complete in any way. It should not be used in an XBRL 
  application without further development and testing.  It is provided as a 
  convenience for developers who wish to have examples of XBRL taxonomy processing 
  using XSLT, and was developed, in part, as an exercise by the author to 
  produce a basic instance document viewer.  Some features of XBRL, 
  such as the  priority attribute, are not handled in this code. The code may or 
  may not handle such conditions as extended links contained within the taxonomy 
  schema file, or resolve linkbaseRef elements found in linkbases.  In some 
  cases, it does not traverse links in both directions.  
  
  As far as performance is concerned, this code will work fine for small files.  
  Working on large files, such as GAAP C&I-sized taxonomies, this code may be 
  slow.  Such XSLT features as key() may be useful to improve performance.  
  
  The msxml extension function node-set() is used in this code.  Major 
  XSLT processors have a similar function.  If you are not using MSXML3 or MSXML4, you
  should change this function to the name of the node-set function your processor
  defines.  This is the only non-standard XSLT feature in this code. 

  This code and the accompanying examples were produced and tested in XML Spy 4.2 
  with MSXML4. Transformations were error free and schema validations were 
  also error-free.  
</Disclaimer>
  
Please forward any comments and/or corrections to the email address below.  

This code may be distributed freely as long as it contains the following attribution:
Don Bruey, Creative Solutions
January 2002
dbruey@creativesolutions.com

-->
<xsl:stylesheet version="1.1"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:xbrllink="http://www.xbrl.org/2001/XLink/xbrllinkbase" 
   xmlns:xlink="http://www.w3.org/1999/xlink" 
   xmlns:tempLink="http://www.creativesolutions.com/XBRL/linkbase" 
   xmlns:csiElt="http://www.creativesolutions.com/XBRL/elements"
   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
   xmlns:msxsl="urn:schemas-microsoft-com:xslt">
<xsl:output method="xml" indent="yes"/>

<!-- this template gets invoked by InstView.xsl -->
<xsl:template match="/" mode="taxonomy">
  <xsl:variable name="extLinks">
    <xsl:apply-templates select="*" />
  </xsl:variable>

  <xsl:element name="csiElt:TaxonomyElements">
    <xsl:for-each select="//xsd:element">
      <xsl:call-template name="schemaOutput_XML">
        <xsl:with-param name="schemaElt" select="." /> 
        <xsl:with-param name="extendedLinks" select="$extLinks" /> 
      </xsl:call-template>
    </xsl:for-each>
  </xsl:element>
  
</xsl:template>

<xsl:template match="/">
<xsl:apply-templates select="/" mode="taxonomy" />
</xsl:template>

<!-- this named template receives two parameters, the schema element that is
being processed, and a node-list of information found in the linkbases. It outputs
a XML elements which can be further queried for information 
-->
<xsl:template name="schemaOutput_XML"> 
  <xsl:param name="schemaElt" />
  <xsl:param name="extendedLinks" />

  <xsl:variable name="namespaceURI" select="/*/@targetNamespace[1]" />

  <xsl:element name="{$schemaElt/@name}" namespace="{$namespaceURI}" >
    <xsl:attribute name="xsd:type"><xsl:value-of select="$schemaElt/@type"/></xsl:attribute>
    <xsl:attribute name="xsd:substitutionGroup"><xsl:value-of select="$schemaElt/@substitutionGroup"/></xsl:attribute>
  
    <xsl:element name="csiElt:labelSet">
      <xsl:for-each select="($extendedLinks)//tempLink:label[substring-after(@tempLink:from, '#') = $schemaElt/@name]">
        <xsl:element name="csiElt:label">
          <xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang" /></xsl:attribute>
          <xsl:value-of select="." />
        </xsl:element>
      </xsl:for-each>
    </xsl:element> 
    
    <xsl:element name="csiElt:calcSet">
      <xsl:element name="csiElt:children">
        <xsl:for-each select="($extendedLinks)//tempLink:calculationArc[substring-after(@tempLink:from, '#') =
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/parent-child']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>  
        </xsl:for-each>
      </xsl:element> 
      
      <xsl:element name="csiElt:parents">
        <xsl:for-each select="($extendedLinks)//tempLink:calculationArc[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/child-parent']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>  
        </xsl:for-each>
      </xsl:element> 
    </xsl:element> 

    <xsl:element name="csiElt:presentationSet">
      <xsl:element name="csiElt:children">
        <xsl:for-each select="($extendedLinks)//tempLink:presentationArc[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/parent-child']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>   
        </xsl:for-each>
      </xsl:element>
      
      <xsl:element name="csiElt:parents">
        <xsl:for-each select="($extendedLinks)//tempLink:presentationArc[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/child-parent']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>  
        </xsl:for-each>
      </xsl:element> 
    </xsl:element> 

    <xsl:element name="csiElt:definitionSet">
      <xsl:element name="csiElt:children">
        <xsl:for-each select="($extendedLinks)//tempLink:definitionArc[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/parent-child']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>  
        </xsl:for-each>
      </xsl:element>
      
      <xsl:element name="csiElt:parents">
        <xsl:for-each select="($extendedLinks)//tempLink:definitionArc[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/child-parent']">
          <xsl:element name="csiElt:point">
            <xsl:value-of select="@tempLink:to"/>
          </xsl:element>  
        </xsl:for-each>
      </xsl:element> 
    </xsl:element> 

    <xsl:element name="csiElt:reportSet">
      <xsl:element name="csiElt:point">
        <xsl:for-each select="($extendedLinks)//tempLink:reference[substring-after(@tempLink:from, '#') = 
                      $schemaElt/@name and @xlink:arcrole = 'http://www.xbrl.org/linkprops/arc/element-reference']">
          <xsl:copy-of select="."/>
        </xsl:for-each>
      </xsl:element>
    </xsl:element>
      
  </xsl:element>
</xsl:template>


<!-- this template matches linkbaseRef elements, creates a variable
out of the transformed contents of the linkbase, then applies 
templates to the variable.  This outputs elements which are described 
in various templates below, and will ultimately be used by the root template
at the top of this code for use during the HTML table output.
-->
<xsl:template match="xbrllink:linkbaseRef">
  <!-- load the linkbase using the document() function -->
  <xsl:variable name="lbase" select="document(@xlink:href)" />
  
  <!-- apply templates to the linkbase's contents -->
  <xsl:apply-templates select="$lbase" mode="linkbase"/>

</xsl:template>


<!-- this is the root template using linkbase mode.  This root element
template will only be invoked by the template for xbrllink:linkbaseRef
element, never by default because it has a mode attribute which is indicated
only by the linkbaseRef template above and nowhere else.
-->
<xsl:template match="/" mode="linkbase">
  <xsl:variable name="linkbaseResults">
    <!-- 
    this invokes templates on all children using the resource-based mode
    of processing data, that is, starting with resource-type elements
    and finding arcs that use them.  Labels and references are the two 
    kinds of linkbases that have templates using this mode.  
    -->
    <xsl:apply-templates select="*" mode="resource-based"/>
  
    <!-- 
    this invokes templates on all children using the arc-based mode
    of processing data, that is, starting with arc-type elements
    and finding locators and/or resources which make up the points of each arc.
    All types of extended links in XBRL are candidates for this kind
    of processing, in this transformation, only referenceLink
    links don't have a template in this example XSLT.  It could
    easily be added.  
    -->
    <xsl:apply-templates select="*" mode="arc-based"/>

  </xsl:variable>

  <!-- copy the results of this transformation to the output -->
  <xsl:copy-of select="$linkbaseResults" />

</xsl:template>


<!-- 
This template outputs label information.

Each labelArc node is processed by this template, and the "to" 
attribute is checked to see if it points to a resource-type element
(<label>) or a locator-type element (<loc>).  
     
There are two possibilities for a labelArc going from element-label: 
1) Arc from an element to label resource-type element 
2) Arc from an element to a locator-type element which points to an external 
   label resource element.

This code intentionally only outputs information about arcs which go from an element 
to a label, never label-element arcs (but it could be easily changed to go both ways.)
In practice, it is likely that the linkbase author will produce resource-type 
elements for labels, not point to external label resources.  But this template 
will handle either situation, and will handle both situations within the same extended link
when applicable. 

If the labelArc is pointing to a <label> resource element, a single node output from this template 
will look like this:

<tempLink:label 
   tempLink:from="sample_taxonomy.xsd#currentAssets" 
   tempLink:to="." 
   xlink:show="embed"  
   xlink:actuate="onRequest" 
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/element-label" 
   xml:lang="en" 
   tempLink:extRole="http://www.xbrl.org/linkprops/extended/balanceSheet">Current Assets</tempLink:label>

   where:
     tempLink:from - the href stored in the locator with the same label as the arc's xlink:from attribute
     tempLink:to - always set to '.' (period), which is based on the notation for AbbreviatedStep in 
                 XPath meaning "context node."  Since this element contains the text of the label, it uses
                 '.' to indicate that the label is found in this node.
     xlink:show - copied from  the context arc element, exactly the same values as for xlink:show in an arc-type element
     xlink:actuate - copied from the context arc element, exactly the same values as for xlink:actuate in an arc-type element
     xlink:arcrole - copied from the context arc element, exactly the same values as for xlink:arcrole in an arc-type element
     xml:lang - standard XML lang attribute - in this example, set to "en" (meaning English)
     tempLink:extRole - copied from the xlink:role attribute of the context node's extended link parent. 
     "Current Assets" - the text of the label for this example

If the labelArc is pointing to a locator, a single node output from the template will look like this:

<tempLink:label 
   tempLink:from="mylabels.xml#curAssetsLabel" 
   tempLink:to="sample_taxonomy.xsd#curAssets" 
   xlink:show="replace" 
   xlink:actuate="onRequest" 
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/label-element" 
   tempLink:extRole="http://www.xbrl.org/linkprops/extended/balanceSheet" />

   where:
     tempLink:from - the href stored in the locator with the same label as the arc's xlink:from attribute
     tempLink:to - the href stored in the locator path with the same label as the arc's xlink:to attribute
     xlink:show - copied from  the context arc element, exactly the same values as for xlink:show in an arc-type element
     xlink:actuate - copied from the context arc element, exactly the same values as for xlink:actuate in an arc-type element
     xlink:arcrole - copied from the context arc element, exactly the same values as for xlink:arcrole in an arc-type element
     tempLink:extRole - copied from the xlink:role attribute of the context node's extended link parent. 

-->
<xsl:template match="xbrllink:labelArc" mode="arc-based">

  <!-- store various information in variables for use below.  Some
  of them aren't necessary, but some are, since they are needed for use within
  XPath predicates, for example, $xlinkTo and $xlinkFrom.
  -->
  <xsl:variable name="xlinkTo" select="@xlink:to" />
  <xsl:variable name="xlinkFrom" select="@xlink:from" />
  <xsl:variable name="xlinkShow" select="@xlink:show" />
  <xsl:variable name="xlinkActuate" select="@xlink:actuate" />
  <xsl:variable name="xlinkArcrole" select="@xlink:arcrole" />

  <!-- only follow links from element to label -->
  <xsl:if test="$xlinkArcrole = 'http://www.xbrl.org/linkprops/arc/element-label'">
    <xsl:choose>
    
      <!-- if we find a label resource at the "to" end of the arc, then use it -->
      <xsl:when test="../xbrllink:label[@xlink:label = $xlinkTo]">
        <xsl:element name="tempLink:label">
          <xsl:attribute name="tempLink:from"><xsl:value-of select="../xbrllink:loc[@xlink:label = $xlinkFrom]/@xlink:href" /></xsl:attribute>
          <xsl:attribute name="tempLink:to">.</xsl:attribute>
          <xsl:attribute name="xlink:show"><xsl:value-of select="$xlinkShow" /></xsl:attribute>
          <xsl:attribute name="xlink:actuate"><xsl:value-of select="$xlinkActuate" /></xsl:attribute>
          <xsl:attribute name="xlink:arcrole"><xsl:value-of select="$xlinkArcrole" /></xsl:attribute>
          <xsl:attribute name="xml:lang" ><xsl:value-of select="../xbrllink:label[@xlink:label = $xlinkTo][1]/@xml:lang" /></xsl:attribute>
          <xsl:attribute name="tempLink:extRole"><xsl:value-of select="../@xlink:role[1]" /></xsl:attribute>
          <xsl:value-of select="../xbrllink:label[@xlink:label = $xlinkTo][1]/text()" />
        </xsl:element>
      </xsl:when>

      <!-- if we find a locator element at the "to" end of the arc, then use it -->
      <xsl:when test="../xbrllink:loc[@xlink:label = $xlinkTo]">
          <xsl:element name="tempLink:label">
          <xsl:attribute name="tempLink:from"><xsl:value-of select="../xbrllink:loc[@xlink:label = $xlinkFrom]/@xlink:href" /></xsl:attribute>
          <xsl:attribute name="tempLink:to"><xsl:value-of select="../xbrllink:loc[@xlink:label = $xlinkTo]/@xlink:href" /></xsl:attribute>
          <xsl:attribute name="xlink:show"><xsl:value-of select="$xlinkShow" /></xsl:attribute>
          <xsl:attribute name="xlink:actuate"><xsl:value-of select="$xlinkActuate" /></xsl:attribute>
          <xsl:attribute name="xlink:arcrole"><xsl:value-of select="$xlinkArcrole" /></xsl:attribute>
          <xsl:attribute name="tempLink:extRole"><xsl:value-of select="../@xlink:role[1]" /></xsl:attribute>
        </xsl:element>
      </xsl:when>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<!-- 
this empty template tells the processor to ignore label resource-type
elements if arc-based processing is underway. 
-->
<xsl:template match="xbrllink:label" mode="arc-based" />

<!-- 
calculationArc, definitionArc, and presentationArc elements are very similar
in makeup, the difference being that presentationArc and calculationArc each
contribute a single unique attribute. The conditional nature of these
attributes is handled in an <xsl:choose> block.

The transformation pulls in the "xlink:role" attribute from the parent
extended XLink element, and changes the to/from attributes from their
xlink:label references to their xlink:href names specified in locator-type
elements. It copies the rest of the attributes straight from the arc element.
It effectively merges the XLink references into a single arc-ish element which
can exist independently of locator-type elements.

The output from this template might look like this:

calculationArc:
<tempLink:calculationArc 
   tempLink:from="sample_taxonomy.xsd#assets.cash" 
   tempLink:to="sample_taxonomy.xsd#currentAssets" 
   xlink:show="replace" 
   xlink:actuate="onRequest" 
   xbrllink:weight="1" 
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/child-parent" 
   tempLink:extRole="" />
   
presentationArc:   
<tempLink:presentationArc 
   tempLink:from="sample_taxonomy.xsd#assets.cash" 
   tempLink:to="sample_taxonomy.xsd#currentAssets" 
   xlink:show="replace" 
   xlink:actuate="onRequest" 
   xbrllink:order="1"
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/child-parent" 
   tempLink:extRole="" />

definitionArc:
<tempLink:definitionArc 
   tempLink:from="sample_taxonomy#assets.cash" 
   tempLink:to="sample_taxonomy#currentAssets" 
   xlink:show="replace" 
   xlink:actuate="onRequest" 
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/child-parent" 
   tempLink:extRole="" />

  where (for all three types of arcs processed in this template)
    tempLink:from - the href stored in the locator referred to by the xlink:from attribute of this arc element   
    tempLink:to - the href stored in the locator referred to by the xlink:to attribute of this arc element   
    xlink:show - copied directly from the source arc-type element
    xlink:actuate - copied directly from the source arc-type element
    xbrllink:order - for presentationArc only - copied directly from the source arc-type element
    xbrllink:weight - for calculationArc only - copied directly from the source arc-type element
    xlink:arcrole - copied directly from the source arc-type element
    tempLink:extRole - copied from the xlink:role attribute of the context node's extended link parent. 
-->
<xsl:template 
  match="xbrllink:calculationArc | xbrllink:presentationArc | xbrllink:definitionArc" 
  mode="arc-based">

  <!-- store some information in variables for later use -->
  <xsl:variable name="fromElt" select="@xlink:from" />
  <xsl:variable name="toElt" select="@xlink:to" />
  <xsl:variable name="resultFrom" select="../xbrllink:loc[@xlink:label=$fromElt]/@xlink:href" />
  <xsl:variable name="resultTo" select="../xbrllink:loc[@xlink:label=$toElt]/@xlink:href" />
  <xsl:variable name="resultElementName" select="concat('tempLink:', local-name(.))" />

  <!-- write an element to the output with the appropriate name -->
  <xsl:element name="{$resultElementName}">
    <xsl:attribute name="tempLink:from"><xsl:value-of select="$resultFrom" /></xsl:attribute>
    <xsl:attribute name="tempLink:to"><xsl:value-of select="$resultTo" /></xsl:attribute>
    <xsl:attribute name="xlink:show"><xsl:value-of select="@xlink:show" /></xsl:attribute>
    <xsl:attribute name="xlink:actuate"><xsl:value-of select="@xlink:actuate" /></xsl:attribute>
  
    <!-- 
    calculationArc elements have a weight attribute to copy,
    presentationArc elements have an order attribute to copy.
    definitionArc elements don't have any special attributes to copy 
    -->
    <xsl:choose>
      <xsl:when test="local-name(.) = 'calculationArc'">
        <xsl:attribute name="xbrllink:weight"><xsl:value-of select="@weight" /></xsl:attribute>
      </xsl:when>
      <xsl:when test="local-name(.) = 'presentationArc'">
        <xsl:attribute name="xbrllink:order"><xsl:value-of select="@order" /></xsl:attribute>
      </xsl:when>
    </xsl:choose>

    <xsl:attribute name="xlink:arcrole"><xsl:value-of select="@xlink:arcrole" /></xsl:attribute>
    <xsl:attribute name="tempLink:extRole"><xsl:value-of select="../@xlink:role[1]" /></xsl:attribute>

  </xsl:element>
</xsl:template>

<!-- xbrllink:reference elements
reference elements are different in that they can contain text and markup while
other types of linkbase elements cannot (except for labels, which can only contain text).  

Output from this template might look like this:
   
<tempLink:reference xlink:href="taxonomy.xsd#currentAssets" 
   xlink:show="embed" xlink:actuate="onRequest"
   xlink:arcrole="http://www.xbrl.org/linkprops/arc/element-reference">
  <sample:publication>Publication reference</sample:publication>
</tempLink:reference>

It is also possible to do arc-based processing on referenceArcs, such as
the processing done in the arc-based label template above.  The label
arc-based template could easily be altered to work with referenceArc
elements if desired.
-->
<xsl:template match="xbrllink:reference" mode="resource-based">
  <xsl:variable name="xlinkLabel" select="@xlink:label" />

  <xsl:for-each select="../xbrllink:referenceArc[@xlink:to = $xlinkLabel]">
    <xsl:variable name="fromElt" select="@xlink:from" />
    <xsl:variable name="elementURI" select="../xbrllink:loc[@xlink:label=$fromElt]/@xlink:href" />

    <xsl:if test="$elementURI != ''">
      <xsl:element name="tempLink:reference">
        <xsl:attribute name="tempLink:from"><xsl:value-of select="$elementURI" /></xsl:attribute>
        <xsl:attribute name="xlink:show"><xsl:value-of select="@xlink:show" /></xsl:attribute>
        <xsl:attribute name="xlink:actuate"><xsl:value-of select="@xlink:actuate" /></xsl:attribute>
        <xsl:attribute name="xlink:arcrole"><xsl:value-of select="@xlink:arcrole" /></xsl:attribute>
        <xsl:attribute name="tempLink:extRole"><xsl:value-of select="../@xlink:role[1]" /></xsl:attribute>
        <xsl:copy-of select="../xbrllink:reference[@xlink:label = $xlinkLabel]/*" />
      </xsl:element>
    </xsl:if>
  </xsl:for-each>
  
</xsl:template>

<!-- default template to prevent text nodes from being output in the case that
     no templates above match  -->
<xsl:template match="text()" />

<!-- xbrllink:label elements, resource-based processing
The most common way to deal with labels is to start with the label resource 
element and find each sibling arc element which has "xlink:to" attribute set to this 
label's xlink:label.  After finding each arc, get the locator element which tells the 
beginning point of the arc pointing to this label (e.g. "taxonomy.xsd#someElement") 
and create one label element out of all the necessary information.

It is possible and legal for a labelArc to point from one locator
element to another.  In this case, this code won't work.  See the arc-based labelArc
template below for one solution for this situation. 

This template only deals with one-way links, from an element to a label. 

Sample output element:
<tempLink:label tempLink:from="sample_taxonomy.xsd#currentAssets" 
   tempLink:role="http://www.xbrl.org/linkprops/label/standard" 
   xml:lang="en" 
   tempLink:extRole="http://www.xbrl.org/linkprops/extended/balanceSheet">Current Assets</tempLink:label>

  where 
    tempLink:from is the "from" attribute of each arc that is pointing to the label
    tempLink:role is the same as the arc-type element's role
    xml:lang is the standard xml language attribute, copied from the label resource element
    tempLink:extRole is taken from the parent extended labelLink element, if applicable

NOTE:
This template is commented out to avoid duplicate label information in the output.
It can be uncommented if needed.  
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-->
<!-- 
<xsl:template match="xbrllink:label" mode="resource-based">
  <xsl:variable name="xlinkLabel" select="@xlink:label" />
  <xsl:variable name="xlinkRole" select="@xlink:role" />
  <xsl:variable name="xmlLang" select="@xml:lang" />
  <xsl:variable name="labelText" select="text()" />

  <xsl:for-each select="../xbrllink:labelArc[@xlink:to = $xlinkLabel]">
    <xsl:variable name="fromElt" select="@xlink:from" />
    <xsl:variable name="elementURI" select="../xbrllink:loc[@xlink:label=$fromElt]/@xlink:href" />

    <xsl:if test="$elementURI != ''">
      <xsl:element name="tempLink:label">
        <xsl:attribute name="tempLink:from"><xsl:value-of select="$elementURI" /></xsl:attribute>
        <xsl:attribute name="xlink:role"><xsl:value-of select="$xlinkRole" /></xsl:attribute>
        <xsl:attribute name="xml:lang" ><xsl:value-of   select="$xmlLang" /></xsl:attribute>
        <xsl:attribute name="tempLink:extRole"><xsl:value-of select="../@xlink:role[1]" /></xsl:attribute>
        <xsl:value-of select="$labelText" />
      </xsl:element>
    </xsl:if>
  </xsl:for-each>
</xsl:template>
-->

</xsl:stylesheet>
