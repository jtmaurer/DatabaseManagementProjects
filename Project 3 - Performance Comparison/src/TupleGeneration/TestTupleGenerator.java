 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.out;

/*****************************************************************************************
 * This class tests the TupleGenerator on the Student Registration Database defined in the
 * Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).  The primary keys
 * (see figure 3.6) and foreign keys (see example 3.2.2) are as given in the textbook.
 */
public class TestTupleGenerator
{

    
    /**
     * Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /**
     * Filename extension for database files
     */
    private static final String EXT = ".dbf";
    
    /*************************************************************************************
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        var test = new TupleGeneratorImpl ();
        
        test.addRelSchema ("movieExec", "producerNo name address fee",
                    "Integer String String Integer", "producerNo", null);
        
        test.addRelSchema ("studio", "studioName address presNo", 
                        "String String Integer", "studioName", null);

        test.addRelSchema ("movie", "title year length genre studioName producerNo",
                    "String Integer Integer String String Integer", "title year",  
                    new String [][] {{ "producerNo", "movieExec", "producerNo" },
                                            { "studioName", "studio", "studioName" }});

        var tables = new String [] {  "movieExec", "studio", "movie"};

        var tups   = new int [] { 100000, 100000, 100000 };
    
        var resultTest = test.generate (tups);

        save(resultTest, "hundred_k_tuple_sets");

        var loaded_tups = load("hundred_k_tuple_sets");
        
        for (var i = 0; i < resultTest.length; i++) {
            out.println (tables [i]);
            // for (var j = 0; j < resultTest [i].length; j++) {
            for (var j = 0; j < 5; j++) {
                for (var k = 0; k < resultTest [i][j].length; k++) {
                    out.print (resultTest [i][j][k] + ",");
                } // for
                out.println ();
                for (var k = 0; k < loaded_tups [i][j].length; k++) {
                    out.print (loaded_tups [i][j][k] + ",");
                }
                out.println ();
            } // for
            out.println ();
        } // for
    } // main

     /**
     * **********************************************************************************
     * Save tuples for two tables to a file
     */
    public static void save(Comparable[][][] tups, String file_name) {
        try {
            var oos = new ObjectOutputStream(new FileOutputStream(DIR + file_name + EXT));
            oos.writeObject(tups);
            oos.close();
        } catch (IOException ex) {
            out.println("save: IO Exception");
            ex.printStackTrace();
        } // try
    } // save

    /**
     * **********************************************************************************
     * Load the two sets of tups from memory
     *
     * @param name the name of the tup_file to load
     */
    public static Comparable[][][] load(String name) {
        Comparable[][][] tups = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DIR + name + EXT));
            tups = (Comparable [][][]) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            out.println("load: IO Exception");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            out.println("load: Class Not Found Exception");
            ex.printStackTrace();
        } // try
        return tups;
    } // load

} // TestTupleGenerator

