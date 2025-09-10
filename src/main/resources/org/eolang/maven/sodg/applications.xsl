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
  Here we inject all application instructions.
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <xsl:variable name="root" select="count(/object/preceding-sibling::o) + 1"/>
      <xsl:for-each select="//o">
        <xsl:if test="not(eo:abstract(.)) and @base and not(@base='Φ.org.eolang.bytes') and o[@as]">
          <xsl:variable name="apos" select="concat('b', position() + 1)"/>
          <xsl:if test="not(@base='Φ.org.eolang.bytes') and not(o[1]/@base='Φ.org.eolang.bytes')">
            <xsl:for-each select="o[not(@base='Φ.org.eolang.bytes')]">
              <xsl:variable name="base" select="tokenize(../@base, '\.')[last()]"/>
              <xsl:variable name="polished" select="substring-after(@base, 'ξ.')"/>
              <xsl:call-template name="i">
                <xsl:with-param name="name" select="'application'"/>
                <xsl:with-param name="args" select="($apos, /object/sodg/i[@name='dispatch' and a[3]=$base]/a[1], @as, /object/sodg/i[@name='dispatch' and a[3]=$polished]/a[1])"/>
              </xsl:call-template>
            </xsl:for-each>
            <xsl:call-template name="i">
              <xsl:with-param name="name" select="'put'"/>
              <xsl:with-param name="args" select="(concat('b', $root), @name, $apos)"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
