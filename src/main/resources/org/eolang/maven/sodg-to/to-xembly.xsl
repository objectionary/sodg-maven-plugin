<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="to-xembly" version="2.0">
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:param name="testing"/>
  <xsl:variable name="EOL">
    <xsl:value-of select="'&#10;'"/>
  </xsl:variable>
  <xsl:variable name="TAB">
    <xsl:value-of select="$EOL"/>
    <xsl:value-of select="'  '"/>
  </xsl:variable>
  <xsl:template match="/sodg">
    <xsl:element name="xembly">
      <xsl:text>ADD "graph";</xsl:text>
      <xsl:value-of select="$EOL"/>
      <xsl:apply-templates select="i"/>
    </xsl:element>
  </xsl:template>
  <!-- Add formation vertex -->
  <xsl:template match="i[@name='formation']">
    <xsl:if test="$testing = 'yes'">
      <!-- Validate the absence of vertex V1: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[1]"/>
      <xsl:text>']"; STRICT "0"; </xsl:text>
    </xsl:if>
    <!-- Add vertex V1: -->
    <xsl:text>XPATH "/graph"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ADD "v"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "id", "</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "attrs", "</xsl:text>
    <xsl:value-of select="a[position() > 1]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <xsl:template match="i[@name='lambda']">
    <!-- Go to object `o`: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>']"; STRICT "1"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Add lambda to vertex: -->
    <xsl:text>ADD "e";</xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "to", "</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "name", "</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <!-- Bind an object to it's formation -->
  <xsl:template match="i[@name='dispatch']">
    <xsl:if test="$testing = 'yes'">
      <!-- Validate the presence of vertex V2: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[2]"/>
      <xsl:text>']"; STRICT "1"; </xsl:text>
      <!-- Validate the absence of V1.A edge: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[1]"/>
      <xsl:text>']/e[@title='</xsl:text>
      <xsl:value-of select="a[3]"/>
      <xsl:text>']"; STRICT "0"; </xsl:text>
    </xsl:if>
    <!-- Delete A-edge at V1 if it already exists: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>']/e[@title='</xsl:text>
    <xsl:value-of select="a[3]"/>
    <xsl:text>']"; REMOVE; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Go to V1: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>']"; STRICT "1"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Add edge: -->
    <xsl:text>ADD "e";</xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "to", "</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "title", "</xsl:text>
    <xsl:value-of select="a[3]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <!-- Add application to graph -->
  <xsl:template match="i[@name='application']">
    <xsl:if test="$testing = 'yes'">
      <!-- Validate the presence of application vertex: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[1]"/>
      <xsl:text>']"; STRICT "1"; </xsl:text>
      <!-- Validate the absence of V2.A edge: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[2]"/>
      <xsl:text>']/e[@title='</xsl:text>
      <xsl:value-of select="a[3]"/>
      <xsl:text>']"; STRICT "0"; </xsl:text>
    </xsl:if>
    <!-- Go to V1: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>']"; STRICT "1"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Add edge: -->
    <xsl:text>ADD "e";</xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "to", "</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "bindings", "</xsl:text>
    <xsl:for-each select="a[position() &gt; 2][position() mod 2 = 1][following-sibling::a]">
      <xsl:value-of select="concat(., ':', following-sibling::a[1])"/>
      <xsl:if test="position() != last()">
        <xsl:text> </xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <!-- Add an application result to it's formation -->
  <xsl:template match="i[@name='put']">
    <xsl:if test="$testing = 'yes'">
      <!-- Validate the presence of vertex V2: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[3]"/>
      <xsl:text>']"; STRICT "1"; </xsl:text>
      <!-- Validate the absence of V1.A edge: -->
      <xsl:text>XPATH "/graph/v[@id='</xsl:text>
      <xsl:value-of select="a[1]"/>
      <xsl:text>']/e[@title='</xsl:text>
      <xsl:value-of select="a[3]"/>
      <xsl:text>']"; STRICT "0"; </xsl:text>
    </xsl:if>
    <!-- Delete A-edge at V3 if it already exists: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[3]"/>
    <xsl:text>']/e[@title='</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>']"; REMOVE; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Go to V3: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[3]"/>
    <xsl:text>']"; STRICT "1"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Add edge: -->
    <xsl:text>ADD "e";</xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "to", "</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>ATTR "attr", "</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <!-- Set delta-asset to the object -->
  <xsl:template match="i[@name='delta']">
    <!-- Go to object `o`: -->
    <xsl:text>XPATH "/graph/v[@id='</xsl:text>
    <xsl:value-of select="a[1]"/>
    <xsl:text>']"; STRICT "1"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <!-- Set data to object: -->
    <xsl:text>ADD "data"; </xsl:text>
    <xsl:value-of select="$TAB"/>
    <xsl:text>SET "</xsl:text>
    <xsl:value-of select="a[2]"/>
    <xsl:text>";</xsl:text>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <xsl:template match="i[@name='comment']">
    <!-- Ignore it -->
  </xsl:template>
  <xsl:template match="i">
    <xsl:message terminate="yes">
      <xsl:text>Unknown SODG '</xsl:text>
      <xsl:value-of select="@name"/>
      <xsl:text>'</xsl:text>
    </xsl:message>
  </xsl:template>
</xsl:stylesheet>
