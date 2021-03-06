

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * @author yiwen zhong
 *
 */

public class Problems {
	public static Problems problem = null;
	public static String fileName = null;
    public static Random rand = new Random();
    
	//to open the file generated by myself
	private Problems(String fileName) throws FileNotFoundException,IOException {
		Problems.fileName = fileName;
       	readFile(fileName);
 	}
	
	/**
	 * The problem to be solved is:
     *  
 	 * The format of the data file is:
     *    number of facilities (n)
     *    optimal solution value (zero if unavailable)
     *    the length of each facility
     *    all flows between two facilities	
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readFile(String fileName) throws FileNotFoundException,IOException{
		FileReader data;
		Scanner scan;

		data = new FileReader(fileName);
		scan = new Scanner(data);
		facilityNumber = scan.nextInt();
		bestValue = scan.nextDouble();
		if (bestValue == 0) {
			bestValue = 0;
		}
		//to read lengths of facilities
		lengths = new int[facilityNumber];
		halfLengths = new double[facilityNumber];
		for (int i = 0; i < facilityNumber; i++) {
			lengths[i] = scan.nextInt();
			halfLengths[i] = lengths[i] / 2.0;
		}
		
		//to read the flows
		flows = new int[facilityNumber][facilityNumber];
		for (int i = 0; i < facilityNumber; i++) {
			for (int j = 0; j < facilityNumber; j++) {
				flows[i][j] = scan.nextInt();
			}
		}

		//to produce relation of halfLength
		difLen = new double[facilityNumber][facilityNumber];
		sumLen = new double[facilityNumber][facilityNumber];
		for (int i = 0; i < facilityNumber; i++) {
			for (int j = 0; j < facilityNumber; j++) {
				difLen[i][j] = halfLengths[i] - halfLengths[j];
				sumLen[i][j] = halfLengths[i] + halfLengths[j];
			}
		}
		
		scan.close();
		data.close();
	}

	public static Problems getProblem() {
		if (problem == null) {
			try {
				problem = new Problems(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return problem;
	}

	public static void setFileName(String fileName) {
		Problems.fileName = fileName;
		try {
			problem = new Problems(fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public String toString() {
		StringBuffer out=new StringBuffer();

		out.append(facilityNumber + "\r\n" + bestValue +  "\r\n");
		for (int i = 0; i < lengths.length; i++) {
			out.append(lengths[i]+"\t");
			if ( (i+1) % 10 == 0) {
				out.append("\r\n");
			}
		}
		out.append("\r\n");
		
	
		for (int i=0; i<flows.length; i++) {
			for (int j=0; j<flows[i].length; j++) {
				out.append(flows[i][j]+"\t");
				if ( (j+1) % 10 == 0) {
					out.append("\r\n");
				}
			}
			out.append("\r\n");
		}

		out.append("\r\n");
		return out.toString();
	}
	
	
	public int getFacilityNumber() { return facilityNumber;}
	public int[] getLengths() { return lengths;}
	public int getLength(int facility) { return lengths[facility];}
	public double[] getHalfLengths() { return halfLengths;}
	public double getHalfLength(int facility) { return halfLengths[facility];}
	
	public int[][] getFlows() { return flows;}
	public int[] getFlows(int facility) { return flows[facility];}
	public int getFlow(int fac1, int fac2) { return flows[fac1][fac2];}

	public double getBestValue() { return bestValue;}



    private int facilityNumber; 
     
	private int[] lengths = null; //length of each facility
	private double[] halfLengths = null;
	private int[][] flows = null; //flow between two facilities
	private double bestValue; //the best known solution
	
	public double[][] difLen;
	public double[][] sumLen;


	
	public static void main(String[] args) throws IOException {
		String filePath = (new File("")).getAbsolutePath() + "/../p110-300/"; 
		String fileName = filePath+"p110.txt";
		Problems.setFileName(fileName); 
		Problems p = Problems.getProblem();
		System.out.println(p);
		System.out.println(p.getBestValue());
		System.out.println(p.getLength(0));
		System.out.println(p.getFlow(0, 2));
	}

}



