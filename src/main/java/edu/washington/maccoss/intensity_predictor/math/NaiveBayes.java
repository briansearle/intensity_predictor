package edu.washington.maccoss.intensity_predictor.math;

import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

public class NaiveBayes {
	private final double positivePrior;
	private final double negativePrior;
	private final NormalDistribution[] positive;
	private final NormalDistribution[] negative;

	NaiveBayes(double positivePrior, double negativePrior, NormalDistribution[] positive, NormalDistribution[] negative) {
		this.positivePrior=positivePrior;
		this.negativePrior=negativePrior;
		this.positive=positive;
		this.negative=negative;
	}

	public double getLogLikelihood(double[] data) {
		int featureCount=Math.min(positive.length, data.length);

		double posLogProb=Math.log10(positivePrior);
		double negLogProb=Math.log10(negativePrior);
		for (int i=0; i<featureCount; i++) {
			double posFeatureProb=positive[i].density(data[i]);
			double negFeatureProb=negative[i].density(data[i]);

			// ignore sitatuations where both probabilities are 0
			if (posFeatureProb==0&&negFeatureProb==0) continue;

			posLogProb+=Math.log10(posFeatureProb);
			negLogProb+=Math.log10(negFeatureProb);
		}
		return posLogProb-negLogProb;
	}

	public static NaiveBayes buildModel(double[][] positiveData, double[][] negativeData) {
		double positivePrior=positiveData.length/(float)(positiveData.length+negativeData.length);
		double negativePrior=negativeData.length/(float)(positiveData.length+negativeData.length);

		int featureCount=Math.min(positiveData[0].length, negativeData[0].length);
		NormalDistribution[] positive=new NormalDistribution[featureCount];
		NormalDistribution[] negative=new NormalDistribution[featureCount];

		Mean meanCalc=new Mean();
		StandardDeviation stdevCalc=new StandardDeviation();
		for (int i=0; i<featureCount; i++) {
			double[] posCol=getColumn(positiveData, i);
			double[] negCol=getColumn(negativeData, i);

			positive[i]=new NormalDistributionImpl(meanCalc.evaluate(posCol), stdevCalc.evaluate(posCol));
			negative[i]=new NormalDistributionImpl(meanCalc.evaluate(negCol), stdevCalc.evaluate(negCol));
		}

		return new NaiveBayes(positivePrior, negativePrior, positive, negative);
	}

	private static double[] getColumn(double[][] data, int col) {
		double[] v=new double[data.length];
		for (int i=0; i<v.length; i++) {
			v[i]=data[i][col];
		}
		return v;
	}
}
