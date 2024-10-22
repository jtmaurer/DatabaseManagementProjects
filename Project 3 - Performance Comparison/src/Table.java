
/** **************************************************************************************
 * @file  Table.java
 *
 * @author   John Miller
 *
 * compile  From the directory Project1- Relational Algebra run the command $ ./compile.sh
 * run      From the directory Project1- Relational Algebra run the command $ ./run.sh
 */
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Boolean.*;
import static java.lang.StringTemplate.STR;
//import static java.lang.StringTemplate.STR;
import static java.lang.System.arraycopy;
import static java.lang.System.out;
import java.lang.classfile.Attributes;
import java.lang.classfile.instruction.ThrowInstruction;
import java.lang.reflect.Array;

/**
 * **************************************************************************************
 * The Table class implements relational database tables (including attribute
 * names, domains and a list of tuples. Five basic relational algebra operators
 * are provided: project, select, union, minus and join. The insert data
 * manipulation operator is also provided. Missing are update and delete data
 * manipulation operators.
 */
public class Table
        implements Serializable {

    /**
     * Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /**
     * Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /**
     * Counter for naming temporary tables.
     */
    private static int count = 0;

    /**
     * Table name.
     */
    private final String name;

    /**
     * Array of attribute names.
     */
    private final String[] attribute;

    /**
     * Array of attribute domains: a domain may be integer types: Long, Integer,
     * Short, Byte real types: Double, Float string types: Character, String
     */
    private final Class[] domain;

    /**
     * Collection of tuples (data storage).
     */
    private final List<Comparable[]> tuples;

    /**
     * Primary key (the attributes forming).
     */
    private final String[] key;

    /**
     * Index into tuples (maps key to tuple).
     */
    private Map<KeyType, Comparable[]> index;

    private final HashSet<List<Comparable>> uniqueKeysSet = new HashSet<List<Comparable>>();

    /**
     * Indexes besides the unique primary key index.
     */
    private ArrayList<Index> alternate_indexes;

    /**
     * The supported map types.
     */
    private enum MapType {
        NO_MAP, TREE_MAP, HASH_MAP, LINHASH_MAP, BPTREE_MAP
    }

    /**
     * The map type to be used for indices. Change as needed.
     */
    private static final MapType mType = MapType.BPTREE_MAP;

    /**
     * **********************************************************************************
     * Make a map (index) given the MapType.
     */
    private static Map<KeyType, Comparable[]> makeMap() {
        return switch (mType) {
            case NO_MAP ->
                null;
            case TREE_MAP ->
                new TreeMap<>();
            case HASH_MAP ->
                new HashMap<>();
            //case LINHASH_MAP -> new LinHashMap <> (KeyType.class, Comparable [].class);
            case BPTREE_MAP ->
                new BpTreeMap<>(KeyType.class, Comparable[].class);
            default ->
                null;
        }; // switch
    } // makeMap

    /**
     * **********************************************************************************
     * Concatenate two arrays of type T to form a new wider array.
     *
     * @param arr1 the first array
     * @param arr2 the second array
     * @return a wider array containing all the values from arr1 and arr2
     * @see
     * http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java
     */
    public static <T> T[] concat(T[] arr1, T[] arr2) {
        T[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    } // concat

    //-----------------------------------------------------------------------------------
    // Constructors
    //-----------------------------------------------------------------------------------
    /**
     * **********************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name the name of the relation
     * @param _attribute the string containing attributes names
     * @param _domain the string containing attribute domains (data types)
     * @param _key the primary key
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = new ArrayList<>();
        index = makeMap();
        out.println(Arrays.toString(domain));
    } // constructor

    /**
     * **********************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples
     * list.
     *
     * @param _name the name of the relation
     * @param _attribute the string containing attributes names
     * @param _domain the string containing attribute domains (data types)
     * @param _key the primary key
     * @param _tuples the list of tuples containing the data
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key,
            List<Comparable[]> _tuples) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = _tuples;
        index = makeMap();
    } // constructor

    /**
     * **********************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param _name the name of the relation
     * @param attributes the string containing attributes names
     * @param domains the string containing attribute domains (data types)
     * @param _key the primary key
     */
    public Table(String _name, String attributes, String domains, String _key) {
        this(_name, attributes.split(" "), findClass(domains.split(" ")), _key.split(" "));

        out.println(STR."DDL> create table \{name} (\{attributes})");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------
    /**
     * **********************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given
     * attributes. Check whether the original key is included in the projection.
     *
     * @param attributes the attributes to project onto
     * @return a table of projected tuples
     * @author Curt Leonard
     * <p>
     * #usage movie.project ("title year studioNo")
     */
    public Table project(String attributes) {
        out.println("RA> " + name + ".project (" + attributes + ")");
        var attrs = attributes.split(" ");
        var colDomain = extractDom(match(attrs), domain);
        var newKey = (Arrays.asList(attrs).containsAll(Arrays.asList(key))) ? key : attrs;

        List<Comparable[]> rows = new ArrayList<>();

        if (mType != MapType.NO_MAP) { // If the table has an index
            for (Map.Entry<KeyType, Comparable[]> entry : index.entrySet()) {
                Comparable[] temp_value = entry.getValue();
                if (temp_value != null) {
                    rows.add(extract(temp_value, attrs));
                }
            }
        } else {
            for (var t : tuples) { //Fallback to original implementation if no index
                rows.add(extract(t, attrs)); // extracts the elements of each row that match the attributes specifies by the user
            }
        }
        return new Table(name + count++, attrs, colDomain, newKey, rows); // returns a new table with the project function applied 
    } // project

    /**
     * **********************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     * <p>
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @param predicate the check condition for tuples
     * @return a table with tuples satisfying the predicate
     */
    public Table select(Predicate<Comparable[]> predicate) {
        out.println(STR."RA> \{name}.select (\{predicate})");

        return new Table(name + count++, attribute, domain, key,
                tuples.stream().filter(t -> predicate.test(t))
                        .collect(Collectors.toList()));
    } // select

    /**
     * **********************************************************************************
     * Select the tuples satisfying the given simple condition on
     * attributes/constants compared using an <op> ==, !=, <, <=, >, >=.
     * <p>
     * #usage movie.select ("year == 1977")
     *
     * @param condition the check condition as a string for tuples
     * @return a table with tuples satisfying the condition
     */
    public Table select(String condition) {
        out.println(STR."RA> \{name}.select (\{condition})");

        List<Comparable[]> rows = new ArrayList<>();

        var token = condition.split(" ");
        var colNo = col(token[0]);

        for (var t : tuples) {
            if (satifies(t, colNo, token[1], token[2])) {
                rows.add(t);
            }
        } // for

        return new Table(name + count++, attribute, domain, key, rows);
    } // select

    /**
     * @author Ridhima Reddy //run tests select
     * **********************************************************************************
     * Does tuple t satify the condition t[colNo] op value where op is ==, !=,
     * <, <=, >, >=?
     * <p>
     * #usage satisfies (t, 1, "<", "1980")
     *
     * @param colNo the attribute's column number
     * @param op the comparison operator
     * @param value the value to compare with (must be converted, String ->
     * domain type)
     * @return whether the condition is satisfied
     */
    private boolean satifies(Comparable[] t, int colNo, String op, String value) {
        var t_A = t[colNo];
        out.println(STR."satisfies: \{t_A} \{op} \{value}");
        var valt = switch (domain[colNo].getSimpleName()) {      // type converted
            case "Byte" ->
                Byte.valueOf(value);
            case "Character" ->
                value.charAt(0);
            case "Double" ->
                Double.valueOf(value);
            case "Float" ->
                Float.valueOf(value);
            case "Integer" ->
                Integer.valueOf(value);
            case "Long" ->
                Long.valueOf(value);
            case "Short" ->
                Short.valueOf(value);
            case "String" ->
                value;
            default ->
                value;
        }; // switch
        var comp = t_A.compareTo(valt);

        return switch (op) {
            case "==" ->
                comp == 0;
            case "!=" ->
                comp != 0;
            case "<" ->
                comp < 0;
            case "<=" ->
                comp <= 0;
            case ">" ->
                comp > 0;
            case ">=" ->
                comp >= 0;
            default ->
                false;
        }; // switch
    } // satifies

    /**
     * **********************************************************************************
     * Select the tuples satisfying the given key predicate (key = value). Use
     * an index (Map) to retrieve the tuple with the given key value. INDEXED
     * SELECT algorithm.
     *
     * @author Curt Leonard
     *
     * @param keyVal the given key value
     * @return a table with the tuple satisfying the key predicate
     */
    public Table select(KeyType keyVal) {
        out.println(STR."RA> \{name}.select (\{keyVal})");

        List<Comparable[]> rows = new ArrayList<>();

        if (mType != MapType.NO_MAP) { // If the table has an index
            var t = index.get(keyVal); // Get the tuple if it exists s.t. key = value
            if (t != null) {
                rows.add(t); // If the tuple exists add it to rows
            }
        } // ? I Think this is everything i need to do but not sure until indexing is done. 
        // ? Because this is just looking at the key value of the index, there shouldnt be a need to loop because the index won't have duplicates.

        return new Table(name + count++, attribute, domain, key, rows);
    } // select

    /**
     * **********************************************************************************
     * Union this table and table2. Check that the two tables are compatible.
     * <p>
     * #usage movie.union (show)
     *
     * @param table2 the rhs table in the union operation
     * @return a table representing the union
     * @author Thomas Nguyen
     * @author Curt Leonard
     */
    public Table union(Table table2) {
        out.println(STR."RA> \{name}.union (\{table2.name})");
        if (!compatible(table2)) {
            return null;
        }

        List<Comparable[]> rows = new ArrayList<>();

        if (table2.mType != MapType.NO_MAP && this.mType != MapType.NO_MAP) { // If both tables have an index

            for (var t : table2.index.values()) { // add all tuples from the second table
                rows.add(t);
            }
            for (var k : this.index.keySet()) { // add all tuples from the first table

                if (!table2.index.containsKey(k)) { // If the tuple doesnt exist in the second table, add it to the rows
                    rows.add(this.index.get(k));
                }
            }
            return new Table(name + count++, attribute, domain, key, rows);
        } else {
            //FALLBACK TO ORIGINAL IMPLEMENTATION

            // loops and adds all tuples in table
            for (int i = 0; i < table2.tuples.size(); i++) {
                rows.add(table2.tuples.get(i));
            }
            // loops and adds all tuples in the original table
            for (int i = 0; i < this.tuples.size(); i++) {
                rows.add(this.tuples.get(i));
            }

            return new Table(name + count++, attribute, domain, key, rows);
        }
    } // union

    /**
     * **********************************************************************************
     * Take the difference of this table and table2. Check that the two tables
     * are compatible.
     * <p>
     * #usage movie.minus (show)
     *
     * @param table2 The rhs table in the minus operation
     * @return a table representing the difference
     * @author Heeya Jolly
     * @author Curt Leonard
     */
    public Table minus(Table table2) {
        out.println(STR."RA> \{name}.minus (\{table2.name})");
        if (!compatible(table2)) {
            return null; // null if not compatible 
        }

        List<Comparable[]> rows = new ArrayList<>();

        if (table2.mType != MapType.NO_MAP) { // If we have an index in table 2 to lookup 
            for (var tup : tuples) {
                if (table2.index.get(new KeyType(extract(tup, key))) == null) { // check to see if it is in table 2 
                    rows.add(tup); // If not in table 2 add it to the new table
                }
            }
            return new Table(name + count++, attribute, domain, key, rows); //Return new table 

        } else { // Fallback to original implementation if no index

            for (var tup : tuples) {
                if (!table2.tuples.contains(tup)) {
                    rows.add(tup);
                }
            }
            return new Table(name + count++, attribute, domain, key, rows);
        }

    } // minus

    /**
     * **********************************************************************************
     * Join this table and table2 by performing an "equi-join". Tuples from both
     * tables are compared requiring attributes1 to equal attributes2.
     * Disambiguate attribute names by appending "2" to the end of any duplicate
     * attribute name. Implement using a NESTED LOOP JOIN ALGORITHM.
     * <p>
     * #usage movie.join ("studioName", "name", studio)
     *
     * @param attributes1 the attributes of this table to be compared (Foreign
     * Key)
     * @param attributes2 the attributes of table2 to be compared (Primary Key)
     * @param table2 the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     * @author Jason Maurer
     */
    public Table join(String attributes1, String attributes2, Table table2) {
        out.println(STR."RA> \{name}.join (\{attributes1}, \{attributes2}, \{table2.name})");

        var t_attrs = attributes1.split(" ");
        var u_attrs = attributes2.split(" ");
        var rows = new ArrayList<Comparable[]>();

        //Get column indexes of each attribute
        var t_attrs_columns = this.match(t_attrs);
        var u_attrs_columns = table2.match(u_attrs);

        //Nested loop, checks both lists for matches, and if matched, stores it in rows variable as concatenated Comparable array
        for (int i = 0; i < this.tuples.size(); i++) {
            Comparable[] tuple_t = this.tuples.get(i);
            for (int j = 0; j < table2.tuples.size(); j++) {
                Comparable[] tuple_u = table2.tuples.get(j);
                boolean matching = true;
                for (int k = 0; k < t_attrs_columns.length; k++) {
                    if (!(tuple_t[t_attrs_columns[k]].compareTo(tuple_u[u_attrs_columns[k]]) == 0)) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    Comparable[] new_tuple = concat(tuple_t, tuple_u);
                    rows.add(new_tuple);
                }
            }
        }

        String[] table_2_modified_attributes = new String[table2.attribute.length];
        System.arraycopy(table2.attribute, 0, table_2_modified_attributes, 0, table2.attribute.length);

        String[] temp_table_1_attributes = this.attribute;

        //Modifies table 2 attributes to append '2' to duplicate-named attribute
        for (int i = 0; i < this.attribute.length; i++) {
            for (int k = 0; k < table_2_modified_attributes.length; k++) {
                if (this.attribute[i].compareTo(table_2_modified_attributes[k]) == 0) {
                    table_2_modified_attributes[k] = table_2_modified_attributes[k] + "2";
                }
            }
        }

        return new Table(name + count++, concat(temp_table_1_attributes, table_2_modified_attributes),
                concat(domain, table2.domain), key, rows);
    } // join

    /**
     * **********************************************************************************
     * Join this table and table2 by performing a "theta-join". Tuples from both
     * tables are compared attribute1 <op> attribute2. Disambiguate attribute
     * names by appending "2" to the end of any duplicate attribute name.
     * Implement using a Nested Loop Join algorithm. Op may include: ==, !=, <, <=,
     * >, >=
     * <p>
     * #usage movie.join ("studioName == name", studio)
     *
     * @param condition the theta join condition
     * @param table2 the rhs table in the join operation
     * @return a table with tuples satisfying the condition
     * @author Jason Maurer
     */
    public Table join(String condition, Table table2) {
        out.println(STR."RA> \{name}.join (\{condition}, \{table2.name})");

        var rows = new ArrayList<Comparable[]>();
        var condition_array = condition.split(" ");

        //Get column number for each attribute
        int table_1_column = this.col(condition_array[0]);
        int table_2_column = table2.col(condition_array[2]);

        //Nested loop, checks both lists for matches, and if matched, stores it in rows variable as concatenated Comparable array
        for (int i = 0; i < this.tuples.size(); i++) {
            Comparable[] tuple_t = this.tuples.get(i);
            for (int j = 0; j < table2.tuples.size(); j++) {
                Comparable[] tuple_u = table2.tuples.get(j);
                String tuple_u_value = tuple_u[table_2_column].toString();
                if (this.satifies(tuple_t, table_1_column, condition_array[1], tuple_u_value)) {
                    Comparable[] new_tuple = concat(tuple_t, tuple_u);
                    rows.add(new_tuple);

                }
            }
        }

        String[] table_2_modified_attributes = new String[table2.attribute.length];
        System.arraycopy(table2.attribute, 0, table_2_modified_attributes, 0, table2.attribute.length);

        //Temporary arrays so concat works
        String[] table_1_temp_attributes = this.attribute;

        //Modifies table 2 attributes to append '2' to duplicate-named attribute
        for (int i = 0; i < this.attribute.length; i++) {
            for (int k = 0; k < table_2_modified_attributes.length; k++) {
                if (this.attribute[i].compareTo(table_2_modified_attributes[k]) == 0) {
                    table_2_modified_attributes[k] = table_2_modified_attributes[k] + "2";
                }
            }
        }

        return new Table(name + count++, concat(table_1_temp_attributes, table_2_modified_attributes),
                concat(domain, table2.domain), key, rows);
    } // join

    /**
     * **********************************************************************************
     * Join this table and table2 by performing an "equi-join". Same as above
     * equi-join, but implemented using an INDEXED JOIN algorithm.
     *
     * @author Curt Leonard
     *
     * @param attributes1 the attributes of this table to be compared (Foreign
     * Key)
     * @param attributes2 the attributes of table2 to be compared (Primary Key)
     * @param table2 the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table i_join(String attributes1, String attributes2, Table table2) {

        out.println(STR."RA> \{name}.i_join (\{attributes1}, \{attributes2}, \{table2.name})");

        var t_attrs = attributes1.split(" ");
        var u_attrs = attributes2.split(" ");
        var rows = new ArrayList<Comparable[]>();

        if (table2.mType != MapType.NO_MAP) { // if table 2 has index

            for (var t : tuples) { // For every tuple in this table
                var keyVal = new KeyType(extract(t, t_attrs)); // get the key value wanted 
                Index table_2_specified_index = table2.create_index(u_attrs, false);
                var u = table_2_specified_index.index_lookup(keyVal); // check if there are matches in table 2
                if (u != null) { // if there are matches
                    rows.add(concat(t, u)); // add concatenated tuples to rows
                }
            }

            String[] tAttrs = this.attribute; // Temp list of all attributes 
            String[] uAttrs = table2.attribute; // Another list of all attributes

            Map<Integer, String> domains = new HashMap<>();
            for (var t : tAttrs) { // For every tuple in table 2
                domains.put(1, t); // put all attributes in a map for easy checking if there are duplicates
            }
            for (int i = 0; i < uAttrs.length; i++) {
                if (!domains.containsValue(uAttrs[i])) { //Because we're changing these values, can't use a foreach loop
                    domains.put(2, uAttrs[i]);
                } else {
                    uAttrs[i] += "2";
                }
            }

            return new Table(name + count++, concat(tAttrs, uAttrs), concat(domain, table2.domain), key, rows); // return the new table with the concatenated tuples

        } else {

            return join(attributes1, attributes2, table2); // fallback to nested loop join if no index
        }

    } // i_join

    /**
     * **********************************************************************************
     * Join this table and table2 by performing an NATURAL JOIN. Tuples from
     * both tables are compared requiring common attributes to be equal. The
     * duplicate column is also eliminated.
     * <p>
     * #usage movieStar.join (starsIn)
     *
     * @param table2 the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     * @author Jason Maurer
     */
    public Table join(Table table2) {
        out.println(STR."RA> \{name}.join (\{table2.name})");

        var rows = new ArrayList<Comparable[]>();

        //Identify common attributes while condensing table 2's attributes so no duplicates in final version
        List<String> commonAttributesList = new ArrayList<>();
        List<String> table_2_condensed_attributes_List = new ArrayList<>();

        //Get common attributes between two tables
        for (int i = 0; i < this.attribute.length; i++) {
            String temp_attr = this.attribute[i];
            for (int j = 0; j < table2.attribute.length; j++) {
                if (temp_attr.compareTo(table2.attribute[j]) == 0) {
                    commonAttributesList.add(temp_attr);
                }
            }
        }

        //Create new attribute list for table 2, that does not include duplicate attributes
        for (int i = 0; i < table2.attribute.length; i++) {
            String tempAttr = table2.attribute[i];
            if (!commonAttributesList.contains(tempAttr) && !table_2_condensed_attributes_List.contains(tempAttr)) {
                table_2_condensed_attributes_List.add(tempAttr);
            }
        }

        //If no overlapping attributes
        if (commonAttributesList.isEmpty()) {
            //Cartesian product loop
            for (int i = 0; i < this.tuples.size(); i++) {
                Comparable[] tuple_t = this.tuples.get(i);
                for (int j = 0; j < table2.tuples.size(); j++) {
                    Comparable[] tuple_u = table2.tuples.get(j);
                    Comparable[] new_tuple = concat(tuple_t, tuple_u);
                    rows.add(new_tuple);
                }
            }

            out.print("No overlapping attributes between table " + this.name + " and table " + table2.name + " - cartesian product returned");
            return new Table(name + count++, concat(this.attribute, table2.attribute),
                    concat(this.domain, table2.domain), key, rows);
        }

        String[] common_attributes = commonAttributesList.toArray(new String[0]);
        String[] table_2_condensed_attributes = table_2_condensed_attributes_List.toArray(new String[0]);

        //Get column indexes of each attribute
        var t_attrs_columns = this.match(common_attributes);
        var u_attrs_columns = table2.match(common_attributes);

        //Nested loop, checks both lists for matches, and if matched, stores it in rows variable as concatenated Comparable array
        for (int i = 0; i < this.tuples.size(); i++) {
            Comparable[] tuple_t = this.tuples.get(i);
            for (int j = 0; j < table2.tuples.size(); j++) {
                Comparable[] tuple_u = table2.tuples.get(j);
                boolean matching = true;
                for (int k = 0; k < t_attrs_columns.length; k++) {
                    if (!(tuple_t[t_attrs_columns[k]].compareTo(tuple_u[u_attrs_columns[k]]) == 0)) {
                        matching = false;
                        break;
                    }
                }
                if (matching) {
                    Comparable[] condensed_tuple_u = extract(tuple_u, table_2_condensed_attributes);
                    Comparable[] new_tuple = concat(tuple_t, condensed_tuple_u);
                    rows.add(new_tuple);
                }
            }
        }

        //Get condensed domains for new attributes
        Class[] table_2_condensed_domains = table2.extractDom(table2.match(table_2_condensed_attributes), table2.domain);

        //Temporary arrays so concat works
        Class[] table_1_temp_domain = this.domain;
        String[] table_1_temp_attributes = this.attribute;

        return new Table(name + count++, concat(table_1_temp_attributes, table_2_condensed_attributes),
                concat(table_1_temp_domain, table_2_condensed_domains), key, rows);
    } // join

/************************************************************************************
     * Return the column position for the given attribute name or -1 if not found.
     *
     * @param attr  the given attribute name
     * @return  a column position
     */
    public int col (String attr)
    {
        for (var i = 0; i < attribute.length; i++) {
           if (attr.equals (attribute [i])) return i;
        } // for

        return -1;       // -1 => not found
    } // col

    /**
     * **********************************************************************************
     * Insert a tuple to the table.
     * <p>
     * #usage movie.insert ("Star_Wars", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup the array of attribute values forming the tuple
     * @return whether insertion was successful
     */
    public boolean insert(Comparable[] tup) {
        out.println(STR."DML> insert into \{name} values (\{Arrays.toString(tup)})");

        if (typeCheck(tup)) {
            var keyVal = new Comparable[key.length];
            var cols = match(key);
            for (var j = 0; j < keyVal.length; j++) {
                keyVal[j] = tup[cols[j]];
            }

            List<Comparable> list = Arrays.asList(keyVal);
            if (this.uniqueKeysSet.contains(list)) {
                throw new IllegalArgumentException(
                        "error: Unable to insert tuple - primary key value already exists. "
                );
            } else {
                this.uniqueKeysSet.add(list);
                tuples.add(tup);
            }

            if (mType != MapType.NO_MAP) {
                index.put(new KeyType(keyVal), tup);
                if (this.alternate_indexes != null) {
                    for (Index ind : this.alternate_indexes) {
                        ind.insertTuple(tup);
                    }
                }
            }
            return true;
        } else {
            return false;
        } // if
    } // insert

/************************************************************************************
     * Get the tuple at index position i.
     *
     * @param i  the index of the tuple being sought
     * @return  the tuple at index position i
     */
    public Comparable [] get (int i)
    {
        return tuples.get (i);
    } // get

    /**
     * **********************************************************************************
     * Get the name of the table.
     *
     * @return the table's name
     */
    public String getName() {
        return name;
    } // getName

/************************************************************************************
     * Print tuple tup.
     * @param tup  the array of attribute values forming the tuple
     */
    public void printTup (Comparable [] tup)
    {
        out.print ("| ");
        for (var attr : tup) out.printf ("%15s", attr);
        out.println (" |");
    } // printTup

    /**
     * **********************************************************************************
     * Print this table.
     */
    public void print() {
        out.println(STR."\n Table \{name}");
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
        out.print("| ");
        for (var a : attribute) {
            out.printf("%15s", a);
        }
        out.println(" |");
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
        for (var tup : tuples) {
            out.print("| ");
            for (var attr : tup) {
                out.printf("%15s", attr);
            }
            out.println(" |");
        } // for
        out.print("|-");
        out.print("---------------".repeat(attribute.length));
        out.println("-|");
    } // print

    /**
     * **********************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex() {
        out.println(STR."\n Index for \{name}");
        out.println("-------------------");
        if (mType != MapType.NO_MAP) {
            for (var e : index.entrySet()) {
                out.println(STR."\{e.getKey()} -> \{Arrays.toString(e.getValue())}");
            } // for
        } // if
        out.println("-------------------");
    } // printIndex

    /**
     * **********************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name the name of the table to load
     */
    public static Table load(String name) {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DIR + name + EXT));
            tab = (Table) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            out.println("load: IO Exception");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            out.println("load: Class Not Found Exception");
            ex.printStackTrace();
        } // try
        return tab;
    } // load

    /**
     * **********************************************************************************
     * Save this table in a file.
     */
    public void save() {
        Map<KeyType, Comparable[]> temp_index = this.index;
        ArrayList<Index> temp_alternate_indexes = this.alternate_indexes;
        try {
            this.index = null;
            this.alternate_indexes = null;

            var oos = new ObjectOutputStream(new FileOutputStream(DIR + name + EXT));
            oos.writeObject(this);
            oos.close();
        } catch (IOException ex) {
            out.println("save: IO Exception");
            ex.printStackTrace();
        } // try
        this.index = temp_index;
        this.alternate_indexes = temp_alternate_indexes;
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------
    /**
     * **********************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e.,
     * have the same number of attributes each with the same corresponding
     * domain.
     *
     * @param table2 the rhs table
     * @return whether the two tables are compatible
     */
    private boolean compatible(Table table2) {
        if (domain.length != table2.domain.length) {
            out.println("compatible ERROR: table have different arity");
            return false;
        } // if
        for (var j = 0; j < domain.length; j++) {
            if (domain[j] != table2.domain[j]) {
                out.println(STR."compatible ERROR: tables disagree on domain \{j}");
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /**
     * **********************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column the array of column names
     * @return an array of column index positions
     */
    private int[] match(String[] column) {
        int[] colPos = new int[column.length];

        for (var j = 0; j < column.length; j++) {
            var matched = false;
            for (var k = 0; k < attribute.length; k++) {
                if (column[j].equals(attribute[k])) {
                    matched = true;
                    colPos[j] = k;
                } // for
            } // for
            if (!matched) {
                out.println(STR."match: domain not found for \{column[j]}");
            }
        } // for

        return colPos;
    } // match

    /**
     * **********************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t the tuple to extract from
     * @param column the array of column names
     * @return a smaller tuple extracted from tuple t
     */
    private Comparable[] extract(Comparable[] t, String[] column) {
        var tup = new Comparable[column.length];
        var colPos = match(column);
        for (var j = 0; j < column.length; j++) {
            tup[j] = t[colPos[j]];
        }
        return tup;
    } // extract

    /**
     * **********************************************************************************
     * Check the size of the tuple (number of elements in array) as well as the
     * type of each value to ensure it is from the right domain.
     *
     * @param t the tuple as a array of attribute values
     * @return whether the tuple has the right size and values that comply with
     * the given domains
     * @author Curt Leonard
     */
    private boolean typeCheck(Comparable[] t) {

        if (t.length != domain.length) { // If the number of values in the tuple is longer or shorter than the domain 
            System.err.println("tuple length is not the same as the domain");
            return false;
        }

        for (int i = 0; i < t.length; i++) { // iterating through the tuple

            if (t[i].getClass() != domain[i]) { // if the class of the element does not match the class that it should be 

                java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());

                logger.log(Level.WARNING, "The class of the element is: " + t[i].getClass() + " \nThe class that it should be is: " + domain[i]);
                //System.err.println("The class of the element is: " + t[i].getClass());
                //System.err.println("The class that it should be is: " + domain[i]);
                return false;

            }
        }

        return true;     // reached only if both the size and the types of the tuple are valid
    } // typeCheck

    /**
     * **********************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className the array of class name (e.g., {"Integer", "String"})
     * @return an array of Java classes
     */
    private static Class[] findClass(String[] className) {
        var classArray = new Class[className.length];

        for (var i = 0; i < className.length; i++) {
            try {
                classArray[i] = Class.forName(STR."java.lang.\{className[i]}");
            } catch (ClassNotFoundException ex) {
                out.println(STR."findClass: \{ex}");
            } // try
        } // for

        return classArray;
    } // findClass

    /**
     * **********************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group where to extract from
     * @return the extracted domains
     */
    private Class[] extractDom(int[] colPos, Class[] group) {
        var obj = new Class[colPos.length];

        for (var j = 0; j < colPos.length; j++) {
            obj[j] = group[colPos[j]];
        } // for

        return obj;
    } // extractDom

    /**
     * **********************************************************************************
     * Return the column position for the given attribute name or -1 if not
     * found.
     *
     * @param attr the given attribute name
     * @return a column position
     */
    public int col(String attr) {
        for (var i = 0; i < attribute.length; i++) {
            if (attr.equals(attribute[i])) {
                return i;
            }
        } // for

        return -1;       // -1 => not found
    } // col

    /**
     * **********************************************************************************
     * Finds the existing index with a key of index_key and a 'Uniqueness'
     * status of _is_Unique.
     * <p>
     * #usage Index customer_id_index = (new String[]{"customer_id"}, true)
     *
     * @param index_key The attributes representing the key for this index
     * @param _is_Unique Whether the index is unique or not
     * @return An Index type which contains the map of the index
     * @author Jason Maurer
     */
    private Index find_index(String[] index_key, Boolean _is_Unique) {
        for (Index alternate_index : this.alternate_indexes) {
            if (Arrays.equals(alternate_index.getIndexKey(), index_key)) {
                if (_is_Unique == alternate_index.isUnique()) {
                    return alternate_index;
                }
            }
        }
        out.println("Couldn't find index with key " + Arrays.toString(index_key) + " and is_Unique: " + _is_Unique);
        return null;
    }

    /**
     * **********************************************************************************
     * Create alternate index to primary key index. Appends to array list
     * holding all initialized indexes not including primary key index.
     *
     * @param index_key attributes making the key for the index
     * @param _is_Unique boolean for if the index is unique
     * @author Jason Maurer
     */
    public Index create_index(String[] index_key, Boolean _is_Unique) {
        if (this.alternate_indexes == null) {
            this.alternate_indexes = new ArrayList<Index>();
        }

        Index new_index = this.find_index(index_key, _is_Unique);

        if (new_index == null) {
            this.drop_index(index_key);
            new_index = new Index(makeMap(), this, _is_Unique, index_key);
            this.alternate_indexes.add(new_index);
        }

        return new_index;
    }

    /**
     * **********************************************************************************
     * Removes the index from the index array list ''.
     *
     * @param index_key attributes of the index to remove
     * @author Jason Maurer
     */
    private void drop_index(String[] index_key) {
        for (Index alternate_index : this.alternate_indexes) {
            if (Arrays.equals(alternate_index.getIndexKey(), index_key)) {
                out.println("Deleted index with key " + Arrays.toString(index_key));
                alternate_indexes.remove(alternate_index);
                return;
            }
        }
    }

    //Utility Methods
    public List<Comparable[]> getTuples() {
        return tuples;
    }

    public void testTable() {
        this.create_index(new String[]{"year", "length"}, true);
        Index n = this.find_index(new String[]{"year", "length"}, true);
        Comparable[] key_test = new Comparable[]{1978, 100};
        Comparable[] key_test2 = new Comparable[]{1985, 200};
        System.out.println(Arrays.toString(n.index_lookup(new KeyType(key_test))));
        System.out.println(Arrays.toString(n.index_lookup(new KeyType(key_test2))));
        //this.drop_index(new String[]{"year", "length"});
        n = null;
        n = this.find_index(new String[]{"year", "length"}, true);
        System.out.println(Arrays.toString(n.index_lookup(new KeyType(key_test))));

    }

} // Table

