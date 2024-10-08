

# Project 2 - Indexing

This project's goals were to replicate the 5 basic relational algebra operators in Java using the Table class provided to us. 

## Authors
- Ridhima Reddy
- Curt Leonard
- Heeya Jolly
- Jason Todd Maurer
- Thomas Nguyen

#Objective
- Speed up the impementation of the Relational Algerbra Operators. implement BpTreeMap, create unique and non-unique indices, and use a creation method.

# Key Features

- Select: Retrieves tuples based on specified conditions
- Project: Selects specific attributes from the tuples
- Union: Combines tuples from two tables without duplicates
- Minus: Removes tuples in the first table that also exist in the second table
- Join: Performs equi-joins between tables based on matching attributes

# Key Goals
- Simulated basic database operations
- Build a foundational understanding of RA operators
- Ensure correct data manipulation and retrieval

# Significance
- Provides hands-on experience with core database operations
- Lays the groundwork for understanding more complext DBMS functionalities

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
-Ridhima(Manager): Tested select function and helped enhance rest of the functions. I also worked on compiling and running scripts and wrote the documentation and readme file for the project.
- Curt Leonard: Added the project function, using the Table class’s extract method to extract the elements that matched the specified elements and add them to the new table returned. Also implemented the typeCheck method, which verifies that the number of elements in the tuple is correct and that each element matches the type specified by the domain.
- Heeya Jolly: Added the minus function by comparing the tuples from both tables and extracting tuples not present in the second table. The method returns a table containing only the rows that are present in the first table and not in the second.
- Jason Todd Maurer: For Class Index: Implemented Constructor to specify values outside of the map that are specific to the index, InsertTuple() which inserts the tuple into the index, while checking that the insert is valid, PopulateMap() which populates an empty index with the tuples that already exist in the table.  For Class Table: Implemented delete_index, which deletes indexes, also implemented an ArrayList, which holds any created indexes, outside of the primary key index. 
- Thomas Nguyen: Added the union function by creating two loops to check table compatibility and merge the tuples into a single table. The first loop adds tuples from the second table, while the second loop adds tuples from the first table, returning a new table with all the combined tuples. Since duplicates were not a concern, the function was completed at that point.


# Documentation
For a detailed explanation of the implementation, usage examples, and performance analysis, please refer to the documentation file named 'Documentation.pdf'.



