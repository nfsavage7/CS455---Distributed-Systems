/* pacakge statement */
package cs455.nfs.client;

/* java imports */
import java.util.Scanner;
import java.util.ArrayList;

/* local imports */ 
import cs455.nfs.communications.Link;
import cs455.nfs.wireformats.PeekRequest;
import cs455.nfs.wireformats.PeekResponce;
import cs455.nfs.wireformats.MountRequest;
import cs455.nfs.wireformats.MountResponce;

/* ************************************************************************************************************************ */
/*                                                      ClientModule                                                        */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Client Module. It is the nfs user interface. It also manages the virtual file system.                   */
/* ************************************************************************************************************************ */

public class ClientModule{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private VirtualFileSystem vfs;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public ClientModule(){
		vfs = new VirtualFileSystem();
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public void peek(String dirService, int port){
		/* Connect to the directory service */
		Link l = new Link(dirService, port);

		/* Send the request */
		PeekRequest request = new PeekRequest();
		l.send(request);

		/* Wait for responce */
		PeekResponce responce = (PeekResponce)(l.read());
		System.out.print(responce);
		l.close();
	}

	public void mount(String dirService, int port, String path){
		Link l = new Link(dirService, port);
		
		MountRequest request = new MountRequest(path);
		l.send(request);

		MountResponce responce = (MountResponce)(l.read());
		ArrayList<String> files = responce.getFiles();
		vfs.populate(responce.getFiles(), responce.isDirectory(), l);
	
	}

	public void pwd(){
		System.out.println(vfs.pwd());
	}

	public void mkdir(String name){
		vfs.mkdir(name);
	}

	public void ls(){
		vfs.ls();
	}

	public void cd(String path){
		vfs.cd(path);
	}

	public void rm(String dir){
		vfs.rm(dir);
	}

	public void mv(String file, String dest){
		vfs.mv(file, dest);
	}

	/* **************************************************************************************************************** */
	/*                                                   Main                                                           */
	/* **************************************************************************************************************** */

	public static void main(String[] args){
		if(args.length != 0){
			System.err.println("usage: java cs455.nfs.client.ClientModule");
			System.exit(1);
		}
		ClientModule client = new ClientModule();
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()){
			try{
				String cmd = in.nextLine();
				if(cmd.startsWith("peek")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					String dirService = tmp.next();
					String tempPort = tmp.next();
					tmp = new Scanner(tempPort);
					int port = tmp.nextInt();
					client.peek(dirService, port);
				} else if (cmd.equals("vpwd")){
					client.pwd();
				} else if (cmd.startsWith("vmkdir")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					client.mkdir(tmp.next());
				} else if (cmd.equals("vls")){
					client.ls();
				} else if (cmd.startsWith("vcd")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					client.cd(tmp.next());
				} else if (cmd.startsWith("vrm")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					client.rm(tmp.next());
				} else if (cmd.startsWith("vmount")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					String dirService = tmp.next();
					String tempPort = tmp.next();
					String dir = tmp.next();
					tmp = new Scanner(tempPort);
					int port = tmp.nextInt();
					client.mount(dirService, port, dir);
				} else if (cmd.startsWith("vmv")){
					Scanner tmp = new Scanner(cmd);
					tmp.next();
					String file = tmp.next();
					String dest = tmp.next();
					client.mv(file, dest);
				} else {
					System.out.println("Command not recognized");
				}
			} catch(Exception e){}
		}
	}
	
}
