<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://lane.stanford.edu/eresources#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:lc="http://lane.stanford.edu/laneclasses" exclude-result-prefixes="lc" version="2.0">


	<xsl:param name="lane-host" />

	<xsl:template match="/lc:classes">
		<xsl:for-each-group select="lc:event_data" group-by="lc:event_name">
            <xsl:apply-templates select="current-group()[last()]"/>
        </xsl:for-each-group>
	</xsl:template>

	<xsl:template match="lc:event_data">
		<xsl:variable name="id" select="lc:module_id" />
		<xsl:variable name="title" select="normalize-space(lc:event_name)" />
		<xsl:variable name="description" select="normalize-space(lc:event_description)" />
		<xsl:variable name="firstName" select="lc:event_instructors/lc:instructor/lc:fname"/>
		<xsl:variable name="lastName" select="lc:event_instructors/lc:instructor/lc:lname"/>
		<eresource id="class-{$id}" recordId="{$id}" type="web" update="19690101000000">
			<title>
				<xsl:value-of select="$title" />
			</title>
			<primaryType>Lane Class</primaryType>
			<type>Lane Class</type>
			<type>Lane Web Page</type>
			<keywords>
				<xsl:value-of select="concat($description, ' ', $title, ' ', string-join( $firstName,' '), ' ', string-join( $lastName, ' '))" />
			</keywords>
			<year>
				<xsl:value-of select="replace(lc:event_dates/lc:start_date[1],'.*(\d{4}).*','$1')" />
			</year>
			<er-date>
				<xsl:value-of select="replace(lc:event_dates/lc:start_date[1],'(\d{1,2})/(\d{1,2})/(\d{4}) .*','$3 $1 $2')" />
			</er-date>
			<description>
				<xsl:value-of select="$description" />
			</description>
			<xsl:for-each select="./lc:event_instructors/lc:instructor" >
				 <xsl:if test="not(contains(./lc:lname, ','))">
				 	<publicationAuthorFacetable><xsl:value-of select="concat(./lc:lname , ', ' , ./lc:fname)"/></publicationAuthorFacetable>
				 </xsl:if>
				 <xsl:if test="contains(./lc:lname, ',')">
				 	<publicationAuthor><xsl:value-of select="concat(substring-before(./lc:lname, ',') , ', ' , ./lc:fname)"/></publicationAuthor>
				 	<publicationAuthorFacetable><xsl:value-of select="concat(substring-before(./lc:lname, ',') , ', ' , ./lc:fname)"/></publicationAuthorFacetable>
				 </xsl:if>
			</xsl:for-each>
			<version>
				<link>
					<label>
						<xsl:value-of select="concat(substring($description, 1, 36),' . . .')" />
					</label>
					<url>
						<xsl:choose>
						<xsl:when test="lc:event_status = 'O'">
								<xsl:value-of select="concat($lane-host, '/classes-consult/laneclass.html?class-id=', $id)" />
						</xsl:when>
						<xsl:otherwise>
								<xsl:value-of select="concat($lane-host, '/classes-consult/archive.html?class-id=', $id)" />
						</xsl:otherwise>
					</xsl:choose>
					</url>	
				</link>
			</version>
		</eresource>
	</xsl:template>

</xsl:stylesheet>
