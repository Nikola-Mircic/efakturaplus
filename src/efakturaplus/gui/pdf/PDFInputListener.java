package efakturaplus.gui.pdf;

import java.awt.event.*;

public class PDFInputListener implements KeyListener, MouseListener, MouseMotionListener {
    private PDFNavigator navigator;

    private int mouse_x_origin = -1;
    private int mouse_y_origin = -1;

    public PDFInputListener(PDFNavigator navigator){
        this.navigator = navigator;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyChar());
        if(e.isControlDown()){
            // Zooming and moving displayed PDF file
            switch (e.getKeyCode()){
                case KeyEvent.VK_ADD -> {
                    navigator.changeScale(0.05f);
                }
                case KeyEvent.VK_SUBTRACT -> {
                    navigator.changeScale(-0.05f);
                }
                case KeyEvent.VK_LEFT -> {
                    navigator.changeOffset(-20, 0);
                }
                case KeyEvent.VK_RIGHT -> {
                    navigator.changeOffset(20, 0);
                }
            }
        }else{
            // Switching between pages
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT -> {
                    navigator.nextPage();
                }
                case KeyEvent.VK_LEFT -> {
                    navigator.previousPage();
                }
                case KeyEvent.VK_UP -> {
                    navigator.changeOffset(0, 20);
                }
                case KeyEvent.VK_DOWN -> {
                    navigator.changeOffset(0, -20);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - mouse_x_origin;
        int dy = e.getY() - mouse_y_origin;

        navigator.changeOffset(dx, dy);

        mouse_x_origin = e.getX();
        mouse_y_origin = e.getY();
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
