Results of commandlines that worked on PMR's machine on 2014-10-03
==================================================================

This is to show that all 4 ami-* work although messily. 


Output from ami-tree
====================


kapi:xhtml2stm pm286$ sh target/appassembler/bin/ami-tree -i ./src/test/resources/org/xmlcml/xhtml2stm/tree/ijsem/ijs.0.014126-0-000.pbm.png 


0    [main] INFO  org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output filenames from input
185  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - in: visitableList: [org.xmlcml.xhtml2stm.visitable.image.ImageVisitable@2acd0c52]
inputArg: ./src/test/resources/org/xmlcml/xhtml2stm/tree/ijsem/ijs.0.014126-0-000.pbm.png
extension: png
extensions: [Ljava.lang.String;@7be3f171
isDirectory: false
recursive: false

185  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - out: null
185  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target
185  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target
185  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
185  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename ijs.0.014126-0-000.pbm
185  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - writing: target/ijs.0.014126-0-000.pbm.xml
185  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - InputVisitables 1
185  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - outfileList []
220  [main] DEBUG org.xmlcml.xhtml2stm.visitor.tree.TreeVisitor  - writing to output File: target/ijs.0.014126-0-000.pbm.png.nwk
777  [main] DEBUG org.xmlcml.image.ImageProcessor  - image BufferedImage@7ddeef8a: type = 10 ColorModel: #pixelBits = 8 numComponents = 1 color space = java.awt.color.ICC_ColorSpace@37408d95 transparency = 1 has alpha = false isAlphaPre = false ByteInterleavedRaster: width = 1183 height = 697 #numDataElements 1 dataOff[0] = 0
1063 [main] DEBUG org.xmlcml.image.pixel.PixelIslandList  - created graphs: 3[; edges: ; nodes: [<(548,15)><(611,50)><(510,84)><(458,119)><(524,154)><(460,257)><(464,223)><(565,188)><(488,292)><(506,362)><(551,327)><(485,397)><(525,431)><(515,500)><(638,466)><(562,535)><(386,605)><(585,569)><(639,659)><(344,427)><(356,156)><(385,240)><(408,318)><(375,214)><(207,501)><(488,32)><(233,433)><(394,97)><(325,332)><(451,58)><(351,237)><(367,457)><(398,483)><(423,344)><(428,136)><(169,553)>], ; edges: ; nodes: [<(41,209)><(41,222)><(135,222)><(135,209)><(42,216)><(135,216)>], ; edges: ; nodes: [<(908,353)><(883,372)><(895,365)><(887,365)>]]
1063 [main] DEBUG org.xmlcml.image.pixel.PixelIslandList  - pixelGraphList: 3
1063 [main] DEBUG org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer  - GraphList 3
1063 [main] DEBUG org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer  - Making NEWICK
1084 [main] DEBUG org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer  - NEWICK: (((585_569:0.58,(562_535:0.5,((((565_188:0.29,(464_223:0.12,460_257:0.11):0.02):0.03,((524_154:0.15,458_119:0.05):0.05,((611_50:0.19,548_15:0.09):0.06,510_84:0.09):0.09):0.06):0.01,((506_362:0.13,551_327:0.19):0.02,488_292:0.12):0.09):0.04,((525_431:0.24,(515_500:0.18,638_466:0.37):0.05):0.04,485_397:0.21):0.03):0.14):0.04):0.06,386_605:0.33):0.25,639_659:0.97):0.03;
1102 [main] DEBUG org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer  - wrote Newick String to /Users/pm286/workspace/xhtml2stm/target/ijs.0.014126-0-000.pbm.png.nwk
1103 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target/ijs.0.014126-0-000.pbm.xml
1103 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target/ijs.0.014126-0-000.pbm.xml
1103 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
1104 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename ijs.0.014126-0-000.pbm
1104 [main] ERROR org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - no visitableOutput fileList
1104 [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output file target/ijs.0.014126-0-000.pbm.xml/results.xml // <results xmlns="http://www.xml-cml.org/xhtml2stm"><nexml xmlns="http://www.nexml.org/2009" xmlns:nex="http://www.nexml.org/2009" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:svgx="http://www.xml-cml.org/schema/svgx"><otus id="tax1" label="RootTaxaBlock" /><trees label="TreesBlockFromXML" id="Trees" otus="tax1"><tree id="tree1" label="tree1" xsi:type="FloatTree" /></trees></nexml></results>
okapi:xhtml2stm pm286$ 

Output from ami-species
=======================

okapi:xhtml2stm pm286$ sh target/appassembler/bin/ami-species -i ./src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml 

0    [main] INFO  org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output filenames from input
142  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - in: visitableList: [org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable@22a34435]
inputArg: ./src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml
extension: xml
extensions: [Ljava.lang.String;@cd30557
isDirectory: false
recursive: false

142  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - out: null
142  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target
142  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target
142  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
142  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename journal.pone.0077058
142  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - writing: target/journal.pone.0077058.xml
142  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - InputVisitables 1
142  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - outfileList [./src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml]
175  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - container: org.xmlcml.xhtml2stm.visitable.xml.XMLContainer@7b6b89ad
289  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - results: [Jeyawati rugoculus, Magnapaulia laticaudus, Bactrosaurus johnsoni x 3, Probactrosaurus gobiensis, Protohadros byrdi, Iguanodon bernissartensis x 2, Shuangmiaosaurus gilmorei, Eolambia caroljonesa x 2, Yunganglong datongensis x 12, Levnesovia transoxiana, Nanyangosaurus zhugeii, Saurolophus osborni, Velociraptor mongoliensis, Glishades ericksoni, Microceratops gobiensis, Parasaurolophus walkeri x 3, Hadrosaurus foulkii]
295  [main] DEBUG org.xmlcml.xhtml2stm.visitor.species.SpeciesVisitor  - =============/Users/pm286/workspace/xhtml2stm/./src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml============
296  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target/journal.pone.0077058.xml
296  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target/journal.pone.0077058.xml
296  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
296  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename journal.pone.0077058
296  [main] ERROR org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - no visitableOutput fileList
296  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output file target/journal.pone.0077058.xml/results.xml // <results xmlns="http://www.xml-cml.org/xhtml2stm"><source name="10.1371/journal.pone.0077058"><speciesList><species count="1">Jeyawati rugoculus</species><species count="3">Parasaurolophus walkeri</species><species count="2">Eolambia caroljonesa</species><species count="1">Magnapaulia laticaudus</species><species count="12">Yunganglong datongensis</species><species count="1">Probactrosaurus gobiensis</species><species count="1">Protohadros byrdi</species><species count="3">Bactrosaurus johnsoni</species><species count="1">Shuangmiaosaurus gilmorei</species><species count="1">Nanyangosaurus zhugeii</species><species count="1">Levnesovia transo</species><species count="2">Iguanodon bernissartensis</species><species count="1">Saurolophus osborni</species><species count="1">Velociraptor mongoliensis</species><species count="1">Glishades ericksoni</species><species count="1">Microceratops gobiensis</species><species count="1">Hadrosaurus foulkii</species></speciesList></source></results>
okapi:xhtml2stm pm286$ 

Output from ami-chem
====================

okapi:xhtml2stm pm286$ sh target/appassembler/bin/ami-chem -i ./src/test/resources/org/xmlcml/xhtml2stm/molecules/image.g.2.13.svg

0    [main] INFO  org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output filenames from input
239  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - in: visitableList: [org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable@3068f00]
inputArg: ./src/test/resources/org/xmlcml/xhtml2stm/molecules/image.g.2.13.svg
extension: svg
extensions: [Ljava.lang.String;@23964a8d
isDirectory: false
recursive: false

239  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - out: null
240  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target
240  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target
240  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
240  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename image.g.2.13
240  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - writing: target/image.g.2.13.xml
240  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - InputVisitables 1
240  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - outfileList [./src/test/resources/org/xmlcml/xhtml2stm/molecules/image.g.2.13.svg]
240  [main] INFO  org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - ChemVisitor: now visiting an SVGVisitable
240  [main] INFO  org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - SVGContainer name: /Users/pm286/workspace/xhtml2stm/./src/test/resources/org/xmlcml/xhtml2stm/molecules/image.g.2.13.svg
240  [main] INFO  org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - Working with svgContainer: /Users/pm286/workspace/xhtml2stm/./src/test/resources/org/xmlcml/xhtml2stm/molecules/image.g.2.13.svg
1658 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.MoleculeCreator  - Looking for reactions
2067 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.MoleculeCreator  - Molecule potentially part of a reaction ((384.63977442374846,459.1559855762515),(508.7623916951517,554.7226083048484)) 16 C1(C(=C(OC(=C1[H])C(O[H])([H])[H])[H])O[H])=O
2067 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - reactions.size() == 0; creating molecules
2067 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.MoleculeCreator  - Looking for molecules
2254 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.MoleculeCreator  - Molecule ((384.63977442374846,459.1559855762515),(508.7623916951517,554.7226083048484)) 16 C1(C(=C(OC(=C1[H])C(O[H])([H])[H])[H])O[H])=O
2277 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - Saving molecules
2278 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - Writing CMLMolecule
2278 [main] INFO  org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - Writing CMLMolecule to file: /Users/pm286/workspace/xhtml2stm/target/image.g.2.13.svg.molecule0
3004 [main] DEBUG org.xmlcml.xhtml2stm.visitor.chem.ChemVisitor  - Creating clickable HTML
3022 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target/image.g.2.13.xml
3022 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target/image.g.2.13.xml
3022 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
3022 [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename image.g.2.13
3022 [main] ERROR org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - ***WARNING results element is null
okapi:xhtml2stm pm286$ 

created
okapi:xhtml2stm pm286$ ls -lt target/image.g.2.13.svg.molecule0.*
-rw-r--r--  1 pm286  staff  9181  3 Oct 17:04 target/image.g.2.13.svg.molecule0.png
-rw-r--r--  1 pm286  staff  5811  3 Oct 17:04 target/image.g.2.13.svg.molecule0.svg
okapi:xhtml2stm pm286$ 

Output from regex
=================

okapi:xhtml2stm pm286$ sh target/appassembler/bin/ami-regex -i "src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml" -g regex/metadata.xml

0    [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - running RegexVisitor 4
102  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.CompoundRegex  - read Compound Regex: pdb
<regex weight="1.0" url="http://contentmine.org/metadata" fields="doi" title="doi">.*10\.1371/journal\.pone\.(\d{7}).*</regex>; .*10\.1371/journal\.pone\.(\d{7}).*; [doi];  
102  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - regex container [<regex weight="1.0" url="http://contentmine.org/metadata" fields="doi" title="doi">.*10\.1371/journal\.pone\.(\d{7}).*</regex>; .*10\.1371/journal\.pone\.(\d{7}).*; [doi];  ]
102  [main] INFO  org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output filenames from input
155  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - in: visitableList: [org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable@70ccbb35]
inputArg: src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml
extension: xml
extensions: [Ljava.lang.String;@1116ce6e
isDirectory: false
recursive: false

155  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - out: null
155  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target
155  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target
156  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
156  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename journal.pone.0077058
156  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - writing: target/journal.pone.0077058.xml
156  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - InputVisitables 1
156  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - outfileList [src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml]
212  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - visiting container with  1 compound regexes
212  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - search XomElement with 1 compoundRegexes
212  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.CompoundRegex  - Searching element with regexComponentList
225  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.CompoundRegex  - with: <regex weight="1.0" url="http://contentmine.org/metadata" fields="doi" title="doi">.*10\.1371/journal\.pone\.(\d{7}).*</regex>; .*10\.1371/journal\.pone\.(\d{7}).*; [doi]; 
489  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - Hits: 1
489  [main] DEBUG org.xmlcml.xhtml2stm.visitor.regex.RegexVisitor  - .*10\.1371/journal\.pone\.(\d{7}).*: 11
490  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - outputFile: target/journal.pone.0077058.xml
490  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - output target/journal.pone.0077058.xml
490  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - mkdirs
490  [main] DEBUG org.xmlcml.xhtml2stm.visitor.VisitorOutput  - basename journal.pone.0077058
490  [main] ERROR org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - no visitableOutput fileList
490  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - creating output file target/journal.pone.0077058.xml/results.xml // <results xmlns="http://www.xml-cml.org/xhtml2stm"><result xmlns="" doi="0077058" count="11" /></results>

