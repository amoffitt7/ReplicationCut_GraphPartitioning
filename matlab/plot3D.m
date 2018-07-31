%clear worskspace variables, close all graphs, clear command window
close all; clear; clc

% Files are in the current directory. Change this folder!
folder = cd;

% 20 vertices
n = 20;

numberOfRows = n * (n-1) / 2 - (n-1) + 1;
numberOfColumns = 70-20+1;

% matrix with all the data
Z = [];

% plot results from 20 to 380 edges
for i = 2:7

    % the number of edges
    edges = i * 10;

    %Extract the data.
    textFilename = sprintf('distribution_GraphFolder_%d_%d_1000.txt', n, edges);
    flowTextFilename = sprintf('distribution_flowcut_GraphFolder_%d_%d_1000.txt', n, edges);

    fileID = fopen(fullfile(folder, textFilename), 'rt');
    flowFileID = fopen(fullfile(folder, flowTextFilename), 'rt');

    T_text = textscan(fileID,'%s',2,'Delimiter','|');
    T = textscan(fileID,'%d %d', 'Delimiter', '|');
    flow_text = textscan(flowFileID,'%s',2,'Delimiter','|');
    flowT = textscan(flowFileID,'%d %d','Delimiter','|');

    fclose(fileID);
    fclose(flowFileID);
    MinCutNumber = double(T{1});
    NumberOfGraphs = double(T{2});
    flowNumberOfGraphs = double(flowT{2});
    
    %Create vector of values; this is so we can plot histograms
    counts = [];
    flowCounts = [];
    for i = 1:size(MinCutNumber, 1)
        for j = 1:NumberOfGraphs(i)
            %i + n - 2 comes from the fact that the first 
            %index is n-1, where n is the number of vertices
            counts = [counts; i + n - 2];
        end
        for j = 1:flowNumberOfGraphs(i)
            flowCounts = [flowCounts; i + n - 2];
        end
    end
    

    edgestemp = ones(size(counts,1),1) * edges;
    temp = [edgestemp, counts];
    Z = [Z; temp];

    hold off;

end

hist3(Z,'Nbins',[6,60]);

xlabel('Edges');
ylabel('# of distinct min cuts');
zlabel('# of instances');
plotTitle = sprintf('%d vertices distributions', n);
title(plotTitle);

