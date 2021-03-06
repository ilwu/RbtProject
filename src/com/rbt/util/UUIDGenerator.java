package com.rbt.util;

/**
 *
 * UUIDGen adopted from the juddi project
 * (http://sourceforge.net/projects/juddi/)
 *
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Used to create new universally unique identifiers or UUID's (sometimes called
 * GUID's). UDDI UUID's are allways formmated according to DCE UUID conventions.
 *
 * @author Maarten Coene
 * @author Steve Viens
 * @version 0.3.2 3/25/2001
 * @since JDK1.2.2
 */
public class UUIDGenerator {
	static final BigInteger countStart = new BigInteger("-12219292800000"); // 15 October 1582

	static final int clock_sequence = (new Random()).nextInt(16384);

	public static String version = "1.00";

	public UUIDGenerator() {
	}

	/**
	 * Creates a new UUID. The algorithm used is described by The Open Group.
	 * See <a href="http://www.opengroup.org/onlinepubs/009629399/apdxa.htm";>
	 * Universal Unique Identifier</a> for more details.
	 * <p>
	 * Due to a lack of functionality in Java, a part of the UUID is a secure random. This results in a long processing time when this method is called for the first time.
	 */
	public static synchronized String getUUID() throws Exception {
		// implemented correctly.

		// the count of 100-nanosecond intervals since 00:00:00.00 15 October 1582
		BigInteger count;

		// the number of milliseconds since 1 January 1970
		BigInteger current = BigInteger.valueOf(System.currentTimeMillis());

		// the number of milliseconds since 15 October 1582
		BigInteger countMillis = current.subtract(countStart);

		// the result
		count = countMillis.multiply(BigInteger.valueOf(10000));

		String bitString = count.toString(2);
		if (bitString.length() < 60) {
			int nbExtraZeros = 60 - bitString.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			bitString = extraZeros.concat(bitString);
		}

		byte[] bits = bitString.getBytes();

		// the time_low field
		byte[] time_low = new byte[32];
		for (int i = 0; i < 32; i++)
			time_low[i] = bits[bits.length - i - 1];

		// the time_mid field
		byte[] time_mid = new byte[16];
		for (int i = 0; i < 16; i++)
			time_mid[i] = bits[bits.length - 32 - i - 1];

		// the time_hi_and_version field
		byte[] time_hi_and_version = new byte[16];
		for (int i = 0; i < 12; i++)
			time_hi_and_version[i] = bits[bits.length - 48 - i - 1];

		time_hi_and_version[12] = ((new String("1")).getBytes())[0];
		time_hi_and_version[13] = ((new String("0")).getBytes())[0];
		time_hi_and_version[14] = ((new String("0")).getBytes())[0];
		time_hi_and_version[15] = ((new String("0")).getBytes())[0];

		// the clock_seq_low field
		BigInteger clockSequence = BigInteger.valueOf(clock_sequence);
		String clockString = clockSequence.toString(2);
		if (clockString.length() < 14) {
			int nbExtraZeros = 14 - bitString.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			clockString = extraZeros.concat(bitString);
		}

		byte[] clock_bits = clockString.getBytes();
		byte[] clock_seq_low = new byte[8];
		for (int i = 0; i < 8; i++)
			clock_seq_low[i] = clock_bits[clock_bits.length - i - 1];

		// the clock_seq_hi_and_reserved
		byte[] clock_seq_hi_and_reserved = new byte[8];
		for (int i = 0; i < 6; i++)
			clock_seq_hi_and_reserved[i] = clock_bits[clock_bits.length - 8 - i - 1];

		clock_seq_hi_and_reserved[6] = ((new String("0")).getBytes())[0];
		clock_seq_hi_and_reserved[7] = ((new String("1")).getBytes())[0];

		String timeLow = Long.toHexString((new BigInteger(new String(reverseArray(time_low)), 2)).longValue());
		if (timeLow.length() < 8) {
			int nbExtraZeros = 8 - timeLow.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			timeLow = extraZeros.concat(timeLow);
		}

		String timeMid = Long.toHexString((new BigInteger(new String(reverseArray(time_mid)), 2)).longValue());
		if (timeMid.length() < 4) {
			int nbExtraZeros = 4 - timeMid.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");
			timeMid = extraZeros.concat(timeMid);
		}

		String timeHiAndVersion = Long.toHexString((new BigInteger(new String(reverseArray(time_hi_and_version)), 2)).longValue());
		if (timeHiAndVersion.length() < 4) {
			int nbExtraZeros = 4 - timeHiAndVersion.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			timeHiAndVersion = extraZeros.concat(timeHiAndVersion);
		}

		String clockSeqHiAndReserved = Long.toHexString((new BigInteger(new String(reverseArray(clock_seq_hi_and_reserved)), 2)).longValue());
		if (clockSeqHiAndReserved.length() < 2) {
			int nbExtraZeros = 2 - clockSeqHiAndReserved.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			clockSeqHiAndReserved = extraZeros.concat(clockSeqHiAndReserved);
		}

		String clockSeqLow = Long.toHexString((new BigInteger(new String(reverseArray(clock_seq_low)), 2)).longValue());
		if (clockSeqLow.length() < 2) {
			int nbExtraZeros = 2 - clockSeqLow.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			clockSeqLow = extraZeros.concat(clockSeqLow);
		}

		String theNode = genTheNode();

		// String result = timeLow + "-" + timeMid + "-" + timeHiAndVersion + "-" + clockSeqHiAndReserved + clockSeqLow + "-" + theNode;
		String result = timeLow + timeMid + timeHiAndVersion + clockSeqHiAndReserved + clockSeqLow + theNode;

		return result.toLowerCase();
	}

	private static byte[] reverseArray(byte[] bits) {
		byte[] result = new byte[bits.length];
		for (int i = 0; i < result.length; i++)
			result[i] = bits[result.length - 1 - i];

		return result;
	}

	static String genTheNode() throws Exception {
		String os = System.getProperty("os.name");
		try {
			if (os.startsWith("Windows")) {
				return checkMac(windowsParseMacAddress(windowsRunIpConfigCommand()));
			} else if (os.startsWith("Linux")) {
				return checkMac(linuxParseMacAddress(linuxRunIfConfigCommand()));
			} else {
				return unknownMacAddress();
			}
		} catch (Exception ex) {
			return unknownMacAddress();
		}
	}

	/*
	 * Linux stuff
	 */
	private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException {
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;
			// see if line contains IP address
			if (containsLocalHost && lastMacAddress != null) {
				return lastMacAddress;
			}

			// see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if (macAddressPosition <= 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
			if (linuxIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}
		ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
	}

	private final static boolean linuxIsMacAddress(String macAddressCandidate) {
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	private final static String linuxRunIfConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}

	/*
	 * Windows stuff
	 */
	private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException {
		//System.out.println("ipConfigResponse:" + ipConfigResponse);
		String localHost = null;
		try {
			localHost = InetAddress.getLocalHost().getHostAddress();
		} catch (java.net.UnknownHostException ex) {
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
		}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = null;
		while (tokenizer.hasMoreTokens()) {
			String line = tokenizer.nextToken().trim();
			// see if line contains IP address
			if (line.endsWith(localHost) && lastMacAddress != null) {
				return lastMacAddress;
			}
			// see if line contains MAC address
			int macAddressPosition = line.indexOf(":");
			if (macAddressPosition <= 0)
				continue;
			String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
			if (windowsIsMacAddress(macAddressCandidate)) {
				lastMacAddress = macAddressCandidate;
				continue;
			}
		}
		ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);
		//ex.printStackTrace();
		throw ex;
	}

	private final static boolean windowsIsMacAddress(String macAddressCandidate) {
		if (macAddressCandidate.length() != 17)
			return false;
		return true;
	}

	private final static String windowsRunIpConfigCommand() throws IOException {
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer = new StringBuffer();
		for (;;) {
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}

	private final static String unknownMacAddress() throws IOException {
		// problem: the node should be the IEEE 802 ethernet address, but can not
		// be retrieved in Java yet.
		// see bug ID 4173528
		// workaround (also suggested in bug ID 4173528)
		// If a system wants to generate UUIDs but has no IEE 802 compliant
		// network card or other source of IEEE 802 addresses, then this section
		// describes how to generate one.
		// The ideal solution is to obtain a 47 bit cryptographic quality random
		// number, and use it as the low 47 bits of the node ID, with the most
		// significant bit of the first octet of the node ID set to 1. This bit
		// is the unicast/multicast bit, which will never be set in IEEE 802
		// addresses obtained from network cards; hence, there can never be a
		// conflict between UUIDs generated by machines with and without network
		// cards.

		Random secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (Exception e) {
			secureRandom = new Random();
		}

		long nodeValue = secureRandom.nextLong();
		nodeValue = Math.abs(nodeValue);
		while (nodeValue > 140737488355328L) {
			nodeValue = secureRandom.nextLong();
			nodeValue = Math.abs(nodeValue);
		}

		BigInteger nodeInt = BigInteger.valueOf(nodeValue);
		String nodeString = nodeInt.toString(2);
		if (nodeString.length() < 47) {
			int nbExtraZeros = 47 - nodeString.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");

			nodeString = extraZeros.concat(nodeString);
		}

		byte[] node_bits = nodeString.getBytes();
		byte[] node = new byte[48];
		for (int i = 0; i < 47; i++)
			node[i] = node_bits[node_bits.length - i - 1];

		node[47] = ((new String("1")).getBytes())[0];
		String theNode = Long.toHexString((new BigInteger(new String(reverseArray(node)), 2)).longValue());
		if (theNode.length() < 12) {
			int nbExtraZeros = 12 - theNode.length();
			String extraZeros = new String();
			for (int i = 0; i < nbExtraZeros; i++)
				extraZeros = extraZeros.concat("0");
			theNode = extraZeros.concat(theNode);
		}

		return theNode;
	}

	static String checkMac(String mac) throws Exception {
		if (mac.length() == 12)
			return mac;
		int idx;
		while ((idx = mac.indexOf("-")) != -1) {
			mac = mac.substring(0, idx) + mac.substring(idx + 1);
		}
		if (mac.length() != 12)
			throw new Exception();
		return mac;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(UUIDGenerator.getUUID());
	}

}
