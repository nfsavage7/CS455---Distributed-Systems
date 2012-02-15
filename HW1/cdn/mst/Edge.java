package cdn.mst;

/* ************************************************************************************************************************ */
/*                                                              Edge class                                                  */
/*                                                             ------------                                                 */
/*                             	This is the Edge class. It is used for the representation of the MST.                       */
/* ************************************************************************************************************************ */
public class Edge{


	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private String vertex;
	private int weight;
	private boolean inMST = false;
	/* **************************************************************************************************************** */
	/*                                        Constructors and other inital methods                                     */
	/* **************************************************************************************************************** */

	public Edge(String v, int w){
		vertex = v;
		weight = w;
	}	
	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */

	public String getVertex(){
		return vertex;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void addToMST(){
		inMST = true;
	}

	public boolean isInMST(){
		return inMST;
	}

	public String toString(){
		return "Conected to Router " + vertex + " with weight " + weight;
	}
}
