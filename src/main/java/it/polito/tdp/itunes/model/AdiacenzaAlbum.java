package it.polito.tdp.itunes.model;

public class AdiacenzaAlbum implements Comparable<AdiacenzaAlbum>{

	Album a;

	double bilancio;
	public AdiacenzaAlbum(Album a, double bilancio) {
		super();
		this.a = a;

		this.bilancio = bilancio;
	}
	public Album getA() {
		return a;
	}
	public void setA(Album a) {
		this.a = a;
	}

	public double getBilancio() {
		return bilancio;
	}
	
	@Override
	public String toString() {
		return a + " - "+"bilancio="+bilancio;
	}
	@Override
	public int compareTo(AdiacenzaAlbum o) {
		// TODO Auto-generated method stub
		return (int)(-(this.bilancio-o.getBilancio()));
	}
}
