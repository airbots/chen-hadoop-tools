import java.io.*;
import java.lang.*;
import java.util.*;
import java.lang.Integer;
import java.io.BufferedReader;
public class dataeff 
{
	public static void main(String[] args) 
	{
		dataeff compete=new dataeff();
		int choice=0;
		atomInAL atom_inal=new atomInAL();
		atomInLL atom_inll=new atomInLL();
		atomInHS atom_inhs=new atomInHS();
		atomInTS atom_ints=new atomInTS();
		atomInHM atom_inhm=new atomInHM();
		atomInTM atom_intm=new atomInTM();
		atomInLHS atom_inlhs=new atomInLHS();
		System.out.println("Test the data I/O efficient:\n 1-2.ArrayList\n 3-4.LinkedList\n 5-6.HashSet\n 7-8.TreeSet\n 9-10.HashMap 11-12.TreeMap\n 13-14.LinkedHashMap");
//        InputStream is=new System.in;
		try
		{
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(reader);
		//	choice=input.readline();
			String inin=input.readLine();
			choice=Integer.parseInt(inin);
		//	input.read();
			System.out.println(choice);
		
		}
		catch (IOException e)
		{
			System.out.println("Caught exeception from reading");
		}
		switch (choice)
		{
				case 1 : {System.out.println("This is splitahead method by ArrayList");atom_inal.splitahead();break;}
				case 2 : atom_inal.spliteverytime();
				break;
				case 3 : atom_inll.splitahead();
				break;
				case 4 : atom_inll.spliteverytime();
				break;
				case 5 : System.out.println("This method is not available");
				break;
				case 6 : atom_inhs.spliteverytime();
				break;
				case 7 : System.out.println("This method is not available");
				break;
				case 8 : atom_ints.spliteverytime();
				break;
				case 9 : atom_inhm.spliteverytime();
				break;
				case 10 : System.out.println("This method is not available");
				break;
				case 11 : atom_intm.spliteverytime();
				break;
				case 12 : atom_intm.spliteverytime();System.out.println("This method is not available");
				break;
				case 13 : System.out.println("This method is not available");
				break;			
				case 14 : atom_inlhs.spliteverytime();
				break;
				default: System.out.println("This is the end!");		
		}
	}
}
class atomInAL
	{
		static String readline;
		static String everyelement="";
		               int counter=0;
		static int sequencenumber=0;
		void splitahead(){
			String everyelement="";
			ArrayList<ArrayList<String>> atom= new ArrayList<ArrayList<String>>(); 
			try
			{	FileReader fr= new FileReader("./argon.3d");
				BufferedReader br=new BufferedReader(fr);
				String readline;
				while ((readline=br.readLine())!=null)
				{
					StringTokenizer token=new StringTokenizer(readline);
					ArrayList<String> eachline=new ArrayList<String>();
					while (token.hasMoreTokens())
					{
						eachline.add(token.nextToken());
			//			System.out.println(readline);
					}
					atom.add(eachline);
				}
				for (int i=0;i<atom.size();i++)
				{
					for (int j=0;j<11;j++)
					{
			//			System.out.println(((ArrayList)atom.get(i)).get(j).toString());
						((ArrayList)atom.get(i)).get(j).toString();
						counter++;
					}
				}
			}
			catch (IOException e1)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
		void spliteverytime(){
			String everyelement="";
			ArrayList<String> atom= new ArrayList<String>();
			try
			{FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;

				while ((readline=br.readLine())!=null)
				{
					atom.add(readline);
				}
				for (int i=0;i<atom.size();i++)
				{
					StringTokenizer token2=new StringTokenizer(atom.get(i));
					while (token2.hasMoreTokens())
					{
			//			System.out.println(token2.nextToken());
						token2.nextToken();
						counter++;
					}
				}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);

		}
	}
class atomInLL
	{
	        int counter=0;
		static String readline;
		static String everyelement="";
		static int sequencenumber=0;
		void splitahead(){
			String everyelement="";
			LinkedList<LinkedList<String>> atom= new LinkedList<LinkedList<String>>();
			try
			{FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;

				while ((readline=br.readLine())!=null)
			{
				StringTokenizer token=new StringTokenizer(readline);
				LinkedList<String> eachline=new LinkedList<String>();
				while (token.hasMoreTokens())
				{
					eachline.add(token.nextToken());
				}
				atom.add(eachline);
			}
			for (int i=0;i<atom.size();i++)
			{
				for (int j=0;j<11;j++)
				{
				//   System.out.println(((LinkedList)atom.get(i)).get(j));
				     String temp= ((LinkedList)atom.get(i)).get(j).toString();
				     counter++;
				//     System.out.println(counter);
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
			
		}
		void spliteverytime(){
			String everyelement="";
			LinkedList<String> atom= new LinkedList<String>(); 
			try
			{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
	
				while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for (int i=0;i<atom.size();i++)
			{
				StringTokenizer token2=new StringTokenizer(atom.get(i));
				LinkedList<String> eachatom=new LinkedList<String>();
				while (token2.hasMoreTokens())
				{
				//	System.out.println(token2.nextToken());
					token2.nextToken();
					counter++;
				//	System.out.println(counter);
				}
			}
			}

			
			catch (IOException e3)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
	}
class atomInHS
	{ //The coordinates may be same. Then, the identical  
               int counter=0;
	
		static String readline;
		static String everyelement="";
		static int sequencenumber=0;

		void splitahead(){
			String everyelement="";
			HashSet<String> atom=new HashSet<String>();
			int sequencenumber=0;
			try
			{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
				while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for(String atm:atom)
			{
				StringTokenizer token=new StringTokenizer(atm);
				while (token.hasMoreTokens())
				{
					token.nextToken();
					counter++;
				}
			}
			}
	
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
		void spliteverytime(){
			String everyelement="";
			HashSet<String> atom=new HashSet<String>();
			int sequencenumber=0;
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for(String atm:atom)
			{
				StringTokenizer token=new StringTokenizer(atm);
				while (token.hasMoreTokens())
				{
		//			System.out.println(token.nextToken());
					token.nextToken();
					counter++;
					
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
	}
class atomInTS
	{
               int counter=0;

				static String readline;
		static String everyelement="";
		static int sequencenumber=0;
		void splitahead(){
			String everyelement="";
			TreeSet<String> atom=new TreeSet<String>();
			int sequencenumber=0;
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for(String atm:atom)
			{
				StringTokenizer token=new StringTokenizer(atm);
				while (token.hasMoreTokens())
				{
					token.nextToken();
					counter++;
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
		void spliteverytime(){
			TreeSet<String> atom=new TreeSet<String>();
			sequencenumber=0;
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for(String atm:atom)
			{
				StringTokenizer token=new StringTokenizer(atm);
				while (token.hasMoreTokens())
				{
					token.nextToken();
					counter++;
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
	}
class atomInHM
	{
               int counter=0;
		int i;

				static String readline;
		static String everyelement="";
		static int sequencenumber=0;
		void spliteverytime(){
			Map atom=new HashMap();
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			sequencenumber=0;
			
			while ((readline=br.readLine())!=null)
			{
				atom.put(sequencenumber,readline);
				sequencenumber++;
			}
			for(i=0;i<atom.size();i++) 
                        {       
                             String value = atom.get(i).toString();              
                             StringTokenizer token=new StringTokenizer(value);
                                        while (token.hasMoreTokens())
                                        {
                                            token.nextToken();
                                            counter++;
                                        }
                        }

			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);

		}
	}
class atomInTM
	{
               int counter=0;
		int i;
		static String readline;
		static String everyelement="";
		static int sequencenumber=0;
		void spliteverytime(){
			Map atom=new TreeMap();
			sequencenumber=0;
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				atom.put(sequencenumber,readline);
				sequencenumber++;
			}
			for(i=0;i<atom.size();i++) 
			{ 
					String value = atom.get(i).toString(); 
					StringTokenizer token=new StringTokenizer(value);
					while (token.hasMoreTokens())
					{
					    token.nextToken();
			  		    counter++;
					}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);

		}
	}
class atomInLHS
	{
               int counter=0;

				static String readline;
		static String everyelement="";
		static int sequencenumber=0;
		void splitahead(){
			LinkedHashSet<LinkedHashSet<String>> atom=new LinkedHashSet<LinkedHashSet<String>>();
			sequencenumber=0;
			try{
				FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				StringTokenizer token=new StringTokenizer(readline);
				LinkedHashSet<String> eachline=new LinkedHashSet();
				int seqkey=0;
		//		System.out.println(readline);
				while (token.hasMoreTokens())
				{
					String value=token.nextToken();
					eachline.add(value);
					System.out.println(value);
					seqkey++;
				}
				atom.add(eachline);
			//	System.out.println(eachline);
				sequencenumber++;
			}
			for(HashSet keys:atom)
			{
			//	System.out.println(atom);
				for(Object key : keys) 
				{
				//	System.out.println(keys);
					counter++;
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);

		}
		void spliteverytime(){
			LinkedHashSet<String> atom=new LinkedHashSet<String>();
			sequencenumber=0;
			try{
			FileReader fr= new FileReader("./argon.3d");
			BufferedReader br=new BufferedReader(fr);
			String readline;
			
			while ((readline=br.readLine())!=null)
			{
				atom.add(readline);
			}
			for(String key : atom)
			{
				StringTokenizer token=new StringTokenizer(key);
				while (token.hasMoreTokens())
				{
					token.nextToken();
					counter++;
				}
			}
			}
			catch (IOException e2)
			{
				System.out.println("Caught IOException");
			}
			System.out.println(counter);
		}
	}
