/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gammirovaniegui;
import java.util.Random;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 * @author Muduck
 */
public class GammaEncryption {
    //modify alphabet if you want to expand supported symbols
    String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890,.!?: ()";
    int[] gamma=null;
    static final Charset SELECTED_CHARSET=StandardCharsets.UTF_8;   //modify to change default charset
    String inputString=null;
    String pathInput=null;
    String pathOutput=null;
    String pathGammaInput=null;
    String pathGammaOutput=null;
    void setInputPath(JTextArea jTextArea,JTextField jTextField){
        pathInput = jTextField.getText();
        jTextArea.setText("New path to import message: "+pathInput );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathInput))){
            inputString=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    };
    void setOutputPath(JTextArea jTextArea,JTextField jTextField){
        pathOutput = jTextField.getText();
        jTextArea.setText("New path to export message: "+pathOutput );
    };
    void setGammaOutputPath(JTextArea jTextArea,JTextField jTextField){
        pathGammaOutput = jTextField.getText();
        jTextArea.setText("New path to export gamma: "+pathGammaOutput );
    };
    void setGammaInputPath(JTextArea jTextArea,JTextField jTextField){
        pathGammaInput = jTextField.getText();
        jTextArea.setText("New path to import gamma: "+pathGammaInput );
    };
    void gammaGenerate(int gammaSize,JTextArea jTextArea){  
        Random randomizer=new Random();
        gamma=new int[gammaSize];
        for(int i=0;i<gamma.length;i++){
            gamma[i]=randomizer.nextInt(alphabet.length());
        }
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathGammaOutput))){
            writer.write(Arrays.toString(gamma));
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void gammaImport(JTextArea jTextArea){ //method allow you to import exists gamma from txt file (gamma must be writed in a certain form(according to Arrays.toString( int [] array) )
        String gammaBuffer=null;
        try(BufferedReader reader= new BufferedReader(new FileReader(pathGammaInput))){
            gammaBuffer=reader.readLine().substring(1).replaceAll("]", "").replaceAll(",", ""); //awful, but it works. gamma must be written as the Arrays.toString () method writes an int array
            gamma=Arrays.stream(gammaBuffer.split(" ")).mapToInt(Integer::parseInt).toArray(); 
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    byte[] stringToBytes(String in){
        return in.getBytes(SELECTED_CHARSET);
    }
    String bytesToString(byte[] in){
        return new String(in,SELECTED_CHARSET);
    }
    byte[] xorWithGamma(byte[] message,int[] gamma){ //length of message's bytes and gamma must be equal
        byte[] result=new byte[message.length];
        for(int i=0;i<message.length;i++){
            result[i]=(byte) (message[i] ^ gamma[i]);
        }
        return result;  
    }
    void encrypt(JTextArea jTextArea){
        if(pathGammaOutput==null){ jTextArea.setText("First specify path to file which contain gamma sequence!"); return; } 
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        byte[] inputBytes=stringToBytes(inputString);
        gammaGenerate(inputBytes.length,jTextArea); //length of gamma equal to bytes of input message length
        byte[] outputBytes=xorWithGamma(inputBytes,gamma);//encrypted symbol calculated by the following formul: I(i) ^ G(i) ,where I - Bytes of input messag and G - gamma
        File file=new File(pathOutput);
        try{FileOutputStream outputStream = new FileOutputStream(file); //create a Stream to output files and writing output bytes to them
            outputStream.write(outputBytes);
            outputStream.close();
        }catch(IOException ex){
            System.out.print("IO Error");
        }
    }
    void decrypt(JTextArea jTextArea){
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        if(gamma==null){ jTextArea.setText("First generate a gamma sequence!"); return; }
        File file=new File(pathInput);
        byte[] inputBytes=null;
        try{FileInputStream inputStream = new FileInputStream(file);//open an exist file with encrypted messages bytes and reading them
            inputBytes =new byte[(int) file.length()];
            inputStream.read(inputBytes);
        }catch( FileNotFoundException ex){
            System.out.println("File not found!");
        }catch(IOException ex){
            System.out.println("IO Error");
        }
        byte[] outputBytes=xorWithGamma(inputBytes,gamma);//encrypted symbol calculated by the following formul: E(i) ^ G(i) ,where E -Encrypted Bytes of input message and G - gamma
        String outputString=bytesToString(outputBytes);
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(pathOutput))){
            writer.write(outputString);
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void hacking(JTextArea jTextArea){
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }
    
}