%clear worskspace variables, close all graphs, clear command window
clc

% number of vertices. Change this number!
n = 20;
% the number of edges. Change this number!
edges = 23;

% file name. Change this file!
rFilename = sprintf('distribution_r_GraphFolder_%d_%d_1000.txt', n, edges);

rFileID = fopen(fullfile(folder, rFilename), 'rt');

R_text = textscan(rFileID,formatSpec,3,'Delimiter','|');

%gets info from the R file
figure;
numberOfColumns = n * (n-1) / 2;
numberOfRows = n + 1;
Z = zeros(numberOfRows, numberOfColumns);
while (~feof(rFileID))  
    R_T = textscan(rFileID, '%d %d %d','Delimiter','|');
    MinCutNumber = double(R_T{1});
    RSize = double(R_T{2});
    NumberOfCuts = double(R_T{3});
    if isempty(MinCutNumber) == 0
        x = MinCutNumber(1);
        %normalize number of cuts
        V = NumberOfCuts/sum(NumberOfCuts);
        Z(:,x) = V;
    end
    eob = textscan(rFileID,'%s',1,'Delimiter','\n'); 
end
r = surf(Z);
r.FaceAlpha = 0;
xlabel('# of Min Cuts');
ylabel('# of nodes in R');
zlabel('Percentage of instances');
theTitle = sprintf('R sizes, %d vertices, %d edges', n, edges);
title(theTitle);
    
fclose(rFileID);