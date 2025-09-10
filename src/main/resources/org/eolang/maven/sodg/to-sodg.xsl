<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="to-sodg" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!--
  Here we convert all objects into SODG format.
    @todo #76:30min Implement `lambda(o, name)` instruction.
     For now we are not handling lambda asset of the object. We should introduce new instruction:
     `lambda(o, name)` - that would set lambda-asset to particular object. Don't forget to add new
     test pack and remove this puzzle.
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <xsl:apply-templates select="/object/o" mode="sodg"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
    <xsl:variable name="root" select="position()"/>
    <xsl:if test="eo:abstract(.)">
      <xsl:call-template name="i">
        <xsl:with-param name="name" select="'formation'"/>
        <xsl:with-param name="args" select="(concat('b', position()), o/@name ! string())"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//o">
      <xsl:if test="not(eo:abstract(.)) and @base and @name and not(starts-with(@base, 'ξ.'))">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'dispatch'"/>
          <xsl:with-param name="args" select="(concat('b', position()), concat('b', count(../preceding-sibling::*) + 1), @name)"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="o[1]/@base='Φ.org.eolang.bytes'">
        <xsl:variable name="oid" select="concat('b', position())"/>
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'delta'"/>
          <xsl:with-param name="args" select="($oid, o[1]/o[1]/text())"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="not(eo:abstract(.)) and @base and @name and starts-with(@base, 'ξ.')">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'dispatch'"/>
          <xsl:with-param name="args" select="(concat('b', position()), concat('b', count(preceding-sibling::*)), tokenize(@base, '\.')[last()])"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
