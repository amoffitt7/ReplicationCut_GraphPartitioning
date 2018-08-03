%clear command window
clc

% Files are in the current directory. Change this!
folder = cd;

% number of vertices. Change this number!
n = 20;
% the number of edges. Change this number!
edges = 20;
% the graph file. Change this!
graphFile = 'graph20_20_1.txt';

% S or R. Change!
set = 's';

% file name. Change this file!
sFilename = sprintf('new_%s_GraphFolder_%d_%d_1000.txt', set, n, edges);

sFileID = fopen(fullfile(folder, sFilename), 'rt');

% start scanning from file
SetSize = [];
NumberOfCuts = [];
fprintf('Scanning for file... \n');
while (~feof(sFileID))  
    graphTitle = textscan(sFileID,'%s',1);
    theTitle = graphTitle{1,1}{1,1};
    toCheck = sprintf('%s:', graphFile);
    % if this is the graph we're looking for
    if strcmp(toCheck, theTitle) == 1
        moreText = textscan(sFileID,'%s',2); % get text out of the way
        mincuts = textscan(sFileID,'%d',1);
        mincuts = mincuts{1,1};
        moreText = textscan(sFileID,'%s',6); % get text out of the way
        data = textscan(sFileID, '%d %d','Delimiter','|');
        SetSize = double(data{1});
        NumberOfCuts = double(data{2});
        break;
    end
    moreText = textscan(sFileID,'%s',9);
    data = textscan(sFileID, '%d %d','Delimiter','|');
end

fprintf('Done. \n');
figure;
hold on;
[expfit, expGoodness] = fit(SetSize,NumberOfCuts,'exp1');
% extract coefficients
coeff = coeffvalues(expfit);
a = coeff(1);
b = coeff(2);
expEquation = sprintf('%.2f*e^{%.2fx}',a,b);
[p, pGoodness] = polyfit(SetSize, NumberOfCuts, 2);
c = p(1);
d = p(2);
f = p(3);
polyEquation = sprintf('%.2fx^2 + %.2fx + %.2f',c,d,f);
f = polyval(p,SetSize);

% plot data
plot(SetSize, NumberOfCuts, 'LineStyle', '--');
plot(expfit);
plot(SetSize, f, 'g');

% labeling stuff

theXLabel = sprintf('%s Size', upper(set));
xlabel(theXLabel);
ylabel('Number of Cuts');
ylim([0,inf]);
legend('Number of Cuts', expEquation, polyEquation);
plotTitle = sprintf('%d vertices, %d edges, %d min cuts', n, edges, mincuts);
title(plotTitle);

hold off;
fclose(sFileID);