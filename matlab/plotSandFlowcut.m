%clear worskspace variables, close all graphs, clear command window
close all; clc

% number of vertices. Change this number!
n = 20;

% edges to iterate through
for i = 2:7

% increment
edges = i * 10;
    
% file names. Change these files!
sFilename = sprintf('distribution_s_GraphFolder_20_%d_1000.txt', edges);
rFilename = sprintf('distribution_flowcut_s_GraphFolder_20_%d_1000.txt', edges);

sFileID = fopen(fullfile(folder, sFilename), 'rt');

S_text = textscan(sFileID,formatSpec,3,'Delimiter','|');

%gets info from the S file
figure;
set(gcf, 'PaperPosition', [2 1 4 2]);
subplot('Position', [0.05 0.2 0.45 0.55]);
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
title('S Sizes for Replication Cut');
    
fclose(sFileID);

rFileID = fopen(fullfile(folder, rFilename), 'rt');

R_text = textscan(rFileID,formatSpec,3,'Delimiter','|');

%gets info from the R file
subplot('Position', [0.6 0.2 0.45 0.55]);
RnumberOfColumns = n * (n-1) / 2;
RnumberOfRows = n - 1;
RZ = zeros(RnumberOfRows, RnumberOfColumns);
while (~feof(rFileID))  
    R_T = textscan(rFileID, '%d %d %d','Delimiter','|');
    RMinCutNumber = double(R_T{1});
    RSize = double(R_T{2});
    RNumberOfCuts = double(R_T{3});
    if isempty(RMinCutNumber) == 0
        Rx = RMinCutNumber(1);
        %normalize number of cuts
        RV = RNumberOfCuts/sum(RNumberOfCuts);
        RZ(:,Rx) = RV;
    end
    eob = textscan(rFileID,'%s',1,'Delimiter','\n'); 
end
s2 = surf(RZ);
s2.FaceAlpha = 0;
xlabel('# of Min Cuts');
ylabel('# of nodes on left side of cut');
zlabel('Percentage of instances');
title('S Sizes for Flow Cuts');
    
fclose(rFileID);

end