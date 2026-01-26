// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.List;

import org.cp.elements.lang.ObjectUtils;

import lombok.Getter;

/**
 * Component used to compute an {@literal fingerprint} from audio.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see AudioConsumer
 * @see FingerprintCalculator
 * @see FingerprinterConfiguration
 */
@Getter
public class Fingerprinter implements AudioConsumer {

    private static final int MIN_FREQ = 28;
    private static final int MAX_FREQ = 3520;

    private AudioProcessor audioProcessor;

    private final Chroma chroma;
    private final ChromaFilter chromaFilter;
    private final ChromaNormalizer chromaNormalizer;

    private final FingerprintCalculator fingerprintCalculator;
    private final FingerprinterConfiguration config;

    private final FFT fft;

    private final SilenceRemover silenceRemover;

    public Fingerprinter(FingerprinterConfiguration config) {

        this.config = ObjectUtils.returnValueOrDefaultIfNull(config, FingerprinterConfigurationTest1::new);
        this.fingerprintCalculator = new FingerprintCalculator(config.getClassifiers(), config.getNumClassifiers());
        this.chromaNormalizer = new ChromaNormalizer(this.fingerprintCalculator);
        this.chromaFilter = new ChromaFilter(config.getFilterCoefficients(), config.getNumFilterCoefficients(), this.chromaNormalizer);
        this.chroma = new Chroma(MIN_FREQ, MAX_FREQ, config.getFrameSize(), config.getSampleRate(), this.chromaFilter);
        this.fft = new FFT(config.getFrameSize(), config.getFrameOverlap(), this.chroma);

        if (config.isRemoveSilence()) {
            this.silenceRemover = new SilenceRemover(this.fft);
            this.silenceRemover.setThreshold(config.getSilenceThreshold());
            this.audioProcessor = new AudioProcessor(config.getSampleRate(), this.silenceRemover);
        }
        else {
            this.audioProcessor = new AudioProcessor(config.getSampleRate(), this.fft);
            this.silenceRemover = null;
        }
    }

    public List<Long> getFingerprint() {
        return getFingerprintCalculator().getFingerprint();
    }

    public boolean setOption(String name, int value) {

        if ("silence_threshold".equals(name)) {
            SilenceRemover silenceRemover = getSilenceRemover();
            if (silenceRemover != null) {
                silenceRemover.setThreshold(value);
                return true;
            }
        }

        return false;
    }

    public boolean start(int sampleRate, int numChannels) {

        if (!getAudioProcessor().reset(sampleRate, numChannels)) {
            return false;
        }

        getFft().reset();
        getChroma().reset();
        getChromaFilter().reset();
        getChromaNormalizer().reset();
        getFingerprintCalculator().reset();

        return true;
    }

    @Override
    public void consume(short[] samples, int length) {
        getAudioProcessor().consume(samples, length);
    }

    public void finish() {
        getAudioProcessor().flush();
    }

    public void clearFingerprint() {
        getFingerprintCalculator().clearFingerprint();
    }
}
