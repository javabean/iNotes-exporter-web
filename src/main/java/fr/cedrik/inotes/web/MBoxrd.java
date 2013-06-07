/**
 *
 */
package fr.cedrik.inotes.web;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import fr.cedrik.inotes.BaseINotesMessage;

/**
 * @author C&eacute;drik LIME
 */
class MBoxrd extends fr.cedrik.inotes.fs.mbox.MBoxrd {
	public static final String MAILBOX_SUFFIX = fr.cedrik.inotes.fs.mbox.MBoxrd.EXTENSION_MBOXRD;

	public MBoxrd() throws IOException {
		super();
	}

	@Override
	public void writeMIME(Writer mbox, BaseINotesMessage message, Iterator<String> mime) throws IOException {
		super.writeMIME(mbox, message, mime);
	}

	/**
	 * @return Outlook-compliant new-line char
	 */
	@Override
	protected String newLine() {
		return "\r\n";//$NON-NLS-1$
	}
}
