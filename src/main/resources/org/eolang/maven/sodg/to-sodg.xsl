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
  Here we convert all objects into SODG format with the following instructions:
  1. `formation(o, attr0, attr1, ...)`
  2. `dispatch(o, base, attr)`
  3. `delta(o, data)`
  4. `lambda(o, name)`
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <xsl:apply-templates select="/object/o" mode="sodg"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
    <xsl:for-each select="//o">
      <xsl:if test="eo:abstract(.) and not(eo:has-data(.)) and not(@name=$eo:lambda)">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'formation'"/>
          <xsl:with-param name="args" select="(concat('b', position()), o/@name ! string())"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="eo:atom(.)">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'lambda'"/>
          <xsl:with-param name="args" select="(concat('b', position()), @name)"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="not(eo:abstract(.)) and @base and @name and not(starts-with(@base, 'ξ.'))">
        <xsl:choose>
          <xsl:when test="..[eo:abstract(.)] and not(preceding-sibling::*)">
            <xsl:call-template name="i">
              <xsl:with-param name="name" select="'dispatch'"/>
              <xsl:with-param name="args" select="(concat('b', position()), ('b' || (position() - 1)), @name)"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="i">
              <xsl:with-param name="name" select="'dispatch'"/>
              <xsl:with-param name="args" select="(concat('b', position()), concat('b', count(../preceding-sibling::*) + 1), @name)"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
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
