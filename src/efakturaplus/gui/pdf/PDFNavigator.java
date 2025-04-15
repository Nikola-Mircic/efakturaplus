package efakturaplus.gui.pdf;

import efakturaplus.gui.StretchIcon;
import efakturaplus.util.PrinterUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class PDFNavigator extends JComponent{
    private PDFDisplay parent;

    private JLabel pageIndexLabel;
    private JSlider zoomSlider;

    public PDFNavigator(PDFDisplay parent){
        this.parent = parent;
        setupLayout();
    }

    private void setupLayout(){
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        pageIndexLabel = new JLabel(getPageIndexLabel() + 1 + " / " + parent.getPages().size(), JLabel.CENTER);
        pageIndexLabel.setPreferredSize(new Dimension(50, 25));

        zoomSlider = getSlider();

        JButton prevPageBtn = createButton("<", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                previousPage();
            }
        });
        JButton nextPageBtn = createButton(">", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                nextPage();
            }
        });

        Image zoomInImg = null;
        Image zoomOutImg = null;
        Image printerImg = null;
        try {
            zoomInImg = ImageIO.read(new File("icons/zoom-in.png"));
            zoomOutImg = ImageIO.read(new File("icons/zoom-out.png"));
            printerImg = ImageIO.read(new File("icons/printer.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JButton zoomInBtn;
        JButton zoomOutBtn;
        JButton printBtn;

        if(zoomInImg != null && zoomOutImg != null){
            zoomInBtn = createButton(new StretchIcon(zoomInImg), new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    changeScale(0.05f);
                }
            });

            zoomOutBtn = createButton(new StretchIcon(zoomOutImg), new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    changeScale(-0.05f);
                }
            });
        }else{
            zoomInBtn = createButton("+", new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    changeScale(0.05f);
                }
            });

            zoomOutBtn = createButton("-", new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    changeScale(-0.05f);
                }
            });
        }

        if(printerImg != null){
            printBtn = createButton(new StretchIcon(printerImg), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PrinterUtil util = new PrinterUtil(parent.pdfDocument);
                    util.show();
                }
            });
        }else{
            printBtn = createButton("Print", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PrinterUtil util = new PrinterUtil(parent.pdfDocument);
                    util.show();
                }
            });
        }

        panel.add(prevPageBtn);
        panel.add(pageIndexLabel);
        panel.add(nextPageBtn);
        panel.add(zoomOutBtn);
        panel.add(zoomSlider);
        panel.add(zoomInBtn);
        panel.add(printBtn);

        this.add(panel);
    }

    public JButton createButton(String text, ActionListener listener){
        JButton button = new JButton(text);

        button.setPreferredSize(new Dimension(60, 30));
        button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.addActionListener(listener);

        return button;
    }

    public JButton createButton(StretchIcon icon, ActionListener listener){
        JButton button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));

        button.add(new JPanel(){
            @Override
            public void paint(Graphics g) {
                this.getInsets().set(0, 0, 0, 0);
                icon.paintIcon(this, g, 0, 0);
            }
        });

        button.setPreferredSize(new Dimension(60, 30));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.addActionListener(listener);

        return button;
    }


    public JSlider getSlider(){
        JSlider slider = new JSlider(50, 200, 100);

        slider.setPreferredSize(new Dimension(150, 30));

        slider.setMajorTickSpacing(20);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                setScale(value/100.0F);
            }
        });

        return slider;
    }

    public void nextPage(){
        parent.pageIndex++;
        parent.pageIndex = Math.min(parent.pageIndex, parent.pages.size() - 1);
        parent.y_offset = 0;
        parent.x_offset = 0;

        pageIndexLabel.setText(getPageIndexLabel() + 1 + "/" + parent.getPages().size());

        refresh();
    }

    public void previousPage(){
        parent.pageIndex--;
        parent.pageIndex = Math.max(parent.pageIndex, 0);
        parent.y_offset = 0;
        parent.x_offset = 0;

        pageIndexLabel.setText(getPageIndexLabel() + 1 + "/" + parent.getPages().size());

        refresh();
    }

    public int getPageIndexLabel() {
        return parent.pageIndex;
    }

    public void changeScale(float dScale){
        zoomSlider.setValue( (int) ((parent.scale + dScale) * 100) );
    }

    public void setScale(float scale) {
        parent.scale = scale;

        parent.scale = Math.max(parent.scale, 0.5F);
        parent.scale = Math.min(parent.scale, 2F);

        try {
            parent.drawPages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        refresh();
    }

    public float getScale() {
        return parent.scale;
    }

    public void setOffset(int x_offset, int y_offset) {
        parent.x_offset = x_offset;
        parent.y_offset = y_offset;

        refresh();
    }

    public void changeOffset(int dX_offset, int dY_offset) {
        parent.x_offset += dX_offset;
        parent.y_offset += dY_offset;

        refresh();
    }

    public void setPageIndex(int pageIndex) {
        parent.pageIndex = pageIndex;

        refresh();
    }

    private void refresh(){
        parent.pagesPanel.repaint();
        parent.repaint();
    }
}
