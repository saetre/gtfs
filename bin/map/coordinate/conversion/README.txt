#From
https://www.ibm.com/developerworks/java/library/j-coordconvert/

# From PROLOG Busstuc
tell('decimalDegreeStations.txt'),
statcoord2(ID,Comm,Name,North,East),North=\=0,
write(Name),write('\t'),
write(North),write('\t'),
write(East),nl,fail ; told.
