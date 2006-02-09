<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:jd="http://www.osjava.org/jardiff/0.1" version="1.0">

  <!-- Format output as html -->
  <xsl:output method="html" indent="yes"/>

  <xsl:template match="jd:diff">
     <html>
      <head>
       <title>Comparing <xsl:value-of select="@old"/> to <xsl:value-of select="@new"/></title>
       <style type="text/css">
span.primitive { text: bold; }
span.type { text: normal; }
       </style>
      </head>
      <body>
       <h2>Comparing <xsl:value-of select="@old"/> to <xsl:value-of select="@new"/></h2>
       <table border="1" width="100%" cellpadding="3" cellspacing="0">
        <tr bgcolor="#CCCCFF" class="TableHeadingColor"> <td colspan="3"><font size="+2"> <b>Changes Summary</b></font></td></tr>
        <xsl:apply-templates/>
       </table>
      </body>
     </html>
  </xsl:template>

  <xsl:template match="jd:removed">
    <xsl:for-each select="jd:class">
      <xsl:call-template name="removed-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="removed-class">
    <tr bgcolor="ffaaaa"><td>Class removed</td><td><xsl:value-of select="@name"/></td><td><xsl:call-template name="print-class"/></td></tr>
  </xsl:template>

  <xsl:template match="jd:added">
    <xsl:for-each select="jd:class">
      <xsl:call-template name="added-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="added-class">
    <tr bgcolor="aaffaa"><td>Class added</td><td><xsl:value-of select="@name"/></td><td><xsl:call-template name="print-class"/></td></tr>
  </xsl:template>

  <xsl:template match="jd:changed">
    <xsl:for-each select="jd:classchanged/jd:changed/jd:classchange">
      <tr bgcolor="aaaaff">
        <td>Class changed</td>
        <td><xsl:value-of select="../../@name"/></td>
        <td>
        <xsl:for-each select="jd:from/jd:class">
          <xsl:call-template name="print-class"/>
        </xsl:for-each>
        <br/>
        <xsl:for-each select="jd:to/jd:class">
          <xsl:call-template name="print-class"/>
        </xsl:for-each>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:for-each select="jd:classchanged">
      <xsl:for-each select="jd:added/jd:method">
        <xsl:call-template name="added-method"/>
      </xsl:for-each>
      <xsl:for-each select="jd:removed/jd:method">
        <xsl:call-template name="removed-method"/>
      </xsl:for-each>
      <xsl:for-each select="jd:changed/jd:methodchange">
        <tr bgcolor="ccccff">
          <td>Method changed</td>
          <td><xsl:value-of select="../../@name"/></td>
          <td>
          <xsl:for-each select="jd:from/jd:method">
            <xsl:call-template name="print-method">
              <xsl:with-param name="classname" select="../../../../@name"/>
            </xsl:call-template>
          </xsl:for-each>
          <br/>
          <xsl:for-each select="jd:to/jd:method">
            <xsl:call-template name="print-method">
              <xsl:with-param name="classname" select="../../../../@name"/>
            </xsl:call-template>
          </xsl:for-each>
          </td>
        </tr>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="added-method">
    <tr bgcolor="ccffcc">
     <td>Method added</td>
     <td><xsl:value-of select="../../@name"/></td>
     <td><xsl:call-template name="print-method"><xsl:with-param name="classname" select="../../@name"/></xsl:call-template></td>
    </tr>
  </xsl:template>

  <xsl:template name="removed-method">
    <tr bgcolor="ffcccc">
     <td>Method removed</td>
     <td><xsl:value-of select="../../@name"/></td>
     <td><xsl:call-template name="print-method"><xsl:with-param name="classname" select="../../@name"/></xsl:call-template></td>
    </tr>
  </xsl:template>

  <xsl:template name="print-class">
     <code style="white-space:pre">
     <xsl:if test="@deprecated='yes'"><i>deprecated: </i></xsl:if>
     <xsl:value-of select="@access"/><xsl:value-of select="' '"/>
     <xsl:if test="@abstract='yes'">abstract<xsl:value-of select="' '"/></xsl:if>
     <xsl:if test="@static='yes'">static<xsl:value-of select="' '"/></xsl:if>
     <xsl:if test="@final='yes'">final<xsl:value-of select="' '"/></xsl:if>
     <xsl:value-of select="@name"/>
     <xsl:if test="@superclass!='java.lang.Object'"> extends <xsl:value-of select="@superclass"/></xsl:if>
     <xsl:for-each select="jd:implements">
       <xsl:if test="position()=1"> implements </xsl:if>
       <xsl:value-of select="@name"/>
       <xsl:if test="position()!=last()">, </xsl:if>
     </xsl:for-each>
     </code>
  </xsl:template>

  <xsl:template name="print-method">
    <xsl:param name="classname"/>
    <code style="white-space:pre">
      <xsl:if test="@deprecated='yes'"><i>deprecated: </i></xsl:if>
      <xsl:value-of select="@access"/><xsl:value-of select="' '"/>
      <xsl:if test="@final='yes'">final </xsl:if>
      <xsl:if test="@static='yes'">static </xsl:if>
      <xsl:if test="@synchronized='yes'">synchronized </xsl:if>
      <xsl:if test="@abstract='yes'">abstract </xsl:if>
      <xsl:choose>
        <xsl:when test="@name='&lt;init&gt;'">
          <xsl:call-template name="print-short-name">
            <xsl:with-param name="classname" select="$classname"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="jd:return/jd:type">
            <xsl:call-template name="print-type"/>
          </xsl:for-each>
          <xsl:value-of select="' '"/><xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>(<xsl:for-each select="jd:arguments/jd:type"><xsl:call-template name="print-type"/><xsl:if test="position()!=last()">, </xsl:if></xsl:for-each>)<xsl:for-each select="jd:exception"><xsl:if test="position() = 1"> throws </xsl:if><xsl:value-of select="@name"/><xsl:if test="position()!=last()">, </xsl:if></xsl:for-each></code>
  </xsl:template>

  <xsl:template name="print-type">
    <xsl:choose>
      <xsl:when test="@primitive='yes'">
        <span class="primitive"><xsl:value-of select="@name"/></span>
      </xsl:when>
      <xsl:otherwise>
        <span class="type"><xsl:value-of select="@name"/></span>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="@array='yes'">
      <xsl:call-template name="array-subscript">
        <xsl:with-param name="dimensions" select="@dimensions"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="array-subscript"><xsl:param name="dimensions"/>[]<xsl:if test="$dimensions > 1"><xsl:call-template name="array-subscript"><xsl:with-param name="dimensions" select="$dimensions - 1"/></xsl:call-template></xsl:if></xsl:template>

  <xsl:template name="print-short-name">
    <xsl:param name="classname"/>
    <xsl:choose>
      <xsl:when test="contains($classname,'.')">
        <xsl:call-template name="print-short-name">
          <xsl:with-param name="classname" select="substring-after($classname,'.')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$classname"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
