package gameoflife;

import java.awt.*;
import java.awt.event.*;
import static java.awt.event.KeyEvent.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.*;

public class View implements ModelListener{
    private final Model model;
    private Controller controller;
    Dimension btnDim = new Dimension(30, 26);
    JFrame frame;
    Canvas canvasPanel;
    JPanel btnPanel;
    final ImageIcon icoFill = new ImageIcon(View.class.getResource("img/btnFill.png"));
    final ImageIcon icoNew = new ImageIcon(View.class.getResource("img/btnNew.png"));
    final ImageIcon icoOpen = new ImageIcon(View.class.getResource("img/btnOpen.png"));
    final ImageIcon icoSave = new ImageIcon(View.class.getResource("img/btnSave.png"));
    final ImageIcon icoStep = new ImageIcon(View.class.getResource("img/btnStep.png"));
    final ImageIcon icoGo = new ImageIcon(View.class.getResource("img/btnGo.png"));
    final ImageIcon icoStop = new ImageIcon(View.class.getResource("img/btnStop.png"));
    final ImageIcon icoFaster = new ImageIcon(View.class.getResource("img/btnFaster.png"));
    final ImageIcon icoSlower = new ImageIcon(View.class.getResource("img/btnSlower.png"));
    final ImageIcon icoColor = new ImageIcon(View.class.getResource("img/btnColor.png"));
    final ImageIcon icoNoColor = new ImageIcon(View.class.getResource("img/btnNoColor.png"));
    final ImageIcon icoGrid = new ImageIcon(View.class.getResource("img/btnGrid.png"));
    JButton btnFill, btnNew, btnOpen, btnSave, btnStep, btnGo, btnStop, btnFaster, btnSlower, btnColor, btnGrid;
    
    public static void main(String[] args){
        View mainWindow = new View();
        mainWindow.go();
        Rectangle rect = new Rectangle();
    }

    public void go(){
        model.addListener(this);        
        frame.setVisible(true);
        controller.run();
    }
    public View() {
        model = new Model();        
        controller = new Controller(model);
        frame = new JFrame(Constants.TITLE_OF_GAME_WINDOW);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constants.FIELD_SIZE, Constants.FIELD_SIZE + Constants.BTN_PANEL_HEIGHT);
        frame.setLocation(Constants.START_LOCATION_X, Constants.START_LOCATION_Y);
        frame.setResizable(false);
        frame.setFocusable(true);
        frame.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e){
                switch(e.getKeyCode()){
                    case VK_R:
                        controller.fillRandom();                        
                        break;
                    case VK_N :
                        controller.clearField();
                        break;
                    case VK_O :
                        readFromFile();
                        break;
                    case VK_K :
                        saveToFile();
                        break;
                    case VK_RIGHT :
                        controller.oneStep();
                        break;
                    case VK_A :
                        controller.go();
                        break;
                    case VK_S :
                        controller.stop();
                        break;
                    case VK_UP :
                        System.out.println("UP");
                        controller.faster();
                        break;
                    case VK_DOWN :
                        System.out.println("DOWN");
                        controller.slower();
                        break;
                    case VK_C :
                        controller.color();
                        break;
                    case VK_G :
                        controller.grid();
                        break;
                    case VK_ESCAPE :
                        System.exit(0);
                        break;
                    default :
                        break;
                }
            }
        }
        );
        createButtons();
        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                int cursor_x = e.getX() / Constants.POINT_RADIUS;
                int cursor_y = e.getY() / Constants.POINT_RADIUS;
                controller.toggleCell(cursor_x, cursor_y);
            }
        });
        canvasPanel.setVisible(true);
        canvasPanel.setFocusable(false);
        btnPanel = new JPanel();
        btnPanel.add(btnFill);
        btnPanel.add(btnNew);
        btnPanel.add(btnOpen);
        btnPanel.add(btnSave);
        btnPanel.add(btnStep);
        btnPanel.add(btnGo);
        btnPanel.add(btnStop);
        btnPanel.add(btnFaster);
        btnPanel.add(btnSlower);
        btnPanel.add(btnColor);
        btnPanel.add(btnGrid);
        btnPanel.setVisible(true);
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.getContentPane().add(BorderLayout.NORTH, btnPanel);
    }
    
    private void createButtons(){
        btnFill = new JButton();
        btnFill.setIcon(icoFill);
        btnFill.setPreferredSize(btnDim);
        btnFill.setToolTipText("Fill randomly");
        btnFill.setFocusable(false);
        btnFill.addActionListener((ActionEvent e) -> {
            controller.fillRandom();
        });
        
        btnNew = new JButton();
        btnNew.setIcon(icoNew);
        btnNew.setPreferredSize(btnDim);
        btnNew.setToolTipText("Clear field");
        btnNew.setFocusable(false);
        btnNew.addActionListener((ActionEvent e) -> {
           controller.clearField();
        });
        
        btnOpen = new JButton();
        btnOpen.setIcon(icoOpen);
        btnOpen.setPreferredSize(btnDim);
        btnOpen.setToolTipText("Open saved file");
        btnOpen.setFocusable(false);
        btnOpen.addActionListener((ActionEvent e) -> {
           readFromFile();
        });
        
        btnSave = new JButton();
        btnSave.setIcon(icoSave);
        btnSave.setPreferredSize(btnDim);
        btnSave.setToolTipText("Save to file");
        btnSave.setFocusable(false);
        btnSave.addActionListener((ActionEvent e) -> {
            saveToFile();
        });
        
        btnStep = new JButton();
        btnStep.setIcon(icoStep);
        btnStep.setPreferredSize(btnDim);
        btnStep.setToolTipText("Show next generation");
        btnStep.setFocusable(false);
        btnStep.addActionListener((ActionEvent e) -> {
            controller.oneStep();
        });
        
        btnGo = new JButton();
        btnGo.setIcon(icoGo);
        btnGo.setPreferredSize(btnDim);
        btnGo.setToolTipText("Toggle automode on");
        btnGo.setFocusable(false);
        btnGo.addActionListener((ActionEvent e) -> {
            controller.go();
        });
        
        btnStop = new JButton();
        btnStop.setIcon(icoStop);
        btnStop.setPreferredSize(btnDim);
        btnStop.setToolTipText("Toggle automode off");
        btnStop.setFocusable(false);
        btnStop.addActionListener((ActionEvent e) -> {
            controller.stop();
        });
        
        btnFaster = new JButton();
        btnFaster.setIcon(icoFaster);
        btnFaster.setPreferredSize(btnDim);
        btnFaster.setToolTipText("Faster");
        btnFaster.setFocusable(false);
        btnFaster.addActionListener((ActionEvent e) -> {
            controller.faster();
        });
        
        btnSlower = new JButton();
        btnSlower.setIcon(icoSlower);
        btnSlower.setPreferredSize(btnDim);
        btnSlower.setToolTipText("Slower");
        btnSlower.setFocusable(false);
        btnSlower.addActionListener((ActionEvent e) -> {
            controller.slower();
        });
        
        btnColor = new JButton();
        btnColor.setIcon(icoColor);
        btnColor.setPreferredSize(btnDim);
        btnColor.setToolTipText("switch the color mode on/off");
        btnColor.setFocusable(false);
        btnColor.addActionListener((ActionEvent e) -> {
            controller.color();
            if(model.isColoredMode()) {
                btnColor.setIcon(icoColor);
            } else {
                btnColor.setIcon(icoNoColor);
            }
        });
        
        btnGrid = new JButton();
        btnGrid.setIcon(icoGrid);
        btnGrid.setPreferredSize(btnDim);
        btnGrid.setToolTipText("Show/hide the grid");
        btnGrid.setFocusable(false);
        btnGrid.addActionListener((ActionEvent e) -> {
            controller.grid();
        });
    }
    
    @Override
    public void updateView() {
        canvasPanel.repaint();
    }

    public void readFromFile() {
        JFileChooser openDialog = new JFileChooser(".");
        openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        openDialog.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()){
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(Constants.EXIT_FILE_EXTENTION);
                }
            }
            @Override
            public String getDescription() {
                return "Saved GameOfLife files (*" + Constants.EXIT_FILE_EXTENTION + ")";
            }
        
        });
        openDialog.setAcceptAllFileFilterUsed(true);
        int result = openDialog.showOpenDialog(frame);
        if(result == JFileChooser.APPROVE_OPTION){
            String absPath = openDialog.getSelectedFile().getAbsolutePath();
            //FileInputStream fis = new FileInputStream(absPath);
            //ObjectInputStream ois = new ObjectInputStream(fis);
            controller.readFromFile(absPath);
        }
    }

    public void saveToFile() {
        JFileChooser saveDialog = new JFileChooser(".");
        saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveDialog.setFileFilter(new FileNameExtensionFilter("Game of live files (*." + Constants.EXIT_FILE_EXTENTION + ")" , Constants.EXIT_FILE_EXTENTION));
        int result = saveDialog.showSaveDialog(frame);
        if(result == JFileChooser.APPROVE_OPTION){
            try {
                String absPath = saveDialog.getSelectedFile().getAbsolutePath();
                controller.saveToFile(absPath);
            } catch (Exception ex) {}
        }
    }
    
    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g){
            super.paint(g);
             for(int x = 0; x < Constants.LIVES_COUNT; ++x){
                for(int y = 0; y < Constants.LIVES_COUNT; ++y){                  
                    if(model.getCell(x, y)){
                        if(model.isColoredMode()){
                            g.setColor((model.willCellDie(x, y)) ? Color.RED : Color.BLUE);
                        } else {
                            g.setColor(Color.BLACK);
                        }
                        g.fillOval(x * Constants.POINT_RADIUS, y * Constants.POINT_RADIUS,
                                   Constants.POINT_RADIUS, Constants.POINT_RADIUS);
                    } 
                    else {
                        if(model.isColoredMode() && model.willCellRevive(x, y)){
                            g.setColor(new Color(199, 211, 135));
                            g.fillOval(x * Constants.POINT_RADIUS, y * Constants.POINT_RADIUS,
                                   Constants.POINT_RADIUS, Constants.POINT_RADIUS);
                        }                        
                    }
                    if(model.isShowGridMode()){
                        g.setColor(Color.lightGray);
                        g.drawLine((x+1)* Constants.POINT_RADIUS-3, (y+1) * Constants.POINT_RADIUS,
                                  (x+1) * Constants.POINT_RADIUS+3, (y+1) * Constants.POINT_RADIUS);
                        g.drawLine((x+1)* Constants.POINT_RADIUS, (y+1) * Constants.POINT_RADIUS-3,
                                  (x+1) * Constants.POINT_RADIUS, (y+1) * Constants.POINT_RADIUS+3);
                    }
                }
            }
            frame.setTitle(Constants.TITLE_OF_GAME_WINDOW + ": " +  model.getCountGeneration());
        }        
    }
}
