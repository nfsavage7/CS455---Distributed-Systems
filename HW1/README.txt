This is the README for CS455 Assignment 1 for Nick Savage spring 2012 semester.

Included are the following files:

cdn.node package:
	Discovery
		- this is the discovery node and all asociated functionality form the assignment.
	Router
		- this is the router node and all functionality as described in the assignment
	Server
		- this is a class that both discovery adn router inherit from. this is how I force funtionality upon the all of the nodes.


cdn. communications package:
	Link
		- this is a wrapper class for a socket. This provides a nice interface for sending and receiving messages.
	LinkSenderThread
		- this is the class that sends the messages across thewire
	LinkReaderThread
		- this is the class that reads in the messages from the wire
	ConnectionAccepterThread
		- this is the class that accepts inbound connections from other nodes in the system

cdn.wireformats pagack:
	Message
		- this is the class that all wireformats inherit form. It forces all of them to marshall/unmarshall and to implement getType(). It also hold useful constants and helper methods.
	RouterInfo
		- this is what is sent when a router initates a connection so the other end knows who just established a connection to him.
	RoutingPlan
		- this is how I tell routers where to send messages to when they get the data.
	LinkInfo
		- this class is sent as part of the peer router list.
	PeerRouterList
		- this sends all of the information about all of the unique links in the network.
	RegisterRequest
		- this is sent from the router to the discovery so that it get registered in the cdn.
	RegisterResponce
		- this is sent form the discovery to the router with the reply to the RegisterResponce
	RemoveLink
		- this is sent if a router cannot connecto to a router that the discovery told it to connect to.
	Data
		- this is send between the routers. It communicates the origonator's current tracker value.
	ChatMessage
		- this was in for test and I ran otu of time to remove, sorry! ^^;

cdn.mst package:
	AdjacencyList
		- this is how I chose to represent the MST within my system.
	Edge
		- this is a component of the AdjacencyList. It holds the destination fo the link and it's weight
	MST
		- this runs Prim's algorithm to compute the MST. It's also incharge of the print-mst command
	MstUpdateThrea
		- this is the timer that periodically updates the weights which causes a change in the MST.

makefile:
	My cude attmept at making a java makefile.

README.txt:
	a quick description of all included files.

Aditional notes:
	None I can think of please feel free to contact me at savagen@cs.colostate.edu with any question and issue. Thanks!

