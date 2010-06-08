<?xml version="1.0" encoding="UTF-8"?>

<!-- 
InstView.xsl
XBRL Version 2 Instance Document Viewer Transformation

This XSLT code, when applied to a version 2 XBRL instance document, 
will output a simple HTML table containing various pieces of 
information about its items. It will load the appropriate taxonomy
information and use it to find labels to show in the output.

Placing the following line in your XBRL instance document directly before
the root <group> element will cause some browsers and processors to 
automatically run this transformation when loaded.  (You may need to 
update your XML processor for this to work.)

      <?xml-stylesheet type="text/xsl" href="InstView.xsl"?>
  
Before using this code, please see the <Disclaimer> element below. 

If you get a message such as "The system cannot locate the object specified" it is 
probably the case that the taxonomy file mentioned in the xsi:schemaLocation 
attribute at the top of the file does not exist.  Check the names and locations 
of the file and try it again.

This transformation makes use of another transformation, found in the file
TxCompress.xsl.  This file must be present in order for this transformation
to run correctly.  TxCompress.xsl is very similar to TxView.xsl, which is 
a transformation which displays various taxonomy information, except its output
is a nodeset of taxonomy information used by this transformation. 

This code has been tested on instance documents which load a single taxonomy.
It may possibly work with multiple taxonomies, but has not been tested and
the user should not assume that it will work correctly.  Any problems using
multiple taxonomies should be fairly easy to remedy. 
  
<Disclaimer>
  This code was tested on two sample file sets.  As such, it should not be considered 
  production-quality or complete in any way. It should not be used in an XBRL 
  application without further development and testing.  It is provided as a 
  convenience for developers who wish to have examples of XBRL instance document 
  processing using XSLT, and was developed by the author to produce a basic 
  instance document viewer for the purpose of reviewing XBRL V2 instance documents 
  exported from various software packages.  
  
  This code does not do a number of things: it does not respect groupings/tuples,
  perform calculations, present items based on presentation or definition arcs in 
  the taxonomy, parse or display entity/segment/scenario information, deal with 
  labels which do not have xml:lang="en", and it does not handle footnote or 
  linkbaseRef elements.
    
  As far as performance is concerned, this code will work fine for small files.  
  Working on large files, such as GAAP C&I-sized taxonomies and large samples, 
  this code may be slow.  Such XSLT features as key() may be useful to 
  improve performance.  
  
  The msxml extension function node-set() is used in this code.  Major 
  XSLT processors have a similar function.  If you are not using MSXML3 or MSXML4, you
  should change this function to the name of the node-set function your processor
  defines.  This is the only non-standard XSLT feature in this code. 

  This code and the accompanying examples were produced and tested in XML Spy 4.2 
  with MSXML4. Transformations were error free and schema validations were 
  also error-free, with the exception of the XBRL-produced xl.xsd, which has
  been modified slightly to avoid a validation error.  
</Disclaimer>
  

Please forward any comments and/or corrections to the email address below.  

This code may be distributed freely as long as it contains the following attribution:
Don Bruey, Creative Solutions
January 2002
dbruey@creativesolutions.com


-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xbrl="http://www.xbrl.org/2001/instance" xmlns:msxsl="urn:schemas-microsoft-com:xslt" xmlns:csiElt="http://www.creativesolutions.com/XBRL/elements" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.1">
  
  <xsl:output method="html" indent="yes"/>
  
  <!-- include another XSL file which handles the consolidation of 
  taxonomy information into one large XML document for use by this 
  transformation -->
  <xsl:include href="TxCompress.xsl"/>
  
  <xsl:template match="/">
    <xsl:variable name="taxonomyInfo"> 
      <xsl:call-template name="loadTaxonomies"/> 
    </xsl:variable>
    
    <xsl:variable name="contexts">
      <xsl:copy-of select="//xbrl:nonNumericContext | //xbrl:numericContext"/>   
    </xsl:variable>
      
    <xsl:call-template name="outputInstElts">
      <xsl:with-param name="txElts" select="$taxonomyInfo"/>
      <xsl:with-param name="contexts" select="$contexts"/>
    </xsl:call-template>

  </xsl:template>
  

  <!-- this template contains a loop that tells another template which 
  item to output, see the "itemOutput" template for more information -->
  <xsl:template name="outputInstElts">
    <xsl:param name="txElts"/>
    <xsl:param name="contexts"/>
    
    <HTML>
    <HR/><H2>XBRL Version 2 Instance Document Viewer</H2><HR/>
     
    <xsl:for-each select=".//*[@numericContext | @nonNumericContext]">
      <xsl:call-template name="itemOutput">
        <xsl:with-param name="item" select="."/>     
        <xsl:with-param name="txElts" select="$txElts"/>     
        <xsl:with-param name="contexts" select="$contexts"/>     
      </xsl:call-template>
    </xsl:for-each>
  </HTML>
  
  </xsl:template>
  
  <!-- this named template outputs information about each element.  Note
  that some complex information is output, such as unit and period,
  but that some other information is not, such as entity. Nothing is 
  preventing this template from generating all additional information that
  is not currently included.  The logic for determining how to output period
  and unit is somewhat involved and shows an example of how to break out the
  possible variations. 
  
  Parameters:
  $item - the XBRL item from the instance document that we're currently using
  $txElts - a large tree of taxonomy information created in the transformation 
           in TxCompress.xsl.  This step happened in the named template loadTaxonomies.
  $contexts - a nodeset containing all numericContext and nonNumericContext elements
           in this instance document.  It will be used to search for information for
           each element that comes through this template.  There are other options
           for how to handle contexts, keys() or XPath lookups inline as info is needed.
           (Using XPath lookups inline with this processing may be slow with large files.)           
  --> 
  <xsl:template name="itemOutput">
    <xsl:param name="item"/>     
    <xsl:param name="txElts"/>     
    <xsl:param name="contexts"/>  
    
   <!-- this variable will be set to a single node, which is the context with
   which the item in question is associated.  It will be used below to more
   easily get context information as output is generated. -->
   <xsl:variable name="curContext">
     <xsl:choose>
       <xsl:when test="$item/@nonNumericContext">
         <xsl:copy-of select="($contexts)//xbrl:nonNumericContext[@id= $item/@nonNumericContext]"/>        
       </xsl:when>
       <xsl:when test="$item/@numericContext">
         <xsl:copy-of select="($contexts)//xbrl:numericContext[@id= $item/@numericContext]"/>        
       </xsl:when>
     </xsl:choose>  
   </xsl:variable>    

   <!-- this variable and its code will go through the various possibilities for how
   period is specified (instant, forever, duration, startDate/duration, 
   duration/endDate, startDate/endDate). -->
   <xsl:variable name="periodValue">
     <xsl:choose>
       <xsl:when test="($curContext)//xbrl:period/xbrl:instant">
         Instant: <xsl:value-of select="($curContext)//xbrl:period/xbrl:instant"/>
       </xsl:when>
       <xsl:when test="($curContext)//xbrl:period/xbrl:duration">
         Duration: <xsl:value-of select="($curContext)//xbrl:period/xbrl:duration"/>
         <xsl:choose>
           <xsl:when test="($curContext)//xbrl:period/xbrl:startDate">
             starting <xsl:value-of select="($curContext)//xbrl:period/xbrl:startDate"/>
           </xsl:when>  
           <xsl:when test="($curContext)//xbrl:period/xbrl:endDate">
             ending <xsl:value-of select="($curContext)//xbrl:period/xbrl:endDate"/>
           </xsl:when>  
         </xsl:choose>
       </xsl:when>
       <xsl:when test="($curContext)//xbrl:period/xbrl:startDate and                         ($curContext)//xbrl:period/xbrl:endDate">
         Dates from <xsl:value-of select="($curContext)//xbrl:period/xbrl:startDate"/> to <xsl:value-of select="($curContext)//xbrl:period/xbrl:endDate"/>          
       </xsl:when>
       <xsl:when test="($curContext)//xbrl:period/xbrl:forever">
         Forever
       </xsl:when>
     </xsl:choose>  
   </xsl:variable>   <!-- end of variable $periodValue -->

   <!-- this variable parses the unit value of the context and makes up a string
   that describes the unit.  One value might look like "ISO4217:EUR/xbrli:shares" -->
   <xsl:variable name="unitValue">
     <xsl:if test="($curContext)//xbrl:unit">
       <xsl:choose>
         <xsl:when test="($curContext)//xbrl:unit/xbrl:operator">
           <xsl:choose>
             <xsl:when test="($curContext)//xbrl:unit/xbrl:operator/@name = 'divide'">
               <xsl:value-of select="($curContext)//xbrl:unit/xbrl:operator/xbrl:measure[1]"/>/<xsl:value-of select="($curContext)//xbrl:unit/xbrl:operator/xbrl:measure[2]"/>
             </xsl:when>
             <xsl:otherwise>
               <xsl:value-of select="($curContext)//xbrl:unit/xbrl:operator/xbrl:measure[1]"/>*<xsl:value-of select="($curContext)//xbrl:unit/xbrl:operator/xbrl:measure[2]"/>
             </xsl:otherwise>
           </xsl:choose>  
         </xsl:when>
         <xsl:otherwise>
           <xsl:value-of select="($curContext)//xbrl:unit/xbrl:measure"/>
         </xsl:otherwise>
       </xsl:choose>  
     </xsl:if>
   </xsl:variable>    <!-- end of variable $unitValue -->

  <!-- this variable is set to the label with xml:lang="en" for the item in question.  This
  is the only value that's retrieved from all the taxonomy information at our disposal at 
  this point.  More information could be had, such as presentation and calculation information,
  if desired.
  
  Technical note:  I thought that the predicate 
  
  [local-name(.) = local-name($item) and namespace-uri(.) = namespace-uri($item)]
  
  should have been able to be written as [name(.) = name($item)] since the elements share the same local 
  name and namespace URI, but that didn't work.  Documentation on the name() function seemed to agree, but
  it doesn't work in this case.  Let me know if you know why this is...  -->
  <xsl:variable name="labelValue">
     <xsl:value-of select="($txElts)//*[local-name(.) = local-name($item) and namespace-uri(.) = namespace-uri($item)]//csiElt:labelSet/csiElt:label[@xml:lang='en']"/>
   </xsl:variable>

  <!-- output the information into an HTML table, label, amount, etc. 
  This code just uses the variables from above to format output in a readable way. -->
  <table>
   <tr><td><H3><xsl:value-of select="$labelValue"/></H3></td>   </tr>
   <tr><td><b>Value: </b></td><td> <xsl:value-of select="$item"/></td>
   </tr>

   <tr><td><b>Taxonomy name: </b></td><td> <xsl:value-of select="local-name($item)"/></td></tr>
   <tr><td><b>Period: </b></td><td> <xsl:value-of select="$periodValue"/></td></tr>
     <xsl:if test="$unitValue">
       <tr><td><b>Unit: </b></td><td> <xsl:value-of select="$unitValue"/></td></tr>
     </xsl:if>  
   </table>
   <HR/>
  </xsl:template>

<!-- this function just calls loadSingleTaxonomy and tells it to
start with each portion of the @xsi:schemaLocation and parse its
way through to find each taxonomy that needs to be loaded. -->
<xsl:template name="loadTaxonomies">
  <xsl:variable name="schemaLoc" select="/xbrl:group/@xsi:schemaLocation"/>
  <xsl:call-template name="loadSingleTaxonomy">
    <xsl:with-param name="schemaLocPart" select="$schemaLoc"/>
    <xsl:with-param name="stringIndex" select="1"/>
  </xsl:call-template>
</xsl:template>  

<!-- parses through the string it's passed and figures out which 
taxonomy files to load.  It loads the file which corresponds to 
every second string in the schemaLocation attribute -->
<xsl:template name="loadSingleTaxonomy">
  <xsl:param name="schemaLocPart"/>
  <xsl:param name="stringIndex"/>

  <xsl:choose>
    <xsl:when test="$stringIndex mod 2 = 1">
      <xsl:variable name="fileNameStart" select="substring-after($schemaLocPart, ' ')"/>
      <xsl:variable name="fileName">
        <xsl:choose>
          <xsl:when test="contains($fileNameStart, ' ')">
            <xsl:value-of select="substring-before($fileNameStart, ' ')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$fileNameStart"/>
          </xsl:otherwise>          
        </xsl:choose>   
      </xsl:variable>
    
    <!-- this loads the taxonomy document and applies templates to everything
    in it.  The result is a nodeset of taxonomy information -->
    <xsl:variable name="taxonomyDocument" select="document($fileName)"/>
    <xsl:apply-templates select="$taxonomyDocument" mode="taxonomy"/>

    </xsl:when>
  </xsl:choose> 

  <!-- pass the rest of the string, if any, back into this function to run 
  it again on any remaining taxonomy files -->
  <xsl:if test="string($schemaLocPart)">
    <xsl:call-template name="loadSingleTaxonomy">
      <xsl:with-param name="schemaLocPart" select="substring-after($schemaLocPart, ' ')"/>
      <xsl:with-param name="stringIndex" select="$stringIndex + 1"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>  

</xsl:stylesheet>