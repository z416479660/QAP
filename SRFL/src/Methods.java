
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * 
 * @author yiwen zhong
 *
 */
public class Methods {

	/**
	 * Zhan, S. H., Lin, J., Zhang, Z. J., & Zhong, Y. W. (2016). 
	 * List-based simulated annealing algorithm for traveling salesman problem. Computational intelligence and neuroscience, 2016, 8.
	 * 
	 * @param solution
	 * @return
	 */
	public static Solution listBasedSA(Solution solution) {
		Solution current = new Solution(solution);
		final int MAX_G = Simulations.MAX_GENERATION; //MAXIMUM GENERATION
		int facilityNumber = Problems.getProblem().getFacilityNumber();
		final int SCHEDULE_LENGTH = facilityNumber*Simulations.MARKOV_CHAIN_FACTOR;
		
		PriorityQueue<Double> tempList= new PriorityQueue<Double>();
		while (tempList.size() < Simulations.listLength) {
			Operator o = current.neighborOperator();
			tempList.offer(-Math.abs(o.gain));
			if (o.gain < 0) {
				current.performChange(o);;
			}
		}
		current.evalCut();
		Solution best = new Solution(current);

		double[] temperatures = new double[MAX_G];
		double[] values = new double[MAX_G];
		double[] bestvalues = new double[MAX_G];
 		for (int q = 0; q < MAX_G; q++) {
			double t = -tempList.peek();
			double totalTemp = 0;
			int counter = 0;
			temperatures[q] = t;
			values[q] = 0;
			for (int k = 0; k < SCHEDULE_LENGTH; k++) {
				Operator o = current.neighborOperator();
				double p = Methods.rand.nextDouble();
				double d = o.gain;
				if ( d < 0 || p < 1.0/Math.exp(Math.abs(d)/t)) {
					//accept
					current.performChange(o);;
					if (current.getValue() < best.getValue()) {
						best.update(current);
						best.setLastImproving(q);
					} 
					if ( d > 0) {//in case worst solution is accepted
						totalTemp += Math.abs( d ) / Math.log(1.0/p);
						counter++;
					}
				}
				values[q] += current.getValue();
			}
			values[q] /= SCHEDULE_LENGTH;
			bestvalues[q] = best.getValue();
		    //update temperature list
			if ( counter != 0 && Simulations.listLength != 0) {
				tempList.remove();
				tempList.offer( - totalTemp/counter);
				//System.out.println("offered temperature:" + totalTemp/counter);
			} 

		}
        //System.out.println(best);
		if (Simulations.isSavingProcessData()) Methods.saveConvergenceData(temperatures, values, bestvalues, Problems.getProblem().getBestValue());
		return best;
	}
	
	
	public static Solution basicSA(Solution solution) {
		Solution current = new Solution(solution);
		int facilityNumber = Problems.getProblem().getFacilityNumber();
		final int SCHEDULE_LENGTH = facilityNumber*Simulations.MARKOV_CHAIN_FACTOR;
		
        //Create initial temperature
		double maxT = 0;
		for (int i = 0; i < 5000; i++) {
			Operator o = current.neighborOperator(ENeighborType.SWAP);
			if ( Math.abs(o.gain) > maxT) {
				maxT = Math.abs(o.gain);
			}
			if (o.gain < 0) {
				current.performChange(o);;
			}
		}
		current.evalCut();
		Solution best = new Solution(current);
		
        double minT = 0.0001;
        double alpha = 0.95;
        final int MAX_G = (int)((Math.log(minT) - Math.log(maxT))/Math.log(alpha)); 
 		double[] temperatures = new double[MAX_G];
		double[] values = new double[MAX_G];
		double[] bestvalues = new double[MAX_G];
	    double t = maxT;
 		for (int q = 0; q < MAX_G; q++) {
			temperatures[q] = t;
			values[q] = 0;
			for (int k = 0; k < SCHEDULE_LENGTH; k++) {
				Operator o; // 
				o = current.neighborOperator();
				double p = Methods.rand.nextDouble();
				if ( o.gain < 0 || p < 1.0/Math.exp(Math.abs(o.gain)/t)) {
					//accept
					current.performChange(o);
					if (current.getValue() < best.getValue()) {
						best.update(current);
						best.setLastImproving(q);
					} 
				}
				values[q] += current.getValue();
			}
			values[q] /= SCHEDULE_LENGTH;
			bestvalues[q] = best.getValue();
			
		    //update temperature 
			t *= alpha;
		}
 		//System.out.println(best.getLastImproving());
		if (Simulations.isSavingProcessData()) Methods.saveConvergenceData(temperatures, values, bestvalues, Problems.getProblem().getBestValue());
		return best;
	}

	
	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs, double bestValue) {
		try {
			String f = Problems.fileName;
			File file = new File(f);
			f = (new File("")).getAbsolutePath() + "\\results\\Convergence\\" + file.getName();
			f += " " + Simulations.getParaSetting() + " for SRFL results.csv";
			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx] + "," + (vs[idx] - bestValue) + "," + ( (bs[idx] - bestValue)));
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static Random rand = new Random();
}
