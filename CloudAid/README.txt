CloudAid

README
CloudAid is the core application for the CloudAid project. It is the prototype applciation for the cloud service Decision-based dicovery and aggregation system. 
In order to work the prototype need also the Decision Methods JAHP and JSAW both can be downloaded here: https://github.com/jorgearj/CloudAid/tree/master/CloudAid/DecisionMEthods

These method are the simulation of external tools. However, since they communicate though the filesystem they must be places in the same folder as the CloudAid Aplication.

INSTRUCTIONS:
	1 - Download the CloudAid jar file and all the Decision Methods jar files.
	2 - Put all the jar files in the same installation folder
	3 - run both the Decision Methods jar files
		java -jar JAHP.jar
		java -jar JSAW.jar
	4 - run the CloudAid jar file
		- java -jar CloudAid.jar (alternative running command are explained in this file)
	5 - Follow the instructions of the CloudAid App

Note: You should only close the Decision MEthods when the CloudAid application has quit. Otherwise you wont be able to finish your Cloud Service Aggregation process.

Installation folder:
	- CloudAid.jar
	- Services - put your service set
	- JAHP.jar
	- JSAW.jar

After the first run you should have somethig like this:
	- CloudAid.jar
	- JAHP.jar
	- JSAW.jar
	- Services - folder to put the service set (turtle or RDF/XML files)
	- Decision - where the output of the Decision methods will be stored (no need for user interaction)
		- AHP
		- SAW
	- TO_Decide - where the input of the Decision methods will be stored (no need for user interaction)
		- AHP
		- SAW


ALTERNATIVE CloudAid Run Commands:
By executing the default command:
	java -jar CloudAid.jar

The application will run with its default mode: Shell model and no incomparability support algorithm. 
Three possible arguments are available:

	java -jar CloudAid.jar <scenario> <decisionMethod> <algorithm>

By ommiting the <algorithm> argument the default is the no incomparability support algorithm. The two options are:
	- noinc -> for the no incomparability support algorithm
	- inc   -> for the incomparability support algorithm

The <scenario> argument specifies the test scenario to be used. This mode will skip the data insertion meu and will run with one of the predefined scenarios. The options are:
	- usecase -> Runs the application with the Use Case data scenario
	- heroku  -> Runs the application with the Heroku data scenario
	- reqs    -> Runs the application with the data scenario for testing multiple requirements numbers
	- norm	  -> Runs the application with the data scenario for testing all the normalization processes

The <decisionMethod> argument specifies which decision method will be used. The options are:
	- saw -> to use the Simple Additive Weighting method
	- ahp -> to use the Analytic Hierarchic Process method

Note that when using the <scenario> argument one has to also specify the <decisionMethod>. These two arguments must also be used together. The <algorithm> argument can be ommited.

Examples:
	java -jar CloudAid.jar usecase ahp inc   -> this command will run the usecase scenario with the AHP method and the incomparability algorithm.

	java -jar CloudAid.jar inc 				 -> runs the default configuration (shell mode) with the incomparability algorithm

	java -jar CloudAid.jar heroku SAW 		 -> runs the heroku scenario with the SAW method.

Note that these scenarios are for testing purposes.

CONTACT:

Jorge Araújo

e-mail: jorge.arj@gmail.com