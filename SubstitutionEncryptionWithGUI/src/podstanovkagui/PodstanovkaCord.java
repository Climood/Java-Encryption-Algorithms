/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podstanovkagui;

import java.io.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 * @author Muduck
 */
public class PodstanovkaCord {
    //Modify alphabet if you want to expand supported alphabet
    String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890,.!?: ()";
    String newAlphabet=null; 
    String inputString=null;
    String pathInput=null;
    String pathOutput=null;
    String pathNewAlphabet=null;
    void setNewAlphabet(JTextArea jTextArea,JTextField jTextField){
        pathNewAlphabet = jTextField.getText();
        jTextArea.setText("New path to substitution alphabet: "+pathNewAlphabet );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathNewAlphabet))){
            newAlphabet=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    void setInputPath(JTextArea jTextArea,JTextField jTextField){
        pathInput = jTextField.getText();
        jTextArea.setText("New path to input string: "+pathInput );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathInput))){
            inputString=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    };
    void setOutputPath(JTextArea jTextArea,JTextField jTextField){
        pathOutput = jTextField.getText();
        jTextArea.setText("New path to output string: "+pathOutput );
    };
    void encrypt(JTextArea jTextArea){
        if(pathNewAlphabet==null){ jTextArea.setText("First specify path to substitution alphabet!"); return; }
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        StringBuilder outputString=new StringBuilder();
        for(int i=0;i<inputString.length();i++){  
            outputString.append(newAlphabet.charAt(alphabet.indexOf(inputString.charAt(i))));         // Be warned with windows notebook and his lovely BOM..
        }
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
            writer.write(outputString.toString());
        }
        catch(IOException ex){
            System.out.println("Wrong path to file! Use global path!");
        }
    }
    void decrypt(JTextArea jTextArea){
        if(newAlphabet==null){ jTextArea.setText("First specify path to substitution alphabet!"); return; }
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        StringBuilder outputString=new StringBuilder();
        for(int i=0;i<inputString.length();i++){
            outputString.append(alphabet.charAt(newAlphabet.indexOf(inputString.charAt(i))));         
        }
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
            writer.write(outputString.toString());
        }
        catch(IOException ex){
            System.out.println("Wrong path to file! Use global path!");
        }
    }
    void hacking(JTextArea jTextArea){
        //not writed yet!
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    } 
}
