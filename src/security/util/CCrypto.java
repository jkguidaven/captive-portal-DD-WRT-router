package security.util;

import java.util.Random;
import java.util.StringTokenizer;

public class CCrypto {
	
	private final static char[] CrypToCode = {
		'i' , 'j'  , 'N' , 'k' , 'l' , 'm' , 'D' , 'n' , 'J' , 'X' , 'Y' ,
		'a' , 'c'  , 'e' , 'f' , 's' , '2' , 'E' , 'H' , 'I' , 'W' , 'Z' ,
		'o' , 'p'  , 'G' , 't' , 'A' , 'h' , '9' , '6' , 'O' , 'V' , 'u' , 
		'F' , 'v'  , 'w' , 'x' , 'C' , 'y' , 'z' , 'P' , 'U' , '0' , '1' ,
		'3' , 'B'  , 'b' , '4' , '5' , 'K' , 'Q' , 'T' ,'g'  , '7' , 'L' ,
		'8' , 'd' , 'r' , 'q' , 'M' , 'R' , 'S' }; 
	
	private static int getIndex(char ch)
	{
		for(int i=0;i<62;i++)
		{
			if(ch == CrypToCode[i])
				return i;
		}
		
		return -1;
	}
	
	
	private static int getCryptoSeed()
	{
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(800) + 99; //100-999
	}
	

	private static String fuseCryptoSeed(String str)
	{
		return toHex( applyShuffleAlgo(str, Integer.toString(getCryptoSeed()) ) ,str.length() + 3 );
	}
	
	private static int[] applyShuffleAlgo(String str,String AlgorithmCode)
	{
		char[] alorithm = AlgorithmCode.toCharArray();
		
		int algo_scheme = (int)alorithm[2] - 48; 
		int shift = (algo_scheme % 2 == 0 ? 
						((int)alorithm[0]-48) + ((int)alorithm[1]-48) : 
						((int)alorithm[0]-48) - ((int)alorithm[1]-48));
		
		char[] arr_str = str.toCharArray();
		int[] appliedShuffle = new int[str.length() + 3];
		for(int i=0;i<str.length();i++)
		{
			appliedShuffle[i] = getIndex(arr_str[i]) + shift;
		}
		appliedShuffle[str.length()] = (int)alorithm[0]-48;
		appliedShuffle[str.length()+1] = (int)alorithm[1]-48;
		appliedShuffle[str.length()+2] = algo_scheme;
		
		
		
		return appliedShuffle;
	}
	
	private static String toHex(int[] shuffle_code, int length)
	{
		String hex_str = "";
		for(int i=0;i<length;i++)
		{
			hex_str += Integer.toHexString(shuffle_code[i]).toUpperCase();
			if(i!=length-1)hex_str += "-";
		}
		return new String(hex_str);
	}
	
	private static int[] toDecimal(String str)
	{
		 StringTokenizer token = new StringTokenizer(str,"-");

		 int[] shuffle_code = new int[token.countTokens()];
		 
		 for(int i=0;token.hasMoreTokens();i++)
		 {
			 shuffle_code[i] = Integer.parseInt(token.nextToken(),16);
		 }
		 return shuffle_code;
	}
	

	private static String revertShuffleAlgo(int[] shuffle_code,int length)
	{
		char[] str_array = new char[length-3];
		int algo_scheme = shuffle_code[length-1];
		int shift = (algo_scheme % 2 == 0 ? 
				shuffle_code[length-3] + shuffle_code[length-2] : 
				 shuffle_code[length-3] - shuffle_code[length-2]);
		
		for(int i=0;i<length-3;i++)
		{
			str_array[i] = CrypToCode[ shuffle_code[i] - shift];
		}
		
		return new String(str_array);
	}
	
	public static String encrypt(String str)
	{
		return fuseCryptoSeed(str);
	}
	
	public static String decrypt(String str)
	{
		return revertShuffleAlgo(toDecimal(str), new StringTokenizer(str,"-").countTokens());
	}
	
}
