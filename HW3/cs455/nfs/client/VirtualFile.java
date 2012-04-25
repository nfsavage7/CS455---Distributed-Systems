/* pacakge statement */
package cs455.nfs.client;

/* java imports */
import java.util.ArrayList;

/* local imports */ 
import cs455.nfs.communications.Link;

/* ************************************************************************************************************************ */
/*                                                        VirtualFile                                                       */
/*                                                     -----------------                                                    */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Virtual File. It contains metadata about a particular file in the virtual file system.                  */
/* ************************************************************************************************************************ */

public class VirtualFile{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private String name;
	private String path;
	private boolean directory;
	private boolean virtual;
	private Link link;
	private ArrayList<VirtualFile> contents;
	private VirtualFile parent;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public VirtualFile(String n){
		name = n;
		if(!name.endsWith("/")){
			path = "/" + name;
		} else {
			path = name;
		}
		directory = true;
		virtual = true;
		contents = new ArrayList<VirtualFile>();
	}

	public VirtualFile(String n, String p, boolean d, Link l){
		name = n;
		path = p;
		directory = d;
		virtual = false;
		link = l;
		contents = new ArrayList<VirtualFile>();
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */


	public String getPath(){
		return path;
	}
	
	public boolean isDirectory(){
		return directory;
	}
	
	public boolean isVirtual(){
		return virtual;
	}

	public Link getLink(){
		return link;
	}
	
	public void setLink(Link l){	
		link = l;
	}

	public void addChild(VirtualFile child){
		contents.add(child);
		child.setParent(this);
		child.setPath();
	}

	public VirtualFile getChild(String name){
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i).getName().equals(name)){
				return contents.get(i);
			}
		}
		return null;
	}

	public ArrayList<VirtualFile> getChildren(){
		return contents;
	}

	public void removeChild(String child){
		for(int i = 0; i < contents.size(); i++){
			if(contents.get(i).getName().equals(child)){
				contents.remove(contents.get(i));
				return;
			}
		}
	}

	private void setPath(){
		if(virtual){
			if(!parent.getPath().endsWith("/")){
				path = parent.getPath() + path;
			} else {
				path =  path;
			}
		} else {
			path = path.substring(1);
		}
	}

	public String getName(){
		return name;
	}

	public void setParent(VirtualFile p){
		parent = p;
	}

	public VirtualFile getParent(){
		return parent;
	}

	public ArrayList<VirtualFile> getContents(){
		return contents;
	}

	public String toString(){
		return name;
	}
}
