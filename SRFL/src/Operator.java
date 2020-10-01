
public class Operator {
	public boolean isSwap;
	public int k, l, r, s;
    public double gain = 0;
    public double[] cut;
   
    public Operator(boolean isSwap, int k, int l, int r, int s, double gain, double[] cut) {
    	this.isSwap = isSwap;
    	this.k = k;
    	this.l = l;
    	this.r = r;
    	this.s = s;
    	this.gain = gain;
    	this.cut = cut;
    }
}
