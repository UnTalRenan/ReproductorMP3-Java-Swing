
package Class;

import java.util.ArrayList;
import java.util.Iterator;


// Referenced classes of package Class:
//            Cancion
//https://www.youtube.com/user/Renan6x3
public class ListasArray extends ArrayList
{

    public ArrayList getCanciones()
    {
        return canciones;
    }

    public ListasArray()
    {
        incremento = 0;
        canciones = new ArrayList();
        ca = new Cancion();
    }

    public void agregar(Cancion arg0)
    {
        canciones.add(arg0);
    }

    public void limpiar()
    {
        canciones.clear();
    }

    public Object getValor(int arg0)
    {
        return canciones.get(arg0);
    }

    public Object eliminar(int arg0)
    {
        return canciones.remove(arg0);
    }

    public Object setValor(int arg0, Cancion arg1)
    {
        return canciones.set(arg0, arg1);
    }

    public int tamaF1o()
    {
        return canciones.size();
    }

    public String getDireccionBuscar(String arg0)
    {
        Iterator iter = canciones.iterator();
        String direccion = "";
        do
        {
            if(!iter.hasNext())
                break;
            Cancion ca = (Cancion)iter.next();
            if(ca.getNombre().equals(String.valueOf(arg0)))
            {
                direccion = ca.getDireccion();
                posicion = ca.getId();
            }
        } while(true);
        return direccion;
    }

    public int getSiguiente(String arg0)
    {
        Iterator iter = canciones.iterator();
        do
        {
            if(!iter.hasNext())
                break;
            Cancion ca = (Cancion)iter.next();
            if(ca.getNombre().equals(String.valueOf(arg0)))
                posicion = ca.getId();
        } while(true);
        return posicion;
    }

    public static ArrayList canciones;
    Cancion ca;
    public static int posicion = 0;
    int incremento;

}
