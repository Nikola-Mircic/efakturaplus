package efakturaplus.gui.pdf;

import java.awt.event.*;
import java.io.IOException;

public class PDFNavigator implements KeyListener, MouseListener, MouseMotionListener {

    public int pageIndex = 0;
    public float scale = 1.0F;
    public int x_offset = 0;
    public int y_offset = 0;

    private int mouse_x_origin = -1;
    private int mouse_y_origin = -1;

    private PDFDisplay parent;

    public PDFNavigator(PDFDisplay parent){
        this.parent = parent;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.isControlDown()){
            // Zooming and moving displayed PDF file
            switch (e.getKeyCode()){
                case KeyEvent.VK_ADD -> {
                    this.scale += 0.05F;
                    try {
                        this.parent.drawPages();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case KeyEvent.VK_SUBTRACT -> {
                    this.scale -= 0.05F;
                    try {
                        this.parent.drawPages();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                case KeyEvent.VK_LEFT -> {
                    x_offset -= 20;
                }
                case KeyEvent.VK_RIGHT -> {
                    x_offset += 20;
                }
            }
        }else{
            // Switching between pages
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT -> {
                    this.pageIndex++;

                    this.pageIndex = Math.min(this.pageIndex, this.parent.getPages().size() - 1);
                    y_offset = 0;

                    System.out.println(pageIndex);
                }
                case KeyEvent.VK_LEFT -> {
                    this.pageIndex--;

                    this.pageIndex = Math.max(this.pageIndex, 0);
                    y_offset = 0;

                    System.out.println(pageIndex);
                }
                case KeyEvent.VK_UP -> {
                    y_offset += 20;
                }
                case KeyEvent.VK_DOWN -> {
                    y_offset -= 20;
                }
            }
        }

        this.parent.getPagesPanel().repaint();
        this.parent.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x_offset += e.getX() - mouse_x_origin;
        y_offset += e.getY() - mouse_y_origin;
        mouse_x_origin = e.getX();
        mouse_y_origin = e.getY();

        this.parent.getPagesPanel().repaint();
        this.parent.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mouse_x_origin = e.getX();
        mouse_y_origin = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouse_x_origin = -1;
        mouse_y_origin = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
