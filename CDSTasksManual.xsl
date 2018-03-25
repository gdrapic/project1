<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" version="4.0" encoding="iso-8859-1" indent="yes"/>


<xsl:template name="lf2br">
	<!-- import $StringToTransform -->
	<xsl:param name="StringToTransform"/>
	<xsl:choose>
		<!-- string contains linefeed -->
		<xsl:when test="contains($StringToTransform,'&#xA;')">
			<!-- output substring that comes before the first linefeed -->
			<!-- note: use of substring-before() function means        -->
			<!-- $StringToTransform will be treated as a string,       -->
			<!-- even if it is a node-set or result tree fragment.     -->
			<!-- So hopefully $StringToTransform is really a string!   -->
			<xsl:value-of select="substring-before($StringToTransform,'&#xA;')"/>
			<!-- by putting a 'br' element in the result tree instead  -->
			<!-- of the linefeed character, a <br> will be output at   -->
			<!-- that point in the HTML                                -->
			<br/>
			<!-- repeat for the remainder of the original string -->
			<xsl:call-template name="lf2br">
				<xsl:with-param name="StringToTransform">
					<xsl:value-of select="substring-after($StringToTransform,'&#xA;')"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<!-- string does not contain newline, so just output it -->
		<xsl:otherwise>
			<xsl:value-of select="$StringToTransform"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template  match = "text()" />

<xsl:template match="/document">
<HTML>
<HEAD>
<TITLE><xsl:value-of select="./@title"/></TITLE>

<script type="text/javascript">

	function ShowHide( id, ref, msg )
	{
		var node = document.getElementById(id);
		
		if(node.style.display == "none"){
			node.style.display = "block";
			ref.innerHTML = '<img src="images/entities_expend.gif" border="0"/>' + msg;
		}
		else {
			node.style.display = "none";	
			ref.innerHTML = '<img src="images/entities.gif" border="0"/>' + msg;		
		}
		return false;		
	}

</script>

<STYLE>
table { border: 1px solid #D3D3D3;}
th {border: 1px solid #D3D3D3; background:#D3D3D3; margin:0; font:10pt Tahoma;}
td {border: 1px solid #D3D3D3; color: black; margin:0; font:10pt Tahoma;}

p {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px;}
pre {background: #ffffdd; border: 1px solid #999999; color: #000000; padding: 5px 5px 5px 7px; font-size: 12px; line-height: normal;}

.attribute_name { background-color: #E9E9E9; font-family:sans-serif; font-size:10pt; font-weight: bold; }
.attribute_value { background-color: #FFFFCC; font-family:monospace; font-size:10pt;  }
.node_name { background-color: #E9E9E9; font-family:sans-serif; font-size:10pt; }
.node_value { background-color: #FFFFCC; font-family:monospace; font-size:10pt; }
.node_text { background-color: #FFFFCC; font-family:monospace; font-size:10pt; }

/*ul {border-width: 1px; border-left: 1px dotted black; text-align: left;}*/

.folder { list-style-position: inside; list-style-image: url(images/icons/16/folder.png); }
.delete { list-style-position: inside; list-style-image: url(images/icons/16/delete-file-icon.png); }
.file { list-style-position: inside; list-style-image: url(images/icons/16/file.gif); }
.textFile { list-style-position: inside; list-style-image: url(images/icons/16/fill.png); }
.copyFile { list-style-position: inside; list-style-image: url(images/icons/16/copyFile.png); }
.readTextFile { list-style-position: inside; list-style-image: url(images/icons/16/fill-180.png); }
.zip { list-style-position: inside; list-style-image: url(images/icons/16/zip.png); }
.tasks { list-style-position: inside; list-style-image: url(images/icons/16/tasks.gif); }
.task { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.page { list-style-position: inside; list-style-image: url(images/dtree/page.gif); }    
.property { list-style-position: inside; list-style-image: url(images/icons/16/property.png); }  
.format { list-style-position: inside; list-style-image: url(images/icons/16/page_1.gif); }  
.condition { list-style-position: inside; list-style-image: url(images/icons/16/question.jpg); }  
.ftp { list-style-position: inside; list-style-image: url(images/icons/16/ftp.png); }  
.put { list-style-position: inside; list-style-image: url(images/icons/16/fill-090.png); }  
.get { list-style-position: inside; list-style-image: url(images/icons/16/fill-270.png); }  
.sqlQuery { list-style-position: inside; list-style-image: url(images/icons/16/reports.png); }  
.export { list-style-position: inside; list-style-image: url(images/icons/16/export.gif); }  
.email { list-style-position: inside; list-style-image: url(images/icons/16/envelope.png); }  
.attach { list-style-position: inside; list-style-image: url(images/icons/16/attachment.png); }  

/*missing icons*/
.renameFile { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.quote { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.sftp { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.cmd { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.smb { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.sqlTransaction { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.sql { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.exportCsv { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.exportXls { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.exportHtml { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }
.fileFormat { list-style-position: inside; list-style-image: url(images/icons/16/task.gif); }


.attribute{	
	list-style-position: inside; 
	list-style-image: url(images/icons/16/a.gif);}  
.attribute_key{	
	font-family: sans-serif;
	font-size: 12px;
	/*padding-left: 5px;*/
	text-align: left;
	text-indent: 4px;
	color: green;}  
.attribute_value{
	font-family: sans-serif;
	font-size: 12px;
	/*padding-left: 5px;*/
	text-align: left;
	text-indent: 4px;
	color: maroon;}
.text { list-style-position: inside; list-style-image: url(images/icons/16/text.gif); }  
.note,.NOTE { list-style-position: inside; list-style-image: url(images/icons/16/note.png); }  

</STYLE>

</HEAD>
<BODY>
	<h2><xsl:value-of select="./@title"/></h2>
	<ul>
		<xsl:apply-templates select="section"/>
	</ul>
</BODY>
</HTML>
</xsl:template>

<xsl:template match="section">
	<xsl:variable name="thisNodeId" select="generate-id(.)"/>
  	<li> 
  		<a href="#" onclick="return ShowHide( '{$thisNodeId}', this,  ' {./@title} ' );">
  			<img src="images/entities.gif" border="0" style="padding-right: 5px;"/>
  			<span style="padding-right: 5px;"><xsl:value-of select="./@title"/></span> 
  		</a>  
		<div id="{$thisNodeId}" style="display: none">
  			<ul>
  			<xsl:apply-templates/>
  			</ul>
  		</div>
  	</li>
</xsl:template>

<xsl:template match="paragraph">
  	<p>
  		<xsl:call-template name="lf2br">
				<xsl:with-param name="StringToTransform" select="."/>
		</xsl:call-template>
  	</p>
</xsl:template>

<xsl:template match="description">
  	<p>
  		<xsl:call-template name="lf2br">
				<xsl:with-param name="StringToTransform" select="."/>
		</xsl:call-template>
  	</p>
</xsl:template>

<xsl:template match="component">
<xsl:variable name="thisNodeId" select="generate-id(.)"/>
<li class="{./@name}">
	<a href="#" onclick="return ShowHide( '{$thisNodeId}', this,  ' {./@name} ' );">
 		<img src="images/entities.gif" border="0" style="padding-right: 5px;"/>
 		<span style="padding-right: 5px;"><xsl:value-of select="./@name"/></span> 
 	</a> 
	<div id="{$thisNodeId}" style="display: none; border: 1px solid #D3D3D3;">
		<p><xsl:apply-templates select="description"/></p>
		<p>Class: <xsl:value-of select="./@class"/></p>
		<xsl:apply-templates select="parameters"/>
		<xsl:apply-templates select="examples"/>
		<hr/>
	</div>
</li>
</xsl:template>

<xsl:template match="parameters">
	<h4>Parameters:</h4>
	<table>
		<tr>
			<th>Type</th>
			<th>Name</th>
			<th>Required</th>
			<th>Default Value</th>
			<th>Description</th>
		</tr>
	<xsl:apply-templates select="parameter"/>
	</table>
</xsl:template>

<xsl:template match="parameter">
	<tr>
		<td valign="top"><xsl:value-of select="./@type"/></td>
		<td valign="top" class="attribute_name"><xsl:value-of select="./@name"/></td>
		<td valign="top"><xsl:value-of select="./@required"/></td>
		<td valign="top"><xsl:value-of select="./defaultValue"/></td>
		<td valign="top"><xsl:apply-templates select="description"/></td>
	</tr>
</xsl:template>

<xsl:template match="examples">
	<h4>Examples: <xsl:value-of select="./@name"/></h4>
	<xsl:apply-templates select="example"/>
</xsl:template>

<xsl:template match="example">
	<p><xsl:apply-templates select="description"/></p>	
	<pre><xsl:value-of select="translate(.,'`','&#62;')"/></pre> 
</xsl:template>

</xsl:stylesheet>