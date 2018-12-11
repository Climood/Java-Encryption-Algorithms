/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vizhenergui;

import java.io.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Muduck
 */
public class VizhenerShifr {
    //modify alphabet if you want to expand supported alphabet 
    //IMPORTANT newAlphabet which used for generating Vigenere matrix must contain all symbols from original alphabet and be equal sized
    //(but order of these symbols must be diffrent)
    String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890,.!?: ()";
    String newAlphabet=null;  //used for generate Vigenere Matrix
    char[][] vigenereMatrix=null; // pointer for generated matrix
    String key=null; 
    String inputString=null;
    String pathInput=null;
    String pathOutput=null;
    String pathKey=null;
    String pathNewAlphabet=null;
    void setInputPath(JTextArea jTextArea,JTextField jTextField){
        pathInput = jTextField.getText();
        jTextArea.setText("New path to imported file: "+pathInput );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathInput))){
            inputString=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    };
    void setOutputPath(JTextArea jTextArea,JTextField jTextField){
        pathOutput = jTextField.getText();
        jTextArea.setText("New path to exported file: "+pathOutput );
    };
    void setKey(JTextArea jTextArea,JTextField jTextField){
        pathKey=jTextField.getText();
        try(BufferedReader reader= new BufferedReader(new FileReader(pathKey))){
            key=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void setPathNewAlphabet(JTextArea jTextArea,JTextField jTextField){
        pathNewAlphabet = jTextField.getText();
        jTextArea.setText("New path to substitution alphabet: "+pathNewAlphabet );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathNewAlphabet))){
            newAlphabet=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void generateMatrixVigener(){
        vigenereMatrix=new char[newAlphabet.length()][newAlphabet.length()];
        int shiftNumb=0;
        for(int i=0;i<newAlphabet.length();i++){
            shiftNumb=i;
            for(int j=0;j<newAlphabet.length();j++){
                if(shiftNumb==newAlphabet.length()){ shiftNumb=0; }
                vigenereMatrix[i][j]=newAlphabet.charAt(shiftNumb++);  //cyclic filling of the matrix with a newAlphabet with a shift in each row by shiftNumb
            }
        }
    }
    void encrypt(JTextArea jTextArea){
        if(key==null){ jTextArea.setText("First specify path to file that contains ciphering message!"); return; }
        if(pathNewAlphabet==null){ jTextArea.setText("First specify path to substitution alphabet!"); return; }
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        generateMatrixVigener();
        int[] indexedInput=new int[inputString.length()];
        int[] indexedKey=new int[key.length()];
        for(int i=0;i<indexedInput.length;i++){
            indexedInput[i]=alphabet.indexOf(inputString.charAt(i));
        }
        for(int i=0;i<indexedKey.length;i++){
            indexedKey[i]=alphabet.indexOf(key.charAt(i));
        }
        char [] outputCharArray=new char[inputString.length()];
        for(int i=0;i<inputString.length();i++){
            for(int j=0;j<key.length();j++){
                outputCharArray[i]=vigenereMatrix[indexedInput[i]][indexedKey[j]];
            }
        }
        String outputString=new String(outputCharArray);
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
            writer.write(outputString);
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void decrypt(JTextArea jTextArea){
        if(key==null){ jTextArea.setText("First specify path to file that contains ciphering message!"); return; }
        if(pathNewAlphabet==null){ jTextArea.setText("First specify path to substitution alphabet!"); return; }
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        generateMatrixVigener();
        int[] indexedEncodedInput=new int[inputString.length()];
        int[] indexedKey=new int[key.length()];
        for(int i=0;i<indexedEncodedInput.length;i++){
            indexedEncodedInput[i]=alphabet.indexOf(inputString.charAt(i));
        }
        for(int i=0;i<indexedKey.length;i++){
            indexedKey[i]=alphabet.indexOf(key.charAt(i));
        }
        char [] outputCharArray=new char[inputString.length()];
        for(int i=0;i<inputString.length();i++){
            for(int j=0;j<key.length();j++){
                int q=0;
                while(inputString.charAt(i)!=vigenereMatrix[q][indexedKey[j]]){
                    q++;
                }
                outputCharArray[i]=alphabet.charAt(q);
            }
        }
        String outputString=new String(outputCharArray);
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
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
