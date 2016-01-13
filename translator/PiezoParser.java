import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

class PiezoParser {
    
    // frequencies in Hz for C-G,A,B in octave 0 
    static double[] pitches = { // as there are no B#/Cb or E#/Fb (...right?), these indices are set to 0
        27.50,29.14,30.87,0,16.35,17.32,18.35,
        19.35,20.60,0,21.83,23.12,25.96
    };
    
    static final int DUR_OFFSET = 1;
    static final int PAUSE_OFFSET = 2;
    
    static List<String> tone = new ArrayList<String>();
    static List<String> duration = new ArrayList<String>();
    static List<String> pause = new ArrayList<String>();
    static List<String> loop_start = new ArrayList<String>();
    static List<String> loop_end = new ArrayList<String>();
    static List<String> loop_count = new ArrayList<String>();
    
    static int loopIndex;
    static int loopCount;
    

    public static void main(String[] args) throws Exception{
        System.out.println(args[0]);
        
        parse(args[0].replaceAll("[\\s]+"," ").trim().split(" "));
        
        File f = new File("piezoOutput.txt");
        if (!f.exists()) {
            f.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("Copy and paste the following values into the arduino source code\n\n");
		
        bw.write("int length = "+tone.size()+";\n");
        bw.write("int loop_count = "+loop_start.size()+";\n");
        bw.write("int pitch[] = {"+tone.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int duration[] = {"+duration.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int pause[] = {"+pause.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int loop_begin[] = {"+loop_start.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int loop_end[] = {"+loop_end.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int loop_repeat[] = {"+loop_count.toString().replaceAll("[\\[\\]]","")+"};\n");
        bw.write("int repeats[] = {"+loop_count.toString().replaceAll("[\\[\\]]","")+"};");
        bw.close();
    }
    
    static void parse(String[] tokens) {
        int index = 0;
        int open_loops = 0;
        for (int i = 0; i < tokens.length; i+=3) {
            while (i < tokens.length && (
                tokens[i].matches("LOOP_THIS:[\\d]+") ||
                tokens[i].matches("END_LOOP"))) {
                if (tokens[i].matches("LOOP_THIS:[\\d]+")) {
                    loop_start.add(
                      loop_start.size()-open_loops,""+index
                    );
                    loop_count.add(
                      loop_count.size()-open_loops,
                      ""+tokens[i].replaceAll("[^\\d]","")
                    );
                    open_loops++;
                    i++;
                } else if (tokens[i].matches("END_LOOP")) {
                    loop_end.add(""+(index-1));
                    open_loops--;
                    i++;
                    if (i >= tokens.length) {
                        return;
                    }
                }
            }
            if (tokens[i].matches("\\d+")) {
                tone.add(tokens[i]);
            } else {
                tone.add(""+process(tokens[i].toUpperCase()));
            }
            duration.add(tokens[i+DUR_OFFSET]);
            pause.add(tokens[i+PAUSE_OFFSET]);
            index++;
        }
    }
    
    static long process(String str) {
        str=str.replaceAll("H","B"); // if this standard is used, replace H with B
        double pitch = 0;
        if (str.matches("[\\d]+\\.[\\d]{2}")) {
            
            pitch = ((double)Integer.parseInt(str.replace("\\.","")))/100;
        } else if (str.matches("[A-G].*")) {
            int octave = Integer.parseInt(str.replaceAll("[^\\d]",""));
            int index = (str.charAt(0)-'A')*2;
            if (str.charAt(1) == 'B') { // if minor/moll
                index--;
                if (index < 0) {
                    index=pitches.length-1;
                }
            } else if (str.charAt(1) =='#') { // if major/dur
                index++;
                if (index >= pitches.length) {
                    index = 0;
                }
            }
            pitch = pitches[index]*Math.pow(2,octave);
        }
        return Math.round(pitch);
    }
    
    
}