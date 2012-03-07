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
/* 	This is the Task interface. Everytime the Thread Pool Manager gets a task, it will add one of these to it's quque   */
/* ************************************************************************************************************************ */

public interface Task{

	/* **************************************************************************************************************** */
	/*                                                Abstract Methods                                                  */
	/* **************************************************************************************************************** */

	void execute(String worker);
}
