

package Class;



public class Cancion
{
//https://www.youtube.com/user/Renan6x3
    public Cancion()
    {
    }

    public Cancion(int id, String nombre, double tamanio, String direccion)
    {
        this.id = id;
        this.nombre = nombre;
        this.tamanio = tamanio;
        this.direccion = direccion;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getDireccion()
    {
        return direccion;
    }

    public String getNombre()
    {
        return nombre;
    }

    public double getTamanio()
    {
        return tamanio;
    }

    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void setTamanio(double tamanio)
    {
        this.tamanio = tamanio;
    }

    private int id;
    private String nombre;
    private double tamanio;
    private String direccion;
}
