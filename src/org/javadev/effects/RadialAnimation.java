package org.javadev.effects;

//import it.unitn.ing.rista.util.Misc;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;


/**
 * The RadialAnimation is a class to animate the transition between
 * one component and the other for AnimatingCardLayout. Based on the Dashboard
 * code of Dmitry Markman.
 * <p/>
 * Description
 *
 * @author Luca Lutterotti
 * @author Dmitry Markman
 * @version $Revision: 1.2 $, $Date: 2006/01/19 14:46:00 $
 * @since JDK1.1
 */

public class RadialAnimation implements Animation {

  SpecialPanel animationPanel = null;
  private AnimationListener listener = null;
  boolean direction = true;
  int       animationDuration = 2000;

  public void setDirection(boolean direction){
    this.direction = direction;
  }

  public void setAnimationDuration(int animationDuration){
    this.animationDuration = (animationDuration < 500)?500:animationDuration;
  }


  public Component animate(final Component toHide, final Component toShow, AnimationListener listener) {
    this.listener = listener;
    animationPanel = new SpecialPanel(this, toHide, toShow);
    animationPanel.needToStartThread = true;
    animationPanel.beginAngle = 0;
    animationPanel.endAngle = 360;
    animationPanel.setAnimationDuration(animationDuration);
    return animationPanel;


  }

  public Component getAnimationPanel() {
    return animationPanel;
  }

  void rotationFinished() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        animationPanel = null;
        listener.animationFinished();
        listener = null;
      }
    });
  }


  class SpecialPanel extends JPanel{
  RadialAnimation owner;

  BufferedImage firstImage;
  BufferedImage secondImage;
  Component component1;
  Component component2;

  float angle = 0;

  public float beginAngle = 0;
  public float endAngle = 360;

  float deltaAngle = 0.5f;
  float effectTime = 2000;
  double dt = effectTime* deltaAngle/360;
  int counter = 0;
  long totalDrawTime = 0;

  public boolean needToStartThread = false;

      SpecialPanel(RadialAnimation owner,BufferedImage firstImage, BufferedImage secondImage){
          this.owner = owner;
          this.firstImage = firstImage;
          this.secondImage = secondImage;
          angle = beginAngle;
          setOpaque(false);
      }

      SpecialPanel(RadialAnimation owner,Component component1, Component component2){
          this.owner = owner;
          this.component1 = component1;
          this.component2 = component2;
          angle = beginAngle;
          setOpaque(false);
      }

      public void setAnimationDuration(int animationDuration){
        effectTime = (animationDuration < 500)?500:animationDuration;
        dt = effectTime* deltaAngle/360;
      }

      void startThread(float val1,float val2){
          counter = 0;
          totalDrawTime = 0;
          this.beginAngle = val1;
          this.endAngle = val2;
          if(endAngle < beginAngle)   deltaAngle = -Math.abs(deltaAngle);
          else                        deltaAngle = Math.abs(deltaAngle);
          angle = beginAngle;          
          final Runnable repaint = new Runnable() { //am@kikamedical.com Arnaud Masson
             public void run() {
                 repaint();
                 getToolkit().sync();
             }                                      };
          Thread t = new Thread(new Runnable(){
              public void run(){
                  float absDeltaAngle=Math.abs(deltaAngle);
                  long startTime = System.currentTimeMillis();
                  long initTime = System.currentTimeMillis();
                  while(true){
                      long time = System.currentTimeMillis();
                      angle += deltaAngle*(time - startTime)/dt;//idea am@kikamedical.com Arnaud Masson
                      startTime = time;
                      if(((angle >= endAngle-deltaAngle/2) && (deltaAngle > 0)) ||
                         ((angle <= endAngle-deltaAngle/2) && (deltaAngle < 0))){
                          angle = endAngle;
                          if(Math.abs(angle - 360) < absDeltaAngle / 2) angle = 0;
                          repaint();
                          //System.out.println("total count "+counter+" time "+(System.currentTimeMillis() - initTime)+" average time "+(totalDrawTime/counter));
                          break;
                      }
                      if(angle >= 360) angle = 0;
                      try{
                         // Thread.sleep(dt);
                         // repaint();
                         // getToolkit().sync();
                         SwingUtilities.invokeAndWait(repaint);  //idea am@kikamedical.com Arnaud Masson
                      }catch(Throwable tt){
                      }
                  }
                  if(owner != null) owner.rotationFinished();
                  synchronized(SpecialPanel.this){
                      if(component1 != null) firstImage = null;
                      if(component2 != null) secondImage = null;
                  }
              }
          });
          t.start();
      }

      public void update(Graphics g){
          paint(g);
      }

      public synchronized void paint(Graphics g){
          if(needToStartThread){
              totalDrawTime = 0;
              counter = 0;
              needToStartThread = false;
              startThread(beginAngle,endAngle);
              if(firstImage == null){
                  firstImage = createImageFromComponent(component1);
              }
              if(secondImage == null){
                  secondImage = createImageFromComponent(component2);
              }
          }
          if(firstImage == null || secondImage == null) return;
          Graphics2D g2d = (Graphics2D)g;
          int ww = firstImage.getWidth();
          int hh = firstImage.getHeight();
          {
              float needAngle = angle;
              if(needAngle < 0) needAngle = 0;
              if(needAngle > 360) needAngle = 360;

              long beforeDraw = System.currentTimeMillis();
              g2d.drawImage(firstImage,null,0,0);
              Shape oldClip = g2d.getClip();
              float r = (float)Math.sqrt(ww*ww+hh*hh)/2;
              Arc2D.Float arc = new Arc2D.Float(ww/2 - r, hh/2 - r,2*r,2*r,0,needAngle,Arc2D.PIE);
              g2d.setClip(arc);
              g2d.drawImage(secondImage,null,0,0);
              g2d.setClip(oldClip);
              totalDrawTime += (System.currentTimeMillis() - beforeDraw);
              counter++;

        }
      }

      BufferedImage createImageFromComponent(Component comp){
          BufferedImage retImage = null;
          if(comp == null) return retImage;
          try{
              GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
              GraphicsDevice gd = genv.getDefaultScreenDevice();
              GraphicsConfiguration gc = gd.getDefaultConfiguration();
              java.awt.image.ColorModel cm = gc.getColorModel();
            boolean hasAlpha = cm.hasAlpha();
            int cw = comp.getSize().width;
            int ch = comp.getSize().height;
            if(hasAlpha){
                retImage = gc.createCompatibleImage(cw,ch);
            }else{
                retImage = new BufferedImage(cw,ch,BufferedImage.TYPE_INT_ARGB);
            }
              if(retImage == null) return retImage;
              Graphics og = retImage.getGraphics();
                  comp.paint(og);
              og.dispose();
          }catch(Throwable t){
          }
          return retImage;

      }

  }



}
