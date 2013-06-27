/**
 *
 */
package fr.cedrik.inotes.web;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cedrik.inotes.Folder;
import fr.cedrik.inotes.FoldersList;
import fr.cedrik.inotes.Session;

/**
 * @author C&eacute;drik LIME
 */
abstract class BaseExporter {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public BaseExporter() {
	}

	public abstract void export(ZipOutputStream outZip, FoldersList folders, Session session, String baseName) throws IOException;

	public abstract String getFileNameQualifier();

	protected String computeZipFolderName(Folder folder, FoldersList folders, String baseName) {
		// compute ZIP full folder name and encode each segment
		List<Folder> foldersChain = folders.getFoldersChain(folder);
		StringBuilder result = new StringBuilder(32);
		result.append(baseName).append('/');
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

}
