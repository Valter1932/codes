import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class provaBatman extends JFrame {
    private static final int l = 10;
    private static final int cellaGrandezza = 50;
    private JButton[][] buttons = new JButton[l][l];
    private int[][] grid = new int[l][l];
    private int navitot = 10; 
    //private int player=1;

    public provaBatman() {
        setTitle("Battalia Navale");
        setSize(l * cellaGrandezza + 20, l * cellaGrandezza + 20);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(11, 10));


        JLabel vuoto=new JLabel(" ");
        gridPanel.add(vuoto);

        
        
        tabella();
        aggiungiNav();
        
        for(char c='A';c<'A'+l;c++){
            JLabel coordinates= new JLabel(Character.toString(c),SwingConstants.CENTER);
            coordinates.setPreferredSize(new Dimension(cellaGrandezza, cellaGrandezza));
            gridPanel.add(coordinates);
        }
        
        for (int i = 0;i < l; i++) {
            JLabel rigaN=new JLabel(Integer.toString(i),SwingConstants.CENTER);
            rigaN.setPreferredSize(new Dimension(cellaGrandezza, cellaGrandezza));
            gridPanel.add(rigaN);
           
            for (int j = 0; j < l; j++) {
                final int r = i;
                final int c = j;
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(cellaGrandezza, cellaGrandezza));
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        click(r, c);
                    }
                });
                gridPanel.add(buttons[i][j]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
        JLabel statusLabel = new JLabel("Affonda tutte le navi Navi rimanenti: " + navitot);
        add(statusLabel, BorderLayout.SOUTH);
        
    }

   
    private void click(int r, int c) {
        if (grid[r][c] == 1) { //Colpito nave
            buttons[r][c].setBackground(Color.RED);
            buttons[r][c].setText("X");
            grid[r][c] = -1; // Marca come colpito
            navitot--;
            
            if (navitot == 0) {
                System.out.println("u win");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this, "Colpito\n Navi rimaste: " + navitot);
            }
        } else if (grid[r][c] == 0) { // Acqua
            buttons[r][c].setBackground(Color.BLUE);
            buttons[r][c].setEnabled(false);
            System.out.println("nessuna nave colpita");
        }
        // -1 significa già colpito, non fare nulla
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new provaBatman().setVisible(true);
            }
        });
    }
    private void tabella() {
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                grid[i][j] = 0;
            }
        }
    }

    private void aggiungiNav() {
        Random rand = new Random();
        int naviPlace = 0;
        
        while (naviPlace < 5) { 
            int x = rand.nextInt(l);
            int y = rand.nextInt(l);
            
            if (grid[x][y] == 0) {
                grid[x][y] = 1;
                naviPlace++;
            }
        }
    }

}