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
<h1>Lotus iNotes exporter for Macintosh Outlook</h1>
</div>

<% if (Throttler.shouldThrottle()) { %>
<div class="alert alert-error">
All download slots are currently busy. Please try again in a few minutes!
</div>
<% } else { %>
<form class="form-horizontal" action="${pageContext.request.contextPath}/export" method="post">
	<input type="hidden" name="format" value="mac" />
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
		<li><img src="mac/bah-zip8.png" alt="ZIP" align="middle" /> Double-clic the downloaded .zip file to uncompress it</li>
		<li><img src="mac/producticon_outlook.png" alt="Outlook" align="middle" /> Open Outlook.app</li>
		<li>
			Choose <cite>File</cite> → <cite>Import…</cite> <i class="icon-chevron-right"></i><br />
			<img src="mac/Mac1.png" class="img-polaroid" align="top" />
		</li>
		<li>
			Choose <cite>Messages from a text file</cite> <i class="icon-chevron-right"></i><br />
			<img src="mac/Mac2.png" align="top" />
		</li>
		<li>
			Choose <cite>Messages from a text file, MBOX format</cite> <i class="icon-chevron-right"></i><br />
			<img src="mac/Mac3.png" align="top" />
		</li>
		<li>Select one of you freshly downloaded file, and import it</li>
		<li>Wash, rince, and repeat until you have imported all your mails. Congratulations!</li>
	</ol>
</fieldset>


<%@ include file="WEB-INF/include/footer.inc" %>
</body>
</html>
