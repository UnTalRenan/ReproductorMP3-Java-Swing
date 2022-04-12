

package Class;


import java.io.File;
import javax.swing.JSlider;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
//https://www.youtube.com/user/Renan6x3
public class Reproductor
{

    public BasicPlayer getBasicPlayer()
    {
        return basicPlayer;
    }

    public Reproductor()
        throws Exception
    {
        bytesLength = 0.0D;
        basicPlayer = new BasicPlayer();
    }

    public void volumen(JSlider j)
    {
        try
        {
            basicPlayer.setGain(0.0D);
            try
            {
                int gainValue = j.getValue();
                int maxGain = j.getMaximum();
                if(gainValue == 0)
                    basicPlayer.setGain(0.0D);
                else
                    basicPlayer.setGain((double)gainValue / (double)maxGain);
            }
            catch(Exception ex) { }
        }
        catch(BasicPlayerException ex) { }
    }

    public void Play()
        throws Exception
    {
        basicPlayer.play();
    }

    public void AbrirFichero(String ruta)
        throws Exception
    {
        basicPlayer.open(new File(ruta));
    }

    public void Pausa()
        throws Exception
    {
        basicPlayer.pause();
    }

    public void Continuar()
        throws Exception
    {
        basicPlayer.resume();
    }

    public void Stop()
        throws Exception
    {
        basicPlayer.stop();
    }

    public BasicPlayer basicPlayer;
    double bytesLength;
}
