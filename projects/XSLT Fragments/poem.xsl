<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   version="1.0">

<xsl:variable name="default-title" select="'A Poem'"/>

<xsl:template match="poem">
<html>
<head>
	    <title><xsl:value-of select="title"/></title>
</head>
<body>
	<xsl:apply-templates select="title"/>
	<xsl:apply-templates select="author"/>
	<xsl:apply-templates select="stanza"/>
	<xsl:apply-templates select="date"/>
</body>
</html>
</xsl:template>


<xsl:template name="set-title">
	<xsl:param name="title" select="$default-title"/>
	
  <xsl:value-of select="$title"/>
</xsl:template>


<xsl:template match="title">
<div align="center"><h1><xsl:value-of select="."/></h1></div>
</xsl:template>

<xsl:template match="author">
<div align="center"><h2>By <xsl:value-of select="."/></h2></div>
</xsl:template>

<xsl:template match="stanza">
<p><xsl:apply-templates select="line"/></p>
</xsl:template>

<xsl:template match="line">
<xsl:if test="position() mod 2 = 0">&#160;&#160;</xsl:if>
<xsl:value-of select="."/><br/>
</xsl:template>

<xsl:template match="date">
<p><i><xsl:value-of select="."/></i></p>
</xsl:template>

</xsl:stylesheet>

