package org.getalp.lexsema.meteor.tuning;

import java.util.Arrays;
import java.util.Random;

import org.getalp.lexsema.wsd.method.cuckoo.CuckooNest;

public class ParameterSet implements CuckooNest
{
    private static final Random random = new Random();

	private int[] parameters;
	
	public ParameterSet(int[] parameters)
	{
		this.parameters = parameters.clone();
	}
	
	@Override
	public void move(double distance) 
	{
		int distanceRemaining = (int) distance;
		while (distanceRemaining > 0)
		{
			int index = random.nextInt(parameters.length);
			int incr = 0;
			if (parameters[index] == 20) incr = -1;
			else if (parameters[index] == 0) incr = 1;
			else incr = (random.nextInt(2) == 0) ? 1 : -1;
			parameters[index] += incr;
			distanceRemaining -= 1;
		}
	}
	
	public int[] getParameters()
	{
		return parameters.clone();
	}

	@Override
	public CuckooNest clone() 
	{
		return new ParameterSet(parameters);
	}
	
	@Override
	public double score()
	{
		return ParameterSetScorer.score(this);
	}
	
	@Override
	public String toString()
	{
		return "parameter set : " + Arrays.toString(parameters);
	}
}
