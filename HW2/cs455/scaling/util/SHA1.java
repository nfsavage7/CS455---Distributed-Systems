/* pacakge statement */
package cs455.scaling.util;

/* java imports */
import java.security.MessageDigest;
import java.math.BigInteger;

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                           SHA1                                                           */
/*                                                         --------                                                         */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the SHA1 class. It computes the hash of the RandomData class                                                */
/* ************************************************************************************************************************ */

public class SHA1{

	/* **************************************************************************************************************** */
	/*                                                Static methods                                                    */
	/* **************************************************************************************************************** */
	
	/* This code was taken form the CS 455 Assignment 2 write up. See URL below */
	/* http://www.cs.colostate.edu/~cs455/CS455-HW2-ProgrammingComponent.pdf */
	public static String SHA1FromBytes(byte[] data){
		String ret = "";
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA1");
			byte[] hash = digest.digest(data);
			BigInteger hashInt = new BigInteger(1, hash);
			ret = hashInt.toString(16);
		} catch (Exception e){}
		return ret;
	}
}
