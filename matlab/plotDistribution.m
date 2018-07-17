%clear worskspace variables, close all graphs, clear command window
clear; close all; clc

% Files are in the current directory.
folder = cd;

% the number of vertices. Change this number!
n = 10;

%Extract the data. Change this file name!
textFilename = 'distribution_GraphFolder_10_100.txt';
%flowTextFilename = 'distribution_flowcut_GraphFolder_%d_100.txt';
    
fileID = fopen(fullfile(folder, textFilename), 'rt');
%flowFileID = fopen(fullfile(folder, flowTextFilename), 'rt');
   
formatSpec = '%s';
N = 2;
T_text = textscan(fileID,formatSpec,N,'Delimiter','|');
T = textscan(fileID,'%d %d', 'Delimiter', '|');
%flow_text = textscan(flowFileID,formatSpec,N,'Delimiter','|');
%flowT = textscan(flowFileID,'%d %d','Delimiter','|');
    
fclose(fileID);
%fclose(flowFileID);
MinCutNumber = double(T{1});
NumberOfGraphs = double(T{2});
%flowNumberOfGraphs = double(flowT{2});
    
%Create vector of values; this is so we can plot histograms
counts = [];
%flowCounts = [];
for i = 1:size(MinCutNumber, 1)
    for j = 1:NumberOfGraphs(i)
        %i + n - 2 comes from the fact that the first 
        %index is n-1, where n is the number of vertices
        counts = [counts; i + n - 2];
    end
    %for j = 1:flowNumberOfGraphs(i)
    %    flowCounts = [flowCounts; i + n - 2];
    %end
end
    
%fit a distribution to it
figure;
hold on;
 
%flowfit = fitdist(flowCounts, 'Logistic');
%poissonfit = fitdist(counts, 'Poisson');
%normalfit = fitdist(counts, 'Normal');
logisticfit = fitdist(counts, 'Logistic');
    
realmean = mean(counts);
realstd = std(counts);
%poissonmle = mle(counts,'distribution','Poisson');
%normalmle = mle(counts);
logisticmle = mle(counts, 'distribution','Logistic');
    
%allMle = [];
%allMle = [[realmean realstd]; allMle; [poissonmle 0]; normalmle; logisticmle];
    
%get pdf
nChooseTwo = n * (n-1) / 2;
x_values = n-2:1:nChooseTwo+1;
%pdfFlow = pdf(flowfit,x_values);
%pdfPoisson = pdf(poissonfit,x_values);
%pdfNorm = pdf(normalfit,x_values);
pdfLog = pdf(logisticfit,x_values);
    
line_width = 2;
nbins = size(NumberOfGraphs,1);
nbins = 30;
histogram(counts,nbins,'Normalization','pdf','EdgeColor', 'none');
%histogram(flowCounts,nbins,'Normalization','pdf','EdgeColor', 'none', 'FaceColor', [1 1 0]);
    
%line(x_values,pdfPoisson,'LineStyle','-','Color','r','LineWidth',line_width)
%line(x_values,pdfNorm,'LineStyle','-.','Color','b','LineWidth',line_width)
%line(x_values,pdfFlow,'LineStyle','-.','Color','b','LineWidth',line_width)
line(x_values,pdfLog,'LineStyle','--','Color','k','LineWidth',line_width)
      
maxX = max(counts);
%maxX = max([max(counts) max(flowCounts)]);
xlim([n-1, maxX])
xlabel('Number of distinct min cuts');
ylabel('Percent of directed graphs');
legend('Replication Cut','Logistic');
plotTitle = sprintf('%d vertices', n);
title(plotTitle);
    
hold off;
        
