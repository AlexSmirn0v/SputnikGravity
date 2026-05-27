import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

enum Curve {
    ELLIPSE("Эллипс") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида (x + a)^2/A^2 + (y + b)^2/B^2 = 1
            double right = Math.pow(params[2], 2) / 4 / params[0] + (Math.pow(params[3], 2) / 4 / params[1])
                    - params[4];
            res.put('A', Math.sqrt(Math.abs(right / params[0])));
            res.put('B', Math.sqrt(Math.abs(right / params[1])));
            res.put('a', params[2] / 2 / params[0]);
            res.put('b', params[3] / 2 / params[1]);

            return res;
        } 

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double A = mapParams.get('A');
            Double B = mapParams.get('B');
            Double a = mapParams.get('a');
            Double b = mapParams.get('b');
            if (A == null || B == null || a == null || b == null)
                return "Неверные параметры";
            // (x + a)^2/A^2 + (y + b)^2/B^2 = 1
            String fmt = "(x + %.3f)<sup>2</sup> / %.3f<sup>2</sup> + (y + %.3f)<sup>2</sup> / %.3f<sup>2</sup> = 1";
            return String.format(fmt, a, A, b, B);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double A = mapParams.get('A');
                Double B = mapParams.get('B');
                Double a = mapParams.get('a');
                Double b = mapParams.get('b');
                if (A == null || B == null || a == null || b == null)
                    return new double[0];

                double inside = 1.0 - Math.pow(x + a, 2) / (A * A);
                if (inside < 0)
                    return new double[0];
                double root = Math.sqrt(inside) * B;
                return new double[] { -b + root, -b - root };
            };
        }
    },
    HYPERBOLA("Гипербола") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            if (params[0] < 0) {
                for (int i = 0; i < params.length; i++)
                    params[i] *= -1;
            }
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида (x + a)^2/A^2 - (y + b)^2/B^2 = 1
            double right = Math.pow(params[2], 2) / 4 / params[0] + (Math.pow(params[3], 2) / 4 / params[1])
                    - params[4];
            res.put('A', Math.sqrt(Math.abs(right / params[0])));
            res.put('B', Math.sqrt(Math.abs(right / params[1])));
            res.put('a', params[2] / 2 / params[0]);
            res.put('b', params[3] / 2 / params[1]);

            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double A = mapParams.get('A');
            Double B = mapParams.get('B');
            Double a = mapParams.get('a');
            Double b = mapParams.get('b');
            if (A == null || B == null || a == null || b == null)
                return "Неверные параметры";
            // (x + a)^2/A^2 - (y + b)^2/B^2 = 1
            String fmt = "(x + %.3f)<sup>2</sup> / %.3f<sup>2</sup> - (y + %.3f)<sup>2</sup> / %.3f<sup>2</sup> = 1";
            return String.format(fmt, a, A, b, B);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double A = mapParams.get('A');
                Double B = mapParams.get('B');
                Double a = mapParams.get('a');
                Double b = mapParams.get('b');
                if (A == null || B == null || a == null || b == null)
                    return new double[0];

                double inside = Math.pow(x + a, 2) / (A * A) - 1.0;
                if (inside < 0)
                    return new double[0];
                double root = Math.sqrt(inside) * B;
                return new double[] { -b + root, -b - root };
            };
        }
    },
    PARABOLA("Парабола") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();
            double c, d, e, f;
            if (Logic.sign(params[0]) == 0) {
                c = params[1];
                d = params[2];
                e = params[3];
                f = params[4];
            } else {
                c = params[0];
                d = params[3];
                e = params[2];
                f = params[4];
            }

            // Для зависимости y(x) и вида (y + b)^2 = p(x + a)
            res.put('b', e / 2 / c);
            res.put('a', f / d - e * e / 4 / c / d);
            res.put('p', -d / c);
            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double b = mapParams.get('b');
            Double a = mapParams.get('a');
            Double p = mapParams.get('p');
            if (b == null || a == null || p == null)
                return "Неверные параметры";
            // (y + b)^2 = p(x + a)
            String fmt = "(y + %.3f)<sup>2</sup> = %.3f (x + %.3f)";
            return String.format(fmt, b, p, a);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double b = mapParams.get('b');
                Double a = mapParams.get('a');
                Double p = mapParams.get('p');
                if (b == null || a == null || p == null)
                    return new double[0];

                double inside = p * (x + a);
                if (inside < 0)
                    return new double[0];
                double root = Math.sqrt(inside);
                return new double[] { -b + root, -b - root };
            };
        }
    },
    LINE("Прямая") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида y = kx + a
            if (Logic.sign(params[0]) != 0 || Logic.sign(params[1]) != 0) {
                res.put('k', 0.0);
                res.put('a', 0.0);
            } else {
                res.put('k', -params[2] / params[3]);
                res.put('a', -params[4] / params[3]);
            }
            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double k = mapParams.get('k');
            Double a = mapParams.get('a');
            if (k == null || a == null)
                return "Неверные параметры";
            // y = kx + a
            String fmt = "y = %.3f x + %.3f";
            return String.format(fmt, k, a);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double k = mapParams.get('k');
                Double a = mapParams.get('a');
                if (k == null || a == null)
                    return new double[0];
                return new double[] { k * x + a };
            };
        }
    },
    TWO_LINE("Две параллельные прямые") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида (y + b)^2 - A^2 = 0
            double c, e, f;
            if (Logic.sign(params[0]) == 0) {
                c = params[1];
                e = params[3];
                f = params[4];
            } else {
                c = params[0];
                e = params[2];
                f = params[4];
            }
            res.put('b', e / 2 / c);
            res.put('A', Math.sqrt(e * e / 4 / c / c - f / c));
            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double b = mapParams.get('b');
            Double A = mapParams.get('A');
            if (b == null || A == null)
                return "Неверные параметры";
            // (y + b)^2 - A^2 = 0
            String fmt = "(y + %.3f)<sup>2</sup> - %.3f<sup>2</sup> = 0";
            return String.format(fmt, b, A);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double b = mapParams.get('b');
                Double A = mapParams.get('A');
                if (b == null || A == null)
                    return new double[0];
                return new double[] { -b + A, -b - A };
            };
        }
    },
    MODULE("Две перпендикулярные прямые") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида A(x + a)^2 + B(y + b)^2 = 0
            res.put('A', params[0]);
            res.put('B', params[1]);
            res.put('a', params[2] / 2 / params[0]);
            res.put('b', params[3] / 2 / params[1]);
            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double A = mapParams.get('A');
            Double B = mapParams.get('B');
            Double a = mapParams.get('a');
            Double b = mapParams.get('b');
            if (A == null || B == null || a == null || b == null)
                return "Неверные параметры";
            // A(x + a)^2 + B(y + b)^2 = 0
            String fmt = "%.3f (x + %.3f)<sup>2</sup> + %.3f (y + %.3f)<sup>2</sup> = 0";
            return String.format(fmt, A, a, B, b);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double A = mapParams.get('A');
                Double B = mapParams.get('B');
                Double a = mapParams.get('a');
                Double b = mapParams.get('b');
                if (A == null || B == null || a == null || b == null)
                    return new double[0];

                // (y + b)^2 = -A/B * (x + a)^2
                double factor = -A / B;
                if (factor < 0)
                    return new double[0];
                double multiplier = Math.sqrt(factor);
                double absTerm = Math.abs(x + a);
                double root = multiplier * absTerm;
                return new double[] { -b + root, -b - root };
            };
        }
    },
    POINT("Точка") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            // Для зависимости y(x) и вида (x + a)^2 + (y + b)^2 = 0
            res.put('a', params[2] / 2 / params[0]);
            res.put('b', params[3] / 2 / params[1]);
            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double a = mapParams.get('a');
            Double b = mapParams.get('b');
            if (a == null || b == null)
                return "Неверные параметры";
            // (x + a)^2 + (y + b)^2 = 0
            String fmt = "(x + %.3f)<sup>2</sup> + (y + %.3f)<sup>2</sup> = 0";
            return String.format(fmt, a, b);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> {
                Double a = mapParams.get('a');
                Double b = mapParams.get('b');
                if (a == null || b == null)
                    return new double[0];
                double eps = 1e-9;
                if (Math.abs(x + a) < eps) {
                    return new double[] { -b };
                } else {
                    return new double[0];
                }
            };
        }
    },
    IMAGINARY("Мнимые уравнения") {
        @Override
        public Map<Character, Double> transformToCanonical(double[] params) {
            Map<Character, Double> res = new HashMap<Character, Double>();

            res.put('a', params[0]);
            res.put('c', params[1]);
            res.put('d', params[2]);
            res.put('e', params[3]);
            res.put('f', params[4]);

            return res;
        }

        @Override
        public String getFormulaString(Map<Character, Double> mapParams) {
            Double a = mapParams.get('a');
            Double c = mapParams.get('c');
            Double d = mapParams.get('d');
            Double e = mapParams.get('e');
            Double f = mapParams.get('f');
            if (a == null || c == null || d == null || e == null || f == null)
                return "Неверные параметры";
            // ax^2 + c xy + d y^2 + e x + f y = 0
            String fmt = "%.3f x<sup>2</sup> + %.3f x y + %.3f y<sup>2</sup> + %.3f x + %.3f y = 0";
            return String.format(fmt, a, c, d, e, f);
        }

        @Override
        public Function<Double, double[]> getYFunction(Map<Character, Double> mapParams) {
            return x -> new double[0];
        }
    };

    public final String label;

    private Curve(String label) {
        this.label = label;
    }

    public abstract String getFormulaString(Map<Character, Double> mapParams);

    public abstract Function<Double, double[]> getYFunction(Map<Character, Double> mapParams);

    public abstract Map<Character, Double> transformToCanonical(double[] params);
}
