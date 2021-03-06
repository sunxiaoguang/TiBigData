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
package com.zhihu.prestosql.tidb;

import io.prestosql.spi.connector.RecordCursor;
import io.prestosql.spi.connector.RecordSet;
import io.prestosql.spi.type.Type;
import com.zhihu.presto.tidb.Expressions;
import com.zhihu.presto.tidb.RecordSetInternal;
import com.zhihu.presto.tidb.Wrapper;

import java.util.List;

import static com.zhihu.prestosql.tidb.TiDBColumnHandle.internalHandles;
import static com.google.common.collect.ImmutableList.toImmutableList;

public final class TiDBRecordSet
        extends Wrapper<RecordSetInternal>
        implements RecordSet
{
    private final List<TiDBColumnHandle> columnHandles;
    private final List<Type> columnTypes;

    public TiDBRecordSet(TiDBSession session, TiDBSplit split, List<TiDBColumnHandle> columnHandles)
    {
        super(new RecordSetInternal(session.getInternal(), split.toInternal(), internalHandles(columnHandles), split.getAdditionalPredicate().map(Expressions::deserialize)));
        this.columnHandles = columnHandles;
        this.columnTypes = columnHandles.stream().map(TiDBColumnHandle::getPrestoType).collect(toImmutableList());
    }

    @Override
    public List<Type> getColumnTypes()
    {
        return columnTypes;
    }

    @Override
    public RecordCursor cursor()
    {
        return new TiDBRecordCursor(columnHandles, getInternal().getColumnTypes(), getInternal().cursor());
    }
}
