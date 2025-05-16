/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provabatman2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class provaBatman2 extends JFrame {
    private  int G = 10;
    private  int cellSize = 50;
    private  int navi_sizes[] = {5, 4, 3, 3, 2};
    private  Color COLOR_WATER = Color.CYAN;
    private  Color COLOR_SHIP = Color.GRAY;
    private  Color COLOR_HIT = Color.RED;
    private  Color COLOR_MISS = Color.WHITE;
    
    private JPanel Pplayer;
    private JPanel Pcomputer;
    private JLabel main;
    
    private int[][] Gplayer;
    private int[][] Gcomputer;
    private int[][] guessCOMgrid;
    
    private boolean turno = true;
    private int naviKOP = 0;
    private int naviKOC = 0;
    
    public provaBatman2() {
        setTitle("Battaglia Navale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        Gplayer = new int[G][G];
        Gcomputer = new int[G][G];
        guessCOMgrid = new int[G][G];
        
        Pplayer = new JPanel(new GridLayout(G, G));
        Pplayer.setPreferredSize(new Dimension(G * cellSize, G * cellSize));
        
        Pcomputer = new JPanel(new GridLayout(G, G));
        Pcomputer.setPreferredSize(new Dimension(G * cellSize, G * cellSize));
        
        main = new JLabel("Posiziona le tue navi. Clicca sulle celle per posizionare.", SwingConstants.CENTER);
        
        add(Pplayer, BorderLayout.WEST);
        add(Pcomputer, BorderLayout.EAST);
        add(main, BorderLayout.SOUTH);
        
        piazzaNaviCOM();
        
        startP();
        startCOMP();


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void piazzaNaviCOM() {
        Random rand = new Random();
        
        for (int shipSize=0;shipSize<navi_sizes.length;shipSize++) {
            boolean piazzato = false;
            
            while (!piazzato) {
                boolean h = rand.nextBoolean();
                int row = rand.nextInt(G);
                int col = rand.nextInt(G);
                
                if (checkplaceShip(Gcomputer, row, col, shipSize, h)) {
                    for (int i = 0; i < shipSize; i++) {
                        if (h) {
                            Gcomputer[row][col + i] = shipSize; 
                        } else {
                            Gcomputer[row + i][col] = shipSize;
                        }
                    }
                    piazzato = true;
                }
            }
        }
    }
    
    private boolean checkplaceShip(int[][] grid, int row, int col, int size, boolean h) {
        if (h) {
            if (col + size > G) 
                return false;
            for (int i = 0; i < size; i++) {
                if (grid[row][col + i] != 0) return false;
            }
        } else {
            if (row + size > G) 
                return false;
            for (int i = 0; i < size; i++) {
                if (grid[row + i][col] != 0) return false;
            }
        }
        return true;
    }
    
    private void startP() {
        Pplayer.removeAll();
        
        for (int row = 0; row < G; row++) {
            for (int col = 0; col < G; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(cellSize, cellSize));
                button.setBackground(COLOR_WATER);
                
                 int r = row;
                 int c = col;
                
                button.addActionListener((ActionEvent e) -> {
                    if(Gplayer[r][c] ==0){
                        Gplayer[r][c] = 1;
                        button.setBackground(COLOR_SHIP);
                    } else{
                        Gplayer[r][c]=0;
                        button.setBackground(COLOR_WATER);      
                    }
                });
                
                Pplayer.add(button);
            }
        }
        
        Pplayer.revalidate();
        Pplayer.repaint();
        
    }
    
    private void startCOMP() {
        Pcomputer.removeAll();
        
        for (int row = 0; row < G; row++) {
            for (int col = 0; col < G; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(cellSize, cellSize));
                button.setBackground(COLOR_WATER);
                
                 int r = row;
                 int c = col;
                
                button.addActionListener((ActionEvent e) -> {
                    if(turno && Gcomputer[r][c]>=0){
                        muoviPlayer(r,c,button);
                        
                    }
                });
                
                Pcomputer.add(button);
            }
        }
        
        Pcomputer.revalidate();
        Pcomputer.repaint();
    }
    
    private void muoviPlayer(int row, int col, JButton button) {
        if(Gcomputer[row][col]  <0 || button.getText().equals("O") || button.getText().equals("X")){
            main.setText("cella giÃ  colpita. prova un'altra cella");
            return;
        }
            
        if (Gcomputer[row][col] > 0) { // headshot
            button.setBackground(COLOR_HIT);
            button.setText("X");
            Gcomputer[row][col] *= -1; // u suuck
            
            // check se la nave Ã¨ affondata
            if (checknaveAffondata(Gcomputer, row, col)) {
                naviKOC++;
                main.setText("Hai affondato una nave! " + (navi_sizes.length - naviKOC) + " rimaste.");
            } else {
                main.setText("Hai colpito una nave! Tocca ancora a te.");
            }
            
            // ck won
            if (naviKOC == navi_sizes.length) {
                JOptionPane.showMessageDialog(this, "Hai vinto! Tutte le navi nemiche sono state affondate.");
                System.exit(0);
            }
        } else { //  ai missato ahahahaha
            button.setBackground(COLOR_MISS);
            button.setText("O");
            main.setText("Acqua! Tocca al computer.");
            turno = false;
            muoviCOM();
        }
    }
    
    private boolean checknaveAffondata(int[][] grid, int row, int col) {
        int shipSize = Math.abs(grid[row][col]);
        
        // Trova tutte le parti della nave
        boolean h = true;
        if (col > 0 && Math.abs(grid[row][col-1]) == shipSize) h = true;
        else if (row > 0 && Math.abs(grid[row-1][col]) == shipSize) 
             h = false;
        else if (col < G-1 && Math.abs(grid[row][col+1]) == shipSize) 
             h = true;
        else if (row < G-1 && Math.abs(grid[row+1][col]) == shipSize) 
            h = false;
        
        // Controlla se tutte le parti sono colpite
        if (h) {
            int startNaveCol = col;
            while (startNaveCol > 0 && Math.abs(grid[row][startNaveCol-1]) == shipSize) startNaveCol--;
            
            for (int c = startNaveCol; c < startNaveCol + shipSize; c++) {
                if (c >= G || grid[row][c] > 0) return false;
            }
        } else {
            int iniziaNaveR = row;
            while (iniziaNaveR > 0 && Math.abs(grid[iniziaNaveR-1][col]) == shipSize) iniziaNaveR--;
            
            for (int r = iniziaNaveR; r < iniziaNaveR + shipSize; r++) {
                if (r >= G || grid[r][col] > 0) return false;
            }
        }
        
        return true;
    }
    
    private void muoviCOM() {
        Random rand = new Random();
        boolean validMove = false;
        
        while (!validMove) {
            int row = rand.nextInt(G);
            int col = rand.nextInt(G);
            
            if (guessCOMgrid[row][col] == 0) {
                validMove = true;
                guessCOMgrid[row][col] = 1;
                
                if (Gplayer[row][col] > 0) { // hit
                    Gplayer[row][col] *= -1;
                    JButton button = (JButton) Pplayer.getComponent(row * G + col);
                    button.setBackground(COLOR_HIT);
                    button.setText("X");
                    
                    if (checknaveAffondata(Gplayer, row, col)) {
                        naviKOP++;
                        main.setText("Il computer ha affondato una tua nave! " + (navi_sizes.length - naviKOP) + " rimaste.");
                    } else {
                        main.setText("Il computer ha colpito una tua nave! Tocca ancora a lui.");
                    }
                    
                    // Controlla loser
                    if (naviKOP == navi_sizes.length) {
                        JOptionPane.showMessageDialog(this, "Hai perso! Tutte le tue navi sono state affondate.");
                        System.exit(0);
                    }
                    
                    muoviCOM(); // extra turn per il computer se colpisce
                } else { // missed!!?
                    JButton button = (JButton) Pplayer.getComponent(row * G + col);
                    button.setBackground(COLOR_MISS);
                    button.setText("O");
                    main.setText("Il computer ha mancato! Tocca a te.");
                    turno = true;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        new provaBatman2();
    }
}
