package gameoflife;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    private final Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public void fillRandom(){
        model.generateRandomSetup();
    }
    
    public void clearField(){
        model.clearField();
    }
    
    public void readFromFile(String path){
        try {
            model.loadGenerationFromFile(path);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveToFile(String path){
        try {
            model.saveGenerationToFile(path);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void oneStep(){
        model.oneStep();
    }
    
    public void run(){
        model.run();
    }
    
    public void go(){
        model.toggleAutomodeOn();
    }
    
    public void stop(){
        model.toggleAutomodeOff();        
    }
    
    public void faster(){
        System.out.println("Увеличиваем скорость");
        model.increaseSpeed();
    }
    
    public void slower(){
        System.out.println("Увеменьшаем скорость");
        model.decreaseSpeed();
    }
    
    public void color(){
        model.toggleColoredMode();
    }
    
    public void grid(){
        model.toggleGridVisibility();
    }
    
    public void toggleCell(int x, int y){
        model.changeCellState(x, y);
    }
}
