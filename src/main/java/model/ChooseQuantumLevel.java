package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChooseQuantumLevel extends JDialog {
    private final String[] items = {"2", "4", "8", "16", "32", "64", "128"};
    private final JComboBox value_red;
    private final JComboBox value_green;
    private final JComboBox value_blue;
    public boolean is_new = false;

    public ChooseQuantumLevel(JFrame owner) {
        super(owner);
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setModalityType(ModalityType.TOOLKIT_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ChooseQuantumLevel.this.setVisible(false);
            }
        });
        JPanel grid = new JPanel();
        GridLayout layout = new GridLayout(5, 2, 5, 12);
        grid.setLayout(layout);

        JLabel figure_type = new JLabel("Level of quantization");
        figure_type.setFont(new Font("Verdana", Font.PLAIN, 15));
        grid.add(figure_type);
        grid.add(new Label(""));

        JLabel red_l = new JLabel("Red level");
        red_l.setFont(new Font("Verdana", Font.PLAIN, 15));
        value_red = new JComboBox(items);
        value_red.setFont(new Font("Verdana", Font.PLAIN, 15));
        //value_red.setSelectedIndex(ind / 2 - 1);
        grid.add(red_l);
        grid.add(value_red);

        JLabel green_l = new JLabel("Green level");
        green_l.setFont(new Font("Verdana", Font.PLAIN, 15));
        value_green = new JComboBox(items);
        value_green.setFont(new Font("Verdana", Font.PLAIN, 15));
        //value_red.setSelectedIndex(ind / 2 - 1);
        grid.add(green_l);
        grid.add(value_green);

        JLabel blue_l = new JLabel("Red level");
        blue_l.setFont(new Font("Verdana", Font.PLAIN, 15));
        value_blue = new JComboBox(items);
        value_blue.setFont(new Font("Verdana", Font.PLAIN, 15));
        //value_red.setSelectedIndex(ind / 2 - 1);
        grid.add(blue_l);
        grid.add(value_blue);

        JButton button1 = new JButton("OK");
        button1.setFont(new Font("Verdana", Font.PLAIN, 15));
        setLayout(new FlowLayout());
        grid.add(button1);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseQuantumLevel.this.is_new = true;
                ChooseQuantumLevel.this.setVisible(false);
            }
        });
        getContentPane().add(grid);
        pack();
        setVisible(true);
    }

    public int[] selectedValues() {
        return new int[]{Integer.parseInt(items[value_red.getSelectedIndex()]), Integer.parseInt(items[value_green.getSelectedIndex()]), Integer.parseInt(items[value_blue.getSelectedIndex()])};
    }
}
