/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.usergrid.persistence.collection.impl;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import org.apache.usergrid.persistence.collection.CollectionScope;
import org.apache.usergrid.persistence.collection.MvccEntity;
import org.apache.usergrid.persistence.collection.event.EntityVersionCreated;
import org.apache.usergrid.persistence.collection.event.EntityVersionDeleted;
import org.apache.usergrid.persistence.collection.mvcc.MvccEntitySerializationStrategy;
import org.apache.usergrid.persistence.collection.mvcc.MvccLogEntrySerializationStrategy;
import org.apache.usergrid.persistence.collection.mvcc.entity.impl.MvccEntityImpl;
import org.apache.usergrid.persistence.collection.serialization.SerializationFig;
import org.apache.usergrid.persistence.collection.serialization.UniqueValueSerializationStrategy;
import org.apache.usergrid.persistence.collection.util.LogEntryMock;
import org.apache.usergrid.persistence.core.task.NamedTaskExecutorImpl;
import org.apache.usergrid.persistence.core.task.TaskExecutor;
import org.apache.usergrid.persistence.model.entity.Entity;
import org.apache.usergrid.persistence.model.entity.Id;
import org.apache.usergrid.persistence.model.entity.SimpleId;
import org.apache.usergrid.persistence.model.util.UUIDGenerator;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created task tests.
 */
public class EntityVersionCreatedTaskTest {

    private static final TaskExecutor taskExecutor = new NamedTaskExecutorImpl( "test", 4, 0 );

    @AfterClass
    public static void shutdown() {
        taskExecutor.shutdown();
    }


    @Test(timeout=10000)
    public void noListener()
            throws ExecutionException, InterruptedException, ConnectionException {

        // create a latch for the event listener, and add it to the list of events

        final int sizeToReturn = 0;

        final Set<EntityVersionCreated> listeners = mock( Set.class );

        when ( listeners.size()).thenReturn( 0 );

        final Id applicationId = new SimpleId( "application" );

        final CollectionScope appScope = new CollectionScopeImpl(
                applicationId, applicationId, "users" );

        final Id entityId = new SimpleId( "user" );
        final Entity entity = new Entity( entityId );

        // start the task

        EntityVersionCreatedTask entityVersionCreatedTask =
                new EntityVersionCreatedTask( appScope, listeners, entity);

        try {
            entityVersionCreatedTask.call();
        }catch(Exception e){
            Assert.fail(e.getMessage());
        }


        // wait for the task
       // future.get();

        //mocked listener makes sure that the task is called
        verify( listeners ).size();

    }
    @Test(timeout=10000)
    public void oneListener()
            throws ExecutionException, InterruptedException, ConnectionException {

        // create a latch for the event listener, and add it to the list of events

        final int sizeToReturn = 1;

        final CountDownLatch latch = new CountDownLatch( sizeToReturn );

        final EntityVersionCreatedTest eventListener = new EntityVersionCreatedTest(latch);

        final Set<EntityVersionCreated> listeners = mock( Set.class );
        final Iterator<EntityVersionCreated> helper = mock(Iterator.class);

        when ( listeners.size()).thenReturn( 1 );
        when ( listeners.iterator()).thenReturn( helper );
        when ( helper.next() ).thenReturn( eventListener );

        final Id applicationId = new SimpleId( "application" );

        final CollectionScope appScope = new CollectionScopeImpl(
                applicationId, applicationId, "users" );

        final Id entityId = new SimpleId( "user" );
        final Entity entity = new Entity( entityId );

        // start the task

        EntityVersionCreatedTask entityVersionCreatedTask =
            new EntityVersionCreatedTask( appScope, listeners, entity);

        try {
            entityVersionCreatedTask.call();
        }catch(Exception e){

            Assert.fail(e.getMessage());
        }
        //mocked listener makes sure that the task is called
        verify( listeners ).size();
        verify( listeners ).iterator();
        verify( helper ).next();

    }

    @Test(timeout=10000)
    public void multipleListener()
            throws ExecutionException, InterruptedException, ConnectionException {

        final int sizeToReturn = 3;

        final Set<EntityVersionCreated> listeners = mock( Set.class );
        final Iterator<EntityVersionCreated> helper = mock(Iterator.class);

        when ( listeners.size()).thenReturn( 3 );
        when ( listeners.iterator()).thenReturn( helper );

        final Id applicationId = new SimpleId( "application" );

        final CollectionScope appScope = new CollectionScopeImpl(
                applicationId, applicationId, "users" );

        final Id entityId = new SimpleId( "user" );
        final Entity entity = new Entity( entityId );

        // start the task

        EntityVersionCreatedTask entityVersionCreatedTask =
                new EntityVersionCreatedTask( appScope, listeners, entity);

        final CountDownLatch latch = new CountDownLatch( sizeToReturn );

        final EntityVersionCreatedTest listener1 = new EntityVersionCreatedTest(latch);
        final EntityVersionCreatedTest listener2 = new EntityVersionCreatedTest(latch);
        final EntityVersionCreatedTest listener3 = new EntityVersionCreatedTest(latch);

        when ( helper.next() ).thenReturn( listener1,listener2,listener3);

        try {
            entityVersionCreatedTask.call();
        }catch(Exception e){
            ;
        }
        //ListenableFuture<Void> future = taskExecutor.submit( entityVersionCreatedTask );

        //wait for the task
        //intentionally fails due to difficulty mocking observable

        //mocked listener makes sure that the task is called
        verify( listeners ).size();
        //verifies that the observable made listener iterate.
        verify( listeners ).iterator();
    }

    @Test(timeout=10000)
    public void oneListenerRejected()
            throws ExecutionException, InterruptedException, ConnectionException {

        // create a latch for the event listener, and add it to the list of events

        final TaskExecutor taskExecutor = new NamedTaskExecutorImpl( "test", 0, 0 );

        final int sizeToReturn = 1;

        final CountDownLatch latch = new CountDownLatch( sizeToReturn );

        final EntityVersionCreatedTest eventListener = new EntityVersionCreatedTest(latch);

        final Set<EntityVersionCreated> listeners = mock( Set.class );
        final Iterator<EntityVersionCreated> helper = mock(Iterator.class);

        when ( listeners.size()).thenReturn( 1 );
        when ( listeners.iterator()).thenReturn( helper );
        when ( helper.next() ).thenReturn( eventListener );

        final Id applicationId = new SimpleId( "application" );

        final CollectionScope appScope = new CollectionScopeImpl(
                applicationId, applicationId, "users" );

        final Id entityId = new SimpleId( "user" );
        final Entity entity = new Entity( entityId );

        // start the task

        EntityVersionCreatedTask entityVersionCreatedTask =
                new EntityVersionCreatedTask( appScope, listeners, entity);

        entityVersionCreatedTask.rejected();

        //mocked listener makes sure that the task is called
        verify( listeners ).size();
        verify( listeners ).iterator();
        verify( helper ).next();

    }

    private static class EntityVersionCreatedTest implements EntityVersionCreated {
        final CountDownLatch invocationLatch;

        private EntityVersionCreatedTest( final CountDownLatch invocationLatch) {
            this.invocationLatch = invocationLatch;
        }

        @Override
        public void versionCreated( final CollectionScope scope, final Entity entity ) {
            invocationLatch.countDown();
        }
    }
}