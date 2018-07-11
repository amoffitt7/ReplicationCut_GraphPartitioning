# ReplicationCut_GraphPartitioning

## C Code (Random Graph Generation)
When you run the C code you will prompted to enter a number of vertices.  

Next you will be prompted to enter the number of random graphs that you  
want generated.

Finally you will be promted to enter a maximum edge weight that will be  
used in all of the graphs generated.  

Once you hit enter after the max edge weight, the program will generate  
as many graphs as you specified with the number of vertices that you entered  
and every graph will have random edges with weights between 1 and the max   
edge weight you entered.  

You will then be promted to complete the first three steps nine more times.  
(DO NOT MAKE THE SAME SELECTIONS OR YOU WILL OVERWRITE)  

Each generated graph is created as a text file that holds the graph data   
as a number of vertices on the first line followed by an adjacency matrix  
on the following lines.  

Every time a group of graphs is generated with specific dimensions, a folder  
is created to store each of the graphs created in that batch.  

Produced folders containing graph files are stored in the same directory/folder  
that main.c is stored in. Folder names will be in the form:  
    <strong>GraphFolder_X_Y</strong>       where...  
    - X = the number of vertices in every graph in this folder  
    - Y = the max edge weight of every graph in this folder  
    
Graph file names will be in the form:  
    <strong> graphX_Y_Z </strong>           where...  
    * X = the number of vertices in this graph  
    * Y = the max edge weight in this graph  
    * Z = a number distinguishing this graph from the others  
    
#### Useful C Links:  
How to create a C project with visual studio:  
https://www.youtube.com/watch?v=Slgwyta-JkA  

Solving scanf issue in newer versions:  
https://www.youtube.com/watch?v=lHfLLy1Ya5U  

Graph Generation code:  
http://condor.depaul.edu/rjohnson/source/graph_ge.c  


## Running the java algorithm code  
Once you have your folders greated via running the c code, you  
need to first make a new folder in the java workspace you are using  
in the same directory/folder that already contains your bin and src  
folders. Once you have made the new folder just transfer all of your  
graph containing folders into this new folder. Once you have done this  
you can now run the java code where you just need to pass the name of  
the folder you just created as an argument.  

To run the java code you just need to make a project and add these four  
files:  

 - MaxFlowMinCut.java
 - Run_Test.java
 - ReplicationCutAlgorithm.java
 - Cut.java
 
 All of the results for each graph are stored in a new text file and are  
 stored in the same folder that the graph file is stored in. 
