<?xml version="1.0" encoding="UTF-8"?>

<!-- This stylesheet demonstrates the use of element extensibility with SAXON -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sql="java:/net.sf.saxon.sql.SQLElementFactory" xmlns:saxon="http://saxon.sf.net/" xmlns:java="http://saxon.sf.net/java-type" version="1.0" extension-element-prefixes="saxon" exclude-result-prefixes="java">

<!-- insert your database details here, or supply them in parameters -->
<xsl:param name="driver" select="'org.gjt.mm.mysql.Driver'"/>
<xsl:param name="database" select="'jdbc:mysql://localhost/test'"/>  
<xsl:param name="user"/>
<xsl:param name="password"/>
<xsl:param name="table"/>
<xsl:param name="column" select="'*'"/>
<xsl:param name="where"/>
<xsl:param name="row-tag"/>
<xsl:param name="column-tag"/>



<!-- This stylesheet queries a SQL database -->

<xsl:variable name="count" select="0" saxon:assignable="yes"/>

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">

<xsl:choose>
    <xsl:when test="not(element-available('sql:connect'))">
        <xsl:message>sql:connect is not available</xsl:message>
    </xsl:when>
    <xsl:otherwise>

    <xsl:message>Connecting to <xsl:value-of select="$database"/>...</xsl:message>


    <xsl:variable name="connection" as="java:java.lang.Object">
        <sql:connect database="{$database}" user="{$user}" password="{$password}" driver="{$driver}" xsl:extension-element-prefixes="sql">
	        <xsl:fallback>
	            <xsl:message terminate="yes">SQL extensions are not installed</xsl:message>
            </xsl:fallback>
        </sql:connect>
    </xsl:variable>

    <xsl:message>Connected...</xsl:message>

    <xsl:variable name="results">

    <sql:query connection="$connection" table="$table" xsl:extension-element-prefixes="sql"/> 

	<xsl:attribute name="column">
		<xsl:choose>
			<xsl:when test="$column"><xsl:value-of select="$column"/></xsl:when>
			<xsl:otherwise><xsl:text>*</xsl:text></xsl:otherwise>
		</xsl:choose>
	</xsl:attribute>
	<xsl:attribute name="row-tag">
		<xsl:choose>
			<xsl:when test="$row-tag"><xsl:value-of select="$row-tag"/></xsl:when>
			<xsl:otherwise><xsl:text>row</xsl:text></xsl:otherwise>
		</xsl:choose>
	</xsl:attribute>
	<xsl:attribute name="column-tag">
		<xsl:choose>
			<xsl:when test="$column-tag"><xsl:value-of select="$column-tag"/></xsl:when>
			<xsl:otherwise><xsl:text>*</xsl:text></xsl:otherwise>
		</xsl:choose>
	</xsl:attribute>
	
	<xsl:if test="$where"><xsl:attribute name="where"><xsl:value-of select="$where"/></xsl:attribute></xsl:if>
	

    </xsl:variable>
    
    <db-output>
        <xsl:copy-of select="$results"/>
    </db-output>
    
    <sql:close connection="$connection" xsl:extension-element-prefixes="sql">
       <xsl:fallback/>
    </sql:close>

    </xsl:otherwise>
</xsl:choose>	


</xsl:template>


</xsl:stylesheet>