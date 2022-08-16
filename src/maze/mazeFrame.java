/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package maze;

/**
 *
 * @author Mush
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class mazeFrame extends javax.swing.JFrame{
    Graphics g;
    
    // Variable to track if user won game
    boolean userWin = false;
    
    // Player icon
    static JLabel user;
    
    // Array to track user coordinates
    int[] userPos = {0, 0};
    
    // Maze Parameter Variables
    static int ROWS = 25;
    static int COLS = 25;
    static int[] START = {0, 0};
    static int[] FINISH = {ROWS - 1, COLS - 1};
    static int[][] maze = new Maze().generateMaze(ROWS, COLS, START, FINISH);
    
    // Variables used for solving
    static int[][] mazeDupe = new int[maze.length][maze[0].length];
    static int algoRuns = 0;
    
    boolean isGenerated = false;
    public mazeFrame() {
        initComponents();
        
        g = mazePanel.getGraphics();
        mazePanel.paintComponents(g);
        
        // Creates player icon
        user = new JLabel();
        user.setBounds(10,10,10,10);
        user.setBackground(Color.green);
        user.setOpaque(true);
        user.setVisible(false);
        mazePanel.add(user);
        
        // Adds Key Listener
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
    }  
    
    // Method for tracking key inputs
    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            int code = e.getKeyCode();
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (isGenerated && isArrowKey(code)) {
                    press(code);
                    move(code);
                    try {
                        sound();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                released(code);
            }
            return false;
        }
    }
    
    // Plays sound
    public void sound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String[] soundsList = {"click1", "click2", "click3"};
        
        // Chooses random sound 
        Random r = new Random();
        String randSound = soundsList[r.nextInt(soundsList.length)];;
        String soundName = "src/Sounds/" + randSound + ".wav";  
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        
        clip.start();
    }
    
    // Method for "animating" key press
    public void press(int code) {
        switch (code) {
            // UP
            case 38:
                ImageIcon up = new ImageIcon(getClass().getResource("/Images/buttonUpPressed.png")) ;
                upButtonLabel.setIcon(up);
                break;
            // DOWN
            case 40:
                ImageIcon down = new ImageIcon(getClass().getResource("/Images/buttonDownPressed.png")) ;
                downButtonLabel.setIcon(down);
                break;
            // LEFT
            case 37:
                ImageIcon left = new ImageIcon(getClass().getResource("/Images/buttonLeftPressed.png")) ;
                leftButtonLabel.setIcon(left);
                break;
            // RIGHT
            case 39:
                ImageIcon right = new ImageIcon(getClass().getResource("/Images/buttonRightPressed.png")) ;
                rightButtonLabel.setIcon(right);
                break;
        }
    }
    
    // Method to set icon back to original, finishing the animation
    public void released(int code) {
        switch (code) {
            // UP
            case 38:
                ImageIcon up = new ImageIcon(getClass().getResource("/Images/buttonUp.png")) ;
                upButtonLabel.setIcon(up);
                break;
            // DOWN
            case 40:
                ImageIcon down = new ImageIcon(getClass().getResource("/Images/buttonDown.png")) ;
                downButtonLabel.setIcon(down);
                break;
            // LEFT
            case 37:
                ImageIcon left = new ImageIcon(getClass().getResource("/Images/buttonLeft.png")) ;
                leftButtonLabel.setIcon(left);
                break;
            // RIGHT
            case 39:
                ImageIcon right = new ImageIcon(getClass().getResource("/Images/buttonRight.png")) ;
                rightButtonLabel.setIcon(right);
                break;
        }
    }
    
    // Method to validate if user clicked an arrow key
    public boolean isArrowKey(int code) {
        return (code <= 40 && code >= 37);
    }
    
    // Moves players icon on the board
    public void move(int code) {
        // Pixels to move the player icon
        final int moveLength = 30;
        if (isValidMove(code) && !userWin) {
            addScore();
            switch (code) {
                // UP
                case 38:
                    user.setLocation(user.getX(), user.getY()-moveLength);
                    break;
                // DOWN
                case 40:
                    user.setLocation(user.getX(), user.getY()+moveLength);
                    break;
                // LEFT
                case 37:
                    user.setLocation(user.getX()-moveLength, user.getY());
                    break;
                // RIGHT
                case 39:
                    user.setLocation(user.getX()+moveLength, user.getY());
                    break;
            }
            if (didWin()){
                userWin = true;
                showWin();
            }
        }
    }
    
    // Checks if user won
    public boolean didWin() {
        return ((userPos[0] == FINISH[0]) && (userPos[1] == FINISH[1]));
    }
    
    // Shows won panel
    public void showWin() {
        wonLabel.setText("YOU WON!");
    }
    
    // Increments users score
    public void addScore() {
        int currentScore = Integer.parseInt(scoreCounter.getText());
        scoreCounter.setText(String.valueOf(currentScore + 1));
    }
    
    // Checks if the user has a path in the position they want to go
    public boolean isValidMove(int code) {
        int[] pos = {0,0};
        pos[0] = userPos[0];
        pos[1] = userPos[1];
        switch (code) {
            case 38 -> pos[1] -= 1; // UP
            case 40 -> pos[1] += 1; // DOWN
            case 37 -> pos[0] -= 1; // LEFT
            case 39 -> pos[0] += 1; // RIGHT
        }
        
        int x = pos[0];
        int y = pos[1];
        
        if (isValidCoord(pos) && maze[x][y] != 0) {
            userPos[0] = pos[0];
            userPos[1] = pos[1];
            return true; 
        }
        return false;
    }
    
    // Makes sure the position the user wants to go is within the maze array
    // Prevents array out of bounds exception
    public boolean isValidCoord(int[] pos) {
        int x = pos[0];
        int y = pos[1];
        return ((x < ROWS && x >= 0) && (y < COLS && y >= 0));
    }
    
    public boolean solveMaze(int x, int y) {
        // Tracks how many steps the algo took
        algoRuns++;
        
        // Checks if in bounds
        if(x >= ROWS || x < 0 || y >= COLS || y < 0) return false;
        
        // If position is a 4 it means we already traversed this path
        else if (mazeDupe[x][y] == 4) return false;
        
        // If position is a 3, we found the solution
        else if (mazeDupe[x][y] == 3) return true;
        
        // If position is a 0, we hit a wall
        else if (mazeDupe[x][y] == 0) return false;
        
        else {
            // Mark position as visited
            mazeDupe[x][y] = 4;
            /* Recursively check each direction
             * If there is a path it will continue
             * If not then reset the position
             * Checks up, down, left, right
            */
            if (solveMaze(x,y-1)) return true;
            if (solveMaze(x,y+1)) return true;
            if (solveMaze(x-1,y)) return true;
            if (solveMaze(x+1,y)) return true;
            
            // Set all previous spots visited to 5
            mazeDupe[x][y] = 5;
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundJPanel = new javax.swing.JPanel();
        titleJLabel = new javax.swing.JLabel();
        titleJLabel1 = new javax.swing.JLabel();
        genMazeButton = new javax.swing.JLabel();
        resetMazeButton = new javax.swing.JLabel();
        solveMazeButton = new javax.swing.JLabel();
        scoreTitleLabel = new javax.swing.JLabel();
        scoreCounter = new javax.swing.JLabel();
        algoScoreTitleLabel = new javax.swing.JLabel();
        algoScoreCounter = new javax.swing.JLabel();
        wonLabel = new javax.swing.JLabel();
        upButtonLabel = new javax.swing.JLabel();
        leftButtonLabel = new javax.swing.JLabel();
        downButtonLabel = new javax.swing.JLabel();
        rightButtonLabel = new javax.swing.JLabel();
        mazePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(69, 69, 69));
        setResizable(false);

        backgroundJPanel.setBackground(new java.awt.Color(52, 52, 52));

        titleJLabel.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        titleJLabel.setText("Mush's ");

        titleJLabel1.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        titleJLabel1.setText("Maze Game");

        genMazeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/genMazeButton.png"))); // NOI18N
        genMazeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genMazeButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                genMazeButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                genMazeButtonMouseReleased(evt);
            }
        });

        resetMazeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/resetMazeButton.png"))); // NOI18N
        resetMazeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetMazeButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                resetMazeButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resetMazeButtonMouseReleased(evt);
            }
        });

        solveMazeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/solveMazeButton.png"))); // NOI18N
        solveMazeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                solveMazeButtonMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                solveMazeButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                solveMazeButtonMouseReleased(evt);
            }
        });

        scoreTitleLabel.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        scoreTitleLabel.setText("Score: ");

        scoreCounter.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        scoreCounter.setForeground(new java.awt.Color(204, 0, 51));
        scoreCounter.setText("0");

        algoScoreTitleLabel.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        algoScoreTitleLabel.setText("PC Score: ");

        algoScoreCounter.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        algoScoreCounter.setForeground(new java.awt.Color(204, 0, 51));
        algoScoreCounter.setText("0");

        wonLabel.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        wonLabel.setForeground(new java.awt.Color(204, 0, 51));

        upButtonLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buttonUp.png"))); // NOI18N

        leftButtonLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buttonLeft.png"))); // NOI18N

        downButtonLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buttonDown.png"))); // NOI18N

        rightButtonLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buttonRight.png"))); // NOI18N

        mazePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 51), 2));
        mazePanel.setOpaque(false);

        javax.swing.GroupLayout mazePanelLayout = new javax.swing.GroupLayout(mazePanel);
        mazePanel.setLayout(mazePanelLayout);
        mazePanelLayout.setHorizontalGroup(
            mazePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 749, Short.MAX_VALUE)
        );
        mazePanelLayout.setVerticalGroup(
            mazePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 745, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout backgroundJPanelLayout = new javax.swing.GroupLayout(backgroundJPanel);
        backgroundJPanel.setLayout(backgroundJPanelLayout);
        backgroundJPanelLayout.setHorizontalGroup(
            backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(genMazeButton)
                                    .addComponent(resetMazeButton, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(solveMazeButton, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(wonLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                                .addComponent(algoScoreTitleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(algoScoreCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                                .addComponent(scoreTitleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(scoreCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 71, Short.MAX_VALUE))
                                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(upButtonLabel)
                                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                                .addComponent(leftButtonLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(downButtonLabel)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rightButtonLabel)
                                        .addGap(28, 28, 28)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(titleJLabel))
                            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(titleJLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(mazePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        backgroundJPanelLayout.setVerticalGroup(
            backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundJPanelLayout.createSequentialGroup()
                .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(titleJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(titleJLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(genMazeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(resetMazeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(solveMazeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scoreTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scoreCounter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(algoScoreTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(algoScoreCounter))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wonLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upButtonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(backgroundJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(leftButtonLabel)
                            .addComponent(downButtonLabel)
                            .addComponent(rightButtonLabel)))
                    .addGroup(backgroundJPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mazePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundJPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Generate Maze Button Clicked
    private void genMazeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genMazeButtonMouseClicked
        // Plays Sound
        try {
            sound();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Resets variables and generates new maze
        wonLabel.setText("");
        user.setVisible(false);
        userWin = false;
        maze = new Maze().generateMaze(ROWS, COLS, START, FINISH);
        
        // Loops through maze array and draws everything in
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if (j == START[0] && i == START[1]) g.setColor(Color.yellow);	
                else if(maze[j][i]==0) g.setColor(Color.black);	
                else if(maze[j][i]==1) g.setColor(Color.white);	
                else if(maze[j][i]==3) g.setColor(Color.red);
                g.fillRect(j*30,i*30,30,30);
                mazeDupe[j][i] = maze[j][i];
            }
        }
        
        // Resets user icon and variables
        user.setLocation(10,10);
        userPos[0] = START[0];
        userPos[1] = START[1];
        user.setVisible(true);
        isGenerated = true;
        
        scoreCounter.setText("0");
        algoScoreCounter.setText("0");
        algoRuns = 0;
    }//GEN-LAST:event_genMazeButtonMouseClicked
    
    // Animates Generate Maze Click
    private void genMazeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genMazeButtonMousePressed
        // Animates Click
        ImageIcon genMazePressed = new ImageIcon(getClass().getResource("/Images/genMazePressed.png")) ;
        genMazeButton.setIcon(genMazePressed);
    }//GEN-LAST:event_genMazeButtonMousePressed

    // Animates Generate Maze UnClicked
    private void genMazeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genMazeButtonMouseReleased
        // Animates Click
        ImageIcon genMazeButtonNormal = new ImageIcon(getClass().getResource("/Images/genMazeButton.png")) ;
        genMazeButton.setIcon(genMazeButtonNormal);
    }//GEN-LAST:event_genMazeButtonMouseReleased

    private void resetMazeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetMazeButtonMouseClicked
        // Plays Sound
        try {
            sound();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Resets variables
        user.setVisible(false);
        userWin = false;
        wonLabel.setText("");
        
        // Loops through maze array and draws everything in
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                if (j == START[0] && i == START[1]) g.setColor(Color.yellow);	
                else if(maze[j][i]==0) g.setColor(Color.black);	
                else if(maze[j][i]==1) g.setColor(Color.white);	
                else if(maze[j][i]==3) g.setColor(Color.red);
                g.fillRect(j*30,i*30,30,30);	
                mazeDupe[j][i] = maze[j][i];
            }
        }
        
        // Resets user icon
        user.setLocation(10,10);
        userPos[0] = START[0];
        userPos[1] = START[1];
        user.setVisible(true);
        
        // Resets Score
        scoreCounter.setText("0");
        algoScoreCounter.setText("0");
        algoRuns = 0;
        
    }//GEN-LAST:event_resetMazeButtonMouseClicked

    private void resetMazeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetMazeButtonMousePressed
        // Animates Click
        ImageIcon resetMazePressed = new ImageIcon(getClass().getResource("/Images/resetMazePressed.png")) ;
        resetMazeButton.setIcon(resetMazePressed);
    }//GEN-LAST:event_resetMazeButtonMousePressed

    private void resetMazeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetMazeButtonMouseReleased
        ImageIcon resetMazeNormal = new ImageIcon(getClass().getResource("/Images/resetMazeButton.png")) ;
        resetMazeButton.setIcon(resetMazeNormal);
    }//GEN-LAST:event_resetMazeButtonMouseReleased

    private void solveMazeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveMazeButtonMouseClicked
        // Plays Sound
        try {
            sound();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(mazeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        solveMaze(0,0);
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {	
                // 4 = Solution
                if(mazeDupe[j][i]==4) {
                    g.setColor(Color.red);
                    g.fillRect(j*30,i*30,30,30);
                } 
                // 5 = All traversed paths
                else if (mazeDupe[j][i]==5) {
                    g.setColor(Color.pink);
                    g.fillRect(j*30,i*30,30,30);
                }
            }
        }
        
        // Displays counter
        algoScoreCounter.setText(String.valueOf(algoRuns));
        
    }//GEN-LAST:event_solveMazeButtonMouseClicked

    private void solveMazeButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveMazeButtonMousePressed
        // Animates Click
        ImageIcon solveMazePressed = new ImageIcon(getClass().getResource("/Images/solveMazePressed.png")) ;
        solveMazeButton.setIcon(solveMazePressed);
    }//GEN-LAST:event_solveMazeButtonMousePressed

    private void solveMazeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveMazeButtonMouseReleased
        // Animates Click
        ImageIcon solveMazeNormal = new ImageIcon(getClass().getResource("/Images/solveMazeButton.png")) ;
        solveMazeButton.setIcon(solveMazeNormal);
    }//GEN-LAST:event_solveMazeButtonMouseReleased

   
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mazeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mazeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mazeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mazeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mazeFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel algoScoreCounter;
    private javax.swing.JLabel algoScoreTitleLabel;
    private javax.swing.JPanel backgroundJPanel;
    private javax.swing.JLabel downButtonLabel;
    private javax.swing.JLabel genMazeButton;
    private javax.swing.JLabel leftButtonLabel;
    private javax.swing.JPanel mazePanel;
    private javax.swing.JLabel resetMazeButton;
    private javax.swing.JLabel rightButtonLabel;
    private javax.swing.JLabel scoreCounter;
    private javax.swing.JLabel scoreTitleLabel;
    private javax.swing.JLabel solveMazeButton;
    private javax.swing.JLabel titleJLabel;
    private javax.swing.JLabel titleJLabel1;
    private javax.swing.JLabel upButtonLabel;
    private javax.swing.JLabel wonLabel;
    // End of variables declaration//GEN-END:variables

  
}
