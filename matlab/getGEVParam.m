%clear worskspace variables, close all graphs, clear command window
close all; clear; clc

% Files are in the current directory. Change this folder! (to the folder 
% which includes the flowcut distribution files)
folder = cd;

% 20 vertices. Change this folder!
n = 20;

% Change these numbers! increment refers to the increment between edge
% weights. For example, if you are plotting for 20, 30, 40 ... edges then
% the increment is 10. If you are plotting for 3, 4, 5 ... edges then the
% increment is 1. Skips if file does not exist.
lowestEdgeWeight = 20;
highestEdgeWeight = 380;
increment = 10;

% --------------------------Code below-------------------------- %

% matrix with all the data
Z = [];

% Holds parameter data
allmle = [];

firstIndex = lowestEdgeWeight / increment;
lastIndex = highestEdgeWeight / increment;
for i = firstIndex:lastIndex

    % the number of edges
    edges = i * increment;

    %Extract the data.
    %textFilename = sprintf('distribution_GraphFolder_%d_%d_1000.txt', n, edges);
    flowTextFilename = sprintf('distribution_flowcut_GraphFolder_%d_%d_1000.txt', n, edges);
    
    if ~exist(fullfile(folder, flowTextFilename), 'file')
        continue;
    end

    %fileID = fopen(fullfile(folder, textFilename), 'rt');
    flowFileID = fopen(fullfile(folder, flowTextFilename), 'rt');

    %T_text = textscan(fileID,'%s',2,'Delimiter','|');
    %T = textscan(fileID,'%d %d', 'Delimiter', '|');
    flow_text = textscan(flowFileID,'%s',2,'Delimiter','|');
    flowT = textscan(flowFileID,'%d %d','Delimiter','|');

    %fclose(fileID);
    fclose(flowFileID);
    MinCutNumber = double(flowT{1});
    %NumberOfGraphs = double(T{2});
    flowNumberOfGraphs = double(flowT{2});
    
    %Create vector of values; this is so we can plot histograms
    %counts = [];
    flowCounts = [];
    for i = 1:size(MinCutNumber, 1)
        %for j = 1:NumberOfGraphs(i)
            %i + n - 2 comes from the fact that the first 
            %index is n-1, where n is the number of vertices
            %counts = [counts; i + n - 2];
        %end
        for j = 1:flowNumberOfGraphs(i)
            flowCounts = [flowCounts; i + n - 2];
        end
    end

    gevfit = fitdist(flowCounts, 'GeneralizedExtremeValue');
    gevmle = mle(flowCounts, 'distribution', 'GeneralizedExtremeValue');
    
    nPermTwo = n * (n-1);
    flow_x_values = n-1:1:nPermTwo;
    pdfGev = pdf(gevfit,flow_x_values);
    flowNorm = flowNumberOfGraphs / sum(flowNumberOfGraphs);
    
    % error values
    l1norm = sum(abs(pdfGev' - flowNorm));
    l2norm = sqrt(sum((pdfGev' - flowNorm).^2));
    linfinitynorm = max(abs(pdfGev' - flowNorm));
    
    row = [n, edges, gevmle, l1norm, l2norm, linfinitynorm];
    allmle = [allmle;row];
    
end

% create spreadsheet from GEV parameters
sheetName = sprintf('%d_vertices_GEV.xlsx', n);
xlswrite(fullfile(cd, sheetName), allmle);