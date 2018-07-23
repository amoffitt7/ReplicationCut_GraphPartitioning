%clear worskspace variables, close all graphs, clear command window
clc

% Files are in the current directory. Change this folder!
folder = cd;

% number of vertices. Change this number!
n = 20;
% the number of edges. Change this number!
edges = 23;

% file name. Change this file!
sFilename = sprintf('distribution_s_GraphFolder_%d_%d_1000.txt', n, edges);

sFileID = fopen(fullfile(folder, sFilename), 'rt');

formatSpec = '%s';
S_text = textscan(sFileID,formatSpec,3,'Delimiter','|');

%gets info from the S file
figure;
numberOfColumns = n * (n-1) / 2;
numberOfRows = (n-1) - 1 + 1;
Z = zeros(numberOfRows, numberOfColumns);
while (~feof(sFileID))  
    S_T = textscan(sFileID, '%d %d %d','Delimiter','|');
    MinCutNumber = double(S_T{1});
    SSize = double(S_T{2});
    NumberOfCuts = double(S_T{3});
    if isempty(MinCutNumber) == 0
        x = MinCutNumber(1);
        %normalize number of cuts
        V = NumberOfCuts/sum(NumberOfCuts);
        Z(:,x) = V;
    end
    eob = textscan(sFileID,'%s',1,'Delimiter','\n'); 
end
s = surf(Z);
s.FaceAlpha = 0;
xlabel('# of Min Cuts');
ylabel('# of nodes on left side of cut');
zlabel('Percentage of instances');
theTitle = sprintf('S Sizes, %d vertices, %d edges', n, edges);
title(theTitle);
    
fclose(sFileID);