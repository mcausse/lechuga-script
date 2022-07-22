package org.homs.lispo.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextFileUtils {

    public static final Charset ISO88591 = StandardCharsets.ISO_8859_1;
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final Charset Cp1252 = Charset.forName("Cp1252");

    public static InputStream fileToInputStream(File f) throws FileNotFoundException {
        return new FileInputStream(f);
    }

    public static String read(File f) {
        return read(f, UTF8);
    }

    public static String read(File f, Charset charset) {
        try {
            InputStream is = fileToInputStream(f);
            return read(is, charset);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("reading file: " + f, e);
        }
    }

    public static String read(InputStream is, Charset charset) {

        BufferedReader b = null;
        try {
            StringBuilder r = new StringBuilder();

            b = new BufferedReader(new InputStreamReader(is, charset));
            String line;
            while ((line = b.readLine()) != null) {
                r.append(line);
                r.append('\n');
            }
            return r.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (b != null) {
                try {
                    b.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }

//	public static void write(File f, Charset charset, String text) {
//		Writer w = null;
//		try {
//			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), charset));
//			w.write(text);
//		} catch (IOException e) {
//			throw new RuntimeException("error writing file: " + f, e);
//		} finally {
//			if (w != null) {
//				try {
//					w.close();
//				} catch (IOException e) {
//					//
//				}
//			}
//		}
//	}
//
//	/**
//	 * https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime
//	 *
//	 * java.version is a system property that exists in every JVM. There are two
//	 * possible formats for it:
//	 *
//	 * Java 8 or lower: 1.6.0_23, 1.7.0, 1.7.0_80, 1.8.0_211
//	 *
//	 * Java 9 or higher: 9.0.1, 11.0.4, 12, 12.0.1
//	 */
//	public static int getJavaVersion() {
//		String version = System.getProperty("java.version");
//		if (version.startsWith("1.")) {
//			version = version.substring(2, 3);
//		} else {
//			int dot = version.indexOf(".");
//			if (dot != -1) {
//				version = version.substring(0, dot);
//			}
//		}
//		return Integer.parseInt(version);
//	}

}
