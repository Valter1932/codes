import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class provaBatman2 extends JFrame {
    private final int GRID_SIZE = 10;
    private final int CELL_SIZE = 50;
    private final int SHIP_SIZES[] = {5, 4, 3, 3, 2};
    private final Color COLOR_WATER = new Color(173, 216, 230);
    private final Color COLOR_SHIP = Color.GRAY;
    private final Color COLOR_HIT = Color.RED;
    private final Color COLOR_MISS = Color.WHITE;
    
    private JPanel Pplayer;
    private JPanel Pcomputer;
    private JLabel main;
    
    private int[][] playerGrid;
    private int[][] computerGrid;
    private int[][] guessCOMgrid;
    
    private boolean turno = true;
    private int naviKOP = 0;
    private int naviKOC = 0;
    
    public provaBatman2() {
        setTitle("Battaglia Navale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Inizializza le griglie
        playerGrid = new int[GRID_SIZE][GRID_SIZE];
        computerGrid = new int[GRID_SIZE][GRID_SIZE];
        guessCOMgrid = new int[GRID_SIZE][GRID_SIZE];
        
        // Pannello giocatore
        Pplayer = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        Pplayer.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        
        // Pannello computer
        Pcomputer = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        Pcomputer.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        
        // Status label
        main = new JLabel("Posiziona le tue navi. Clicca sulle celle per posizionare.", SwingConstants.CENTER);
        
        // Aggiungi componenti al frame
        add(Pplayer, BorderLayout.WEST);
        add(Pcomputer, BorderLayout.EAST);
        add(main, BorderLayout.SOUTH);
        
        // Posiziona le navi del computer
        piazzaNaviCOM();
        
        // Inizializza le griglie grafiche
        startGridP();
        startCOMP();


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void piazzaNaviCOM() {
        Random rand = new Random();
        
        for (int shipSize : SHIP_SIZES) {
            boolean piazzato = false;
            
            while (!piazzato) {
                boolean h = rand.nextBoolean();
                int row = rand.nextInt(GRID_SIZE);
                int col = rand.nextInt(GRID_SIZE);
                
                if (checkplaceShip(computerGrid, row, col, shipSize, h)) {
                    for (int i = 0; i < shipSize; i++) {
                        if (h) {
                            computerGrid[row][col + i] = shipSize; // Usiamo shipSize come ID
                        } else {
                            computerGrid[row + i][col] = shipSize;
                        }
                    }
                    piazzato = true;
                }
            }
        }
    }
    
    private boolean checkplaceShip(int[][] grid, int row, int col, int size, boolean h) {
        if (h) {
            if (col + size > GRID_SIZE) return false;
            for (int i = 0; i < size; i++) {
                if (grid[row][col + i] != 0) return false;
            }
        } else {
            if (row + size > GRID_SIZE) return false;
            for (int i = 0; i < size; i++) {
                if (grid[row + i][col] != 0) return false;
            }
        }
        return true;
    }
    
    private void startGridP() {
        Pplayer.removeAll();
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                button.setBackground(COLOR_WATER);
                
                final int r = row;
                final int c = col;
                
                button.addActionListener(e -> {
                    if (playerGrid[r][c] == 0) {
                        playerGrid[r][c] = 1; // 1 rappresenta una nave
                        button.setBackground(COLOR_SHIP);
                    } else {
                        playerGrid[r][c] = 0;
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
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                button.setBackground(COLOR_WATER);
                
                final int r = row;
                final int c = col;
                
                button.addActionListener(e -> {
                    if (turno) {
                        makePlayerMove(r, c, button);
                    }
                });
                
                Pcomputer.add(button);
            }
        }
        
        Pcomputer.revalidate();
        Pcomputer.repaint();
    }
    
    private void makePlayerMove(int row, int col, JButton button) {
        if (computerGrid[row][col] > 0) { // headshot
            button.setBackground(COLOR_HIT);
            button.setText("X");
            computerGrid[row][col] *= -1; // u suuck
            
            // check se la nave è affondata
            if (checknaveAffondata(computerGrid, row, col)) {
                naviKOC++;
                main.setText("Hai affondato una nave! " + (SHIP_SIZES.length - naviKOC) + " rimaste.");
            } else {
                main.setText("Hai colpito una nave! Tocca ancora a te.");
            }
            
            // ck won
            if (naviKOC == SHIP_SIZES.length) {
                JOptionPane.showMessageDialog(this, "Hai vinto! Tutte le navi nemiche sono state affondate.");
                System.exit(0);
            }
        } else { //  ai missato ahahahaha
            button.setBackground(COLOR_MISS);
            button.setText("O");
            main.setText("Acqua! Tocca al computer.");
            turno = false;
            computerMove();
        }
    }
    
    private boolean checknaveAffondata(int[][] grid, int row, int col) {
        int shipSize = Math.abs(grid[row][col]);
        
        // Trova tutte le parti della nave
        boolean h = true;
        if (col > 0 && Math.abs(grid[row][col-1]) == shipSize) h = true;
        else if (row > 0 && Math.abs(grid[row-1][col]) == shipSize) 
             h = false;
        else if (col < GRID_SIZE-1 && Math.abs(grid[row][col+1]) == shipSize) 
             h = true;
        else if (row < GRID_SIZE-1 && Math.abs(grid[row+1][col]) == shipSize) 
            h = false;
        
        // Controlla se tutte le parti sono colpite
        if (h) {
            // Trova l'inizio della nave
            int startNaveCol = col;
            while (startNaveCol > 0 && Math.abs(grid[row][startNaveCol-1]) == shipSize) startNaveCol--;
            
            // Controlla tutte le celle
            for (int c = startNaveCol; c < startNaveCol + shipSize; c++) {
                if (c >= GRID_SIZE || grid[row][c] > 0) return false;
            }
        } else {
            // Trova l'inizio della nave
            int iniziaNaveR = row;
            while (iniziaNaveR > 0 && Math.abs(grid[iniziaNaveR-1][col]) == shipSize) iniziaNaveR--;
            
            // Controlla tutte le celle
            for (int r = iniziaNaveR; r < iniziaNaveR + shipSize; r++) {
                if (r >= GRID_SIZE || grid[r][col] > 0) return false;
            }
        }
        
        return true;
    }
    
    private void computerMove() {
        Random rand = new Random();
        boolean validMove = false;
        
        while (!validMove) {
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);
            
            if (guessCOMgrid[row][col] == 0) {
                validMove = true;
                guessCOMgrid[row][col] = 1;
                
                if (playerGrid[row][col] > 0) { // Colpito
                    playerGrid[row][col] *= -1;
                    JButton button = (JButton) Pplayer.getComponent(row * GRID_SIZE + col);
                    button.setBackground(COLOR_HIT);
                    button.setText("X");
                    
                    if (checknaveAffondata(playerGrid, row, col)) {
                        naviKOP++;
                        main.setText("Il computer ha affondato una tua nave! " + (SHIP_SIZES.length - naviKOP) + " rimaste.");
                    } else {
                        main.setText("Il computer ha colpito una tua nave! Tocca ancora a lui.");
                    }
                    
                    // Controlla loser
                    if (naviKOP == SHIP_SIZES.length) {
                        JOptionPane.showMessageDialog(this, "Hai perso! Tutte le tue navi sono state affondate.");
                        System.exit(0);
                    }
                    
                    computerMove(); // extra turn per il computer se colpisce
                } else { // missed!!?
                    JButton button = (JButton) Pplayer.getComponent(row * GRID_SIZE + col);
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