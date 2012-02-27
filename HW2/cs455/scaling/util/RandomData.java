/* pacakge statement */
package cs455.scaling.util;

/* java imports */
import java.util.Random;

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                      Random Data                                                         */
/*                                                    ---------------                                                       */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Random Data class. It represents the message that the client will be sending to the server		    */
/* ************************************************************************************************************************ */

public class RandomData{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private byte[] data = new byte[8000];
	
	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public RandomData(){
		Random rand = new Random();
		rand.nextBytes(data);
	}

	public RandomData(byte[] d){
		data = d;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public String getHash(){
		return SHA1.SHA1FromBytes(data);
	}

	public String toString(){
		String ret = "";
		for(int i = 0; i < data.length; i++){
			ret += data[i];
		}
		return ret; 
	}

}
