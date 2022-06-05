/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package mp3seth;

/**
 *
 * @author Renan
 */

import Class.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import javazoom.jlgui.basicplayer.*;
import java.net.*;
import java.nio.file.Paths;
//https://www.youtube.com/user/Renan6x3
public class Principal extends JFrame implements BasicPlayerListener, ActionListener//,EscuchaMensajes
{
//https://www.youtube.com/user/Renan6x3
    String ruta_conf="src/data.txt";
    String ruta_conf_color="src/viscolor.txt";
    String ruta_listas_de_reproduccion="src/Listas/";
    Archivo a=new Archivo();
    public void actionPerformed(ActionEvent evt)
    {
    	
    	if(Button_dir==evt.getSource())
    	{
    		setDir();
    	}
        if(Button_elim == evt.getSource() && !listas.getSelectedItem().toString().equals("Nueva"))
        {
            int r = JOptionPane.showConfirmDialog(null, (new StringBuilder()).append("Esta seguro que eliminara la lista ").append(listas.getSelectedItem().toString()).toString());
            if(r == 1)
                return;
            File fichero = new File((new StringBuilder()).append(ruta_listas_de_reproduccion).append(listas.getSelectedItem().toString()).toString());
            if(fichero.delete())
            {
                JOptionPane.showMessageDialog(null, "Lista Eliminada");
                llenarCombo();
            } else
            {
                JOptionPane.showMessageDialog(null, "Error no se pudo eliminar");
            }
        }
        if(listas == evt.getSource() && !listas.getSelectedItem().toString().equals("Nueva"))
            llenarTabla();
        if(Button_siguiente == evt.getSource())
            menuItemSig(evt);
        if(Button_anterior == evt.getSource())
            menuItemAnt(evt);
        if(Button_Agregar == evt.getSource())
        {
            buscar.buscar("mp3", modelTable.getDtm());
            acomodarTabla();
        }
        if(Button_Eliminar == evt.getSource())
            try
            {
                int i[] = Table_Principal.getSelectedRows();
                for(int x = 0; x < i.length; x++)
                    modelTable.eliminarFila(i[x]);

            }
            catch(Exception e) { }
        if(Button_play == evt.getSource())
            menuItemPlay(evt);
        if(Button_stop == evt.getSource())
            try
            {
                player.Stop();
                crono.stop();
                Button_play.setEnabled(false);
                text_nombreCancion.setText("");
            }
            catch(Exception ex) { }
        if(Button_repe == evt.getSource())
        {
            String conf[] = a.traeLineas(ruta_conf);
            if(repetir)
            {
                repetir = false;
                Button_repe.setToolTipText("Repetir");
                Button_repe.setIcon(new ImageIcon("src/Iconos/repetir2.png"));
                conf[1] = "0";
            } else
            {
                repetir = true;
                Button_repe.setToolTipText("Desactivar Repetir");
                Button_repe.setIcon(new ImageIcon("src/Iconos/repetir1.png"));
                conf[1] = "1";
            }
            a.escribeLineas(ruta_conf, conf);
        }
        if(Button_alea == evt.getSource())
        {
            String conf[] = a.traeLineas(ruta_conf);
            if(aleatorio)
            {
                aleatorio = false;
                Button_alea.setToolTipText("Activar Reproduccion Aleatoria");
                Button_alea.setIcon(new ImageIcon("src/Iconos/aleatorio2.png"));
                conf[0] = "0";
            } else
            {
                aleatorio = true;
                Button_alea.setIcon(new ImageIcon("src/Iconos/aleatorio1.png"));
                conf[0] = "1";
            }
            a.escribeLineas(ruta_conf, conf);
        }
        if(Button_guardar == evt.getSource())
        {
            if(modelTable.getValor(0, 0).toString().equals(""))
                return;
            nombre = listas.getSelectedItem().toString();
            nombre = nombre.substring(0, nombre.length() - 4);
            if(listas.getSelectedItem().toString().equals("Nueva"))
                nombre = nombre();
            PrintStream DDescritor;
            try
            {
                DDescritor = new PrintStream((new StringBuilder()).append(ruta_listas_de_reproduccion).append(nombre).append(".txt").toString());
            }
            catch(Exception ex) { }
            for(int i = 0; i < Table_Principal.getRowCount(); i++)
            {
                String linea = Buscandodireccion(i);
                String original[] = a.traeLineas((new StringBuilder()).append(ruta_listas_de_reproduccion).append(nombre).append(".txt").toString());
                String temp[] = new String[original.length + 1];
                for(int x = 0; x < original.length; x++)
                    temp[x] = original[x];

                temp[original.length] = linea;
                a.escribeLineas((new StringBuilder()).append(ruta_listas_de_reproduccion).append(nombre).append(".txt").toString(), temp);
                System.out.println(Buscandodireccion(i));
            }

            if(listas.getSelectedItem().toString().equals("Nueva"))
            {
                llenarCombo();
                listas.setSelectedItem((new StringBuilder()).append(nombre).append(".txt").toString());
            }
        }
    }

    String nombre()
    {
        String nom = JOptionPane.showInputDialog("Ingrese el nombre de la Lista");
        if(nom.isEmpty())
            nombre();
        return nom;
    }

    void llenarCombo()
    {
        try
        {
            listas.removeAllItems();
        }
        catch(Exception ex) { }
        listas.addItem("Nueva");
        File directorio = new File(ruta_listas_de_reproduccion);
        
        if (!directorio.exists()) 
            directorio.mkdirs(); 
                
        File archivos[] = directorio.listFiles();
        for(int x = 0; x < archivos.length; x++)
            listas.addItem(archivos[x].getName());

        if(!listas.getSelectedItem().toString().equals("Nueva"))
            nombre = listas.getSelectedItem().toString();
        
    }

    void refrescar()
    {
        for(int x = Table_Principal.getRowCount() - 1; x >= 0; x--)
            if(Table_Principal.getValueAt(x, 0).toString().equals(""))
                modelTable.removeRow(x);

    }

    void llenarTabla()
    {
        for(int x = Table_Principal.getRowCount() - 1; x >= 0; x--)
        {
            modelTable.eliminarFila(x);
            System.out.println((new StringBuilder()).append("").append(x).toString());
        }

        String rutas[] = new String[0];
        String ruta[] = new String[0];
        try
        {
            rutas = a.traeLineas((new StringBuilder()).append(ruta_listas_de_reproduccion).append(listas.getSelectedItem().toString()).toString());
        }
        catch(Exception e) { }
        int con = 0;
        for(int i = 0; i < rutas.length; i++)
            if(!rutas[i].isEmpty())
            {
                ruta = agrandar_Arreglo(ruta);
                ruta[con] = rutas[i];
                System.out.println(ruta[con]);
                con++;
            }

        System.out.println((new StringBuilder()).append("Rutas 1 ").append(rutas.length).toString());
        System.out.println((new StringBuilder()).append("Rutas 2 ").append(ruta.length).toString());
        buscar.AgregarConListas(ruta, modelTable.getDtm());
        acomodarTabla();
    }

    void acomodarTabla()
    {
    	
    	System.out.println ("INI 1");
        String e[] = new String[0];
        try
        {
            e = new String[21 - Table_Principal.getRowCount()];
        }
        catch(Exception ex)
        {
            return;
        }
        int con = 0;
        for(int i = modelTable.getRowCount(); i < e.length; i++)
        {
            e[con] = "";
            con++;
            modelTable.agregarFilas2(e);
        }
        System.out.println ("fin 1");

    }
	 Objeto ob[]={};
     int indice=0;
    public Principal()
    {
    	
    	
        pnlEspectrometro = new JPanel();
        audioInfo = null;
        secondsAmount = 0L;
        player2 = null;
       // a = new Archivo();
        repetir = false;
        aleatorio = false;
        listas = new JComboBox();
        getContentPane().setLayout(null);
        setTitle("- A R M A D A - MP3 ");
        direccion = "";
        valor = "";
        salir = false;
        initComponents();
        init();System.out.println ("contructor");
        player.getBasicPlayer().addBasicPlayerListener(this);
        crono = new Hilo();
        espectrometro = new SpectrumTimeAnalyzer();
        espectrometro.setDisplayMode(1);
        espectrometro.setSpectrumAnalyserBandCount(80);
        espectrometro.setSpectrumAnalyserDecay(0.1F);
        int fps = 50;
        espectrometro.setFps(fps);
        espectrometro.setPeakDelay((int)((float)fps * 0.4F));
        espectrometro.setBackground(Color.black);
        espectrometro.setVisColor(getViscolor(ruta_conf_color));
        pnlEspectrometro.setBounds(270, 80, 600, 400);
        add(pnlEspectrometro);
        pnlEspectrometro.setBorder(BorderFactory.createEtchedBorder());
        pnlEspectrometro.setLayout(new BorderLayout());
        pnlEspectrometro.add(espectrometro, "Center");
        JLabel fondo = new JLabel(new ImageIcon("src/Iconos/fondo.png"));
        fondo.setBounds(0, 0, 900, 600);
        add(fondo);
        setDefaultCloseOperation(3);
        setResizable(false);
        System.out.println ("FILED DR");
        Table_Principal.getColumnModel().getColumn(0).setPreferredWidth(240);
        new FileDrop(System.out, Table_Principal, new FileDrop.Listener() {

            public void filesDropped(File files[])
            {
                buscar.agregar_Drop(files, modelTable.getDtm());
                acomodarTabla();
            }

           
        }
		);
        
        
		System.out.println ("acomodar");
        acomodarTabla();
        java.awt.Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png"));
        setIconImage(icon);
        iniMenuContextual();
        trayIcon = new TrayIcon(icon, "Reproductor mp3 1.0", null);
        setDefaultCloseOperation(3);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setVisible(true);
         confiRaiz();
//        iniciarServidor();
       System.out.println ("fin contr");
        
    }
	
	
//	ArrayList

	ArrayList<String> carpetas = new ArrayList<>();
		ArrayList<String> archivos = new ArrayList<>();
	  public void confiRaiz()
    {	 

    	carpetas.clear();
		archivos.clear();
    	
    	String conf[] = a.traeLineas(ruta_conf);
    	String Directorio =conf[3];
		File f = new File(Directorio);

		if (f.exists())
		{
			     
		    File[] ficheros = f.listFiles();
		    for (int x=0;x<ficheros.length;x++){
		    	
		    	if(ficheros[x].isDirectory())
		    	{
		    		carpetas.add(""+ficheros[x].getName());
		    	}else{
		    		archivos.add(""+ficheros[x].getName());
		    	}
		    	System.out.println(ficheros[x].getName());
		    }
		}
    	
    }
   public void confiDir()
    {	 System.out.println ("ini 2");
    	carpetas.clear();
		archivos.clear();
    	
    	String conf[] = a.traeLineas(ruta_conf);
    	String Directorio = conf[3];
		File f = new File(Directorio);
	System.out.println ("fin 2");
		if (f.exists())
		{
			     
		    File[] ficheros = f.listFiles();
		    for (int x=0;x<ficheros.length;x++){
		    	
		    	if(ficheros[x].isDirectory())
		    	{
		    		carpetas.add(""+ficheros[x].getName());
		    	}else{
		    		archivos.add(""+ficheros[x].getName());
		    	}
		    	System.out.println(ficheros[x].getName());
		    }
		}
    	
    }
     JFileChooser fc= new JFileChooser();;
    public void setDir()
    {
    	
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			String ruta="";
			
			    int returnVal = fc.showOpenDialog(this);
		
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	
		                File file = fc.getSelectedFile();
		                //This is where a real application would open the file.
		                System.out.println("seleciono: " + file.getName() + "."+file.getPath());
		                	ruta=file.getPath();           
		                	String conf[] = a.traeLineas(ruta_conf);
		        			conf[3]=ruta;
		        			a.escribeLineas(ruta_conf,conf);
		        			
		                
		            } else {
		                System.out.println("Operacion cancelada.");
		            }
    }
	public String getCarpetas()
	{
		String datos="";
			for (String nombre: carpetas)
				datos=datos+nombre+";";
			
			
			System.out.println (datos);
			
			if(datos.isEmpty())
				datos="SIN CARPETAS";
				
				return datos;
	}
	public String getArchivos()
	{
		String datos="";
			for (String nombre: archivos)
				datos=datos+nombre+";";
				
				
		
			if(datos.isEmpty())
				datos="SIN ARCHIVOS";
						
				return datos;
	}
  


   	public static Objeto[] agrandar_Arreglo(Objeto  arr[])
    {
    	Objeto  tmp[]=new Objeto [arr.length+1];
    	for(int i=0;i<arr.length;i++)
    	{
    		tmp[i]= arr[i];
    	}
    
       	return tmp;
    }
    
    

	boolean volEstatus=true;

   public String getViscolor(String path) {
        String viscolor = "";
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            archivo = new File(path);
            if (!archivo.exists()) {
                archivo = new File(ruta_conf_color);
            }
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                viscolor += (linea + "\n");
            }
        } catch (Exception e) {
            System.out.println("archivo no leido");
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return viscolor;
    }

    public void init()
    {
        try
        {
        	System.out.println ("ini ");
            player = new Reproductor();
            modelTable = new ModeloTabla();
            buscar = new Buscador();
            al = new ListasArray();
            JS_volumen.setMajorTickSpacing(20);
            JS_volumen.setMinorTickSpacing(5);
            JS_volumen.setPaintTicks(true);
            JS_volumen.setPaintLabels(true);
            JS_progreso.setValue(0);
            modelTable.nuevaColumna("                    LISTA DE CANCIONES");
            Table_Principal.setModel(modelTable.getDtm());
           
            String conf[] = a.traeLineas(ruta_conf);
             System.out.println ("Edd ");
            if(conf[0].equals("0"))
            {
                Button_alea.setIcon(new ImageIcon("src/Iconos/aleatorio2.png"));
                Button_alea.setToolTipText("Activar Reproduccion Aleatoria");
                aleatorio = false;
            } else
            {
                Button_alea.setIcon(new ImageIcon("src/Iconos/aleatorio1.png"));
                Button_alea.setToolTipText("Desactivar Reproduccion Aleatoria");
                aleatorio = true;
            }
            if(conf[1].equals("0"))
            {
                Button_repe.setIcon(new ImageIcon("src/Iconos/repetir2.png"));
                Button_repe.setToolTipText("Repetir");
                repetir = false;
            } else
            {
                Button_repe.setIcon(new ImageIcon("src/Iconos/repetir1.png"));
                Button_repe.setToolTipText("Desactivar Repetir");
                repetir = true;
            }
            JS_volumen.setValue(Integer.parseInt(conf[2]));
            
            /*
            Button_alea.setIcon(new ImageIcon("Iconos/aleatorio2.png"));
                Button_alea.setToolTipText("Activar Reproduccion Aleatoria");
                aleatorio = false;
                
                   Button_repe.setIcon(new ImageIcon("Iconos/repetir1.png"));
                Button_repe.setToolTipText("Desactivar Repetir");
                repetir = true;*/
        }
        catch(Exception ex) {
        	System.out.println ("E "+ex); }
    }

    private void initComponents()
    {
        JS_volumen = new JSlider();
        jScrollPane1 = new JScrollPane();
        Table_Principal = new JTable();
        Button_play = new JButton();
        Button_stop = new JButton();
        Button_siguiente = new JButton();
        Button_anterior = new JButton();
        Button_Agregar = new JButton();
        Button_Eliminar = new JButton();
        Button_alea = new JButton();
        Button_repe = new JButton();
        Button_guardar = new JButton();
        Button_elim = new JButton();
        JS_progreso = new JSlider();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        label_crono = new JLabel();
        text_nombreCancion = new JLabel();
        jMenuBar1 = new JMenuBar();
        Menu_archivo = new JMenu();
        Menu_archivo_agregar = new JMenuItem();
        jMenuItem1 = new JMenuItem();
        Menu_configuracion = new JMenu();
        Menu_config_apariencias = new JMenu();
        eticombo = new JLabel();
        setDefaultCloseOperation(3);
        setCursor(new Cursor(0));
        setIconImages(null);
        label_crono.setBounds(90, 50, 100, 30);
        label_crono.setForeground(Color.WHITE);
        label_crono.setToolTipText("Tiempo de reproduccion, Cancion actual");
        add(label_crono);
        eticombo.setBounds(10, 80, 70, 30);
        eticombo.setForeground(Color.WHITE);
        add(eticombo);
        listas.setBounds(eticombo.getX() + eticombo.getWidth(), eticombo.getY(), 140, 30);
        listas.addActionListener(this);
        listas.setCursor(new Cursor(12));
        add(listas);
        llenarCombo();
        Button_elim.setBounds(listas.getX() + listas.getWidth() + 5, listas.getY(), 30, 30);
        Button_elim.addActionListener(this);
        Button_elim.setCursor(new Cursor(12));
        Button_elim.setIcon(new ImageIcon("src/Iconos/borrar.png"));
        Button_elim.setToolTipText("Eliminar Lista de Reproduccion Seleccionada");
        add(Button_elim);
        JS_progreso.setBounds(270, 530, 600, 20);
        JS_progreso.setOpaque(false);
        JS_progreso.setCursor(new Cursor(12));
        add(JS_progreso);
        Button_play.setBounds(500, 480, 60, 60);
        Button_play.addActionListener(this);
        Button_play.setContentAreaFilled(false);
        Button_play.setCursor(new Cursor(12));
        add(Button_play);
        Button_stop.setBounds(400, 490, 40, 40);
        Button_stop.addActionListener(this);
        Button_stop.setContentAreaFilled(false);
        Button_stop.setToolTipText("Detener la cancion completamente");
        Button_stop.setCursor(new Cursor(12));
        add(Button_stop);
        Button_anterior.setBounds(460, 490, 40, 40);
        Button_anterior.setIcon(new ImageIcon("src/Iconos/mini_player_anterior.png"));
        Button_anterior.addActionListener(this);
        Button_anterior.setContentAreaFilled(false);
        Button_anterior.setToolTipText("Pasar a la cancion anterior");
        Button_anterior.setCursor(new Cursor(12));
        add(Button_anterior);
        Button_siguiente.setBounds(560, 490, 40, 40);
        Button_siguiente.setIcon(new ImageIcon("src/Iconos/mini_player_siguiente.png"));
        Button_siguiente.addActionListener(this);
        Button_siguiente.setContentAreaFilled(false);
        Button_siguiente.setToolTipText("Pasar la cancion siguiente");
        Button_siguiente.setCursor(new Cursor(12));
        add(Button_siguiente);
        Button_Agregar.setBounds(20, 490, 100, 30);
        Button_Agregar.setIcon(new ImageIcon("src/Iconos/agregar.png"));
        Button_Agregar.addActionListener(this);
        Button_Agregar.setContentAreaFilled(false);
        Button_Agregar.setCursor(new Cursor(12));
        Button_Agregar.setToolTipText("Agregar una cancion a la lista de reproduccion");
        Button_Agregar.setBorder(BorderFactory.createBevelBorder(0));
        add(Button_Agregar);
        Button_Eliminar.setIcon(new ImageIcon("src/Iconos/eliminar.png"));
        Button_Eliminar.setBounds(140, 490, 100, 30);
        Button_Eliminar.addActionListener(this);
        Button_Eliminar.setContentAreaFilled(false);
        Button_Eliminar.setCursor(new Cursor(12));
        Button_Eliminar.setToolTipText("Eliminar una cancion de la lista de reproduccion");
        Button_Eliminar.setBorder(BorderFactory.createBevelBorder(0));
        add(Button_Eliminar);
        Button_alea.setBounds(60, 530, 40, 40);
        Button_alea.addActionListener(this);
        Button_alea.setContentAreaFilled(false);
        Button_alea.setCursor(new Cursor(12));
        Button_alea.setBorder(BorderFactory.createBevelBorder(0));
        add(Button_alea);
        Button_repe.setBounds(Button_alea.getX() + Button_alea.getWidth(), 530, 40, 40);
        Button_repe.addActionListener(this);
        Button_repe.setContentAreaFilled(false);
        Button_repe.setCursor(new Cursor(12));
        Button_repe.setBorder(BorderFactory.createBevelBorder(0));
        add(Button_repe);
        Button_guardar.setBounds(Button_repe.getX() + Button_repe.getWidth(), 530, 40, 40);
        Button_guardar.setIcon(new ImageIcon("src/Iconos/guardar.png"));
        Button_guardar.addActionListener(this);
        Button_guardar.setContentAreaFilled(false);
        Button_guardar.setToolTipText("Guardar Lista de Reproduccion");
        Button_guardar.setCursor(new Cursor(12));
        Button_guardar.setBorder(BorderFactory.createBevelBorder(0));
        add(Button_guardar);
        text_nombreCancion.setBounds(300, 60, 500, 20);
        text_nombreCancion.setForeground(Color.WHITE);
        add(text_nombreCancion);
        jLabel1.setBounds(100, 0, 300, 40);
        add(jLabel1);
        JS_volumen.setBounds(620, 480, 150, 50);
        JS_volumen.setOpaque(false);
        JS_volumen.setForeground(Color.WHITE);
        JS_volumen.setCursor(new Cursor(12));
        JS_volumen.setToolTipText("Volumen ");
        add(JS_volumen);
        
        	Button_dir=new JButton("..");
			Button_dir.setBounds(10,10,30,30);
			Button_dir.addActionListener(this);
			add(Button_dir);
			
        JS_volumen.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt)
            {
                JS_volumenStateChanged(evt);
            }


		
           // final Principal this$0;
         //   final Principal this$0$;


// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
        }
);
        Table_Principal.setModel(new DefaultTableModel(new Object[0][], new String[0]));
        Table_Principal.setAutoResizeMode(0);
        Table_Principal.setColumnSelectionAllowed(false);
        Table_Principal.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt)
            {
                Table_PrincipalMouseClicked(evt);
            }

            //final Principal this$0;
           // final Principal this$0$;


// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
        }
);
        jScrollPane1.setViewportView(Table_Principal);
        jScrollPane1.setBounds(10, 110, 250, 370);
        add(jScrollPane1);
        jLabel4.setBounds(790, 550, 95, 22);
        jLabel4.setForeground(Color.WHITE);
        add(jLabel4);
        Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_playCHICOOTRO.png")));
        //Button_play.setEnabled(false);
        Button_stop.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_stop1.png")));
        JS_progreso.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent evt)
            {
                JS_progresoStateChanged(evt);
            }

          //  final Principal this$0;
          //  final Principal this$0$;


// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
        }
);
        JS_progreso.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent evt)
            {
                JS_progresoMouseDragged(evt);
            }

            public void mouseMoved(MouseEvent evt)
            {
                JS_progresoMouseMoved(evt);
            }

          //  final Principal this$0;
         //   final Principal this$0$;


// JavaClassFileOutputException: Invalid index accessing method local variables table of <init>
        }
);
        jLabel1.setFont(new Font("DejaVu Sans", 1, 25));
        jLabel1.setText("");
        jLabel2.setFont(new Font("Tahoma", 0, 18));
        jLabel2.setText("seth");
        jLabel3.setFont(new Font("DejaVu Sans", 1, 14));
        jLabel3.setText("Ingenieria Sistemas");
        jLabel4.setText("powered by RC");
        jLabel4.setFont(new Font("DejaVu Sans", 1, 11));
        jLabel4.setBorder(BorderFactory.createBevelBorder(0));
        eticombo.setText("Listas Repr");
        eticombo.setFont(new Font("DejaVu Sans", 1, 11));
        label_crono.setFont(new Font("DejaVu Sans", 0, 20));
        label_crono.setHorizontalAlignment(0);
        label_crono.setText("0:0:0");
        label_crono.setBorder(BorderFactory.createBevelBorder(0));
        text_nombreCancion.setBorder(BorderFactory.createBevelBorder(0));
        eticombo.setBorder(BorderFactory.createBevelBorder(0));
    }

    private void Table_PrincipalMouseClicked(MouseEvent evt)
    {
        if(evt.getClickCount() == 2)
        {
        	cargarP();
        }
    }
	public void cargarP()
	{
		   try
            {
                fila = Table_Principal.getSelectedRow();
                if(fila==-1)
                	fila=0;
                columna = 0;
                valor = (String)Table_Principal.getValueAt(fila, columna);
                text_nombreCancion.setText(valor);
                setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(valor).toString());
                direccion = al.getDireccionBuscar(valor);
                id = al.getSiguiente(valor);
                player.AbrirFichero(direccion);
                player.Play();
                salir = true;
                crono = new Hilo();
                crono.start();
                player.volumen(JS_volumen);
                Button_play.setEnabled(true);
                Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
                Table_Principal.changeSelection(id, columna, false, false);
//                multiEnviar();
                
            }
            catch(Exception ex) { }
	}
	public void cargar2(String nom)
	{
		   try
            {
               
                valor =nom;
                text_nombreCancion.setText(valor);
                setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(valor).toString());
                direccion = al.getDireccionBuscar(valor);
                id = al.getSiguiente(valor);
                player.AbrirFichero(direccion);
                player.Play();
                salir = true;
                crono = new Hilo();
                crono.start();
                player.volumen(JS_volumen);
                Button_play.setEnabled(true);
                Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
                Table_Principal.changeSelection(id, columna, false, false);
//                multiEnviar();
                
            }
            catch(Exception ex) { }
	}
    private void JS_volumenStateChanged(ChangeEvent evt)
    {
    		System.out.println("VOLUMEN");
        player.volumen(JS_volumen);
        String conf[] = a.traeLineas(ruta_conf);
        conf[2] = (new StringBuilder()).append("").append(JS_volumen.getValue()).toString();
        a.escribeLineas(ruta_conf, conf);
       	
       	System.out.println("VOL:"+conf[2]);
        
        
    }
    /*
	public void volumen(int vol)
	{
		JS_volumen.setValue(vol);
		 player.volumen(JS_volumen);
      //  String conf[] = a.traeLineas(ruta_conf);
      //  conf[2] = (new StringBuilder()).append("").append(JS_volumen.getValue()).toString();
     //   a.escribeLineas(ruta_conf, conf);
	}*/
    private void JS_progresoStateChanged(ChangeEvent changeevent1)
    {
    	
    //	System.out.println ("//////////////////////////////////////PROGRESOSTATECHANGE ");
    }

    private void JS_progresoMouseMoved(MouseEvent mouseevent1)
    {
    //	System.out.println ("//////////////////////////////////////PROGRESOSTATECHANGE MOVE ");
    }

    private void JS_progresoMouseDragged(MouseEvent evt)
    {
        try
        {
            player.getBasicPlayer().seek(JS_progreso.getValue());
        }
        catch(BasicPlayerException ex) { }
    }

    public void opened(Object arg0, Map arg1)
    {
        if(arg1.containsKey("audio.length.bytes"))
        {
            bytesLength = Double.parseDouble(arg1.get("audio.length.bytes").toString());
            JS_progreso.setMajorTickSpacing((int)bytesLength);
            JS_progreso.setMaximum((int)bytesLength);
        }
        audioInfo = arg1;
        bytesLength = 0.0D;
        if(arg1.containsKey("audio.length.bytes"))
            bytesLength = Long.parseLong(arg1.get("audio.length.bytes").toString());
    }

    public void progress(int bytesread, long microseconds, byte pcmdata[], Map properties)
    {
        float progressUpdate = (float)(((double)((float)bytesread * 1.0F) / bytesLength) * 1.0D);
        int progressNow = (int)(bytesLength * (double)progressUpdate);
        JS_progreso.setValue(progressNow);
        int cal = (int)((double)(bytesread * 100) / bytesLength);
        String porcentaje = (new StringBuilder()).append(String.valueOf(cal)).append("%").toString();
        JS_progreso.setToolTipText(porcentaje);
        label_crono.setText(crono.getCronoText());
        long total = -1L;
        if(total <= 0L)
            total = Math.round(getTimeLengthEstimation(audioInfo) / 1000L);
        if(total <= 0L)
            total = -1L;
        float progress = -1F;
        if(bytesread > 0 && bytesLength > 0.0D)
            progress = (float)(((double)((float)bytesread * 1.0F) / bytesLength) * 1.0D);
        if(audioInfo.containsKey("basicplayer.sourcedataline") && espectrometro != null)
        {
            espectrometro.writeDSP(pcmdata);
            espectrometro.writeDSP(pcmdata);
        }
        if(audioInfo.containsKey("audio.type"))
        {
            String audioformat = (String)audioInfo.get("audio.type");
            if(audioformat.equalsIgnoreCase("mp3"))
            {
                if(total > 0L)
                    secondsAmount = (long)((float)total * progress);
                else
                    secondsAmount = -1L;
            } else
            if(audioformat.equalsIgnoreCase("wave"))
                secondsAmount = (long)((float)total * progress);
            else
                secondsAmount = Math.round(microseconds / 0xf4240L);
            if(secondsAmount < 0L)
                secondsAmount = Math.round(microseconds / 0xf4240L);
        }
    }

    public long getTimeLengthEstimation(Map properties)
    {
        long milliseconds = -1L;
        int byteslength = -1;
        if(properties != null)
        {
            if(properties.containsKey("audio.length.bytes"))
                byteslength = ((Integer)properties.get("audio.length.bytes")).intValue();
            if(properties.containsKey("duration"))
            {
                milliseconds = (int)((Long)properties.get("duration")).longValue() / 1000;
            } else
            {
                int bitspersample = -1;
                int channels = -1;
                float samplerate = -1F;
                int framesize = -1;
                if(properties.containsKey("audio.samplesize.bits"))
                    bitspersample = ((Integer)properties.get("audio.samplesize.bits")).intValue();
                if(properties.containsKey("audio.channels"))
                    channels = ((Integer)properties.get("audio.channels")).intValue();
                if(properties.containsKey("audio.samplerate.hz"))
                    samplerate = ((Float)properties.get("audio.samplerate.hz")).floatValue();
                if(properties.containsKey("audio.framesize.bytes"))
                    framesize = ((Integer)properties.get("audio.framesize.bytes")).intValue();
                if(bitspersample > 0)
                    milliseconds = (int)((1000F * (float)byteslength) / (samplerate * (float)channels * (float)(bitspersample / 8)));
                else
                    milliseconds = (int)((1000F * (float)byteslength) / (samplerate * (float)framesize));
            }
        }
        return milliseconds;
    }

    public void stateUpdated(BasicPlayerEvent arg0)
    {
    	
    	System.out.println ("ACTUALIZANDO:"+arg0.getCode());
      /*  if(arg0.getCode() != 8)
        {
        }
           // break MISSING_BLOCK_LABEL_309;
        anterior = fila;
        if(aleatorio)
        {
        }
          //  break MISSING_BLOCK_LABEL_134;
        if(id + 2 <= Table_Principal.getRowCount())
        {
            id++;
            if(Table_Principal.getValueAt(id, columna).toString().equals(""))
            {   if(repetir)
                    id = 0;
                else
                    return;
        	}
           // break MISSING_BLOCK_LABEL_185;
        }*/
      /*  try
        {
            if(repetir)
            {
                if(Table_Principal.getValueAt(id, columna).toString().equals(""))
                    id = 0;
            } else
            {
                return;
            }
           // break MISSING_BLOCK_LABEL_185;
        }
        catch(Exception ex) { }*/
       // break MISSING_BLOCK_LABEL_309;
      /*  do
        {
            fila = (int)(Math.random() * (double)Table_Principal.getRowCount());
            id = fila;
        } while(Table_Principal.getValueAt(id, columna).toString().equals(""));
        crono.stop();*/
       /* try{
        	  player.AbrirFichero(Buscandodireccion(id));
        text_nombreCancion.setText(buscarNombre(id));
        setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(buscarNombre(id)).toString());
        player.Play();
        	
        }catch(Exception e){
        }
      
        crono = new Hilo();
        crono.start();
        player.volumen(JS_volumen);
        Table_Principal.changeSelection(id, columna, false, false);*/
       // break MISSING_BLOCK_LABEL_309;
       int state = arg0.getCode();
       if(state==8 )
       {
       		int temID=(id+1);
       		String dat=Table_Principal.getValueAt(temID,0).toString();	
       		 if(id + 1 >= Table_Principal.getRowCount() || dat.isEmpty())
        	{
        		id=0;
        		if(repetir)
        		{
        			
		        	try
		            {
		                fila =0;
		                columna =0;
		                valor = (String)Table_Principal.getValueAt(fila, columna);
		                text_nombreCancion.setText(valor);
		                setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(valor).toString());
		                direccion = al.getDireccionBuscar(valor);
		                id = al.getSiguiente(valor);
		                player.AbrirFichero(direccion);
		                player.Play();
		                salir = true;
		                crono = new Hilo();
		                crono.start();
		                player.volumen(JS_volumen);
		                Button_play.setEnabled(true);
		                Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
		                Table_Principal.changeSelection(id, columna, false, false);
		            }
		            catch(Exception ex) { }
        			
        		}	
        			
        	}else{
        		siguiente();
        	}
//       	   multiEnviar();
       }
      
       
        
        Object obj = arg0.getDescription();
        if(state != 8)
            if(state == 2)
            {
                if(audioInfo.containsKey("basicplayer.sourcedataline") && espectrometro != null)
                {
                    espectrometro.setupDSP((SourceDataLine)audioInfo.get("basicplayer.sourcedataline"));
                    espectrometro.startDSP((SourceDataLine)audioInfo.get("basicplayer.sourcedataline"));
                    espectrometro.setupDSP((SourceDataLine)audioInfo.get("basicplayer.sourcedataline"));
                    espectrometro.startDSP((SourceDataLine)audioInfo.get("basicplayer.sourcedataline"));
                }
            } else
            if(state != 6 && state != 7)
                if(state == 0)
                {
                    if((obj instanceof URL) || !(obj instanceof InputStream));
                } else
                if(state == 3 && espectrometro != null)
                {
                    espectrometro.stopDSP();
                    espectrometro.repaint();
                    espectrometro.stopDSP();
                    espectrometro.repaint();
                }
        return;
    }

    public String Buscandodireccion(int id)
    {
        String dir = "";
        Iterator it = ListasArray.canciones.iterator();
        do
        {
            if(!it.hasNext())
                break;
            Cancion object = (Cancion)it.next();
            if(object.getId() == id)
                dir = object.getDireccion();
        } while(true);
        return dir;
    }

    public String buscarNombre(int id)
    {
        String nombre = "";
        Iterator it = ListasArray.canciones.iterator();
        do
        {
            if(!it.hasNext())
                break;
            Cancion object = (Cancion)it.next();
            if(object.getId() == id)
                nombre = object.getNombre();
        } while(true);
        return nombre;
    }

    public void setController(BasicController basiccontroller1)
    {
    }

    private void iniMenuContextual()
    {
        popupContextual = new JPopupMenu();
        menuItemPlay = new JMenuItem(new ImageIcon("botones2/restaurar.gif"));
        separator = new JSeparator();
        menuItemSig = new JMenuItem(new ImageIcon("botones2/salir.gif"));
        separator = new JSeparator();
        menuItemAnt = new JMenuItem(new ImageIcon("botones2/salir.gif"));
        separator = new JSeparator();
        menuItemSalir = new JMenuItem(new ImageIcon("botones2/salir.gif"));
        if(player.getBasicPlayer().getStatus() == 0)
            menuItemPlay.setText("Pausa");
        else
            menuItemPlay.setText("Reproducir");
        menuItemPlay.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt)
            {
                menuItemPlay(evt);
            }

           /* final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
        }
);
        popupContextual.add(menuItemPlay);
        popupContextual.add(separator);
        popupContextual.add(menuItemSig);
        popupContextual.add(separator);
        popupContextual.add(menuItemAnt);
        popupContextual.add(separator);
        popupContextual.add(menuItemSalir);
        menuItemSig.setText("Siguiente >");
        menuItemSig.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt)
            {
                menuItemSig(evt);
            }

           /* final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
        }
);
        menuItemAnt.setText("< Anterior");
        menuItemAnt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt)
            {
                menuItemAnt(evt);
            }

          /*  final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
        }
);
        menuItemSalir.setText("Salir");
        menuItemSalir.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt)
            {
                menuItemSalirActionPerformed(evt);
            }

         /*   final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
        }
);
        setDefaultCloseOperation(3);
        setName("Menu");
        setResizable(false);
        addWindowStateListener(new WindowStateListener() {

            public void windowStateChanged(WindowEvent evt)
            {
                EstadoCambiado(evt);
            }

         /*  final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
        }
);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, 32767));
        pack();
    }

    private void EstadoCambiado(WindowEvent evt)
    {
        if(evt.getNewState() == 1)
        {
            if(player.getBasicPlayer().getStatus() == 0)
                menuItemPlay.setText("Pausa");
            else
                menuItemPlay.setText("Reproducir");
            setState(0);
            setVisible(false);
            if(SystemTray.isSupported())
            {
                tray = SystemTray.getSystemTray();
                MouseListener mouseListener = new MouseListener() {

                    public void mouseClicked(MouseEvent e)
                    {
                        MouseEvent _tmp = e;
                        if(e.getButton() == 1)
                        {
                            String estado = "";
                            String nom = text_nombreCancion.getText();
                            if(player.getBasicPlayer().getStatus() == 0)
                                estado = "Reproduciendo";
                            if(player.getBasicPlayer().getStatus() == 1)
                                estado = "En Pausa";
                            if(player.getBasicPlayer().getStatus() == 2)
                            {
                                estado = "En Stop";
                                nom = "Zombie Team 1.0";
                            }
                            trayIcon.displayMessage(estado, nom, java.awt.TrayIcon.MessageType.INFO);
                        }
                    }

                    public void mouseEntered(MouseEvent mouseevent)
                    {
                    }

                    public void mouseExited(MouseEvent mouseevent)
                    {
                    }

                    public void mousePressed(MouseEvent mouseevent)
                    {
                    }

                    public void mouseReleased(MouseEvent e)
                    {
                        if(e.isPopupTrigger())
                        {
                            popupContextual.setLocation(e.getX(), e.getY());
                            popupContextual.setInvoker(popupContextual);
                            popupContextual.setVisible(true);
                        }
                    }

/*                final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
                }
;
                trayIcon.setImageAutoSize(true);
                trayIcon.addMouseListener(mouseListener);
                trayIcon.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e)
                    {
                        menuRestaurar(e);
                    }

              /*      final Principal this$0;

            
            {
                this$0 = Principal.this;
                super();
            }*/
                }
);
                try
                {
                    tray.add(trayIcon);
                }
                catch(AWTException e)
                {
                    System.err.println("No se pudo agregar el icono a la barra tray");
                    setVisible(true);
                }
            }
        }
    }

    private void menuRestaurar(ActionEvent evt)
    {
        setVisible(true);
        toFront();
        tray.remove(trayIcon);
    }

    private void menuItemSalirActionPerformed(ActionEvent evt)
    {
        int r = JOptionPane.showConfirmDialog(null, "Desesa cerrar el reprodcutor?");
        if(r == 0)
            System.exit(0);
    }

    private void menuItemSig(ActionEvent evt)
    {
       siguiente();
    }

    private void menuItemAnt(ActionEvent evt)
    {
    	anterior();
    }

    private void menuItemPlay(ActionEvent evt)
    {
       play();  
    }
	public void play()
	{
		
		System.out.println ("ESTADO "+player.basicPlayer.getStatus());
			if(player.basicPlayer.getStatus()==-1)
			{	cargarP();	
				return;
			}
	
		 if(!salir)
            try
            {
                player.Continuar();
                salir = true;
                crono.reanudar();
                menuItemPlay.setText("Pausa");
                player.volumen(JS_volumen);
                Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
//                	 multiEnviar();
            }
            catch(Exception ex) { }
        else
            try
            {
                player.Pausa();
                menuItemPlay.setText("Reproducir");
                salir = false;
                crono.pause();
                Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_playCHICOOTRO.png")));
//                	 multiEnviar();
            }
            catch(Exception ex) { }
		
	
	
	}
	public void anterior()
	{
		
		    try
        {
            if(aleatorio)
            {
                if(anterior == fila)
                {
                    valor = (String)Table_Principal.getValueAt(--fila, columna);
                    Table_Principal.changeSelection(fila, columna, false, false);
                } else
                {
                    valor = (String)Table_Principal.getValueAt(anterior, columna);
                    Table_Principal.changeSelection(anterior, columna, false, false);
                }
            } else
            {
                valor = (String)Table_Principal.getValueAt(--fila, columna);
                Table_Principal.changeSelection(fila, columna, false, false);
            }
            text_nombreCancion.setText(valor);
            setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(valor).toString());
            direccion = al.getDireccionBuscar(valor);
            id = al.getSiguiente(valor);
            player.AbrirFichero(direccion);
            player.Play();
            salir = true;
            crono = new Hilo();
            crono.start();
            player.volumen(JS_volumen);
            Button_play.setEnabled(true);
            Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
        }
        catch(Exception e) { }
        
//         multiEnviar();
	}
	public void siguiente()
	{
		
		 try
        {
            anterior = fila;
            if(!aleatorio)
            {
                if(fila + 2 <= Table_Principal.getRowCount())
                {
                    valor = (String)Table_Principal.getValueAt(++fila, columna);
                    if(valor.equals(""))
                    {
                        fila = 0;
                        valor = (String)Table_Principal.getValueAt(fila, columna);
                    }
                } else
                {
                    fila = 0;
                    valor = (String)Table_Principal.getValueAt(fila, columna);
                }
            } else
            {
                do
                {
                    fila = (int)(Math.random() * (double)Table_Principal.getRowCount());
                    valor = (String)Table_Principal.getValueAt(fila, columna);
                } while(valor.equals(""));
            }
            text_nombreCancion.setText(valor);
            setTitle((new StringBuilder()).append("- A R M A D A - MP3 ").append(valor).toString());
            direccion = al.getDireccionBuscar(valor);
            id = al.getSiguiente(valor);
            player.AbrirFichero(direccion);
            player.Play();
            salir = true;
            crono = new Hilo();
            crono.start();
            player.volumen(JS_volumen);
            Table_Principal.changeSelection(fila, columna, false, false);
            Button_play.setEnabled(true);
            Button_play.setIcon(new ImageIcon(getClass().getResource("/Iconos/mini_player_pause.png")));
        }
        catch(Exception e) { }
//		 multiEnviar();
	}
        
    public static void main(String args[])
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(UnsupportedLookAndFeelException e) { }
        catch(ClassNotFoundException e) { }
        catch(InstantiationException e) { }
        catch(IllegalAccessException e) { }
        new Principal();
    }

    public static String[] agrandar_Arreglo(String arr[])
    {
        String tmp[] = new String[arr.length + 1];
        for(int i = 0; i < arr.length; i++)
            tmp[i] = arr[i];

        return tmp;
    }

    JPanel pnlEspectrometro;
    private SpectrumTimeAnalyzer espectrometro;
    private Map audioInfo;
    private long secondsAmount;
    private BasicPlayer player2;
    int anterior;
    
    String nombre;
    private JMenuItem menuItemPlay;
    private JMenuItem menuItemSig;
    private JMenuItem menuItemAnt;
    private JMenuItem menuItemSalir;
    private JPopupMenu popupContextual;
    private JSeparator separator;
    private SystemTray tray;
    private final TrayIcon trayIcon;
    ModeloTabla modelTable;
    Buscador buscar;
    Reproductor player;
    ListasArray al;
    double bytesLength;
    String direccion;
    int id;
    String valor;
    Hilo crono;
    boolean salir;
    int fila;
    int columna;
     private JButton Button_dir;
    private JButton Button_play;
    private JButton Button_stop;
    private JButton Button_Agregar;
    private JButton Button_Eliminar;
    private JButton Button_siguiente;
    private JButton Button_anterior;
    private JButton Button_alea;
    private JButton Button_repe;
    private JButton Button_guardar;
    private JButton Button_elim;
    private JSlider JS_progreso;
    private JSlider JS_volumen;
    private JMenu Menu_archivo;
    private JMenuItem Menu_archivo_agregar;
    private JMenu Menu_config_apariencias;
    private JMenu Menu_configuracion;
    private JTable Table_Principal;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JScrollPane jScrollPane1;
    private JLabel label_crono;
    private JLabel text_nombreCancion;
    private boolean repetir;
    private boolean aleatorio;
    private JLabel eticombo;
    private JComboBox listas;




}

