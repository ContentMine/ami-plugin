
== Making the commandline utilities ==

build as normal

 $ mvn clean install

This should create the following in '''target''':

'''
ami-core_0.1~SNAPSHOT_all.changes
ami-core_0.1~SNAPSHOT_all.deb
ami-core-0.1-SNAPSHOT-bin.tar.gz
ami-core-0.1-SNAPSHOT-bin.zip
ami-core-0.1-SNAPSHOT.jar
'''

=== Running the *.deb file ===

If you are on a Debian-compatible platform (including our VirtualBox/Vagrant)
 then install the *.deb file. That will create commandline options of the form:
 '''
 ami-species
 ami-regex
 ami-chem
 ami-sequence
 ami-phylo
 '''

Typing 
'''
 $ sh ./regex  -i ../../src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml -o target/junk1/ -g ../../regex/metadata.xml
 '''
 
 === **outdated** ===
now package this:

 $ mvn package

this should create :
 
 $ target/appassembler/bin

copy this from target:

 $ cp -r target/appassembler ./

now 
 
 $ cd appassembler/bin

and run a typical command

 $ sh ./regex  -i ../../src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml -o target/junk1/ -g ../../regex/metadata.xml

This will create the results in 

 $  target/journal.pone.0077058.xml/results.xml

whose contents should look like:

 $ <results xmlns="http://www.xml-cml.org/xhtml2stm"><result xmlns="" doi="0077058" count="11" /></results>



 $ <results xmlns="http://www.xml-cml.org/xhtml2stm"><result xmlns="" doi="0077058" count="11" /></results> 
