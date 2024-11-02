
import java.io.*;
import static java.lang.System.nanoTime;
import static java.lang.System.out;

/**
 * The TimingComparisonScript class tests and compares the performance of select and join operations 
 * on tables using various indexing methods (NO_MAP, TREE_MAP, HASH_MAP, BPTREE_MAP) across tuple 
 * sets of different sizes (10K to 100K). It loads datasets, generates indexed tables, and times 
 * each operation, printing average execution times for performance analysis.
 */
class TimingComparisonScript {

    // "movieExec", "studio", "movie" is order of tables
    static Comparable[][][] ten_k_tuple_set;
    static Comparable[][][] twenty_k_tuple_set;
    static Comparable[][][] thirty_k_tuple_set;
    static Comparable[][][] forty_k_tuple_set;
    static Comparable[][][] fifty_k_tuple_set;
    static Comparable[][][] hundred_k_tuple_set;

    /**
     * Relative path for storage directory
     */
    private static final String DIR = "src" + File.separator + "TupleGeneration" + File.separator + "store" + File.separator;

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
            tups = (Comparable[][][]) ois.readObject();
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

    /**
     * Generates a table with a given name, tuple size, and index type. The
     * tuples are loaded from predefined datasets (e.g., 10k, 20k) and inserted
     * into the table. The table uses a specified map type for indexing (e.g.,
     * NO_MAP, TREE_MAP).
     *
     * @param table_name The name of the table to generate (e.g., "movie",
     * "movieExec", "studio").
     * @param tuple_count The size of the tuple set to load (e.g., "ten_k",
     * "twenty_k", "thirty_k").
     * @param mType The type of map to use for the table index (e.g., NO_MAP,
     * TREE_MAP, HASH_MAP).
     * @return A new instance of the `IndexTestsTable` with tuples loaded and
     * indexed.
     *
     * @throws IllegalArgumentException if the table name is invalid.
     */
    static IndexTestsTable generate_table(String table_name, String tuple_count, IndexTestsTable.MapType mType) {

        IndexTestsTable table;
        Comparable[][][] temp_tups;
        int table_index;

        temp_tups = switch (tuple_count) {
            case "ten_k" ->
                ten_k_tuple_set;
            case "twenty_k" ->
                twenty_k_tuple_set;
            case "thirty_k" ->
                thirty_k_tuple_set;
            case "forty_k" ->
                forty_k_tuple_set;
            case "fifty_k" ->
                fifty_k_tuple_set;
            default ->
                hundred_k_tuple_set;
        };

        switch (table_name) {
            case "movie" -> {
                table = new IndexTestsTable("movie", "title year length genre studioName producerNo",
                        "String Integer Integer String String Integer", "title year", mType);
                table_index = 2;
            }
            case "movieExec" -> {
                table = new IndexTestsTable("movieExec", "producerNo name address fee",
                        "Integer String String Integer", "producerNo", mType);
                table_index = 0;
            }
            default -> {
                table = new IndexTestsTable("studio", "studioName address presNo",
                        "String String Integer", "studioName", mType);
                table_index = 1;
            }
        }

        for (Comparable[] temp_tup : temp_tups[table_index]) {
            table.insert(temp_tup);
        }

        return table;
    }

    /**
     * Tests the performance of select and join operations on three tables. For
     * each operation, the method measures execution time across multiple
     * iterations and prints the average time in microseconds (mu-sec).
     *
     * The operations tested include: - Select by key from the `movieExec` and
     * `studio` tables. - Join between the `movie` and `movieExec` tables on
     * "producerNo". - Join between the `movie` and `studio` tables on
     * "studioName".
     * 
     * The select test keys were found in the tuples data sets beforehand.
     *
     * @param movie_table The `IndexTestsTable` instance representing the
     * "movie" table.
     * @param movieExec_table The `IndexTestsTable` instance representing the
     * "movieExec" table.
     * @param studio The `IndexTestsTable` instance representing the "studio"
     * table.
     */
    static void time_tester(IndexTestsTable movie_table, IndexTestsTable movieExec_table, IndexTestsTable studio) {
        // Select cases
        Comparable select_test_key_one;
        Comparable select_test_key_two;
        int num_tups = movieExec_table.getTupleLength();

        switch (num_tups) {
            case 10000 -> {
                select_test_key_one = 547043; //movieExec - producerNo
                select_test_key_two = "studioName817983"; // studio - studioName
            }
            case 20000 -> {
                select_test_key_one = 803120; //movieExec - producerNo
                select_test_key_two = "studioName919878"; // studio - studioName
            }
            case 30000 -> {
                select_test_key_one = 975011; //movieExec - producerNo
                select_test_key_two = "studioName813852"; // studio - studioName

            }
            case 40000 -> {
                select_test_key_one = 760809; //movieExec - producerNo
                select_test_key_two = "studioName174769"; // studio - studioName

            }
            case 50000 -> {
                select_test_key_one = 578052; //movieExec - producerNo
                select_test_key_two = "studioName251754"; // studio - studioName

            }
            case 100000 -> {
                select_test_key_one = 776436; //movieExec - producerNo
                select_test_key_two = "studioName358324"; // studio - studioName

            }
            default -> {
                select_test_key_one = 0;
                select_test_key_two = "none";
            }
        }

        System.out.println(num_tups + ", " + movie_table.mType + " Timing Test cases");

        var sum = 0;
        for (var it = 0; it < 6; it++) {
            var t0 = nanoTime();
            movieExec_table.select(new KeyType(select_test_key_one));
            var et = (nanoTime() - t0) / 1000;
            if (it > 0) {
                sum += et;
            }
        } // for
        System.out.println("Select case one Average time: " + sum / 5 + " mu-sec");

        sum = 0;
        for (var it = 0; it < 6; it++) {
            var t0 = nanoTime();
            studio.select(new KeyType(select_test_key_two));
            var et = (nanoTime() - t0) / 1000;
            if (it > 0) {
                sum += et;
            }
        } // for
        System.out.println("Select case two Average time: " + sum / 5 + " mu-sec");

        //Join test cases
        //Join movie table with movieExec table
        sum = 0;
        for (var it = 0; it < 6; it++) {
            var t0 = nanoTime();
            movie_table.i_join("producerNo", "producerNo", movieExec_table);
            var et = (nanoTime() - t0) / 1000;
            if (it > 0) {
                sum += et;
            }
        } // for
        System.out.println("Join case one Average time: " + sum / 5 + " mu-sec");

        //Join movie with studio table
        sum = 0;
        for (var it = 0; it < 6; it++) {
            var t0 = nanoTime();
            movie_table.i_join("studioName", "studioName", studio);
            var et = (nanoTime() - t0) / 1000;
            if (it > 0) {
                sum += et;
            }
        } // for
        System.out.println("Join case two Average time: " + sum / 5 + " mu-sec");

    }

    public static void main(String[] args) {
        System.out.println("Starting");
        ten_k_tuple_set = load("ten_k_tuple_sets");
        twenty_k_tuple_set = load("twenty_k_tuple_sets");
        thirty_k_tuple_set = load("thirty_k_tuple_sets");
        forty_k_tuple_set = load("forty_k_tuple_sets");
        fifty_k_tuple_set = load("fifty_k_tuple_sets");
        hundred_k_tuple_set = load("hundred_k_tuple_sets");
        System.out.println("Finished loading");

        // 10K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        var movie = generate_table("movie", "ten_k", IndexTestsTable.MapType.NO_MAP);
        var movieExec = generate_table("movieExec", "ten_k", IndexTestsTable.MapType.NO_MAP);
        var studio = generate_table("studio", "ten_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "ten_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "ten_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "ten_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "ten_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "ten_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "ten_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "ten_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "ten_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "ten_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

        // 20K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        movie = generate_table("movie", "twenty_k", IndexTestsTable.MapType.NO_MAP);
        movieExec = generate_table("movieExec", "twenty_k", IndexTestsTable.MapType.NO_MAP);
        studio = generate_table("studio", "twenty_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "twenty_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "twenty_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "twenty_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "twenty_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "twenty_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "twenty_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "twenty_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "twenty_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "twenty_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

        // 30K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        movie = generate_table("movie", "thirty_k", IndexTestsTable.MapType.NO_MAP);
        movieExec = generate_table("movieExec", "thirty_k", IndexTestsTable.MapType.NO_MAP);
        studio = generate_table("studio", "thirty_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "thirty_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "thirty_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "thirty_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "thirty_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "thirty_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "thirty_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "thirty_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "thirty_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "thirty_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

        // 40K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        movie = generate_table("movie", "forty_k", IndexTestsTable.MapType.NO_MAP);
        movieExec = generate_table("movieExec", "forty_k", IndexTestsTable.MapType.NO_MAP);
        studio = generate_table("studio", "forty_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "forty_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "forty_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "forty_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "forty_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "forty_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "forty_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "forty_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "forty_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "forty_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

        // 50K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        movie = generate_table("movie", "fifty_k", IndexTestsTable.MapType.NO_MAP);
        movieExec = generate_table("movieExec", "fifty_k", IndexTestsTable.MapType.NO_MAP);
        studio = generate_table("studio", "fifty_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "fifty_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "fifty_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "fifty_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "fifty_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "fifty_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "fifty_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "fifty_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "fifty_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "fifty_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

        // 100K Tuple Test Cases ********************************************************************************************************************
        // NO_MAP 
        movie = generate_table("movie", "hundred_k", IndexTestsTable.MapType.NO_MAP);
        movieExec = generate_table("movieExec", "hundred_k", IndexTestsTable.MapType.NO_MAP);
        studio = generate_table("studio", "hundred_k", IndexTestsTable.MapType.NO_MAP);
        time_tester(movie, movieExec, studio);
        // TREE_MAP        
        movie = generate_table("movie", "hundred_k", IndexTestsTable.MapType.TREE_MAP);
        movieExec = generate_table("movieExec", "hundred_k", IndexTestsTable.MapType.TREE_MAP);
        studio = generate_table("studio", "hundred_k", IndexTestsTable.MapType.TREE_MAP);
        time_tester(movie, movieExec, studio);
        // HASH_MAP        
        movie = generate_table("movie", "hundred_k", IndexTestsTable.MapType.HASH_MAP);
        movieExec = generate_table("movieExec", "hundred_k", IndexTestsTable.MapType.HASH_MAP);
        studio = generate_table("studio", "hundred_k", IndexTestsTable.MapType.HASH_MAP);
        time_tester(movie, movieExec, studio);
        // BPTREE_MAP        
        movie = generate_table("movie", "hundred_k", IndexTestsTable.MapType.BPTREE_MAP);
        movieExec = generate_table("movieExec", "hundred_k", IndexTestsTable.MapType.BPTREE_MAP);
        studio = generate_table("studio", "hundred_k", IndexTestsTable.MapType.BPTREE_MAP);
        time_tester(movie, movieExec, studio);

    }
}
