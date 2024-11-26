package efakturaplus.gui.pdf;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class PDFNavigator extends JComponent{
    private PDFDisplay parent;

    private JLabel pageIndex;
    private JButton prevPageBtn;
    private JButton nextPageBtn;
    private JButton zoomInBtn;
    private JButton zoomOutBtn;
    private JSlider zoomSlider;

    public PDFNavigator(PDFDisplay parent){
        this.parent = parent;

        setupLayout();
    }

    private void setupLayout(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        pageIndex = new JLabel(parent.getPageIndex() + 1 + " / " + parent.getPages().size());

        zoomSlider = getSlider();

        prevPageBtn = createButton("<", new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.nextPage();
                pageIndex.setText(parent.getPages().size() + "/" + parent.getPages().size());
            }
        });
        nextPageBtn = createButton(">", new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.previousPage();
                pageIndex.setText(parent.getPages().size() + "/" + parent.getPages().size());
            }
        });
        zoomInBtn = createButton("+", new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.changeScale(0.05f);

                zoomSlider.setValue((int)(parent.getScale() * 100));
            }
        });
        zoomOutBtn = createButton("-", new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.changeScale(-0.05f);

                zoomSlider.setValue((int)(parent.getScale() * 100));
            }
        });

        panel.add(prevPageBtn);
        panel.add(pageIndex);
        panel.add(nextPageBtn);
        panel.add(zoomOutBtn);
        panel.add(zoomSlider);
        panel.add(zoomInBtn);

        this.add(panel);
    }

    public JButton createButton(String text, ActionListener listener){
        JButton button = new JButton(text);

        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.addActionListener(listener);

        return button;
    }


    public JSlider getSlider(){
        JSlider slider = new JSlider(50, 200, 100);

        slider.setMajorTickSpacing(50);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                parent.setScale(value/100.0F);
            }
        });

        return slider;
    }

}
