<%@page session="false" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="<%= response.getLocale() %>" xml:lang="<%= response.getLocale() %>">
<head>
<%@ include file="WEB-INF/include/header.inc" %>

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

<div class="hero-unit">
<h1>Lotus iNotes exporter for Outlook</h1>

<p>This tool will enable you to export your Lotus Notes emails, so that you can import them in Outlook.<br />
Be warned that archived emails are local to your Notes computer, and thus can not be exported with this tool!</p>

	<h2>Choose your platform</h2>
<p>
<% if (request.getHeader("User-Agent") != null && request.getHeader("User-Agent").contains("Macintosh")) { %>
	<a class="btn btn-primary btn-large" href="mac.jsp">Macintosh</a>
	<a class="btn" href="windows.jsp">Windows</a>
<% } else { %>
	<a class="btn btn-primary btn-large" href="windows.jsp">Windows</a>
	<a class="btn" href="mac.jsp">Macintosh</a>
<% } %>
</p>
</div>

<p class="alert alert-info" style="font-size: smaller; font-weight: lighter;">Confidentiality notice: your Lotus credential are never stored in any way, and are only used to access the Lotus iNotes server one on your behalf.</p>


<%@ include file="WEB-INF/include/footer.inc" %>
</body>
</html>
