## Post-analysis of MIGMAP output

**IMPORTANT** MIGEC built-in analysis module requires saving a binary version of output file, which can be done by running

```bash
java -Xmx8G -jar migmap.jar -R IGH -S human --write-binary out.bin sample.fastq.gz out.txt
```

Note that ``--write-binary`` option cannot be used together with ``--by-read``.

To summarize somatic hypermutations and generate clonotype trees run

```bash
java -Xmx8G -jar migmap.jar com.antigenomics.migmap.Analyze out.bin out
```

This folder contains analysis results for IGH repertoire of hypermutating Raji cell line:

- Reads stored in ``raji_R12.fastq.gz``.. actually, those are not raw reads, but error-corrected assembled consensuses, see [MIGEC](https://github.com/mikessh/migec).
- Clonotype table and binary output are stored in ``raji_R12.txt`` and ``raji_R12.bin``.

### Analysis of somatic hypermutation profile

![Rmd SHM analysis](https://github.com/mikessh/migmap/blob/develop/post/analyze_shm.png)

Files:

- ``raji_R12.shm.txt`` - mutation table
- ``analyze_shm.Rmd`` - analysis template

The whole analysis template is stored in R markdown format, which means it can be loaded to [Rstudio](http://rmarkdown.rstudio.com/), customized and executed. Detailed explanation of all analysis steps and plots is embedded into the template. An example PDF output is shown in ``analyze_shm.pdf`` (click [here](https://github.com/mikessh/migmap/blob/develop/post/analyze_shm.pdf) to view it).

### Clonotype tree

![Rmd SHM analysis](https://github.com/mikessh/migmap/blob/develop/post/raji.png)

Files:

- ``out.net.txt`` - network graph
- ``out.node.txt`` - node attributes
- ``out.edge.txt`` - edge attributes

The analysis can be entirely performed in [Cytoscape](http://www.cytoscape.org/) software as follows.

#### Loading network

Go to ``File>Import>Network>File..`` and import ``out.net.txt``. Specify ``from`` and ``to`` columns as ``source`` and ``target``.
Go to ``File>Import>Table>File..`` and import ``out.node.txt`` and ``out.edge.txt``. Select ``Node Table columns`` and ``Edge Table columns`` respectively under ``Import Data as..`.

#### Specify parameter mapping

For nodes:

- Node size: continuous mapping for ``freq`` attribute.
- Node label: passthrough mapping for ``cdr3aa`` attribute.
- Node label color: discrete mapping for ``cdr3.code`` attribute. Then apply (right-click) a Mapping Value Generator. Clonotypes with close ``cdr3.code`` values have similar CDR3 sequences.

For edges:

- Target arrow shape: select an arrow, the graph is directed and built using parsimony principle.
- Edge color: continuous mapping for ``replacement.ratio`` attribute. Note that it is not S:R ratio, but rather number of replacement mutation among all mutations. Only mutations that are present in target node, but not in its parent (i.e. the difference) are counted here.
- Width: continuous mapping for ``shm.count.neg`` attribute. The more hypermutations separate two clonotypes, the smaller is the edge width.

#### Finalize

Apply an ``Edge weighted Spring Embedded Layout`` from the ``Layout`` menu, use ``shm.count.neg`` as parameter. An example cytoscape file with the network for Raji cell line that can be also used as a template is stored in this folder (``raji.cys``).

### Clonotype network

**Under development** The concept is a network showing each CDR3 with its children (edges are weighted by hypermutation load/S:R ratio) and edges between distinct CDR3 which are likely hypermutations. E.g. a single mutation within "N" region of CDR3 and consequent match of 5 "N" nucleotides between two different CDR3 tells that they are more likely a result of hypermutation than of independent V-D-J recombination.