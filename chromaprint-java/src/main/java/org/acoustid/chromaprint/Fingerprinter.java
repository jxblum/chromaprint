// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.List;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see AudioConsumer
 */
public class Fingerprinter implements AudioConsumer {

    private static final int MIN_FREQ = 28;
    private static final int MAX_FREQ = 3520;

    private AudioProcessor audioProcessor;

    private Chroma chroma;
    private ChromaFilter chromaFilter;
    private ChromaNormalizer chromaNormalizer;


    private FingerprintCalculator fingerprintCalculator;
    private FingerprinterConfiguration config;

    private FFT fft;

    private SilenceRemover silenceRemover;

    public Fingerprinter(FingerprinterConfiguration config) {
        if (config == null) {
            config = new FingerprinterConfigurationTest1();
        }
        this.config = config;
        this.fingerprintCalculator = new FingerprintCalculator(
            config.getClassifiers(), config.getNumClassifiers()
        );
        this.chromaNormalizer = new ChromaNormalizer(fingerprintCalculator);
        this.chromaFilter = new ChromaFilter(
            config.getFilterCoefficients(),
            config.getNumFilterCoefficients(),
            chromaNormalizer
        );
        this.chroma = new Chroma(
            MIN_FREQ, MAX_FREQ,
            config.getFrameSize(),
            config.getSampleRate(),
            chromaFilter
        );
        this.fft = new FFT(config.getFrameSize(), config.getFrameOverlap(), chroma);

        if (config.isRemoveSilence()) {
            this.silenceRemover = new SilenceRemover(fft);
            silenceRemover.setThreshold(config.getSilenceThreshold());
            this.audioProcessor = new AudioProcessor(config.getSampleRate(), silenceRemover);
        } else {
            this.silenceRemover = null;
            this.audioProcessor = new AudioProcessor(config.getSampleRate(), fft);
        }
    }

    public boolean setOption(String name, int value) {
        if ("silence_threshold".equals(name)) {
            if (silenceRemover != null) {
                silenceRemover.setThreshold(value);
                return true;
            }
        }
        return false;
    }

    public boolean start(int sampleRate, int numChannels) {
        if (!audioProcessor.reset(sampleRate, numChannels)) {
            return false;
        }
        fft.reset();
        chroma.reset();
        chromaFilter.reset();
        chromaNormalizer.reset();
        fingerprintCalculator.reset();
        return true;
    }

    @Override
    public void consume(short[] samples, int length) {
        audioProcessor.consume(samples, length);
    }

    public void finish() {
        audioProcessor.flush();
    }

    public List<Long> getFingerprint() {
        return fingerprintCalculator.getFingerprint();
    }

    public void clearFingerprint() {
        fingerprintCalculator.clearFingerprint();
    }

    public FingerprinterConfiguration getConfig() {
        return config;
    }
}
