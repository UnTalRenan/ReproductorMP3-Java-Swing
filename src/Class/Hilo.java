
package Class;


import java.io.PrintStream;
//https://www.youtube.com/user/Renan6x3
public class Hilo
    implements Runnable
{

    public void setJ(String argo)
    {
        cronoText = argo;
    }

    public Hilo()
    {
        minutos = 0;
        segundos = 0;
        horas = 0;
        crono = new Thread(this);
    }

    public String getCronoText()
    {
        return cronoText;
    }

    public int getCronometro()
    {
        return cronometro;
    }

    public void run()
    {
        try
        {
            while(true) 
            {
                if(segundos == 59)
                {
                    segundos = 0;
                    minutos++;
                }
                if(minutos == 59)
                {
                    minutos = 0;
                    horas++;
                }
                segundos++;
                cronoText = (new StringBuilder()).append(horas).append(":").append(minutos).append(":").append(segundos).toString();
                Thread _tmp = crono;
                Thread.sleep(1000L);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void pause()
    {
        crono.suspend();
    }

    public void reanudar()
    {
        crono.resume();
    }

    public void stop()
    {
        if(crono.isAlive())
            crono.stop();
    }

    public void start()
    {
        crono.start();
    }

    public Thread crono;
    private int cronometro;
    private String cronoText;
    int minutos;
    int segundos;
    int horas;
}
