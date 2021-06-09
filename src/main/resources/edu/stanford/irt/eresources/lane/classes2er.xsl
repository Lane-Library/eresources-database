<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://lane.stanford.edu/eresources#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">

    <xsl:variable name="apostrophe">'</xsl:variable>

    <xsl:template match="/classes">
        <xsl:apply-templates select="class"/>
    </xsl:template>

    <xsl:template match="class">
        <xsl:variable name="id" select="id" />
        <xsl:variable name="title" select="title" />
        <xsl:variable name="description" select="normalize-space(text_description)" />
        <xsl:variable name="description" select="replace($description,'&amp;nbsp;',' ')"/>
        <xsl:variable name="description" select="replace($description,'&amp;rsquo;',$apostrophe)"/>
        <xsl:variable name="description" select="replace($description,'&amp;#39;',$apostrophe)"/>
        <xsl:variable name="description" select="replace($description,'&amp;trade;',' ')"/>
        <xsl:variable name="presenters" select="presenter" />
        <xsl:variable name="keywords">
                <xsl:value-of select="concat($description, ' ', $title, ' ', $presenters)" />
        </xsl:variable>
        <xsl:variable name="year" select="end/year" />
        <xsl:variable name="er-date">
            <xsl:value-of select="replace(substring-before(end/datetime,'T'),'-','')" />
        </xsl:variable>
        <eresource id="class-{$id}" recordId="{$id}" type="web" update="19690101000000">
            <title>
                <xsl:value-of select="$title" />
            </title>
            <primaryType>Lane Class</primaryType>
            <type>Lane Class</type>
            <type>Lane Web Page</type>
            <keywords>
                <xsl:value-of select="$keywords" />
            </keywords>
            <year>
                <xsl:value-of select="$year" />
            </year>
            <er-date>
                <xsl:value-of select="$er-date" />
            </er-date>
            <description>
                <xsl:value-of select="$description" />
            </description>
            <xsl:for-each select="tokenize($presenters, ', ')" >
                 <publicationAuthor><xsl:value-of select="concat(substring-after(., ' ') , ', ' , substring-before(., ' '))"/></publicationAuthor>
                 <publicationAuthorFacetable><xsl:value-of select="concat(substring-after(., ' ') , ', ' , substring-before(., ' '))"/></publicationAuthorFacetable>
            </xsl:for-each>
            <version>
                <link>
                    <label>
                        <xsl:value-of select="concat(substring($description, 1, 50),' . . .')" />
                    </label>
                    <url>
                        <xsl:value-of select="url/public" />
                    </url>    
                </link>
            </version>
        </eresource>
    </xsl:template>

</xsl:stylesheet>
