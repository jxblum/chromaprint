// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
public class FingerprinterConfigurationTest3 extends FingerprinterConfiguration {

	// Same classifiers as Test2
	private static final Classifier[] CLASSIFIERS = {
		new Classifier(new Filter(0, 15, 3, 4), new Quantizer(1.98215, 2.35817, 2.63523)),
		new Classifier(new Filter(4, 15, 6, 4), new Quantizer(-1.03809, -0.651211, -0.282167)),
		new Classifier(new Filter(1, 16, 4, 0), new Quantizer(-0.298702, 0.119262, 0.558497)),
		new Classifier(new Filter(3, 12, 2, 8), new Quantizer(-0.105439, 0.0153946, 0.135898)),
		new Classifier(new Filter(3, 8, 4, 4), new Quantizer(-0.142891, 0.0258736, 0.200632)),
		new Classifier(new Filter(4, 5, 3, 0), new Quantizer(-0.826319, -0.590612, -0.368214)),
		new Classifier(new Filter(1, 9, 2, 2), new Quantizer(-0.557409, -0.233035, 0.0534525)),
		new Classifier(new Filter(2, 4, 3, 7), new Quantizer(-0.0646826, 0.00620476, 0.0784847)),
		new Classifier(new Filter(2, 16, 2, 6), new Quantizer(-0.192387, -0.029699, 0.215855)),
		new Classifier(new Filter(2, 2, 3, 1), new Quantizer(-0.0397818, -0.00568076, 0.0292026)),
		new Classifier(new Filter(5, 15, 1, 10), new Quantizer(-0.53823, -0.369934, -0.190235)),
		new Classifier(new Filter(3, 10, 2, 6), new Quantizer(-0.124877, 0.0296483, 0.139239)),
		new Classifier(new Filter(2, 14, 1, 1), new Quantizer(-0.101475, 0.0225617, 0.231971)),
		new Classifier(new Filter(3, 4, 6, 5), new Quantizer(-0.0799915, -0.00729616, 0.063262)),
		new Classifier(new Filter(1, 12, 2, 9), new Quantizer(-0.272556, 0.019424, 0.302559)),
		new Classifier(new Filter(3, 14, 2, 4), new Quantizer(-0.164292, -0.0321188, 0.0846339))
	};

	public FingerprinterConfigurationTest3() {
		setClassifiers(CLASSIFIERS, CLASSIFIERS.length);
		setFilterCoefficients(CHROMA_FILTER_COEFFICIENTS, CHROMA_FILTER_SIZE);
		setInterpolate(true);
		setFrameSize(DEFAULT_FRAME_SIZE);
	}
}
