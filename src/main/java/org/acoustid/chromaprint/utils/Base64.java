// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint.utils;

public class Base64 {
    private static final char[] BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
    
    private static final byte[] BASE64_CHARS_REVERSED = new byte[256];
    
    static {
        for (int i = 0; i < 256; i++) {
            BASE64_CHARS_REVERSED[i] = 0;
        }
        BASE64_CHARS_REVERSED['-'] = 62;
        BASE64_CHARS_REVERSED['_'] = 63;
        for (int i = 0; i < 10; i++) {
            BASE64_CHARS_REVERSED['0' + i] = (byte)(52 + i);
        }
        for (int i = 0; i < 26; i++) {
            BASE64_CHARS_REVERSED['A' + i] = (byte)(i);
            BASE64_CHARS_REVERSED['a' + i] = (byte)(26 + i);
        }
    }
    
    public static int getBase64EncodedSize(int size) {
        return (size * 4 + 2) / 3;
    }
    
    public static int getBase64DecodedSize(int size) {
        return size * 3 / 4;
    }
    
    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder(getBase64EncodedSize(data.length));
        int i = 0;
        while (i + 3 <= data.length) {
            int s0 = data[i++] & 0xFF;
            int s1 = data[i++] & 0xFF;
            int s2 = data[i++] & 0xFF;
            sb.append(BASE64_CHARS[(s0 >> 2) & 63]);
            sb.append(BASE64_CHARS[((s0 << 4) | (s1 >> 4)) & 63]);
            sb.append(BASE64_CHARS[((s1 << 2) | (s2 >> 6)) & 63]);
            sb.append(BASE64_CHARS[s2 & 63]);
        }
        if (i + 2 == data.length) {
            int s0 = data[i++] & 0xFF;
            int s1 = data[i++] & 0xFF;
            sb.append(BASE64_CHARS[(s0 >> 2) & 63]);
            sb.append(BASE64_CHARS[((s0 << 4) | (s1 >> 4)) & 63]);
            sb.append(BASE64_CHARS[(s1 << 2) & 63]);
        } else if (i + 1 == data.length) {
            int s0 = data[i++] & 0xFF;
            sb.append(BASE64_CHARS[(s0 >> 2) & 63]);
            sb.append(BASE64_CHARS[(s0 << 4) & 63]);
        }
        return sb.toString();
    }
    
    public static String encode(String src) {
        return encode(src.getBytes());
    }
    
    public static byte[] decode(String encoded) {
        byte[] data = new byte[getBase64DecodedSize(encoded.length())];
        int src = 0;
        int dst = 0;
        int size = encoded.length();
        while (size >= 4) {
            byte b0 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b1 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b2 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b3 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            data[dst++] = (byte)((b0 << 2) | (b1 >> 4));
            data[dst++] = (byte)(((b1 << 4) & 255) | (b2 >> 2));
            data[dst++] = (byte)(((b2 << 6) & 255) | b3);
            size -= 4;
        }
        if (size == 3) {
            byte b0 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b1 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b2 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            data[dst++] = (byte)((b0 << 2) | (b1 >> 4));
            data[dst++] = (byte)(((b1 << 4) & 255) | (b2 >> 2));
        } else if (size == 2) {
            byte b0 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            byte b1 = BASE64_CHARS_REVERSED[encoded.charAt(src++) & 255];
            data[dst++] = (byte)((b0 << 2) | (b1 >> 4));
        }
        if (dst < data.length) {
            byte[] result = new byte[dst];
            System.arraycopy(data, 0, result, 0, dst);
            return result;
        }
        return data;
    }
    
    public static String decodeToString(String encoded) {
        return new String(decode(encoded));
    }
}
