import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Index<KeyType> {

    private enum MapType {
        NO_MAP, TREE_MAP, HASH_MAP, LINHASH_MAP, BPTREE_MAP
    }

    private final Map<KeyType, Comparable[]> map;  // The map object
    private final boolean isUnique;  // The boolean flag
    private final String[] index_key;  // The string array
    private final int[] key_columns;
    private HashSet<List<Comparable>> uniqueKeysSet;
    private final Table table;

    // Constructor
    public Index(Map<KeyType, Comparable[]> new_map, Table table, boolean isUnique, String[] index_key) {
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
        this.map.put((KeyType) key_values, tuple);
    }

    private void populateMap() {
        if (!this.table.getTuples().isEmpty()) {
            this.map.clear();
            var allRows = this.table.getTuples();
            for(Comparable[] row : allRows){
                this.insertTuple(row);
            }
        }
    }

    // Getters and setters
    public Map<KeyType, Comparable[]> getMap() {
        return map;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public String[] getIndexKey() {
        return index_key;
    }
}
