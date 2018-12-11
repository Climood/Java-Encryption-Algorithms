/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.caesarframe;
import java.io.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 * @author Muduck
 */
public class CaesarCord {
    //Modify both cases alphabets if you want to expand supported alphabet
    //lenghts both alphabets must be equials!
    String upCaseAlphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ";    
    String lowCaseAlphabet="abcdefghijklmnopqrstuvwxyz";
    int key=0;
    int alphabetLength=upCaseAlphabet.length(); 
    String inputString=null;
    String pathInput=null;
    String pathOutput=null;
    String pathKey=null;
    void setKey(JTextArea jTextArea,JTextField jTextField){
        pathKey = jTextField.getText();
        jTextArea.setText("New path to key: "+pathKey );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathKey))){
            key=Integer.parseInt(reader.readLine()) % alphabetLength;
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
    void encrypt(int isEncrypt,JTextArea jTextArea){  //isEncrypt allow to not write a similar method. Set 1 to Enctypt and -1 to Decrypt!
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathKey==null){ jTextArea.setText("First specify path to key!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        StringBuilder outputString=new StringBuilder();
        char[] input=inputString.toCharArray();
        for(int i=0;i<input.length;i++){
            if( upCaseAlphabet.contains(Character.toString(input[i]))){
                int index =upCaseAlphabet.indexOf(input[i]) + isEncrypt*key;
                if(index<0){
                    outputString.append(upCaseAlphabet.charAt(alphabetLength + index));
                    continue;
                }
                outputString.append(upCaseAlphabet.charAt((upCaseAlphabet.indexOf(input[i]) + isEncrypt*key) % alphabetLength));
            }else if( lowCaseAlphabet.contains(Character.toString(input[i]))){
                int index =lowCaseAlphabet.indexOf(input[i]) + isEncrypt*key;
                if(index<0){
                    outputString.append(lowCaseAlphabet.charAt(alphabetLength + index));
                    continue;
                }
                outputString.append(lowCaseAlphabet.charAt((lowCaseAlphabet.indexOf(input[i]) + isEncrypt*key) % alphabetLength));
            }else{
                outputString.append(input[i]);
            }
        }
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
            writer.write(outputString.toString());
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    public void hacking(JTextArea jTextArea){
        if(pathInput==null){ jTextArea.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextArea.setText("First specify path to exported file!"); return; }
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(pathOutput))){
            String outputString=""; //i didnt use StringBuilder here because outputString often to reset
            char[] input=inputString.toCharArray();
            for(int i=1;i<upCaseAlphabet.length()-1;i++){
                for(char s:input){
                    if(upCaseAlphabet.contains(Character.toString(s))){
                        int index =upCaseAlphabet.indexOf(s) - i;
                        if(index<0){
                            outputString+=upCaseAlphabet.charAt(alphabetLength + index);
                            continue;
                        }
                        outputString+=upCaseAlphabet.charAt(upCaseAlphabet.indexOf(s) - i); 
                    }else if(lowCaseAlphabet.contains(Character.toString(s))){
                        int index =lowCaseAlphabet.indexOf(s) - i;
                        if(index<0){
                            outputString+=lowCaseAlphabet.charAt(alphabetLength + index);
                            continue;
                        }
                        outputString+=lowCaseAlphabet.charAt(lowCaseAlphabet.indexOf(s) - i);
                    }else{
                        outputString+=s;
                    }
                }
                writer.write(outputString+" \r\n");
                outputString="";
            }
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }
}
