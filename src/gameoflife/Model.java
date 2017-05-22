package gameoflife;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    private boolean[][] currentGeneration;
    private final boolean[][] nextGeneration;
    private int countGeneration = 0;
    private int showDelay = Constants.DEFAULT_DELAY;
    private volatile boolean automode;
    private boolean isColoredMode = true;
    private boolean showGrid = false;
    Random rand;    
    private final List<ModelListener> listeners; //The list of listeners
    
    public Model() {
        this.automode = false;
        currentGeneration = new boolean[Constants.LIVES_COUNT][Constants.LIVES_COUNT];
        nextGeneration = new boolean[Constants.LIVES_COUNT][Constants.LIVES_COUNT];
        rand = new Random();
        listeners = new ArrayList<>();
    }
    
    public final void printField(){
        for(int x = 0; x < currentGeneration.length; ++x){
            for(int y = 0; y < currentGeneration[x].length; ++y){
                System.out.print(currentGeneration[y][x] + " "); 
            }
            System.out.println("");
        }
    }
    public void generateRandomSetup(){
        for(int x = 0; x < currentGeneration.length; ++x){
            for(int y = 0; y < currentGeneration[x].length; ++y){
                currentGeneration[y][x] = rand.nextBoolean();
            }
        }
        notifyAllListeners();
    }
    
    public void clearField(){
        for(int x = 0; x < currentGeneration.length; ++x){
            for(int y = 0; y < currentGeneration[x].length; ++y){
                currentGeneration[y][x] = false;
            }
        }
        notifyAllListeners();
    }
    
    public void readFromFile(){
        
    }
    
    public boolean isColoredMode() {
        return isColoredMode;
    }

    public boolean isShowGridMode() {
        return showGrid;
    }

    public boolean isAutomode() {
        return automode;
    }

    public int getCountGeneration() {
        return countGeneration;
    }

    public boolean[][] getCurrentGeneration() {
        return currentGeneration;
    }
    
    public void loadGenerationFromFile(String inputFilePath ) throws ClassNotFoundException{
            try {
                FileInputStream fis = new FileInputStream(inputFilePath);
                ObjectInputStream ois = new ObjectInputStream(fis);
                currentGeneration = (boolean[][]) ois.readObject();                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }
        notifyAllListeners();
    }
    
    public void saveGenerationToFile(String outputFilePath) throws FileNotFoundException, IOException{
        FileOutputStream fos = new FileOutputStream(outputFilePath + (outputFilePath.endsWith(Constants.EXIT_FILE_EXTENTION) ? "" : Constants.EXIT_FILE_EXTENTION));
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(currentGeneration);
    }
    
    public void oneStep(){
        processOfLife();
        notifyAllListeners();
    }
    
    public void run(){
        while (true) {      
            if(automode) {
                oneStep();
                try {
                    Thread.sleep(showDelay);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }        
    }
    
    public void toggleAutomodeOn(){
        automode = true;
    }
    public void toggleAutomodeOff(){
        automode = false;
    }
    
    public void toggleColoredMode(){
        isColoredMode = !isColoredMode;
        notifyAllListeners();
    }
    
    public void toggleGridVisibility(){
        showGrid = !showGrid; 
        notifyAllListeners();
    }
    
    public void increaseSpeed(){
        if(showDelay >= Constants.MIN_DELAY) {
            showDelay -= Constants.DELAY_INCREMENT;
        }
    }
    
    public void decreaseSpeed(){
        if(showDelay <= Constants.MAX_DELAY) {
            showDelay += Constants.DELAY_INCREMENT;
        }        
    }
    
    public void resetSpeed(){
        showDelay = Constants.DEFAULT_DELAY;
    }
    
    public void changeCellState(int x, int y){
        currentGeneration[y][x] = !currentGeneration[y][x];
        notifyAllListeners();
    }
    
    public boolean getCell(int x, int y){
        return currentGeneration[y][x];
    }    
  
    public boolean willCellDie(int x, int y){
        byte countOfNeighbours = countNeighbours(x, y);
        return countOfNeighbours < Constants.MIN_NEIGHBOUR_COUNT || countOfNeighbours > Constants.MAX_NEIGHBOUR_COUNT;
    }
    
    public boolean willCellRevive(int x, int y){
       byte countOfNeighbours = countNeighbours(x, y);
       return (!currentGeneration[y][x]) && (countOfNeighbours == Constants.MAX_NEIGHBOUR_COUNT);
    }
    public byte countNeighbours(int x, int y){
        byte count = 0;
        for(int dx = -1; dx < 2; ++dx){
            for(int dy = -1; dy < 2; ++dy){
                int nX = x + dx;
                int nY = y + dy;
                nX = (nX < 0) ? Constants.LIVES_COUNT - 1 : nX;
                nY = (nY < 0) ? Constants.LIVES_COUNT - 1 : nY;
                nX = (nX > Constants.LIVES_COUNT - 1) ? 0 : nX;
                nY = (nY > Constants.LIVES_COUNT - 1) ? 0 : nY;
                count += (currentGeneration[nY][nX]) ? 1 : 0;
            }
        }
        if(currentGeneration[y][x]){
           --count ; 
        }
        return count;
    }
    public void processOfLife(){
        for(int x = 0; x < currentGeneration.length; ++x){
            for(int y = 0; y < currentGeneration[x].length; ++y){
                byte countOfNeighbours = countNeighbours(x, y);
                if(willCellRevive(x, y)){
                    nextGeneration[y][x] = true; // if current cell is dead and has 3 neighbours it becomes alive
                } else if(willCellDie(x, y)){
                    nextGeneration[y][x] = false; // if current cell has less than 2 or more than 3 neighbours it will die by loneliness or overpopulation.
                } else {
                    nextGeneration[y][x] = currentGeneration[y][x];
                }
            }
        }
        for(int x = 0; x < currentGeneration.length; ++x){
            System.arraycopy(nextGeneration[x], 0, currentGeneration[x], 0, Constants.LIVES_COUNT);
        }
        ++countGeneration;
    }
    
    public void addListener(ModelListener listener){
        listeners.add(listener);        
    }
    
    public boolean removeListener(ModelListener listener){
        return listeners.remove(listener);
    }
    
    private void notifyAllListeners(){
        for(ModelListener l : listeners){
            l.updateView();
        }
    }
}
