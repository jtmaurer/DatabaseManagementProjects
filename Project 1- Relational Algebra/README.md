

# Project 2 - Indexing

This project's goals were to speed up the implementation of the following Relational Algebra Operators, implement BpTreeMap, create both Unique and Non-Unique Indices, and use a creation method. 

## Authors
- Ridhima Reddy
- Curt Leonard
- Heeya Jolly
- Jason Todd Maurer
- Thomas Nguyen

# Objective
- Speed up the impementation of the Relational Algerbra Operators. implement BpTreeMap, create unique and non-unique indices, and use a creation method.


# Key Features

1. Relational Algebra Operators:
   - Implementation and optimization of five core relational algebra operators: **Select**, **Project**, **Union**, **Minus**, and **Join**.
   - Focus on improving the efficiency of these operators using indexing techniques.

2. Indexing Techniques:
   - Use of **BpTreeMap** and **B+Tree Index** to speed up query operations.
   - Index structures help reduce the need for full table scans, enhancing performance for data retrieval and manipulation.

3. Indexed Select and Join:
   - Indexed Select allows faster retrieval of tuples based on conditions using the index.
   - Indexed Joinn improves the performance of matching tuples between two datasets.

4. Unique and Non-Unique Indexes:
   - Creation of both **Unique** and **Non-Unique** indices.
   - Default behavior creates a unique index for primary keys, while non-unique indices are used for other columns.

5. Index Management:
   - Methods like **create_index**, **create_unique_index**, and **drop_index** allow easy management of indices.
   - These methods allow flexibility in creating and deleting indices as needed for performance optimization.

6. Performance Focus:
   - Emphasis on testing and benchmarking operations with and without indexing to measure performance improvements.
   - Enhanced query speed for **Select**, **Project**, **Union**, **Minus**, and **Join** operations.



# Key Goals
- Improve performance** of relational algebra operators using indexing techniques like B+ Tree and BpTreeMap.  
- Deepen understanding** of relational algebra operators and their role in databases.  
- Ensure accurate data manipulation** while optimizing speed and efficiency.  
- Learn to create and manage indexes** to enhance query performance.

# Significance
- Provides hands on experience with essential database operations and indexing.  
- Builds a foundation for learning more advanced database management concepts like query optimization.

# Prerequisites
- Before you begin, ensure you have me the following requirements:
- You have installed that latest version of Java
- You have a compatible IDE for Java development and testing
- BASH scripts to console

# Technologies Used
- Java 20
- VS Code
- Github
- Git Version Control

# Execution
1. to compile:
 #!/bin/bash

# Remove existing .class files in the bin folder
rm -f bin/*.class

2. to run:
#!/bin/bash

java -cp  bin --enable-preview MovieDB

# Compile all Java files in the src folder
javac -d bin --enable-preview --release 22 src/*.java

# Contributions
-Ridhima(Manager): Tested and worked on Relational algebra functions and helped enhance rest of the indexes. I also worked on compiling and running scripts and wrote the documentation and readme file for the project.
- Curt Leonard: Worked on speeding up the implementation of the following Relational Algebra Operators: Select, Project, Union, Minus, and Join.
- Heeya Jolly: Created the create_index method designed to create non-unique indexes on a column in a table. This method accepts table name and column name and constructs an index using B+ Tree on the specified column. This method can be used to improve search and lookup operations.
- Jason Todd Maurer: For Class Index: Implemented Constructor to specify values outside of the map that are specific to the index, InsertTuple() which inserts the tuple into the index, while checking that the insert is valid, PopulateMap() which populates an empty index with the tuples that already exist in the table.  For Class Table: Implemented delete_index, which deletes indexes, also implemented an ArrayList, which holds any created indexes, outside of the primary key index. 
- Thomas Nguyen: Worked on the BpTree Map and implemented the Addi Method, which adds a node to the tree and handles internal node overflows by invoking a split function. Also, added the Insert which checks whether the node is null. If the node is a leaf, it is added in the usual manner. However, if the node is internal, the method recursively descends to ensure that the leaf nodes are correctly added to existing nodes. 


# Documentation
For a detailed explanation of our project, key features,implementations, indexes, and performance testing, and conculsion please refer to the documentation file named 'Documentation.pdf'.



