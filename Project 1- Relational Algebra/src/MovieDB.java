
/*****************************************************************************************
 * @file  MovieDB.java
 *
 * @author   John Miller
 */

import static java.lang.System.out;

/*****************************************************************************************
 * The MovieDB class makes a Movie Database.  It serves as a template for making other
 * databases.  See "Database Systems: The Complete Book", second edition, page 26 for more
 * information on the Movie Database schema.
 */
class MovieDB
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
        var film5 = new Comparable [] { "Star_Wars", 1977, 124, "sciFi", "Fox", 12345 };

        out.println ();
        movie.insert (film0);
        movie.insert (film1);
        movie.insert (film2);
        movie.insert (film3);
        movie.insert (film3);
        movie.insert(film5);
        movie.print ();

        var film4 = new Comparable [] { "Galaxy_Quest", 1999, 104, "comedy", "DreamWorks", 67890 };
        out.println ();
        cinema.insert (film2);
        cinema.insert (film3);
        cinema.insert (film4);
        cinema.print ();

        var star0 = new Comparable [] { "Carrie_Fisher", "Hollywood", 'F', "9/9/99" }; 
        var star1 = new Comparable [] { "Mark_Hamill", "Brentwood", 'M', "8/8/88" };
        var star2 = new Comparable [] { "Harrison_Ford", "Beverly_Hills", 'M', "7/7/77" };
        var star3 = new Comparable [] { "Carrie_Fisher", "Hollywood", 'M', "9/9/99" }; // TESTING FOR DUPLICATE FUNCTIONALITY
        out.println ();
        movieStar.insert (star0);
        movieStar.insert (star1);
        movieStar.insert (star2);
        movieStar.insert (star3);
        movieStar.print ();

        var cast0 = new Comparable [] { "Star_Wars", 1977, "Carrie_Fisher" };
        out.println ();
        starsIn.insert (cast0);
        starsIn.print ();

        var exec0 = new Comparable [] { 9999, "S_Spielberg", "Hollywood", 10000.00 };
        out.println ();
        //This insert fails
        movieExec.insert (exec0);
        movieExec.print ();

        var studio0 = new Comparable [] { "Fox", "Los_Angeles", 7777 };
        var studio1 = new Comparable [] { "Universal", "Universal_City", 8888 };
        var studio2 = new Comparable [] { "DreamWorks", "Universal_City", 9999 };
        var studio3 = new Comparable [] { "DreamWorks", "Universal_City", 9999 };
        out.println ();
        studio.insert (studio0);
        studio.insert (studio1);
        studio.insert (studio2);
        studio.insert (studio3);
        studio.print ();

        movie.save ();
        cinema.save ();
        movieStar.save ();
        starsIn.save ();
        movieExec.save ();
        studio.save ();

        movieStar.printIndex ();

        //--------------------- project: title year

        out.println ();
        var t_project1 = movie.project ("title year");
        t_project1.print ();

         //--------------------- project: producerNo year

         out.println ();
         var t_project2 = cinema.project ("producerNo year");
         t_project2.print ();

          //--------------------- project: name birthdate

        out.println ();
        var t_project3 = movieStar.project ("name birthdate");
        t_project3.print ();


         //--------------------- project: starName

         out.println ();
         var t_project4 = starsIn.project ("starName");
         t_project4.print ();


          //--------------------- project: presNo name

        out.println ();
        var t_project5 = studio.project ("presNo name");
        t_project5.print ();

        //--------------------- select: equals, &&

        out.println ();
        var t_select = movie.select (t -> t[movie.col("title")].equals ("Star_Wars") &&
                                          t[movie.col("year")].equals (1977));
        t_select.print ();

        //--------------------- select: <

        out.println ();
        var t_select2 = movie.select (t -> (Integer) t[movie.col("year")] < 1980);
        t_select2.print ();

        //--------------------- select (condition): <

        out.println ();
        var t_select3 = movie.select ("year < 1980");
        t_select3.print ();

        //--------------------- select (condition): ==
        out.println ();
        var t_select4 = movie.select ("year == 1977");
        t_select4.print ();

        //--------------------- select (condition): <=
        out.println ();
        var t_select5 = movie.select ("length <= 124");
        t_select5.print ();

        //--------------------- select (condition): >
        out.println ();
        var t_select6 = movie.select ("length > 124");
        t_select6.print ();

        //--------------------- indexed select: key

        out.println ();
        var t_iselect = movieStar.select (new KeyType ("Harrison_Ford"));
        t_iselect.print ();

        //--------------------- union: movie UNION cinema

        out.println ();
        var t_union = movie.union (cinema);
        t_union.print ();

        //--------------------- minus: movie MINUS cinema

        out.println ();
        var t_minus = movie.minus (cinema);
        t_minus.print ();

        //--------------------- equi-join: movie JOIN studio ON studioName = name

        out.println ();
        var t_join = movie.join ("studioName", "name", studio);
        t_join.print ();

        //--------------------- natural join: movie JOIN studio
        out.println ();
        starsIn.print();
        out.println ();
        var t_join2 = movie.join (starsIn);
        t_join2.print ();

        //--------------------- index-join: movie JOIN studio ON studioName = name

        out.println ();
        var i_join = movie.i_join ("studioName", "name", studio);
        i_join.print ();

    } // main

} // MovieDB

