
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.AdaptMode;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.EdgeSelectStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.ExpandStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.KeyStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.LinearStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.PairedKeyStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.SPStrategy;
import org.apache.commons.numbers.examples.jmh.arrays.Partition.StopperStrategy;









final class PartitionFactory {

    private static final Pattern QS_PATTERN = Pattern.compile("QS(\\d+)");

    private static final Pattern EC_PATTERN = Pattern.compile("EC(\\d+)");

    private static final Pattern LC_PATTERN = Pattern.compile("LC(\\d+)");

    private static final Pattern SU_PATTERN = Pattern.compile("SU(\\d+)");

    private static final Pattern RM_PATTERN = Pattern.compile("RM(\\d+\\.?\\d*)");

    private static final Pattern RC_PATTERN = Pattern.compile("RC(\\d+)");

    private static final Pattern CL_PATTERN = Pattern.compile("CL(\\d+)");

    private static final Pattern CF_PATTERN = Pattern.compile("CF(-?\\d+)");

    private static final Pattern OF_PATTERN = Pattern.compile("OF(-?\\d+)");


    private PartitionFactory() {}












    static KthSelector createKthSelector(String name, String prefix) {
        return createKthSelector(name, prefix, 0);
    }























    static KthSelector createKthSelector(String name, String prefix, int qs) {
        final String[] s = {name};
        final PivotingStrategy sp = getEnumOrElse(s, PivotingStrategy.class, Partition.PIVOTING_STRATEGY);
        final int minQuickSelectSize = qs != 0 ? qs : getMinQuickSelectSize(s);

        for (int i = prefix.length(); i < s[0].length(); i++) {
            if (s[0].charAt(i) != '_') {
                throw new IllegalStateException(
                    String.format("Unharvested KthSelector parameters: %s -> %s", name, s[0]));
            }
        }
        return new KthSelector(sp, minQuickSelectSize);
    }













    static Partition createPartition(String name, String prefix) {
        return createPartition(name, prefix, 0, 0);
    }








































    static Partition createPartition(String name, String prefix, int qs, int ec) {
        if (!name.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid prefix: " + prefix + " for " + name);
        }
        final String[] s = {name.substring(prefix.length())};
        final PivotingStrategy sp = getEnumOrElse(s, PivotingStrategy.class, Partition.PIVOTING_STRATEGY);
        final DualPivotingStrategy dp = getEnumOrElse(s, DualPivotingStrategy.class, Partition.DUAL_PIVOTING_STRATEGY);
        final int minQuickSelectSize = qs != 0 ? qs : getMinQuickSelectSize(s);
        final int edgeSelectConstant = ec != 0 ? ec : getEdgeSelectConstant(s);
        final int linearSortSelectConstant = ec != 0 ? ec : getLinearSortSelectConstant(s);
        final int subSamplingSize = getSubSamplingSize(s);
        final KeyStrategy keyStrategy = getEnumOrElse(s, KeyStrategy.class, Partition.KEY_STRATEGY);
        final PairedKeyStrategy pairedKeyStrategy =
            getEnumOrElse(s, PairedKeyStrategy.class, Partition.PAIRED_KEY_STRATEGY);
        final double recursionMultiple = getRecursionMultiple(s);
        final int recursionConstant = getRecursionConstant(s);
        final int compressionLevel = getCompressionLevel(s);
        final int controlFlags = getControlFlags(s);
        final SPStrategy spStrategy = getEnumOrElse(s, SPStrategy.class, Partition.SP_STRATEGY);
        final ExpandStrategy expandStrategy = getEnumOrElse(s, ExpandStrategy.class, Partition.EXPAND_STRATEGY);
        final LinearStrategy linearStrategy = getEnumOrElse(s, LinearStrategy.class, Partition.LINEAR_STRATEGY);
        final EdgeSelectStrategy esStrategy = getEnumOrElse(s, EdgeSelectStrategy.class, Partition.EDGE_STRATEGY);
        final StopperStrategy stopStrategy = getEnumOrElse(s, StopperStrategy.class, Partition.STOPPER_STRATEGY);
        final AdaptMode adaptMode = getEnumOrElse(s, AdaptMode.class, Partition.ADAPT_MODE);

        for (int i = s[0].length(); --i >= 0;) {
            if (s[0].charAt(i) != '_') {
                throw new IllegalStateException(
                    String.format("Unharvested Partition parameters: %s -> %s", name, prefix + s[0]));
            }
        }
        final Partition p = new Partition(sp, dp, minQuickSelectSize,
            edgeSelectConstant, subSamplingSize);


        p.setKeyStrategy(keyStrategy);
        p.setPairedKeyStrategy(pairedKeyStrategy);
        p.setRecursionMultiple(recursionMultiple);
        p.setRecursionConstant(recursionConstant);
        p.setCompression(compressionLevel);
        p.setControlFlags(controlFlags);
        p.setSPStrategy(spStrategy);
        p.setExpandStrategy(expandStrategy);
        p.setLinearStrategy(linearStrategy);
        p.setEdgeSelectStrategy(esStrategy);
        p.setStopperStrategy(stopStrategy);
        p.setLinearSortSelectSize(linearSortSelectConstant);
        p.setAdaptMode(adaptMode);

        return p;
    }









    static int getMinQuickSelectSize(String[] name) {
        final Matcher m = QS_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.MIN_QUICKSELECT_SIZE;
    }







    static int getEdgeSelectConstant(String[] name) {
        final Matcher m = EC_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.EDGESELECT_CONSTANT;
    }







    static int getLinearSortSelectConstant(String[] name) {
        final Matcher m = LC_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.LINEAR_SORTSELECT_SIZE;
    }







    static int getSubSamplingSize(String[] name) {
        final Matcher m = SU_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.SUBSAMPLING_SIZE;
    }







    static double getRecursionMultiple(String[] name) {
        final Matcher m = RM_PATTERN.matcher(name[0]);
        if (m.find()) {
            final double d = Double.parseDouble(m.group(1));
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return d;
        }
        return Partition.RECURSION_MULTIPLE;
    }







    static int getRecursionConstant(String[] name) {
        final Matcher m = RC_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.RECURSION_CONSTANT;
    }







    static int getCompressionLevel(String[] name) {
        final Matcher m = CL_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return Partition.COMPRESSION_LEVEL;
    }








    static int getControlFlags(String[] name) {
        return getControlFlags(name, Partition.CONTROL_FLAGS);
    }









    static int getControlFlags(String[] name, int defaultValue) {
        final Matcher m = CF_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return defaultValue;
    }








    static int getOptionFlags(String[] name) {
        return getOptionFlags(name, Partition.OPTION_FLAGS);
    }









    static int getOptionFlags(String[] name, int defaultValue) {
        final Matcher m = OF_PATTERN.matcher(name[0]);
        if (m.find()) {
            final int i = Integer.parseInt(name[0], m.start(1), m.end(1), 10);
            name[0] = name[0].substring(0, m.start()) + name[0].substring(m.end(), name[0].length());
            return i;
        }
        return defaultValue;
    }










    static <E extends Enum<E>> E getEnumOrElse(String[] name, Class<E> cls, E defaultValue) {

        int index = -1;
        int len = 0;
        E result = defaultValue;
        for (final E s : cls.getEnumConstants()) {

            final int i = name[0].indexOf(s.name());
            if ((i > 0 && name[0].charAt(i - 1) == '_' || i == 0) && s.name().length() > len) {
                index = i;
                len = s.name().length();
                result = s;
            }
        }
        if (index >= 0) {
            name[0] = name[0].substring(0, index) + name[0].substring(index + len);
        }
        return result;
    }
}
