

package Class;


import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

// Referenced classes of package Class:
//            Cancion, ListasArray
//https://www.youtube.com/user/Renan6x3
public class Buscador
{

    public Buscador()
    {
        id = 0;
        reproduciendo = 0;
        fc = new JFileChooser();
        c = new Cancion();
        al = new ListasArray();
    }

    public JFileChooser getFc()
    {
        return fc;
    }

    public void setFc(JFileChooser fc)
    {
        this.fc = fc;
    }

    public int getReproduciendo()
    {
        return reproduciendo;
    }

    public void setReproduciendo(int reproduciendo)
    {
        this.reproduciendo = reproduciendo;
    }

    public void buscar(String filtrar, DefaultTableModel modelo)
    {
        fc.setFileSelectionMode(0);
        if(!fc.isMultiSelectionEnabled())
            fc.setMultiSelectionEnabled(true);
        FileFilter filtro = new FileNameExtensionFilter(null, new String[] {
            filtrar, "mp3"
        });
        fc.addChoosableFileFilter(filtro);
        fc.showOpenDialog(null);
        File archivosSeleccionados[] = fc.getSelectedFiles();
        for(int i = 0; i < archivosSeleccionados.length; i++)
        {
            File file = archivosSeleccionados[i];
            String direccion = file.getAbsoluteFile().toString();
            String nombre = file.getName().toString();
            double tam = file.length() / 0x100000L;
            c = new Cancion(id, nombre, tam, direccion);
            al.agregar(c);
            String listas[] = {
                c.getNombre()
            };
            refrescar(modelo);
            modelo.addRow(listas);
            id++;
        }

    }
  public void agrega(String dir, DefaultTableModel modelo)
    {
       
        File file = new File(dir);
            if(file.exists())
            {
            	 String direccion = dir;
	            String nombre = file.getName().toString();
	            double tam = file.length() / 0x100000L;
	            c = new Cancion(id, nombre, tam, direccion);
	            al.agregar(c);
	            String listas[] = {
	                c.getNombre()
	            };
	            refrescar(modelo);
	            modelo.addRow(listas);
	            id++;
            }else{
            	System.out.println ("Direccion incorrecta");
            }
          
        

    }
    void refrescar(DefaultTableModel modelo)
    {
        for(int x = modelo.getRowCount() - 1; x >= 0; x--)
            if(modelo.getValueAt(x, 0).toString().equals(""))
                modelo.removeRow(x);

    }

    public void AgregarConListas(String rutas[], DefaultTableModel modelo)
    {
        al = null;
        al = new ListasArray();
        c = null;
        c = new Cancion();
        id = 0;
        File archivosSeleccionados[] = new File[rutas.length];
        URL mediaURL = null;
        for(int i = 0; i < rutas.length; i++)
            archivosSeleccionados[i] = new File(rutas[i]);

        for(int i = 0; i < archivosSeleccionados.length; i++)
        {
            File file = archivosSeleccionados[i];
            String direccion = file.getAbsoluteFile().toString();
            String nombre = file.getName().toString();
            double tam = file.length() / 0x100000L;
            c = new Cancion(id, nombre, tam, direccion);
            al.agregar(c);
            String listas[] = {
                c.getNombre()
            };
            modelo.addRow(listas);
            id++;
        }

    }

    public void agregar_Drop(File archivosSeleccionados[], DefaultTableModel modelo)
    {
        for(int i = 0; i < archivosSeleccionados.length; i++)
        {
            File file = archivosSeleccionados[i];
            String direccion = file.getAbsoluteFile().toString();
            String nombre = file.getName().toString();
            double tam = file.length() / 0x100000L;
            c = new Cancion(id, nombre, tam, direccion);
            al.agregar(c);
            String listas[] = {
                c.getNombre()
            };
            refrescar(modelo);
            modelo.addRow(listas);
            id++;
        }

    }

    public boolean compararCon(String arg0)
    {
        return false;
    }

    private JFileChooser fc;
    private Cancion c;
    private ListasArray al;
    int id;
    int reproduciendo;
}
