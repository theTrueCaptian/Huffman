package huffmanvers1;

/**
 *
 * Maeda Hanafi
 * Node that represents a letter from the file and its frequency
 */
public class FrequencyNode {
    private int frequency;
    private char character;
    
    //this is only used if the node is not a leaf of the huffman tree
    private FrequencyNode zero;
    private FrequencyNode one;

    private boolean leaf = false;

    //this constructor is called when it creates a leaf of the tree
    public FrequencyNode(int frequency, char character){
        this.frequency = frequency;
        this.character = character;
        leaf = true;
    }

    //this contructor is for nodees that aren't leaves of the tree
    public FrequencyNode(int frequency, FrequencyNode zero, FrequencyNode one){
        this.frequency = frequency;
        System.out.println("New frequency node: freq = "+frequency+", letters:"+zero.character+" "+one.character);
        this.zero = zero;
        this.one = one;
    }

    public boolean isLeaf(){
        return leaf;
    }

    public int getFrequency(){
        return frequency;
    }

    public char getCharacter(){
        return character;
    }

    public void incrementFrequency(){
        frequency++;
    }

    //this returns the node that is indicated by "direction" (left or right)
    public FrequencyNode getNode(int direction){
        if(direction==0)
            return zero;
        else
            return one;
    }
}
