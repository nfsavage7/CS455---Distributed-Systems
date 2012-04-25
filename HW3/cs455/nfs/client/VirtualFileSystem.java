/* pacakge statement */
package cs455.nfs.client;

/* java imports */
import java.util.ArrayList;

/* local imports */ 
import cs455.nfs.communications.Link;
import cs455.nfs.wireformats.MkdirRequest;
import cs455.nfs.wireformats.RmRequest;
import cs455.nfs.wireformats.RmResponce;
import cs455.nfs.wireformats.MvRequest;

/* ************************************************************************************************************************ */
/*                                                      VirtualFileSystem                                                   */
/*                                                   -----------------------                                                */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Virtual File System. It is incharge of managing the directory structure for the ClientModule.           */
/* ************************************************************************************************************************ */

public class VirtualFileSystem{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private VirtualFile workingDir;
	private VirtualFile root;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public VirtualFileSystem(){
		workingDir = new  VirtualFile("/");
		root = workingDir;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public String pwd(){
		return workingDir.getPath();
	}

	public void mkdir(String name){
		VirtualFile dir;
		String path;
		if(!workingDir.isVirtual()){
			if(workingDir.getPath().startsWith("/")){
				path  = workingDir.getPath().substring(1);
			} else {
				path = workingDir.getPath();
			}
			MkdirRequest request = new MkdirRequest(path + "/" + name);
			Link l = workingDir.getLink();
			l.send(request);
			dir = new VirtualFile(name, workingDir.getPath() + "/" + name, true, l);
		} else {
			dir = new VirtualFile(name);
		}
		workingDir.addChild(dir);
	}

	public void rm(String dir){
		VirtualFile child = workingDir.getChild(dir);
		if(!workingDir.isVirtual()){
			String path;
			if(workingDir.getPath().startsWith("/")){
				path  = workingDir.getPath().substring(1);
			} else {
				path = workingDir.getPath();
			}	
			RmRequest request = new RmRequest(path + "/" + dir);
			Link l = workingDir.getLink();
			l.send(request);
			RmResponce responce = (RmResponce)(l.read());
			if(responce.getStatus() > 0){
				System.err.println("Could not remove directory: " + dir);
			}
		}
		workingDir.removeChild(dir);
	}

	public void ls(){
		ArrayList<VirtualFile> contents = workingDir.getContents();
		for(int i = 0; i < contents.size(); i++){
			System.out.println(contents.get(i));
		}
	}

	public void cd(String path){
		if(path.equals("..")){
			workingDir = workingDir.getParent();
			return;
		}
		ArrayList<VirtualFile> contents = workingDir.getContents();
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i).getPath().contains(path)){
				workingDir = contents.get(i);
				return;
			}
		}

	}

	private VirtualFile getParent(String path){
		VirtualFile dir = workingDir;
		while (path.indexOf("/") != -1){
			dir = dir.getChild(path.substring(0, path.indexOf("/")));
			path = path.substring(path.indexOf("/") +1);
			
		}
		return dir;
	}

	private String getName(String path){
		return path.substring(path.lastIndexOf("/")+1);
	}

	public void populate(ArrayList<String> files, byte[] isDir, Link link){
		for(int i = 0; i < files.size(); i++){
			String name = getName(files.get(i));
			VirtualFile parent = getParent(files.get(i));
			String path = workingDir + "/" + files.get(i);
			VirtualFile child;
			if(isDir[i] == 1){
				child = new VirtualFile(name, path, true, link);
			} else {
				child = new VirtualFile(name, path, false, link);
			}
			parent.addChild(child);
		}
	}

	public void mv(String file, String dest){
		if(file.startsWith("/")){
			file = file.substring(1);
		}
		String filename = file.substring(file.lastIndexOf("/")+1);
		VirtualFile src = findFile(workingDir, filename);
		if(src == null){
			src = findFile(root, filename);
		}
		VirtualFile destDir;
		if(dest.endsWith("/")){
			dest = dest.substring(0, dest.length()-1);	
		} 
		if(dest.contains("/")){
			destDir = findFile(root, dest.substring(0, dest.lastIndexOf("/")));
		} else {
			destDir = findFile(root, dest);
		}
		if (destDir.getName().equals(dest.substring(dest.lastIndexOf("/")+1))){
			dest = dest + "/" + filename;
		}
		if(src == null){
			System.err.println("vmv:error: " + file + " does not exist");
			return;
		}
		if(src.isDirectory()){
			System.err.println("vmv:error: " + file + " is a directory and cannot be moved");
			return;
		}
		Link srcLink = src.getLink();
		Link destLink = destDir.getLink();
		MvRequest request = new MvRequest(src.getPath(), destLink.getIP(), destLink.getPort(), dest);
		srcLink.send(request);
		src.getParent().removeChild(src.getName());
		destDir.addChild(src);
		src.setLink(destLink);
	}

	private VirtualFile findFile(VirtualFile curr, String name){
		ArrayList<VirtualFile> children = curr.getChildren();
		VirtualFile target = null;
		for(int i = 0; i < children.size(); i++){
			if(children.get(i).getName().equals(name)){
				return children.get(i);
			} else if(children.get(i).isDirectory()){
				target = findFile(children.get(i), name);
				if( target != null){
					return target;
				}
			}
		}
		return target;
	}
	

}
