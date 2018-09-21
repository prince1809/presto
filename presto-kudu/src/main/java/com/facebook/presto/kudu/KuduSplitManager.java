/*
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
package com.facebook.presto.kudu;

import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.ConnectorSplitSource;
import com.facebook.presto.spi.ConnectorTableLayoutHandle;
import com.facebook.presto.spi.FixedSplitSource;
import com.facebook.presto.spi.connector.ConnectorSplitManager;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;

import javax.inject.Inject;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class KuduSplitManager
        implements ConnectorSplitManager
{
    private final String connectorId;
    private final KuduClientSession clientSession;

    @Inject
    public KuduSplitManager(KuduConnectorId connectorId, KuduClientSession clientSession)
    {
        this.connectorId = requireNonNull(connectorId, "connectorId is null").toString();
        this.clientSession = requireNonNull(clientSession, "clientSession is null");
    }

    @Override
    public ConnectorSplitSource getSplits(ConnectorTransactionHandle transactionHandle,
            ConnectorSession session, ConnectorTableLayoutHandle layout,
            SplitSchedulingStrategy splitSchedulingStrategy)
    {
        KuduTableLayoutHandle layoutHandle = (KuduTableLayoutHandle) layout;

        List<KuduSplit> splits = clientSession.buildKuduSplits(layoutHandle);

        return new FixedSplitSource(splits);
    }
}
