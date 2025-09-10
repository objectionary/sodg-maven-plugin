<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="to-text" version="2.0">
  <!--
    Here we transform SODG as part of XMIR to text representation:
    ```
    formation(b1, "qty", "price", "cost")
    dispatch(b2, b1, "qty")
    dispatch(b3, b2, "mul")
    dispatch(b5, b1, "price")
    application(b4, b3, 𝛼0, b5)
    put(b1, "cost", b4)
    ```
  -->
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="EOL">
    <xsl:value-of select="'&#10;'"/>
  </xsl:variable>
  <xsl:template match="/sodg">
    <xsl:element name="text">
      <xsl:apply-templates select="i"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="i[@name='comment']">
    <xsl:value-of select="$EOL"/>
    <xsl:text># </xsl:text>
    <xsl:value-of select="c"/>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
  <xsl:template match="i[@name!='comment']">
    <xsl:if test="@name = 'formation'">
      <xsl:value-of select="@name"/>
      <xsl:text>(</xsl:text>
      <xsl:for-each select="a">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="position() = 1">
            <xsl:value-of select="."/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="eo:escape(text())"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:text>);</xsl:text>
    </xsl:if>
    <xsl:if test="@name = 'dispatch'">
      <xsl:value-of select="@name"/>
      <xsl:text>(</xsl:text>
      <xsl:for-each select="a">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="position() = 3">
            <xsl:value-of select="eo:escape(text())"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:text>);</xsl:text>
    </xsl:if>
    <xsl:if test="@name = 'delta'">
      <xsl:value-of select="@name"/>
      <xsl:text>(</xsl:text>
      <xsl:for-each select="a">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="position() = 2">
            <xsl:value-of select="eo:escape(text())"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:text>);</xsl:text>
    </xsl:if>
    <xsl:if test="@name = 'application'">
      <xsl:value-of select="@name"/>
      <xsl:text>(</xsl:text>
      <xsl:for-each select="a">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:value-of select="."/>
      </xsl:for-each>
      <xsl:text>);</xsl:text>
    </xsl:if>
    <xsl:if test="@name = 'put'">
      <xsl:value-of select="@name"/>
      <xsl:text>(</xsl:text>
      <xsl:for-each select="a">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="position() = 2">
            <xsl:value-of select="eo:escape(text())"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:text>);</xsl:text>
    </xsl:if>
    <xsl:if test="c and not(empty(c/text()))">
      <xsl:text> # </xsl:text>
      <xsl:value-of select="c"/>
    </xsl:if>
    <xsl:value-of select="$EOL"/>
  </xsl:template>
</xsl:stylesheet>
