package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class Model extends Observable implements IModel {
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Maze maze;
    private int characterPositionRow=0;
    private int characterPositionColumn=0;
    private Solution sol;

    public Model() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        Configurations.getInstance();
    }

    @Override
    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    @Override
    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void generateMaze(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(height * width) + 50]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            setChanged();
            notifyObservers(maze);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solveMaze() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        sol = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            setChanged();
            notifyObservers(sol);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case NUMPAD8:
                if (maze.getStatus(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                }
                break;
            case NUMPAD2:
                if (maze.getStatus(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                }
                break;
            case NUMPAD6:
                if (maze.getStatus(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                }
                break;
            case NUMPAD4:
                if (maze.getStatus(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                }
                break;
            case NUMPAD9:
                if (maze.getStatus(characterPositionRow-1,characterPositionColumn+1)
                        &&( maze.getStatus(characterPositionRow-1,characterPositionColumn) || maze.getStatus(characterPositionRow,characterPositionColumn+1))) {
                    characterPositionRow--;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD3:
                if (maze.getStatus(characterPositionRow+1,characterPositionColumn+1)
                        && (maze.getStatus(characterPositionRow+1,characterPositionColumn)|| maze.getStatus(characterPositionRow,characterPositionColumn+1))) {
                    characterPositionRow++;
                    characterPositionColumn++;
                }
                break;
            case NUMPAD1:
                if (maze.getStatus(characterPositionRow+1,characterPositionColumn-1)
                        && (maze.getStatus(characterPositionRow+1,characterPositionColumn)|| maze.getStatus(characterPositionRow,characterPositionColumn-1))) {
                    characterPositionColumn--;
                    characterPositionRow++;
                }
                break;
            case NUMPAD7:
                if (maze.getStatus(characterPositionRow-1,characterPositionColumn-1)
                        &&  (maze.getStatus(characterPositionRow-1,characterPositionColumn)|| maze.getStatus(characterPositionRow,characterPositionColumn-1))) {
                    characterPositionColumn--;
                    characterPositionRow--;
                }
                break;
        }
        setChanged();
        notifyObservers(new int []{characterPositionRow,characterPositionColumn});
    }

    @Override
    public Solution getSolution() {
        return sol;
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    public void setMaze(Maze maze) {
        this.maze = maze;
        characterPositionRow = maze.getStartPosition().getRowIndex();
        characterPositionColumn = maze.getStartPosition().getColumnIndex();
    }

    @Override
    public void setCharacter(int rowIndex, int columnIndex) {
        characterPositionRow=rowIndex;
        characterPositionColumn=columnIndex;
    }

    public Maze loadMaze(File chosen){
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(chosen.getAbsolutePath()));
            maze =(Maze) in.readObject();
            in.close();
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            setChanged();
            notifyObservers(maze);
            setChanged();
            notifyObservers(new int []{characterPositionRow,characterPositionColumn});
            return maze;
        }
        catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }

    @Override
    public void saveMaze(File chosen) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(chosen.getAbsolutePath());
            ObjectOutputStream o = new ObjectOutputStream(out);
            o.writeObject(this.maze);
            o.flush();
            o.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}