%clear worskspace variables, close all graphs, clear command window
clc

% number of vertices. Change this number!
n = 20;

% file name. Change this file!
rFilename = 'distribution_r_GraphFolder_20_20_1000.txt';

rFileID = fopen(fullfile(folder, rFilename), 'rt');

R_text = textscan(rFileID,formatSpec,3,'Delimiter','|');

%gets info from the R file
figure;
numberOfColumns = n * (n-1) / 2;
numberOfRows = (n-1) - 1 + 1;
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
surf(Z);
xlabel('# of Min Cuts');
ylabel('# of nodes in R');
zlabel('Percentage of instances');
title('R Sizes');
    
fclose(rFileID);