/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int MAX_BIT_WIDTH = 16;
    private static final int MIN_BIT_WIDTH = 9;
    private static final double COMPRESSION_RATIO_THRESHOLD = 1.1; //In monitor mode, don't let the ratio of compression ratios exceed this value
    private static int R = 256;             // number of input chars
    private static int W = 9;               // codeword width
    private static int L = (int) Math.pow(2, W);  // number of codewords = 2^W
    private static char Mode = 'n';

    //Initializes a symbol table object from TST.java used in the compress() function
    public static void initializeSymbolTable(TST<Integer> table){
        for (int i = 0; i < R; i++)
            table.put("" + (char) i, i);
    }

    //Returns a brand new String[] symbol table used in the expand() function
    public static String[] initializeSymbolArray(){
        String[] array = new String[(int) Math.pow(2, MAX_BIT_WIDTH)]; //This is the maximum number of codewords there could be
        int i;
        for (i = 0; i < R; i++)
            array[i] = "" + (char) i;
        array[i++] = "";                        // (unused) lookahead for EOF
        return array;
    }

    public static void compress() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        initializeSymbolTable(st);
        int code = R+1;  // R is codeword for EOF

        BinaryStdOut.write((byte) Mode);

        int uncompressedDataSize = 0; //Input data
        int compressedDataSize = 0; //Output data
        double oldCompressionRatio = 0.0;
        double newCompressionRatio = 0.0;

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();

            uncompressedDataSize += (t*16); //Data read is in the length of the string multiplied by 16-bits (size of each char)
            compressedDataSize += W; //Data written is current codeword width

            if (t < input.length() && code < L){    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
                oldCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
            } else if(t < input.length() && code >= L){ //The codebook is full at the moment
                if(W < MAX_BIT_WIDTH){ //We can still add words to the codebook without it being full
                    L = (int) Math.pow(2, ++W); //Expand the bit-width and add more codeword slots
                    st.put(input.substring(0, t + 1), code++);
                } else if(W == MAX_BIT_WIDTH){ //Symbol table is completely full
                    if(Mode == 'r'){ //Reset mode
                        //Reset the codebook
                        st = new TST<Integer>();
                        initializeSymbolTable(st);
                        W = MIN_BIT_WIDTH;
                        L = (int) Math.pow(2, W);
                        code = R+1;

                        st.put(input.substring(0, t + 1), code++); //Now, since there is room, add the new codeword
                    } else if(Mode == 'm'){ //Monitor mode
                        newCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
                        double ratioOfCompressionRatios = oldCompressionRatio/newCompressionRatio;
                        if(ratioOfCompressionRatios > COMPRESSION_RATIO_THRESHOLD){
                            //Reset the codebook
                            st = new TST<Integer>();
                            initializeSymbolTable(st);
                            W = MIN_BIT_WIDTH;
                            L = (int) Math.pow(2, W);
                            code = R+1;

                            st.put(input.substring(0, t + 1), code++); //Now, since there is room, add the new codeword
                        }
                    } //Else, Mode is 'n', which means "do nothing"
                }
            }
            input = input.substring(t);            // Scan past s in input.
        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    public static void expand() {
        String[] st = initializeSymbolArray(); // initialize symbol table with all 1-character strings
        int i = R+1; // next available codeword value

        Mode = BinaryStdIn.readChar(); //Read in the mode, the first byte of the file
        if(Mode != 'n' && Mode != 'r' && Mode != 'm') throw new IllegalArgumentException("This file contains an invalid compression mode: " + Mode);

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        int uncompressedDataSize = 0;
        int compressedDataSize = 0;
        double oldCompressionRatio = 0.0;
        double newCompressionRatio = 0.0;

        while (true) {
            uncompressedDataSize += (val.length() * 16); //Data output becomes uncompressed, which is 16-bits times the length of the codeword in data
            compressedDataSize += W; //Data read in from the file is W bits

            BinaryStdOut.write(val);

            //If the current codeword value (i) is outside the range of the current number of available codebook slots
            if(i >= L){
                if(W < MAX_BIT_WIDTH){ //There is still room to expand the codebook
                    L = (int) Math.pow(2, ++W);
                    oldCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
                } else if(W == MAX_BIT_WIDTH){ //The codebook is absolutely full
                    if(Mode == 'r'){
                        //Reset the codebook
                        st = initializeSymbolArray();
                        W = MIN_BIT_WIDTH;
                        L = (int) Math.pow(2, W);
                        i = R+1;
                    } else if(Mode == 'm'){
                        newCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
                        double ratioOfCompressionRatios = oldCompressionRatio/newCompressionRatio;
                        if(ratioOfCompressionRatios > COMPRESSION_RATIO_THRESHOLD){
                            //Reset the codebook
                            st = initializeSymbolArray();
                            W = MIN_BIT_WIDTH;
                            L = (int) Math.pow(2, W);
                            i = R+1;
                            oldCompressionRatio = 0.0;
                        }
                    } //Else, Mode is 'n', which means "do nothing"
                }
            }

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];

            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L){
                st[i++] = val + s.charAt(0);
                oldCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
            }

            val = s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if(args.length >= 2){ //There is a mode as well as compress/expand option (used below)
            if(args[1].length() == 1 && (args[1].equals("m") || args[1].equals("n") || args[1].equals("r"))){
                Mode = args[1].charAt(0);
            }
        }

        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
