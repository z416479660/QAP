

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author yiwen zhong
 *
 */
public class Solution implements Comparable<Solution> {

	public Solution(boolean initial) {
		problem = Problems.getProblem();
		match = new int[problem.getFacilityNumber()];
		if ( initial ) { 
			List<Integer> itemsList = new ArrayList<>();
			for (int i = 0; i < match.length; i++) {
				itemsList.add(i);
			}
			Collections.shuffle(itemsList);
			for (int i = 0; i < match.length; i++) {
				match[i] = itemsList.get(i);
			}
			//eval();
			evalCut();
		} 
	}

	/**
	 * Use parameter to clone a new Solution
	 * 
	 * @param solution
	 */
	public Solution(Solution solution) {
		problem = Problems.getProblem();
		match = solution.match.clone(); 
		lastImproving = solution.lastImproving;
		value = solution.value;
		if (solution.cut!=null) cut = solution.cut.clone();
		if (solution.entry!=null) entry = solution.entry.clone();
	}
	
	
	/**
	 * Use parameter to update this object
	 * 
	 * @param s
	 */
	public void update(Solution s) {
		for (int i = 0; i <match.length; i++) {
			match[i] = s.match[i];
		}
		lastImproving = s.lastImproving;
		value = s.value;
		if (s.cut!=null) cut = s.cut.clone();
		if (s.entry!=null) entry = s.entry.clone();
	}


	
//	private void eval() {
//		calcuDist();
//		value = 0;
//		for (int i = 0; i < match.length-1; i++) {
//			int fac1 = match[i];
//			for (int j = i+1; j < match.length; j++) {
//				int fac2 = match[j];
//				value += dist[fac1][fac2] * problem.getFlow(fac1, fac2);
//			}
//		}
//	}
	
	public void evalCut() {
		cut = new double[match.length];
		entry = new double[match.length];
		
		double[] fb = new double[match.length]; //accumulated flow before facility
		double[] fa = new double[match.length]; //accumulated flow after facility
		for (int i = 0; i < match.length; i++) {
			for (int j = 0; j < i; j++) {
				fb[match[i]] += problem.getFlow(match[i], match[j]);
			}
			for (int j = i+1; j < match.length; j++) {
				fa[match[i]] += problem.getFlow(match[i], match[j]);
			}
		}
		
		for (int i = 0; i < match.length; i++) {
			entry[match[i]] = fa[match[i]] - fb[match[i]];
		}
		
		for (int i = 0; i < match.length; i++) {
			if ( i == 0 ) {
				cut[i] = entry[match[i]];
			} else {
				cut[i] = cut[i-1] + entry[match[i]];
			}
		}
		
		value = 0;
		for (int i = 0; i < match.length-1; i++) {
			value += cut[i] * (problem.getHalfLength(match[i]) + problem.getHalfLength(match[i+1]));
		}
	}
	
//	private void calcuDist() {
//		dist = new double[match.length][match.length];
//		for (int i = 0; i < match.length; i++) {
//			int fac1 = match[i]; //first facility
//			dist[fac1][fac1] = 0;
//			int prev = fac1;
//			for (int j = i + 1; j < match.length; j++) {
//				int fac2 = match[j];
//				dist[fac1][fac2] = dist[fac1][prev] + (problem.getLength(prev) + problem.getLength(fac2)) / 2.0;
//				dist[fac2][fac1] = dist[fac1][fac2];
//                prev = fac2;
//			}
//		}
//	}
	
//	private Operator swapOperator() {
//		//find two position l and k, l > k
//		int l = rand.nextInt(match.length);
//		int k = l;
//		while (k == l) {
//			k = rand.nextInt(match.length);
//		}
//		
//		return swapOperator(k, l);
//	}
	
	private Operator swapOperator(int k, int l) {
		if (  l < k ) {
			int temp = l;
			l = k;
			k = temp;
		}
		int r = match[k];
		int s = match[l];
		
		//calculate new cut
		double[] newCut = cut.clone();
		double t = 0;
		for (int i = k; i < l; i++) {
			t += 2 * problem.getFlow(s, match[i]);
		}
		newCut[k] = ((k > 0)? cut[k-1] : 0) + entry[s] + t;
		for (int m = k+1; m < l; m++) {
			newCut[m] = newCut[m-1] + entry[match[m]] + 2 * (problem.getFlow(r, match[m]) - problem.getFlow(s, match[m]));
		}
		
		//calculate gain
		double gain = 0;
		if ( l == k + 1) {
			gain = (((k > 0)? cut[k-1] : 0) - cut[l]) * problem.difLen[s][r]; //(problem.getHalfLength(s) - problem.getHalfLength(r));
			gain += (((k > 0)? cut[k-1] : 0) + entry[s] + 2 * problem.getFlow(r, s) - cut[k]) * problem.sumLen[r][s];//(problem.getHalfLength(r) + problem.getHalfLength(s)); 
		} else {
			for (int m = k + 1; m < l - 1; m++) {
				gain += (newCut[m] - cut[m]) *  problem.sumLen[match[m]][match[m+1]];//(problem.getHalfLength(match[m]) + problem.getHalfLength(match[m+1])); 
			}
			gain += (((k > 0)? cut[k-1] : 0) - cut[l]) *  problem.difLen[s][r]; //(problem.getHalfLength(s) - problem.getHalfLength(r));
			gain += newCut[k] * problem.sumLen[s][match[k+1]];//(problem.getHalfLength(s) + problem.getHalfLength(match[k+1]));
			gain -= cut[k] * problem.sumLen[r][match[k+1]];//(problem.getHalfLength(r) + problem.getHalfLength(match[k+1]));
			gain += newCut[l-1] * problem.sumLen[match[l-1]][r];//(problem.getHalfLength(match[l-1]) + problem.getHalfLength(r));
			gain -= cut[l-1] * problem.sumLen[match[l-1]][s];//(problem.getHalfLength(match[l-1]) + problem.getHalfLength(s));
		}
		
		return new Operator(true, k, l, r, s, gain, newCut);
	}
	
	private void performSwap(Operator o) {
		match[o.k] = o.s;
		match[o.l] = o.r;
		value += o.gain;
		cut = o.cut;
		for (int m = 0; m < match.length; m++) {
			entry[match[m]] = cut[m] - (m==0?0:cut[m-1]);
		}
	}
	
//	private Operator insertOperator() {
//		//find two position l and k
//		int l = rand.nextInt(match.length);
//		int k = l;
//		while (k == l) {
//			k = rand.nextInt(match.length);
//		}
//		
//		return insertOperator(k, l);
//	}
	
	private Operator insertOperator(int k, int l) {
		if (  Math.abs(l - k) == 1 ) {
			return swapOperator(k, l);
		}
		int r = match[k];
		int s = match[l];
		
		//calculate new cut
		double[] newCut = cut.clone();
		double gain = 0;
		if ( k < l) {
			for (int m = k; m < l; m++) {
				newCut[m] = (m==0?0:newCut[m-1]) + entry[match[m+1]] + 2 * problem.getFlow(r, match[m+1]);
			}
			
			for (int m = k+1; m < l; m++) {
				gain += (newCut[m-1]-cut[m]) * problem.sumLen[match[m]][match[m+1]];//(problem.getHalfLength(match[m]) + problem.getHalfLength(match[m+1])); 
			}
			gain += (k==0?0:cut[k-1]) * problem.difLen[match[k+1]][r];//(problem.getHalfLength(match[k+1]) - problem.getHalfLength(r)); 
			gain -= cut[k] * problem.sumLen[match[k+1]][r];// (problem.getHalfLength(match[k+1]) + problem.getHalfLength(r)); 
			gain += newCut[l-1] * problem.sumLen[r][match[l]];// (problem.getHalfLength(r) + problem.getHalfLength(match[l])); 
			gain += cut[l] * problem.difLen[r][match[l]];//(problem.getHalfLength(r) - problem.getHalfLength(match[l])); 
		} else { //k > l
			for (int m = k-1; m >= l; m--) {
				newCut[m] = newCut[m+1] - entry[match[m]] + 2 * problem.getFlow(r, match[m]);
			}
			
			for (int m = l; m < k-1; m++) {
				gain += (newCut[m+1]-cut[m]) * problem.sumLen[match[m]][match[m+1]];//(problem.getHalfLength(match[m]) + problem.getHalfLength(match[m+1])); 
			}
			
			gain += cut[k] * problem.difLen[match[k-1]][r];//(problem.getHalfLength(match[k-1]) - problem.getHalfLength(r)); 
			gain -= cut[k-1] * problem.sumLen[match[k-1]][r];//(problem.getHalfLength(match[k-1]) + problem.getHalfLength(r)); 
			gain += newCut[l] * problem.sumLen[r][match[l]];// (problem.getHalfLength(r) + problem.getHalfLength(match[l])); 
			gain += (l==0?0:cut[l-1]) * problem.difLen[r][match[l]];// (problem.getHalfLength(r) - problem.getHalfLength(match[l])); 
		}
		return new Operator(false, k, l, r, s, gain, newCut);
	}
	
	private void performInsert(Operator o) {
		//insert the facility in idx1 into position idx2
		int idx1 = o.k;
		int idx2 = o.l;
		int fac = match[idx1];
		if ( idx1 > idx2) {
			for (int i = idx1; i > idx2; i--) {
				match[i] = match[i-1];
			}
		} else {
			for (int i = idx1; i < idx2; i++) {
				match[i] = match[i+1];
			}
		}
		match[idx2] = fac;
		value += o.gain;
		cut = o.cut;
		for (int m = 0; m < match.length; m++) {
			entry[match[m]] = cut[m] - (m==0?0:cut[m-1]);
		}
	}
	
	public Operator neighborOperator() {
		return neighborOperator(Simulations.neighborType);
	}
	
	public Operator neighborOperator(ENeighborType neighborType) {
		//find two position l and k
		int l = rand.nextInt(match.length);
		int k = l;
		while (k == l) {
			k = rand.nextInt(match.length);
		}
		
		if (neighborType == ENeighborType.SWAP) {
			return swapOperator(k, l);
		} else if (neighborType == ENeighborType.INSERT) {
			return insertOperator(k, l);
		} else if (neighborType == ENeighborType.SWAP_OR_INSERT) {
			if (rand.nextDouble() < 0.5) {
				return swapOperator(k, l);
			} else {
				return insertOperator(k, l);
			}
		} else { //Simulations.neighborType == ENeighborType.SWAP_AND_INSERT
			Operator o1 = swapOperator(k, l);
			Operator o2 = insertOperator(k, l);
			if ( o1.gain < o2.gain) {
				return o1;
			} else {
				return o2;
			}
		}
	}
	
	public void performChange(Operator o) {
		if ( o.isSwap ) {
			performSwap(o);
		} else {
			performInsert(o);
		}
	}
	
//	private Solution neighbor() {
//		if (rand.nextDouble() < 0.5) {
//			return swapNeighbor();
//		} else {
//		    return insertNeighbor();
//		}
//	}
//	
//	
//	private Solution neighbor(int facNum) {
//		Solution s = new Solution(false);
//		s.match = this.match.clone();
//		//produce facility list
//		List<Integer> facList = new ArrayList<Integer>();
//		for (int i = 0; i < match.length; i++) {
//			facList.add(i);
//		}
//		int[] facs = new int[facNum];
//		for (int i = 0; i < facs.length; i++) {
//			facs[i] = facList.remove(rand.nextInt(facList.size()));
//		}
//
//		for (int i=0; i < facs.length; i++) {
//			s.match[facs[i]] = match[facs[(i+1)%facs.length]];
//		}
//		s.eval();
//		return s;
//	}
//	
//	private Solution swapNeighbor() {
//		//swap to facility
//		int idx1 = rand.nextInt(match.length);
//		int idx2 = idx1;
//		while (idx2 == idx1) {
//			idx2 = rand.nextInt(match.length);
//		}
//		return swapNeighbor(idx1, idx2);
//	}
//	
//	private Solution swapNeighbor(int idx1, int idx2) {
//		Solution s = new Solution(false);
//		s.match = this.match.clone();
//		s.match[idx1] = match[idx2];
//		s.match[idx2] = match[idx1];
//		s.eval();
//		return s;
//	}
//	
//	private Solution insertNeighbor() {
//		//find two different position
//		int idx1 = rand.nextInt(match.length);
//		int idx2 = idx1;
//		while (idx2 == idx1) {
//			idx2 = rand.nextInt(match.length);
//		}
//		
//		return insertNeighbor(idx1, idx2);
//	}
//	
//	private Solution insertNeighbor(int idx1, int idx2) {
//		Solution s = new Solution(false);
//		s.match = this.match.clone();
//		
//		//insert the facility in idx1 into position idx2
//		int fac = s.match[idx1];
//		if ( idx1 > idx2) {
//			for (int i = idx1; i > idx2; i--) {
//				s.match[i] = s.match[i-1];
//			}
//		} else {
//			for (int i = idx1; i < idx2; i++) {
//				s.match[i] = s.match[i+1];
//			}
//		}
//		s.match[idx2] = fac;
//		s.eval();
//		return s;
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Solution s) {
		if ( value < s.value) {
			return 1;
		} else if ( value == s.value) {
			return 0;
		} else {
			return -1;
		}
	}

	public String toString() {
		String str = "";
        str += value + "\r\n";
        for (int i = 0; i < match.length; i++) {
        	str += match[i] + ",";
        }
        str += "\r\n";
        
        for (int i=0; i<dist.length; i++) {
			for (int j=0; j<dist[i].length; j++) {
				str += dist[i][j]+"\t";
				if ( (j+1) % 10 == 0) {
					str += "\r\n";
				}
			}
			str += "\r\n";
		}
 		return str;
	}

	public int getFacilityNumber() { return match.length; }
	public double getValue() {return value;}
	public void setLastImproving(int n) { this.lastImproving = n; }
	public int getLastImproving() { return lastImproving;}

	private Problems problem;
	private int[] match;
	private double[][] dist;
	private double value;
	private int lastImproving = 0; //
	
	private double[] cut; //cut of a position
	private double[] entry;//the difference of accumulated flow between facilities after and before
	
	
	public static void main(String[] args) {
		String filePath = (new File("")).getAbsolutePath() + "/../p110-300/"; 
		String fileName = filePath+"p110.txt";
		Problems.setFileName(fileName); 
		Solution s = new Solution(true);
		System.out.println(s.value);
		s.evalCut();
		
		int l = rand.nextInt(s.match.length);
		int k = l;
		while (k == l) {
			k = rand.nextInt(s.match.length);
		}
		
//		Operator o = s.swapOperator(k, l);
//		Solution neighbor = s.swapNeighbor(k, l);
//		System.out.println(s.getValue());
//		System.out.println((s.getValue() + o.gain) + "==" + neighbor.getValue() + "?");
//		s.performSwap(o);
//		System.out.println(s.getValue());
//		
//		s.evalCut();
//		o = s.insertOperator(k, l);
//		neighbor = s.insertNeighbor(k, l);
//		System.out.println(s.getValue());
//		System.out.println((s.getValue() + o.gain) + "==" + neighbor.getValue() + "?");
//		s.performInsert(o);
//		System.out.println(s.getValue());
//		s.evalCut();
//		System.out.println(s.getValue());
	}

	private static Random rand = new Random();
}

