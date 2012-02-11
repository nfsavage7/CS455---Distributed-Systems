package cdn.node;

import cdn.communications.Link;
/* ************************************************************************************************************************ */
/*                                                      Server node class                                                   */
/*						       -------------------                                                  */
/* 	This is an abstract class that ensures that all nodes in the CDN implement the methods they need to be able to      */
/* recieve connections and message in this setup.                                                                           */
/* ************************************************************************************************************************ */

public abstract class Server{
	/* **************************************************************************************************************** */
	/*                                              Abstract methods                                                    */
	/* **************************************************************************************************************** */

	public abstract void acceptMsg(byte[] msg, Link l);
	public abstract void addLink(Link l);
}
