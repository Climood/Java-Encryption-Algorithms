/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package descrypt;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Muduck
 */
public class DESCryptByteVersion {
    static final int COUNT_OF_ROUNDS=16; //Count of rounds feistel function
    static boolean hideText=false;
    static String inputString=null;
    static byte[] key=new byte[7];
    static byte generatedKeys[][]=new byte[16][];  //variable for storage generated keys for their future use in decryption
    static String pathInput=null;
    static String pathOutput=null;
    static String pathKeyInput=null;
    static final Charset SELECTED_CHARSET=StandardCharsets.UTF_8; //modify if you want to change default charset
    static final int[] IP = {   // initial permuation matrix
      58, 50, 42, 34, 26, 18, 10, 2,
      60, 52, 44, 36, 28, 20, 12, 4,
      62, 54, 46, 38, 30, 22, 14, 6,
      64, 56, 48, 40, 32, 24, 16, 8,
      57, 49, 41, 33, 25, 17,  9, 1,
      59, 51, 43, 35, 27, 19, 11, 3,
      61, 53, 45, 37, 29, 21, 13, 5,
      63, 55, 47, 39, 31, 23, 15, 7
   };
   static final int[] E = {            //Matrix of expansion permutation for 32-bit block  (expand to 48 bit) 
      32,  1,  2,  3,  4,  5,
       4,  5,  6,  7,  8,  9,
       8,  9, 10, 11, 12, 13,
      12, 13, 14, 15, 16, 17,
      16, 17, 18, 19, 20, 21,
      20, 21, 22, 23, 24, 25,
      24, 25, 26, 27, 28, 29,
      28, 29, 30, 31, 32,  1
   };
   static final int[] CD = {                //Matrix of permutation for E-extended key (not touching added bits (i-th % 8=0) after key expansion). After permutation size of key = 64
      57, 49, 41, 33, 25, 17,  9,              //CD size = 56
       1, 58, 50, 42, 34, 26, 18,
      10,  2, 59, 51, 43, 35, 27,
      19, 11,  3, 60, 52, 44, 36,
      63, 55, 47, 39, 31, 23, 15,
       7, 62, 54, 46, 38, 30, 22,
      14,  6, 61, 53, 45, 37, 29,
      21, 13,  5, 28, 20, 12,  4
   };
   static final int[] CD2 = {                   //Matrix of permutation for i-th CD-extended key 
      14, 17, 11, 24,  1,  5,                //CD2 size = 48
       3, 28, 15,  6, 21, 10,
      23, 19, 12,  4, 26,  8,
      16,  7, 27, 20, 13,  2,
      41, 52, 31, 37, 47, 55,
      30, 40, 51, 45, 33, 48,
      44, 49, 39, 56, 34, 53,
      46, 42, 50, 36, 29, 32
   };
   static final int[] SHIFTS_KEY = {                        //number of shifts for each i-th CD-extended key;  i = 1..16
      1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1            //in decrypt mode used in reverse order; i=16...1
   };
   
   static int[][][] S = {            //substitution boxes which transformate each 48 bit blocks to 6-bit pieces according to funcS method
{ 		{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
		{ 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
		{ 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
		{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } 
},
{ 		{ 15, 1, 8, 14, 6, 11, 3, 2, 9, 7, 2, 13, 12, 0, 5, 10 },
		{ 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
		{ 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
		{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } 
},
{ 		{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
		{ 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
		{ 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
		{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } 
},
{ 		{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
		{ 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
		{ 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
		{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } 
},
{ 		{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
		{ 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
		{ 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
		{ 11, 8, 12, 7, 1, 14, 2, 12, 6, 15, 0, 9, 10, 4, 5, 3 } 
},
{ 		{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
		{ 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
		{ 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
		{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }
},
{ 		{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
		{ 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
		{ 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
		{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }
},
{ 		{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
		{ 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
		{ 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
		{ 2, 1, 14, 7, 4, 10, 18, 13, 15, 12, 9, 0, 3, 5, 6, 11 }
} };
   static final int[] P = {                  //Matrix of permutation for block returned from feistel function 
      16,  7, 20, 21,
      29, 12, 28, 17,
       1, 15, 23, 26,
       5, 18, 31, 10,
       2,  8, 24, 14,
      32, 27,  3,  9,
      19, 13, 30,  6,
      22, 11,  4, 25
   };
   static final int[] FP = { // final permuation matrix
      40, 8, 48, 16, 56, 24, 64, 32,
      39, 7, 47, 15, 55, 23, 63, 31,
      38, 6, 46, 14, 54, 22, 62, 30,
      37, 5, 45, 13, 53, 21, 61, 29,
      36, 4, 44, 12, 52, 20, 60, 28,
      35, 3, 43, 11, 51, 19, 59, 27,
      34, 2, 42, 10, 50, 18, 58, 26,
      33, 1, 41,  9, 49, 17, 57, 25
   };
    static void setInputPath(JTextArea jTextArea,JTextField jTextField){
        pathInput = jTextField.getText();
        jTextArea.setText("New path to imported file: "+pathInput );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathInput))){
            inputString=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    };
    static void setOutputPath(JTextArea jTextArea,JTextField jTextField){
        pathOutput = jTextField.getText();
        jTextArea.setText("New path to exported file: "+pathOutput );
    };
    static void setKeyInputPath(JTextArea jTextArea,JTextField jTextField,JTextField jTextFieldKeyView){
        pathKeyInput = jTextField.getText();
        jTextArea.setText("New path to file contains key: "+pathKeyInput );
        try(BufferedReader reader= new BufferedReader(new FileReader(pathKeyInput))){
                String keyBuf=reader.readLine();
                while(keyBuf.length()<7){
                    keyBuf+="&";            //extend key, if key have less than 7 bytes 
                }
                byte[] keyByte=stringToBytes(keyBuf);
                for(int i=0;i<key.length;i++){
                    key[i]=keyByte[i];      //Take first 7 bytes from key, they will be truely key 
                }
                jTextFieldKeyView.setText(bytesToString(key));
        }
        catch(IOException ex){
            jTextArea.setText("Wrong path to file! Use global path!");
        }
    };
    static void setHideText(){  //toggle true if you want to see open\closed message
        hideText = !hideText;
    }
    
    static byte[] stringToBytes(String in){ 
        return in.getBytes(SELECTED_CHARSET);
    }
    static String bytesToString(byte[] in){
        return new String(in,SELECTED_CHARSET);
    }
    static void generateKeys(){//Generated keys are safed for future using in decryprion
        byte[] expandedKey=expandKey(key);
        byte[] CDkey=resortBlock(expandedKey,CD);
        for(int i=0;i<16;i++){
            CDkey=shiftLeftKey(CDkey,SHIFTS_KEY[i]); 
            generatedKeys[i]=resortBlock(CDkey,CD2);
        }
    }
    static byte[] expandKey(byte[] key){//Function than expand original 56 bit key to 64 bit
        //( inserting new bit after each 7 bits of next byte,which is equal 1 if the previous 7 bits have an even number of ones , else is equal 0) 
        byte[] expandedKey=new byte[8];
        int countOfOnes=0;
        int oldIndex=0;
        for(int i=0;i<expandedKey.length * 8;i++){
            if((i+1) % 8 == 0){ 
                if(countOfOnes % 2 == 0){
                    setBit(expandedKey,i,1);
                    countOfOnes=0;
                    continue;
                }else{
                    setBit(expandedKey,i,0);
                    countOfOnes=0;
                    continue;
                }
            }
            if(getBit(key,oldIndex) == 1 ) countOfOnes++;
            setBit(expandedKey,i,getBit(key,oldIndex));
            oldIndex++;
        }
        return expandedKey; 
    }
    static byte[] resortBlock(byte[] in,int[] resortMatrix){  //Function for all permutation
        byte[] resortedBlock = new byte [(resortMatrix.length - 1) / 8 + 1]; //I do -1 if i get empty matrix on entry
        for(int i=0;i<resortMatrix.length;i++){
            int tmpVal=getBit(in,resortMatrix[i] - 1);
            setBit(resortedBlock,i,tmpVal);
        }
        return resortedBlock;
    }
    static byte[] shiftLeftKey(byte[] key,int pos){// Function that shifts key 
        byte[] out=new byte[key.length];
        for(int i=0;i<key.length*8;i++){  
            int tmpBit=getBit(key,(i+pos) % (key.length*8));
            setBit(out,i,tmpBit);
        }
        return out;
    }
    static void setBit(byte[] in,int pos, int val){ 
        int posByte=pos/8; //64 32 1  = 01000000 00100000 00000001   
        int posBit=pos%8;
        byte tmpByte = in[posByte];	//01000000								
	tmpByte = (byte) (((0xFF7F >> posBit) & tmpByte) & 0x00FF);	//0xFF7F = 1111 1111 0111 1111, //( 0111 1111 1011 1111 & 01000000 )& 0000 0000 1111 1111 = 0000 0000 0000 0000 	
	byte newByte = (byte) ((val << (8 - (posBit + 1))) | tmpByte);   //shifts to 7-th position    // val=1=00000001 << 6 =>01000000 | 0000 0000 0000 0000 = 0000 0000 0100 0000   checking is OK
	in[posByte] = newByte;
    }
    static int getBit(byte[] in,int pos){
        int posByte = pos / 8;   //64 32 1  = 01000000 00100000 00000001 вводим (in,1)
	int posBit = pos % 8;										
	byte tmpByte = in[posByte];  // 01000000
	int bit = tmpByte >> (8 - (posBit + 1)) & 0x0001; // 01000000 >> 6 & 000000001  //shifts to end 
	return bit; //1
    }
    static byte[] getBits(byte[] in,int pos,int len){  //pos- position from taken bits, len -length of sequence of bits
        byte[] out=new byte[(len-1) / 8 + 1];
        for(int i=0;i<len;i++){
            int val=getBit(in,pos+i);
            setBit(out,i,val);
        }
        return out;
    }
    static byte[] xor(byte[] L,byte[] R){
        byte[] result=new byte[L.length];
        for(int i=0;i<L.length;i++){
            result[i]=(byte) (L[i] ^ R[i]);
        }
        return result; 
    }
    static byte[] separateBytes(byte[] in,int lenOfByte){
        byte[] out=new byte[ ( (8*in.length)-1 ) / lenOfByte + 1];//Get numbers of byte needs to put all input bits into them if bytes were length = lenOfByte (in my realization = 6 ) 
        for(int i=0;i<out.length;i++){
            for(int j=0;j<lenOfByte;j++){
                int tmpBit=getBit(in,lenOfByte * i + j);//Filling every byte with bits from 0..5 index 
                setBit(out,8 * i + j,tmpBit);
            }
        }
        return out;
    }
    static byte[] connectBlocks(byte[] L,int Llen,byte[] R,int Rlen){ //Connects two 32bit blocks into 1 64 bit block
        byte[] out=new byte[ (Llen + Rlen - 1) / 8 + 1];
        int index=0;
        for(int i=0;i<Llen;i++){
            int tmpBit=getBit(L,i);
            setBit(out,index,tmpBit);
            index++;
        }
        for(int i=0;i<Rlen;i++){
            int tmpBit=getBit(R,i);
            setBit(out,index,tmpBit);
            index++;
        }
        return out;
    }
    static byte[] funcS(byte[] in){//On entry get 48 bit block which will be transforming in 32 bit block on return
        in=separateBytes(in,6); //Separate entry blocks bytes to bytes with length = 6
        byte[] out=new byte[in.length/2];// Get 4 bytes with 8 bits
        int halfByte = 0;											
	for (int b = 0; b < in.length; b++) {
	    byte valByte = in[b];
	    int r = 2 * (valByte >> 7 & 0x0001) + (valByte >> 2 & 0x0001); //extreme bits give the first index through conversion														
	    int c = valByte >> 3 & 0x000F;//center bits give the second index through conversion
	    int val = S[b][r][c];
	    if (b % 2 == 0){
		halfByte = val;
            }else{
		out[b / 2] = (byte) (16 * halfByte + val);
            }	
        }
	return out;       //To test..
    }
    static byte[] funcFeistel(byte[] in,byte[] key_i){
        byte[] temp=resortBlock(in,E); //Expend in block with E matrix
        temp=xor(temp,key_i); 
        temp=funcS(temp);
        temp=resortBlock(temp,P);
        return temp;
    }
    static byte[] encryptBlock(byte[] in){
        byte[] IPblock = resortBlock(in,IP);
        byte[] L=getBits(IPblock,0,IP.length/2); //I use the length of the IP matrix so as not to multyply the length of the IPblock at 8
        byte[] R=getBits(IPblock,IP.length/2,IP.length/2);
        for(int i=0;i<COUNT_OF_ROUNDS;i++){   //Feistel conversation
            byte[] tmpR=R;
            R=xor(L,funcFeistel(R,generatedKeys[i]));
            L=tmpR;
        }
        IPblock=connectBlocks(L,IP.length/2,R,IP.length/2);
        IPblock=resortBlock(IPblock,FP);
        return IPblock;        
    }
    static byte[] decryptBlock(byte[] in){
        byte[] IPblock = resortBlock(in,IP);
        byte[] L=getBits(IPblock,0,IP.length/2); ///I use the length of the IP matrix so as not to multyply the length of the IPblock at 8
        byte[] R=getBits(IPblock,IP.length/2,IP.length/2);
        for(int i=0;i<COUNT_OF_ROUNDS;i++){   //Feistel conversation
            byte[] tmpL=L;
            L=xor(R,funcFeistel(L,generatedKeys[15-i])); //Gets already generated keys from static array
            R=tmpL;
        }
        IPblock=connectBlocks(L,IP.length/2,R,IP.length/2);
        IPblock=resortBlock(IPblock,FP);
        return IPblock;        
    }
    static byte[] extendInputBytes(byte[] input){
        if(input.length % 8 == 0) {
            return input;
        }
        int countOfAddedByte=8 - input.length % 8; //if number of input Bytes % 8!= 0 then expand them to % 8 = 0
        byte[] newInputBytes=new byte[input.length + countOfAddedByte];
        for(int i=0;i<input.length;i++){
            newInputBytes[i]=input[i];
        }
        for(int j=input.length;j<newInputBytes.length;j++){
            newInputBytes[j]=(byte)1;
        }
        return newInputBytes;
    }
    static byte[][] bytesToBlocks64Bit(byte[] bytes){
        byte[][] bytesBlocks=new byte[bytes.length/8][8];
        for(int i=0;i<bytesBlocks.length;i++){
            for(int j=0;j<8;j++){
                bytesBlocks[i][j]=bytes[i*8 + j];
            }
        }
        return bytesBlocks;
    }
    static byte[] Blocks64BitToBytes(byte[][] bytesBlocks){
        byte[] bytes=new byte[bytesBlocks.length*8];
        for(int i=0;i<bytesBlocks.length;i++){
            for(int j=0;j<8;j++){
                bytes[i*8 + j]=bytesBlocks[i][j];
            }
        }
        return bytes;
    }
    static void encrypt(JTextArea jTextAreaRight,JTextArea jTextAreaLeft){
        if(pathKeyInput==null){ jTextAreaRight.setText("Firts specify path to file that contain key!"); return; } 
        if(pathInput==null){ jTextAreaRight.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextAreaLeft.setText("First specify path to exported file!"); return; }
        if(hideText){
            jTextAreaRight.setText(inputString);
        }
        generateKeys(); //Generate keys in static array
        byte[] inputBytes=extendInputBytes(stringToBytes(inputString)); //Transformating string in bytes and expand it if this need
        byte[][] inputBytesBlocks=bytesToBlocks64Bit(inputBytes); //Cut it for 64bit blocks
        byte[][] outputBytesBlocks=new byte[inputBytesBlocks.length][8];//Create storage space for output blocks
        for(int i=0;i<inputBytesBlocks.length;i++){
            outputBytesBlocks[i]=encryptBlock(inputBytesBlocks[i]);
        }
        byte[] outputBytes=Blocks64BitToBytes(outputBytesBlocks);//Get encrypted sequence of bytes
        if(hideText){
            jTextAreaLeft.setText(outputBytes.toString());
        }
        File file=new File(pathOutput); 
        try{FileOutputStream outputStream = new FileOutputStream(file);//Writing encrypted bytes to txt file
            outputStream.write(outputBytes);
            outputStream.close();
        }catch(IOException ex){
            System.out.print("error");
        }
    }
    static void decrypt(JTextArea jTextAreaRight,JTextArea jTextAreaLeft){
        if(pathKeyInput==null){ jTextAreaRight.setText("Firts specify path to file that contain key!"); return; } 
        if(pathInput==null){ jTextAreaRight.setText("First specify path to imported file!"); return; }
        if(pathOutput==null){ jTextAreaLeft.setText("First specify path to exported file!"); return; }
        if(hideText){
            jTextAreaRight.setText(inputString);
        }
        generateKeys();  //Generate keys in static array
        File file=new File(pathInput);
        byte[] inputBytes=null;
        try{FileInputStream inputStream = new FileInputStream(file);//Reading encrypted bytes from txt file
            inputBytes = new byte[(int) file.length()];
            inputStream.read(inputBytes);
        }catch( FileNotFoundException ex){
            System.out.println("File not found");
        }catch(IOException ex){
            System.out.println("Error");
        }
        byte[][] inputBytesBlocks=bytesToBlocks64Bit(inputBytes); //Cut it for 64bit blocks
        byte[][] outputBytesBlocks=new byte[inputBytesBlocks.length][8];//Create storage space for output blocks
        for(int i=0;i<inputBytesBlocks.length;i++){
            outputBytesBlocks[i]=decryptBlock(inputBytesBlocks[i]);
        }
        byte[] outputBytes=Blocks64BitToBytes(outputBytesBlocks);//Gets decrypted sequence of bytes
        String outputString=bytesToString(outputBytes);//Transformatiing decrypted bytes to string
        if(hideText){
            jTextAreaLeft.setText(outputString);
        }  //Writing decrypted string to txt file
        try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathOutput), SELECTED_CHARSET))){
            writer.write(outputString);
        }
        catch(IOException ex){
            jTextAreaRight.setText("Wrong path to file! Use global path!");
        }
    }
    static void hacking(JTextArea jTextArea){
        
    }
}
