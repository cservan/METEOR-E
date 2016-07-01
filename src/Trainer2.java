import org.getalp.lexsema.meteor.tuning.ParameterSet;
import org.getalp.lexsema.wsd.method.cuckoo.CuckooSearch;

public class Trainer2 
{
	public static void main(String[] args)
	{
        int iterations = 1000000;
        double minLevyLocation = 1;
        double maxLevyLocation = 5;
        double minLevyScale = 0.5;
        double maxLevyScale = 1.5;
		CuckooSearch cuckoo = new CuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale);
		
		ParameterSet init = new ParameterSet(new int[]{20, 10, 10, 10, 10, 10, 10, 10, 10, 10});
		
		ParameterSet best = (ParameterSet) cuckoo.start(init);
		
		System.out.println("Best " + best.toString());
	}
}
