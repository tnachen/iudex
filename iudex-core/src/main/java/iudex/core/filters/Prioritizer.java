/*
 * Copyright (c) 2008-2010 David Kellum
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package iudex.core.filters;

import static iudex.core.ContentKeys.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import iudex.filter.Described;
import iudex.filter.Filter;

import com.gravitext.htmap.UniMap;

public class Prioritizer implements Filter, Described
{
    public Prioritizer( String name )
    {
        _name = name;
    }

    @Override
    public boolean filter( UniMap content )
    {
        //FIXME: Keep as simple place holder impl?

        Float priority = content.get( PRIORITY );
        if( priority == null ) content.set( PRIORITY, 1.0f );

        content.set( PRIORITY, adjustPriority( content ) );

        Date start = content.get( VISIT_START );
        content.set( NEXT_VISIT_AFTER,
                     new Date( start.getTime() + visitDelta( content ) ) );

        return true;
    }

    public float adjustPriority( UniMap content )
    {
        return content.get( PRIORITY );
    }

    public long visitDelta( UniMap content )
    {
        return 0;
    }

    @Override
    public List<?> describe()
    {
        return Arrays.asList( _name );
    }

    private String _name;
}
