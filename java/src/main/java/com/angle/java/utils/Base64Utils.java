package com.angle.java.utils;

import java.io.*;

/**
 * Created by User on 2018/8/9.
 * 因为java和android中base64不能统一
 * 所以这里需要一个相应的工具类
 */
public class Base64Utils {
    public Base64Utils() {
    }

    /**
     * 功能：编码字符串
     *
     * @param data 源字符串
     * @return String
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static String encode(String data) {
        return new String(encode(data.getBytes()));
    }

    /**
     * 功能：解码字符串
     *
     * @param data 源字符串
     * @return String
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static String decode(String data) {
        return new String(decode(data.toCharArray()));
    }


    /**
     * 功能：编码byte[]
     *
     * @param data 源
     * @return char[]
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }

    /**
     * 功能：解码
     *
     * @param data 编码后的字符数组
     * @return byte[]
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static byte[] decode(char[] data) {

        int tempLen = data.length;
        for (int ix = 0; ix < data.length; ix++) {
            if ((data[ix] > 255) || codes[data[ix]] < 0) {
                --tempLen; // ignore non-valid chars and padding
            }
        }
        // calculate required length:
        // -- 3 bytes for every 4 valid base64 chars
        // -- plus 2 bytes if there are 3 extra base64 chars,
        // or plus 1 byte if there are 2 extra.

        int len = (tempLen / 4) * 3;
        if ((tempLen % 4) == 3) {
            len += 2;
        }
        if ((tempLen % 4) == 2) {
            len += 1;

        }
        byte[] out = new byte[len];

        int shift = 0; // # of excess bits stored in accum
        int accum = 0; // excess bits
        int index = 0;

        // we now go through the entire array (NOT using the 'tempLen' value)
        for (int ix = 0; ix < data.length; ix++) {
            int value = (data[ix] > 255) ? -1 : codes[data[ix]];

            if (value >= 0) { // skip over non-code
                accum <<= 6; // bits shift up by 6 each time thru
                shift += 6; // loop, with new bits being put in
                accum |= value; // at the bottom.
                if (shift >= 8) { // whenever there are 8 or more shifted in,
                    shift -= 8; // write them out (from the top, leaving any
                    out[index++] = // excess at the bottom for next iteration.
                            (byte) ((accum >> shift) & 0xff);
                }
            }
        }

        // if there is STILL something wrong we just have to throw up now!
        if (index != out.length) {
            throw new Error("Miscalculated data length (wrote " + index
                    + " instead of " + out.length + ")");
        }

        return out;
    }

    /**
     * 功能：编码文件
     *
     * @param file 源文件
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static void encode(File file) throws IOException {
        if (!file.exists()) {
            System.exit(0);
        } else {
            byte[] decoded = readBytes(file);
            char[] encoded = encode(decoded);
            writeChars(file, encoded);
        }
        file = null;
    }

    /**
     * 功能：解码文件。
     *
     * @param file 源文件
     * @throws IOException
     * @author jiangshuai
     * @date 2016年10月03日
     */
    public static void decode(File file) throws IOException {
        if (!file.exists()) {
            System.exit(0);
        } else {
            char[] encoded = readChars(file);
            byte[] decoded = decode(encoded);
            writeBytes(file, decoded);
        }
        file = null;
    }

    //
    // code characters for values 0..63
    //
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
            .toCharArray();

    //
    // lookup table for converting base64 characters to value in range 0..63
    //
    private static byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
            // LoggerUtil.debug(i + "&" + codes[i] + " ");
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            codes[i] = (byte) (i - 'A');
            // LoggerUtil.debug(i + "&" + codes[i] + " ");
        }

        for (int i = 'a'; i <= 'z'; i++) {
            codes[i] = (byte) (26 + i - 'a');
            // LoggerUtil.debug(i + "&" + codes[i] + " ");
        }
        for (int i = '0'; i <= '9'; i++) {
            codes[i] = (byte) (52 + i - '0');
            // LoggerUtil.debug(i + "&" + codes[i] + " ");
        }
        codes['+'] = 62;
        codes['/'] = 63;
    }

    private static byte[] readBytes(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = null;
        InputStream fis = null;
        InputStream is = null;
        try {
            fis = new FileInputStream(file);
            is = new BufferedInputStream(fis);
            int count = 0;
            byte[] buf = new byte[16384];
            while ((count = is.read(buf)) != -1) {
                if (count > 0) {
                    baos.write(buf, 0, count);
                }
            }
            b = baos.toByteArray();

        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return b;
    }

    private static char[] readChars(File file) throws IOException {
        CharArrayWriter caw = new CharArrayWriter();
        Reader fr = null;
        Reader in = null;
        try {
            fr = new FileReader(file);
            in = new BufferedReader(fr);
            int count = 0;
            char[] buf = new char[16384];
            while ((count = in.read(buf)) != -1) {
                if (count > 0) {
                    caw.write(buf, 0, count);
                }
            }

        } finally {
            try {
                if (caw != null)
                    caw.close();
                if (in != null)
                    in.close();
                if (fr != null)
                    fr.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return caw.toCharArray();
    }

    private static void writeBytes(File file, byte[] data) throws IOException {
        OutputStream fos = null;
        OutputStream os = null;
        try {
            fos = new FileOutputStream(file);
            os = new BufferedOutputStream(fos);
            os.write(data);

        } finally {
            try {
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void writeChars(File file, char[] data) throws IOException {
        Writer fos = null;
        Writer os = null;
        try {
            fos = new FileWriter(file);
            os = new BufferedWriter(fos);
            os.write(data);

        } finally {
            try {
                if (os != null)
                    os.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        String str = "5YWl5Y+j77ya6YeH6LSt566h55CGLT7kuJrliqHnrqHnkIYtPumHh+i0reiuouWNlQ0KPGltZyBz\n" +
                "cmMgPSIke3BpY3R1cmVEb2NJcEFkcmVzc30vc2VydmljZS9DR0dMLVlXR0wtQ0dERC0yLTEucG5n\n" +
                "Ij4NCuaTjeS9nOatpemqpO+8mg0KMeOAgQnlnKjjgJDph4fotK3orqLljZXjgJHpobXpnaItPuWP\n" +
                "s+S4iuinkuOAkOaWsOWinuOAke+8mw0KPGltZyBzcmMgPSIke3BpY3R1cmVEb2NJcEFkcmVzc30v\n" +
                "c2VydmljZS9DR0dMLVlXR0wtQ0dERC0yLTIucG5nIj4NCjLjgIEJ6YCJ5oup77mk5L6b5bqU5ZWG\n" +
                "77ml77ybDQoz44CBCea3u+WKoO+5pOWVhuWTgeWQjeensO+5peOAgeiuoui0p++5pOaVsOmHj++5\n" +
                "peOAge+5pOaIkOS6pOWNleS7t++5pe+8m+ezu+e7n+S8muiHquW4pu+5pOWNleaNrue8luWPt++5\n" +
                "peOAge+5pOiuouWNleaXpeacn++5peOAge+5pOmHh+i0reS6uuWRmO+5peOAge+5pOmHh+i0remD\n" +
                "qOmXqO+5pe+8jOS5n+S8muagueaNruiuoui0p+aVsOmHj+WSjOaIkOS6pOWNleS7t+iuoeeul+WH\n" +
                "uu+5pOmHkemineWwj+iuoe+5peetieaVsOaNru+8mw0KNOOAgQnnvJbovpHlrozmiJDngrnlh7vj\n" +
                "gJDkv53lrZjjgJHljbPlj6/jgIINCjxpbWcgc3JjID0iJHtwaWN0dXJlRG9jSXBBZHJlc3N9L3Nl\n" +
                "cnZpY2UvQ0dHTC1ZV0dMLUNHREQtMi0zLnBuZyI+";
        System.out.println(decode(str));
    }
}