package cs455.scaling.tasks;

/* java imports */
import java.nio.channels.SelectionKey;

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                            Task                                                          */
/*                                                          --------                                                        */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/*														            */
/* 	This is the Task class. Everytime the Thread Pool Manager gets a task, it will add one of these to it's quque	    */
/* ************************************************************************************************************************ */

public abstract class Task{ 			//TODO: This will most likely be abstract

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */


	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */
	

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public boolean equals(Task t){
		return t.getSelectionKey().equals(this.getSelectionKey());
	}

	abstract public SelectionKey getSelectionKey();
	abstract public void execute();
}
