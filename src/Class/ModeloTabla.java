

package Class;


import javax.swing.table.DefaultTableModel;
//https://www.youtube.com/user/Renan6x3
public class ModeloTabla extends DefaultTableModel
{

    public ModeloTabla()
    {
        dtm = new DefaultTableModel() {

            public boolean isCellEditable(int row, int column)
            {
                return false;
            }

           // final ModeloTabla this$0;
           // final ModeloTabla this$0$;



        };
    }

    public DefaultTableModel getDtm()
    {
        return dtm;
    }

    public void nuevaColumna(Object arg0)
    {
        dtm.addColumn(arg0);
    }

    public void agregarFilas(Object arg0[])
    {
        getDtm().addRow(arg0);
    }

    public void agregarFilas2(String ar[])
    {
        getDtm().addRow(ar);
    }

    public String getNombreColumna(int arg0)
    {
        return dtm.getColumnName(arg0);
    }

    public int getCantidadFilas()
    {
        return dtm.getRowCount();
    }

    public Object getValor(int arg0, int arg1)
    {
        return dtm.getValueAt(arg0, arg1);
    }

    public boolean isEditable(int arg0, int arg1)
    {
        return super.isCellEditable(arg0, arg1);
    }

    public void eliminarFila(int arg0)
    {
        dtm.removeRow(arg0);
    }

    public void setNumeroFilas(int arg0)
    {
        dtm.setNumRows(arg0);
    }

    public void setValorFila(Object arg0, int arg1, int arg2)
    {
        dtm.setValueAt(arg0, arg1, arg2);
    }

    private DefaultTableModel dtm;
}
