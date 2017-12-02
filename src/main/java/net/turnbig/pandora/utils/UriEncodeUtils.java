package net.turnbig.pandora.utils;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.BitSet;

/**
 *
 * @author QianBiao.NG
 * @date   2017-11-20 20:22:02
 */
public class UriEncodeUtils {
	
	private static BitSet unreservedAndReserved;
	private static final int caseDiff = 32;
	private static String dfltEncName = "UTF-8";

	public static String encode(String s) {
		return encode(s, dfltEncName);
	}

	public static String encode(String s, String enc) {
		boolean needToChange = false;
		StringBuffer out = new StringBuffer(s.length());
		CharArrayWriter charArrayWriter = new CharArrayWriter();
		if (enc == null)
			throw new RuntimeException("Unsupported encoding exception.");
		Charset charset;
		try {
			charset = Charset.forName(enc);
		} catch (IllegalCharsetNameException e) {
			throw new RuntimeException("Unsupported encoding exception.");
		}
		int i = 0;
		boolean firstHash = true;
		while (i < s.length()) {
			int c = s.charAt(i);
			if (92 == c) {
				out.append('/');
				needToChange = true;
				i++;
			} else if (37 == c) {
				int v = -1;
				if (i + 2 < s.length()) {
					try {
						v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
					} catch (NumberFormatException e) {
						v = -1;
					}
					if (v >= 0)
						out.append((char) c);
				}
				if (v < 0) {
					needToChange = true;
					out.append("%25");
				}
				i++;
			} else if (35 == c) {
				if (firstHash) {
					out.append((char) c);
					firstHash = false;
				} else {
					out.append("%23");
					needToChange = true;
				}
				i++;
			} else if (unreservedAndReserved.get(c)) {
				out.append((char) c);
				i++;
			} else {
				do {
					charArrayWriter.write(c);
					if (c >= 55296 && c <= 56319 && i + 1 < s.length()) {
						int d = s.charAt(i + 1);
						if (d >= 56320 && d <= 57343) {
							charArrayWriter.write(d);
							i++;
						}
					}
				} while (++i < s.length() && !unreservedAndReserved.get(c = s.charAt(i)));
				charArrayWriter.flush();
				String str = new String(charArrayWriter.toCharArray());
				byte ba[] = str.getBytes(charset);
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit(ba[j] >> 4 & 15, 16);
					if (Character.isLetter(ch))
						ch -= ' ';
					out.append(ch);
					ch = Character.forDigit(ba[j] & 15, 16);
					if (Character.isLetter(ch))
						ch -= ' ';
					out.append(ch);
				}

				charArrayWriter.reset();
				needToChange = true;
			}
		}
		return needToChange ? out.toString() : s;
	}

	static {
		unreservedAndReserved = new BitSet(256);
		for (int i = 97; i <= 122; i++)
			unreservedAndReserved.set(i);

		for (int i = 65; i <= 90; i++)
			unreservedAndReserved.set(i);

		for (int i = 48; i <= 57; i++)
			unreservedAndReserved.set(i);

		unreservedAndReserved.set(45);
		unreservedAndReserved.set(95);
		unreservedAndReserved.set(46);
		unreservedAndReserved.set(126);
		unreservedAndReserved.set(58);
		unreservedAndReserved.set(47);
		unreservedAndReserved.set(63);
		unreservedAndReserved.set(35);
		unreservedAndReserved.set(91);
		unreservedAndReserved.set(93);
		unreservedAndReserved.set(64);
		unreservedAndReserved.set(33);
		unreservedAndReserved.set(36);
		unreservedAndReserved.set(38);
		unreservedAndReserved.set(39);
		unreservedAndReserved.set(92);
		unreservedAndReserved.set(40);
		unreservedAndReserved.set(41);
		unreservedAndReserved.set(42);
		unreservedAndReserved.set(43);
		unreservedAndReserved.set(44);
		unreservedAndReserved.set(59);
		unreservedAndReserved.set(61);
	}

}
