
package mp3seth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.TooManyListenersException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
//https://www.youtube.com/user/Renan6x3
public class FileDrop
{
    public static class TransferableObject
        implements Transferable
    {
        public static interface Fetcher
        {

            public abstract Object getObject();
        }


        public DataFlavor getCustomDataFlavor()
        {
            return customFlavor;
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            if(customFlavor != null)
                return (new DataFlavor[] {
                    customFlavor, DATA_FLAVOR, DataFlavor.stringFlavor
                });
            else
                return (new DataFlavor[] {
                    DATA_FLAVOR, DataFlavor.stringFlavor
                });
        }

        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
        {
            if(flavor.equals(DATA_FLAVOR))
                return fetcher != null ? fetcher.getObject() : data;
            if(flavor.equals(DataFlavor.stringFlavor))
                return fetcher != null ? fetcher.getObject().toString() : data.toString();
            else
                throw new UnsupportedFlavorException(flavor);
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            if(flavor.equals(DATA_FLAVOR))
                return true;
            return flavor.equals(DataFlavor.stringFlavor);
        }

        public static final String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";
        ///public static final DataFlavor DATA_FLAVOR = new DataFlavor(mp3seth/FileDrop$TransferableObject, "application/x-net.iharder.dnd.TransferableObject");
         public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = 
            new java.awt.datatransfer.DataFlavor( FileDrop.TransferableObject.class, MIME_TYPE );
        private Fetcher fetcher;
        private Object data;
        private DataFlavor customFlavor;


        public TransferableObject(Object data)
        {
            this.data = data;
            customFlavor = new DataFlavor(data.getClass(), "application/x-net.iharder.dnd.TransferableObject");
        }

        public TransferableObject(Fetcher fetcher)
        {
            this.fetcher = fetcher;
        }

        public TransferableObject(Class dataClass, Fetcher fetcher)
        {
            this.fetcher = fetcher;
            customFlavor = new DataFlavor(dataClass, "application/x-net.iharder.dnd.TransferableObject");
        }
    }

    public static class Event extends EventObject
    {

        public File[] getFiles()
        {
            return files;
        }

        private File files[];

        public Event(File files[], Object source)
        {
            super(source);
            this.files = files;
        }
    }

    public static interface Listener
    {

        public abstract void filesDropped(File afile[]);
    }


    public FileDrop(Component c, Listener listener)
    {
        this(null, c, ((Border) (BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor))), true, listener);
    }

    public FileDrop(Component c, boolean recursive, Listener listener)
    {
        this(null, c, ((Border) (BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor))), recursive, listener);
    }

    public FileDrop(PrintStream out, JTable c, Listener listener)
    {
        this(out, ((Component) (c)), ((Border) (BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor))), false, listener);
    }

    public FileDrop(PrintStream out, Component c, boolean recursive, Listener listener)
    {
        this(out, c, ((Border) (BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor))), recursive, listener);
    }

    public FileDrop(Component c, Border dragBorder, Listener listener)
    {
        this(null, c, dragBorder, false, listener);
    }

    public FileDrop(Component c, Border dragBorder, boolean recursive, Listener listener)
    {
        this(null, c, dragBorder, recursive, listener);
    }

    public FileDrop(PrintStream out, Component c, Border dragBorder, Listener listener)
    {
        this(out, c, dragBorder, false, listener);
    }

    public FileDrop(final PrintStream out, final Component c, final Border dragBorder, boolean recursive, final Listener listener)
    {
        if(supportsDnD())
        {
            dropListener = new java.awt.dnd.DropTargetListener()
            {   public void dragEnter( java.awt.dnd.DropTargetDragEvent evt )
                {       log( out, "FileDrop: dragEnter event." );

                    // Is this an acceptable drag event?
                    if( isDragOk( out, evt ) )
                    {
                        // If it's a Swing component, set its border
                        if( c instanceof javax.swing.JComponent )
                        {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                            normalBorder = jc.getBorder();
                            log( out, "FileDrop: normal border saved." );
                            jc.setBorder( dragBorder );
                            log( out, "FileDrop: drag border set." );
                        }   // end if: JComponent   

                        // Acknowledge that it's okay to enter
                        //evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                        evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY );
                        log( out, "FileDrop: event accepted." );
                    }   // end if: drag ok
                    else 
                    {   // Reject the drag event
                        evt.rejectDrag();
                        log( out, "FileDrop: event rejected." );
                    }   // end else: drag not ok
                }   // end dragEnter

                public void dragOver( java.awt.dnd.DropTargetDragEvent evt ) 
                {   // This is called continually as long as the mouse is
                    // over the drag target.
                }   // end dragOver

                public void drop( java.awt.dnd.DropTargetDropEvent evt )
                {   log( out, "FileDrop: drop event." );
                    try
                    {   // Get whatever was dropped
                        java.awt.datatransfer.Transferable tr = evt.getTransferable();

                        // Is it a file list?
                        if (tr.isDataFlavorSupported (java.awt.datatransfer.DataFlavor.javaFileListFlavor))
                        {
                            // Say we'll take it.
                            //evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                            evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY );
                            log( out, "FileDrop: file list accepted." );

                            // Get a useful list
                            java.util.List fileList = (java.util.List) 
                                tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            java.util.Iterator iterator = fileList.iterator();

                            // Convert list to array
                            java.io.File[] filesTemp = new java.io.File[ fileList.size() ];
                            fileList.toArray( filesTemp );
                            final java.io.File[] files = filesTemp;

                            // Alert listener to drop.
                            if( listener != null )
                                listener.filesDropped( files );

                            // Mark that drop is completed.
                            evt.getDropTargetContext().dropComplete(true);
                            log( out, "FileDrop: drop complete." );
                        }   // end if: file list
                        else // this section will check for a reader flavor.
                        {
                            // Thanks, Nathan!
                            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                            DataFlavor[] flavors = tr.getTransferDataFlavors();
                            boolean handled = false;
                            for (int zz = 0; zz < flavors.length; zz++) {
                                if (flavors[zz].isRepresentationClassReader()) {
                                    // Say we'll take it.
                                    //evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                                    evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                                    log(out, "FileDrop: reader accepted.");

                                    Reader reader = flavors[zz].getReaderForText(tr);

                                    BufferedReader br = new BufferedReader(reader);
                                    
                                    if(listener != null)
                                        listener.filesDropped(createFileArray(br, out));
                                    
                                    // Mark that drop is completed.
                                    evt.getDropTargetContext().dropComplete(true);
                                    log(out, "FileDrop: drop complete.");
                                    handled = true;
                                    break;
                                }
                            }
                            if(!handled){
                                log( out, "FileDrop: not a file list or reader - abort." );
                                evt.rejectDrop();
                            }
                            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                        }   // end else: not a file list
                    }   // end try
                    catch ( java.io.IOException io) 
                    {   log( out, "FileDrop: IOException - abort:" );
                        io.printStackTrace( out );
                        evt.rejectDrop();
                    }   // end catch IOException
                    catch (java.awt.datatransfer.UnsupportedFlavorException ufe) 
                    {   log( out, "FileDrop: UnsupportedFlavorException - abort:" );
                        ufe.printStackTrace( out );
                        evt.rejectDrop();
                    }   // end catch: UnsupportedFlavorException
                    finally
                    {
                        // If it's a Swing component, reset its border
                        if( c instanceof javax.swing.JComponent )
                        {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                            jc.setBorder( normalBorder );
                            log( out, "FileDrop: normal border restored." );
                        }   // end if: JComponent
                    }   // end finally
                }   // end drop

                public void dragExit( java.awt.dnd.DropTargetEvent evt ) 
                {   log( out, "FileDrop: dragExit event." );
                    // If it's a Swing component, reset its border
                    if( c instanceof javax.swing.JComponent )
                    {   javax.swing.JComponent jc = (javax.swing.JComponent) c;
                        jc.setBorder( normalBorder );
                        log( out, "FileDrop: normal border restored." );
                    }   // end if: JComponent
                }   // end dragExit

                public void dropActionChanged( java.awt.dnd.DropTargetDragEvent evt ) 
                {   log( out, "FileDrop: dropActionChanged event." );
                    // Is this an acceptable drag event?
                    if( isDragOk( out, evt ) )
                    {   //evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                        evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY );
                        log( out, "FileDrop: event accepted." );
                    }   // end if: drag ok
                    else 
                    {   evt.rejectDrag();
                        log( out, "FileDrop: event rejected." );
                    }   // end else: drag not ok
                }   // end dropActionChanged
            }; // end DropTargetListener
;
            makeDropTarget(out, c, recursive);
        } else
        {
            log(out, "FileDrop: Drag and drop is not supported with this JVM");
        }
    }

    private static boolean supportsDnD()
    {
        if(supportsDnD == null)
        {
            boolean support = false;
            try
            {
                Class arbitraryDndClass = Class.forName("java.awt.dnd.DnDConstants");
                support = true;
            }
            catch(Exception e)
            {
                support = false;
            }
            supportsDnD = new Boolean(support);
        }
        return supportsDnD.booleanValue();
    }

     private static File[] createFileArray(BufferedReader bReader, PrintStream out)
     {
        try { 
            java.util.List list = new java.util.ArrayList();
            java.lang.String line = null;
            while ((line = bReader.readLine()) != null) {
                try {
                    // kde seems to append a 0 char to the end of the reader
                    if(ZERO_CHAR_STRING.equals(line)) continue; 
                    
                    java.io.File file = new java.io.File(new java.net.URI(line));
                    list.add(file);
                } catch (Exception ex) {
                    log(out, "Error with " + line + ": " + ex.getMessage());
                }
            }

            return (java.io.File[]) list.toArray(new File[list.size()]);
        } catch (IOException ex) {
            log(out, "FileDrop: IOException");
        }
        return new File[0];
     }

    private void makeDropTarget( final java.io.PrintStream out, final java.awt.Component c, boolean recursive )
    {
        // Make drop target
        final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();
        try
        {   dt.addDropTargetListener( dropListener );
        }   // end try
        catch( java.util.TooManyListenersException e )
        {   e.printStackTrace();
            log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?" );
        }   // end catch
        
        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        c.addHierarchyListener( new java.awt.event.HierarchyListener()
        {   public void hierarchyChanged( java.awt.event.HierarchyEvent evt )
            {   log( out, "FileDrop: Hierarchy changed." );
                java.awt.Component parent = c.getParent();
                if( parent == null )
                {   c.setDropTarget( null );
                    log( out, "FileDrop: Drop target cleared from component." );
                }   // end if: null parent
                else
                {   new java.awt.dnd.DropTarget(c, dropListener);
                    log( out, "FileDrop: Drop target added to component." );
                }   // end else: parent not null
            }   // end hierarchyChanged
        }); // end hierarchy listener
        if( c.getParent() != null )
            new java.awt.dnd.DropTarget(c, dropListener);
        
        if( recursive && (c instanceof java.awt.Container ) )
        {   
            // Get the container
            java.awt.Container cont = (java.awt.Container) c;
            
            // Get it's components
            java.awt.Component[] comps = cont.getComponents();
            
            // Set it's components as listeners also
            for( int i = 0; i < comps.length; i++ )
                makeDropTarget( out, comps[i], recursive );
        }   // end if: recursively set components as listener
    }   // end dropListener

    private boolean isDragOk(PrintStream out, DropTargetDragEvent evt)
    {
        boolean ok = false;
        DataFlavor flavors[] = evt.getCurrentDataFlavors();
        for(int i = 0; !ok && i < flavors.length; i++)
        {
            DataFlavor curFlavor = flavors[i];
            if(curFlavor.equals(DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader())
                ok = true;
        }

        if(out != null)
        {
            if(flavors.length == 0)
                log(out, "FileDrop: no data flavors.");
            for(int i = 0; i < flavors.length; i++)
                log(out, flavors[i].toString());

        }
        return ok;
    }

    private static void log(PrintStream out, String message)
    {
        if(out != null)
            out.println(message);
    }

    public static boolean remove(Component c)
    {
        return remove(null, c, true);
    }

    public static boolean remove(PrintStream out, Component c, boolean recursive)
    {
        if(supportsDnD())
        {
            log(out, "FileDrop: Removing drag-and-drop hooks.");
            c.setDropTarget(null);
            if(recursive && (c instanceof Container))
            {
                Component comps[] = ((Container)c).getComponents();
                for(int i = 0; i < comps.length; i++)
                    remove(out, comps[i], recursive);

                return true;
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    private transient Border normalBorder;
    private transient DropTargetListener dropListener;
    private static Boolean supportsDnD;
    private static Color defaultBorderColor = new Color(0.0F, 0.0F, 1.0F, 0.25F);
    private static String ZERO_CHAR_STRING = "\0";







}
