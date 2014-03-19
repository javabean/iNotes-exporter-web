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
import org.springframework.mail.MailParseException;

import fr.cedrik.inotes.BaseINotesMessage;
import fr.cedrik.inotes.Folder;
import fr.cedrik.inotes.FoldersList;
import fr.cedrik.inotes.INotesMessagesMetaData;
import fr.cedrik.inotes.Session;
import fr.cedrik.inotes.util.Charsets;
import fr.cedrik.inotes.util.IteratorChain;

import gnu.trove.impl.Constants;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * @author C&eacute;drik LIME
 */
class EMLExporter extends BaseExporter {

	public EMLExporter() {
	}

	@Override
	public String getFileNameQualifier() {
		return "eml";
	}

	@Override
	public void export(ZipOutputStream outZip, FoldersList folders, Session session, String baseName) throws IOException {
		EML eml = new EML();
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
				TLongSet exportedMails = new TLongHashSet((int) (messages.entries.size() / Constants.DEFAULT_LOAD_FACTOR) + 1); // for the current folder
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
						String log = "Empty MIME message or error while retrieving! (message: "+message+" folder: "+folder+"); skipping this message.";
						logger.error(log);
						errors.add(log);
						IOUtils.closeQuietly(mime);
						continue;
					}
					ZipEntry entry = new ZipEntry(computeZipMailFileName(message, folder, folders, exportedMails, baseName));
					if (message.getDate() != null) {
						entry.setTime(message.getDate().getTime());
					}
					outZip.putNextEntry(entry);
					// write message
					Writer outWriter = new BufferedWriter(new OutputStreamWriter(outZip, Charsets.US_ASCII), 32*1024);
					try {
						eml.writeMIME(outWriter, message, mime);
					} finally {
						IOUtils.closeQuietly(mime);
					}
					outWriter.flush(); // very important in order to not corrupt the ZIP stream!
					outZip.closeEntry();
				}
			}
		}
		appendErrorLog(outZip, errors);
	}

	protected String computeZipMailFileName(BaseINotesMessage message, Folder folder, FoldersList folders, TLongSet exportedMails, String baseName) {
		String zipFolder = super.computeZipFolderName(folder, folders, baseName);
		long id = message.getDate().getTime();
		while (exportedMails.contains(id)) {
			++id;
		}
		String zipFQN = zipFolder + '/' + id + EML.MAILBOX_SUFFIX;
		exportedMails.add(id);
		return zipFQN;
	}

}
