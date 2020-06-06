Plugins Style

cd framework<br />
javac *.java<br />
jar cfm framework.jar manifest.mf *.class<br />
cd ../app1<br />
javac -cp ../framework/framework.jar *.java<br />
jar cf app1.jar *.class<br />
cd ../app2<br />
javac -cp ../framework/framework.jar *.java<br />
jar cf app2.jar *.class<br />
cd ../deploy<br />
cp ../framework/*.jar ../app1/*.jar ../app2/*.jar .<br />
java -jar framework.jar ../../../pride-and-prejudice.txt
