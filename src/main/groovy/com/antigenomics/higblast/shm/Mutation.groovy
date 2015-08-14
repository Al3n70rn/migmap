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

package com.antigenomics.higblast.shm

class Mutation {
    final int startInRead, endInRead
    final String ntFrom, ntTo

    int start, end

    String aaFrom, aaTo
    String region
    SubRegion subRegion

    Mutation(int start, int end, int startInRead, int endInRead, String ntFrom, String ntTo) {
        this.end = end
        this.startInRead = startInRead
        this.endInRead = endInRead
        this.ntFrom = ntFrom
        this.ntTo = ntTo
        this.start = start
    }
}
