package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ChooseWindowSize extends JDialog {
    public boolean is_new = false;
    private final String[] items = {"3", "5", "7", "9", "11"};
    private final JComboBox value_type;

    public ChooseWindowSize(JFrame owner, int ind) {
        super(owner);
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ChooseWindowSize.this.setVisible(false);
            }
        });
        JPanel grid = new JPanel();
        GridLayout layout = new GridLayout(2, 2, 5, 12);
        grid.setLayout(layout);

        JLabel figure_type = new JLabel("Size of Gauss window");
        figure_type.setFont(new Font("Verdana", Font.PLAIN, 15));
        grid.add(figure_type);
        value_type = new JComboBox(items);
        value_type.setFont(new Font("Verdana", Font.PLAIN, 15));
        value_type.setSelectedIndex(ind / 2 - 1);
        grid.add(value_type);
        grid.add(new JLabel());

        JButton button1 = new JButton("OK");
        button1.setFont(new Font("Verdana", Font.PLAIN, 15));
        setLayout(new FlowLayout());
        grid.add(button1);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseWindowSize.this.is_new = true;
                ChooseWindowSize.this.setVisible(false);
            }
        });
        getContentPane().add(grid);
        pack();
        setVisible(true);
    }

    public String selectedSize() {
        if (value_type.getSelectedIndex() < items.length && value_type.getSelectedIndex() >= 0)
            return items[value_type.getSelectedIndex()];
        return "3";
    }
}
