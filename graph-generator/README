# Graph Generation (HOW-TO-USE)

### Running the code
This is a single file that you can just load into whatever IDE you are  
using that will run C code. When you run the code, graph folders will  
be generated in the same location that the code is run from.  
This is important because you will need to manually transfer these  
folders afterward.

### C Code (Random Graph Generation)
When you run the C code you will prompted to enter a number of vertices.    
(The number needs to be greater than 2)

Next you will be prompted to enter the number of edges you want in the graph.  
(This number needs to be greater than the number of vertices - 1, and less than  
C(N, 2) (N choose 2) where N is the number of vertices)  
**These restrictions are based off the current assumption that the base cycle  
length is automatically being set to be equal to the number of vertices.

Finally you will be prompted to enter the number of random graphs that you  
want generated.

Once you hit enter after the number of graphs prompt, the program will generate  
as many graphs as you specified with the number of vertices that you entered  
and every graph will have the specified number of edges that you entered with   
edge weights between 1 and the max edge weight which is currently set to 1000.  

**If you want to see print statements to watch what is going on during   
generation, just uncomment the print statements between lines 306 to 378

You will then be promted to complete the first three steps nine more times.  
(DO NOT MAKE THE SAME SELECTIONS OR YOU WILL OVERWRITE)  

Each generated graph is created as a text file that holds the graph data   
as a number of vertices on the first line followed by an adjacency matrix  
on the following lines.  

Every time a group of graphs is generated with specific dimensions, a folder  
is created to store each of the graphs created in that batch.  

Produced folders containing graph files are stored in the same directory/folder  
that main.c is stored in. Folder names will be in the form:  
    <strong>GraphFolder_X_Y_Z</strong>       where...  
    - X = the number of vertices in every graph in this folder  
    - Y = the number of edges in every graph in this folder    
    - Z = the number of graphs in this folder 
    
Graph file names will be in the form:  
    <strong> graphX_Y_Z </strong>           where...  
    * X = the number of vertices in this graph  
    * Y = the number of edges in this graph  
    * Z = a number distinguishing this graph from the others  
    
#### Useful C Links:  
How to create a C project with visual studio:  
https://www.youtube.com/watch?v=Slgwyta-JkA  

Solving scanf issue in newer versions:  
https://www.youtube.com/watch?v=lHfLLy1Ya5U  

Graph Generation code:  
http://condor.depaul.edu/rjohnson/source/graph_ge.c  
