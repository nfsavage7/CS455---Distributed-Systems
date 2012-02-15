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
			//System.out.println("inMST: " + inMST.size());
		//	System.out.println(seed);
			Edge min = null;
			//System.out.println("hit");
			for(int i = 0; i < inMST.size(); i++){
				Edge e = adjLst.getMinEdge(inMST.get(i));
				if(min == null && e != null && !inMST.contains(e.getVertex())){
					from  = inMST.get(i);
					min = e;
				} else if (e == null || inMST.contains(e.getVertex())){
					continue;
				} else if(/*(e != null && min != null) && */(e.getWeight() <= min.getWeight())  && !(inMST.contains(min.getVertex()))){
					from = inMST.get(i);
					min = e;
				}
			}
			/*if(min.isInMST()){
				System.out.println("MST: " + inMST.size());
				System.out.println("min1: " + adjLst.getMinEdge(inMST.get(0)));
				System.out.println("min2: " + adjLst.getMinEdge(inMST.get(1)));
				System.out.println("Min: " + min);
				break;
			}*/
			ArrayList<Edge> e = new ArrayList<Edge>();
			if(mst.containsKey(from)){
				e = mst.get(from);
				mst.remove(from);
			}
			/*if(min != null && mst.containsKey(min.getVertex())){
				eTo = mst.get(min.getVertex());
				mst.remove(min.getVertex());
			}*/
			if(min != null){
				e.add(min);
				mst.put(from, e);
				min.addToMST();
				//System.out.println(min);
			//	System.out.println(adjLst);
				Edge edge = adjLst.getEdge(min.getVertex(), from);
			//	System.out.println("Edge: " + edge);
				edge.addToMST();
				inMST.add(min.getVertex());
				System.out.println(from + ": " + e);
			}
				
		}
		if(routers < numNodes){
			System.out.println("MST::Prim: something isn't right");
		}
	}

	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */

}
