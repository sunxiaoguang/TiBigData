/*
 * Copyright 2020 Zhihu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.presto.tidb;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;
import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.stream.Collectors.toCollection;

public final class SplitManagerInternal
{
    private final ClientSession session;

    public SplitManagerInternal(ClientSession session)
    {
        this.session = requireNonNull(session, "session is null");
    }

    public List<SplitInternal> getSplits(TableHandleInternal tableHandle)
    {
        List<SplitInternal> splits = session.getTableRanges(tableHandle)
                .stream()
                .map(range -> new SplitInternal(tableHandle, range))
                .collect(toCollection(ArrayList::new));
        Collections.shuffle(splits);
        return Collections.unmodifiableList(splits);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("session", session)
                .toString();
    }
}
