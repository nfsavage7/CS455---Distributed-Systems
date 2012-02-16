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
		if(routers < numNodes){
			System.out.println("MST::Prim: Unable to compute MST");
		}
	}

	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */
	
	public ArrayList<String> get(String id){
		ArrayList<String >ret = new ArrayList<String>();
		ArrayList<Edge> edges = mst.get(id);

		for(int i = 0; i < edges.size(); i++){
			ret.add(edges.get(i).getVertex());
		}
		
		return ret;
	}

	public HashMap<String, ArrayList<String>> getRoutingPlan(){
		HashMap<String, ArrayList<String>> plan = new HashMap<String, ArrayList<String>>();

		Object[] keys = mst.keySet().toArray();
		for(int i = 0; i < keys.length; i++){
			String key = (String)(keys[i]);
			ArrayList<Edge> edges = mst.get(key);
			ArrayList<String> peers = new ArrayList<String>();
			
			for(int j = 0; j < edges.size(); j++){
				peers.add(edges.get(j).getVertex());
			}
			
			plan.put(key, peers);
		}
		return plan;
	}
	
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
