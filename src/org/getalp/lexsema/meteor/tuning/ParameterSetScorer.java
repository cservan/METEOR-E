package org.getalp.lexsema.meteor.tuning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ParameterSetScorer 
{
	static Map<int[], Double> cache = new HashMap<>();
	
	static DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	static double score(ParameterSet params)
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
			String[] commands = {"./from-cache.sh", weightsStr, realParametersStr, "cache.txt"};
			Process p = Runtime.getRuntime().exec(commands);
			p.waitFor();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String scoreStr = "";
		    String line = "";			
		    while ((line = reader.readLine())!= null) 
		    {
		    	scoreStr += line;
		    }
		    scoreStr = scoreStr.trim();
		    synchronized (System.out)
		    {
		    	System.out.println("Scoring weights=[" + weightsStr + "] and parameters=[" + realParametersStr + "] : " + scoreStr);
		    }
		    double score = Double.valueOf(scoreStr);
		    synchronized(cache)
		    {
		    	cache.put(parameters, score);
		    }
		    return score;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}
}
