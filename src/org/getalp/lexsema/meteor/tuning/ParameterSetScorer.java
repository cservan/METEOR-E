package org.getalp.lexsema.meteor.tuning;


import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import edu.cmu.meteor.scorer.MeteorStats;

public class ParameterSetScorer 
{
	private static DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	private Map<int[], Double> cache = new HashMap<>();
	
	private List<String> scoresCacheHyp = new ArrayList<>();
	
	private List<String> scoresCacheRef = new ArrayList<>();
	
	private List<String> scoresCacheLang = new ArrayList<>();
	
	private List<String> scoresCacheSet = new ArrayList<>();
	
	private List<String> scoresCacheSys = new ArrayList<>();
	
	private List<String> scoresCacheLine = new ArrayList<>();
	
	public ParameterSetScorer(String cacheFilePath)
	{
	    try 
	    {
	    	BufferedReader br = new BufferedReader(new FileReader(cacheFilePath));
	    	String line;
			while ((line = br.readLine()) != null) 
			{
				String[] tab = line.split(" ");
				scoresCacheHyp.add(tab[0]);
				scoresCacheRef.add(tab[1]);
				scoresCacheLang.add(tab[2]);
				scoresCacheSet.add(tab[3]);
				scoresCacheSys.add(tab[4]);
				String ScoresCacheLineElement = tab[5];
				for (int i = 6 ; i < tab.length ; i++)
				{
					ScoresCacheLineElement += " " + tab[i];
				}
				scoresCacheLine.add(ScoresCacheLineElement);
			}
			br.close();
		} 
	    catch (IOException e) 
	    {
			throw new RuntimeException(e);
	    }
	}
	
	public double score(ParameterSet params)
	{
		int[] parameters = params.getParameters();
		synchronized (cache)
		{
			for (int[] key : cache.keySet())
			{
				if (key.length != parameters.length) continue;
				boolean same = true;
				for (int i = 0 ; i < key.length ; i++)
				{
					if (key[i] != parameters[i])
					{
						same = false;
						break;
					}
				}
				if (same) return cache.get(key);
			}
		}
		
		double[] weights = new double[parameters.length - 4];
		for (int i = 0 ; i < parameters.length - 4 ; i++)
		{
			weights[i] = 0.05 * parameters[i];
		}
		
		double[] realParameters = new double[4];
		for (int i = 0 ; i < realParameters.length ; i++)
		{
			realParameters[i] = 0.05 * parameters[i + (parameters.length - 4)];
		}
		
		String weightsStr = "";
		for (int i = 0 ; i < weights.length ; i++) 
		{
			if (i != 0) weightsStr += " ";
			weightsStr += decimalFormat.format(weights[i]);
		}

		String realParametersStr = "";
		for (int i = 0 ; i < realParameters.length ; i++) 
		{
			if (i != 0) realParametersStr += " ";
			realParametersStr += decimalFormat.format(realParameters[i]);
		}
		 
		try 
		{
			File tmpFile = File.createTempFile("tmp", "", new File("."));
			PrintWriter tmpFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(tmpFile.getAbsolutePath(), true)));
			for (int i = 0 ; i < scoresCacheLine.size() ; i++)
			{
				// String hyp = scoresCacheHyp.get(i);
				// String ref = scoresCacheRef.get(i);
				String lang = scoresCacheLang.get(i);
				String set = scoresCacheSet.get(i);
				String sys = scoresCacheSys.get(i);
				String line = scoresCacheLine.get(i);
				double score = computeMetrics(line, weights, realParameters);
				tmpFileWriter.println("tuning" + "\t" + lang + "\t" + set + "\t" + sys + "\t" + score);
			}
			tmpFileWriter.close();
			String[] commands = {"./from-cache2.sh", tmpFile.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(commands);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String scoreStr = "";
		    String line = "";			
		    while ((line = reader.readLine())!= null) 
		    {
		    	scoreStr += line;
		    }
		    scoreStr = scoreStr.trim();
		    reader.close();
		    synchronized (System.out)
		    {
		    	System.out.println("Scoring weights=[" + weightsStr + "] and parameters=[" + realParametersStr + "] : " + scoreStr);
		    }
		    double score = Double.valueOf(scoreStr);
		    synchronized(cache)
		    {
		    	cache.put(parameters, score);
		    }
		    tmpFile.delete();
		    return score;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	

	
	public static double computeMetrics(String cacheLine, double[] weights, double[] parameters)
	{
		MeteorStats aggStats = new MeteorStats(cacheLine);
		computeMetrics(aggStats, weights, parameters[0], parameters[1], parameters[2], parameters[3]);
		//Meteor.printVerboseStats(aggStats, config);
		return aggStats.score;
	}
	
	public static void computeMetrics(MeteorStats stats, double[] moduleWeights, double alpha, double beta, double gamma, double delta) 
	{
		stats.testWeightedMatches = 0;
		stats.referenceWeightedMatches = 0;

		stats.testWeightedLength = (delta * (stats.testLength - stats.testFunctionWords))
				+ ((1.0 - delta) * (stats.testFunctionWords));
		stats.referenceWeightedLength = (delta * (stats.referenceLength - stats.referenceFunctionWords))
				+ ((1.0 - delta) * (stats.referenceFunctionWords));

		// Apply module weights and delta to test and reference matches
		// (Content)
		for (int i = 0; i < moduleWeights.length; i++)
			stats.testWeightedMatches += stats.testStageMatchesContent.get(i)
					* moduleWeights[i] * delta;
		for (int i = 0; i < moduleWeights.length; i++)
			stats.referenceWeightedMatches += stats.referenceStageMatchesContent
					.get(i) * moduleWeights[i] * delta;

		// Apply module weights and delta to test and reference matches
		// (Function)
		for (int i = 0; i < moduleWeights.length; i++)
			stats.testWeightedMatches += stats.testStageMatchesFunction.get(i)
					* moduleWeights[i] * (1.0 - delta);
		for (int i = 0; i < moduleWeights.length; i++)
			stats.referenceWeightedMatches += stats.referenceStageMatchesFunction
					.get(i) * moduleWeights[i] * (1.0 - delta);

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
