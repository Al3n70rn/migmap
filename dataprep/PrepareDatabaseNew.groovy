/**
 Copyright 2014 Mikhail Shugay (mikhail.shugay@gmail.com)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

def inputFileName = "./segments.all.minor.txt", outputPath = "./database/"
new File(outputPath).deleteDir()
new File(outputPath).mkdir()
new File("jref.txt").delete()

def speciesGeneHash = new HashSet<String>()
def speciesAliasMap = ["HomoSapiens"         : "human",
                       "MusMusculus"         : "mouse",
                       "RattusNorvegicus"    : "rat",
                       "OryctolagusCuniculus": "rabbit",
                       "MacacaMulatta"       : "rhesus_monkey"]

def allowedVSegmentBySpecies = new HashMap<String, Set<String>>()

// Not all segments have domain markup
// make sure we use only marked segments
speciesAliasMap.values().each { alias ->
    def allowedSet = new HashSet<String>()
    allowedVSegmentBySpecies.put(alias, allowedSet)
    new File(alias + ".ndm.imgt").splitEachLine("\t") {
        allowedSet.add(it[0])
    }
}

new File(inputFileName).splitEachLine("\t") {
    def (species, geneFull, segment, segmentFull, refPoint, seq) = it

    // Change names to IgBlast semantics
    species = speciesAliasMap[species]
    segment = segment[0]
    segmentFull = segmentFull.replaceAll("/", "_") // otherwise makeblastdb will crash
    def _prefix = "$outputPath/${species}_${geneFull[0..1]}_${geneFull[2]}"

    if (species) {
        boolean majorAllele = segmentFull.endsWith("*01")

        if (segment != "V" || allowedVSegmentBySpecies[species].contains(segmentFull)) {
            (majorAllele ? [_prefix, "${_prefix}_all"] : ["${_prefix}_all"]).each { prefix ->
                speciesGeneHash.add(prefix)

                new File("${prefix}_${segment}.fa").withWriterAppend { writer ->
                    writer.println(">$segmentFull\n$seq")
                }

                if (segment == "J") {
                    new File("jref.txt").withWriterAppend { writer ->
                        writer.println([species, geneFull, segmentFull, refPoint, seq].join("\t"))
                    }
                }
            }
        }
    } else {
        // Ignore those species, we won't be able to use them without framework markup anyway
    }
}

/*
Add dummy references. 
This is absolutely necessary as IgBlast requires D segment references 
and is not aware whether a certain chain has D segment or not
 */
speciesGeneHash.each {
    def fileName = it + "_D.fa"
    def file = new File(fileName)
    if (!file.exists()) {
        file.withPrintWriter { pw ->
            pw.println(">.\nGGGGGGGGGGGGGGGG")
        }
    }
}

new File(outputPath).listFiles().each {
    "makeblastdb -parse_seqids -dbtype nucl -in $outputPath/$it.name -out $outputPath/${it.name[0..-4]}".execute()
}