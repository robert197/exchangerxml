<?xml version="1.0" ?>



<!-- The command-line parameters are:
  			phase           NMTOKEN | "#ALL" (default) Select the phase for validation
    	    diagnose=true|false    Add the diagnostics to the assertion test in reports
            generate-paths=true|false   suffix messages with ":" and the Xpath
            path-format=1|2          Which built-in path display method to use. 1 is for computers. 2 is for humans.
            message-newline=true|false  add an extra newline to the end of the message
            sch.exslt.imports semi-colon delimited string of filenames for some EXSLT implementations  
   		    optimize        "visit-no-attributes"     Use only when the schema has no attributes as the context nodes
            
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias"
	xmlns:sch="http://www.ascc.net/xml/schematron"
	xmlns:iso="http://purl.oclc.org/dsdl/schematron">
	<!-- Select the import statement and adjust the path as 
   necessary for your system.
	-->
	<xsl:import href="iso_schematron_skeleton_for_saxon.xsl"/>
	<!--
		<xsl:import href="skeleton1-5.xsl"/>
		<xsl:import href="skeleton1-6.xsl"/> -->
	
	<xsl:template name="process-message">
		<xsl:param name="pattern" />
		<xsl:param name="role" />
   		<axsl:message>
      		<xsl:apply-templates mode="text"/> (<xsl:value-of select="$pattern" />
      		<xsl:if test="$role"> / <xsl:value-of select="$role" />
      		</xsl:if>)</axsl:message>
	</xsl:template>
</xsl:stylesheet>

