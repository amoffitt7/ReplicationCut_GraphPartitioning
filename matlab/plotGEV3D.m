%clear worskspace variables, close all graphs, clear command window
close all; clear; clc

% Files are in the current directory. Change this folder! (to the folder 
% which includes the flowcut distribution files)
folder = cd;

% 20 vertices. Change this number!
n = 70;

% Change these numbers! increment refers to the increment between edge
% weights. For example, if you are plotting for 20, 30, 40 ... edges then
% the increment is 10. If you are plotting for 3, 4, 5 ... edges then the
% increment is 1. Skips if file does not exist.
lowestEdgeWeight = 70;
highestEdgeWeight = 4570;
increment = 10;

% This is the maximum number of edges for which fitting a GEV distribution
% will not produce an error. You will have to guess and check for this!
error_bound = 1000;

% --------------------------Code below-------------------------- %

% matrix with all the data
Z = [];

maxY = 1;

firstIndex = lowestEdgeWeight / increment;
lastIndex = highestEdgeWeight / increment;
for i = firstIndex:lastIndex

    % the number of edges
    edges = i * increment;

    %Extract the data.
    flowTextFilename = sprintf('distribution_flowcut_GraphFolder_%d_%d_1000.txt', n, edges);
    
    if ~exist(fullfile(folder, flowTextFilename), 'file')
        continue;
    end

    flowFileID = fopen(fullfile(folder, flowTextFilename), 'rt');

    flow_text = textscan(flowFileID,'%s',2,'Delimiter','|');
    flowT = textscan(flowFileID,'%d %d','Delimiter','|');

    fclose(flowFileID);
    MinCutNumber = double(flowT{1});
    flowNumberOfGraphs = double(flowT{2});
    
    %Create vector of values; this is so we can plot histograms
    flowCounts = [];
    for i = 1:size(MinCutNumber, 1)
        for j = 1:flowNumberOfGraphs(i)
            flowCounts = [flowCounts; i + n - 2];
        end
    end

    edgestemp = ones(size(flowCounts,1),1) * edges;
    temp = [edgestemp, flowCounts];
    Z = [Z; temp];

    if edges < error_bound
        % plotting GEV overlay
        gevfit = fitdist(flowCounts, 'GeneralizedExtremeValue');
        nPermTwo = n * (n-1);
        flow_x_values = n-1:1:nPermTwo;
        pdfGev = (pdf(gevfit,flow_x_values))';
        x = ones(size(pdfGev)) * edges;
        y = MinCutNumber;
        plot3(x,y,pdfGev,'Color','r'); 
    end
    hold on;
    
    maxY = max([maxY, max(flowCounts)]);

end


xlabel('Edges');
ylabel('# of distinct min cuts');
ylim([0,maxY])
zlabel('# of instances');
plotTitle = sprintf('%d vertices distributions', n);
title(plotTitle);