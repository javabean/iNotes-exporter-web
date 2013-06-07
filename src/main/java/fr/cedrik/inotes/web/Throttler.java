/**
 *
 */
package fr.cedrik.inotes.web;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author C&eacute;drik LIME
 */
public final class Throttler {
	static int MAX_CONCURRENT_EXPORTS = 1;
	private static final AtomicInteger EXPORTS = new AtomicInteger(0);

	private Throttler() {
		assert false;
	}

	public static boolean shouldThrottle() {
		return EXPORTS.get() > MAX_CONCURRENT_EXPORTS;
	}

	public static int currentSessions() {
		return EXPORTS.get();
	}

	public static void newExport() {
		EXPORTS.incrementAndGet();
	}

	public static void exportDone() {
		EXPORTS.decrementAndGet();
	}
}
