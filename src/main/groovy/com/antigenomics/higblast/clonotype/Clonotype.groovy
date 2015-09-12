/*
 * Copyright 2013-2015 Mikhail Shugay (mikhail.shugay@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antigenomics.higblast.clonotype

import com.antigenomics.higblast.Util
import com.antigenomics.higblast.genomic.Segment
import com.antigenomics.higblast.mapping.Cdr3Markup
import com.antigenomics.higblast.mapping.ReadMapping
import com.antigenomics.higblast.mapping.Truncations
import com.antigenomics.higblast.mutation.Mutation
import com.antigenomics.higblast.mutation.MutationStringifier

class Clonotype implements Comparable<Clonotype> {
    final String cdr3nt, cdr3aa
    final Segment vSegment, dSegment, jSegment
    final List<Mutation> mutations
    final long count
    final double freq
    final byte minQual
    final byte[] cdrInsertQual, mutationQual
    final boolean hasCdr3, complete, inFrame, noStop
    final Cdr3Markup cdr3Markup
    final Truncations truncations
    final ReadMapping representativeMapping

    Clonotype(ClonotypeKey key, ClonotypeData data, long total) {
        this.representativeMapping = key.representativeMapping
        this.cdr3nt = key.cdr3nt
        this.vSegment = key.vSegment
        this.dSegment = key.dSegment
        this.jSegment = key.jSegment
        this.mutations = key.mutations
        this.count = data.count.longValue()
        this.freq = (double) count / total
        this.cdrInsertQual = new byte[data.cdrInsertQual.length()]
        def minQual = Util.MAX_QUAL
        (0..<cdrInsertQual.length).each {
            def qual = (byte) ((double) data.cdrInsertQual.get(it) / count)
            cdrInsertQual[it] = qual
            minQual = Math.min(qual, minQual)
        }
        this.mutationQual = new byte[data.mutationQual.length()]
        (0..<mutationQual.length).each {
            def qual = (byte) ((double) data.mutationQual.get(it) / count)
            mutationQual[it] = qual
            minQual = Math.min(qual, minQual)
        }
        this.minQual = minQual

        def representativeMapping = key.representativeMapping

        this.cdr3Markup = representativeMapping.mapping.cdr3Markup
        this.truncations = representativeMapping.mapping.truncations

        this.hasCdr3 = representativeMapping.mapping.hasCdr3
        this.complete = representativeMapping.mapping.complete

        boolean inFrame, noStop

        if (hasCdr3) {
            this.cdr3aa = complete ? Util.translateCdr(cdr3nt) : Util.translateLinear(cdr3nt)
            inFrame = !cdr3aa.contains("?")
            noStop = !cdr3aa.contains("*")
        } else {
            this.cdr3aa = Util.MY_NA
        }

        this.inFrame = representativeMapping.mapping.inFrame && inFrame
        this.noStop = representativeMapping.mapping.noStop && noStop
    }

    @Override
    int compareTo(Clonotype o) {
        -this.count.compareTo(o.count)
    }

    static
    final String OUTPUT_HEADER = "freq\tcount\tv\td\tj\tcdr3nt\tcdr3aa\t" + MutationStringifier.OUTPUT_HEADER +
            "\tcdr.insert.qual\tmutations.qual\t" + Cdr3Markup.OUTPUT_HEADER + "\t" + Truncations.OUTPUT_HEADER +
            "\thas.cdr3\tin.frame\tno.stop\tcomplete"

    @Override
    String toString() {
        [freq, count,
         vSegment.toString(), dSegment.toString(), jSegment.toString(),
         cdr3nt, cdr3aa,
         MutationStringifier.toString(mutations),
         Util.qualToString(cdrInsertQual), Util.qualToString(mutationQual),
         cdr3Markup, truncations,
         hasCdr3, inFrame, noStop, complete].join("\t")
    }
}
