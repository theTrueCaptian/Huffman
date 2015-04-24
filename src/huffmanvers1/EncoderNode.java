package huffmanvers1;

/**
 *
 * Maeda Hanafi
 *
 *this node contains information on the letter and its variable and fixed length translation
 */
 public class EncoderNode {
    private char letter;
    private String variableTranslation;//huffman tree result
    private String fixedTranslation;//an arbitrary fixed length representation of letter
    //ascci holds the binary representation of the ascii of letter
    private String ascii;
    public EncoderNode(char letter, String variableTranslation, String fixedTranslation){
        this.letter = letter;
        this.variableTranslation = variableTranslation;
        this.fixedTranslation = fixedTranslation;
        System.out.println("New Encoder Node: "+letter+" "+variableTranslation+" "+ fixedTranslation);
        //get the ascii binary rep
        ascii = Integer.toBinaryString((char)letter);
        //make ascii have 8 bits
        int rem = 8-ascii.length();
        for(int i=0; i<rem; i++)
            ascii = "0"+ascii;
        System.out.println("ascii binary representation of "+letter+":"+ascii);
    }
    public String getVariableTranslation(){
        return variableTranslation;
    }
    public int getVariableLength(){
        return variableTranslation.length();
    }
    public String getFixedTranslation(){
        return fixedTranslation;
    }
    public char getLetter(){
        return letter;
    }
    public String getAsciiValue(){
        return ascii;
    }
}
