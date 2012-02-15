package cdn.mst;

/* java imports */
import java.util.HashMap;
import java.util.ArrayList;

/* local imports */
import cdn.wireformats.LinkWeightUpdate;

/* ************************************************************************************************************************ */
/*                                                             MST class                                                    */
/*                                                            -----------                                                   */
/* 	            This is the MST class. It uses Prim's Algorithm to compute the minimum spanning tree.                   */
/* ************************************************************************************************************************ */

public class MST{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	private HashMap<String, ArrayList<Edge>> mst = new HashMap<String, ArrayList<Edge>>();
	private AdjacencyList adjLst;
	private String seed;

	/* **************************************************************************************************************** */
	/*                                        Constructors and other inital methods                                     */
	/* **************************************************************************************************************** */
	public MST(LinkWeightUpdate msg, int numConnections, String s){
		adjLst = new AdjacencyList(msg, numConnections);
		seed = s;
		prim();
	}

	public void prim(){
		ArrayList<String> inMST = new ArrayList<String>();
		//Start with the seed
		//take it's minimum edge, and add that vertex to the MST
		//Then look from the minimum edge from thereamining edges
		//do this until all of the verticies are in the MST
		//Edges now know if they are in the MST
		//SO, the idea is to add the edge, not the vertex (the edge has the vertex ^^)
		inMST.add(seed);
		int routers = 1;
		int numNodes = adjLst.getNumNodes();
		for(routers = 1; routers < numNodes; routers++){
			String from = inMST.get(0);
			Edge min = null;
			for(int i = 0; i < inMST.size(); i++){
				Edge e = adjLst.getMinEdge(inMST.get(i));
				if(min == null && e != null && !inMST.contains(e.getVertex())){
					from  = inMST.get(i);
					min = e;
				} else if (e == null || inMST.contains(e.getVertex())){
					continue;
				} else if((e.getWeight() <= min.getWeight())  && !(inMST.contains(min.getVertex()))){
					from = inMST.get(i);
					min = e;
				}
			}
			ArrayList<Edge> e = new ArrayList<Edge>();
			if(mst.containsKey(from)){
				e = mst.get(from);
				mst.remove(from);
			}
			if(min != null){
				e.add(min);
				mst.put(from, e);
				min.addToMST();
				Edge edge = adjLst.getEdge(min.getVertex(), from);
				edge.addToMST();
				inMST.add(min.getVertex());
			}
				
		}
		//TODO pretty this message up
		if(routers < numNodes){
			System.out.println("MST::Prim: something isn't right");
		}
	}

	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */
	
	public void print(String id){
		if(!mst.containsKey(id)){
			System.out.println(id);
			return;
		}
		ArrayList<Edge> edges = mst.get(id);
		for(int i = 0; i < edges.size(); i++){
			System.out.print(id + "--" + edges.get(i).getWeight() + "--");
			print(edges.get(i).getVertex());
		}
	}
}
