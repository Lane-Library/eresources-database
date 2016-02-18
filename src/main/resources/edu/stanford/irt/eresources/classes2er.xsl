<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://lane.stanford.edu/eresources#" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:lc="http://lane.stanford.edu/laneclasses"
                exclude-result-prefixes="lc"
                version="2.0">


    <xsl:param name="lane-host"/>

    <xsl:template match="/lc:classes">
        <xsl:for-each-group select="event_data" group-by="event_name">
            <xsl:apply-templates select="current-group()[last()]"/>
        </xsl:for-each-group>
    </xsl:template>

    <xsl:template match="event_data">
        <xsl:variable name="id" select="module_id"/>
        <xsl:variable name="title" select="normalize-space(event_name)"/>
        <xsl:variable name="description" select="normalize-space(event_description)"/>
        <eresource id="{$id}" type="class" update="19690101000000">
            <title><xsl:value-of select="$title"/></title>
            <primaryType>laneclass</primaryType>
            <type>Lane Class</type>
            <type>Lane Web Page</type>
            <keywords><xsl:value-of select="concat($description, ' ', $title)"/></keywords>
            <year><xsl:value-of select="replace(event_dates/start_date[1],'.*(\d{4}).*','$1')"/></year>
            <er-date><xsl:value-of select="replace(event_dates/start_date[1],'(\d{1,2})/(\d{1,2})/(\d{4}) .*','$3 $1 $2')"/></er-date>
            <version>
                <link>
                    <label><xsl:value-of select="concat(substring($description, 1, 36),' . . .')"/></label>
                    <url><xsl:value-of select="concat($lane-host, '/classes-consult/laneclass.html?class-id=', $id)"/></url>
                </link>
            </version>
        </eresource>
    </xsl:template>

</xsl:stylesheet>
