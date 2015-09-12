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


package com.antigenomics.higblast.io

import com.antigenomics.higblast.mapping.ReadMapping
import com.antigenomics.higblast.mapping.ReadMappingDetailsProvider

class ReadMappingOutput implements InputPort<ReadMapping> {
    final PlainTextOutput plainTextOutput
    final ReadMappingDetailsProvider readMappingDetailsProvider

    ReadMappingOutput(PlainTextOutput plainTextOutput = StdOutput.INSTANCE,
                      ReadMappingDetailsProvider readMappingDetailsProvider = ReadMappingDetailsProvider.DUMMY) {
        this.plainTextOutput = plainTextOutput
        this.readMappingDetailsProvider = readMappingDetailsProvider
        if (plainTextOutput != StdOutput.INSTANCE)
            plainTextOutput.put(ReadMapping.OUTPUT_HEADER + readMappingDetailsProvider.header)
    }

    @Override
    void put(ReadMapping readMapping) {
        if (readMapping.mapped) {
            plainTextOutput.put(readMapping.toString() + readMappingDetailsProvider.getDetails(readMapping))
        }
    }

    @Override
    void close() {
        plainTextOutput.close()
    }
}
