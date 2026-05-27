import java.util.Map;
import java.util.function.Function;

public class Logic {
    private final static double THRESHOLD = 1e-12;

    public record CurveInstance(Curve type, double[] params) {
        public CurveInstance {
            params = params.clone();
        }

        public Map<Character, Double> mapParams() {
            return type.transformToCanonical(params);
        }

        public double[] params() {
            return params.clone();
        }

        public String getFormulaString() {
            return type.getFormulaString(mapParams());
        }

        public Function<Double, double[]> getYFunction() {
            return type.getYFunction(mapParams());
        }

        public String getTypeString() {
            return type.label;
        }
    }

    private static double[] rotate(double[] params) {
        // ax2 + bxy + cy2 + dx + ey + f = 0
        double a = params[0], b = params[1], c = params[2], d = params[3], e = params[4], f = params[5];

        double turnAngle = Math.atan2(b, a - c) / 2;
        double turnSin = Math.sin(turnAngle);
        double turnCos = Math.cos(turnAngle);

        double ta = a * turnCos * turnCos + b * turnCos * turnSin + c * turnSin * turnSin;
        double tc = a * turnSin * turnSin - b * turnCos * turnSin + c * turnCos * turnCos;
        double td = d * turnCos + e * turnSin;
        double te = e * turnCos - d * turnSin;
        double tf = f;

        return new double[] { ta, tc, td, te, tf };
    }

    public static int sign(double a) {
        if (Math.abs(a) < THRESHOLD)
            return 0;
        if (a > 0)
            return 1;
        return -1;
    }

    public static CurveInstance findCurve(double[] initParams) {
        double[] params = rotate(initParams);

        // ax2 + cy2 + dx + ey + f = 0
        double a = params[0], c = params[1], d = params[2], e = params[3], f = params[4];
        int signA = sign(params[0]);
        int signC = sign(params[1]);

        if (signA == 0 && signC == 0) {
            return new CurveInstance(Curve.LINE, params);
        }

        if (signA == 0 || signC == 0) {
            boolean isVertical = (signC == 0);
            double nonZeroQuad = isVertical ? a : c;
            double linearOfOther = isVertical ? e : d;
            double linearOfQuad = isVertical ? d : e;

            //Один - в квадрате, второй - в первой степени
            if (sign(linearOfOther) != 0) {
                return new CurveInstance(Curve.PARABOLA, params);
            }

            //Иначе считаем дискриминант вида (для a != 0): d^2 - 4ef
            double discriminant = (linearOfQuad * linearOfQuad) - (4 * nonZeroQuad * f);
            int signDisc = sign(discriminant);

            switch (signDisc) {
                case 1:
                    return new CurveInstance(Curve.TWO_LINE, params);
                case 0:
                    return new CurveInstance(Curve.LINE, params);
                case -1:
                    return new CurveInstance(Curve.IMAGINARY, params);
            } 
        }

        double tF = params[4];
        if (signA != 0)
            tF -= Math.pow(params[2], 2) / 4 / params[0]; // f -= d^2 / 4a
        if (signC != 0)
            tF -= Math.pow(params[3], 2) / 4 / params[1]; // f -= e^2 / 4c
        int signF = sign(tF);

        if (signF == 0) {
            if (signA == signC)
                return new CurveInstance(Curve.POINT, params);
            return new CurveInstance(Curve.MODULE, params);
        }

        if (signA != signC)
            return new CurveInstance(Curve.HYPERBOLA, params);

        if (signA == -signF) {
            return new CurveInstance(Curve.ELLIPSE, params);
        }

        return new CurveInstance(Curve.IMAGINARY, params);
    }
}