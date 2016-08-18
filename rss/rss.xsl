<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
	<html>
	<body>
	
	<xsl:for-each select="documentcollection">
	
	<xsl:for-each select="document">
	<p>	<xsl:value-of select="@location"></xsl:value-of> </p> 
	<p>	<xsl:value-of select="rss/channel/title"></xsl:value-of></p>
	<p>	<xsl:value-of select="rss/channel/description"></xsl:value-of> </p>
		
	</xsl:for-each>
	</xsl:for-each>
	</body>
	</html>	
	</xsl:template>
	
	
	
</xsl:stylesheet>