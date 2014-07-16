/**
 *
 */
package fr.cedrik.inotes.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailParseException;

import fr.cedrik.email.FoldersList;
import fr.cedrik.email.MessagesMetaData;
import fr.cedrik.email.fs.BaseZipWriter;
import fr.cedrik.email.spi.Message;
import fr.cedrik.inotes.Folder;
import fr.cedrik.inotes.Session;
import fr.cedrik.util.Charsets;
import fr.cedrik.util.IteratorChain;

/**
 * @author C&eacute;drik LIME
 */
abstract class BaseExporter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public BaseExporter() {
	}

	public abstract String getFileNameQualifier();

	public abstract BaseZipWriter getZipWriter(ZipOutputStream outZip, String baseName);

	public void export(ZipOutputStream outZip, FoldersList folders, Session session, String baseName) throws IOException {
		BaseZipWriter zipWriter = getZipWriter(outZip, baseName);
		List<String> errors = new ArrayList<String>();
		for (Folder folder : folders) {
			if (folder.isAllMails()) {
				continue;
			}
			// messages and meeting notices meta-data
			MessagesMetaData<? extends Message> messages;
			try {
				session.setCurrentFolder(folder);
				messages = session.getMessagesAndMeetingNoticesMetaData();
			} catch (IOException ioe) {
				String log = "Can not change to, or retrieve the list of messages of, folder: "+folder+"; skipping all messages in this folder.";
				logger.error(log);
				errors.add(log);
				continue;
			}
			if (! messages.entries.isEmpty()) {
				String folderChainName = computeZipFolderName(folder, folders);
				zipWriter.openFolder(folderChainName);
				for (Message message : messages.entries) {
					IteratorChain<String> mime;
					try {
						mime = session.getMessageMIME(message);
					} catch (MailParseException mpe) {
						mime = null;
					} catch (IOException ioe) {
						mime = null;
					}
					if (mime == null || ! mime.hasNext()) {
						String log = "Empty MIME message or error while retrieving! (message: "+message+" folder: "+folder+"); skipping this message.";
						logger.error(log);
						errors.add(log);
						IOUtils.closeQuietly(mime);
						continue;
					}
					try {
						zipWriter.openFile(folderChainName, message);
						zipWriter.write(message, mime);
					} finally {
						IOUtils.closeQuietly(mime);
						zipWriter.closeFile(message);
					}
				}
				zipWriter.closeFolder();
			}
		}
		appendErrorLog(outZip, errors);
	}

	protected String computeZipFolderName(Folder folder, FoldersList folders) {
		// compute ZIP full folder name and encode each segment
		List<Folder> foldersChain = folders.getFoldersChain(folder);
		StringBuilder result = new StringBuilder(32);
		for (Folder parent : foldersChain) {
			result.append(encodeFolderName(parent.name));
			result.append('/');
		}
		result.deleteCharAt(result.length() - 1); // remove trailing '/'
		return result.toString();
	}

	/**
	 * @see RFC2060 5.1.3.  Mailbox International Naming Convention  + special treatment for '/'
	 */
	protected String encodeFolderName(String folderName) {
//		return BASE64MailboxEncoder.encode(folderName).replace("/", "&AC8-");
		return StringUtils.replaceChars(folderName, "\\/:*?\"<>|", "_________");
	}

	protected void appendErrorLog(ZipOutputStream outZip, List<String> errors) throws IOException {
		if (errors.isEmpty()) {
			return;
		}
		ZipEntry entry = new ZipEntry("errors.txt");//$NON-NLS-1$
		outZip.putNextEntry(entry);
		Writer outWriter = new BufferedWriter(new OutputStreamWriter(outZip, Charsets.UTF_8), 4*1024);
		for (String log : errors) {
			outWriter.append(log).append("\r\n");//$NON-NLS-1$
		}
		outWriter.flush();
		outZip.closeEntry();
	}
}
