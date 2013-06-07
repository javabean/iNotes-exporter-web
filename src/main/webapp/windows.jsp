<%@page session="false" contentType="text/html; charset=UTF-8" %><%
%><%@page import="fr.cedrik.inotes.web.Throttler"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="<%= response.getLocale() %>" xml:lang="<%= response.getLocale() %>">
<head>
<%@ include file="WEB-INF/include/header.inc" %>
	<% if (Throttler.shouldThrottle()) { %>
	<meta http-equiv="Refresh" content="120;url=">
	<% } %>

	<title>iNotes exporter</title>
	<link rel="stylesheet" type="text/css" href="css.css" />
	<style type="text/css">
	</style>
	<script type="text/javascript">//<![CDATA[
	//]]>
	</script>
</head>
<% response.flushBuffer(); %>
<body>

<div class="container">

<div class="page-header">
<h1>Lotus iNotes exporter for Windows Outlook</h1>
</div>

<% if (Throttler.shouldThrottle()) { %>
<div class="alert alert-error">
All download slots are currently busy. Please try again in a few minutes!
</div>
<% } else { %>
<form class="form-horizontal" action="${pageContext.request.contextPath}/export" method="post">
	<input type="hidden" name="format" value="windows" />
	<div class="control-group">
		<label class="control-label" for="login">Your Lotus Notes login: </label>
		<div class="controls"><input class="input-large" type="text" id="login" name="login" required="required" placeholder="Your Lotus Notes login" /></div>
	</div>
	<div class="control-group">
		<label class="control-label" for="password">Your Lotus Notes password: </label>
		<div class="controls"><input class="input-large" type="password" id="password" name="password" required="required" placeholder="Your Lotus Notes password" /></div>
	</div>
	<span class="help-block">Note: exporting your emails is a time-consuming task (<em>several dozen minutes at least</em>). Please do not close this window nor refresh the page, nor click the export button several times!</span>
	<span class="help-block">Check your browser's download manager for export progress.</span>
	<div class="form-actions"><input class="btn btn-large" type="submit" name="export" id="exportButton" value="Export!" onclick="this.disabled='disabled'; this.form.submit(); return false;" /></div>
</form>
<% } %>

<fieldset>
	<legend>What's happening next?</legend>
	<p>By clicking the <code>export</code> button, you will download a compressed archive of your emails.</p>
	<ol>
		<li>Go to <a href="http://www.outlookfreeware.com/en/products/all/OutlookMessagesImportEML/" target="_new">OutlookFreeware.com EML importer</a></li>
		<li>Scroll to the bottom of the page and install both
			<a href="http://www.outlookfreeware.com/download/OutlookFreewareSetup.exe"><cite>OutlookFreeware.com Runtime</cite></a> and
			<a href="http://www.outlookfreeware.com/download/OutlookMessagesImportEMLSetup.exe"><cite>Import Messages from EML Format</cite></a>
		</li>
		<li>
			<img src="win/aa700919.zip_icone-fr-fr_MSDN.10-.png" alt="ZIP" width="26" height="33" align="middle" />
			Double-clic the previously downloaded .zip file to uncompress it
		</li>
		<li>
			<img src="win/ZA102503325.jpg" alt="Outlook" align="middle" />
			Launch Outlook
		</li>
		<li>
			Click the <cite>OutlookFreeware.com</cite> tab and then <cite>Import Messages from EML Format</cite> <i class="icon-chevron-right"></i><br />
			<img src="win/Ribbon.png" class="img-polaroid" align="top" />
		</li>
		<li>
			Import your unzipped folder <i class="icon-chevron-right"></i><br />
			<img src="win/OutlookMessagesImportEML.png" class="img-polaroid" align="top" />
		</li>
		<li>Congratulations!</li>
	</ol>
</fieldset>


<%@ include file="WEB-INF/include/footer.inc" %>
</body>
</html>
