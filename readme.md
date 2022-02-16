# camel-demo

## Overview
This projects aims to demo simple file transfers and transformation using apache camel. 

## Approach 
The problem is basic with basic solution, using camel file routes to listen to source folder and finally producer template to dynamically create file route to write data in partitioning manner.
 
## Stack
- apache camel
- gradle

## Configuration (Non Camel)
You can export environment variables to set source and sink folders
- export SOURCE_FOLDER=./source
- export SINK_FOLDER=./sink

default value is in place, SOURCE_FOLDER=source and SINK_FOLDER=sink

## Run using gradle
In root project directory
```
./gradlew clean build
./gradlew bootRun
```
Then on a separate terminal you can write json files to the source folder.

## Testing
I have provided data under **testdata folder** from root directory
Once you have run the project using **gradlew bootRun**, then you can just copy all the test data json files into **source folder**
and then you can watch **sink folder** on its respective partitions getting populated.

## Assumptions
- source folder data will come from external vendor/tool which will feed json profiles to source folder
- json profiles is expected to have the minimum set of fields declared in the agreed business doc.
- height will be in Foot and weight will be in kilograms
- both source and sink folder is expected to be accesable on the current machine the integration is running on. 
- file formats of source json files will be in {userId}.json, otherwise it will not be processsed. 

## Rollbacks
if theres any errors occured the original file will be retried and will not be moved to any failed folders.

## Performance
- running the current design is not production friendly
- We cant scale the design because doing so will cause IO Errors
- even if we can, IO is always slow specially if you are not using ssd 
- depending on the volume of data then running the integration for the first time(or if you run the integration using external scheduler) against an already populated source folder might exlode specially if you attempt to read them simultainously 

## Support
If theres any need, reach out to ortizmark905@gmail.com or create an issue to this repo.


