# AMI Tutorial

* AMI does not yet work with URLs but will RSN. Examples are provided, but download any additional files that you want to work with and put them in a directory (remember where it is, because you will need to tell AMI).
* All exercises are from the commandline. By default this will be run from AMi's bin directory.https://bitbucket.org/petermr/ami/wiki/AMI_Tutorial
* The examples are biological, but don't worry. 
* Windows people will use *.zip, the others *.tar.gz

* ''This tutorial dates from 2013-11 and may be outdated in places''. The main site is now https://bitbucket.org/petermr/ami-core/

## installation

OUTDATED (we now also use *.deb)

find https://bitbucket.org/petermr/xhtml2stm-dev/downloads and download your file. 

     $ cd /some/where/convenient/that/doesnt/matter e.g.:
     $ C:\Users\pm286\amitest You'll probably want to delete this after the exercise 

Unzip/untar the file and navigate to 

    $ cd C:\Users\pm286\amitest\xhtml2stm-0.1.2-SNAPSHOT-bin\xhtml2stm-0.1-SNAPSHOT
    
# navigation

    $ 'ls' OR 'dir' should show:
	$             bin/ exampleData/ and (ignore) repo/ 
    $ cd exampleData
    $ ls OR dir should show html/ and pdf/ and maybe more
    $ cd pdf 
    $ ls or dir should show
    $    multiple-1471-2148-11-312.pdf
    $    tree-1471-2148-11-313.pdf
    $ cd ..
    $ cd html
    $ ls or dir should show pb1.html and multiple.312.html
    $ cd ..
    $ cd ..
    

    $ cd bin

if you now list the files (dir or ls) you should see:

    $ chem
    $ chem.bat
    $ ...
    $ species
    $ species.bat
    ...
we are going to use species and sequence for most of the exercises.
    
## running

We suggest you run from within the bin/ directory. If you have extract files, put them in html/or pdf/

 Type: 
 [Updated - normally '''ami-species''' etc.]

    $ species

and you'll see the help. now type:

    $ species -i ../exampleData/html/multiple.312.html -o output312.xml

This will create a new file in bin , output312.xml. (If you are confident with filesystems we suggest you put it somewhere else to be tidy).

Now you can vary the options in ami:

### commands

    $ **species** does Binomial names in italics (Genera coming soon)
    $ **sequence** works on DNA-containing text (it will be extended to use RNA, proteins, soon)

