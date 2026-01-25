// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.acoustid.chromaprint.utils.GaussianFilter;
import org.acoustid.chromaprint.utils.Gradient;

public class FingerprintMatcher {
    private static final int ACOUSTID_MAX_BIT_ERROR = 2;
    private static final int ACOUSTID_MAX_ALIGN_OFFSET = 120;
    private static final int ACOUSTID_QUERY_START = 80;
    private static final int ACOUSTID_QUERY_LENGTH = 120;
    private static final int ACOUSTID_QUERY_BITS = 28;
    private static final long ACOUSTID_QUERY_MASK = (((1L << ACOUSTID_QUERY_BITS) - 1) << (32 - ACOUSTID_QUERY_BITS));

    private static final int ALIGN_BITS = 12;
    private static final long ALIGN_MASK = ((1L << ALIGN_BITS) - 1);

    private FingerprinterConfiguration config;
    private List<Long> offsets;
    private List<Integer> histogram;
    private List<AlignmentPair> bestAlignments;
    private List<Segment> segments;
    private double matchThreshold;
    private static final double DEFAULT_MATCH_THRESHOLD = 10.0;
    private Random random;

    public FingerprintMatcher(FingerprinterConfiguration config) {
        this.config = config;
        this.offsets = new ArrayList<>();
        this.histogram = new ArrayList<>();
        this.bestAlignments = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.matchThreshold = DEFAULT_MATCH_THRESHOLD;
        this.random = new Random();
    }

    public void setMatchThreshold(double threshold) {
        this.matchThreshold = threshold;
    }

    public double getMatchThreshold() {
        return matchThreshold;
    }

    public static double getDefaultMatchThreshold() {
        return DEFAULT_MATCH_THRESHOLD;
    }

    public boolean match(List<Long> fp1, List<Long> fp2) {
        long[] fp1Array = new long[fp1.size()];
        long[] fp2Array = new long[fp2.size()];
        for (int i = 0; i < fp1.size(); i++) {
            fp1Array[i] = fp1.get(i);
        }
        for (int i = 0; i < fp2.size(); i++) {
            fp2Array[i] = fp2.get(i);
        }
        return match(fp1Array, fp1Array.length, fp2Array, fp2Array.length);
    }

    public boolean match(long[] fp1Data, int fp1Size, long[] fp2Data, int fp2Size) {
        final int hashShift = 32 - ALIGN_BITS;
        final long hashMask = ((1L << ALIGN_BITS) - 1) << hashShift;
        final long offsetMask = (1L << (32 - ALIGN_BITS - 1)) - 1;
        final long sourceMask = 1L << (32 - ALIGN_BITS - 1);

        if (fp1Size + 1 >= offsetMask) {
            return false; // Fingerprint 1 too long
        }
        if (fp2Size + 1 >= offsetMask) {
            return false; // Fingerprint 2 too long
        }

        offsets = new ArrayList<>(fp1Size + fp2Size);

        for (int i = 0; i < fp1Size; i++) {
            long alignStrip = alignStrip(fp1Data[i]);
            offsets.add((alignStrip << hashShift) | (i & offsetMask));
        }
        for (int i = 0; i < fp2Size; i++) {
            long alignStrip = alignStrip(fp2Data[i]);
            offsets.add((alignStrip << hashShift) | (i & offsetMask) | sourceMask);
        }
        Collections.sort(offsets);

        histogram.clear();
        for (int i = 0; i < fp1Size + fp2Size; i++) {
            histogram.add(0);
        }

        for (int i = 0; i < offsets.size(); i++) {
            long offset = offsets.get(i);
            long hash = offset & hashMask;
            long offset1 = offset & offsetMask;
            long source1 = offset & sourceMask;

            if (source1 != 0) {
                // if we got hash from fp2, it means there is no hash from fp1,
                // because if there was, it would be first
                continue;
            }

            int j = i + 1;
            while (j < offsets.size()) {
                long offset2 = offsets.get(j);
                long hash2 = offset2 & hashMask;
                if (hash != hash2) {
                    break;
                }
                long offset2Val = offset2 & offsetMask;
                long source2 = offset2 & sourceMask;
                if (source2 != 0) {
                    int offsetDiff = (int)(offset1 + fp2Size - offset2Val);
                    if (offsetDiff >= 0 && offsetDiff < histogram.size()) {
                        histogram.set(offsetDiff, histogram.get(offsetDiff) + 1);
                    }
                }
                j++;
            }
        }

        bestAlignments.clear();
        int histogramSize = histogram.size();
        for (int i = 0; i < histogramSize; i++) {
            int count = histogram.get(i);
            if (count > 1) {
                boolean isPeakLeft = (i > 0) ? histogram.get(i - 1) <= count : true;
                boolean isPeakRight = (i < histogramSize - 1) ? histogram.get(i + 1) <= count : true;
                if (isPeakLeft && isPeakRight) {
                    bestAlignments.add(new AlignmentPair(count, i));
                }
            }
        }
        Collections.sort(bestAlignments, Collections.reverseOrder());

        segments.clear();

        for (AlignmentPair item : bestAlignments) {
            int offsetDiff = (int)(item.offset - fp2Size);

            int offset1 = offsetDiff > 0 ? offsetDiff : 0;
            int offset2 = offsetDiff < 0 ? -offsetDiff : 0;

            int size = Math.min(fp1Size - offset1, fp2Size - offset2);
            if (size <= 0) {
                continue;
            }

            List<Float> bitCounts = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                long hammingDist = Utils.hammingDistance(fp1Data[offset1 + i], fp2Data[offset2 + i]);
                bitCounts.add((float)hammingDist + random.nextFloat() * 0.001f);
            }

            List<Float> origBitCounts = new ArrayList<>(bitCounts);
            List<Float> smoothedBitCounts = new ArrayList<>();
            GaussianFilter.gaussianFilter(bitCounts, smoothedBitCounts, 8.0, 3);

            List<Float> gradient = new ArrayList<>(size);
            Gradient.gradient(smoothedBitCounts, gradient);

            for (int i = 0; i < size; i++) {
                gradient.set(i, Math.abs(gradient.get(i)));
            }

            List<Integer> gradientPeaks = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                float gi = gradient.get(i);
                if (i > 0 && i < size - 1 && gi > 0.15 && gi >= gradient.get(i - 1) && gi >= gradient.get(i + 1)) {
                    if (gradientPeaks.isEmpty() || gradientPeaks.get(gradientPeaks.size() - 1) + 1 < i) {
                        gradientPeaks.add(i);
                    }
                }
            }
            gradientPeaks.add(size);

            int begin = 0;
            for (int end : gradientPeaks) {
                int duration = end - begin;
                if (duration > 0) {
                    float sum = 0.0f;
                    for (int i = begin; i < end; i++) {
                        sum += origBitCounts.get(i);
                    }
                    double score = sum / duration;

                    if (score < matchThreshold) {
                        boolean added = false;
                        if (!segments.isEmpty()) {
                            Segment s1 = segments.get(segments.size() - 1);
                            if (Math.abs(s1.getScore() - score) < 0.7) {
                                Segment newSegment = new Segment(offset1 + begin, offset2 + begin, duration, score);
                                segments.set(segments.size() - 1, s1.merged(newSegment));
                                added = true;
                            }
                        }
                        if (!added) {
                            segments.add(new Segment(offset1 + begin, offset2 + begin, duration, score));
                        }
                    }
                }
                begin = end;
            }

            // TODO try to merge segments from multiple offsets
            break;
        }

        return true;
    }

    public double getHashTime(int i) {
        return config.getItemDurationInSeconds() * i;
    }

    public double getHashDuration(int i) {
        return getHashTime(i) + config.getDelayInSeconds();
    }

    public List<Segment> getSegments() {
        return new ArrayList<>(segments);
    }

    private static long alignStrip(long x) {
        return (x & 0xFFFFFFFFL) >>> (32 - ALIGN_BITS);
    }

    private static class AlignmentPair implements Comparable<AlignmentPair> {
        int count;
        long offset;

        AlignmentPair(int count, long offset) {
            this.count = count;
            this.offset = offset;
        }

        @Override
        public int compareTo(AlignmentPair other) {
            if (count != other.count) {
                return Integer.compare(count, other.count);
            }
            return Long.compare(offset, other.offset);
        }
    }
}
