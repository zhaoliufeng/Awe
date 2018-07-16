package com.ws.mesh.awe.utils;

import android.graphics.Color;
import android.util.SparseArray;

import com.ws.mesh.awe.bean.Device;

import java.util.Arrays;
import java.util.Locale;

public class Utils {
    /**
     * @param startColor 开始颜色
     * @param endColor   结束颜色
     * @param pos        中间值
     * @return 中间值的颜色
     */
    public static int interpolate(int startColor, int endColor, float pos) {
        int startA = alpha(startColor);
        int startR = red(startColor);
        int startG = green(startColor);
        int startB = blue(startColor);

        int endA = alpha(endColor);
        int endR = red(endColor);
        int endG = green(endColor);
        int endB = blue(endColor);

        int interA = (int) (startA + (endA - startA) * pos);
        int interR = (int) (startR + (endR - startR) * pos);
        int interG = (int) (startG + (endG - startG) * pos);
        int interB = (int) (startB + (endB - startB) * pos);

        return argb(interA, interR, interG, interB);
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static byte[] weekNumToBinaryByteArray(int intVal) {
        byte[] values = new byte[]{0, 0, 0, 0, 0, 0, 0};
        if (intVal != -1) {
            int step = 1;
            while (intVal != 0) {
                values[values.length - step] = (byte) (intVal % 2);
                intVal /= 2;
                step++;
            }
        }
        return values;
    }

    public static int byteArrayToWeekNum(byte[] weekByte) {
        int value;
        value = (weekByte[0] << 6)
                + (weekByte[1] << 5)
                + (weekByte[2] << 4)
                + (weekByte[3] << 3)
                + (weekByte[4] << 2)
                + (weekByte[5] << 1)
                + (weekByte[6]);
        return value;
    }


    //翻转byte[]
    public static byte[] reverseBytes(byte[] src) {
        byte tempVal = src[0];
        for (int i = 0; i < src.length / 2; i++) {
            src[i] = src[src.length - i - 1];
            src[src.length - i - 1] = tempVal;
            tempVal = src[i + 1];
        }
        return src;
    }

    public static String[] getAlarmShow(int hours, int minutes) {
        String[] data = new String[2];
        if (hours >= 0 && hours < 12) {
            data[0] = alarmShow(hours == 0 ? 12 : hours) + ":" + alarmShow(minutes);
            data[1] = "AM";
        } else {
            data[0] = alarmShow(hours == 12 ? 12 : hours - 12) + ":" + alarmShow(minutes);
            data[1] = "PM";
        }
        return data;
    }


    private static String alarmShow(int time) {
        if (time < 10) {
            if (time == -1) return "0";
            return "0" + time;
        } else {
            return Integer.toString(time);
        }
    }

    public synchronized static int getVaildMeshAddress(SparseArray<Device> mDeviceArray) {
        for (int x = 1; x < 254; x++) {
            if (mDeviceArray.get(x & 0xFF) == null) {
                return x;
            }
        }
        return -1;
    }

    public static class UIColor {
        private final static int
                H = 0, S = 1, B = 2, A = 3,
                RGBA_SECTION = 0xFF;
        double[] hsba = new double[ 4 ];     // All: [0.0, 1.0]
        long rgba = 0;                 // all to 0, [0, 255]

        @Deprecated
        public UIColor() {
        }   // Only used for ISRUtil.

//	    public UIColor(int argb)
//	    {
//	        this(red(argb), green(argb), blue(argb), alpha(argb));
//	    }

        /*
         * r,g,b => [0, 255]
         */
        public UIColor(int r, int g, int b) {
            this(r, g, b, 0xFF);
        }

        /*
         * r,g,b,a => [0, 255]
         */
        public UIColor(int r, int g, int b, int a) {
            rgba = Color.argb(a, r, g, b);

            float[] hsba_f = new float[ 4 ];
//            Color.RGBToHSV(r, g, b, hsba_f);
            hsba_f[ H ] /= 360.0;
            for (int idx = 0 ; idx < A ; ++idx)
                hsba[ idx ] = hsba_f[ idx ];

            hsba[ A ] = (a % RGBA_SECTION) / (float) RGBA_SECTION;

            if (hsba[ A ] > 1.0) hsba[ A ] = 1.0f;
            else if (hsba[ A ] < 0.0) hsba[ A ] = 0.0f;
        }

        /*
         * h,s,b,a => [0.0, 1.0]
         */
        public UIColor(double h, double s, double b) {
            this(h, s, b, 1.0);
        }

        public UIColor(double h, double s, double b, double a) {
            fromHSBA(h, s, b, a);
        }

        public void fromHSBA(double h, double s, double b, double a) {
            hsba[ H ] = h;
            hsba[ S ] = s;
            hsba[ B ] = b;
            hsba[ A ] = a;

            for (int idx = 0 ; idx < hsba.length ; idx++)
                if (hsba[ idx ] > 1.0f)
                    hsba[ idx ] = 1.0f;
                else if (hsba[ idx ] < 0.0f)
                    hsba[ idx ] = 0.0f;

            rgba = Color.HSVToColor((int) (hsba[ A ] * RGBA_SECTION),
                    new float[]{ (float) (h * 360.0), (float) s, (float) b });
        }

        public int R() {
            return Color.red((int) rgba);
        }

        public int G() {
            return Color.green((int) rgba);
        }

        public int B() {
            return Color.blue((int) rgba);
        }

        public int Ai() {
            return Color.alpha((int) rgba);
        }

        public int ARGB() {
            return (int) rgba;
        }

        public double hue() {
            return hsba[ H ];
        }

        public double sat() {
            return hsba[ S ];
        }

        public double brt() {
            return hsba[ B ];
        }

        public double Af() {
            return hsba[ A ];
        }

        // public UIColor hue(double h)
        // { hsba[H] = h; return this; }
        // public UIColor sat(double s)
        // { hsba[S] = s; return this; }
        // public UIColor brt(double b)
        // { hsba[B] = b; return this; }
        // public UIColor Af(double a)
        // { hsba[A] = a; return this; }

        public String toString() {
            return String.format(Locale.getDefault(),
                    "A:%4f / R: %x G:%x B:%x / H:%4f S:%4f B:%4f", Af(), R(),
                    G(), B(), hue(), sat(), brt());
        }

//        @Override
//        public void saveClass(Map<String, Object> map) {
//            // ISRUtil.save(hsba, "hsba", map);
//            ISRUtil.save(hsba[H], "H", map);
//            ISRUtil.save(hsba[S], "S", map);
//            ISRUtil.save(hsba[B], "B", map);
//            ISRUtil.save(hsba[A], "A", map);
//        }
//
//        @Override
//        public Object restoreClass(Map<String, Object> map) {
//            // hsba = ISRUtil.restore(hsba, "hsba", map);
//            hsba[H] = ISRUtil.restore(hsba[H], "H", map);
//            hsba[S] = ISRUtil.restore(hsba[S], "S", map);
//            hsba[B] = ISRUtil.restore(hsba[B], "B", map);
//            hsba[A] = ISRUtil.restore(hsba[A], "A", map);
//            fromHSBA(hsba[H], hsba[S], hsba[B], hsba[A]);
//
//            return this;
//        }

        public static int interpolate(int startColor, int endColor, float pos) {
            int startA = Color.alpha(startColor);
            int startR = Color.red(startColor);
            int startG = Color.green(startColor);
            int startB = Color.blue(startColor);

            int endA = Color.alpha(endColor);
            int endR = Color.red(endColor);
            int endG = Color.green(endColor);
            int endB = Color.blue(endColor);

            int interA = (int) (startA + (endA - startA) * pos);
            int interR = (int) (startR + (endR - startR) * pos);
            int interG = (int) (startG + (endG - startG) * pos);
            int interB = (int) (startB + (endB - startB) * pos);

            return Color.argb(interA, interR, interG, interB);
        }
    }

    public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB) {
        assert 0 <= rgbR && rgbR <= 255;
        assert 0 <= rgbG && rgbG <= 255;
        assert 0 <= rgbB && rgbB <= 255;
        int[] rgb = new int[]{ rgbR, rgbG, rgbB };
        Arrays.sort(rgb);
        int max = rgb[ 2 ];
        int min = rgb[ 0 ];

        float hsbB = max / 255.0f;
        float hsbS = max == 0 ? 0 : (max - min) / (float) max;

        float hsbH = 0;
        if (max == rgbR && rgbG >= rgbB) {
            hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;
        } else if (max == rgbR && rgbG < rgbB) {
            hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;
        } else if (max == rgbG) {
            hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;
        } else if (max == rgbB) {
            hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;
        }

        return new float[]{ hsbH, hsbS, hsbB };
    }
}
