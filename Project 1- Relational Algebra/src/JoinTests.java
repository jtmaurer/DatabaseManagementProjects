
/*****************************************************************************************
 * @file  JoinTests.java
 *
 * @author   Jason MAurer
 */

import static java.lang.System.out;

/*****************************************************************************************
 * 
 */
class JoinTests
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

        var cinema = new Table ("cinema", "title year length genre studioName producerNo",
                                          "String Integer Integer String String Integer", "title year");

        var movieStar = new Table ("movieStar", "name address gender birthdate",
                                                "String String Character String", "name");

        var starsIn = new Table ("starsIn", "movieTitle movieYear starName",
                                            "String Integer String", "movieTitle movieYear starName");

        var movieExec = new Table ("movieExec", "certNo name address fee",
                                                "Integer String String Float", "certNo");

        var studio = new Table ("studio", "name address presNo",
                                          "String String Integer", "name");

        var film0 = new Comparable [] { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };
        var film1 = new Comparable [] { "Star_Wars_2", 1980, 124, "sciFi", "Fox", 12345 };
        var film2 = new Comparable [] { "Rocky", 1985, 200, "action", "Universal", 12125 };
        var film3 = new Comparable [] { "Rambo", 1978, 100, "action", "Universal", 32355 };

        out.println ();
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        // movie.print ();

        var film4 = new Comparable [] { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        out.println ();
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        // cinema.print ();

        var star0 = new Comparable [] { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" };
        var star1 = new Comparable [] { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
        var star2 = new Comparable [] { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
        out.println ();
        movieStar.insert (star0);
        movieStar.insert (star1);
        movieStar.insert (star2);
        // movieStar.print ();

        var cast0 = new Comparable [] { "Star_Wars", 1977, "Carrie_Fisher" };
        out.println ();
        starsIn.insert (cast0);
        // starsIn.print ();

        var exec0 = new Comparable [] { 9999, "S_Spielberg", "Hollywood", 10000.00 };
        out.println ();
        movieExec.insert (exec0);
        // movieExec.print ();

        var studio0 = new Comparable [] { "Fox", "Los_Angeles", 7777 };
        var studio1 = new Comparable [] { "Universal", "Universal_City", 8888 };
        var studio2 = new Comparable [] { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
        // studio.print ();

        //--------------------- equi-join: movie JOIN studio ON studioName = name

        out.println ();
        var t_join = movie.join ("studioName", "name", studio);
        t_join.print ();

        //--------------------- equi-join: movie JOIN studio ON studioName = name
        
        out.println ();
        movie.print();
        cinema.print();
        var t_join5 = movie.join("title year", "title year", cinema);
        t_join5.print ();

        //--------------------- natural join: movie JOIN studio - removes duplicate columns

        out.println ();
        movie.print();
        cinema.print();
        var t_join2 = movie.join (cinema);
        t_join2.print ();

        //--------------------- theta join: movie JOIN studio

        out.println ();
        movie.print();
        studio.print();
        var t_join3 = movie.join("studioName != name", studio);
        t_join3.print ();

        
        //--------------------- theta join: movie JOIN cinema 

        out.println ();
        movie.print();
        cinema.print();
        var t_join4 = movie.join("year > year", cinema);
        t_join4.print ();



    } // main

} // MovieDB

