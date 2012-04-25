This is the README for Assignment 3 byt Nick Savage

Please note, for each Directory Service to run properly, there needs to be a /tmp/savage/HW3 direcotyr on the local machine. My program dose not make one, or check for it.

packages
	client
		ClientModule is the command line interface for the user
		VirtualFileSystem is the client side manger of the file system
		VirtualFile is the representation of each file within the VFS

	communications
		Link is the class incharge of sending the wireformats between the client and direcotry services
	
	remote
		DirecotryService is the manager of the physical files at each node

	wireformats
		Message is the abstract class with some helper methods for all wireformats
		Each of the following classes are self explanitory and contain a breief description in the file headers. Sorry, but I ran out of time to do this part. ^^;
			MkdirRequest
			MountRequest
			MountResponce
			MvMessage
			MvRequest
			PeekRequest
			PeekResponce
			RmRequest
			RmResponce
