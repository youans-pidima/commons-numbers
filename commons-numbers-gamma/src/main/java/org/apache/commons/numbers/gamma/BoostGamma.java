
























package org.apache.commons.numbers.gamma;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.apache.commons.numbers.core.Sum;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction.Coefficient;














final class BoostGamma {











































    private static final double EPSILON = 0x1.0p-52;


    private static final double ROOT_EPSILON = 1.4901161193847656E-8;




    private static final int LOG_MAX_VALUE = 709;




    private static final int LOG_MIN_VALUE = -708;


    private static final int MAX_FACTORIAL = 170;

    private static final int MAX_GAMMA_Z = MAX_FACTORIAL + 1;

    private static final double LOG_ROOT_TWO_PI = 0.9189385332046727417803297;

    private static final double LOG_PI = 1.144729885849400174143427;

    private static final double EULER = 0.5772156649015328606065120900824024310;

    private static final int LANCZOS_THRESHOLD = 20;

    private static final double TWO_POW_53 = 0x1.0p53;


    private static final double[] FACTORIAL = {
        1,
        1,
        2,
        6,
        24,
        120,
        720,
        5040,
        40320,
        362880.0,
        3628800.0,
        39916800.0,
        479001600.0,
        6227020800.0,
        87178291200.0,
        1307674368000.0,
        20922789888000.0,
        355687428096000.0,
        6402373705728000.0,
        121645100408832000.0,
        0.243290200817664e19,
        0.5109094217170944e20,
        0.112400072777760768e22,
        0.2585201673888497664e23,
        0.62044840173323943936e24,
        0.15511210043330985984e26,
        0.403291461126605635584e27,
        0.10888869450418352160768e29,
        0.304888344611713860501504e30,
        0.8841761993739701954543616e31,
        0.26525285981219105863630848e33,
        0.822283865417792281772556288e34,
        0.26313083693369353016721801216e36,
        0.868331761881188649551819440128e37,
        0.29523279903960414084761860964352e39,
        0.103331479663861449296666513375232e41,
        0.3719933267899012174679994481508352e42,
        0.137637530912263450463159795815809024e44,
        0.5230226174666011117600072241000742912e45,
        0.203978820811974433586402817399028973568e47,
        0.815915283247897734345611269596115894272e48,
        0.3345252661316380710817006205344075166515e50,
        0.1405006117752879898543142606244511569936e52,
        0.6041526306337383563735513206851399750726e53,
        0.265827157478844876804362581101461589032e55,
        0.1196222208654801945619631614956577150644e57,
        0.5502622159812088949850305428800254892962e58,
        0.2586232415111681806429643551536119799692e60,
        0.1241391559253607267086228904737337503852e62,
        0.6082818640342675608722521633212953768876e63,
        0.3041409320171337804361260816606476884438e65,
        0.1551118753287382280224243016469303211063e67,
        0.8065817517094387857166063685640376697529e68,
        0.427488328406002556429801375338939964969e70,
        0.2308436973392413804720927426830275810833e72,
        0.1269640335365827592596510084756651695958e74,
        0.7109985878048634518540456474637249497365e75,
        0.4052691950487721675568060190543232213498e77,
        0.2350561331282878571829474910515074683829e79,
        0.1386831185456898357379390197203894063459e81,
        0.8320987112741390144276341183223364380754e82,
        0.507580213877224798800856812176625227226e84,
        0.3146997326038793752565312235495076408801e86,
        0.1982608315404440064116146708361898137545e88,
        0.1268869321858841641034333893351614808029e90,
        0.8247650592082470666723170306785496252186e91,
        0.5443449390774430640037292402478427526443e93,
        0.3647111091818868528824985909660546442717e95,
        0.2480035542436830599600990418569171581047e97,
        0.1711224524281413113724683388812728390923e99,
        0.1197857166996989179607278372168909873646e101,
        0.8504785885678623175211676442399260102886e102,
        0.6123445837688608686152407038527467274078e104,
        0.4470115461512684340891257138125051110077e106,
        0.3307885441519386412259530282212537821457e108,
        0.2480914081139539809194647711659403366093e110,
        0.188549470166605025498793226086114655823e112,
        0.1451830920282858696340707840863082849837e114,
        0.1132428117820629783145752115873204622873e116,
        0.8946182130782975286851441715398316520698e117,
        0.7156945704626380229481153372318653216558e119,
        0.5797126020747367985879734231578109105412e121,
        0.4753643337012841748421382069894049466438e123,
        0.3945523969720658651189747118012061057144e125,
        0.3314240134565353266999387579130131288001e127,
        0.2817104114380550276949479442260611594801e129,
        0.2422709538367273238176552320344125971528e131,
        0.210775729837952771721360051869938959523e133,
        0.1854826422573984391147968456455462843802e135,
        0.1650795516090846108121691926245361930984e137,
        0.1485715964481761497309522733620825737886e139,
        0.1352001527678402962551665687594951421476e141,
        0.1243841405464130725547532432587355307758e143,
        0.1156772507081641574759205162306240436215e145,
        0.1087366156656743080273652852567866010042e147,
        0.103299784882390592625997020993947270954e149,
        0.9916779348709496892095714015418938011582e150,
        0.9619275968248211985332842594956369871234e152,
        0.942689044888324774562618574305724247381e154,
        0.9332621544394415268169923885626670049072e156,
        0.9332621544394415268169923885626670049072e158,
        0.9425947759838359420851623124482936749562e160,
        0.9614466715035126609268655586972595484554e162,
        0.990290071648618040754671525458177334909e164,
        0.1029901674514562762384858386476504428305e167,
        0.1081396758240290900504101305800329649721e169,
        0.1146280563734708354534347384148349428704e171,
        0.1226520203196137939351751701038733888713e173,
        0.132464181945182897449989183712183259981e175,
        0.1443859583202493582204882102462797533793e177,
        0.1588245541522742940425370312709077287172e179,
        0.1762952551090244663872161047107075788761e181,
        0.1974506857221074023536820372759924883413e183,
        0.2231192748659813646596607021218715118256e185,
        0.2543559733472187557120132004189335234812e187,
        0.2925093693493015690688151804817735520034e189,
        0.339310868445189820119825609358857320324e191,
        0.396993716080872089540195962949863064779e193,
        0.4684525849754290656574312362808384164393e195,
        0.5574585761207605881323431711741977155627e197,
        0.6689502913449127057588118054090372586753e199,
        0.8094298525273443739681622845449350829971e201,
        0.9875044200833601362411579871448208012564e203,
        0.1214630436702532967576624324188129585545e206,
        0.1506141741511140879795014161993280686076e208,
        0.1882677176888926099743767702491600857595e210,
        0.237217324288004688567714730513941708057e212,
        0.3012660018457659544809977077527059692324e214,
        0.3856204823625804217356770659234636406175e216,
        0.4974504222477287440390234150412680963966e218,
        0.6466855489220473672507304395536485253155e220,
        0.8471580690878820510984568758152795681634e222,
        0.1118248651196004307449963076076169029976e225,
        0.1487270706090685728908450891181304809868e227,
        0.1992942746161518876737324194182948445223e229,
        0.269047270731805048359538766214698040105e231,
        0.3659042881952548657689727220519893345429e233,
        0.5012888748274991661034926292112253883237e235,
        0.6917786472619488492228198283114910358867e237,
        0.9615723196941089004197195613529725398826e239,
        0.1346201247571752460587607385894161555836e242,
        0.1898143759076170969428526414110767793728e244,
        0.2695364137888162776588507508037290267094e246,
        0.3854370717180072770521565736493325081944e248,
        0.5550293832739304789551054660550388118e250,
        0.80479260574719919448490292577980627711e252,
        0.1174997204390910823947958271638517164581e255,
        0.1727245890454638911203498659308620231933e257,
        0.2556323917872865588581178015776757943262e259,
        0.380892263763056972698595524350736933546e261,
        0.571338395644585459047893286526105400319e263,
        0.8627209774233240431623188626544191544816e265,
        0.1311335885683452545606724671234717114812e268,
        0.2006343905095682394778288746989117185662e270,
        0.308976961384735088795856467036324046592e272,
        0.4789142901463393876335775239063022722176e274,
        0.7471062926282894447083809372938315446595e276,
        0.1172956879426414428192158071551315525115e279,
        0.1853271869493734796543609753051078529682e281,
        0.2946702272495038326504339507351214862195e283,
        0.4714723635992061322406943211761943779512e285,
        0.7590705053947218729075178570936729485014e287,
        0.1229694218739449434110178928491750176572e290,
        0.2004401576545302577599591653441552787813e292,
        0.3287218585534296227263330311644146572013e294,
        0.5423910666131588774984495014212841843822e296,
        0.9003691705778437366474261723593317460744e298,
        0.1503616514864999040201201707840084015944e301,
        0.2526075744973198387538018869171341146786e303,
        0.4269068009004705274939251888899566538069e305,
        0.7257415615307998967396728211129263114717e307,
    };






















    static final class Lanczos {

















        static final double G = 6.024680040776729583740234375;






        static final double GMH = 5.524680040776729583740234375;


        private static final int[] DENOM = {
            0,
            39916800,
            120543840,
            150917976,
            105258076,
            45995730,
            13339535,
            2637558,
            357423,
            32670,
            1925,
            66,
            1
        };


        private Lanczos() {

        }







        static double lanczosSum(double z) {
            final double[] num = {
                23531376880.41075968857200767445163675473,
                42919803642.64909876895789904700198885093,
                35711959237.35566804944018545154716670596,
                17921034426.03720969991975575445893111267,
                6039542586.35202800506429164430729792107,
                1439720407.311721673663223072794912393972,
                248874557.8620541565114603864132294232163,
                31426415.58540019438061423162831820536287,
                2876370.628935372441225409051620849613599,
                186056.2653952234950402949897160456992822,
                8071.672002365816210638002902272250613822,
                210.8242777515793458725097339207133627117,
                2.506628274631000270164908177133837338626
            };
            return evaluateRational(num, DENOM, z);
        }







        static double lanczosSumExpGScaled(double z) {

            final double[] num = {
                56906521.91347156388090791033559122686859,
                103794043.1163445451906271053616070238554,
                86363131.28813859145546927288977868422342,
                43338889.32467613834773723740590533316085,
                14605578.08768506808414169982791359218571,
                3481712.15498064590882071018964774556468,
                601859.6171681098786670226533699352302507,
                75999.29304014542649875303443598909137092,
                6955.999602515376140356310115515198987526,
                449.9445569063168119446858607650988409623,
                19.51992788247617482847860966235652136208,
                0.5098416655656676188125178644804694509993,
                0.006061842346248906525783753964555936883222
            };
            return evaluateRational(num, DENOM, z);
        }






















        private static double evaluateRational(double[] a, int[] b, double x) {









            if (x <= 1) {
                final double x2 = x * x;
                double t0 = a[12] * x2 + a[10];
                double t1 = a[11] * x2 + a[9];
                double t2 = b[12] * x2 + b[10];
                double t3 = b[11] * x2 + b[9];
                t0 *= x2;
                t1 *= x2;
                t2 *= x2;
                t3 *= x2;
                t0 += a[8];
                t1 += a[7];
                t2 += b[8];
                t3 += b[7];
                t0 *= x2;
                t1 *= x2;
                t2 *= x2;
                t3 *= x2;
                t0 += a[6];
                t1 += a[5];
                t2 += b[6];
                t3 += b[5];
                t0 *= x2;
                t1 *= x2;
                t2 *= x2;
                t3 *= x2;
                t0 += a[4];
                t1 += a[3];
                t2 += b[4];
                t3 += b[3];
                t0 *= x2;
                t1 *= x2;
                t2 *= x2;
                t3 *= x2;
                t0 += a[2];
                t1 += a[1];
                t2 += b[2];
                t3 += b[1];
                t0 *= x2;
                t2 *= x2;
                t0 += a[0];
                t2 += b[0];
                t1 *= x;
                t3 *= x;
                return (t0 + t1) / (t2 + t3);
            }
            final double z = 1 / x;
            final double z2 = 1 / (x * x);
            double t0 = a[0] * z2 + a[2];
            double t1 = a[1] * z2 + a[3];
            double t2 = b[0] * z2 + b[2];
            double t3 = b[1] * z2 + b[3];
            t0 *= z2;
            t1 *= z2;
            t2 *= z2;
            t3 *= z2;
            t0 += a[4];
            t1 += a[5];
            t2 += b[4];
            t3 += b[5];
            t0 *= z2;
            t1 *= z2;
            t2 *= z2;
            t3 *= z2;
            t0 += a[6];
            t1 += a[7];
            t2 += b[6];
            t3 += b[7];
            t0 *= z2;
            t1 *= z2;
            t2 *= z2;
            t3 *= z2;
            t0 += a[8];
            t1 += a[9];
            t2 += b[8];
            t3 += b[9];
            t0 *= z2;
            t1 *= z2;
            t2 *= z2;
            t3 *= z2;
            t0 += a[10];
            t1 += a[11];
            t2 += b[10];
            t3 += b[11];
            t0 *= z2;
            t2 *= z2;
            t0 += a[12];
            t2 += b[12];
            t1 *= z;
            t3 *= z;
            return (t0 + t1) / (t2 + t3);
        }




    }


    private BoostGamma() {

    }







    static double[] getFactorials() {
        return FACTORIAL.clone();
    }










    static double uncheckedFactorial(int n) {
        return FACTORIAL[n];
    }






















    static double tgamma(double z) {

        if (Math.rint(z) == z) {
            if (z <= 0) {

                return Double.NaN;
            }
            if (z <= MAX_GAMMA_Z) {

                return FACTORIAL[(int) z - 1];
            }

            return Double.POSITIVE_INFINITY;
        }

        if (Math.abs(z) <= LANCZOS_THRESHOLD) {







            if (z >= 1) {








                double prod = 1;
                double t = z;
                while (t > 2.5) {
                    t -= 1;
                    prod *= t;
                }
                return prod / (1 + InvGamma1pm1.value(t - 1));
            }







            double prod = z;
            double t = z;
            while (t < -0.5) {
                t += 1;
                prod *= t;
            }
            return 1 / (prod * (1 + InvGamma1pm1.value(t)));
        }




        if (z < 0) {








            return -Math.PI / (sinpx(z) * tgamma(-z));
        } else if (z > MAX_GAMMA_Z + 1) {

            return Double.POSITIVE_INFINITY;
        }

        double result = Lanczos.lanczosSum(z);
        final double zgh = z + Lanczos.GMH;
        final double lzgh = Math.log(zgh);
        if (z * lzgh > LOG_MAX_VALUE) {







            final double hp = Math.pow(zgh, (z / 2) - 0.25);
            result *= hp / Math.exp(zgh);


            result *= hp;
        } else {
            result *= Math.pow(zgh, z - 0.5) / Math.exp(zgh);
        }

        return result;
    }








    static double sinpx(double x) {
        int sign = 1;


        x = -x;
        double fl = Math.floor(x);
        double dist;
        if (isOdd(fl)) {
            fl += 1;
            dist = fl - x;
            sign = -sign;
        } else {
            dist = x - fl;
        }
        if (dist > 0.5f) {
            dist = 1 - dist;
        }
        final double result = Math.sin(dist * Math.PI);
        return sign * x * result;
    }







    private static boolean isOdd(double v) {








        return (((long) -v) & 0x1) == 1;
    }








    static double lgamma(double z) {
        return lgamma(z, null);
    }









    static double lgamma(double z, int[] sign) {
        double result;
        int sresult = 1;
        if (z <= -ROOT_EPSILON) {

            if (Math.rint(z) == z) {

                return Double.NaN;
            }

            double t = sinpx(z);
            z = -z;
            if (t < 0) {
                t = -t;
            } else {
                sresult = -sresult;
            }



            result = Sum.of(-lgamma(z)).add(-Math.log(t)).add(LOG_PI).getAsDouble();
        } else if (z < ROOT_EPSILON) {
            if (z == 0) {

                return Double.NaN;
            }
            if (4 * Math.abs(z) < EPSILON) {
                result = -Math.log(Math.abs(z));
            } else {
                result = Math.log(Math.abs(1 / z - EULER));
            }
            if (z < 0) {
                sresult = -1;
            }
        } else if (z < 15) {
            result = lgammaSmall(z, z - 1, z - 2);


        } else if (z < 100) {

            result = Math.log(tgamma(z));
        } else {

            final double zgh = z + Lanczos.GMH;
            result = Math.log(zgh) - 1;
            result *= z - 0.5f;



            if (result * EPSILON < 20) {
                result += Math.log(Lanczos.lanczosSumExpGScaled(z));
            }
        }

        if (nonZeroLength(sign)) {
            sign[0] = sresult;
        }
        return result;
    }









    private static double lgammaSmall(double z, double zm1, double zm2) {





        final Sum result = Sum.create();










        if ((zm1 == 0) || (zm2 == 0)) {

            return 0;
        } else if (z > 2) {




            if (z >= 3) {
                do {
                    z -= 1;
                    result.add(Math.log(z));
                } while (z >= 3);

                zm2 = z - 2;
            }

















            double P;
            P = -0.324588649825948492091e-4;
            P = -0.541009869215204396339e-3 + P * zm2;
            P = -0.259453563205438108893e-3 + P * zm2;
            P =  0.172491608709613993966e-1 + P * zm2;
            P =  0.494103151567532234274e-1 + P * zm2;
            P =   0.25126649619989678683e-1 + P * zm2;
            P = -0.180355685678449379109e-1 + P * zm2;
            double Q;
            Q = -0.223352763208617092964e-6;
            Q =  0.224936291922115757597e-3 + Q * zm2;
            Q =   0.82130967464889339326e-2 + Q * zm2;
            Q =  0.988504251128010129477e-1 + Q * zm2;
            Q =   0.541391432071720958364e0 + Q * zm2;
            Q =   0.148019669424231326694e1 + Q * zm2;
            Q =   0.196202987197795200688e1 + Q * zm2;
            Q =                       0.1e1 + Q * zm2;

            final float Y = 0.158963680267333984375e0f;

            final double r = zm2 * (z + 1);
            final double R = P / Q;

            result.addProduct(r, Y).addProduct(r, R);
        } else {




            if (z < 1) {
                result.add(-Math.log(z));
                zm2 = zm1;
                zm1 = z;
                z += 1;
            }




            if (z <= 1.5) {


















                final float Y = 0.52815341949462890625f;

                double P;
                P = -0.100346687696279557415e-2;
                P = -0.240149820648571559892e-1 + P * zm1;
                P =  -0.158413586390692192217e0 + P * zm1;
                P =  -0.406567124211938417342e0 + P * zm1;
                P =  -0.414983358359495381969e0 + P * zm1;
                P = -0.969117530159521214579e-1 + P * zm1;
                P =  0.490622454069039543534e-1 + P * zm1;
                double Q;
                Q = 0.195768102601107189171e-2;
                Q = 0.577039722690451849648e-1 + Q * zm1;
                Q =  0.507137738614363510846e0 + Q * zm1;
                Q =  0.191415588274426679201e1 + Q * zm1;
                Q =  0.348739585360723852576e1 + Q * zm1;
                Q =  0.302349829846463038743e1 + Q * zm1;
                Q =                      0.1e1 + Q * zm1;

                final double r = P / Q;
                final double prefix = zm1 * zm2;

                result.addProduct(prefix, Y).addProduct(prefix, r);
            } else {

















                final float Y = 0.452017307281494140625f;

                final double mzm2 = -zm2;
                double P;
                P =  0.431171342679297331241e-3;
                P = -0.850535976868336437746e-2 + P * mzm2;
                P =  0.542809694055053558157e-1 + P * mzm2;
                P =  -0.142440390738631274135e0 + P * mzm2;
                P =   0.144216267757192309184e0 + P * mzm2;
                P = -0.292329721830270012337e-1 + P * mzm2;
                double Q;
                Q = -0.827193521891290553639e-6;
                Q = -0.100666795539143372762e-2 + Q * mzm2;
                Q =   0.25582797155975869989e-1 + Q * mzm2;
                Q =  -0.220095151814995745555e0 + Q * mzm2;
                Q =   0.846973248876495016101e0 + Q * mzm2;
                Q =  -0.150169356054485044494e1 + Q * mzm2;
                Q =                       0.1e1 + Q * mzm2;
                final double r = zm2 * zm1;
                final double R = P / Q;

                result.addProduct(r, Y).addProduct(r, R);
            }
        }
        return result.getAsDouble();
    }







    static double tgamma1pm1(double dz) {




        final double result;
        if (dz < 0) {
            if (dz < -0.5) {

                result = tgamma(1 + dz) - 1;
            } else {

                result = Math.expm1(-Math.log1p(dz) + lgammaSmall(dz + 2, dz + 1, dz));
            }
        } else {
            if (dz < 2) {

                result = Math.expm1(lgammaSmall(dz + 1, dz, dz - 1));
            } else {

                result = tgamma(1 + dz) - 1;
            }
        }

        return result;
    }








    static double tgamma(double a, double x) {
        return gammaIncompleteImp(a, x, false, true, Policy.getDefault());
    }









    static double tgamma(double a, double x, Policy policy) {
        return gammaIncompleteImp(a, x, false, true, policy);
    }








    static double tgammaLower(double a, double x) {
        return gammaIncompleteImp(a, x, false, false, Policy.getDefault());
    }









    static double tgammaLower(double a, double x, Policy policy) {
        return gammaIncompleteImp(a, x, false, false, policy);
    }








    static double gammaQ(double a, double x) {
        return gammaIncompleteImp(a, x, true, true, Policy.getDefault());
    }









    static double gammaQ(double a, double x, Policy policy) {
        return gammaIncompleteImp(a, x, true, true, policy);
    }








    static double gammaP(double a, double x) {
        return gammaIncompleteImp(a, x, true, false, Policy.getDefault());
    }









    static double gammaP(double a, double x, Policy policy) {
        return gammaIncompleteImp(a, x, true, false, policy);
    }











    static double gammaPDerivative(double a, double x) {



        if (Double.isNaN(a) || Double.isNaN(x) || a <= 0 || x < 0) {
            return Double.NaN;
        }



        if (x == 0) {
            if (a > 1) {
                return 0;
            }
            return (a == 1) ? 1 : Double.POSITIVE_INFINITY;
        }



        double f1 = regularisedGammaPrefix(a, x);
        if (f1 == 0) {

            f1 = a * Math.log(x) - x - lgamma(a) - Math.log(x);
            f1 = Math.exp(f1);
        } else {


            f1 /= x;
        }

        return f1;
    }

















    private static double gammaIncompleteImp(double a, double x,
            boolean normalised, boolean invert, Policy pol) {
        if (Double.isNaN(a) || Double.isNaN(x) || a <= 0 || x < 0) {
            return Double.NaN;
        }

        double result = 0;

        if (a >= MAX_FACTORIAL && !normalised) {











            if (invert && (a * 4 < x)) {

                result = a * Math.log(x) - x;
                result += Math.log(upperGammaFraction(a, x, pol));
            } else if (!invert && (a > 4 * x)) {

                result = a * Math.log(x) - x;
                result += Math.log(lowerGammaSeries(a, x, 0, pol) / a);
            } else {
                result = gammaIncompleteImp(a, x, true, invert, pol);
                if (result == 0) {
                    if (invert) {

                        result = 1 + 1 / (12 * a) + 1 / (288 * a * a);
                        result = Math.log(result) - a + (a - 0.5f) * Math.log(a) + LOG_ROOT_TWO_PI;
                    } else {



                        result = a * Math.log(x) - x;
                        result += Math.log(lowerGammaSeries(a, x, 0, pol) / a);
                    }
                } else {
                    result = Math.log(result) + lgamma(a);
                }
            }


            return Math.exp(result);
        }

        final boolean isInt;
        final boolean isHalfInt;

        final boolean isSmallA = (a < 30) && (a <= x + 1) && (-x > LOG_MIN_VALUE);
        if (isSmallA) {
            final double fa = Math.floor(a);
            isInt = fa == a;
            isHalfInt = !isInt && (Math.abs(fa - a) == 0.5f);
        } else {
            isInt = isHalfInt = false;
        }

        final int evalMethod;

        if (isInt && (x > 0.6)) {

            invert = !invert;
            evalMethod = 0;
        } else if (isHalfInt && (x > 0.2)) {

            invert = !invert;
            evalMethod = 1;
        } else if ((x < ROOT_EPSILON) && (a > 1)) {
            evalMethod = 6;
        } else if ((x > 1000) && (a < x * 0.75f)) {
















            invert = !invert;
            evalMethod = 7;
        } else if (x < 0.5) {



            if (-0.4 / Math.log(x) < a) {

                evalMethod = 2;
            } else {
                evalMethod = 3;
            }
        } else if (x < 1.1) {



            if (x * 0.75f < a) {

                evalMethod = 2;
            } else {
                evalMethod = 3;
            }
        } else {





            boolean useTemme = false;
            if (normalised && (a > 20)) {
                final double sigma = Math.abs((x - a) / a);
                if (a > 200) {








                    if (20 / a > sigma * sigma) {
                        useTemme = true;
                    }
                } else {



                    if (sigma < 0.4) {
                        useTemme = true;
                    }
                }
            }
            if (useTemme) {
                evalMethod = 5;
            } else {








                if (x - (1 / (3 * x)) < a) {
                    evalMethod = 2;
                } else {
                    evalMethod = 4;
                    invert = !invert;
                }
            }
        }

        switch (evalMethod) {
        case 0:
            result = finiteGammaQ(a, x);
            if (!normalised) {
                result *= tgamma(a);
            }
            break;
        case 1:
            result = finiteHalfGammaQ(a, x);
            if (!normalised) {
                result *= tgamma(a);
            }
            break;
        case 2:

            result = normalised ? regularisedGammaPrefix(a, x) : fullIgammaPrefix(a, x);
            if (result != 0) {













                double initValue = 0;
                boolean optimisedInvert = false;
                if (invert) {
                    initValue = normalised ? 1 : tgamma(a);
                    if (normalised || (result >= 1) || (Double.MAX_VALUE * result > initValue)) {
                        initValue /= result;
                        if (normalised || (a < 1) || (Double.MAX_VALUE / a > initValue)) {
                            initValue *= -a;
                            optimisedInvert = true;
                        } else {
                            initValue = 0;
                        }
                    } else {
                        initValue = 0;
                    }
                }
                result *= lowerGammaSeries(a, x, initValue, pol) / a;
                if (optimisedInvert) {
                    invert = false;
                    result = -result;
                }
            }
            break;
        case 3:

            invert = !invert;
            final double[] g = {0};
            result = tgammaSmallUpperPart(a, x, pol, g, invert);
            invert = false;
            if (normalised) {

                if (g[0] == Double.POSITIVE_INFINITY) {



                    result = Math.exp(Math.log(result) - lgamma(a));
                } else {
                    result /= g[0];
                }
            }
            break;
        case 4:

            result = normalised ? regularisedGammaPrefix(a, x) : fullIgammaPrefix(a, x);
            if (result != 0) {
                result *= upperGammaFraction(a, x, pol);
            }
            break;
        case 5:

            result = igammaTemmeLarge(a, x);
            if (x >= a) {
                invert = !invert;
            }
            break;
        case 6:


            if (normalised) {

                result = Math.pow(x, a) / tgamma(a + 1);
            } else {
                result = Math.pow(x, a) / a;
            }
            result *= 1 - a * x / (a + 1);
            break;
        case 7:
        default:


            result = normalised ? regularisedGammaPrefix(a, x) : fullIgammaPrefix(a, x);
            result /= x;
            if (result != 0) {
                result *= incompleteTgammaLargeX(a, x, pol);
            }
            break;
        }

        if (normalised && (result > 1)) {
            result = 1;
        }
        if (invert) {
            final double gam = normalised ? 1 : tgamma(a);
            result = gam - result;
        }

        return result;
    }













    static double upperGammaFraction(double a, double z, Policy pol) {
        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();
















        final double zma1 = z - a + 1;

        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {

            private int k;

            @Override
            public Coefficient get() {
                ++k;
                return Coefficient.of(k * (a - k), zma1 + 2.0 * k);
            }
        };

        return 1 / GeneralizedContinuedFraction.value(zma1, gen, eps, maxIterations);
    }









    private static double finiteGammaQ(double a, double x) {







        double sum = Math.exp(-x);
        double term = sum;
        for (int n = 1; n < a; ++n) {
            term /= n;
            term *= x;
            sum += term;
        }
        return sum;
    }









    private static double finiteHalfGammaQ(double a, double x) {








        double e = BoostErf.erfc(Math.sqrt(x));
        if (a > 1) {
            double term = Math.exp(-x) / Math.sqrt(Math.PI * x);
            term *= x;
            term /= 0.5;
            double sum = term;
            for (int n = 2; n < a; ++n) {
                term /= n - 0.5;
                term *= x;
                sum += term;
            }
            e += sum;
        }
        return e;
    }














    static double lowerGammaSeries(double a, double z, double initValue, Policy pol) {
        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();


        final DoubleSupplier gen = new DoubleSupplier() {

            private double result = 1;

            private int n;

            @Override
            public double getAsDouble() {
                final double r = result;
                n++;
                result *= z / (a + n);
                return r;
            }
        };

        return BoostTools.kahanSumSeries(gen, eps, maxIterations, initValue);
    }











    private static double tgammaSmallUpperPart(double a, double x, Policy pol, double[] pgam, boolean invert) {



        double result;
        result = tgamma1pm1(a);





        pgam[0] = (result + 1) / a;

        double p = BoostMath.powm1(x, a);
        result -= p;
        result /= a;

        final int maxIter = pol.getMaxIterations();
        p += 1;
        final double initValue = invert ? pgam[0] : 0;


        final DoubleSupplier gen = new DoubleSupplier() {

            private double result = -x;

            private final double z = -x;

            private int n = 1;

            @Override
            public double getAsDouble() {
                final double r = result / (a + n);
                n++;
                result = result * z / n;
                return r;
            }
        };

        result = -p * BoostTools.kahanSumSeries(gen, pol.getEps(), maxIter, (initValue - result) / p);
        if (invert) {
            result = -result;
        }
        return result;
    }









    static double fullIgammaPrefix(double a, double z) {
        if (z > Double.MAX_VALUE) {
            return 0;
        }
        final double alz = a * Math.log(z);
        final double prefix;

        if (z >= 1) {
            if ((alz < LOG_MAX_VALUE) && (-z > LOG_MIN_VALUE)) {
                prefix = Math.pow(z, a) * Math.exp(-z);
            } else if (a >= 1) {
                prefix = Math.pow(z / Math.exp(z / a), a);
            } else {
                prefix = Math.exp(alz - z);
            }
        } else {
            if (alz > LOG_MIN_VALUE) {
                prefix = Math.pow(z, a) * Math.exp(-z);
            } else {







                prefix = Math.pow(z / Math.exp(z / a), a);
            }
        }

        return prefix;
    }










    static double regularisedGammaPrefix(double a, double z) {
        if (z >= Double.MAX_VALUE) {
            return 0;
        }


        if (a <= 1) {













            if (-z <= LOG_MIN_VALUE) {

                return Math.exp(a * Math.log(z) - z - lgamma(a));
            }


            return Math.pow(z, a) * Math.exp(-z) / tgamma(a);
        }











        if (a <= MAX_GAMMA_Z) {
            final double alz1 = a * Math.log(z);
            if (z >= 1) {
                if ((alz1 < LOG_MAX_VALUE) && (-z > LOG_MIN_VALUE)) {
                    return Math.pow(z, a) * Math.exp(-z) / tgamma(a);
                }
            } else if (alz1 > LOG_MIN_VALUE) {
                return Math.pow(z, a) * Math.exp(-z) / tgamma(a);
            }
        }






        final double agh = a + Lanczos.GMH;
        double prefix;

        final double factor = Math.sqrt(agh / Math.E) / Lanczos.lanczosSumExpGScaled(a);




        if (a > 128) {
            final double d = ((z - a) - Lanczos.GMH) / agh;
            if (Math.abs(d * d * a) <= 100) {




                prefix = a * SpecialMath.log1pmx(d) + z * -Lanczos.GMH / agh;
                prefix = Math.exp(prefix);
                return prefix * factor;
            }
        }







        final double alz = a * Math.log(z / agh);
        final double amz = a - z;
        if ((Math.min(alz, amz) <= LOG_MIN_VALUE) || (Math.max(alz, amz) >= LOG_MAX_VALUE)) {
            final double amza = amz / a;
            if ((Math.min(alz, amz) / 2 > LOG_MIN_VALUE) && (Math.max(alz, amz) / 2 < LOG_MAX_VALUE)) {

                final double sq = Math.pow(z / agh, a / 2) * Math.exp(amz / 2);
                prefix = sq * sq;
            } else if ((Math.min(alz, amz) / 4 > LOG_MIN_VALUE) &&
                    (Math.max(alz, amz) / 4 < LOG_MAX_VALUE) && (z > a)) {

                final double sq = Math.pow(z / agh, a / 4) * Math.exp(amz / 4);
                prefix = sq * sq;
                prefix *= prefix;
            } else if ((amza > LOG_MIN_VALUE) && (amza < LOG_MAX_VALUE)) {
                prefix = Math.pow((z * Math.exp(amza)) / agh, a);
            } else {
                prefix = Math.exp(alz + amz);
            }
        } else {
            prefix = Math.pow(z / agh, a) * Math.exp(amz);
        }
        prefix *= factor;
        return prefix;
    }



















































    static double igammaTemmeLarge(double a, double x) {
        final double sigma = (x - a) / a;
        final double phi = -SpecialMath.log1pmx(sigma);
        final double y = a * phi;
        double z = Math.sqrt(2 * phi);
        if (x < a) {
            z = -z;
        }










        final double[] workspace = new double[10];

        final double[] C0 = {
            -0.33333333333333333,
            0.083333333333333333,
            -0.014814814814814815,
            0.0011574074074074074,
            0.0003527336860670194,
            -0.00017875514403292181,
            0.39192631785224378e-4,
            -0.21854485106799922e-5,
            -0.185406221071516e-5,
            0.8296711340953086e-6,
            -0.17665952736826079e-6,
            0.67078535434014986e-8,
            0.10261809784240308e-7,
            -0.43820360184533532e-8,
            0.91476995822367902e-9,
        };
        workspace[0] = BoostTools.evaluatePolynomial(C0, z);

        final double[] C1 = {
            -0.0018518518518518519,
            -0.0034722222222222222,
            0.0026455026455026455,
            -0.00099022633744855967,
            0.00020576131687242798,
            -0.40187757201646091e-6,
            -0.18098550334489978e-4,
            0.76491609160811101e-5,
            -0.16120900894563446e-5,
            0.46471278028074343e-8,
            0.1378633446915721e-6,
            -0.5752545603517705e-7,
            0.11951628599778147e-7,
        };
        workspace[1] = BoostTools.evaluatePolynomial(C1, z);

        final double[] C2 = {
            0.0041335978835978836,
            -0.0026813271604938272,
            0.00077160493827160494,
            0.20093878600823045e-5,
            -0.00010736653226365161,
            0.52923448829120125e-4,
            -0.12760635188618728e-4,
            0.34235787340961381e-7,
            0.13721957309062933e-5,
            -0.6298992138380055e-6,
            0.14280614206064242e-6,
        };
        workspace[2] = BoostTools.evaluatePolynomial(C2, z);

        final double[] C3 = {
            0.00064943415637860082,
            0.00022947209362139918,
            -0.00046918949439525571,
            0.00026772063206283885,
            -0.75618016718839764e-4,
            -0.23965051138672967e-6,
            0.11082654115347302e-4,
            -0.56749528269915966e-5,
            0.14230900732435884e-5,
        };
        workspace[3] = BoostTools.evaluatePolynomial(C3, z);

        final double[] C4 = {
            -0.0008618882909167117,
            0.00078403922172006663,
            -0.00029907248030319018,
            -0.14638452578843418e-5,
            0.66414982154651222e-4,
            -0.39683650471794347e-4,
            0.11375726970678419e-4,
        };
        workspace[4] = BoostTools.evaluatePolynomial(C4, z);

        final double[] C5 = {
            -0.00033679855336635815,
            -0.69728137583658578e-4,
            0.00027727532449593921,
            -0.00019932570516188848,
            0.67977804779372078e-4,
            0.1419062920643967e-6,
            -0.13594048189768693e-4,
            0.80184702563342015e-5,
            -0.22914811765080952e-5,
        };
        workspace[5] = BoostTools.evaluatePolynomial(C5, z);

        final double[] C6 = {
            0.00053130793646399222,
            -0.00059216643735369388,
            0.00027087820967180448,
            0.79023532326603279e-6,
            -0.81539693675619688e-4,
            0.56116827531062497e-4,
            -0.18329116582843376e-4,
        };
        workspace[6] = BoostTools.evaluatePolynomial(C6, z);

        final double[] C7 = {
            0.00034436760689237767,
            0.51717909082605922e-4,
            -0.00033493161081142236,
            0.0002812695154763237,
            -0.00010976582244684731,
        };
        workspace[7] = BoostTools.evaluatePolynomial(C7, z);

        final double[] C8 = {
            -0.00065262391859530942,
            0.00083949872067208728,
            -0.00043829709854172101,
        };
        workspace[8] = BoostTools.evaluatePolynomial(C8, z);
        workspace[9] = -0.00059676129019274625;

        double result = BoostTools.evaluatePolynomial(workspace, 1 / a);
        result *= Math.exp(-y) / Math.sqrt(2 * Math.PI * a);
        if (x < a) {
            result = -result;
        }

        result += BoostErf.erfc(Math.sqrt(y)) / 2;

        return result;
    }













    static double incompleteTgammaLargeX(double a, double x, Policy pol) {
        final double eps = pol.getEps();
        final int maxIterations = pol.getMaxIterations();


        final DoubleSupplier gen = new DoubleSupplier() {

            private double term = 1;

            private int n;

            @Override
            public double getAsDouble() {
                final double result = term;
                n++;
                term *= (a - n) / x;
                return result;
            }
        };

        return BoostTools.kahanSumSeries(gen, eps, maxIterations);
    }







    private static boolean nonZeroLength(int[] array) {
        return array != null && array.length != 0;
    }















    static double tgammaDeltaRatio(double z, double delta) {
        final double zDelta = z + delta;
        if (Double.isNaN(zDelta)) {

            return Double.NaN;
        }
        if (z <= 0 || zDelta <= 0) {

            return tgamma(z) / tgamma(zDelta);
        }




        if (Math.rint(delta) == delta) {
            if (delta == 0) {
                return 1;
            }




            if (Math.rint(z) == z &&
                z <= MAX_GAMMA_Z && zDelta <= MAX_GAMMA_Z) {
                return FACTORIAL[(int) z - 1] / FACTORIAL[(int) zDelta - 1];
            }
            if (Math.abs(delta) < 20) {



                if (delta < 0) {
                    z -= 1;
                    double result = z;
                    for (int d = (int) (delta + 1); d != 0; d++) {
                        z -= 1;
                        result *= z;
                    }
                    return result;
                }
                double result = 1 / z;
                for (int d = (int) (delta - 1); d != 0; d--) {
                    z += 1;
                    result /= z;
                }
                return result;
            }
        }
        return tgammaDeltaRatioImpLanczos(z, delta);
    }















    private static double tgammaDeltaRatioImpLanczos(double z, double delta) {
        if (z < EPSILON) {









            if (MAX_GAMMA_Z < delta) {
                double ratio = tgammaDeltaRatioImpLanczos(delta, MAX_GAMMA_Z - delta);
                ratio *= z;
                ratio *= FACTORIAL[MAX_FACTORIAL];
                return 1 / ratio;
            }
            return 1 / (z * tgamma(z + delta));
        }
        final double zgh = z + Lanczos.GMH;
        double result;
        if (z + delta == z) {





















            result = Math.exp(-delta);
        } else {
            if (Math.abs(delta) < 10) {
                result = Math.exp((0.5 - z) * Math.log1p(delta / zgh));
            } else {
                result = Math.pow(zgh / (zgh + delta), z - 0.5);
            }

            result *= Lanczos.lanczosSum(z) / Lanczos.lanczosSum(z + delta);
        }
        result *= Math.pow(Math.E / (zgh + delta), delta);
        return result;
    }















    static double tgammaRatio(double x, double y) {
        if (x <= 0 || !Double.isFinite(x) || y <= 0 || !Double.isFinite(y)) {
            return Double.NaN;
        }
        if (x <= Double.MIN_NORMAL) {

            return TWO_POW_53 * tgammaRatio(x * TWO_POW_53, y);
        }

        if (x <= MAX_GAMMA_Z && y <= MAX_GAMMA_Z) {

            return tgamma(x) / tgamma(y);
        }
        double prefix = 1;
        if (x < 1) {
            if (y < 2 * MAX_GAMMA_Z) {


                prefix /= x;
                x += 1;
                while (y >= MAX_GAMMA_Z) {
                    y -= 1;
                    prefix /= y;
                }
                return prefix * tgamma(x) / tgamma(y);
            }



            return Math.exp(lgamma(x) - lgamma(y));
        }
        if (y < 1) {
            if (x < 2 * MAX_GAMMA_Z) {


                prefix *= y;
                y += 1;
                while (x >= MAX_GAMMA_Z) {
                    x -= 1;
                    prefix *= x;
                }
                return prefix * tgamma(x) / tgamma(y);
            }



            return Math.exp(lgamma(x) - lgamma(y));
        }



        return tgammaDeltaRatio(x, y - x);
    }
}
