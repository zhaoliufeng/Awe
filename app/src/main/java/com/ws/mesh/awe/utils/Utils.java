package com.ws.mesh.awe.utils;

public class Utils {
    /**
     * @param startColor 开始颜色
     * @param endColor 结束颜色
     * @param pos 中间值
     * @return 中间值的颜色
     */
    public static int interpolate(int startColor, int endColor, float pos){
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
}
