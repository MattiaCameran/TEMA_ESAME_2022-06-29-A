package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private Graph<Album, DefaultWeightedEdge> grafo;
	
	private ItunesDAO dao;
	
	private Map<Integer, Album> idMap;
	
	List<Album> camminoMassimo;
	int contatore = 0;
	int max = 0;
	
	public Model() {
		
		this.dao = new ItunesDAO();	//Creo subito il dao nel costruttore.
		
		idMap = new HashMap<Integer, Album>();	//Creo la idMap, che popolo nel grafo
		
	}
	
	public String creaGrafo(int n) {
	
		this.dao.mapAlbums(n, idMap);	//Solo quando creo il grafo decido che n utilizzare, per questo motivo la mappa sarà diversa da n a n scelto e quindi la sovrascrivo per ogni creazione del grafo.
		
		//Creo il grafo
		grafo = new SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//Lo popolo con i vertici.
		
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Provo.
		//System.out.println("Grafo creato!");
		//System.out.println("Numero vertici "+this.grafo.vertexSet().size());
		
		//Aggiungo gli archi
		for(Album a1: this.grafo.vertexSet()) {
			for(Album a2: this.grafo.vertexSet()) {
				
				if(!a1.equals(a2)) {
					
				int peso = a1.getNCanzoni()-a2.getNCanzoni();
				
				if(peso > 0) {
					Graphs.addEdgeWithVertices(this.grafo, a2, a1, Math.abs(peso));
				}
				else if(peso < 0) {
					Graphs.addEdgeWithVertices(this.grafo, a1, a2, Math.abs(peso));
				}
				
				}
			}
		}
		return "Grafo creato!\n" + "Numero vertici "+ this.grafo.vertexSet().size()+"\n"
									+"Numero archi "+this.grafo.edgeSet().size()+"\n";
	}
	
	public List<Album> getAlbums(int n){
		List<Album> t = new LinkedList<Album>(idMap.values());
		Collections.sort(t);
		return t;
	}
	
	public List<AdiacenzaAlbum> getAdiacenzeOrdinate(Album a1){
		

		List<Album> successori = Graphs.successorListOf(this.grafo, a1);
		List<AdiacenzaAlbum> risultato = new ArrayList<AdiacenzaAlbum>();
		

		
		for(Album a2: successori) {
			
				AdiacenzaAlbum ab = this.getBilancio(a2);
				
				if(ab!=null) {
					risultato.add(ab);
				}
		}
		
		Collections.sort(risultato);
		
		return risultato;
	}
	
	public AdiacenzaAlbum getBilancio(Album a) {
		
		List<Album> predecessori = Graphs.predecessorListOf(this.grafo, a);
		List<Album> successori = Graphs.successorListOf(this.grafo, a);
		
		double sommaP = 0.0;
		double sommaS = 0.0;
		double bilancio = 0;
		
		for(Album a2: successori) {
	
				sommaS += this.grafo.getEdgeWeight(this.grafo.getEdge(a, a2));
		}
			for(Album a3: predecessori) {
				
				sommaP += this.grafo.getEdgeWeight(this.grafo.getEdge(a3, a));
			}
			
			bilancio = sommaP-sommaS;
			AdiacenzaAlbum ab = new AdiacenzaAlbum(a, bilancio);
			
			return ab;
	}
	
	public List<Album> calcolaPercorso(Album partenza, Album destinazione, int x)
	{
	
		
		camminoMassimo = new LinkedList<Album>();	//Creo la lista cammino.
		
		List<Album> parziale = new ArrayList<Album>();	//Creo la lista parziale.
		
		parziale.add(partenza);							//Io so già che parto dall'album inserito dall'utente.
		
		cerca(parziale, destinazione, x);
		
		return camminoMassimo;
	}

	private void cerca(List<Album> parziale, Album destinazione, int x) {
		
		AdiacenzaAlbum aa0 = this.getBilancio(parziale.get(0));
		
		//Stato terminale
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			
			if(contatore > max) {
				max = contatore;
				camminoMassimo = new LinkedList<Album>(parziale);
				return;
			}
		}
			//Stato normale
			for(Album a: Graphs.successorListOf(this.grafo, parziale.get(parziale.size()-1))) {
				
				contatore = 0;	//Contatore numero vertici con bilancio superiore a album di partenza
				
				if(!parziale.contains(a)) {	//Se la lista non contiene (prevengo cicli)
					
				if(this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1), a)) > x) {	//Se il peso dell'arco formato dall'ultimo elemento della lista e l'album da inserire supera x
					
					AdiacenzaAlbum aa = this.getBilancio(a);
					if(aa.getBilancio() > aa0.getBilancio()) {	//Se il bilancio del vertice a supera quello del vertice di partenza, aggiungi uno.
						contatore++;
					}
					
					parziale.add(a);	//Aggiungi l'album
					cerca(parziale, destinazione, x);
					parziale.remove(parziale.size()-1);
					
				}
			}
			
		}
		
	}
}

