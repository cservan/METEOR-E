import java.util.List;
import java.util.Properties;
import edu.cmu.meteor.scorer.MeteorConfiguration;
import edu.cmu.meteor.scorer.MeteorStats;

public class Trainer3 
{
	static double alpha;
	
	static double beta;
	
	static double gamma;
	
	static double delta;
	
	static List<Double> moduleWeights;
	
	public static void main(String[] args)
	{
		Properties props = Meteor.createPropertiesFromArgs(args, 0);
		MeteorConfiguration config = new MeteorConfiguration(props);
		moduleWeights = config.getModuleWeights();
		List<Double> parameters = config.getParameters();
		alpha = parameters.get(0);
		beta = parameters.get(1);
		gamma = parameters.get(2);
		delta = parameters.get(3);
		MeteorStats aggStats = new MeteorStats(props.getProperty("cache"));
		computeMetrics(aggStats);
		//Meteor.printVerboseStats(aggStats, config);
		System.out.println(aggStats.score);
		return;
	}
	
	public static void computeMetrics(MeteorStats stats) 
	{
		stats.testWeightedMatches = 0;
		stats.referenceWeightedMatches = 0;

		stats.testWeightedLength = (delta * (stats.testLength - stats.testFunctionWords))
				+ ((1.0 - delta) * (stats.testFunctionWords));
		stats.referenceWeightedLength = (delta * (stats.referenceLength - stats.referenceFunctionWords))
				+ ((1.0 - delta) * (stats.referenceFunctionWords));

		// Apply module weights and delta to test and reference matches
		// (Content)
		for (int i = 0; i < moduleWeights.size(); i++)
			stats.testWeightedMatches += stats.testStageMatchesContent.get(i)
					* moduleWeights.get(i) * delta;
		for (int i = 0; i < moduleWeights.size(); i++)
			stats.referenceWeightedMatches += stats.referenceStageMatchesContent
					.get(i) * moduleWeights.get(i) * delta;

		// Apply module weights and delta to test and reference matches
		// (Function)
		for (int i = 0; i < moduleWeights.size(); i++)
			stats.testWeightedMatches += stats.testStageMatchesFunction.get(i)
					* moduleWeights.get(i) * (1.0 - delta);
		for (int i = 0; i < moduleWeights.size(); i++)
			stats.referenceWeightedMatches += stats.referenceStageMatchesFunction
					.get(i) * moduleWeights.get(i) * (1.0 - delta);

		// Precision = test matches / test length
		stats.precision = stats.testWeightedMatches / stats.testWeightedLength;
		// Recall = ref matches / ref length
		stats.recall = stats.referenceWeightedMatches
				/ stats.referenceWeightedLength;
		// F1 = 2pr / (p + r) [not part of final score]
		stats.f1 = (2 * stats.precision * stats.recall)
				/ (stats.precision + stats.recall);
		// Fmean = 1 / alpha-weighted average of p and r
		stats.fMean = 1.0 / (((1.0 - alpha) / stats.precision) + (alpha / stats.recall));
		// Fragmentation
		double frag;
		// Case if test = ref
		if (stats.testTotalMatches == stats.testLength
				&& stats.referenceTotalMatches == stats.referenceLength
				&& stats.chunks == 1)
			frag = 0;
		else
			frag = ((double) stats.chunks)
					/ (((double) (stats.testWordMatches + stats.referenceWordMatches)) / 2);
		// Fragmentation penalty
		stats.fragPenalty = gamma * Math.pow(frag, beta);
		// Score
		double score = stats.fMean * (1.0 - stats.fragPenalty);

		// Catch division by zero
		if (Double.isNaN(score))
			stats.score = 0;
		else
			// score >= 0.0
			stats.score = Math.max(score, 0.0);
	}
}
