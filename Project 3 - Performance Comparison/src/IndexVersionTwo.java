
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * **********************************************************************************
 * Copy of Index, but modified to account for convenient changes for testing.
 *
 * Contains method insertTuple and index_lookup to interact with the map.
 *
 * @author Heeya Jolly and Jason Maurer
 */
public class IndexVersionTwo {

    private final Map<KeyType, Comparable[]> map;  // The map object
    private final boolean isUnique;  // The boolean flag
    private final String[] index_key;  // The string array
    private final int[] key_columns;
    private HashSet<List<Comparable>> uniqueKeysSet;
    private final IndexTestsTable table;

    /**
     * **********************************************************************************
     * Construct an index, given parameters isUnique, index_key, and the empty new_map.
     * 
     * Parameter table represents the table the index will get key column numbers, and
     * get tuples from (if needed).
     * 
     * In the case of the index being unique, a hashset is created to check for duplicate
     * keys.
     * 
     * PopulateMap() is called in case the table already has tuples that must be indexed. 
     *
     * @param new_map the empty map passed in
     * @param table the table the index is being made for
     * @param isUnique the boolean for if the index is unique
     * @param index_key the attributes of the key for the index
     */
    public IndexVersionTwo(Map<KeyType, Comparable[]> new_map, IndexTestsTable table, boolean isUnique, String[] index_key) {
        if (isUnique) {
            this.uniqueKeysSet = new HashSet<List<Comparable>>();
        }
        this.key_columns = new int[index_key.length];
        for (int i = 0; i < index_key.length; i++) {
            this.key_columns[i] = table.col(index_key[i]);
            if (this.key_columns[i] == -1) {
                throw new IllegalArgumentException(
                        "error: index_key attributes not found in table."
                );
            }
        }
        this.isUnique = isUnique;
        this.index_key = index_key;
        this.table = table;
        this.map = new_map;
        populateMap();
    }

    /**
     * **********************************************************************************
     * Inserts tuple into index, checking if the key value is repeated if the index is 
     * unique. 
     *
     * @param tuple the tuple to insert
     */
    public void insertTuple(Comparable[] tuple) {
        Comparable[] key_values = new Comparable[this.key_columns.length];
        for (int i = 0; i < this.key_columns.length; i++) {
            key_values[i] = tuple[this.key_columns[i]];
        }

        if (this.isUnique) {
            List<Comparable> list = Arrays.asList(key_values);
            if (this.uniqueKeysSet.contains(list)) {
                throw new IllegalArgumentException(
                        "error: Unable to insert tuple - already exists in unique index. "
                );
            } else {
                this.uniqueKeysSet.add(list);
            }
        }
        this.map.put(new KeyType(key_values), tuple);
    }

    /**
     * **********************************************************************************
     * Inserts existing tuples from table into index. 
     */
    private void populateMap() {
        if (!this.table.getTuples().isEmpty()) {
            if (this.map != null && !this.map.isEmpty()) {
                this.map.clear();
            }
            var allRows = this.table.getTuples();
            for (Comparable[] row : allRows) {
                this.insertTuple(row);
            }
        }
    }

    //Getter Methods -----------------------------------------------------------------------------------------
    public Comparable[] index_lookup(KeyType key_value) {
        return this.map.get(key_value);
    }

    public Map<KeyType, Comparable[]> getMap() {
        return map;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public String[] getIndexKey() {
        return index_key;
    }

    /**
     * **********************************************************************************
     * BpTreeIndex specific method for if there's a table thats saved.
     */
    public static BpTreeMap<Integer, Comparable[]> createIndex(String tableName, String columnName) {

        Table tab = Table.load(tableName); // loading the table

        // error checking for input variables
        if (tab == null) {
            throw new IllegalArgumentException(
                    "error: table name '" + tableName + "' not found or cannot be loaded."
            );
        }

        int colIndex = tab.col(columnName);
        if (colIndex < 0) {
            throw new IllegalArgumentException(
                    "error: column name " + columnName + " does not exist in table " + tableName
            );
        }

        var allRows = tab.getTuples();
        int rowCount = allRows.size();
        var bpTree = new BpTreeMap<Integer, Comparable[]>(Integer.class, Comparable[].class);
        for (var i = 0; i < rowCount; i++) {
            bpTree.put((Integer) (allRows.get(i))[colIndex], allRows.get(i));
        }

        return bpTree;
    }
}
