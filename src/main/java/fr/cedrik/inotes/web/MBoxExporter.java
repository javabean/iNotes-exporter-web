/**
 *
 */
package fr.cedrik.inotes.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.mail.MailParseException;

import fr.cedrik.inotes.BaseINotesMessage;
import fr.cedrik.inotes.Folder;
import fr.cedrik.inotes.FoldersList;
import fr.cedrik.inotes.INotesMessagesMetaData;
import fr.cedrik.inotes.Session;
import fr.cedrik.inotes.util.Charsets;
import fr.cedrik.inotes.util.IteratorChain;

/**
 * @author C&eacute;drik LIME
 */
class MBoxExporter extends BaseExporter {

	public MBoxExporter() {
	}

	@Override
	public String getFileNameQualifier() {
		return "mbox";
	}

	@Override
	public void export(ZipOutputStream outZip, FoldersList folders, Session session, String baseName) throws IOException {
		MBoxrd mboxrd = new MBoxrd();
		List<String> errors = new ArrayList<String>();
		for (Folder folder : folders) {
			if (folder.isAllMails()) {
				continue;
			}
			// messages and meeting notices meta-data
			INotesMessagesMetaData<? extends BaseINotesMessage> messages;
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
				ZipEntry entry = new ZipEntry(computeZipFolderName(folder, folders, baseName) + MBoxrd.MAILBOX_SUFFIX);
				outZip.putNextEntry(entry);
				Date lastExportedMessageDate = null;
				// write messages
				Writer outWriter = new BufferedWriter(new OutputStreamWriter(outZip, Charsets.US_ASCII), 32*1024);
				for (BaseINotesMessage message : messages.entries) {
					IteratorChain<String> mime;
					try {
						mime = session.getMessageMIME(message);
					} catch (MailParseException mpe) {
						mime = null;
					} catch (IOException ioe) {
						mime = null;
					}
					if (mime == null || ! mime.hasNext()) {
						String log = "Empty MIME message or error while retrieving! (message: "+message+" folder: "+folder+"); skipping remaining messages in this folder.";
						logger.error(log);
						errors.add(log);
						IOUtils.closeQuietly(mime);
						break;
					}
					try {
						mboxrd.writeMIME(outWriter, message, mime);
					} finally {
						IOUtils.closeQuietly(mime);
					}
					lastExportedMessageDate = message.getDate();
				}
				outWriter.flush(); // very important in order to not corrupt the ZIP stream!
				if (lastExportedMessageDate != null) {
					entry.setTime(lastExportedMessageDate.getTime());
				}
				outZip.closeEntry();
			}
		}
		appendErrorLog(outZip, errors);
	}

}
