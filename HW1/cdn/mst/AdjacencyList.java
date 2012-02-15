package cdn.mst;

/* java imports */
import java.util.HashMap;
import java.util.ArrayList;

/* local imports */
import cdn.wireformats.LinkWeightUpdate;
import cdn.wireformats.LinkInfo;
import cdn.wireformats.RouterInfo;

/* ************************************************************************************************************************ */
/*                                                        Adjacency List class                                              */
/*                                                       ----------------------                                             */
/* 	This is the adjacency list class. It is the data structure that will represent the coneected graph for the mst.     */
/* ************************************************************************************************************************ */
public class AdjacencyList{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private HashMap<String, ArrayList<Edge>> adjLst = new HashMap<String, ArrayList<Edge>>();

	/* **************************************************************************************************************** */
	/*                                        Constructors and other inital methods                                     */
	/* **************************************************************************************************************** */

	public AdjacencyList(LinkWeightUpdate msg, int numConnections){
		ArrayList<LinkInfo> links = msg.getLinks();
		ArrayList<String> done = new ArrayList<String>();
		for(int i = 0; i < links.size(); i++){
			LinkInfo l = links.get(i);
			if(done.contains(l.getRouterOne().getID()) && done.contains(l.getRouterTwo().getID())){
				continue;
			} else if(done.contains(l.getRouterOne().getID())){
				adjLst.put(l.getRouterTwo().getID(), msg.getLinks(l.getRouterTwo(), numConnections));
				done.add(l.getRouterTwo().getID());
			} else if (done.contains(l.getRouterTwo().getID())) { 
				adjLst.put(l.getRouterOne().getID(), msg.getLinks(l.getRouterOne(), numConnections));
				done.add(l.getRouterOne().getID());
			} else {
				adjLst.put(l.getRouterOne().getID(), msg.getLinks(l.getRouterOne(), numConnections));
				done.add(l.getRouterOne().getID());
				//System.out.println(done.get(done.size()-1) + ": " + adjLst.get(done.get(done.size()-1)));
				adjLst.put(l.getRouterTwo().getID(), msg.getLinks(l.getRouterTwo(), numConnections));
				done.add(l.getRouterTwo().getID());
			}
			//System.out.println(done.get(done.size()-1) + ": " + adjLst.get(done.get(done.size()-1)));
		}
		
	}

	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */

	public ArrayList<Edge> getEdges(String r){
		return adjLst.get(r);
	}

	public Edge getMinEdge(String r){
		ArrayList<Edge> edges = adjLst.get(r);
		Edge min = null;
		if( edges == null ){
			System.out.println("null");
			return min;
		}
		for(int i = 0; i < edges.size(); i++){
			if(min == null && !(edges.get(i).isInMST())){
				min = edges.get(i);
				continue;
			} else if (min == null){
				continue;
			}
			if(edges.get(i).getWeight() <= min.getWeight() && !(edges.get(i).isInMST()) ){
				min = edges.get(i);
			}
		}
		//System.out.println("getMinEdge:" + min);
		return min;
	}

	public Edge getEdge(String from, String to){
		ArrayList<Edge> edges = adjLst.get(from);
		Edge ret = null;
		//System.out.println(edges.size());
		for(int i = 0; i < edges.size(); i++){
			if(edges.get(i).getVertex().equals(to)){
				ret = edges.get(i);
				break;
			}
		}
		//System.out.println("makring link from " + from + " to " + to);
		return ret;
	}
	
	public int getNumNodes(){
		return adjLst.keySet().size();
	}
}
