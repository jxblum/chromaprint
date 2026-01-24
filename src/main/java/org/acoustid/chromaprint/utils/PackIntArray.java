// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint.utils;

import java.util.ArrayList;
import java.util.List;

public class PackIntArray {
    public static int getPackedInt3ArraySize(int size) {
        return (size * 3 + 7) / 8;
    }

    public static int getPackedInt5ArraySize(int size) {
        return (size * 5 + 7) / 8;
    }

    public static int getUnpackedInt3ArraySize(int size) {
        return size * 8 / 3;
    }

    public static int getUnpackedInt5ArraySize(int size) {
        return size * 8 / 5;
    }

    public static byte[] packInt3Array(List<Byte> input) {
        byte[] output = new byte[getPackedInt3ArraySize(input.size())];
        int src = 0;
        int dst = 0;
        int size = input.size();

        while (size >= 8) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            byte s6 = input.get(src++);
            byte s7 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2) | ((s3 & 0x07) << 1) | ((s4 & 0x07) << 4) | ((s5 & 0x01) << 7));
            output[dst++] = (byte)(((s5 & 0x06) >> 1) | ((s6 & 0x07) << 2) | ((s7 & 0x07) << 5));
            size -= 8;
        }

        if (size == 7) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            byte s6 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2) | ((s3 & 0x07) << 1) | ((s4 & 0x07) << 4) | ((s5 & 0x01) << 7));
            output[dst++] = (byte)(((s5 & 0x06) >> 1) | ((s6 & 0x07) << 2));
        } else if (size == 6) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2) | ((s3 & 0x07) << 1) | ((s4 & 0x07) << 4) | ((s5 & 0x01) << 7));
            output[dst++] = (byte)(((s5 & 0x06) >> 1));
        } else if (size == 5) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2) | ((s3 & 0x07) << 1) | ((s4 & 0x07) << 4));
        } else if (size == 4) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2) | ((s3 & 0x07) << 1));
        } else if (size == 3) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3) | ((s2 & 0x03) << 6));
            output[dst++] = (byte)(((s2 & 0x04) >> 2));
        } else if (size == 2) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x07) | ((s1 & 0x07) << 3));
        } else if (size == 1) {
            byte s0 = input.get(src++);
            output[dst++] = (byte)(s0 & 0x07);
        }

        if (dst < output.length) {
            byte[] result = new byte[dst];
            System.arraycopy(output, 0, result, 0, dst);
            return result;
        }
        return output;
    }

    public static byte[] unpackInt3Array(byte[] input) {
        List<Byte> output = new ArrayList<>();
        int src = 0;
        int size = input.length;

        while (size >= 3) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            byte s2 = input[src++];
            output.add((byte)(s0 & 0x07));
            output.add((byte)((s0 & 0x38) >> 3));
            output.add((byte)(((s0 & 0xc0) >> 6) | ((s1 & 0x01) << 2)));
            output.add((byte)((s1 & 0x0e) >> 1));
            output.add((byte)((s1 & 0x70) >> 4));
            output.add((byte)((s1 & 0x80) >> 7) | ((s2 & 0x03) << 1)));
            output.add((byte)((s2 & 0x1c) >> 2));
            output.add((byte)((s2 & 0xe0) >> 5));
            size -= 3;
        }

        if (size == 2) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            output.add((byte)(s0 & 0x07));
            output.add((byte)((s0 & 0x38) >> 3));
            output.add((byte)(((s0 & 0xc0) >> 6) | ((s1 & 0x01) << 2)));
            output.add((byte)((s1 & 0x0e) >> 1));
            output.add((byte)((s1 & 0x70) >> 4));
        } else if (size == 1) {
            byte s0 = input[src++];
            output.add((byte)(s0 & 0x07));
            output.add((byte)((s0 & 0x38) >> 3));
        }

        byte[] result = new byte[output.size()];
        for (int i = 0; i < output.size(); i++) {
            result[i] = output.get(i);
        }
        return result;
    }

    public static byte[] packInt5Array(List<Byte> input) {
        byte[] output = new byte[getPackedInt5ArraySize(input.size())];
        int src = 0;
        int dst = 0;
        int size = input.size();

        while (size >= 8) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            byte s6 = input.get(src++);
            byte s7 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2) | ((s3 & 0x01) << 7));
            output[dst++] = (byte)(((s3 & 0x1e) >> 1) | ((s4 & 0x0f) << 4));
            output[dst++] = (byte)(((s4 & 0x10) >> 4) | ((s5 & 0x1f) << 1) | ((s6 & 0x03) << 6));
            output[dst++] = (byte)(((s6 & 0x1c) >> 2) | ((s7 & 0x1f) << 3));
            size -= 8;
        }

        if (size == 7) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            byte s6 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2) | ((s3 & 0x01) << 7));
            output[dst++] = (byte)(((s3 & 0x1e) >> 1) | ((s4 & 0x0f) << 4));
            output[dst++] = (byte)(((s4 & 0x10) >> 4) | ((s5 & 0x1f) << 1) | ((s6 & 0x03) << 6));
            output[dst++] = (byte)((s6 & 0x1c) >> 2);
        } else if (size == 6) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            byte s5 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2) | ((s3 & 0x01) << 7));
            output[dst++] = (byte)(((s3 & 0x1e) >> 1) | ((s4 & 0x0f) << 4));
            output[dst++] = (byte)(((s4 & 0x10) >> 4) | ((s5 & 0x1f) << 1));
        } else if (size == 5) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            byte s4 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2) | ((s3 & 0x01) << 7));
            output[dst++] = (byte)(((s3 & 0x1e) >> 1) | ((s4 & 0x0f) << 4));
            output[dst++] = (byte)((s4 & 0x10) >> 4);
        } else if (size == 4) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            byte s3 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2) | ((s3 & 0x01) << 7));
            output[dst++] = (byte)((s3 & 0x1e) >> 1);
        } else if (size == 3) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            byte s2 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)(((s1 & 0x18) >> 3) | ((s2 & 0x1f) << 2));
        } else if (size == 2) {
            byte s0 = input.get(src++);
            byte s1 = input.get(src++);
            output[dst++] = (byte)((s0 & 0x1f) | ((s1 & 0x07) << 5));
            output[dst++] = (byte)((s1 & 0x18) >> 3);
        } else if (size == 1) {
            byte s0 = input.get(src++);
            output[dst++] = (byte)(s0 & 0x1f);
        }

        if (dst < output.length) {
            byte[] result = new byte[dst];
            System.arraycopy(output, 0, result, 0, dst);
            return result;
        }
        return output;
    }

    public static byte[] unpackInt5Array(byte[] input) {
        List<Byte> output = new ArrayList<>();
        int src = 0;
        int size = input.length;

        while (size >= 5) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            byte s2 = input[src++];
            byte s3 = input[src++];
            byte s4 = input[src++];
            output.add((byte)(s0 & 0x1f));
            output.add((byte)(((s0 & 0xe0) >> 5) | ((s1 & 0x03) << 3)));
            output.add((byte)((s1 & 0x7c) >> 2));
            output.add((byte)(((s1 & 0x80) >> 7) | ((s2 & 0x0f) << 1)));
            output.add((byte)(((s2 & 0xf0) >> 4) | ((s3 & 0x01) << 4)));
            output.add((byte)((s3 & 0x3e) >> 1));
            output.add((byte)(((s3 & 0xc0) >> 6) | ((s4 & 0x07) << 2)));
            output.add((byte)((s4 & 0xf8) >> 3));
            size -= 5;
        }

        if (size == 4) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            byte s2 = input[src++];
            byte s3 = input[src++];
            output.add((byte)(s0 & 0x1f));
            output.add((byte)(((s0 & 0xe0) >> 5) | ((s1 & 0x03) << 3)));
            output.add((byte)((s1 & 0x7c) >> 2));
            output.add((byte)(((s1 & 0x80) >> 7) | ((s2 & 0x0f) << 1)));
            output.add((byte)(((s2 & 0xf0) >> 4) | ((s3 & 0x01) << 4)));
            output.add((byte)((s3 & 0x3e) >> 1));
        } else if (size == 3) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            byte s2 = input[src++];
            output.add((byte)(s0 & 0x1f));
            output.add((byte)(((s0 & 0xe0) >> 5) | ((s1 & 0x03) << 3)));
            output.add((byte)((s1 & 0x7c) >> 2));
            output.add((byte)(((s1 & 0x80) >> 7) | ((s2 & 0x0f) << 1)));
        } else if (size == 2) {
            byte s0 = input[src++];
            byte s1 = input[src++];
            output.add((byte)(s0 & 0x1f));
            output.add((byte)(((s0 & 0xe0) >> 5) | ((s1 & 0x03) << 3)));
            output.add((byte)((s1 & 0x7c) >> 2));
        } else if (size == 1) {
            byte s0 = input[src++];
            output.add((byte)(s0 & 0x1f));
        }

        byte[] result = new byte[output.size()];
        for (int i = 0; i < output.size(); i++) {
            result[i] = output.get(i);
        }
        return result;
    }
}
