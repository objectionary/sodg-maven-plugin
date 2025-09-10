<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="applications" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!--
  Here we inject all application SODG instructions.
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <xsl:variable name="root" select="count(/object/preceding-sibling::o) + 1"/>
      <xsl:for-each select="//o">
        <xsl:if test="not(eo:abstract(.)) and @base and not(@base='Φ.org.eolang.bytes') and o[@as]">
          <xsl:variable name="apos" select="position() + 1"/>
          <xsl:if test="not(@base='Φ.org.eolang.bytes') and not(o[1]/@base='Φ.org.eolang.bytes')">
            <xsl:for-each select="o[not(@base='Φ.org.eolang.bytes')]">
              <i>
                <xsl:attribute name="name">
                  <xsl:value-of select="'application'"/>
                </xsl:attribute>
                <a>
                  <xsl:text>b</xsl:text>
                  <xsl:value-of select="$apos"/>
                </a>
                <a>
                  <xsl:variable name="base" select="tokenize(../@base, '\.')[last()]"/>
                  <xsl:value-of select="/object/sodg/i[@name='dispatch' and a[3]=$base]/a[1]"/>
                </a>
                <a>
                  <xsl:value-of select="@as"/>
                </a>
                <a>
                  <xsl:variable name="polished" select="substring-after(@base, 'ξ.')"/>
                  <xsl:value-of select="/object/sodg/i[@name='dispatch' and a[3]=$polished]/a[1]"/>
                </a>
              </i>
            </xsl:for-each>
            <i>
              <xsl:attribute name="name">
                <xsl:value-of select="'put'"/>
              </xsl:attribute>
              <a>
                <xsl:text>b</xsl:text>
                <xsl:value-of select="$root"/>
              </a>
              <a>
                <xsl:value-of select="@name"/>
              </a>
              <a>
                <xsl:text>b</xsl:text>
                <xsl:value-of select="$apos"/>
              </a>
            </i>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
