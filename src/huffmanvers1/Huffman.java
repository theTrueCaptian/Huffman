package huffmanvers1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Maeda Hanafi
 * Huffman Encoding
 * For compression, the program reads the text file and creates FrequencyNodes that are stored in a priority queue.
 * The huffman algorithm is applied to the priority queue(priorityList). The priorityList becomes a tree and it is traversed
 * to get information on encoding a letter. In addition to creating the huffman translation, the program also creates the
 * fixed translations. Afterwards, the program goes through the contents of the text file and outputs it using
 * BitOutputStream using the following format:
 * format: numberOfDistinctCharacters(8 bits) asciiBinaryValueofLetter(8 bits) lengthOfVariableTranlsation(8bits) variableHuffmanTranslation lengthOfMessage(8 bits) compressedMessage
 * Finally, the ratio is computed.
 * For decompression, the program reads the .dat file with a specified format. Firstly, the program extracts information on
 * the encoding and, then decodes the contents of the .dat file. Afterwards, the decoded result is printed to the specified text file.
 *
 */
public class Huffman {
    //files to input and output
    String input_file;
    String output_file;
    //String that holds the contents of the file
    String contents = "";
    //number of distinct letters in the file
    int letter = 0;

    //holds information on how to encode letters
    ArrayList<EncoderNode> encoder = new ArrayList<EncoderNode>();

    //Variable length translation
    //priorityList is the huffmantree is going to be constructed
    ArrayList<FrequencyNode> priorityList = new ArrayList<FrequencyNode>();

    //Fixed length Translation
    //contains nformation on the number of bits for fixed length translation
    int fixedLength = 0;
    //array holding fixed length translation
    String[] fixedLengthArray;
    //counter used for fixedLengthArray
    int counter = 0;

    //contents of the output file
    String outputContents = "";
    
    public Huffman(String input_file, String output_file){
        this.input_file = input_file;
        this.output_file = output_file;
    }

    public static void main(String[] args) {         
         String input_file = args[0];
         String output_file = args[1];
         System.out.println("input:"+input_file+" output:"+output_file);
    
         Huffman huffman = new Huffman(input_file, output_file);
         int extTest = huffman.checkExtension(input_file);
         if(extTest == 1){
             //read .txt file and compress
             huffman.compress();
         }else if(extTest == 0){
             //read the .dat file and decompress
             huffman.decompress();
         }else{
             System.out.println("Sorry the input file doesn't have a valid extension.");
         }
         
    }

    //this methods calls other methods to compress files
    public void compress(){
        readTextFile();
        createTree();
        generateFixedTranslation();
        traverseTree(priorityList.get(0),"");
              
        outputDatFile();
        calculateRatio();
    }

    //returns 1 if the file is .txt or 0 if the file is .dat
    //if the extension is invalid it returns -1
    public int checkExtension(String file){
        String extension = "" + file.charAt(file.length()-3) + file.charAt(file.length()-2) + file.charAt(file.length()-1);
        if(extension.equals("txt")){
            return 1;
        }else if(extension.equals("dat")){
            return 0;
        }else{
            return -1;
        }
    }

    //reads the inpt_file if it is a text file and calculates the frequency.
    public void readTextFile(){
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(input_file));
            while((line = reader.readLine())!=null){
                for(int i=0; i<line.length(); i++)
                    if(!discard(line.charAt(i))){
                        calculateFrequencySoFar(line.charAt(i));
                        contents = contents + line.charAt(i);
                    }
                    
            }
            //calculations for the number of distinct letters 
            letter = priorityList.size();
            
            priorityList = (ArrayList<FrequencyNode>)mergeSort(priorityList);
            //display frequencies
            System.out.println("After reading the file and sorting:");
            for(int i=0; i<priorityList.size(); i++){
                System.out.println(priorityList.get(i).getFrequency()+": "+priorityList.get(i).getCharacter());
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Calculates the frequency of the letter and adds the frequencies to the frequencies so far.
    public void calculateFrequencySoFar(char letter){
        //find the node in the priorityList that represents the letter.
        //if the node doesn't exist then create a new frequency node and add it to the frequency node
        boolean inc = false;
        for(int i=0; i<priorityList.size(); i++){
            if(priorityList.get(i).getCharacter()==letter){
                priorityList.get(i).incrementFrequency();
                inc = true;
                break;
            }
        }
        //should we create another node?
        if(!inc ){
            //create a node and increment
            priorityList.add(new FrequencyNode(1, letter));
            inc = true;
        }
        
    }

    //decides whether to dicard the character
    public boolean discard(char inChar){
        if(((int)inChar>=48 && (int)inChar<=57)||((int)inChar>=65 && (int)inChar<=90)||((int)inChar>=97 && (int)inChar<=122)||
                inChar=='!'||inChar=='.'|| inChar==','||inChar=='?'){
            return false;
        }else{
            return true;
        }
    }
    //sorts priorityList
    private List<FrequencyNode> mergeSort(List<FrequencyNode> list){
        if (list.size() > 1) {
            // Merge sort the first half
            List<FrequencyNode> firstHalf = list.subList(0, (int)list.size()/2);
            firstHalf = mergeSort(firstHalf);
            // Merge sort the second half
            List<FrequencyNode> secondHalf = list.subList((int)list.size() / 2, list.size());
            secondHalf = mergeSort(secondHalf);
            // Merge firstHalf with secondHalf
            List<FrequencyNode> temp = merge(firstHalf, secondHalf);
            return temp;
        }
        return list;

    }
    
    private List<FrequencyNode> merge(List<FrequencyNode> list1, List<FrequencyNode> list2) {
        List<FrequencyNode> temp = new ArrayList<FrequencyNode>();
        int current1 = 0; // Index in list1
        int current2 = 0; // Index in list2
        int current3 = 0; // Index in temp

        while (current1 < list1.size() && current2 < list2.size()) {
            if (list1.get(current1).getFrequency() < list2.get(current2).getFrequency())
                temp.add(current3++, list1.get(current1++));
            else
                temp.add(current3++, list2.get(current2++));
        }

        while (current1 < list1.size())
             temp.add(current3++, list1.get(current1++));

        while (current2 < list2.size())
             temp.add(current3++, list2.get(current2++));

        return temp;
    }

    //create the tree to encode the file
    private void createTree(){
        //the 2 nodes with least frequency are going to be connected with a new node   
        while(priorityList.size()!=1){
            //create a node
            FrequencyNode connector = new FrequencyNode(priorityList.get(0).getFrequency()+priorityList.get(1).getFrequency(), priorityList.get(0), priorityList.get(1));
            //add to the priorityList and sort
            priorityList.remove(0);
            priorityList.remove(0);
            priorityList.add(connector);
            
            priorityList = (ArrayList)mergeSort(priorityList);
            
        }
    }

    //generates the fixed length translation
    private void generateFixedTranslation(){
        //calculate the required number of bits for each letter
        //check if the calcultion is going to be a decimal number
        System.out.println("Number of bits required for fixed length translation:"+Math.pow((double)letter, 0.5)%1);
        if(Math.pow((double)letter, 0.5)%1==0){ //the number is an integer so the length doesn't need an extra bit
            fixedLength = (int) Math.pow((double)letter, 0.5);
        }else{ //the length needs an extra bit
            fixedLength = (int) Math.pow((double)letter, 0.5)+1;
        }
        
        System.out.println("fixedlength:"+fixedLength);
        //initialize the array
        fixedLengthArray = new String[letter];
        //generate binary numbers for each letter
        System.out.println("fixedlengtharray contents:");
        for(int i=0; i<letter; i++){
            String binaryNum = Integer.toBinaryString(i);
            //concatenate enough zeros in the front of the binary string to fulfill the number of bits for fixed length
            for(int j=0; j<fixedLength-Integer.toBinaryString(i).length(); j++){
                binaryNum = "0"+binaryNum;
            }
            System.out.println(binaryNum);
            fixedLengthArray[i] = binaryNum;
        }

    }

    //traversing the tree and creating EncoderNodes to hold information on letter translation (fixed and variable)
    private void traverseTree(FrequencyNode node, String num){
        if(node!=null){            
            System.out.println(node.getCharacter()+": "+node.getFrequency()+" "+num);
            //if a leaf is reached then record the information into encoder arraylist
            if(node.isLeaf()){
                encoder.add(new EncoderNode(node.getCharacter(), num, fixedLengthArray[counter]));
                counter++;
            }
            String path = num + "0";            
            traverseTree(node.getNode(0), path);
            String path1 = num + "1";            
            traverseTree(node.getNode(1), path1);
        }
    }
   
    //creates a .dat file with the decompressed information
    //format: numberOfDistinctCharacters(8 bits) asciibinaryValueofLetter(8 bits) lengthOfVariableTranlsation(8bits) variableHuffmanTranslation lengthOfMessage(8 bits) compressedMessage
    private void outputDatFile(){
        try {
            //DataOutputStream output = new DataOutputStream(new FileOutputStream(output_file));
            BitOutputStream output = new BitOutputStream(new FileOutputStream(output_file));
            //write the number of distinct characters
            System.out.println("Output into the dat file:");
            String formattedLetter = format8Bit(Integer.toString(letter, 2));
            System.out.println(formattedLetter);
            //output.writeBytes(formattedLetter);
            write(output, formattedLetter);
            //asciiBinaryValue lengthOfVariableTranlsation variableHuffmanTranslation
            for(int i=0; i<encoder.size(); i++){
                String varLength = format8Bit(Integer.toBinaryString(encoder.get(i).getVariableLength()));
                System.out.println(encoder.get(i).getAsciiValue()+" "+varLength+" "+encoder.get(i).getVariableTranslation());
                //output.writeBytes(encoder.get(i).getAsciiValue()+varLength+encoder.get(i).getVariableTranslation());
                write(output, encoder.get(i).getAsciiValue()+varLength+encoder.get(i).getVariableTranslation());
            }
            //write the lengthOfMessage(8 bits)
            System.out.println("contents:"+contents);
            String lengthOfMessage = format8Bit(Integer.toBinaryString(getLengthContents()));
            System.out.println("length of message:"+lengthOfMessage+"="+Integer.toString(contents.length(), 2));
            //output.writeBytes(lengthOfMessage);
            write(output, lengthOfMessage);
            
            //write the message
            for(int i=0; i<contents.length(); i++){
                //output.writeBytes(getVariableTranslation(contents.charAt(i)));
                write(output, getVariableTranslation(contents.charAt(i)));
            }
            output.close();

        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    //writes the binary String to the output stream
    private void write(BitOutputStream output, String inString){
        for(int i=0; i<inString.length(); i++){
            try {
                if(inString.charAt(i)=='0')
                    output.writeBit(0);
                else
                    output.writeBit(1);
            } catch (IOException ex) {
                Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //gets the length of the binary contents to be output
    private int getLengthContents(){
        int length = 0;
        for(int i=0; i<contents.length(); i++){
            length = length + getVariableTranslation(contents.charAt(i)).length();
        }
        return length;
    }
    //formats inString that contains binary numbers to have 8 bits
    private String format8Bit(String inString){
        int rem = 8-inString.length();
            for(int i=0; i<rem; i++){
                inString = "0"+inString;
            }
        return inString;
    }
    //gets the translation
    private String getVariableTranslation(char inChar){
        for(int i=0; i<encoder.size(); i++){
            if(encoder.get(i).getLetter()==inChar)
                return encoder.get(i).getVariableTranslation();
        }
        return "";
    }

    private String getFixedTranslation(char inChar){
        for(int i=0; i<encoder.size(); i++){
            if(encoder.get(i).getLetter()==inChar){
                return encoder.get(i).getFixedTranslation();
            }
        }
        return "";
    }

    //calculates the compression ratio = compressedSize/uncompressedSize
    private void calculateRatio(){
        double compressedSize = 0.0;
        for(int i=0; i<contents.length(); i++){
            compressedSize = compressedSize + getVariableTranslation(contents.charAt(i)).length();
        }
        double uncompressedSize = contents.length()*getFixedTranslation(contents.charAt(0)).length();
        double ratio = compressedSize/uncompressedSize;
        System.out.println("Ratio: "+ratio);
    }

    //decompress calls other methods to decompress
    private void decompress(){
        readDatFile();
        decodeMessage();
    }

    //This reads the .dat file
    private void readDatFile(){
        System.out.println("Reading:");
        try {
            //DataInputStream input = new DataInputStream(new FileInputStream(input_file));
            BitInputStream input = new BitInputStream(new FileInputStream(input_file));
            //reads numberOfDistinctCharacters(8 bits)
            String letterBits = "";
            letterBits = nextBits(input, 8);
            letter = Integer.parseInt(letterBits, 2);
            System.out.println("Number of distinct letters:"+letter);
            //generate fixed translations since the number of distinct letters is known
            generateFixedTranslation();
            //read information on translation: asciibinaryValueofLetter(8 bits) lengthOfVariableTranslation(8bits) variableHuffmanTranslation
            for(int i=0; i<letter; i++){
                //read asciibinaryValueofLetter(8 bits)
                String asciiBinary="";
                asciiBinary = nextBits(input, 8);
                int ascii = Integer.parseInt(asciiBinary, 2);
                char actualLetter = (char)ascii;
                System.out.println("ascii:"+asciiBinary+"="+(char)actualLetter);
                //read lengthOfVariableTranslation(8bits)
                String bitsVar = "";
                bitsVar = nextBits(input, 8);
                int varLength= Integer.parseInt(""+Integer.parseInt(bitsVar, 2), 10);
                System.out.println("length:"+bitsVar+"="+varLength);
                //read variableHuffmanTranslation
                String huffmanTrans = nextBits(input, varLength);
                System.out.println("huffman:"+huffmanTrans);

                //create a new encoder node to store information 
                encoder.add(new EncoderNode(actualLetter, huffmanTrans, fixedLengthArray[i]));
            }

            //lengthOfMessage(8 bits)
            String lengthOfMessage = nextBits(input, 8);
            int messageLength = Integer.parseInt(""+Integer.parseInt(lengthOfMessage, 2), 10);
            System.out.println("length of message "+messageLength);
            //read the rest of the message
            int ctr = 0;
            while(ctr<messageLength){
                int read = Integer.parseInt(nextBits(input, 1), 10);//input.read();
                contents = contents + read;//interpret(read);
                ctr++;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //reads the next X bits from the input
    private String nextBits(BitInputStream input, int x){
        String read = "";
        for(int i=0; i<x; i++){
            try {
                read = read + input.readBit();//interpret(input.read());
            } catch (IOException ex) {
                Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return read;
    }
   
    //this methods decodes the message using the encoder arraylist, which contains information on letter translation
    private void decodeMessage(){
        try {
            DataOutputStream output = new DataOutputStream(new FileOutputStream(output_file));
            System.out.println("contents:"+contents);
            int i=0;
            String temp = "";
            while(i<contents.length()){
                temp = temp + contents.charAt(i);
                i++;
                //if temp is a character, then output the translation
                char possibleChar = findLetter(temp);
                if(possibleChar!='~'){
                    System.out.print(possibleChar);
                    output.write(possibleChar);
                    temp = "";
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //returns the letter with the huffman binary representation
    private char findLetter(String binary){
        for(int i=0; i<encoder.size(); i++){
            if(binary.equals(encoder.get(i).getVariableTranslation())){
                return encoder.get(i).getLetter();
            }
        }
        return '~';
    }

}
