package com.ws.mesh.awe.utils;

public final class MathUtil {

    /**
     * Returns the closest double approximation of the arc tangent of the
     * argument within the range {@code [-pi/2..pi/2]}. The returned result is
     * within 1 ulp (unit in the last place) of the real result.
     * <p/>
     * Special cases:
     * <ul>
     * <li>{@code atan(+0.0) = +0.0}</li>
     * <li>{@code atan(-0.0) = -0.0}</li>
     * <li>{@code atan(+infinity) = +pi/2}</li>
     * <li>{@code atan(-infinity) = -pi/2}</li>
     * <li>{@code atan(NaN) = NaN}</li>
     * </ul>
     *
     * @return the arc tangent of the argument.
     */
    public static double Radian(double vec_x, double vec_y) {
        if (vec_x == 0) {
            if (vec_y == 0)
                return 0;
            else if (vec_y > 0)
                return Math.PI / 2;
            else
                return Math.PI / -2;
        } else if (vec_x > 0)
            return Math.atan(vec_y / vec_x);
        else if (vec_x < 0) {
            if (vec_y >= 0)
                return Math.PI + Math.atan(vec_y / vec_x);
            else
                return Math.atan(vec_y / vec_x) - Math.PI;
        }
        return 0;
    }

    public static double Angel(double vec_x, double vec_y) {
        return Radian(vec_x, vec_y) * 180.0 / Math.PI;
    }

    public static double Angel(double rad) {
        return rad * 180.0 / Math.PI;
    }

    public static double Radian(double angel) {
        return angel * Math.PI / 180.0;
    }

    /**
     * Constrain.
     */
    public static float constrain(float v, float l, float h) {
        return v < l ? l : (v > h ? h : v);
    }

    public static double constrain(double v, double l, double h) {
        return v < l ? l : (v > h ? h : v);
    }

    public static int constrain(int v, int l, int h) {
        return v < l ? l : (v > h ? h : v);
    }
}
