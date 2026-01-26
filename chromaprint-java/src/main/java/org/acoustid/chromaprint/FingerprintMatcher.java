// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.acoustid.chromaprint.utils.GaussianFilter;
import org.acoustid.chromaprint.utils.Gradient;

import lombok.Getter;

/**
 * Component used to match {@literal digital fingerprints} for similarity
 * based on a pre-defined or configured threshold.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see GaussianFilter
 * @see Gradient
 */
@Getter
public class FingerprintMatcher {

    private static final double DEFAULT_MATCH_THRESHOLD = 10.0;

    private static final int ACOUSTID_MAX_ALIGN_OFFSET = 120;
    private static final int ACOUSTID_MAX_BIT_ERROR = 2;
    private static final int ACOUSTID_QUERY_START = 80;
    private static final int ACOUSTID_QUERY_BITS = 28;
    private static final int ACOUSTID_QUERY_LENGTH = 120;
    private static final int ALIGN_BITS = 12;

    private static final long ACOUSTID_QUERY_MASK = (((1L << ACOUSTID_QUERY_BITS) - 1) << (32 - ACOUSTID_QUERY_BITS));
    private static final long ALIGN_MASK = ((1L << ALIGN_BITS) - 1);

    private double matchThreshold;

    private FingerprinterConfiguration config;

    private final List<AlignmentPair> bestAlignments;
    private final List<Integer> histogram;
    private List<Long> offsets;
    private final List<Segment> segments;

    private final Random random;

    public FingerprintMatcher(FingerprinterConfiguration config) {
        this.config = config;
        this.bestAlignments = new ArrayList<>();
        this.histogram = new ArrayList<>();
        this.offsets = new ArrayList<>();
        this.random = new Random();
        this.segments = new ArrayList<>();
        this.matchThreshold = DEFAULT_MATCH_THRESHOLD;
    }

    public double getHashDuration(int multiplier) {
        return getHashTime(multiplier) + getConfig().getDelayInSeconds();
    }

    public double getHashTime(int multiplier) {
        return getConfig().getItemDurationInSeconds() * multiplier;
    }

    public List<Segment> getSegments() {
        return new ArrayList<>(this.segments);
    }

    public boolean match(List<Long> fp1, List<Long> fp2) {

        long[] fp1Array = new long[fp1.size()];
        long[] fp2Array = new long[fp2.size()];

        for (int index = 0; index < fp1.size(); index++) {
            fp1Array[index] = fp1.get(index);
        }

        for (int index = 0; index < fp2.size(); index++) {
            fp2Array[index] = fp2.get(index);
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

        this.offsets = new ArrayList<>(fp1Size + fp2Size);

        for (int index = 0; index < fp1Size; index++) {
            long alignStrip = alignStrip(fp1Data[index]);
            this.offsets.add((alignStrip << hashShift) | (index & offsetMask));
        }

        for (int index = 0; index < fp2Size; index++) {
            long alignStrip = alignStrip(fp2Data[index]);
            this.offsets.add((alignStrip << hashShift) | (index & offsetMask) | sourceMask);
        }

        Collections.sort(this.offsets);
        this.histogram.clear();

        for (int count = 0; count < fp1Size + fp2Size; count++) {
            this.histogram.add(0);
        }

        for (int index = 0; index < this.offsets.size(); index++) {
            long offset = this.offsets.get(index);
            long hash = offset & hashMask;
            long offset1 = offset & offsetMask;
            long source1 = offset & sourceMask;

            if (source1 != 0) {
                // if we got hash from fp2, it means there is no hash from fp1,
                // because if there was, it would be first
                continue;
            }

            int j = index + 1;

            while (j < this.offsets.size()) {
                long offset2 = this.offsets.get(j);
                long hash2 = offset2 & hashMask;
                if (hash != hash2) {
                    break;
                }
                long offset2Val = offset2 & offsetMask;
                long source2 = offset2 & sourceMask;
                if (source2 != 0) {
                    int offsetDiff = (int)(offset1 + fp2Size - offset2Val);
                    if (offsetDiff >= 0 && offsetDiff < this.histogram.size()) {
                        this.histogram.set(offsetDiff, this.histogram.get(offsetDiff) + 1);
                    }
                }
                j++;
            }
        }

        this.bestAlignments.clear();

        for (int index = 0, histogramSize = this.histogram.size(); index < histogramSize; index++) {
            int count = this.histogram.get(index);
            if (count > 1) {
                boolean isPeakLeft = index < 1 || histogram.get(index - 1) <= count;
                boolean isPeakRight = index >= histogramSize - 1 || histogram.get(index + 1) <= count;
                if (isPeakLeft && isPeakRight) {
                    this.bestAlignments.add(new AlignmentPair(count, index));
                }
            }
        }

        this.bestAlignments.sort(Collections.reverseOrder());
        this.segments.clear();

        for (AlignmentPair item : this.bestAlignments) {
            int offsetDiff = (int) (item.offset - fp2Size);
            int offset1 = Math.max(offsetDiff, 0);
            int offset2 = offsetDiff < 0 ? -offsetDiff : 0;
            int size = Math.min(fp1Size - offset1, fp2Size - offset2);

            if (size <= 0) {
                continue;
            }

            List<Float> bitCounts = new ArrayList<>(size);

            for (int index = 0; index < size; index++) {
                long hammingDist = Utils.hammingDistance(fp1Data[offset1 + index], fp2Data[offset2 + index]);
                bitCounts.add((float) hammingDist + getRandom().nextFloat() * 0.001f);
            }

            List<Float> origBitCounts = new ArrayList<>(bitCounts);
            List<Float> smoothedBitCounts = new ArrayList<>();
            List<Float> gradient = new ArrayList<>(size);

            GaussianFilter.gaussianFilter(bitCounts, smoothedBitCounts, 8.0, 3);
            Gradient.gradient(smoothedBitCounts, gradient);

            for (int index = 0; index < size; index++) {
                gradient.set(index, Math.abs(gradient.get(index)));
            }

            List<Integer> gradientPeaks = new ArrayList<>();

            for (int index = 0; index < size; index++) {
                float gi = gradient.get(index);
                if (index > 0 && index < size - 1 && gi > 0.15 && gi >= gradient.get(index - 1) && gi >= gradient.get(index + 1)) {
                    if (gradientPeaks.isEmpty() || gradientPeaks.get(gradientPeaks.size() - 1) + 1 < index) {
                        gradientPeaks.add(index);
                    }
                }
            }

            gradientPeaks.add(size);

            int begin = 0;

            for (int end : gradientPeaks) {
                int duration = end - begin;
                if (duration > 0) {
                    float sum = 0.0f;

                    for (int index = begin; index < end; index++) {
                        sum += origBitCounts.get(index);
                    }

                    double score = sum / duration;

                    if (score < this.matchThreshold) {
                        boolean added = false;
                        if (!this.segments.isEmpty()) {
                            Segment s1 = this.segments.get(this.segments.size() - 1);
                            if (Math.abs(s1.getScore() - score) < 0.7) {
                                Segment newSegment = new Segment(offset1 + begin, offset2 + begin, duration, score);
                                this.segments.set(this.segments.size() - 1, s1.merged(newSegment));
                                added = true;
                            }
                        }
                        if (!added) {
                            this.segments.add(new Segment(offset1 + begin, offset2 + begin, duration, score));
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

    private static long alignStrip(long x) {
        return (x & 0xFFFFFFFFL) >>> (32 - ALIGN_BITS);
    }

    private record AlignmentPair(int count, long offset) implements Comparable<AlignmentPair> {

        @Override
            public int compareTo(AlignmentPair other) {

                if (count() != other.count()) {
                    return Integer.compare(count(), other.count());
                }

                return Long.compare(offset(), other.offset());
            }
        }
}
