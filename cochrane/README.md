# Cochrane workshop 

Workshop on 2015-03-16 in the Cochrane Foundation in Oxford UK to develop strategy and tools for analysing clinical trial documentation.
(All materials are openly licensed CC-BY or Apache2 - in essence do what you want, including making and distributing derivatives,  but give contentmine.org credit).

Some materials for this workshop are included in the `ami-plugin` project (see https://bitbucket.org/petermr/ami-plugin/ ).

## Overview

### scraping and normalizing 

First you will need to scrape and normalize a number of files. (To get you started with ami we have created a set of examples/ in case
you aren't connected).

### quickscrape

You'll need to be connected to the net and know the URLs of the web-pages you want to scrape. Here's what I used:
 * I copied `trialsjournal.json` to my machine
 * `cd` to the directory where the results would be


```
quickscrape \
    -u http://www.trialsjournal.com/content/16/1/1 \
       http://www.trialsjournal.com/content/16/1/2 \
       http://www.trialsjournal.com/content/16/1/3 \
    -s /Users/pm286/workspace/journal-scrapers/scrapers/trialsjournal.json
    
which will download 3 articles from a list. The output 
is in 3 directories
```
http_www.trialsjournal.com_content_16_1_1
http_www.trialsjournal.com_content_16_1_2
http_www.trialsjournal.com_content_16_1_3
```
If the scrape failed the directory (of type `QSN`) may be empty. Otherwise it will contain some or all of the following:
```
results.json
fulltext.pdf
fulltext.xml
fulltext.html
```
If it doesn't contain `fulltext.xml` we shan't be able to process it in this tutorial. (We do have tools for 
converting PDF but they are necessarily ucky and no fun for newcomers. We are bundling them up to make them easier).

### normalization with `norma`

The scraped XML files have to be normalized (converted to conformant HTML - `scholarly.html`). This requires a different stylesheet for each journal. The good news is that many publishers use the same flavour of XML ("NIH" 'NLM" "JATS"). BMC - which publishes its own flavour needs a different stylesheet and we've written this (`bmc2html`)

```
norma -q http_www.trialsjournal.com_content_16_1_3/ -i fulltext.xml -o scholarly.html --xsl bmc2html
```
This will take a single scraped directory (`http_www.trialsjournal.com_content_16_1_3/`) and using the stylesheet `bc2html` convert the `fulltext.xml` into `scholarly.html` in the same directory

To avoid huge commandlines we can also process a directory *containing* several `QSNs`. This will convert all of the contained 
files, e.g.
```
norma -q examples/ -i fulltext.xml -o scholarly.html --xsl bmc2html
```
#ami

`ami-plugin` provides a extensible architecture for any number of "plugins".

There are two `ami` programs used for analysing documents. Programs are called from the commandline (e.g.
```
ami-regex <optional_arguments>
```
or
```
ami-words <optional_arguments>
``
If the arguments are omitted, HELP will be output to the console (`sysout`)

### ami-words

`ami-words` will carry out operations on the words in a document. It can list them, calculate the frequencies and summarize the results. It can also compare documents and classify them.

Examples are given in `wordsexamples.sh`. If you run this file (with `sh wordsexamples.sh`) then it will run all the examples

Read the file for explanations, but in summary it should create new files in our output directory (e.g. 
`target/wordstemp` or `examplestemp`) 

#### per article frequency analysis

`target/wordstest/http_www.trialsjournal.com_content_16_1_19/results/words/frequencies` which will contain
 *   results.xml (machine-readable)
 *   results.html (human-readable)

#### overall frequency analysis

In the example given the system writes summary frequencies to `target/examples/` and the results are in `booleanFrequency.html`

<<<<<<< Local Changes

=======
#### variations

By varying the stopwords parameter you can require different words to be omitted. We have lists for common english words 
(`stopwords`) and the commonest words in `trialsjournal.com`. If there are further words you would like to omit, then create your own stopwords file and add it to the list of `--w.stopwords` arguments.
>>>>>>> External Changes
