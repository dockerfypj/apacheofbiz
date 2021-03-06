/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

productionRunId = parameters.productionRunId ?: parameters.workEffortId;
context.productionRunId = productionRunId;

delivGoodStandard = EntityUtil.getFirst(delegator.findByAnd("WorkEffortGoodStandard", [workEffortId : productionRunId, workEffortGoodStdTypeId : "PRUN_PROD_DELIV", statusId : "WEGS_CREATED"], ["-fromDate"], false));
if (delivGoodStandard) {
    context.delivProductId = delivGoodStandard.productId;
}
if (context.delivProductId && (parameters.partyId || parameters.contentLocale)) {
    delivProductContents = EntityUtil.filterByDate(delegator.findByAnd("ProductContentAndInfo", [productId : context.delivProductId], ["-fromDate"], false));
    context.delivProductContents = delivProductContents;

    Locale contentLocale = null;
    if (parameters.contentLocale) {
        contentLocale = new Locale(parameters.contentLocale);
    }
    delivProductContentsForLocaleAndUser = [];
    delivProductContents.each { delivProductContent ->
        GenericValue content = ContentWorker.findContentForRendering(delegator, delivProductContent.contentId, contentLocale, parameters.partyId, parameters.roleTypeId, true);
        delivProductContentsForLocaleAndUser.add(EntityUtil.getFirst(delegator.findByAnd("ContentDataResourceView", [contentId : content.contentId], null, false)));
    }
    context.delivProductContentsForLocaleAndUser = delivProductContentsForLocaleAndUser;
}
