/* pacakge statement */
package cs455.nfs.remote;

/* java imports */
import java.io.*;
import java.lang.Runtime;
import java.lang.Process;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/* local imports */
import cs455.nfs.communications.Link;
import cs455.nfs.wireformats.Message;
import cs455.nfs.wireformats.PeekRequest;
import cs455.nfs.wireformats.PeekResponce; 
import cs455.nfs.wireformats.MountRequest;
import cs455.nfs.wireformats.MountResponce;
import cs455.nfs.wireformats.MkdirRequest;
import cs455.nfs.wireformats.RmRequest;
import cs455.nfs.wireformats.RmResponce;
import cs455.nfs.wireformats.MvRequest;
import cs455.nfs.wireformats.MvMessage;

/* ************************************************************************************************************************ */
/*                                                       DirectoryService                                                   */
/*                                                   ------------------------                                               */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the direcotry service. It manages the directory structures on remote hosts.                                 */
/* ************************************************************************************************************************ */

public class DirectoryService{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private File rootDir;
	private ServerSocketChannel server;
	private Selector selector;
	private Link client;
	private Link remoteDirService;
	private int port;
	private String IP;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public DirectoryService(int p){
		rootDir = new File("/tmp/savage/HW3");
		try{
			port = p;
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			InetSocketAddress address = new InetSocketAddress(port);
			IP = InetAddress.getLocalHost().getHostName();
			server.socket().bind(new InetSocketAddress(port));
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e){
			System.err.println("DirectoryService:: failed to bind to socket");
		}
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public void listen(){
		while(true){
			Set keys = null;
			try{
				selector.select();
				keys = selector.selectedKeys();
			} catch (Exception e){}
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey)iter.next();
				iter.remove();
				if(key.isValid() && key.isAcceptable()){
					accept();
				}
				if(key.isValid() && key.isReadable()){
					System.out.println("Got message");
					Message msg;
					if(client.hasBytes()){
						msg = client.read();
					} else {
						msg = remoteDirService.read();
					}
					processMsg(msg, key);
				}
			}
		}
	}

	public void accept(){
		try{
			SocketChannel sock = server.accept();
			sock.configureBlocking(false);
			sock.register(selector, SelectionKey.OP_READ);
			if (client == null){
				client = new Link(sock);
			} else {
				System.out.println("Got connection");
				remoteDirService  = new Link(sock);
			}
		} catch (Exception e){
			System.err.println("DirectoryService:: COunld not connect to client");
		}
	}

	public void processMsg(Message msg, SelectionKey key){


		if(msg instanceof PeekRequest){
			PeekResponce responce = new PeekResponce(getContents(rootDir.getPath()));
			client.send(responce);


		} else if (msg instanceof MountRequest){
			ArrayList<String> contents = (getContents(rootDir.getPath() + "/" +((MountRequest)msg).getPath()));
			byte[] isDir = new byte[contents.size()]; 
			for(int i = 0; i < isDir.length; i++){
				if((new File(rootDir.getPath() + "/" +((MountRequest)msg).getPath() + "/" + contents.get(i))).isDirectory()){
					isDir[i] = 1;
				} else {
					isDir[i] = 0;
				}
			}
			MountResponce responce = new MountResponce(contents, isDir);
			client.send(responce);


		} else if(msg instanceof MkdirRequest){
			String path = ((MkdirRequest)(msg)).getPath();
			String name = path.substring(path.indexOf("/") + 1);
			File parent = findFile(rootDir, path.substring(0, path.indexOf("/")));
			new File(parent.getAbsolutePath() + "/" + name).mkdir();


		} else if (msg instanceof RmRequest){
			String path = ((RmRequest)msg).getPath();
			File target = findFile(rootDir, path.substring(path.indexOf("/") + 1));
			boolean sucess = deleteContents(target);
			byte status;
			if(sucess){
				status = 0;
			} else {
				status = 1;
			}
			RmResponce responce = new RmResponce(status);
			client.send(responce);


		} else if(msg instanceof MvRequest){
			mv((MvRequest)msg);
		} else if (msg instanceof MvMessage){
			makeFile((MvMessage)msg);
		} else if(msg == null){
			key.cancel();
			client = null;
		}
			
	}

	private void mv(MvRequest request){
	File target = findFile(rootDir, request.getFile().substring(request.getFile().lastIndexOf("/") + 1));
		if(request.getRemoteIP().equals(IP) && request.getRemotePort() == port){
			String dir = request.getTargetPath();
			dir = dir.substring(0, dir.indexOf("/"));
			File parent = findFile(rootDir, dir);
			try{
				Process p = Runtime.getRuntime().exec("mv " + target.getPath() + " " + parent.getPath() + "/" + request.getTargetPath().substring(request.getTargetPath().indexOf("/")+1));
			} catch (Exception e){e.printStackTrace();}
		} else {
			try{
				FileInputStream in = new FileInputStream(target);
				byte[] data = new byte [(int)target.length()];
				in.read(data);
				in.close();
				remoteDirService = new Link(request.getRemoteIP(), request.getRemotePort());
				MvMessage msg = new MvMessage(request.getTargetPath(), data);
				remoteDirService.send(msg);
				remoteDirService.close();
				target.delete();
			} catch (Exception e){}


		}
	}

	private void makeFile(MvMessage msg){
		String parent = msg.getPath().substring(0, msg.getPath().indexOf("/"));
		File parentDir = findFile(rootDir, parent);
		try{
			File f = new File(parentDir.getPath() + "/" + msg.getPath().substring(msg.getPath().indexOf("/")+1));
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(msg.getContents());
			fos.flush();
			fos.close();
			remoteDirService.close();
		} catch (Exception e){e.printStackTrace();}
	}

	private File findFile(File curr, String name){
		File [] contents = curr.listFiles();
		File target = null;
		for(int i = 0; i < contents.length; i++){
			if(contents[i].getName().equals(name)){
				return contents[i];
			} else if (contents[i].isDirectory()){
				target = findFile(contents[i], name);
				if(target != null){
					return target;
				}
			}
		}
		return target;
	}
	
	private boolean deleteContents(File dir){
		File[] contents = dir.listFiles();
		for(int i = 0; i < contents.length; i++){
			if(!deleteContents(contents[i])){
				return false;
			}
		}
		return dir.delete();
	}

	public ArrayList<String> getContents(String root){
		ArrayList<String> contents = new ArrayList<String>();
		contents = walk(contents, new File(root));
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 1; i < contents.size(); i++){
			ret.add(getLocalPath(contents.get(i), root));
		}
		return ret;
	}
	
	private String getLocalPath(String path, String root){
		if(path.equals("/tmp/savage/HW3") && root.equals("/")){
			return "/";
		}
		File r = new File(root);
		return path.replace(r.getPath() + "/", "");
	}

	private ArrayList<String> walk(ArrayList<String> ret, File file){
		ret.add(file.getAbsolutePath());
		if(file.isDirectory()){
			File[] contents = file.listFiles();
			for(int i = 0; i < contents.length; i++){
				walk(ret, contents[i]);
			}
		}
		return ret;
	}
	
	public static void main(String[] args){
		//TODO usage
		if(args.length != 1){
			System.err.println("usage: java cs455.nfs.remote.DirectoryService <portnum>");
			return;
		}
		Scanner in = new Scanner(args[0]);
		int port = in.nextInt();
		DirectoryService dirServ = new DirectoryService(port);
		dirServ.listen();		
	}

}
