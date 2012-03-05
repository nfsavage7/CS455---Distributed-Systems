/* pacakge statement */
package cs455.scaling.client;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                          Message                                                         */
/*                                                        -----------                                                       */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Message class. It is used as a way to track the hashes that are being sent back from the server         */
/* ************************************************************************************************************************ */

public class Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private String hash;
	private int ID;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Message (String h, int id){
		hash = h;
		ID = id;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public String getHash(){
		return hash;
	}
	
	public int getID(){
		return ID;
	}

	public boolean equals(Message msg){
		return hash.equals(msg.getHash());
	}

	public String toString(){
		return hash;
	}

}
