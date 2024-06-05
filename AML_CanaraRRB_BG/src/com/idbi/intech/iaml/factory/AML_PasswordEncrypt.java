package com.idbi.intech.iaml.factory;

import java.security.InvalidKeyException;

public final class AML_PasswordEncrypt
{

    public AML_PasswordEncrypt()
    {
    }

    static void trace(boolean flag, String s)
    {
    }

    static void trace(String s)
    {
    }

    private static final int LFSR1(int i)
    {
        return i >> 1 ^ ((i & 1) == 0 ? 0 : 0xb4);
    }

    private static final int LFSR2(int i)
    {
        return i >> 2 ^ ((i & 2) == 0 ? 0 : 0xb4) ^ ((i & 1) == 0 ? 0 : 0x5a);
    }

    private static final int Mx_1(int i)
    {
        return i;
    }

    private static final int Mx_X(int i)
    {
        return i ^ LFSR2(i);
    }

    private static final int Mx_Y(int i)
    {
        return i ^ LFSR1(i) ^ LFSR2(i);
    }

    public static synchronized Object makeKey(byte abyte0[])
        throws InvalidKeyException
    {
        if(abyte0 == null)
            throw new InvalidKeyException("Empty key");
        int i = abyte0.length;
        if(i != 8 && i != 16 && i != 24 && i != 32)
            throw new InvalidKeyException("Incorrect key length");
        int j = i / 8;
        byte byte0 = 40;
        int ai[] = new int[4];
        int ai1[] = new int[4];
        int ai2[] = new int[4];
        int k = 0;
        int l = 0;
        for(int i1 = j - 1; l < 4 && k < i; i1--)
        {
            ai[l] = abyte0[k++] & 0xff | (abyte0[k++] & 0xff) << 8 | (abyte0[k++] & 0xff) << 16 | (abyte0[k++] & 0xff) << 24;
            ai1[l] = abyte0[k++] & 0xff | (abyte0[k++] & 0xff) << 8 | (abyte0[k++] & 0xff) << 16 | (abyte0[k++] & 0xff) << 24;
            ai2[i1] = RS_MDS_Encode(ai[l], ai1[l]);
            l++;
        }

        int ai3[] = new int[byte0];
        int j1;
        for(int k1 = j1 = 0; k1 < byte0 / 2;)
        {
            int i2 = F32(j, j1, ai);
            int k2 = F32(j, j1 + 0x1010101, ai1);
            k2 = k2 << 8 | k2 >>> 24;
            i2 += k2;
            ai3[2 * k1] = i2;
            i2 += k2;
            ai3[2 * k1 + 1] = i2 << 9 | i2 >>> 23;
            k1++;
            j1 += 0x2020202;
        }

        int l1 = ai2[0];
        int j2 = ai2[1];
        int l2 = ai2[2];
        int i3 = ai2[3];
        int ai4[] = new int[1024];
        for(int j3 = 0; j3 < 256; j3++)
        {
            int k3;
            int l3;
            int i4;
            int j4 = k3 = l3 = i4 = j3;
            switch(j & 3)
            {
            case 1: // '\001'
                ai4[2 * j3] = MDS[0][P[0][j4] & 0xff ^ b0(l1)];
                ai4[2 * j3 + 1] = MDS[1][P[0][k3] & 0xff ^ b1(l1)];
                ai4[512 + 2 * j3] = MDS[2][P[1][l3] & 0xff ^ b2(l1)];
                ai4[512 + 2 * j3 + 1] = MDS[3][P[1][i4] & 0xff ^ b3(l1)];
                break;

            case 0: // '\0'
                j4 = P[1][j4] & 0xff ^ b0(i3);
                k3 = P[0][k3] & 0xff ^ b1(i3);
                l3 = P[0][l3] & 0xff ^ b2(i3);
                i4 = P[1][i4] & 0xff ^ b3(i3);
                // fall through

            case 3: // '\003'
                j4 = P[1][j4] & 0xff ^ b0(l2);
                k3 = P[1][k3] & 0xff ^ b1(l2);
                l3 = P[0][l3] & 0xff ^ b2(l2);
                i4 = P[0][i4] & 0xff ^ b3(l2);
                // fall through

            case 2: // '\002'
                ai4[2 * j3] = MDS[0][P[0][P[0][j4] & 0xff ^ b0(j2)] & 0xff ^ b0(l1)];
                ai4[2 * j3 + 1] = MDS[1][P[0][P[1][k3] & 0xff ^ b1(j2)] & 0xff ^ b1(l1)];
                ai4[512 + 2 * j3] = MDS[2][P[1][P[0][l3] & 0xff ^ b2(j2)] & 0xff ^ b2(l1)];
                ai4[512 + 2 * j3 + 1] = MDS[3][P[1][P[1][i4] & 0xff ^ b3(j2)] & 0xff ^ b3(l1)];
                break;
            }
        }

        Object aobj[] = {
            ai4, ai3
        };
        return ((Object) (aobj));
    }

    public static byte[] blockEncrypt(byte abyte0[], int i, Object obj)
    {
        Object aobj[] = (Object[])obj;
        int ai[] = (int[])aobj[0];
        int ai1[] = (int[])aobj[1];
        int j = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int k = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int l = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int i1 = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        j ^= ai1[0];
        k ^= ai1[1];
        l ^= ai1[2];
        i1 ^= ai1[3];
        int j1 = 8;
        for(int k1 = 0; k1 < 16; k1 += 2)
        {
            int l1 = Fe32(ai, j, 0);
            int i2 = Fe32(ai, k, 3);
            l ^= l1 + i2 + ai1[j1++];
            l = l >>> 1 | l << 31;
            i1 = i1 << 1 | i1 >>> 31;
            i1 ^= l1 + 2 * i2 + ai1[j1++];
            l1 = Fe32(ai, l, 0);
            i2 = Fe32(ai, i1, 3);
            j ^= l1 + i2 + ai1[j1++];
            j = j >>> 1 | j << 31;
            k = k << 1 | k >>> 31;
            k ^= l1 + 2 * i2 + ai1[j1++];
        }

        l ^= ai1[4];
        i1 ^= ai1[5];
        j ^= ai1[6];
        k ^= ai1[7];
        byte abyte1[] = {
            (byte)l, (byte)(l >>> 8), (byte)(l >>> 16), (byte)(l >>> 24), (byte)i1, (byte)(i1 >>> 8), (byte)(i1 >>> 16), (byte)(i1 >>> 24), (byte)j, (byte)(j >>> 8),
            (byte)(j >>> 16), (byte)(j >>> 24), (byte)k, (byte)(k >>> 8), (byte)(k >>> 16), (byte)(k >>> 24)
        };
        return abyte1;
    }

    public static byte[] blockDecrypt(byte abyte0[], int i, Object obj)
    {
        Object aobj[] = (Object[])obj;
        int ai[] = (int[])aobj[0];
        int ai1[] = (int[])aobj[1];
        int j = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int k = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int l = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        int i1 = abyte0[i++] & 0xff | (abyte0[i++] & 0xff) << 8 | (abyte0[i++] & 0xff) << 16 | (abyte0[i++] & 0xff) << 24;
        j ^= ai1[4];
        k ^= ai1[5];
        l ^= ai1[6];
        i1 ^= ai1[7];
        int j1 = 39;
        for(int k1 = 0; k1 < 16; k1 += 2)
        {
            int l1 = Fe32(ai, j, 0);
            int i2 = Fe32(ai, k, 3);
            i1 ^= l1 + 2 * i2 + ai1[j1--];
            i1 = i1 >>> 1 | i1 << 31;
            l = l << 1 | l >>> 31;
            l ^= l1 + i2 + ai1[j1--];
            l1 = Fe32(ai, l, 0);
            i2 = Fe32(ai, i1, 3);
            k ^= l1 + 2 * i2 + ai1[j1--];
            k = k >>> 1 | k << 31;
            j = j << 1 | j >>> 31;
            j ^= l1 + i2 + ai1[j1--];
        }

        l ^= ai1[0];
        i1 ^= ai1[1];
        j ^= ai1[2];
        k ^= ai1[3];
        byte abyte1[] = {
            (byte)l, (byte)(l >>> 8), (byte)(l >>> 16), (byte)(l >>> 24), (byte)i1, (byte)(i1 >>> 8), (byte)(i1 >>> 16), (byte)(i1 >>> 24), (byte)j, (byte)(j >>> 8),
            (byte)(j >>> 16), (byte)(j >>> 24), (byte)k, (byte)(k >>> 8), (byte)(k >>> 16), (byte)(k >>> 24)
        };
        return abyte1;
    }

    public static boolean self_test()
    {
        return self_test(16);
    }

    private static final int b0(int i)
    {
        return i & 0xff;
    }

    private static final int b1(int i)
    {
        return i >>> 8 & 0xff;
    }

    private static final int b2(int i)
    {
        return i >>> 16 & 0xff;
    }

    private static final int b3(int i)
    {
        return i >>> 24 & 0xff;
    }

    private static final int RS_MDS_Encode(int i, int j)
    {
        int k = j;
        for(int l = 0; l < 4; l++)
            k = RS_rem(k);

        k ^= i;
        for(int i1 = 0; i1 < 4; i1++)
            k = RS_rem(k);

        return k;
    }

    private static final int RS_rem(int i)
    {
        int j = i >>> 24 & 0xff;
        int k = (j << 1 ^ ((j & 0x80) == 0 ? 0 : 0x14d)) & 0xff;
        int l = j >>> 1 ^ ((j & 1) == 0 ? 0 : 0xa6) ^ k;
        int i1 = i << 8 ^ l << 24 ^ k << 16 ^ l << 8 ^ j;
        return i1;
    }

    private static final int F32(int i, int j, int ai[])
    {
        int k = b0(j);
        int l = b1(j);
        int i1 = b2(j);
        int j1 = b3(j);
        int k1 = ai[0];
        int l1 = ai[1];
        int i2 = ai[2];
        int j2 = ai[3];
        int k2 = 0;
        switch(i & 3)
        {
        case 1: // '\001'
            k2 = MDS[0][P[0][k] & 0xff ^ b0(k1)] ^ MDS[1][P[0][l] & 0xff ^ b1(k1)] ^ MDS[2][P[1][i1] & 0xff ^ b2(k1)] ^ MDS[3][P[1][j1] & 0xff ^ b3(k1)];
            break;

        case 0: // '\0'
            k = P[1][k] & 0xff ^ b0(j2);
            l = P[0][l] & 0xff ^ b1(j2);
            i1 = P[0][i1] & 0xff ^ b2(j2);
            j1 = P[1][j1] & 0xff ^ b3(j2);
            // fall through

        case 3: // '\003'
            k = P[1][k] & 0xff ^ b0(i2);
            l = P[1][l] & 0xff ^ b1(i2);
            i1 = P[0][i1] & 0xff ^ b2(i2);
            j1 = P[0][j1] & 0xff ^ b3(i2);
            // fall through

        case 2: // '\002'
            k2 = MDS[0][P[0][P[0][k] & 0xff ^ b0(l1)] & 0xff ^ b0(k1)] ^ MDS[1][P[0][P[1][l] & 0xff ^ b1(l1)] & 0xff ^ b1(k1)] ^ MDS[2][P[1][P[0][i1] & 0xff ^ b2(l1)] & 0xff ^ b2(k1)] ^ MDS[3][P[1][P[1][j1] & 0xff ^ b3(l1)] & 0xff ^ b3(k1)];
            break;
        }
        return k2;
    }

    private static final int Fe32(int ai[], int i, int j)
    {
        return ai[2 * _b(i, j)] ^ ai[2 * _b(i, j + 1) + 1] ^ ai[512 + 2 * _b(i, j + 2)] ^ ai[512 + 2 * _b(i, j + 3) + 1];
    }

    private static final int _b(int i, int j)
    {
        int k = 0;
        switch(j % 4)
        {
        case 0: // '\0'
            k = b0(i);
            break;

        case 1: // '\001'
            k = b1(i);
            break;

        case 2: // '\002'
            k = b2(i);
            break;

        case 3: // '\003'
            k = b3(i);
            break;
        }
        return k;
    }

    public static int blockSize()
    {
        return 16;
    }

    private static boolean self_test(int i)
    {
        boolean flag = false;
        try
        {
            byte abyte0[] = new byte[i];
            byte abyte1[] = new byte[16];
            for(int j = 0; j < i; j++)
                abyte0[j] = (byte)j;

            for(int k = 0; k < 16; k++)
                abyte1[k] = (byte)k;

            Object obj = makeKey(abyte0);
            byte abyte2[] = blockEncrypt(abyte1, 0, obj);
            byte abyte3[] = blockDecrypt(abyte2, 0, obj);
            flag = areEqual(abyte1, abyte3);
            if(!flag)
                throw new RuntimeException("Symmetric operation failed");
        }
        catch(Exception exception) { }
        return flag;
    }

    private static boolean areEqual(byte abyte0[], byte abyte1[])
    {
        int i = abyte0.length;
        if(i != abyte1.length)
            return false;
        for(int j = 0; j < i; j++)
            if(abyte0[j] != abyte1[j])
                return false;

        return true;
    }

    private static String intToString(int i)
    {
        char ac[] = new char[8];
        for(int j = 7; j >= 0; j--)
        {
            ac[j] = HEX_DIGITS[i & 0xf];
            i >>>= 4;
        }

        return new String(ac);
    }

    private static String toString(byte abyte0[])
    {
        return toString(abyte0, 0, abyte0.length);
    }

    private static String toString(byte abyte0[], int i, int j)
    {
        char ac[] = new char[j * 2];
        int k = i;
        int l = 0;
        while(k < i + j)
        {
            byte byte0 = abyte0[k++];
            ac[l++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            ac[l++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(ac);
    }

    public static void main(String args[])
    {
        self_test(16);
        self_test(24);
        self_test(32);
    }

    private static final byte P[][] = {
        {
            -87, 103, -77, -24, 4, -3, -93, 118, -102, -110,
            -128, 120, -28, -35, -47, 56, 13, -58, 53, -104,
            24, -9, -20, 108, 67, 117, 55, 38, -6, 19,
            -108, 72, -14, -48, -117, 48, -124, 84, -33, 35,
            25, 91, 61, 89, -13, -82, -94, -126, 99, 1,
            -125, 46, -39, 81, -101, 124, -90, -21, -91, -66,
            22, 12, -29, 97, -64, -116, 58, -11, 115, 44,
            37, 11, -69, 78, -119, 107, 83, 106, -76, -15,
            -31, -26, -67, 69, -30, -12, -74, 102, -52, -107,
            3, 86, -44, 28, 30, -41, -5, -61, -114, -75,
            -23, -49, -65, -70, -22, 119, 57, -81, 51, -55,
            98, 113, -127, 121, 9, -83, 36, -51, -7, -40,
            -27, -59, -71, 77, 68, 8, -122, -25, -95, 29,
            -86, -19, 6, 112, -78, -46, 65, 123, -96, 17,
            49, -62, 39, -112, 32, -10, 96, -1, -106, 92,
            -79, -85, -98, -100, 82, 27, 95, -109, 10, -17,
            -111, -123, 73, -18, 45, 79, -113, 59, 71, -121,
            109, 70, -42, 62, 105, 100, 42, -50, -53, 47,
            -4, -105, 5, 122, -84, 127, -43, 26, 75, 14,
            -89, 90, 40, 20, 63, 41, -120, 60, 76, 2,
            -72, -38, -80, 23, 85, 31, -118, 125, 87, -57,
            -115, 116, -73, -60, -97, 114, 126, 21, 34, 18,
            88, 7, -103, 52, 110, 80, -34, 104, 101, -68,
            -37, -8, -56, -88, 43, 64, -36, -2, 50, -92,
            -54, 16, 33, -16, -45, 93, 15, 0, 111, -99,
            54, 66, 74, 94, -63, -32
        }, {
            117, -13, -58, -12, -37, 123, -5, -56, 74, -45,
            -26, 107, 69, 125, -24, 75, -42, 50, -40, -3,
            55, 113, -15, -31, 48, 15, -8, 27, -121, -6,
            6, 63, 94, -70, -82, 91, -118, 0, -68, -99,
            109, -63, -79, 14, -128, 93, -46, -43, -96, -124,
            7, 20, -75, -112, 44, -93, -78, 115, 76, 84,
            -110, 116, 54, 81, 56, -80, -67, 90, -4, 96,
            98, -106, 108, 66, -9, 16, 124, 40, 39, -116,
            19, -107, -100, -57, 36, 70, 59, 112, -54, -29,
            -123, -53, 17, -48, -109, -72, -90, -125, 32, -1,
            -97, 119, -61, -52, 3, 111, 8, -65, 64, -25,
            43, -30, 121, 12, -86, -126, 65, 58, -22, -71,
            -28, -102, -92, -105, 126, -38, 122, 23, 102, -108,
            -95, 29, 61, -16, -34, -77, 11, 114, -89, 28,
            -17, -47, 83, 62, -113, 51, 38, 95, -20, 118,
            42, 73, -127, -120, -18, 33, -60, 26, -21, -39,
            -59, 57, -103, -51, -83, 49, -117, 1, 24, 35,
            -35, 31, 78, 45, -7, 72, 79, -14, 101, -114,
            120, 92, 88, 25, -115, -27, -104, 87, 103, 127,
            5, 100, -81, 99, -74, -2, -11, -73, 60, -91,
            -50, -23, 104, 68, -32, 77, 67, 105, 41, 46,
            -84, 21, 89, -88, 10, -98, 110, 71, -33, 52,
            53, 106, -49, -36, 34, -55, -64, -101, -119, -44,
            -19, -85, 18, -94, 13, 82, -69, 2, 47, -87,
            -41, 97, 30, -76, 80, 4, -10, -62, 22, 37,
            -122, 86, 85, 9, -66, -111
        }
    };
    private static final int P_00 = 1;
    private static final int P_01 = 0;
    private static final int P_02 = 0;
    private static final int P_03 = 1;
    private static final int P_04 = 1;
    private static final int P_10 = 0;
    private static final int P_11 = 0;
    private static final int P_12 = 1;
    private static final int P_13 = 1;
    private static final int P_14 = 0;
    private static final int P_20 = 1;
    private static final int P_21 = 1;
    private static final int P_22 = 0;
    private static final int P_23 = 0;
    private static final int P_24 = 0;
    private static final int P_30 = 0;
    private static final int P_31 = 1;
    private static final int P_32 = 1;
    private static final int P_33 = 0;
    private static final int P_34 = 1;
    private static final int GF256_FDBK = 361;
    private static final int GF256_FDBK_2 = 180;
    private static final int GF256_FDBK_4 = 90;
    private static final int MDS[][];
    private static final int RS_GF_FDBK = 333;
    private static final char HEX_DIGITS[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'
    };

    static
    {
        MDS = new int[4][256];
        long l = System.currentTimeMillis();
        int ai[] = new int[2];
        int ai1[] = new int[2];
        int ai2[] = new int[2];
        for(int i = 0; i < 256; i++)
        {
            int j = P[0][i] & 0xff;
            ai[0] = j;
            ai1[0] = Mx_X(j) & 0xff;
            ai2[0] = Mx_Y(j) & 0xff;
            j = P[1][i] & 0xff;
            ai[1] = j;
            ai1[1] = Mx_X(j) & 0xff;
            ai2[1] = Mx_Y(j) & 0xff;
            MDS[0][i] = ai[1] << 0 | ai1[1] << 8 | ai2[1] << 16 | ai2[1] << 24;
            MDS[1][i] = ai2[0] << 0 | ai2[0] << 8 | ai1[0] << 16 | ai[0] << 24;
            MDS[2][i] = ai1[1] << 0 | ai2[1] << 8 | ai[1] << 16 | ai2[1] << 24;
            MDS[3][i] = ai1[0] << 0 | ai[0] << 8 | ai2[0] << 16 | ai1[0] << 24;
        }

        l = System.currentTimeMillis() - l;
    }
}
