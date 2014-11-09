# Download, installation and running on Ubuntu/Debian:

All downloads can be found [here](https://bitbucket.org/petermr/xhtml2stm-dev/src/08af03c19db30b20fc3d5192cc0fa50df336fa82/AMI.md?at=default)

Download and extract:

    $ cd
   	$ wget https://bitbucket.org/petermr/xhtml2stm-dev/downloads/xhtml2stm-dev-0.3-SNAPSHOT-bin.tar.gz
	$ tar xfvz xhtml2stm-dev-0.3-SNAPSHOT-bin.tar.gz
	$ cd xhtml2stm-dev-0.3-SNAPSHOT/bin/

You now have various commands for the various visitors:

    $ chem
    $ chem.bat
    $ ...

    $ species
    $ species.bat

# Running from command line

These are simple, powerful commandline apps that saves users from complications. The form is:

    $ command -i <inputspec> -o <outputspec> -x [extensions] -r

The processor guesses as much as possible and uses defaults.

    $ -i   --input      [file]       take input from file or directory
    $ -o   --output     [file]       put output in file or directory
    $ -r   --recursive               recurse through input directories
    $ -x   --extensions  ex1 ex2 ..  list of input extensions

The commands are prepared and driven by the applications (plugins/visitors):

    $ chem         extract chemistry
    $ regex        general regular expressions (can be used for identifiers, and much more)
    $ sequence     extracts sequences (default DNA)
    $ species      extracts species (default binomial names in italics)
    $ tree         extracts phylogenetic trees from diagrams

a typical usage. Use "/" on all OS. Note directories are closed with trailing "/"):

    $ species    -i  foo/myfile.html -o mydir/species/bar.xml    !extracts species into mydir/species/bar.xml
    $ species    -i  foo/myfile.html -o mydir/species/          !extracts species into mydir/species/myfile.xml
    $ species    -i  foo/bar/ -x htm html -o mydir/bar.xml      !extracts all *.htm and *.html under foo/bar/

# Species example with an XML feed of a paper

Now, execute the species visitor on some supplied example XML data. This is the [XML feed](http://www.plosone.org/article/fetchObjectAttachment.action;jsessionid=222E9669F4EED8371D02B706D6A5FC69?uri=info%3Adoi%2F10.1371%2Fjournal.pone.0077058&representation=XML) form of the following 
PLOS ONE [paper](http://dx.doi.org/10.1371/journal.pone.0077058)

	./species -i ../exampleData/species/journal.pone.0077058.xml

Change to the newly created output directory:

	cd ../exampleData/species/journal.pone.0077058/

Observe the extracted species output in XML format:

	cat results.xml




