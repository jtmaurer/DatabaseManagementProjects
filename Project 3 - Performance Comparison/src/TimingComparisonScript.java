import java.io.*;
import static java.lang.System.out;

class TimingComparisonScript{

    Comparable[][][] ten_k_tuple_set;
    Comparable[][][] twenty_k_tuple_set;    
    Comparable[][][] thrity_k_tuple_set;
    Comparable[][][] forty_k_tuple_set;
    Comparable[][][] fifty_k_tuple_set;

    /**
     * Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /**
     * Filename extension for database files
     */
    private static final String EXT = ".dbf";

    private enum MapType {
        NO_MAP, TREE_MAP, HASH_MAP, LINHASH_MAP, BPTREE_MAP
    }

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

    IndexTestsTable generate_table(String table_name, String tuple_count, IndexTestsTable.MapType mType, Comparable[][] tuples){
        
        return table;
    }

    void time_tester(boolean is_select, IndexTestsTable table){

    }

    public static void main(String [] args){



        // 10K Tuple Test Cases ********************************************************************************************************************

        // NO_MAP 
        var movie = new IndexTestsTable ("movie", "title year length genre studioName producerNo",
                                        "String Integer Integer String String Integer", "title year", IndexTestsTable.MapType.NO_MAP);
        
        
        var movieExec = new IndexTestsTable ("movieExec", "producerNo name address fee",
                                                "Integer String String Float", "producerNo", IndexTestsTable.MapType.NO_MAP);

        var studio = new IndexTestsTable ("studio", "studioName address presNo",
                                          "String String Integer", "studioName", IndexTestsTable.MapType.NO_MAP);

        // Create index for two attributes to be found with select in movie.

        // Dont need to create indexes for movieexec and studio.  They already have indexes
        
        // Insert predefined 10K Tuples for 3 tables from .dbf file

        // Create test cases - select two attributes 
    }
}