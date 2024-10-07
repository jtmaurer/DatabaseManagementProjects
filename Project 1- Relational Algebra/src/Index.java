public class Index {


    //    public static void  main() {
    //        var indexMap = createIndex("movie", "year");
    //    }
    
        public static BpTreeMap <Integer, Comparable[]> createIndex(String tableName, String columnName) {
    
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
            var bpTree = new BpTreeMap <Integer, Comparable[]> (Integer.class, Comparable[].class);
            for (var i = 0; i < rowCount; i++)
                bpTree.put ((Integer) (allRows.get(i))[colIndex], allRows.get(i));
    
            return bpTree;
        }
    }