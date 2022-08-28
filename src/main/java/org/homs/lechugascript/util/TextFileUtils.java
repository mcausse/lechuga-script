package org.homs.lechugascript.util;

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
}
