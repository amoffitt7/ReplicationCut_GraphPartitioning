/* 
n     m
v1    w1    c1
v2    w2    c2
.
where  n  is  the  number of vertices and m is the number of
edges  in  the  graph. Edge i is incident on vertices vi and
wi.  The weight of edge i is ci. If the graph is unweighted,
the value of ci is 1 for all i.
*/

#include <stdio.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>


/*** Methods ***/
void seed_ran(void);
void build_graphs(void);
void display_dimensions_menu();
void process_choice(char* directory);
void create_outfile_name();

void random_connected_graph(int v,
	int e,
	int max_wgt,
	char* out_file,
	char* directory);


void print_graph(int v,
	int e,
	char* out_file,
	int* adj_matrix,
	int dir_flag,
	char* directory);


int ran(int k);                                               /* customized random number generator */
void permute(int* a, int n);                                  /* gives a random permutation */							 
void swap(int* a, int *b);									  /* swap two ints */
void init_array(int* a, int end);                             /* initialize array a so that a[ i ] = i */
int get_int(char* indent);                                    /* get user input & check for valid input */
int getWeight();


/*** Miscellany ***/
#define True         1
#define False        0
#define None         -999
#define Undirected   0
#define Directed     1
#define ExitProgram  0
#define MaxWeight    1000


/*** Prompts ***/
static char  *numVertices_prompt =        " Number of Vertices: ";
static char  *numEdges_prompt =           " Number of edges: ";
static char  *edge_range_prompt1 =        " Enter a starting edge value: ";
static char  *edge_range_prompt2 =        " Enter a ending edge value: ";
static char  *edge_range_prompt3 =        " Enter an increment size: ";
static char  *unitWeight_prompt =         " Unit weights ( Enter 1 for YES, 0 for NO ): ";
static char  *maxEdgeWeight_prompt =      " Maximum edge weight: ";
static char  *cycleSize_prompt =          " Cycle size: ";
static char  *numGraphs_prompt =	      " How many graphs would you like generated: ";

static char  *invalidVertices_prompt =    " Invalid Entry! \n\t\t Enter a number greater than 3: ";
static char  *invalidEdges_prompt =       " Invalid Entry! \n\t\t Enter a number greater than ";
static char  *invalidIncrement_prompt =   " Invalid Entry! \n\t\t Enter a number greater than ";
static char  *invalidEdgeWeight_prompt =  " Invalid Entry! \n\t\t Enter a weight greater than 0: ";
static char  *invalidCycleLength_prompt = " Invalid Entry! \n\t\t Enter a length greater than 1 ";

static char genericTitle[128];
static int maxNumberOfVertices;

static int numberOfGraphs = 1;                  /*User entry that tells how many graphs we will make with current settings*/
static int graphCount = 1;                      /*Number graphs generated so far with provided settings*/
static int build_more_graphs;
static int edgeWeightScheme;
static int rangeStart;
static int rangeEnd;
static int increment;



/*** graphs ***/
typedef struct graph_parms {
	int      edge_count;
	int      vertex_count;
	int      max_weight;
	int      max_degree;
	int      base_cycle_size;
} GraphParms;

/*** Menu ***/
#define MaxFileName   128
typedef struct menu {
	GraphParms   parms;
	char         outfile[MaxFileName + 1];
} MenuStruct;

typedef MenuStruct *Menu;
static MenuStruct  menu_structure;
static Menu        menu = &menu_structure;





int main()
{
	sprintf(genericTitle, "%s", "graph");

	seed_ran();  /* seed system's random number generator */

	for (int i = 0; i < 10; i++) {
		build_more_graphs = True;

		display_dimensions_menu();

		for (int i = rangeStart; i <= rangeEnd; i = i + increment) {
			menu->parms.edge_count = i;
			build_graphs();
			graphCount = 1;
		}

		graphCount = 1;
	}
	return 0;
}



/*** Function to seed the random number generator ***/
void seed_ran(void)
{
	srand((unsigned short)time(NULL));
}



void build_graphs(void)
{
	/*** Make new Folder to store all generated graphs ***/
	char directory[40];
	sprintf(directory, "%s", "GraphFolder_");

	char numVertices[5];
	sprintf(numVertices, "%d", menu->parms.vertex_count);
	strcat(directory, numVertices);

	strcat(directory, "_");

	char edgeCount[5];
	sprintf(edgeCount, "%d", menu->parms.edge_count);
	strcat(directory, edgeCount);

	strcat(directory, "_");

	char numGraphs[8];
	sprintf(numGraphs, "%d", numberOfGraphs);
	strcat(directory, numGraphs);

	char command[50];
	sprintf(command, "%s", "mkdir ");
	strcat(command, directory);

	system(command);

	/*** Create graphs ***/
	while (graphCount <= numberOfGraphs) {
		process_choice(directory);
		graphCount++;
	}
}



void display_dimensions_menu()
{
	/*** Get # vertices ***/
	printf("\n\t\t%s", numVertices_prompt);
	menu->parms.vertex_count = get_int("\n\t\t");
	while (menu->parms.vertex_count < 3) {
		printf("\n\t\t%s", invalidVertices_prompt);
		menu->parms.vertex_count = get_int("\n\t\t");
	} 

	/*** Get base cycle size ***/
	/*
	printf("\n\t\t%s", cycleSize_prompt);
	menu->parms.base_cycle_size = get_int("\n\t\t");

	while(menu->parms.base_cycle_size < 2 ||
	menu->parms.base_cycle_size > menu->parms.vertex_count) {
	printf("\n\t\t%s and less than %d", invalidCycleLength_prompt, menu->parms.vertex_count + 1);
	menu->parms.base_cycle_size = get_int("\n\t\t");
	}*/
	

	menu->parms.base_cycle_size = menu->parms.vertex_count;  //Hard coded


	/*** Get # edges ***/
	int numEdgesRequired = (menu->parms.vertex_count - menu->parms.base_cycle_size) + 1;
	if (numEdgesRequired == 1) {
		numEdgesRequired = 0;
	}

	int numPossibleEdges = menu->parms.vertex_count * (menu->parms.vertex_count - 1);

	printf("\n\t\t Enter a number greater than or equal to %d, and less than %d",
		numEdgesRequired + menu->parms.base_cycle_size, numPossibleEdges + 1);

	printf("\n\t\t%s", edge_range_prompt1);
	rangeStart = get_int("\n\t\t");
	while (rangeStart < numEdgesRequired + menu->parms.base_cycle_size ||
		rangeStart > numPossibleEdges) {
		printf("\n\t\t%s%d and less than %d", invalidEdges_prompt,
			numEdgesRequired + menu->parms.base_cycle_size, numPossibleEdges + 1);
		rangeStart = get_int("\n\t\t");
	}

	printf("\n\t\t%s", edge_range_prompt2);
	rangeEnd = get_int("\n\t\t");
	while (rangeEnd < numEdgesRequired + menu->parms.base_cycle_size ||
		rangeEnd > numPossibleEdges || rangeEnd < rangeStart) {
		printf("\n\t\t%s%d and less than %d", invalidEdges_prompt,
			numEdgesRequired + menu->parms.base_cycle_size, numPossibleEdges + 1);
		rangeEnd = get_int("\n\t\t");
	}


	printf("\n\t\t%s", edge_range_prompt3);
	increment = get_int("\n\t\t");
	while (increment < 0 || increment >= rangeEnd - rangeStart + 1) {
		printf("\n\t\t%s 1 and less than %d", invalidIncrement_prompt, rangeEnd - rangeStart + 1);
		increment = get_int("\n\t\t");
	}

	/*
	printf("\n\t\t%s", numEdges_prompt);
	menu->parms.edge_count = get_int("\n\t\t");
	while(menu->parms.edge_count < numEdgesRequired + menu->parms.base_cycle_size ||
			menu->parms.edge_count > numPossibleEdges) {
		printf("\n\t\t%s%d and less than %d", invalidEdges_prompt, 
			numEdgesRequired + menu->parms.base_cycle_size, numPossibleEdges + 1);
		menu->parms.edge_count = get_int("\n\t\t");
	}*/



	/*** Get unit weight response ***/
	printf("\n\t\t%s", unitWeight_prompt);
	edgeWeightScheme = get_int("\n\t\t");
	while (edgeWeightScheme != 0 && edgeWeightScheme != 1) {
		printf("\n\t\t%s", unitWeight_prompt);
		edgeWeightScheme = get_int("\n\t\t");
	}
	

	/*** Get Maximum edge weight value ***/
	/*
		printf("\n\t\t%s", maxEdgeWeight_prompt);
		menu->parms.max_weight = get_int("\n\t\t\t");

		while(menu->parms.max_weight <= 0) {
			printf("\n\t\t%s", invalidEdgeWeight_prompt);
			menu->parms.max_weight = get_int("\n\t\t");
		}
	*/
	menu->parms.max_weight = MaxWeight;

	printf("\n\t\t\tHow many random graphs:  ");
	numberOfGraphs = get_int("\n\t\t\t");
}



void process_choice(char* directory)
{
	create_outfile_name();

	random_connected_graph(menu->parms.vertex_count,
		menu->parms.edge_count,
		menu->parms.max_weight,
		menu->outfile,
		directory);
}



void create_outfile_name()
{	
	char numVertices[5];
	sprintf(numVertices, "%d", menu->parms.vertex_count);
	char numEdges[5];
	sprintf(numEdges, "%d", menu->parms.edge_count);
	char gCount[7];
	sprintf(gCount, "%d", graphCount);

	char combined[40];
	sprintf(combined, "%s", genericTitle);
	strcat(combined, numVertices);
	strcat(combined, "_");
	strcat(combined, numEdges);
	strcat(combined, "_");
	strcat(combined, gCount);

	char extension[5];
	sprintf(extension, "%s", ".txt");
	strcat(combined, extension);

	sscanf(combined, "%s", menu->outfile);
}



/******************** graph generator **************************/

/*
To  generate  a  random  connected  graph,  we begin by
generating  a  random  spanning  tree.  To generate a random
spanning  tree,  we  first  generate  a  random  permutation
tree[0],...,tree[v-1]. (v = number of vertices.)
We  then create a base cycle, followed by connecting disconnected 
vertices. Finally based on the user entered amount of edges, we
randomly add any remaining needed edges.
*/
void random_connected_graph(int v,
	int e,
	int max_wgt,
	char* out_file,
	char* directory)
{
	int i, *adj_matrix, *tree;

	if ((adj_matrix = (int *)calloc(v * v, sizeof(int)))
		== NULL) {
		printf("Not enough room for this size graph\n");
		return;
	}


	if ((tree = (int *)calloc(v, sizeof(int))) == NULL) {
		printf("Not enough room for this size graph\n");
		free(adj_matrix);
		return;
	}

	printf("\n\tBeginning construction of graph.\n");

	/*  Generate a random permutation in the array tree. */
	init_array(tree, v);
	permute(tree, v);

	
	for (int z = 0; z < v; z++) {
		//printf("Tree at %d index = %d \n \n", z, tree[z]);
	}
	

	/*Generate number of vertices in the base cycle (min = 2)*/
	int cycleLength = menu->parms.base_cycle_size;
	int baseCycleLength = cycleLength;
	//printf("Base cycle length = %d \n", cycleLength);

	//printf("CYCLE ENTRIES \n \n");
	for (i = 0; i < cycleLength - 1; i++) {
		adj_matrix[tree[i] * v + tree[i + 1]] = getWeight();

		//printf("AM entry for edge from %d to %d \n", tree[i], tree[i+1]);
	}
	adj_matrix[tree[cycleLength - 1] * v + tree[0]] = getWeight();
	//printf("AM entry for edge from %d to %d \n", tree[cycleLength - 1], tree[0]);


	int numEdgesNotAdded = e - cycleLength;
	//printf("Edges left to add after cycle edges = %d \n", numEdgesNotAdded);

	int numDisconnected = v - cycleLength;

	int numInEndCycle = 0;
	int temp1 = numDisconnected;
	int temp2 = numEdgesNotAdded;
	while (temp2 > temp1 + 1 && temp1  > 0) {
		temp2 -= 2;
		temp1 -= 1;
	}
	numInEndCycle = temp1;

	int cycleTheRest = False;


	//printf("CONNECTING ALL DISCONNECTED VERTICES \n \n");
	if (numEdgesNotAdded <= numDisconnected + 1 && numDisconnected > 0) {     // Base Case 
		int startFrom = ran(cycleLength);
		//printf("randomStartFrom = %d \n", tree[startFrom]);

		adj_matrix[tree[startFrom] * v + tree[cycleLength]] = getWeight();
		//printf("AM entry for edge from %d to %d \n", tree[startFrom], tree[cycleLength]);

		for (i = cycleLength; i < v - 1; i++) {
			adj_matrix[tree[i] * v + tree[i + 1]] = getWeight();
			//printf("AM entry for edge from %d to %d \n", tree[i], tree[i+1]);
		}

		int endTo = ran(cycleLength);
		//printf("randomEndTo = %d \n", tree[endTo]);

		adj_matrix[tree[v - 1] * v + tree[endTo]] = getWeight();
		//printf("AM entry for edge from %d to %d \n", tree[v - 1], tree[endTo]);
		numEdgesNotAdded -= numDisconnected + 1;
	}	
	else {
		for (i = cycleLength; i < v; i++) {
			int randomTo;
			int randomFrom;

			//printf("Vertex %d is not connected so we are going to connect it \n", tree[i]);

			randomFrom = tree[ran(cycleLength)];
			//printf("randomFrom = %d \n", randomFrom);

			adj_matrix[randomFrom * v + tree[i]] = getWeight();
			//printf("AM entry for edge from %d to %d (FROM)\n", randomFrom, tree[i]);
			numEdgesNotAdded--;
			numDisconnected--;


			if (numEdgesNotAdded > numDisconnected + 1) {
				if (ran(2) == 0 && numInEndCycle > 0) {
					randomTo = tree[ran(numInEndCycle) + (v - numInEndCycle)];
				}
				else {
					randomTo = tree[ran(cycleLength)];
				}

				while (randomTo == tree[i]) {
					if (ran(2) == 0 && numInEndCycle > 0) {
						randomTo = tree[ran(numInEndCycle) + (v - numInEndCycle)];
					}
					else {
						randomTo = tree[ran(cycleLength)];
					}
				}

				//printf("randomTo = %d \n", randomTo);

				if (adj_matrix[tree[i] * v + randomTo] == 0) {
					adj_matrix[tree[i] * v + randomTo] = getWeight();
					//printf("AM entry for edge from %d to %d (TO)\n", tree[i], randomTo);
					numEdgesNotAdded--;
				}

				cycleLength++;
			}

			if(numEdgesNotAdded <= numDisconnected + 1 && numDisconnected != 0) {
				cycleTheRest = True;
				break;
			}
		}
	}



	if (cycleTheRest && numEdgesNotAdded > 0) {
		//printf("Edges left before end Cycle = %d \n", numEdgesNotAdded);
		//printf("\n CYCLING THE REST \n");

		//Add EXCEPTION

		int startFrom = ran(cycleLength);
		//printf("randomStartFrom = %d \n", tree[startFrom]);

		adj_matrix[tree[startFrom] * v + tree[cycleLength]] = getWeight();
		//printf("AM entry for edge from %d to %d \n", tree[startFrom], tree[cycleLength]);

		for (i = cycleLength; i < v - 1; i++) {
			adj_matrix[tree[i] * v + tree[i + 1]] = getWeight();
			//printf("AM entry for edge from %d to %d \n", tree[i], tree[i+1]);
		}

		int endTo = ran(baseCycleLength);
		//printf("randomEndTo = %d \n", tree[endTo]);

		adj_matrix[tree[v - 1] * v + tree[endTo]] = getWeight();
		//printf("AM entry for edge from %d to %d \n", tree[v - 1], tree[endTo]);
		numEdgesNotAdded -= numDisconnected + 1;
	}

	//printf("Edges left to add after CONNECTION PHASE = %d \n", numEdgesNotAdded);
	

	//printf("Randomly adding %d more edges \n", numEdgesNotAdded);
	while (numEdgesNotAdded > 0) {
		int randomTo = tree[ran(v)];
		int randomFrom = tree[ran(v)];

		//printf("randomFrom = %d \n", randomFrom);
		//printf("randomTo = %d \n", randomTo);

		if (randomTo != randomFrom && adj_matrix[randomFrom * v + randomTo] == 0) {
			adj_matrix[randomFrom * v + randomTo] = getWeight();
			//printf("AM entry for edge from %d to %d (FROM)\n", randomFrom, randomTo);
			numEdgesNotAdded--;
		}
		else {
			continue;
		}
	}

	print_graph(v, e, out_file, adj_matrix, Directed, directory);

	free(tree);
	free(adj_matrix);
}



void print_graph(int v,
	int e,
	char* out_file,
	int* adj_matrix,
	int dir_flag,
	char* directory)
{
	int i, j, index;
	FILE *fp;

	char dirAndFileName[60];
	sprintf(dirAndFileName, "%s", directory);
	strcat(dirAndFileName, "/");
	strcat(dirAndFileName, out_file);

	if ((fp = fopen(dirAndFileName, "w")) == NULL) {
		printf("Unable to open file %s for writing.\n", out_file);
		return;
	}
	printf("\n\tWriting graph to file %s.\n", out_file);

	fprintf(fp, "%5d\n", v);

	if (!dir_flag)
		for (i = 1; i < v; i++)
			for (j = i + 1; j <= v; j++) {
				index = (i - 1) * v + j - 1;
				if (adj_matrix[index])
					fprintf(fp, "%5d   %5d   %5d\n", i, j, adj_matrix[index]);
			}
	else
		for (i = 1; i <= v; i++)
			for (j = 1; j <= v; j++) {
				index = (i - 1) * v + j - 1;
				if (adj_matrix[index])
					fprintf(fp, "%5d   %5d   %5d\n", i, j, adj_matrix[index]);
			}
	fclose(fp);
	printf("\tGraph is written to file %s.\n", out_file);
}



/* Return a random integer between 0 and k-1 inclusive. */
int ran(int k)
{
	if (k == 0) {
		return 0;
	}
	return rand() % k;
}



/* randomly permute a[ 0 ],...,a[ n - 1 ] */
void permute(int* a, int n)
{
	int i;

	for (i = 0; i < n - 1; i++)
		swap(a + i + ran(n - i), a + i);
}



void swap(int* a, int *b)
{
	int temp;

	temp = *a;
	*a = *b;
	*b = temp;
}



/* set a[ i ] = i, for i = 0,...,end - 1 */
void init_array(int* a, int end)
{
	int i;

	for (i = 0; i < end; i++)
		*a++ = i;
}



/* Get integer input from user. If not an int, prompt for correct
value. indent gives the proper indentation for error message.
*/
int get_int(char* indent)
{
	char buff[30];
	int val;

	for (; ; ) {
		scanf("%s", buff);
		if (sscanf(buff, "%d", &val) != 1)
			printf("%s%s", indent, "***** Illegal input.  Reenter value: ");
		else
			return val;
	}
}



/* Return either 1 or a value between 0 and MaxWeight
*/
int getWeight()
{
	if (edgeWeightScheme) {
		return 1;
	}
	return ran(MaxWeight) + 1;
}
