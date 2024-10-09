import static java.lang.System.out;

/*****************************************************************************************
 * 
 */
class IndexTests
{
    /*************************************************************************************
     * Main method for creating, populating and querying a Movie Database.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        out.println ();

        var movie = new Table ("movie", "title year length genre studioName producerNo",
                                        "String Integer Integer String String Integer", "title year");

        

        var film0 = new Comparable [] { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        var film1 = new Comparable [] { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        var film2 = new Comparable [] { "Rocky", 1985, 200, "action", "Universal", 12125 };
        var film3 = new Comparable [] { "Rambo", 1978, 100, "action", "Universal", 32355 };

        out.println ();
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        movie.print ();
        movie.testTable();
        
        // Index ind = movie.create_index(new String[]{"year"}, true);
        // Comparable[] key_test = new Comparable[]{1978};
        // Comparable[] key_test2 = new Comparable[]{1985};
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test))));    
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test2))));        
        // movie.insert (film3);
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test))));        
    }
}