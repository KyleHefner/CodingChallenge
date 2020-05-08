# CodingChallenge

Summary: This is a java program that can read a csv file as a command line argument and insert each record into a sql .db file.
The csv file must contain records that have exactly 10 fields. If a record doesn't have 10 fields, then it is returned to 
a bad csv file. At the end of processing, some statistics will be written to a log file. The statistics are: # of total records, # of bad records, and # of successful records.


Getting Started:
I suggest that you use the Eclipse IDE because that is what I used. Download or clone this repository and open it in Eclipse. 
When you open the project, the only two things you should be worried about are src/main/java/ and the popm.xml file. Make sure
in the pom.xml file that sqlite-jdbc and opencsv have been added as dependencies. in src/main/java there should be a package called
proj.CodingChallengeDemo and in that package there is App.java and input.csv. App.java is the source code for the program and is the 
only thing you need to edit/observe when using the program. There have been comments written throughout the file to help with understanding.
To run the program correctly, you need to first add a command line argument. To do this, go to Run -> Run Configurations. Once there 
navigate to the (x)=Arguments tab and add the name of the csv file you want to use as input. Note, your input csv file
must be in the same package as App.js for the program to run correctly. I am not sure of how to add command line arguments using
other IDE's but I'm sure it is a simple google search. Once you do this, you can now successfully run the program. You will know the 
program is done when either an error occurs or 'Done.' is printed on the console. If everything goes well and and there isn't an error, 
you should be able to refresh your project structure by pressing F5, and some new files should appear. A .db file, a log file, and 
a bad csv file. 


Overview:
I decided to make this a maven project so that I could easily use 3rd party libraries.
One design choice that I made was to separate this program into many functions to be 
easily readable and informative with good method names. 
Another design choice was to use the 3rd party library Opencsv instead of core java to read and write to csv files.
This helped because I was better able to gracefully handle different kinds of problematic csv fields such as 
having commas in the field itself. Without Opencsv I would have to come up with a complicated regex to use in 
a string.split() method which would make the code less clear.
