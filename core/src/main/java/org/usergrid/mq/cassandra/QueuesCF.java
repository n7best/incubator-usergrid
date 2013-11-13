/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.usergrid.mq.cassandra;


import java.util.List;

import org.usergrid.persistence.cassandra.CFEnum;

import me.prettyprint.hector.api.ddl.ColumnDefinition;

import static me.prettyprint.hector.api.ddl.ComparatorType.COUNTERTYPE;
import static org.usergrid.persistence.cassandra.CassandraPersistenceUtils.getIndexMetadata;

// Auto-generated by ApplicationCFGenerator


public enum QueuesCF implements CFEnum
{

    MESSAGE_PROPERTIES( "Entity_Properties", "BytesType", false ),

    QUEUE_PROPERTIES( "Queue_Properties", "BytesType" ),

    QUEUE_INBOX( "Queue_Inbox", "UUIDType" ),

    QUEUE_DICTIONARIES( "Queue_Dictionaries", "BytesType" ),

    QUEUE_SUBSCRIBERS( "Queue_Subscribers", "BytesType" ),

    QUEUE_SUBSCRIPTIONS( "Queue_Subscriptions", "BytesType" ),

    /**
     * Time based UUID list of future timeouts for messages. The UUID value is a pointer to the original message in the
     * topic
     */
    CONSUMER_QUEUE_TIMEOUTS( "MQ_Consumers_Timeout", "UUIDType" ),

    CONSUMERS( "MQ_Consumers", "BytesType" ),

    CONSUMER_QUEUE_MESSAGES_PROPERTIES( "Consumer_Queue_Messages_Properties", "BytesType" ),

    COUNTERS( "MQ_Counters", "BytesType", COUNTERTYPE.getClassName() ),

    PROPERTY_INDEX( "MQ_Property_Index",
            "DynamicCompositeType(a=>AsciiType,b=>BytesType,i=>IntegerType,x=>LexicalUUIDType,l=>LongType," +
                    "t=>TimeUUIDType,s=>UTF8Type,u=>UUIDType,A=>AsciiType(reversed=true),B=>BytesType(reversed=true)," +
                    "I=>IntegerType(reversed=true),X=>LexicalUUIDType(reversed=true),L=>LongType(reversed=true)," +
                    "T=>TimeUUIDType(reversed=true),S=>UTF8Type(reversed=true),U=>UUIDType(reversed=true))" ),

    PROPERTY_INDEX_ENTRIES( "MQ_Property_Index_Entries",
            "DynamicCompositeType(a=>AsciiType,b=>BytesType,i=>IntegerType,x=>LexicalUUIDType,l=>LongType," +
                    "t=>TimeUUIDType,s=>UTF8Type,u=>UUIDType,A=>AsciiType(reversed=true),B=>BytesType(reversed=true)," +
                    "I=>IntegerType(reversed=true),X=>LexicalUUIDType(reversed=true),L=>LongType(reversed=true)," +
                    "T=>TimeUUIDType(reversed=true),S=>UTF8Type(reversed=true),U=>UUIDType(reversed=true))" ),;

    public final static String STATIC_MESSAGES_KEYSPACE = "Usergrid_Messages";
    public final static String APPLICATION_MESSAGES_KEYSPACE_SUFFIX = "_messages";

    private final String cf;
    private final String comparator;
    private final String validator;
    private final String indexes;
    private final boolean create;


    QueuesCF( String cf, String comparator )
    {
        this.cf = cf;
        this.comparator = comparator;
        validator = null;
        indexes = null;
        create = true;
    }


    QueuesCF( String cf, String comparator, boolean create )
    {
        this.cf = cf;
        this.comparator = comparator;
        validator = null;
        indexes = null;
        this.create = create;
    }


    QueuesCF( String cf, String comparator, String validator )
    {
        this.cf = cf;
        this.comparator = comparator;
        this.validator = validator;
        indexes = null;
        create = true;
    }


    QueuesCF( String cf, String comparator, String validator, String indexes )
    {
        this.cf = cf;
        this.comparator = comparator;
        this.validator = validator;
        this.indexes = indexes;
        create = true;
    }


    @Override
    public String toString()
    {
        return cf;
    }


    @Override
    public String getColumnFamily()
    {
        return cf;
    }


    @Override
    public String getComparator()
    {
        return comparator;
    }


    @Override
    public String getValidator()
    {
        return validator;
    }


    @Override
    public boolean isComposite()
    {
        return comparator.startsWith( "DynamicCompositeType" );
    }


    @Override
    public List<ColumnDefinition> getMetadata()
    {
        return getIndexMetadata( indexes );
    }


    @Override
    public boolean create()
    {
        return create;
    }

}
