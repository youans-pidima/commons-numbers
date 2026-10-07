























package org.apache.commons.numbers.examples.jmh.gamma;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Supplier;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction.Coefficient;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;





@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class GammaContinuedFractionPerformance {


    static final String IMP_NUMBERS_1_0 = "Numbers 1.0";

    static final String IMP_NUMBERS_EXT_A = "Numbers Extended A";


    static final String IMP_NUMBERS_EXT_A1 = "Numbers Extended A1";

    static final String IMP_ITERATOR = "Iterator";

    static final String IMP_NUMBERS_1_1 = "Numbers 1.1";

    static final String IMP_NUMBERS_1_1_INC = "Numbers 1.1 Inc";







    private static final double SMALL = 1e-50;




    private static final double EPSILON = 0x1.0p-52;

    private static final int MAX_ITERATIONS = 100000;










    private static final double[][] A_Z = {
                 {32.5, 65.0},
                 {33.0, 66.0},
                 {36.5, 73.0},
                 {37.0, 74.0},
                 {1.6504575484077577E-12, 100.0},
                 {2.0654589150126412E-12, 100.0},
                 {6.93323176648164E-12, 100.0},
                 {1.335143107183967E-11, 100.0},
                 {1.6399770430552962E-11, 100.0},
                 {5.730160790307082E-11, 100.0},
                 {1.113731329382972E-10, 100.0},
                 {1.4214707189097453E-10, 100.0},
                 {3.800633141537446E-10, 100.0},
                 {6.09162720266454E-10, 100.0},
                 {1.0221641311147778E-9, 100.0},
                 {2.8819229225263143E-9, 100.0},
                 {4.7627768395841485E-9, 100.0},
                 {8.854135202795987E-9, 100.0},
                 {2.305032964500242E-8, 100.0},
                 {5.9392490925347374E-8, 100.0},
                 {1.1667650312574551E-7, 100.0},
                 {2.3799674409019644E-7, 100.0},
                 {4.684659415943315E-7, 100.0},
                 {9.382700909554842E-7, 100.0},
                 {1.1039858236472355E-6, 100.0},
                 {3.2917764656303916E-6, 100.0},
                 {7.517214726249222E-6, 100.0},
                 {1.5114666894078255E-5, 100.0},
                 {2.986399704241194E-5, 100.0},
                 {3.387029209989123E-5, 100.0},
                 {9.06601344468072E-5, 100.0},
                 {2.194953412981704E-4, 100.0},
                 {4.395215946715325E-4, 100.0},
                 {6.333151832222939E-4, 100.0},
                 {0.0011151233920827508, 100.0},
                 {0.001962467795237899, 100.0},
                 {0.005553754977881908, 100.0},
                 {0.00869112927466631, 100.0},
                 {0.029933366924524307, 100.0},
                 {0.05124260485172272, 100.0},
                 {0.9759566783905029, 1.9519133567810059},
                 {0.9759566783905029, 100.0},
                 {3.667367935180664, 4.034104824066162},
                 {3.667367935180664, 7.334735870361328},
                 {3.667367935180664, 366.7367858886719},
                 {3.927384853363037, 4.320123195648193},
                 {3.927384853363037, 7.854769706726074},
                 {3.927384853363037, 392.7384948730469},
                 {4.053312301635742, 4.458643436431885},
                 {4.053312301635742, 8.106624603271484},
                 {4.053312301635742, 405.33123779296875},
                 {4.125904560089111, 4.538495063781738},
                 {4.125904560089111, 8.251809120178223},
                 {4.125904560089111, 412.5904541015625},
                 {5.094053268432617, 5.603458404541016},
                 {5.094053268432617, 10.188106536865234},
                 {5.094053268432617, 509.40533447265625},
                 {5.596034526824951, 6.155638217926025},
                 {5.596034526824951, 11.192069053649902},
                 {5.596034526824951, 559.6034545898438},
                 {10.16461181640625, 11.181073188781738},
                 {10.16461181640625, 20.3292236328125},
                 {10.205269813537598, 11.225796699523926},
                 {10.205269813537598, 20.410539627075195},
                 {11.431244850158691, 12.574369430541992},
                 {11.431244850158691, 22.862489700317383},
                 {11.69021987915039, 12.859241485595703},
                 {11.69021987915039, 23.38043975830078},
                 {12.955684661865234, 14.251253128051758},
                 {12.955684661865234, 25.91136932373047},
                 {13.026715278625488, 14.329386711120605},
                 {13.026715278625488, 26.053430557250977},
                 {13.135188102722168, 14.44870662689209},
                 {13.135188102722168, 26.270376205444336},
                 {13.979962348937988, 15.377958297729492},
                 {13.979962348937988, 27.959924697875977},
                 {14.617691040039062, 16.07946014404297},
                 {14.617691040039062, 29.235382080078125},
                 {15.336841583251953, 16.870525360107422},
                 {15.336841583251953, 30.673683166503906},
                 {16.18250274658203, 17.800752639770508},
                 {16.18250274658203, 32.36500549316406},
                 {17.5330753326416, 19.2863826751709},
                 {17.5330753326416, 35.0661506652832},
                 {17.799583435058594, 19.57954216003418},
                 {17.799583435058594, 35.59916687011719},
                 {19.09382438659668, 21.003206253051758},
                 {19.09382438659668, 38.18764877319336},
                 {19.24400520324707, 21.168405532836914},
                 {19.24400520324707, 38.48801040649414},
                 {21.415802001953125, 42.83160400390625},
                 {21.586471557617188, 43.172943115234375},
                 {22.492887496948242, 44.985774993896484},
                 {28.053834915161133, 56.107669830322266},
                 {28.210573196411133, 56.421146392822266},
                 {30.054428100585938, 60.108856201171875},
                 {30.540353775024414, 61.08070755004883},
                 {31.162620544433594, 62.32524108886719},
                 {31.996768951416016, 63.99353790283203},
                 {32.05139923095703, 64.10279846191406},
                 {36.448753356933594, 72.89750671386719},
                 {38.465065002441406, 76.93013000488281},
                 {39.526588439941406, 79.05317687988281},
                 {40.17448425292969, 80.34896850585938},
                 {41.16875076293945, 82.3375015258789},
                 {42.465248107910156, 84.93049621582031},
                 {42.49772262573242, 84.99544525146484},
                 {44.15506362915039, 88.31012725830078},
                 {44.8358268737793, 89.6716537475586},
                 {46.06991958618164, 92.13983917236328},
                 {47.738487243652344, 95.47697448730469},
                 {48.79487609863281, 97.58975219726562},
                 {49.01310729980469, 98.02621459960938},
                 {49.2315559387207, 98.4631118774414},
                 {49.3136100769043, 98.6272201538086},
                 {50.61444091796875, 101.2288818359375},
                 {54.91470718383789, 109.82941436767578},
                 {54.948448181152344, 109.89689636230469},
                 {63.41974639892578, 126.83949279785156},
                 {64.15645599365234, 128.3129119873047},
                 {64.80814361572266, 129.6162872314453},
                 {65.72004699707031, 131.44009399414062},
                 {65.74620056152344, 131.49240112304688},
                 {66.52874755859375, 133.0574951171875},
                 {68.03414916992188, 136.06829833984375},
                 {68.29527282714844, 136.59054565429688},
                 {69.63545227050781, 139.27090454101562},
                 {70.7515869140625, 141.503173828125},
                 {71.08180236816406, 142.16360473632812},
                 {72.72097778320312, 145.44195556640625},
                 {74.19440460205078, 148.38880920410156},
                 {74.44168090820312, 148.88336181640625},
                 {75.59132385253906, 151.18264770507812},
                 {75.8951416015625, 151.790283203125},
                 {76.49312591552734, 152.9862518310547},
                 {76.6689224243164, 153.3378448486328},
                 {79.32462310791016, 158.6492462158203},
                 {79.5005111694336, 159.0010223388672},
                 {79.62239074707031, 159.24478149414062},
                 {79.829345703125, 159.65869140625},
                 {79.8938980102539, 159.7877960205078},
                 {79.91152954101562, 159.82305908203125},
                 {80.1279067993164, 160.2558135986328},
                 {80.84933471679688, 161.69866943359375},
                 {81.56500244140625, 163.1300048828125},
                 {82.27938079833984, 164.5587615966797},
                 {82.43405151367188, 164.86810302734375},
                 {83.5833511352539, 167.1667022705078},
                 {84.98836517333984, 169.9767303466797},
                 {87.30667114257812, 174.61334228515625},
                 {87.90385437011719, 175.80770874023438},
                 {90.62629699707031, 181.25259399414062},
                 {91.38089752197266, 182.7617950439453},
                 {91.61568450927734, 183.2313690185547},
                 {92.12703704833984, 184.2540740966797},
                 {93.43232727050781, 186.86465454101562},
                 {95.0470962524414, 190.0941925048828},
                 {95.73811340332031, 191.47622680664062},
                 {95.77192687988281, 191.54385375976562},
                 {95.96949768066406, 191.93899536132812},
                 {96.50640869140625, 193.0128173828125},
                 {96.78564453125, 193.5712890625},
                 {96.90234375, 193.8046875},
                 {97.07398223876953, 194.14796447753906},
                 {98.12041473388672, 196.24082946777344},
                 {99.29168701171875, 198.5833740234375},
                 {99.4098129272461, 198.8196258544922},
                 {99.64790344238281, 199.29580688476562},
                 {1.7306554127571872E-6, 100.0},
                 {2.1657506295014173E-6, 100.0},
                 {7.2700195232755505E-6, 100.0},
                 {1.4000004739500582E-5, 100.0},
                 {1.71964547917014E-5, 100.0},
                 {6.008507625665516E-5, 100.0},
                 {1.1678319424390793E-4, 100.0},
                 {1.490520080551505E-4, 100.0},
                 {3.98525211494416E-4, 100.0},
                 {6.387534085661173E-4, 100.0},
                 {0.0010718167759478092, 100.0},
                 {0.0030219152104109526, 100.0},
                 {0.004994133487343788, 100.0},
                 {0.009284233674407005, 100.0},
                 {0.02417002245783806, 100.0},
                 {0.06227754056453705, 100.0},
                 {0.12234418094158173, 100.0},
                 {0.24955767393112183, 100.0},
                 {0.4912221431732178, 100.0},
                 {0.9838474988937378, 1.9676949977874756},
                 {0.9838474988937378, 100.0},
                 {1.1576130390167236, 2.3152260780334473},
                 {1.1576130390167236, 115.76130676269531},
                 {3.4516777992248535, 3.7968456745147705},
                 {3.4516777992248535, 6.903355598449707},
                 {3.4516777992248535, 345.16778564453125},
                 {7.882370948791504, 8.670608520507812},
                 {7.882370948791504, 15.764741897583008},
                 {15.848876953125, 17.433765411376953},
                 {15.848876953125, 31.69775390625},
                 {31.31467056274414, 62.62934112548828},
                 {35.51557540893555, 71.0311508178711},
                 {95.06404113769531, 190.12808227539062},
                 {230.1575469970703, 460.3150939941406},
                 {460.8717956542969, 921.7435913085938},
                 {0.75, 708.0},
                 {0.75, 708.5},
                 {0.75, 707.5},
                 {32.5, 34.5},
                 {33.0, 35.0},
                 {36.5, 38.5},
                 {37.0, 39.0},
                 {21.415802001953125, 23.415802001953125},
                 {21.586471557617188, 23.586471557617188},
                 {22.492887496948242, 24.492887496948242},
                 {28.053834915161133, 30.053834915161133},
                 {28.210573196411133, 30.210573196411133},
                 {30.054428100585938, 32.05442810058594},
                 {30.540353775024414, 32.54035186767578},
                 {31.162620544433594, 33.162620544433594},
                 {31.996768951416016, 33.996768951416016},
                 {32.05139923095703, 34.05139923095703},
                 {36.448753356933594, 38.448753356933594},
                 {38.465065002441406, 40.465065002441406},
                 {39.526588439941406, 41.526588439941406},
                 {40.17448425292969, 42.17448425292969},
                 {41.16875076293945, 43.16875076293945},
                 {42.465248107910156, 44.465248107910156},
                 {42.49772262573242, 44.49772262573242},
                 {44.15506362915039, 46.15506362915039},
                 {44.8358268737793, 46.8358268737793},
                 {46.06991958618164, 48.06991958618164},
                 {47.738487243652344, 49.738487243652344},
                 {48.79487609863281, 50.79487609863281},
                 {49.01310729980469, 51.01310729980469},
                 {49.2315559387207, 51.2315559387207},
                 {49.3136100769043, 51.3136100769043},
                 {50.61444091796875, 52.61444091796875},
                 {54.91470718383789, 56.91470718383789},
                 {54.948448181152344, 56.948448181152344},
                 {63.41974639892578, 65.41974639892578},
                 {64.15645599365234, 66.15645599365234},
                 {64.80814361572266, 66.80814361572266},
                 {65.72004699707031, 67.72004699707031},
                 {65.74620056152344, 67.74620056152344},
                 {66.52874755859375, 68.52874755859375},
                 {68.03414916992188, 70.03414916992188},
                 {68.29527282714844, 70.29527282714844},
                 {69.63545227050781, 71.63545227050781},
                 {70.7515869140625, 72.7515869140625},
                 {71.08180236816406, 73.08180236816406},
                 {72.72097778320312, 74.72097778320312},
                 {74.19440460205078, 76.19440460205078},
                 {74.44168090820312, 76.44168090820312},
                 {75.59132385253906, 77.59132385253906},
                 {75.8951416015625, 77.8951416015625},
                 {76.49312591552734, 78.49312591552734},
                 {76.6689224243164, 78.6689224243164},
                 {79.32462310791016, 81.32462310791016},
                 {79.5005111694336, 81.5005111694336},
                 {79.62239074707031, 81.62239074707031},
                 {79.829345703125, 81.829345703125},
                 {79.8938980102539, 81.8938980102539},
                 {79.91152954101562, 81.91152954101562},
                 {80.1279067993164, 82.1279067993164},
                 {80.84933471679688, 82.84933471679688},
                 {81.56500244140625, 83.56500244140625},
                 {82.27938079833984, 84.27938079833984},
                 {82.43405151367188, 84.43405151367188},
                 {83.5833511352539, 85.5833511352539},
                 {84.98836517333984, 86.98836517333984},
                 {87.30667114257812, 89.30667114257812},
                 {87.90385437011719, 89.90385437011719},
                 {90.62629699707031, 92.62629699707031},
                 {91.38089752197266, 93.38089752197266},
                 {91.61568450927734, 93.61568450927734},
                 {92.12703704833984, 94.12703704833984},
                 {93.43232727050781, 95.43232727050781},
                 {95.0470962524414, 97.0470962524414},
                 {95.73811340332031, 97.73811340332031},
                 {95.77192687988281, 97.77192687988281},
                 {95.96949768066406, 97.96949768066406},
                 {96.50640869140625, 98.50640869140625},
                 {96.78564453125, 98.78564453125},
                 {96.90234375, 98.90234375},
                 {97.07398223876953, 99.07398223876953},
                 {98.12041473388672, 100.12041473388672},
                 {99.29168701171875, 101.29168701171875},
                 {99.4098129272461, 101.4098129272461},
                 {99.64790344238281, 101.64790344238281},
                 {7.882370948791504, 788.2371215820312},
                 {31.31467056274414, 33.31467056274414},
                 {35.51557540893555, 37.51557540893555},
                 {95.06404113769531, 97.06404113769531},
                 {230.1575469970703, 23015.75390625},
                 {460.8717956542969, 46087.1796875},
                 {664.0791015625, 66407.90625},
                 {1169.2916259765625, 116929.1640625},
                 {2057.796630859375, 205779.65625},
                 {5823.5341796875, 582353.4375},
                 {9113.3095703125, 911330.9375},
                 {31387.41015625, 3138741.0},
                 {53731.765625, 5373176.5},
                 {117454.09375, 1.1745409E7},
                 {246209.65625, 2.4620966E7},
                 {513669.1875, 5.136692E7},
                 {788352.3125, 7.8835232E7},
                 {1736170.0, 1.73616992E8},
                 {170.0, 1000.0},
                 {185.0, 1500.0},
    };




    @State(Scope.Benchmark)
    public static class BaseData {

        private static final String MSG_DIVERGED = "Continued fraction diverged to %s for value %s";

        private static final String MSG_MAX_ITERATIONS = "Maximum iterations exceeded: ";


        @Param({IMP_NUMBERS_1_0, IMP_NUMBERS_EXT_A, IMP_NUMBERS_EXT_A1,
                IMP_ITERATOR, IMP_NUMBERS_1_1, IMP_NUMBERS_1_1_INC})
        private String implementation;


        private DoubleBinaryOperator function;






        public DoubleBinaryOperator getFunction() {
            return function;
        }




        @Setup
        public void setup() {
            function = createFunction(implementation);
        }








        static DoubleBinaryOperator createFunction(String implementation) {
            if (IMP_NUMBERS_1_0.equals(implementation)) {

                return (a, z) -> {
                    final ContinuedFraction cf = new ContinuedFraction() {

                        @Override
                        protected double getA(int n, double x) {
                            return n * (a - n);
                        }


                        @Override
                        protected double getB(int n, double x) {
                            return ((2 * n) + 1) - a + x;
                        }
                    };

                    return 1 / cf.evaluate(z, EPSILON, MAX_ITERATIONS);
                };
            } else if (IMP_NUMBERS_EXT_A.equals(implementation)) {


                return (a, z) -> {
                    final ContinuedFractionA cf = new ContinuedFractionA() {
                        @Override
                        protected double getA(int n, double x) {
                            n++;
                            return n * (a - n);
                        }

                        @Override
                        protected double getB(int n, double x) {
                            n++;
                            return ((2 * n) + 1) - a + x;
                        }
                    };

                    return 1 / (z - a + 1 + cf.evaluate(z, EPSILON, MAX_ITERATIONS));
                };
            } else if (IMP_NUMBERS_EXT_A1.equals(implementation)) {

                return (a, z) -> {
                    final ContinuedFractionA1 cf = new ContinuedFractionA1() {
                        @Override
                        protected double getA(int n, double x) {
                            return n * (a - n);
                        }

                        @Override
                        protected double getB(int n, double x) {
                            return ((2 * n) + 1) - a + x;
                        }
                    };

                    return 1 / (z - a + 1 + cf.evaluate(z, EPSILON, MAX_ITERATIONS));
                };
            } else if (IMP_ITERATOR.equals(implementation)) {

                return (a, z) -> {
                    final ContinuedFractionIterator cf = new ContinuedFractionIterator() {

                        private double x = z - a + 1;

                        private int k;

                        @Override
                        void next() {
                            ++k;
                            x += 2;
                            setCoefficients(k * (a - k), x);
                        }
                    };

                    return 1 / (z - a + 1 + cf.evaluate(EPSILON, MAX_ITERATIONS));
                };
            } else if (IMP_NUMBERS_1_1.equals(implementation)) {


                return (a, z) -> {
                    final double zma1 = z - a + 1;

                    final Supplier<Coefficient> gen = new Supplier<>() {

                        private int k;

                        @Override
                        public Coefficient get() {
                            ++k;
                            return Coefficient.of(k * (a - k), zma1 + 2.0 * k);
                        }
                    };

                    return 1 / GeneralizedContinuedFraction.value(zma1, gen, EPSILON, MAX_ITERATIONS);
                };
            } else if (IMP_NUMBERS_1_1_INC.equals(implementation)) {


                return (a, z) -> {

                    final Supplier<Coefficient> gen = new Supplier<>() {

                        private int k;

                        private double b = z - a + 1;

                        @Override
                        public Coefficient get() {
                            ++k;
                            b += 2;
                            return Coefficient.of(k * (a - k), b);
                        }
                    };

                    return 1 / GeneralizedContinuedFraction.value(z - a + 1, gen, EPSILON, MAX_ITERATIONS);
                };
            } else {
                throw new IllegalStateException("unknown: " + implementation);
            }
        }



























        abstract static class ContinuedFraction {








            protected abstract double getA(int n, double x);









            protected abstract double getB(int n, double x);


























            public double evaluate(double x, double epsilon, int maxIterations) {


                final double eps =  epsilon > EPSILON ? epsilon : EPSILON;

                double hPrev = updateIfCloseToZero(getB(0, x));

                int n = 1;
                double dPrev = 0.0;
                double cPrev = hPrev;
                double hN;

                while (n <= maxIterations) {
                    final double a = getA(n, x);
                    final double b = getB(n, x);

                    double dN = updateIfCloseToZero(b + a * dPrev);
                    final double cN = updateIfCloseToZero(b + a / cPrev);

                    dN = 1 / dN;
                    final double deltaN = cN * dN;
                    hN = hPrev * deltaN;

                    if (!Double.isFinite(hN)) {
                        throw new ArithmeticException(String.format(
                                MSG_DIVERGED, hN, x));
                    }


                    if (deltaN == 0) {
                        throw new ArithmeticException();
                    }

                    if (Math.abs(deltaN - 1) < eps) {
                        return hN;
                    }

                    dPrev = dN;
                    cPrev = cN;
                    hPrev = hN;
                    ++n;
                }

                throw new ArithmeticException(MSG_MAX_ITERATIONS + maxIterations);
            }
        }















        abstract static class ContinuedFractionA extends ContinuedFraction {
            @Override
            public double evaluate(double x, double epsilon, int maxIterations) {


                final double eps =  epsilon > EPSILON ? epsilon : EPSILON;

                final double a0 = getA(0, x);

                double hPrev = updateIfCloseToZero(getB(0, x));

                int n = 1;
                double dPrev = 0.0;
                double cPrev = hPrev;
                double hN;

                while (n <= maxIterations) {
                    final double a = getA(n, x);
                    final double b = getB(n, x);

                    double dN = updateIfCloseToZero(b + a * dPrev);
                    final double cN = updateIfCloseToZero(b + a / cPrev);

                    dN = 1 / dN;
                    final double deltaN = cN * dN;
                    hN = hPrev * deltaN;

                    if (!Double.isFinite(hN)) {
                        throw new ArithmeticException(String.format(
                            MSG_DIVERGED, hN, x));
                    }

                    if (deltaN == 0) {
                        throw new ArithmeticException();
                    }

                    if (Math.abs(deltaN - 1) < eps) {

                        return a0 / hN;
                    }

                    dPrev = dN;
                    cPrev = cN;
                    hPrev = hN;
                    ++n;
                }

                throw new ArithmeticException(MSG_MAX_ITERATIONS + maxIterations);
            }
        }















        abstract static class ContinuedFractionA1 extends ContinuedFraction {
            @Override
            public double evaluate(double x, double epsilon, int maxIterations) {


                final double eps =  epsilon > EPSILON ? epsilon : EPSILON;

                final double a0 = getA(1, x);

                double hPrev = updateIfCloseToZero(getB(1, x));

                int n = 2;
                double dPrev = 0.0;
                double cPrev = hPrev;
                double hN;

                while (n <= maxIterations) {
                    final double a = getA(n, x);
                    final double b = getB(n, x);

                    double dN = updateIfCloseToZero(b + a * dPrev);
                    final double cN = updateIfCloseToZero(b + a / cPrev);

                    dN = 1 / dN;
                    final double deltaN = cN * dN;
                    hN = hPrev * deltaN;

                    if (!Double.isFinite(hN)) {
                        throw new ArithmeticException(String.format(
                            MSG_DIVERGED, hN, x));
                    }

                    if (deltaN == 0) {
                        throw new ArithmeticException();
                    }

                    if (Math.abs(deltaN - 1) < eps) {

                        return a0 / hN;
                    }

                    dPrev = dN;
                    cPrev = cN;
                    hPrev = hN;
                    ++n;
                }

                throw new ArithmeticException(MSG_MAX_ITERATIONS + maxIterations);
            }
        }














        abstract static class ContinuedFractionIterator {

            private double a = 1;

            private double b = 1;





            protected void setCoefficients(double ca, double cb) {
                this.a = ca;
                this.b = cb;
            }




            abstract void next();








            public double evaluate(double epsilon, int maxIterations) {


                final double eps = epsilon > EPSILON ? epsilon : EPSILON;

                next();
                final double a0 = a;

                double hPrev = updateIfCloseToZero(b);

                int n = 2;
                double dPrev = 0.0;
                double cPrev = hPrev;
                double hN;

                while (n <= maxIterations) {
                    next();

                    double dN = updateIfCloseToZero(b + a * dPrev);
                    final double cN = updateIfCloseToZero(b + a / cPrev);

                    dN = 1 / dN;
                    final double deltaN = cN * dN;
                    hN = hPrev * deltaN;

                    if (!Double.isFinite(hN)) {
                        throw new ArithmeticException("Continued fraction diverged to: " + hN);
                    }

                    if (deltaN == 0) {
                        throw new ArithmeticException();
                    }




                    if (Math.abs(deltaN - 1) <= eps) {

                        return a0 / hN;
                    }

                    dPrev = dN;
                    cPrev = cN;
                    hPrev = hN;
                    ++n;
                }

                throw new ArithmeticException(MSG_MAX_ITERATIONS + maxIterations);
            }
        }










        private static double updateIfCloseToZero(double value) {
            return Math.abs(value) < SMALL ? Math.copySign(SMALL, value) : value;
        }
    }






    static double[][] getData() {
        return Arrays.stream(A_Z).map(d -> d.clone()).toArray(double[][]::new);
    }







    private static void apply(DoubleBinaryOperator fun, Blackhole bh) {
        for (int i = 0; i < A_Z.length; i++) {
            final double[] az = A_Z[i];
            bh.consume(fun.applyAsDouble(az[0], az[1]));
        }
    }










    @Benchmark
    public void evaluate(BaseData data, Blackhole bh) {
        apply(data.getFunction(), bh);
    }
}
