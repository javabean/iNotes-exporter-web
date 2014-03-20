/**
 *
 */
package fr.cedrik.inotes.web;

import java.util.zip.ZipOutputStream;

import fr.cedrik.inotes.fs.BaseZipWriter;
import fr.cedrik.inotes.fs.mbox.MBoxrdZipWriter;

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
	public BaseZipWriter getZipWriter(ZipOutputStream outZip, String baseName) {
		return new MBoxrdZipWriter(outZip, baseName);
	}
}
