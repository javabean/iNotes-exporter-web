/**
 *
 */
package fr.cedrik.inotes.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipOutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import fr.cedrik.inotes.FoldersList;
import fr.cedrik.inotes.INotesProperties;
import fr.cedrik.inotes.Session;

/**
 * @author C&eacute;drik LIME
 */
public class ExportServlet extends HttpServlet implements Servlet {

	private URL webmailURL;

	/**
	 *
	 */
	public ExportServlet() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			process(request, response);
		} finally {
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			process(request, response);
		} finally {
		}
	}

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		String format = request.getParameter("format");
		BaseExporter exporter = null;
		if ("pc".equalsIgnoreCase(format) || "win".equalsIgnoreCase(format) || "windows".equalsIgnoreCase(format) || "microsoft".equalsIgnoreCase(format) || "eml".equalsIgnoreCase(format)) {
			exporter = new EMLExporter();
		} else if ("mac".equalsIgnoreCase(format) || "macintosh".equalsIgnoreCase(format) || "apple".equalsIgnoreCase(format) || "mbox".equalsIgnoreCase(format)) {
			exporter = new MBoxExporter();
		}
		if (StringUtils.isBlank(login) || StringUtils.isBlank(password) || exporter == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing login, password or format parameter");
			return;
		}

		INotesProperties iNotes = new INotesProperties(INotesProperties.FILE);
		iNotes.setServerAddress(webmailURL);
		iNotes.setUserName(login);
		iNotes.setUserPassword(password);
		Session session = new Session(iNotes);
		if (! session.login(iNotes.getUserName(), iNotes.getUserPassword())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Can not login user "+iNotes.getUserName());
			return;
		}

		try {
			Throttler.newExport();
			// export folders hierarchy
			FoldersList folders = session.getFolders();
			response.setContentType("application/zip");//$NON-NLS-1$
			String currentDate = DateFormatUtils.ISO_DATE_FORMAT.format(new Date());
			{
				String theFileName = login + '_' + exporter.getFileNameQualifier() + '_' + currentDate + ".zip";
				theFileName = StringUtils.replace(theFileName, "\"", "\\\"");//$NON-NLS-1$//$NON-NLS-2$
				String contentDisposition = "inline;filename=\""+theFileName+'"';//$NON-NLS-1$
				response.setHeader("Content-Disposition", contentDisposition);//$NON-NLS-1$
			}
//			resp.setHeader("Content-Description", "");//$NON-NLS-1$
//			resp.setHeader("Content-Transfer-Encoding", "binary");//$NON-NLS-1$//$NON-NLS-2$
			ZipOutputStream outZip = new ZipOutputStream(response.getOutputStream());
			outZip.setComment("Lotus iNotes archive for " + login + " at " + DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
			exporter.export(outZip, folders, session, login+'_'+currentDate);
			outZip.finish();
			response.flushBuffer();
		} catch (Exception e) {
			log("Error while exporting emails for "+login+": ", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while exporting emails: "+e.toString());
			return;
		} finally {
			Throttler.exportDone();
			session.logout();
		}
	}


	/** {@inheritDoc} */
	@Override
	public String getServletInfo() {
		return "ExportServlet, copyright (c) 2013 Cédrik LIME";
	}

	/** {@inheritDoc} */
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			webmailURL = new URL(getServletContext().getInitParameter("webmail.url"));//$NON-NLS-1$
		} catch (MalformedURLException e) {
			throw new UnavailableException("Missing or malformed \"webmail.url\" parameter: " + e.toString());
		}
		try {
			Throttler.MAX_CONCURRENT_EXPORTS = Integer.parseInt(getServletContext().getInitParameter("maxConcurrentExports"));//$NON-NLS-1$
		} catch (NumberFormatException e) {
			throw new UnavailableException("Missing or malformed \"maxConcurrentExports\" parameter: " + e.toString());
		}
		log("Using parameters maxConcurrentExports: " + Throttler.MAX_CONCURRENT_EXPORTS + " webmail.url: " + webmailURL);
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		webmailURL = null;
		super.destroy();
	}
}
