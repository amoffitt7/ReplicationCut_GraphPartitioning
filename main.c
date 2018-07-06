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

/*** record for qsorting a graph to provide another isomorphic to it ***/
#define RecordSize 24
typedef struct {
	char record[RecordSize];
} Record;

int zero_vertex_or_edge_count(void);
void fix_imbalanced_graph(void);

void print_graph(int v,
	int e,
	char* out_file,
	int* adj_matrix,
	int dir_flag);

int unpicked_vertices_p(int* closed, int n);
int pick_to(int* closed, int vertices);


void random_connected_graph(int v,
	int e,
	int max_wgt,
	int weight_flag,
	char* out_file);


void warning(char* message);
int bogus_file_name(int count);
void build_graphs(void);
void process_choice(void);
void display_dimensions_menu(int which);
void display_max_weight_menu(void);
void display_outfile_menu(int count, int index1, int index2);
void seed_ran(void);
int illegal_parms(int files);
int ran(int k);     /* customized random number generator */
void permute(int* a, int n); /* gives a random permutation */

							 /* initialize array a so that a[ i ] = i */
void init_array(int* a, int end);

void swap(int* a, int *b); /* swap two ints */
int get_int(char* indent); /* get user input & check for valid input */
						   /*** miscellany ***/
#define True         1
#define False        0
#define None         -999
#define Undirected   0
#define Directed     1
#define ExitProgram  0

						   /*** graph generators ***/
#define RandomConnectedGraph   1

						   /*** warnings ***/
#define IllegalDimensions   "\n\n\tNeither edge nor vertex count may be zero.\n\n"
#define IllegalFileName     "\n\n\tOutput file has illegal name.\n\n"
#define InsufficientStorage "\n\n\tThere is not enough space for this size graph.\n\n"
#define SingleVertexNetwork "\n\n\tA single vertex network is not allowed.\n\n"

						   /*** prompts ***/

#define ToplevelPrompts  2
static char  *toplevel_prompts[] =
{ 
	" 0. Exit Program ",
	" 1. Random Connected Graph ",
};



#define VerticesOnly      -111
static char *dimension_prompt = " Max Number of Vertices: ";



static char *main_file_prompt = " File for graph:  ";

static char genericTitle[128];
static int maxNumberOfVertices;
static int numVertices = 3;

/*** output files ***/
#define MaxFileName   128
typedef struct out_files {
	char  outfile1[MaxFileName + 1];
	char  outfile2[MaxFileName + 1]; 
	char  outfile3[MaxFileName + 1];
} Outfile;

/*** graphs ***/
typedef struct graph_parms {
	int      edge_count;
	int      vertex_count;
	int      max_weight;
	int      max_degree;
} GraphParms;

typedef struct graph_props {
	int      simple_p;
	int      weighted_p;
	int      directed_p;
	int      dag_p;
	int      isomorphic_p;
	int      network_p;
} GraphProps;

/*** menu ***/
typedef struct menu {
	GraphProps   props;
	GraphParms   parms;
	Outfile      outfiles;
} MenuStruct;

typedef MenuStruct *Menu;

static MenuStruct  menu_structure;
static Menu        menu = &menu_structure;

static int   build_more_graphs,
menu_choice;

/* min, max, odd */
#undef min
#define min( x, y )   ((( x ) < ( y )) ? ( x ) : ( y ))

#undef max
#define max( x, y )   ((( x ) > ( y )) ? ( x ) : ( y ))

#define odd( num )    ( ( num ) % 2 )


int main()
{
	sprintf(genericTitle, "%s", "graph");
	build_more_graphs = True;
	build_graphs();
	return 0;
}

void build_graphs(void)
{
	display_dimensions_menu(VerticesOnly);
	display_max_weight_menu();

	while (numVertices <= maxNumberOfVertices) {
		process_choice();
		numVertices++;
	}
}



void process_choice(void)
{
	seed_ran();  /* seed system's random number generator */

	menu->props.weighted_p =
		menu->props.dag_p =
		menu->props.isomorphic_p =
		menu->props.simple_p =
		menu->props.directed_p =
		menu->props.network_p = False;



	menu->props.simple_p = True;
	menu->props.directed_p = True;
	menu->props.weighted_p = True;

	menu->parms.vertex_count = numVertices;
	menu->parms.edge_count = menu->parms.vertex_count; //Avoids an error

	display_outfile_menu(1, None, None);
	if (illegal_parms(1))
		return;
	else
		random_connected_graph(menu->parms.vertex_count,
			menu->parms.edge_count,
			menu->parms.max_weight,
			menu->props.weighted_p,
			menu->outfiles.outfile1);
}



void display_dimensions_menu(int which)
{
	printf("\n\t\t%s", dimension_prompt);
	maxNumberOfVertices = get_int("\n\t\t");
}



void display_max_weight_menu(void)
{
	printf("\n\t\t\tMaximum Edge Weight:  ");
	menu->parms.max_weight = get_int("\n\t\t\t");
}



void display_outfile_menu(int count, int index1, int index2)
{	
	char snum[3];
	sprintf(snum, "%d", numVertices);

	char combined[20];
	sprintf(combined, "%s", genericTitle);
	strcat(combined, snum);

	sscanf(combined, "%s", menu->outfiles.outfile1);
}



int zero_vertex_or_edge_count(void)
{
	return (menu->parms.vertex_count == 0 ||
		menu->parms.edge_count == 0);
}



int bogus_file_name(int count)
{
	if (count == 1)
		return (!strlen(menu->outfiles.outfile1));
	else
		return (!strlen(menu->outfiles.outfile1) &&
			!strlen(menu->outfiles.outfile2));
}



void fix_imbalanced_graph(void)
{
	int  max_edges;

	if (menu->props.simple_p) {
		max_edges = menu->parms.vertex_count
			* (menu->parms.vertex_count - 1);
		if (!menu->props.directed_p)
			max_edges /= 2;
		if (menu->parms.edge_count > max_edges)
			menu->parms.edge_count = max_edges;
	}
	else if (menu->props.dag_p) {
		max_edges = (menu->parms.vertex_count
			* (menu->parms.vertex_count - 1)) / 2;
		if (menu->parms.edge_count > max_edges)
			menu->parms.edge_count = max_edges;
	}
	else if (menu->props.isomorphic_p) {
		if (odd(menu->parms.max_degree))
			if (odd(menu->parms.vertex_count))
				menu->parms.vertex_count++;
		if (menu->parms.vertex_count <= menu->parms.max_degree)
			menu->parms.vertex_count = menu->parms.max_degree + 1;
	}
	else if (menu->props.network_p) {
		max_edges =
			(menu->parms.vertex_count * menu->parms.vertex_count) -
			(menu->parms.vertex_count * 3) + 3;
		if (menu->parms.edge_count >= max_edges)
			menu->parms.edge_count = max_edges;
	}
}



int illegal_parms(int files)
{
	if (zero_vertex_or_edge_count()) {
		warning(IllegalDimensions);
		return (True);
	}
	else if (bogus_file_name(files)) {
		warning(IllegalFileName);
		return (True);
	}
	else {
		fix_imbalanced_graph();
		return (False);
	}
}



void warning(char* message)
{
	printf(message);
}


/******************** graph generator **************************/

/* This function generates a random connected simple graph with
v vertices and max(v-1,e) edges.  The graph can be weighted
(weight_flag == 1) or unweighted (weight_flag != 1). If
it is weighted, the weights are in the range 1 to max_wgt.
It is assumed that e <= v(v-1)/2. (In this program, this assured
because of the call to fix_imbalanced_graph.)

To  generate  a  random  connected  graph,  we begin by
generating  a  random  spanning  tree.  To generate a random
spanning  tree,  we  first  generate  a  random  permutation
tree[0],...,tree[v-1]. (v = number of vertices.)
We  then  iteratively  add edges  to form a
tree.  We  begin with the tree consisting of vertex tree[0] and
no   edges.   At   the   iterative   step,  we  assume  that
tree[0],tree[1],...,tree[i-1]  are  in  the  tree.  We  then add vertex
tree[i]  to     the    tree    by    adding    the    edge
(tree[i],tree[rand(i)]).  (This  construction  is similar to
that  of  Prim's algorithm.) Finally, we add random edges to
produce the desired number of edges.
*/
void random_connected_graph(int v,
	int e,
	int max_wgt,
	int weight_flag,
	char* out_file)
{
	int i, j, count, index, *adj_matrix, *tree;

	int numPossibleEdges = v * (v - 1);

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

	/*  Next generate a random spanning tree.
	The algorithm is:

	Assume that vertices tree[ 0 ],...,tree[ i - 1 ] are in
	the tree.  Add an edge incident on tree[ i ]
	and a random vertex in the set {tree[ 0 ],...,tree[ i - 1 ]}.
	*/

	for (i = 1; i < v; i++) {
		j = ran(i);
		adj_matrix[tree[i] * v + tree[j]] =
			weight_flag ? 1 + ran(max_wgt) : 1;
		adj_matrix[tree[j] * v + tree[i]] =
			weight_flag ? 1 + ran(max_wgt) : 1;
	}

	/* Add additional random edges until achieving at least desired number */

	for (count = 0; count < ran(2 * (v - 1)); ) {
		i = ran(v);
		j = ran(v);

		if (i == j)
			continue;

		if (i > j)
			swap(&i, &j);

		index = i * v + j;
		if (!adj_matrix[index]) {
			adj_matrix[index] = weight_flag ? 1 + ran(max_wgt) : 1;
			count++;
		}
	}

	print_graph(v, count, out_file, adj_matrix, Directed);

	free(tree);
	free(adj_matrix);
}




int pick_to(int* closed, int vertices)
{
	int  vertex;

	if (closed == NULL)        /* pick any vertex, even if picked before... */
		do {
			vertex = ran(vertices);
		} while (vertex == 0);  /* ...except for start */
	else {
		do {
			vertex = ran(vertices);
		} while (closed[vertex]);
		closed[vertex] = True;
	}
	return (vertex);
}



int unpicked_vertices_p(int* closed, int n)
{
	int  i = 1;

	while (i < n)
		if (!closed[i])
			return (True);
		else
			i++;
	return (False);
}


/*** ran, etc. ***/
void seed_ran(void)
{
	srand((unsigned short)time(NULL));
}



/* Return a random integer between 0 and k-1 inclusive. */
int ran(int k)
{
	if (k == 0) {
		return 0;
	}
	return rand() % k;
}



void print_graph(int v,
	int e,
	char* out_file,
	int* adj_matrix,
	int dir_flag)
{
	int i, j, index;
	FILE *fp;

	if ((fp = fopen(out_file, "w")) == NULL) {
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
