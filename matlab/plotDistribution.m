%clear worskspace variables, close all graphs, clear command window
close all; clear; clc

% Files are in the current directory. Change this folder!
folder = cd;

% the number of vertices. Change this number!
n = 20;
% the number of edges. Change this number!
edges = 20;

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

figure;
hold on;
 
% the fit
repfit = fitdist(counts, 'GeneralizedExtremeValue');
flowfit = fitdist(flowCounts, 'GeneralizedExtremeValue');

% the maximum likelihood estimate
repmle = mle(counts, 'distribution','GeneralizedExtremeValue');
flowmle = mle(flowCounts, 'distribution', 'GeneralizedExtremeValue');
    
% get pdf
nChooseTwo = n * (n-1) / 2;
x_values = n-1:1:nChooseTwo;
pdfRep = pdf(repfit,x_values);

nPermTwo = n * (n-1);
flow_x_values = n-1:1:nPermTwo;
pdfFlow = pdf(flowfit,flow_x_values);
pdfNorm = pdf(fitdist(flowCounts, 'Normal'), flow_x_values);
pdfLog = pdf(fitdist(flowCounts, 'Logistic'), flow_x_values);

% histograms
nbins = size(NumberOfGraphs,1);
nbins = 30;
histogram(counts,nbins,'Normalization','pdf','EdgeColor', 'none');
histogram(flowCounts,nbins,'Normalization','pdf','EdgeColor', 'none', 'FaceColor', [1 1 0]);

% pdf lines
line_width = 2;
line(x_values,pdfRep,'LineStyle','--','Color','k','LineWidth',line_width)
line(flow_x_values,pdfFlow,'LineStyle','-.','Color','r','LineWidth',line_width)
      
maxX = max(counts);
maxX = max([max(counts) max(flowCounts)]);
xlim([n-1, maxX])
xlabel('Number of distinct min cuts');
ylabel('NOT percent of directed graphs');
legend('Replication Cut','Flow Cut','GEV','GEV Flow');
plotTitle = sprintf('%d vertices, %d edges', n, edges);
title(plotTitle);
    
hold off;
  
min(flowCounts)
mean(flowCounts)
max(flowCounts)
min(counts)
mean(counts)
max(counts)

fprintf('Printing GEV parameters of rep cut:\n');
repmle
fprintf('Printing GEV parameters of flow cut:\n');
flowmle

% error values
repNorm = NumberOfGraphs / sum(NumberOfGraphs);
flowNorm = flowNumberOfGraphs / sum(flowNumberOfGraphs);

fprintf('Printing l norm values of rep cut');
l1norm = sum(abs(pdfRep' - repNorm))
l2norm = sqrt(sum((pdfRep' - repNorm).^2))
linfinitynorm = max(abs(pdfRep' - repNorm))

fprintf('Printing l norm values of flow cut');
l1norm_flow = sum(abs(pdfFlow' - flowNorm))
l2norm_flow = sqrt(sum((pdfFlow' - flowNorm).^2))
linfinitynorm_flow = max(abs(pdfFlow' - flowNorm))

normnorm1 = sum(abs(pdfNorm' - flowNorm));
normnorm2 = sqrt(sum((pdfNorm' - flowNorm).^2));
normnorminf = max(abs(pdfNorm' - flowNorm));

lognorm1 = sum(abs(pdfLog' - flowNorm));
lognorm2 = sqrt(sum((pdfLog' - flowNorm).^2));
lognorminf = max(abs(pdfLog' - flowNorm));

