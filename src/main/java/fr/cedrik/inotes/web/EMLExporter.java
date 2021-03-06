/**
 *
 */
package fr.cedrik.inotes.web;

import java.util.zip.ZipOutputStream;

import fr.cedrik.email.fs.BaseZipWriter;
import fr.cedrik.email.fs.maildir.EMLZipWriter;

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
	public BaseZipWriter getZipWriter(ZipOutputStream outZip, String baseName) {
		return new EMLZipWriter(outZip, baseName);
	}

}
