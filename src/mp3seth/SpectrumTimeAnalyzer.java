

package mp3seth;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JPanel;
import kj.dsp.KJDigitalSignalProcessingAudioDataConsumer;
import kj.dsp.KJDigitalSignalProcessor;
import kj.dsp.KJFFT;

// Referenced classes of package mp3seth:
//            AbsoluteConstraints
//https://www.youtube.com/user/Renan6x3
public class SpectrumTimeAnalyzer extends JPanel
    implements KJDigitalSignalProcessor
{

    public SpectrumTimeAnalyzer()
    {
        displayMode = 0;
        scopeColor = DEFAULT_SCOPE_COLOR;
        spectrumAnalyserColors = getDefaultSpectrumAnalyserColors();
        dsp = null;
        dspStarted = false;
        peakColor = null;
        peaks = new int[19];
        peaksDelay = new int[19];
        peakDelay = 20;
        peaksEnabled = true;
        visColors = null;
        barOffset = 1;
        saDecay = 0.05F;
        m_line = null;
        vuDecay = 0.02F;
        lfu = 0L;
        fc = 0;
        fps = 50;
        showFPS = false;
        constraints = null;
        setOpaque(false);
        initialize();
    }

    public void setConstraints(AbsoluteConstraints cnts)
    {
        constraints = cnts;
    }

    public AbsoluteConstraints getConstraints()
    {
        return constraints;
    }

    public boolean isPeaksEnabled()
    {
        return peaksEnabled;
    }

    public void setPeaksEnabled(boolean peaksEnabled)
    {
        this.peaksEnabled = peaksEnabled;
    }

    public int getFps()
    {
        return fps;
    }

    public void setFps(int fps)
    {
        this.fps = fps;
    }

    public void startDSP(SourceDataLine line)
    {
        if(displayMode == 2)
            return;
        if(line != null)
            m_line = line;
        if(dsp == null)
        {
            dsp = new KJDigitalSignalProcessingAudioDataConsumer(2048, fps);
            dsp.add(this);
        }
        if(dsp != null && m_line != null)
        {
            if(dspStarted)
                stopDSP();
            dsp.start(m_line);
            dspStarted = true;
        }
    }

    public void stopDSP()
    {
        if(dsp != null)
        {
            dsp.stop();
            dspStarted = false;
        }
    }

    public void closeDSP()
    {
        if(dsp != null)
        {
            stopDSP();
            dsp = null;
        }
    }

    public void setupDSP(SourceDataLine line)
    {
        if(dsp != null)
        {
            int channels = line.getFormat().getChannels();
            if(channels == 1)
                dsp.setChannelMode(1);
            else
                dsp.setChannelMode(2);
            int bits = line.getFormat().getSampleSizeInBits();
            if(bits == 8)
                dsp.setSampleType(1);
            else
                dsp.setSampleType(2);
        }
    }

    public void writeDSP(byte pcmdata[])
    {
        if(dsp != null && dspStarted)
            dsp.writeAudioData(pcmdata);
    }

    public KJDigitalSignalProcessingAudioDataConsumer getDSP()
    {
        return dsp;
    }

    public void setVisColor(String viscolor)
    {
        ArrayList visColors;
        BufferedReader bin;
        visColors = new ArrayList();
        viscolor = viscolor.toLowerCase();
        ByteArrayInputStream in = new ByteArrayInputStream(viscolor.getBytes());
        bin = new BufferedReader(new InputStreamReader(in));
        try
        {
            for(String line = null; (line = bin.readLine()) != null;)
                visColors.add(getColor(line));

            Color colors[] = new Color[visColors.size()];
            visColors.toArray(colors);
            Color specColors[] = new Color[15];
            System.arraycopy(colors, 2, specColors, 0, 15);
            java.util.List specList = Arrays.asList(specColors);
            Collections.reverse(specList);
            specColors = (Color[])(Color[])specList.toArray(specColors);
            setSpectrumAnalyserColors(specColors);
            setBackground((Color)visColors.get(0));
            if(visColors.size() > 23)
                setPeakColor((Color)visColors.get(23));
            if(visColors.size() > 18)
                setScopeColor((Color)visColors.get(18));
        }
        catch(IOException ex)
        {
            try
            {
                if(bin != null)
                    bin.close();
            }
            catch(IOException e) { }
            //break MISSING_BLOCK_LABEL_253;
        }
        try
        {
            if(bin != null)
                bin.close();
        }
        catch(IOException e) { }
        //break MISSING_BLOCK_LABEL_253;
        Exception exception;
        //exception;
        try
        {
            if(bin != null)
                bin.close();
        }
        catch(IOException e) { }
        //throw exception;
    }

    public void setPeakColor(Color c)
    {
        peakColor = c;
    }

    public void setPeakDelay(int framestowait)
    {
        int min = Math.round(0.3F * (float)fps);
        int max = Math.round(0.5F * (float)fps);
        if(framestowait >= min && framestowait <= max)
            peakDelay = framestowait;
        else
            peakDelay = Math.round(0.4F * (float)fps);
    }

    public int getPeakDelay()
    {
        return peakDelay;
    }

    public Color getColor(String linecolor)
    {
        Color color = Color.BLACK;
        StringTokenizer st = new StringTokenizer(linecolor, ",");
        int red = 0;
        int green = 0;
        int blue = 0;
        try
        {
            if(st.hasMoreTokens())
                red = Integer.parseInt(st.nextToken().trim());
            if(st.hasMoreTokens())
                green = Integer.parseInt(st.nextToken().trim());
            if(st.hasMoreTokens())
            {
                String blueStr = st.nextToken().trim();
                if(blueStr.length() > 3)
                    blueStr = blueStr.substring(0, 3).trim();
                blue = Integer.parseInt(blueStr);
            }
            color = new Color(red, green, blue);
        }
        catch(NumberFormatException e) { }
        return color;
    }

    private void computeColorScale()
    {
        saColorScale = ((float)spectrumAnalyserColors.length / (float)height) * (float)barOffset * 1.0F;
        vuColorScale = ((float)spectrumAnalyserColors.length / (float)(width - 32)) * 2.0F;
    }

    private void computeSAMultiplier()
    {
        saMultiplier = saFFTSampleSize / 2 / saBands;
    }

    private void drawScope(Graphics pGrp, float pSample[])
    {
        pGrp.setColor(scopeColor);
        int wLas = (int)(pSample[0] * (float)height_2) + height_2;
        int wSt = 2;
        int a = wSt;
        for(int c = 0; c < width; c++)
        {
            int wAs = (int)(pSample[a] * (float)height_2) + height_2;
            pGrp.drawLine(c, wLas, c + 1, wAs);
            wLas = wAs;
            a += wSt;
        }

    }

    private void drawSpectrumAnalyser(Graphics pGrp, float pSample[], float pFrrh)
    {
        float c = 0.0F;
        float wFFT[] = fft.calculate(pSample);
        float wSadfrr = saDecay * pFrrh;
        float wBw = (float)width / (float)saBands;
        int a = 0;
        for(int bd = 0; bd < saBands; bd++)
        {
            float wFs = 0.0F;
            for(int b = 0; (float)b < saMultiplier; b++)
                wFs += wFFT[a + b];

            wFs *= (float)Math.log(bd + 2);
            if(wFs > 1.0F)
                wFs = 1.0F;
            if(wFs >= old_FFT[a] - wSadfrr)
            {
                old_FFT[a] = wFs;
            } else
            {
                old_FFT[a] -= wSadfrr;
                if(old_FFT[a] < 0.0F)
                    old_FFT[a] = 0.0F;
                wFs = old_FFT[a];
            }
            drawSpectrumAnalyserBar(pGrp, (int)c, height, (int)wBw - 1, (int)(wFs * (float)height), bd);
            c += wBw;
            a = (int)((float)a + saMultiplier);
        }

    }

    private void drawVUMeter(Graphics pGrp, float pLeft[], float pRight[], float pFrrh)
    {
        if(displayMode == 2)
            return;
        float wLeft = 0.0F;
        float wRight = 0.0F;
        float wSadfrr = vuDecay * pFrrh;
        for(int a = 0; a < pLeft.length; a++)
        {
            wLeft += Math.abs(pLeft[a]);
            wRight += Math.abs(pRight[a]);
        }

        wLeft = (wLeft * 2.0F) / (float)pLeft.length;
        wRight = (wRight * 2.0F) / (float)pRight.length;
        if(wLeft > 1.0F)
            wLeft = 1.0F;
        if(wRight > 1.0F)
            wRight = 1.0F;
        if(wLeft >= oldLeft - wSadfrr)
        {
            oldLeft = wLeft;
        } else
        {
            oldLeft -= wSadfrr;
            if(oldLeft < 0.0F)
                oldLeft = 0.0F;
        }
        if(wRight >= oldRight - wSadfrr)
        {
            oldRight = wRight;
        } else
        {
            oldRight -= wSadfrr;
            if(oldRight < 0.0F)
                oldRight = 0.0F;
        }
        int wHeight = (height >> 1) - 24;
        drawVolumeMeterBar(pGrp, 16, 16, (int)(oldLeft * (float)(width - 32)), wHeight);
        drawVolumeMeterBar(pGrp, 16, wHeight + 32, (int)(oldRight * (float)(width - 32)), wHeight);
    }

    private void drawSpectrumAnalyserBar(Graphics pGraphics, int pX, int pY, int pWidth, int pHeight, int band)
    {
        float c = 0.0F;
        for(int a = pY; a >= pY - pHeight; a -= barOffset)
        {
            c += saColorScale;
            if(c < (float)spectrumAnalyserColors.length)
                pGraphics.setColor(spectrumAnalyserColors[(int)c]);
            pGraphics.fillRect(pX, a, pWidth, 1);
        }

        if(peakColor != null && peaksEnabled)
        {
            pGraphics.setColor(peakColor);
            if(pHeight > peaks[band])
            {
                peaks[band] = pHeight;
                peaksDelay[band] = peakDelay;
            } else
            {
                peaksDelay[band]--;
                if(peaksDelay[band] < 0)
                    peaks[band]--;
                if(peaks[band] < 0)
                    peaks[band] = 0;
            }
            pGraphics.fillRect(pX, pY - peaks[band], pWidth, 1);
        }
    }

    private void drawVolumeMeterBar(Graphics pGraphics, int pX, int pY, int pWidth, int pHeight)
    {
        float c = 0.0F;
        for(int a = pX; a <= pX + pWidth; a += 2)
        {
            c += vuColorScale;
            if(c < 256F)
                pGraphics.setColor(spectrumAnalyserColors[(int)c]);
            pGraphics.fillRect(a, pY, 1, pHeight);
        }

    }

    private synchronized Image getDoubleBuffer()
    {
        if(bi == null || bi.getWidth(null) != getSize().width || bi.getHeight(null) != getSize().height)
        {
            width = getSize().width;
            height = getSize().height;
            height_2 = height >> 1;
            computeColorScale();
            bi = getGraphicsConfiguration().createCompatibleVolatileImage(width, height);
        }
        return bi;
    }

    public static Color[] getDefaultSpectrumAnalyserColors()
    {
        Color wColors[] = new Color[256];
        for(int a = 0; a < 128; a++)
            wColors[a] = new Color(0, (a >> 1) + 192, 0);

        for(int a = 0; a < 64; a++)
            wColors[a + 128] = new Color(a << 2, 255, 0);

        for(int a = 0; a < 64; a++)
            wColors[a + 192] = new Color(255, 255 - (a << 2), 0);

        return wColors;
    }

    public int getDisplayMode()
    {
        return displayMode;
    }

    public int getSpectrumAnalyserBandCount()
    {
        return saBands;
    }

    public float getSpectrumAnalyserDecay()
    {
        return saDecay;
    }

    public Color getScopeColor()
    {
        return scopeColor;
    }

    public Color[] getSpectrumAnalyserColors()
    {
        return spectrumAnalyserColors;
    }

    private void initialize()
    {
        setSize(556, 128);
        prepareDisplayToggleListener();
        setSpectrumAnalyserBandCount(19);
        setSpectrumAnalyserFFTSampleSize(512);
    }

    public boolean isShowingFPS()
    {
        return showFPS;
    }

    public void paintComponent(Graphics pGraphics)
    {
        if(displayMode == 2)
            return;
        if(dspStarted)
            pGraphics.drawImage(getDoubleBuffer(), 0, 0, null);
        else
            super.paintComponent(pGraphics);
    }

    private void prepareDisplayToggleListener()
    {
        setCursor(Cursor.getPredefinedCursor(12));
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent pEvent)
            {
                if(pEvent.getButton() == 1)
                    if(displayMode + 1 > 1)
                        displayMode = 0;
                    else
                        displayMode++;
            }

            final SpectrumTimeAnalyzer this$0;

            
            {
                this$0 = SpectrumTimeAnalyzer.this;
               // super();
            }
        }
);
    }

    public synchronized void process(float pLeft[], float pRight[], float pFrameRateRatioHint)
    {
        if(displayMode == 2)
            return;
        Graphics wGrp = getDoubleBuffer().getGraphics();
        wGrp.setColor(getBackground());
        wGrp.fillRect(0, 0, getSize().width, getSize().height);
        switch(displayMode)
        {
        case 0: // '\0'
            drawScope(wGrp, stereoMerge(pLeft, pRight));
            break;

        case 1: // '\001'
            drawSpectrumAnalyser(wGrp, stereoMerge(pLeft, pRight), pFrameRateRatioHint);
            break;

        case 2: // '\002'
            drawVUMeter(wGrp, pLeft, pRight, pFrameRateRatioHint);
            break;
        }
        if(showFPS)
        {
            if(System.currentTimeMillis() >= lfu + 1000L)
            {
                lfu = System.currentTimeMillis();
                fps = fc;
                fc = 0;
            }
            fc++;
            wGrp.setColor(Color.yellow);
            wGrp.drawString((new StringBuilder()).append("FPS: ").append(fps).append(" (FRRH: ").append(pFrameRateRatioHint).append(")").toString(), 0, height - 1);
        }
        if(getGraphics() != null)
            getGraphics().drawImage(getDoubleBuffer(), 0, 0, null);
    }

    public synchronized void setDisplayMode(int pMode)
    {
        displayMode = pMode;
    }

    public synchronized void setScopeColor(Color pColor)
    {
        scopeColor = pColor;
    }

    public synchronized void setShowFPS(boolean pState)
    {
        showFPS = pState;
    }

    public synchronized void setSpectrumAnalyserBandCount(int pCount)
    {
        saBands = pCount;
        peaks = new int[saBands];
        peaksDelay = new int[saBands];
        computeSAMultiplier();
    }

    public synchronized void setSpectrumAnalyserDecay(float pDecay)
    {
        if(pDecay >= 0.02F && pDecay <= 0.08F)
            saDecay = pDecay;
        else
            saDecay = 0.05F;
    }

    public synchronized void setSpectrumAnalyserColors(Color pColors[])
    {
        spectrumAnalyserColors = pColors;
        computeColorScale();
    }

    public synchronized void setSpectrumAnalyserFFTSampleSize(int pSize)
    {
        saFFTSampleSize = pSize;
        fft = new KJFFT(saFFTSampleSize);
        old_FFT = new float[saFFTSampleSize];
        computeSAMultiplier();
    }

    private float[] stereoMerge(float pLeft[], float pRight[])
    {
        for(int a = 0; a < pLeft.length; a++)
            pLeft[a] = (pLeft[a] + pRight[a]) / 2.0F;

        return pLeft;
    }

    public static final int DISPLAY_MODE_SCOPE = 0;
    public static final int DISPLAY_MODE_SPECTRUM_ANALYSER = 1;
    public static final int DISPLAY_MODE_OFF = 2;
    public static final int DEFAULT_WIDTH = 556;
    public static final int DEFAULT_HEIGHT = 128;
    public static final int DEFAULT_FPS = 50;
    public static final int DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE = 512;
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 19;
    public static final float DEFAULT_SPECTRUM_ANALYSER_DECAY = 0.05F;
    public static final int DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY = 20;
    public static final float DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO = 0.4F;
    public static final float DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY_FPS_RATIO_RANGE = 0.1F;
    public static final float MIN_SPECTRUM_ANALYSER_DECAY = 0.02F;
    public static final float MAX_SPECTRUM_ANALYSER_DECAY = 0.08F;
    public static final Color DEFAULT_SCOPE_COLOR = new Color(255, 192, 0);
    public static final float DEFAULT_VU_METER_DECAY = 0.02F;
    private Image bi;
    private int displayMode;
    private Color scopeColor;
    private Color spectrumAnalyserColors[];
    private KJDigitalSignalProcessingAudioDataConsumer dsp;
    private boolean dspStarted;
    private Color peakColor;
    private int peaks[];
    private int peaksDelay[];
    private int peakDelay;
    private boolean peaksEnabled;
    private java.util.List visColors;
    private int barOffset;
    private int width;
    private int height;
    private int height_2;
    private KJFFT fft;
    private float old_FFT[];
    private int saFFTSampleSize;
    private int saBands;
    private float saColorScale;
    private float saMultiplier;
    private float saDecay;
    private float sad;
    private SourceDataLine m_line;
    private float oldLeft;
    private float oldRight;
    private float vuDecay;
    private float vuColorScale;
    private long lfu;
    private int fc;
    private int fps;
    private boolean showFPS;
    private AbsoluteConstraints constraints;




}
