package mp3seth; 

/**
 *
 * @author //https://www.youtube.com/user/Renan6x3
 */
 

import java.io.*;

public class Archivo
{
	//https://www.youtube.com/user/Renan6x3
	String info[]=new String [0];
	String file="";

    public Archivo()
    {
    }

    int size(String file)
    {
        File input=null;
        FileReader in=null;
        int r=0;

        try
        {
            input= new File(file);
            in=new FileReader(input);

            int c;
            do
            {
                c=in.read();
                if(c==10)
                    r++;
            }while(c!=-1);
            in.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return 0;
        }
        return r;
    }

    int size2(String file)
    {
        File input=null;
        FileReader in=null;
        int r=0;
        int espe=0;
        try
        {
            input= new File(file);
            in=new FileReader(input);

            int c;
            do
            {
                c=in.read();
                if(c==32)
                    r++;
                if(c==10)
                	espe++;
            }while(c!=-1);
            in.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return 0;
        }
        return r+espe;
    }

    int size3(String file)
    {
        File input=null;
        FileReader in=null;
        int r=0;
        int espe=0;
        try
        {
            input= new File(file);
            in=new FileReader(input);

            int c;
            do
            {
                c=in.read();
                if(c==10)
                    r++;
            }while(c!=-1);
            in.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return 0;
        }
        return r;
    }

    String[] traeLineas(String file)
    {
        File input=null;
        FileReader in=null;
        String temp[]=new String [0];
        String info[]=new String [0];

        try
        {
            input= new File(file);
            in=new FileReader(input);

            int Size=size(file);
            int c=0;

            info=new String[Size];
			System.out.print(""+Size);
            for(int i=0;i<Size;i++)
            {
                info[i]="";
                do
                {
                    c=in.read();
                    if(c!=10&&c!=13)
                    {
                        info[i]+=((char)c)+"";
                    }
                }while(c!=13);
            };
            in.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            return info;
        }
        return info;
    }

    String [] traeInfo(String file)
    {
    	String arre[]=new String [size2(file)];
    	String acum="";

    	try
    	{
    		File x=new File(file);
    		FileReader RX=new FileReader(x);

    		int c;
    		int cont=0;
    		while(true)
    		{
    			c=RX.read();
    			if(c!=32&&c!=10&&c!=13&&c!=-1)
    			{
    				acum+=(char)c +"";
    			}

				if(c==32||c==10)
				{
					arre[cont]=new String(acum);
    				cont++;
    				acum="";
				}

    			if(c==-1)
    			{
    				break;
    			}
    		}
    	}catch(Exception e)
		{
			System.out.println (e);
			return null;
		}
		return arre;
    }


    /*nodo traeInformacion(String file)
    {
    	String acum="";
    	nodo nodo_x=null;

       	try
    	{
    		File x=new File(file);
    		FileReader RX=new FileReader(x);

    		int c;
    		int cont=0;
    		while(true)
    		{
    			c=RX.read();
    			if(c!=32&&c!=10&&c!=13&&c!=-1)
    			{
    				acum+=(char)c +"";
    			}

				if(c==32||c==10)
				{
    				nodo_x=crea(acum, nodo_x);
    				acum="";
				}

    			if(c==-1)
    			{
    				break;
    			}
    		}
    	}catch(Exception e)
		{
			System.out.println (e);
			return null;
		}
		return nodo_x;
    }*/

    void escribeLineas(String file,String info[])
    {
        File output;
        FileWriter out;
        try
        {
            output=new File(file);
            out=new FileWriter(output);

            for(int i=0;i<info.length;i++)
            {
                for(int x=0;x<info[i].length();x++)
                {
                    out.write((int)info[i].charAt(x));
                }
                //out.write(' ');
                //System.out.println(info[i]);
              	out.write((char)13+"\n");
            }
            out.close();
        }
        catch(Exception e)
        {
            System.out.print(e);
        }
    }
}

