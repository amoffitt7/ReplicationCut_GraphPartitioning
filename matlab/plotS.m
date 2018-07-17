%clear worskspace variables, close all graphs, clear command window
clc

% number of vertices. Change this number!
n = 15;

% file name. Change this file!
sFilename = 'distribution_s_GraphFolder_15_200_1000.txt';

sFileID = fopen(fullfile(folder, sFilename), 'rt');

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
surf(Z);
xlabel('# of Min Cuts');
ylabel('# of nodes on left side of cut');
zlabel('Percentage of instances');
    
fclose(sFileID);