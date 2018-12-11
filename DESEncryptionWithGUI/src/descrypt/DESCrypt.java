/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package descrypt;
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
public class DESCrypt {
    /**
     * @param args the command line arguments
     */
    static final int COUNT_OF_ROUNDS=16;
    static boolean hide_text=false;
    static String input_string=null;
    static String output_string="";
    static byte[] key=new byte[7];
    static String path_input=null;
    static String path_output=null;
    static String path_key_input=null;
    static final Charset SELECTED_CHARSET=StandardCharsets.UTF_8;
    static final int[] IP = {
      58, 50, 42, 34, 26, 18, 10, 2,
      60, 52, 44, 36, 28, 20, 12, 4,
      62, 54, 46, 38, 30, 22, 14, 6,
      64, 56, 48, 40, 32, 24, 16, 8,
      57, 49, 41, 33, 25, 17,  9, 1,
      59, 51, 43, 35, 27, 19, 11, 3,
      61, 53, 45, 37, 29, 21, 13, 5,
      63, 55, 47, 39, 31, 23, 15, 7
   };
   static final int[] E = {
      32,  1,  2,  3,  4,  5,
       4,  5,  6,  7,  8,  9,
       8,  9, 10, 11, 12, 13,
      12, 13, 14, 15, 16, 17,
      16, 17, 18, 19, 20, 21,
      20, 21, 22, 23, 24, 25,
      24, 25, 26, 27, 28, 29,
      28, 29, 30, 31, 32,  1
   };
   static final int[] CD = {                //матрица для перестановки ключа  НЕ ТРОГАЕТ БИТЫ расширенного ключа, кратные 8!! т.е после перестановки ключ так же будет 64 бит
      57, 49, 41, 33, 25, 17,  9,              //САМ РАЗМЕР CD матрицы 56 был баг из за этого,внимательно!
       1, 58, 50, 42, 34, 26, 18,
      10,  2, 59, 51, 43, 35, 27,
      19, 11,  3, 60, 52, 44, 36,
      63, 55, 47, 39, 31, 23, 15,
       7, 62, 54, 46, 38, 30, 22,
      14,  6, 61, 53, 45, 37, 29,
      21, 13,  5, 28, 20, 12,  4
   };
   static final int[] CD2 = {                   //матрица для выбора ключа iого из расширенного через CD лключа
      14, 17, 11, 24,  1,  5,                //САМ РАЗМЕР CD2 матрицы 48 ,внимательно!
       3, 28, 15,  6, 21, 10,
      23, 19, 12,  4, 26,  8,
      16,  7, 27, 20, 13,  2,
      41, 52, 31, 37, 47, 55,
      30, 40, 51, 45, 33, 48,
      44, 49, 39, 56, 34, 53,
      46, 42, 50, 36, 29, 32
   };
   static final int[] SDVIGI_TO_CRYPT = {                        // число сдвигов для каждого iого расширенного через CD ключа  i=1..16
      1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
   };
   static final int[] SDVIGI_TO_UNCRYPT={                                   //i=16..1
      0, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1  
   };
   static final int[][] S = {                                               
       {14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7}, // S1
       {0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8},
       {4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0},
       {15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13},
       {15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10}, // S2
       {3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11, 5},
       {0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15},
       {13, 8,  10, 1, 3,  15, 4,  2,  11,  6, 7,  12,  0,  5,  14, 9},//Эта строка была причиной бага
       {10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8}, // S3
       {13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1},
       {13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7},
       {1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12},
       {7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15}, // S4
       {13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9},
       {10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4},
       {3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14},
       {2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9}, // S5
       {14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6},
       {4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14},
       {11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3},
       {12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11}, // S6
       {10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8},
       {9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6},
       {4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13},
       {4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1}, // S7
       {13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6},
       {1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2},
       {6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12},
       {13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7}, // S8
       {1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2},
       {7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8},
       {2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11}
   };   
   static final int[] P = {                  //Перестановка для матрицы выходящей з фейстеля
      16,  7, 20, 21,
      29, 12, 28, 17,
       1, 15, 23, 26,
       5, 18, 31, 10,
       2,  8, 24, 14,
      32, 27,  3,  9,
      19, 13, 30,  6,
      22, 11,  4, 25
   };
   static final int[] IP_TO_BACK = {
      40, 8, 48, 16, 56, 24, 64, 32,
      39, 7, 47, 15, 55, 23, 63, 31,
      38, 6, 46, 14, 54, 22, 62, 30,
      37, 5, 45, 13, 53, 21, 61, 29,
      36, 4, 44, 12, 52, 20, 60, 28,
      35, 3, 43, 11, 51, 19, 59, 27,
      34, 2, 42, 10, 50, 18, 58, 26,
      33, 1, 41,  9, 49, 17, 57, 25
   };
    static void Set_input_path(JTextArea jTextArea,JTextField jTextField){
        path_input = jTextField.getText();
        jTextArea.setText("Новый путь к импорту: "+path_input );
        try(BufferedReader reader= new BufferedReader(new FileReader(path_input))){
            input_string=reader.readLine();
        }
        catch(IOException ex){
            jTextArea.setText("Неверный путь к файлу.");
        }
    };
    static void Set_output_path(JTextArea jTextArea,JTextField jTextField){
        path_output = jTextField.getText();
        jTextArea.setText("Новый путь к экспорту: "+path_output );
    };
    static void Set_key_input_path(JTextArea jTextArea,JTextField jTextField,JTextField jTextFieldKeyView){
        path_key_input = jTextField.getText();
        jTextArea.setText("Новый путь к импорту ключа: "+path_key_input );
        try(BufferedReader reader= new BufferedReader(new FileReader(path_key_input))){
                String key_buf=reader.readLine();
                while(key_buf.length()<7){
                    key_buf+="&";            //дополняем текстовый ключ, если слишком короткий, до 7 символов, ибо они в UTF8 равны 7 байтам( в UTF16 просто возьмется 3.5 символа,лол)
                }
                byte[] key_byte=string_to_bytes(key_buf);
                System.out.print("Ключ в виде байт : ");
                for(int i=0;i<key.length;i++){
                    key[i]=key_byte[i];      //Берем первые 7 байтов с введеного ключа, они и станут ключом (позже "Ужмем" их до 6 байт)
                    System.out.print(" "+key[i]);
                }
                System.out.println();
                jTextFieldKeyView.setText(bytes_to_string(key));
        }
        catch(IOException ex){
            jTextArea.setText("Неверный путь к файлу.");
        }
    };
    static void Set_hide_text(){
        hide_text = !hide_text;
    }
    
    static byte[] string_to_bytes(String in){
        return in.getBytes(SELECTED_CHARSET);
    }
    static String bytes_to_string(byte[] in){
        return new String(in,SELECTED_CHARSET);
    }
    static String byte_to_bit(byte in){    //НЕ ПОДХОДИТ, ПЕРЕДЕЛЫВАЕМ ПОД ПОДДЕРЖКУ UTF8
        StringBuilder out=new StringBuilder();
        for(int i=0;i<8;i++){
            out.append((int)(in>>(8-(i+1)) & 0x0001)); //& 00000001 сдвигаем байт и записываем крайний бит 8 раз(запишется весь байт)
        }
        return out.toString();
    }
    static String bytes_to_bits(byte[] in){
        StringBuilder out=new StringBuilder();
        for(byte b:in){
            out.append(byte_to_bit(b));
        }
        return out.toString();
    }
    static byte[] bits_to_bytes(String in){
    int splitSize = 8;
    if(in.length() % splitSize == 0){
        int index = 0;
        int position = 0;            
        byte[] resultByteArray = new byte[in.length()/splitSize];
        while (index < in.length()) {
            //String binaryStringChunk = text.substring(index, Math.min(index + splitSize, text.length()));
            String binaryStringChunk = in.substring(index, index + splitSize);
            Integer byteAsInt = Integer.parseInt(binaryStringChunk, 2);
            resultByteArray[position] = byteAsInt.byteValue();
            index += splitSize;
            position ++;
        }
        return resultByteArray;
    }
    else{
        System.out.println("Нельзя перевести бинарную строку в байты, так как" +in+"' % 8 != 0");
        return null;
    }
    
       /*int length=in.length()/8;
       byte[] result=new byte[length];
       for(int i=0;i<in.length();i+=8){
           result[i/8]=bit_to_byte(in.substring(i,i+8));
       }
       return result;*/
    }
    /*static byte bit_to_byte(String in){  //НЕ ПОДХОДИТ, ПЕРЕДЕЛЫВАЕМ ПОД ПОДДЕРЖКУ UTF8
        //Гавно, из за этого проблемы, надо поставить 8ой бит как "-"  : return Byte.parseByte(in,2);    //Ошибки дает, пример Value out of range. Value:"10110000" Radix:2, т.к. byte от -128 до 127
        return (byte) Short.parseShort(in,2); //Работает как надо для байтов от -128 до 127 (англ буквы)
    }*/
    static String bits_to_string(String in){///НЕ ПОДХОДИТ, ПЕРЕДЕЛЫВАЕМ ПОД ПОДДЕРЖКУ UTF8
    /*
    StringBuilder result=new StringBuilder();
    for(int i=0;i<in.length();i+=8){
        result.append((char)bit_to_byte(in.substring(i, i+8)));
        System.out.println("int["+i+"]= "+bit_to_byte(in.substring(i, i+8)));
        System.out.println("int["+i+"]= "+(char)bit_to_byte(in.substring(i, i+8)));
    }
    return result.toString();*/
    return bytes_to_string(bits_to_bytes(in));
    }
    static String IP_to_bit_block(String block){
        StringBuilder new_block=new StringBuilder();
        for(int i=0;i<block.length();i++){
            new_block.append(block.charAt(IP[i]-1));  //В самой перестановке индексы 1..64
        }
        return new_block.toString();
    }
    static String bit_summing_module_two(String L,String R){
       StringBuilder result=new StringBuilder();
       for(int i=0;i<L.length();i++){
           if(L.charAt(i)=='1' && R.charAt(i)=='1'){
               result.append('0');
           }else if(L.charAt(i)=='0' && R.charAt(i)=='0'){
               result.append('0');
           }else{
               result.append('1');
           }
       }
       return result.toString();
    }
    static int value_of_char_in_string(String in,char what){
        int result=0;
        for(int i=0;i<in.length();i++){
            if(in.charAt(i)==what){
            result++;
        }
        }
        return result;
    }
    static String key_to_CD_key(String old_key){  //ключ для шифровки i=1..16    //Переводим 56 битный ключ в 48 битный (сначала расширяем до 64 битного матрицей, делаем перестановки CD
        StringBuilder wildth_key=new StringBuilder();
        int count_of_ones=0;
        for(int i=0;i<old_key.length();){    // расширяем 56 битный ключ до 64 битного, вставляя после каждого 7 бита очередного байта новый бит, который равен 1, если в предыдущих 7ми битах число единиц четное, иначе равен 0, в итоге получим 64 битный ключ , в каждом байте которого нечетное колво едениц
            count_of_ones=value_of_char_in_string(old_key.substring(i,i+7),'1');
            wildth_key.append(old_key.substring(i,i+7));
            if(count_of_ones%2==0){
                wildth_key.append('1');
            }else{
                wildth_key.append('0');
            }
            i+=7;
        }
        StringBuilder CD_key=new StringBuilder();
        int index_CD=0;
        for(int i=0;i<wildth_key.length();i++){        //Делаем 1ую CD  перестановку над расширенным ключом
            if((i+1)%8==0){                  //Перестановка не затрагивает биты, кратные 8, на которые мы расширяли
                CD_key.append(wildth_key.charAt(i));//БАГ РЕШЕН (было (i+1%8==0): условие не выполняется, тупо только else , потому и выходит за индекс не доделав строку
                System.out.println("в ключ без изменений внесен +"+i+"бит");//баг был в том, что я баран, ибло у % приоритет выше, чем у +
            }else{                  
                CD_key.append(wildth_key.charAt(CD[index_CD++]-1));     //сам индекс с 1цы                  //????? убедиться , что пробежит от 0 до 55
            }
        }//НОВЫЙ БАГ, CD_key на 1 бит меньше wildth_key НАШЕЛ ПРИЧИНУ послдений бит, который так же не трогается CD перестановкой, у меня пропускается вообще и не заносится в новый ключ ИНОГДА???
        return CD_key.toString();
    }
    static String sdvigged_CD_key_to_CDtwo_key(String sdvigged_CD_key){//берем уже сдвинутый ключ и ужимаем матрицей CD2 , Получаем финальный ключ iый 48 битный ВРОДЕ бы для шифра и расшифра ужим по одной матрице
        StringBuilder new_key=new StringBuilder();
        for(int i=0;i<CD2.length;i++){
            new_key.append(sdvigged_CD_key.charAt(CD2[i]-1));
        }
        return new_key.toString();
    }
    static String CD_key_sdvig_to_right(String key,int pos){            //число позиций для сдвига берем по матрице СВДИГА
        StringBuilder new_key=new StringBuilder();
        if(pos==0){ return key; } //Для поддержки сдвига на 0 при расшифровке  
        if(pos==1){
            new_key.append(key.charAt(63)); // пихаем последний бит ключа в начало нового ключа
            new_key.append(key.substring(0, 63)); //присоединяем к новому ключу все оставшиеся биты, Кроме последнего, получается цикл сдвиг вправо
        }else{
            new_key.append(key.charAt(62));  //аналогично но перемещаем 2 бита
            new_key.append(key.charAt(63));
            new_key.append(key.substring(0, 62));
        }
        return new_key.toString();
    }
    static String CD_key_sdvig_to_left(String key,int pos){
        StringBuilder new_key=new StringBuilder();
        if(pos==1){
            new_key.append(key.substring(1, 64)); //присоединяем к новому ключу все  биты, Кроме первого
            new_key.append(key.charAt(0)); // пихаем первый бит ключа в конец нового ключ, получается цикл сдвиг влево
        }else{
            new_key.append(key.substring(2, 64));
            new_key.append(key.charAt(0));  
            new_key.append(key.charAt(1));           //аналогично, но перемещаем 2 бита
        }
        return new_key.toString();  
    }
    static int[] Bone_block_to_indexs(String inbits){ //101111  //поможет облегчить код создания матрицы B2 в фейстеле
        int[] indexs=new int[2];
        String krai_bits="";
        krai_bits+=inbits.charAt(0);
        krai_bits+=inbits.charAt(5);
        indexs[0]=Integer.parseInt(krai_bits,2);                //получаем индексстроки         
        String middle_bits=inbits.substring(1, 5);
        indexs[1]=Integer.parseInt(middle_bits,2);   //Получаем индекс столбца
        return indexs;
    }
    static String int_to_bits(int in){ //поможет облегчить код создания матрицы B2 в фейстеле  ВОЗМОЖНО, РЕАЛИЗАЦИЯ НЕ ПОДХОДИТ
        String result = "";
        while(in>0){
            result =  ( (in % 2 ) == 0 ? "0" : "1") +result;
            in = in / 2;
        }
        return result;
    }
    static String feistel_crypt(String old_R,String key){   //на вход идет R 32 бита и СД2 ключ 48 бит
        StringBuilder old_R_to_E=new StringBuilder();
        for(int i=0;i<E.length;i++){    //Расширяем старую Ri-1 С помощью матрицы  E , дублируя биты 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21, 24, 25, 28, 29, 32
            old_R_to_E.append(old_R.charAt(E[i]-1));  //индексы в E с 1цы
        }
        String E=old_R_to_E.toString();  //Получили матрицу E 48 бит
        String E_plus_key=bit_summing_module_two(E,key);
        String[] Bone_blocks=new String[8];
        int j=0;  //Фиксим баг , чтобы нормально шло по индексам
        for(int i=0;i<48;){
            Bone_blocks[j]=E_plus_key.substring(i, i+6); //БАГ РЕШЕНО вылзает за массив на 12 индексе,написал херь, не так расставил индексы //Заполняем 8 блоков B1 по 6 бит из E+key блока
            i+=6;
            j++;
        }
        String[] Btwo_blocks=new String[8];     //так, тут без стакана не поймешь, даьше пиздец, который очень "понятно" описан в вики
            for(int i=0;i<8;i++){                      ///МАтрица S делится на 8 под матриц, по 4(0..3) строки и 16(0..15) столбцов     //ДАльше смотрим 2 крайних  бита одного 6ти битового блока и 4 средних бита между ними. 2 крайних бита переводи в число и получаем индекс строки для iого блока (строка и столбец идут по  iому S), 4 средних бита переводим в число и получаем индекс столбца ,потом смотрим какое число на пересечение этих индексов стоит в текущей S и битовое представление этого числа заносим в новый 4х битный блок B2 iое, кончил   
                int []indexs=Bone_block_to_indexs(Bone_blocks[i]);
                Btwo_blocks[i]=int_to_bits(S[indexs[0]+(i*4)][indexs[1]]);   //БАГ РЕШЕНО(необычный баг, просто криворуко скопировал матрицу S с вики, пропустил 1 строку:) ) выходит за пределы на 32 индексе ИНОГДА?///??? Убедиться, что индекс строки скачет правильно по S iым
                while(Btwo_blocks[i].length()<4){   ////Убедимся, что все блоки B2 вышли по 4 бита ,пхаем 0ли в старшие
                    Btwo_blocks[i]='0'+Btwo_blocks[i];
                }
            }                                      //пример B1(3)=101111 , крайние биты равны 11 = 3, а между ними 0111=7,  значит индексы (3,7) смотрим в S(3) и видим что по индексам (3,7) лежит число 7=0111, заносим 0111 в B2(3)
        String Btwo_united_block="";
        for(String s:Btwo_blocks){
            Btwo_united_block+=s;
        }
        StringBuilder Btwo_united_block_P=new StringBuilder();    //Соединяем 8 4х битный блоков B2 в 1 блок 32 бит и применяем к нему последную перестановку P
        for(int i=0;i<Btwo_united_block.length();i++){
            Btwo_united_block_P.append(Btwo_united_block.charAt(P[i]-1));        //Индексы в P идут с 1 
        }
        return Btwo_united_block_P.toString();
    }
    static String code_all_round_feistel(String L,String R,String key){
        String result="";
        String CD_key=key_to_CD_key(key); ///получаем CD ключ 64 бит
        for(int i=0;i<COUNT_OF_ROUNDS;i++){
            String old_L=L;
            String old_R=R;
            CD_key=CD_key_sdvig_to_left(CD_key,SDVIGI_TO_CRYPT[i]);  //получаем CD iый ключ сдвинутый по iому сдвигу
            String CDtwo_key=sdvigged_CD_key_to_CDtwo_key(CD_key);        //получаем CD2 iый ключ по ужимающей матрице CD2 
            System.out.print("  code CDtwo_key["+i+"]= "+bits_to_string(CDtwo_key));
            L=old_R;
            R=bit_summing_module_two(old_L,feistel_crypt(old_R,CDtwo_key));
        }
        result+=L;
        result+=R;
        return result;
    }
    static String decode_all_round_feistel(String L,String R,String key){
        String result="";
        String CD_key=key_to_CD_key(key); ///получаем CD ключ 64 бит
        for(int i=0;i<COUNT_OF_ROUNDS;i++){
            String old_L=L;
            String old_R=R;
            CD_key=CD_key_sdvig_to_right(CD_key,SDVIGI_TO_UNCRYPT[COUNT_OF_ROUNDS-1-i]); //Индекс чтобы идти от конца матрицы к началу //получаем CD iый ключ сдвинутый по iому сдвигу
            String CDtwo_key=sdvigged_CD_key_to_CDtwo_key(CD_key);        //получаем CD2 iый ключ по ужимающей матрице CD2      
            System.out.print(" decode CDtwo_key["+i+"]= "+bits_to_string(CDtwo_key));
            R=old_L;
            //L=old_R;
            //R=bit_summing_module_two(old_L,feistel_crypt(old_R,CDtwo_key));
            L=bit_summing_module_two(old_R,feistel_crypt(old_L,CDtwo_key));
        }
        result+=L;
        result+=R;
        return result;
    }
    static String block_to_feistel_code(String block,String key){
        String IP_block=IP_to_bit_block(block);         //Делаем IP перестановку над входным блоком
        String L=IP_block.substring(0, 32);// берем 32 старших бита блока
        String R=IP_block.substring(32, 64); //Берем 32 младших бита блока
        String result_block=code_all_round_feistel(L,R,key);  //Получаем кодированный блок и делаем над ним последнюю обратную IP перестановку
        StringBuilder IP_back_result_block=new StringBuilder();
        for(int i=0;i<result_block.length();i++){
            IP_back_result_block.append(result_block.charAt(IP_TO_BACK[i]-1));   // в матрице индексы с 1цы
        }
        return IP_back_result_block.toString();
    }
    static String block_to_feistel_decode(String block,String key){
        String IP_block=IP_to_bit_block(block);         //Делаем IP перестановку над входным блоком
        String L=IP_block.substring(0, 32);// берем 32 старших бита блока
        String R=IP_block.substring(32, 64); //Берем 32 младших бита блока
        String result_block=decode_all_round_feistel(L,R,key);  //Получаем кодированный блок и делаем над ним последнюю обратную IP перестановку
        StringBuilder IP_back_result_block=new StringBuilder();
        for(int i=0;i<result_block.length();i++){
            IP_back_result_block.append(result_block.charAt(IP_TO_BACK[i]-1));   // в матрице индексы с 1цы
        }
        return IP_back_result_block.toString();
    }   
    
    static byte[] extend_input_bytes(byte[] input){
        int count_of_added_byte=input.length;
        while(count_of_added_byte%8!=0){ //Если число байтов не кратно 8ми, то дополняем, чтобы делились ровно на блоки по 8 байт
            count_of_added_byte++;
        }
        byte[] new_input_bytes=new byte[count_of_added_byte];
        for(int i=0;i<input.length;i++){
            new_input_bytes[i]=input[i];
        }
        for(int j=count_of_added_byte-1;j>=input.length;j--){
            new_input_bytes[j]=(byte)1;
        }
        return new_input_bytes;
    }
    static void Coding(JTextArea jTextAreaRight,JTextArea jTextAreaLeft){
        if(path_key_input==null){ jTextAreaRight.setText("Сначала укажите путь для импорта ключа!!"); return; } 
        if(path_input==null){ jTextAreaRight.setText("Сначала укажите путь к импортируемому файлу!!"); return; }
        if(path_output==null){ jTextAreaLeft.setText("Сначала укажите путь к экспортируемому файлу!!"); return; }
        if(hide_text){
            jTextAreaRight.setText(input_string);
        }
        byte[] input_string_bytes=string_to_bytes(input_string);  //открытый текст в виде байт
        System.out.println("Bytes :"); //выводит корректно
        for(byte b:input_string_bytes){
            System.out.print(b);     //выводит корректно
        }
        System.out.println("String :"+bytes_to_string(input_string_bytes)); //выводит корректно
        input_string_bytes=extend_input_bytes(input_string_bytes); //Если число байтов не кратно 8ми, то дополняем, чтобы делились ровно на блоки по 8 байт
        byte[][] input_string_bytes_blocks=new byte[input_string_bytes.length/8][8];
        int index=0;
        for(int i=0;i<input_string_bytes.length/8;i++){
            for(int j=0;j<8;j++){
                input_string_bytes_blocks[i][j]=input_string_bytes[index++];      //Разбиваем открытый текст на блоки по 8 байт
            }
        }
        String[] input_string_bite_blocks=new String[input_string_bytes.length/8]; 
        for(int i=0;i<input_string_bytes.length/8;i++){
            input_string_bite_blocks[i]="";   //Исправили null в начале каждого ебучего блока
            for(int j=0;j<8;j++){
                input_string_bite_blocks[i]+=byte_to_bit(input_string_bytes_blocks[i][j]);      //Разбиваем открытый текст на блоки по 64 бит!
            }
            System.out.println("input_string_bites_blocks["+i+"] ="+input_string_bite_blocks[i]);   //ИСПРАВИЛ Блоки выходят по 64 бита, но в каждом блоке перед ними еще ебаный null ИСПРАВИЛ
            System.out.println("input_string_bites_blocks["+i+"] в виде символов ="+bits_to_string(input_string_bite_blocks[i]));//ИСПРАВИЛ( не передаю парсинг инта в чар, а использую мою функцию битов в байт, а потом в чар) выводит НЕкорректно,некоторые символы содержат 2 байта с 1но байтовыми английскими тоже!!
            System.out.print("input_string_bites_blocks["+i+"] в виде байтов = ");  //ИСПРАВИЛ( не передаю парсинг инта в чар, а использую мою функцию битов в байт) Полученные байты не соответствуют ИСХОДНЫМ ИСПРАВИТЬ ЭТО С УТРА!! СМОТРЕТЬ ОТСЮДА         
            byte[] temp=bits_to_bytes(input_string_bite_blocks[i]);
            System.out.println(Arrays.toString(temp));
            /*for(int j=0;j<input_string_bite_blocks[i].length();j+=8){
                temp[j/8]=bit_to_byte(input_string_bite_blocks[i].substring(j, j+8)); //ИСПРАВИЛ ТУТ ОШИБКА БРАЛ ПО 7ой , а второй индекс НЕ ВКЛЮЧИТЕЛЕН
                System.out.print(input_string_bite_blocks[i].substring(j, j+8)+" = "+temp[j/8]+" ");
            }*/
            System.out.println("input_string_bites_blocks["+i+"] эти байты в виде Символов ="+bytes_to_string(temp));    //Байты в символы == биты в символы - это хорошо, символы левые - это плохо
        }
        String bits_key="";
        for(int i=0;i<key.length;i++){               //Получили ключ в виде битового блока
            bits_key+=byte_to_bit(key[i]);    
        }
        System.out.println("Исходный ключ в виде бит блока: "+bits_key);
        System.out.println("Исходный ключ в виде байт: "+Arrays.toString(bits_to_bytes(bits_key)));
        String[] output_string_bite_blocks=new String[input_string_bite_blocks.length];
        for(int i=0;i<output_string_bite_blocks.length;i++){
            output_string_bite_blocks[i]=block_to_feistel_code(input_string_bite_blocks[i],bits_key); //Сам процесс (в decode_key поместим блок из 48 бит последнего ключа)
        }
        for(String s:output_string_bite_blocks){
            System.out.println("output_string_bite_blocks[] ="+s);
        }
        for(String s:output_string_bite_blocks){
            System.out.println("output_string_bite_blocks in string[] ="+bits_to_string(s));
        }
        //Переводим out бит блоки в массив байтов
        byte[] output_string_bytes=new byte[input_string_bytes.length];///Точно ли????????????????????? длина будет такая же , как и у входных блоков
        int index_output_byte=0;
        for(int i=0;i<output_string_bite_blocks.length;i++){   //Переводим out бит блоки в массив байтов
            for(int j=0;j<8;j++){
            output_string_bytes[index_output_byte++]=bits_to_bytes(output_string_bite_blocks[i])[j];//Баг с ParseByte,не дает парсить байты начинающиеся на 1, т.к. они вне диапазона byte -128 127 ,если подумать, все символы идут со страшим 0ем , может парсю строки не в том направлении?   //??????????????Проверить что делит строки нормально по 8 байтов 
            } //Решение бага?: поменять на ParseInt и фигачить сразу в символы char,а затем в строку
        }
        System.out.println("output_string_bytes: "+Arrays.toString(output_string_bytes));
          //Переводим массив out байтов в строку
        
        output_string=bytes_to_string(output_string_bytes);
        System.out.println("output_string "+output_string);
        if(hide_text){
            jTextAreaLeft.setText(output_string);
        }
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(path_output))){
            writer.write(output_string);
        }
        catch(IOException ex){
            jTextAreaRight.setText("Неверный путь к файлу.");
        }
        output_string="";
    }
    static void DeCoding(JTextArea jTextAreaRight,JTextArea jTextAreaLeft){
        if(path_key_input==null){ jTextAreaRight.setText("Сначала укажите путь для импорта ключа!!"); return; } 
        if(path_input==null){ jTextAreaRight.setText("Сначала укажите путь к импортируемому файлу!!"); return; }
        if(path_output==null){ jTextAreaLeft.setText("Сначала укажите путь к экспортируемому файлу!!"); return; }
        if(hide_text){
            jTextAreaRight.setText(input_string);
        }
        byte[] input_string_bytes=string_to_bytes(input_string);  //открытый текст в виде байт
        System.out.println("Bytes :"); //выводит корректно
        for(byte b:input_string_bytes){
            System.out.print(b);     //выводит корректно
        }
        System.out.println("String :"+bytes_to_string(input_string_bytes)); //выводит корректно
        input_string_bytes=extend_input_bytes(input_string_bytes); //Если число байтов не кратно 8ми, то дополняем, чтобы делились ровно на блоки по 8 байт
        byte[][] input_string_bytes_blocks=new byte[input_string_bytes.length/8][8];
        int index=0;
        for(int i=0;i<input_string_bytes.length/8;i++){
            for(int j=0;j<8;j++){
                input_string_bytes_blocks[i][j]=input_string_bytes[index++];      //Разбиваем открытый текст на блоки по 8 байт
            }
        }
        String[] input_string_bite_blocks=new String[input_string_bytes.length/8]; 
        for(int i=0;i<input_string_bytes.length/8;i++){
            input_string_bite_blocks[i]="";   //Исправили null в начале каждого ебучего блока
            for(int j=0;j<8;j++){
                input_string_bite_blocks[i]+=byte_to_bit(input_string_bytes_blocks[i][j]);      //Разбиваем открытый текст на блоки по 64 бит!
            }
            System.out.println("input_string_bites_blocks["+i+"] ="+input_string_bite_blocks[i]);   //ИСПРАВИЛ Блоки выходят по 64 бита, но в каждом блоке перед ними еще ебаный null ИСПРАВИЛ
            System.out.println("input_string_bites_blocks["+i+"] в виде символов ="+bits_to_string(input_string_bite_blocks[i]));//ИСПРАВИЛ( не передаю парсинг инта в чар, а использую мою функцию битов в байт, а потом в чар) выводит НЕкорректно,некоторые символы содержат 2 байта с 1но байтовыми английскими тоже!!
            System.out.print("input_string_bites_blocks["+i+"] в виде байтов = ");  //ИСПРАВИЛ( не передаю парсинг инта в чар, а использую мою функцию битов в байт) Полученные байты не соответствуют ИСХОДНЫМ ИСПРАВИТЬ ЭТО С УТРА!! СМОТРЕТЬ ОТСЮДА         
            byte[] temp=bits_to_bytes(input_string_bite_blocks[i]);
            System.out.println(Arrays.toString(temp));
            /*for(int j=0;j<input_string_bite_blocks[i].length();j+=8){
                temp[j/8]=bit_to_byte(input_string_bite_blocks[i].substring(j, j+8)); //ИСПРАВИЛ ТУТ ОШИБКА БРАЛ ПО 7ой , а второй индекс НЕ ВКЛЮЧИТЕЛЕН
                System.out.print(input_string_bite_blocks[i].substring(j, j+8)+" = "+temp[j/8]+" ");
            }*/
            System.out.println("input_string_bites_blocks["+i+"] эти байты в виде Символов ="+bytes_to_string(temp));    //Байты в символы == биты в символы - это хорошо, символы левые - это плохо
        }
        String bits_key="";
        for(int i=0;i<key.length;i++){               //Получили ключ в виде битового блока
            bits_key+=byte_to_bit(key[i]);
        }
        String[] output_string_bite_blocks=new String[input_string_bite_blocks.length];
        for(int i=0;i<output_string_bite_blocks.length;i++){
            output_string_bite_blocks[i]=block_to_feistel_decode(input_string_bite_blocks[i],bits_key);  //Все меняем отсюда, переделываем функцию под дешифровку
        }
        for(String s:output_string_bite_blocks){
            System.out.println("output_string_bite_blocks[] ="+s);
        }
        byte[] output_string_bytes=new byte[input_string_bytes.length];///Точно ли????????????????????? длина будет такая же , как и у входных блоков
        int index_output_byte=0;
        for(int i=0;i<output_string_bite_blocks.length;i++){   //Переводим out бит блоки в массив байтов
            for(int j=0;j<8;j++){
            output_string_bytes[index_output_byte++]=bits_to_bytes(output_string_bite_blocks[i])[j];//Баг с ParseByte,не дает парсить байты начинающиеся на 1, т.к. они вне диапазона byte -128 127 ,если подумать, все символы идут со страшим 0ем , может парсю строки не в том направлении?   //??????????????Проверить что делит строки нормально по 8 байтов 
            } //Решение бага?: поменять на ParseInt и фигачить сразу в символы char,а затем в строку
        }
        System.out.println("output_string_bytes: "+Arrays.toString(output_string_bytes));
          //Переводим массив out байтов в строку
        
        output_string=bytes_to_string(output_string_bytes);
        System.out.println("output_string "+output_string);
        if(hide_text){
            jTextAreaLeft.setText(output_string);
        }
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(path_output))){
            writer.write(output_string);
        }
        catch(IOException ex){
            jTextAreaRight.setText("Неверный путь к файлу.");
        }
        output_string="";
        
    }
    static void Hacking(JTextArea jTextArea){
        
    }
    public static void main(String[] args) {
        
    }
}
