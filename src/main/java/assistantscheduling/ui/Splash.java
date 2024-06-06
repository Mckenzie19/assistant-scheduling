package assistantscheduling.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Splash extends Frame implements ActionListener{

	private static final long serialVersionUID = 2L;

	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = {"foo", "bar", "baz"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120,140,200,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading "+comps[(frame/5)%3]+"...", 120, 150);
	}
	 public Splash() {
	        super("Splash Screen Loader");
	        final SplashScreen splash = SplashScreen.getSplashScreen();
	        if (splash == null) {
	            System.out.println("SplashScreen.getSplashScreen() returned null");
	            return;
	        }
	        Graphics2D g = splash.createGraphics();
	        if (g == null) {
	            System.out.println("g is null");
	            return;
	        }
	        for(int i=0; i<100; i++) {
	            renderSplashFrame(g, i);
	            splash.update();
	            try {
	                Thread.sleep(90);
	            }
	            catch(InterruptedException e) {
	            }
	        }
	        splash.close();
	        setVisible(true);
	        toFront();
	    }

	    @Override
		public void actionPerformed(ActionEvent ae) {
	        System.exit(0);
	    }
}
