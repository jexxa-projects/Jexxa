
## QuickStart - HelloWorld
https://www.graalvm.org/java/quickstart/

## Build with graalvm 
* download / install graalvm https://www.graalvm.org/downloads/
                         
* XCODE (unter Macosx) muss installiert sein. Sonst Fehlermeldung, dass Foundation/Foundation.h nicht gefunden wird!! Es kann gut sein, dass auf anderen System ebenfalls System spezifische Entwicklungsumgebungen installiert werden müssen. 

* set JAVA_HOME and path to graalvm directory
  * export JAVA_HOME=/Volumes/WorkspaceMac/Entwicklung/Java/graalvm-ce-java17-21.3.0/Contents/Home
      * export PATH=$JAVA_HOME/bin:$PATH

      *   michael@Michaels-Mini HelloJexxa % echo $JAVA_HOME
          *   /Volumes/WorkspaceMac/Entwicklung/Java/graalvm-ce-java17-21.3.0/Contents/Home
      *   michael@Michaels-Mini HelloJexxa % echo $PATH
          *   /Volumes/WorkspaceMac/Entwicklung/Java/graalvm-ce-java17-21.3.0/Contents/Home:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/MacGPG2/bin:/Volumes/WorkspaceMac/Entwicklung/Java/jdk-11.0.2.jdk/Contents/Home/bin/:/opt/local/bin:/Volumes/WorkspaceMac/Applications/anaconda3/bin/:/opt/local/sbin:/Volumes/WorkspaceMac/Applications/apache-maven/bin/

gu install native-image

*   mvn clean install

*   Resources require explicit config file -> https://www.graalvm.org/reference-manual/native-image/Resources/ 
    *   michael@Michaels-Mini HelloJexxa % java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar target/hellojexxa-jar-with-dependencies.jar

    * Entfernen aus src/main/resources/META-INF/native-image/reflect-config.json  {
        "name":"org.graalvm.compiler.hotspot.management.AggregatedMemoryPoolBean",
        "allPublicConstructors":true
    
      },
    
    *   In tutorial HelloJexxa nochmal 
        michael@Michaels-Mini HelloJexxa % mvn clean install -e (damit reflection stuff eingebunden wird) 

## Works
* michael@Michaels-Mini HelloJexxa % ./target/hellojexxa-native
* michael@Michaels-Mini ~ % curl -X GET http://localhost:7500/HelloJexxa/greetings
    "Hello Jexxa"%

* Michaels-Mini:HelloJexxa michael$ curl -k -X GET https://localhost:8081/HelloJexxa/greetings
"Hello Jexxa"(base) Michaels-Mini:HelloJexxa michael$

## Known issues

* Static HTML files (included in jar) do not work 
* JMX Access does not work at least when using JConsole

## Untested
*   Everything else 