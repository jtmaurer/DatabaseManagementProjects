
import static java.lang.System.out;

/**
 * ***************************************************************************************
 *
 */
class IndexTests {

    /**
     * ***********************************************************************************
     * Main method for creating, populating and querying a Movie Database.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        out.println();

        var movie = new IndexTestsTable("movie", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year", IndexTestsTable.MapType.HASH_MAP);

        var cinema = new IndexTestsTable("cinema", "title year length genre studioName producerNo",
                "String Integer Integer String String Integer", "title year", IndexTestsTable.MapType.HASH_MAP);

        var studio = new IndexTestsTable("studio", "name address presNo",
                "String String Integer", "name", IndexTestsTable.MapType.HASH_MAP);

        var film0 = new Comparable[]{"Star_Wars", 1977, 124, "sciFi", "Fox", 12345};
        var film1 = new Comparable[]{"Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345};
        var film2 = new Comparable[]{"Rocky", 1985, 200, "action", "Universal", 12125};
        var film3 = new Comparable[]{"Rambo", 1978, 100, "action", "Universal", 32355};
        var film4 = new Comparable[]{"Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890};

        out.println();
        cinema.insert(film2);
        cinema.insert(film3);
        cinema.insert(film4);
        cinema.insert(film4);
        out.println();
        movie.insert(film0);
        movie.insert(film1);
        movie.insert(film2);
        movie.insert(film3);
        movie.print();
        // movie.testTable();
        out.println();
        var studio0 = new Comparable [] { "Fox", "Los_Angeles", 7777 };
        var studio1 = new Comparable [] { "Universal", "Universal_City", 8888 };
        var studio2 = new Comparable [] { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
        studio.print ();

        out.println("**************************");
        var t_project1 = movie.project("title year");
        t_project1.print();

        out.println();
        var t_union = movie.union(cinema);
        t_union.print();

        out.println();
        var t_minus = movie.minus(cinema);
        t_minus.print();

        out.println();
        var i_join = movie.i_join("studioName", "name", studio);
        i_join.print();

        // Index ind = movie.create_index(new String[]{"year"}, true);
        // Comparable[] key_test = new Comparable[]{1978};
        // Comparable[] key_test2 = new Comparable[]{1985};
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test))));    
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test2))));        
        // movie.insert (film3);
        // System.out.println(Arrays.toString(ind.index_lookup(new KeyType(key_test))));        
    }
}
