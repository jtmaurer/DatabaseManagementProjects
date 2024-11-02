

# Project 3 - Performance Comparison

Project 3 aims to compare how different data structures (NO_MAP, TREE_MAP, HASH_MAP, and BPTREE_MAP) perform when handling search and combination tasks in a database. By testing each structure with different data sizes, we’ll identify which one is the fastest and most efficient as data grows.
## Authors
- Ridhima Reddy
- Curt Leonard
- Heeya Jolly
- Jason Todd Maurer
- Thomas Nguyen


# Key Features

- Dataset Generation: Creates datasets of varying sizes using a tuple generator for consistent testing.
- Multiple Map Implementations: Tests four data structures—NO_MAP, TREE_MAP, HASH_MAP, and BPTREE_MAP—for handling data.
- Select and Join Operations: Performs two Select and two Join operations for each map type to analyze their efficiency.
- Performance Timing: Measures runtime using nanoTime(), averaging over five iterations while skipping the first for JIT accuracy.
- Data Visualization: Plots runtime performance against dataset size to visualize how each map type scales.
- Documentation: Provides detailed documentation for all classes and methods for clarity and usability.


# Key Goals
- Compare the efficiency of different data structures (NO_MAP, TREE_MAP, HASH_MAP, BPTREE_MAP) for Select and Join database operations.
- Determine how each structure performs as dataset sizes increase.
- Identify the most efficient data structure for handling large data in database applications.


# Significance
-This project provides insights into which data structure is best suited for database operations as data scales, which is crucial for optimizing storage and retrieval efficiency.
- Understanding these performance differences can help improve database management systems, especially for applications with growing data needs.s

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
- Ridhima Reddy- Ran test cases, wrote documentation and readme, tested functionality, and analyzed all the graphs.
- Curt Leonard- Implemented DIndex and developed some scripts, and tested tuple operations.
- Heeya Jolly- Added the tuple Generator, which randomly generated large number of tuples. It generates tuples (rows) based on the schema of a table, which specifies column names, data types, and other constraints. This allows for automated creation of data rows to populate tables for testing, simulations, or general use. 
- Jason Todd Maurer-Designed and implemented test cases for select and join operations. created tuple data sets for different amounts of total tuples (10-100K).
- Thomas Nguyen- Compiled all the data that was given to him and jotted the data into the software to be able to graph them. Made four columns for each map and plotted them on a graph. Generated a line of best fit for each of the cases of select case 1, select case 2, join case 1, and join case 2. Then made them all logarithmic graphs.

# Documentation
For a detailed explanation of our project, key features,implementations, indexes, and performance testing, and conculsion please refer to the documentation file named 'Documentation.pdf'.
