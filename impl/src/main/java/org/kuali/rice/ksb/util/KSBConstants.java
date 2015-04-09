/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.util;

import org.kuali.rice.core.util.JSTLConstants;

/**
 * This is a file for constants used by the KSB module of Rice
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KSBConstants extends JSTLConstants {

    private static final long serialVersionUID = -958108423493026266L;

    /**
     * Configuration Parameters
     */
    public class Config {
    	public static final String SERVICE_NAMESPACE = "service.namespace";
        public static final String MESSAGE_PERSISTENCE = "message.persistence";
        public static final String MESSAGING_OFF = "message.off";
        public static final String MESSAGE_DELIVERY = "message.delivery";
        public static final String INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY = "message.injected.scheduler";
        public static final String FIXED_POOL_SIZE = "ksb.fixedPoolSize";
        public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY = "RouteQueue.maxRetryAttempts";
        public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY = "RouteQueue.maxRetryAttemptsOverride";
        public static final String ROUTE_QUEUE_TIME_INCREMENT_KEY = "RouteQueue.timeIncrement";
        public static final String IMMEDIATE_EXCEPTION_ROUTING = "Routing.ImmediateExceptionRouting";
        public static final String ALLOW_SYNC_EXCEPTION_ROUTING = "rice.ksb.allowSyncExceptionRouting";
        public static final String KSB_ALLOW_SELF_SIGNED_SSL = "rice.ksb.config.allowSelfSignedSSL";
        public static final String KSB_MESSAGE_DATASOURCE = "ksbMessage.datasource";
        public static final String KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE = "ksbMessage.nonTransactional.datasource";
        public static final String KSB_REGISTRY_DATASOURCE = "ksbRegistry.datasource";
        public static final String KSB_MESSAGE_DATASOURCE_JNDI = "ksbMessage.datasource.jndi.location";
        public static final String KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE_JNDI = "ksbMessage.nonTransactional.datasource.jndi.location";
        public static final String KSB_REGISTRY_DATASOURCE_JNDI = "ksbRegistry.datasource.jndi.location";
        public static final String USE_QUARTZ_DATABASE = "useQuartzDatabase";
        public static final String KSB_ALTERNATE_ENDPOINTS = "ksb.alternateEndpoints";
        public static final String KSB_ALTERNATE_ENDPOINT_LOCATIONS = "ksb.alternateEndpointLocations";
        public static final String LOAD_KNS_MODULE_CONFIGURATION = "rice.ksb.loadKNSModuleConfiguration";
        public static final String RESTFUL_SERVICE_PATH = "rice.ksb.restfulServicePath";
    }
    
    // messaging constants
    
    public static final String MESSAGING_SYNCHRONOUS = "synchronous";
    public static final String ROUTE_QUEUE_QUEUED = "Q";
    public static final String ROUTE_QUEUE_EXCEPTION = "E";
    public static final String ROUTE_QUEUE_ROUTING = "R";
    public static final String ROUTE_QUEUE_EXCEPTION_LABEL = "EXCEPTION";
    public static final String ROUTE_QUEUE_ROUTING_LABEL = "ROUTING";
    public static final String ROUTE_QUEUE_QUEUED_LABEL = "QUEUED";    
    public static final Integer ROUTE_QUEUE_DEFAULT_PRIORITY = new Integer(5);
    public static final String ROUTE_QUEUE_FILTER_SUFFIX = "Filter";

    public static final String FORWARD_HANDLER_SUFFIX = "-forwardHandler";
    
    // custom http header keys
    public static final String DIGITAL_SIGNATURE_HEADER = "KEW_DIGITAL_SIGNATURE";
    public static final String KEYSTORE_ALIAS_HEADER = "KEW_KEYSTORE_ALIAS";
    public static final String KEYSTORE_CERTIFICATE_HEADER = "KEW_CERTIFICATE_ALIAS";
    
    public class ServiceNames {
    	public static final String BAM_SERVICE = "bamService";
    	public static final String BUS_ADMIN_SERVICE = "busAdminService";
    	public static final String BUS_IN_INTERCEPTORS = "ksbInInterceptors";
        public static final String BUS_OUT_INTERCEPTORS = "ksbOutInterceptors";
    	public static final String CACHE_ADMINISTRATOR_FACTORY = "enKEWCacheAdministratorFactoryService";
    	public static final String CXF_BUS = "cxf";
    	public static final String DIGITAL_SIGNATURE_SERVICE = "digitalSignatureService";
    	public static final String ENCRYPTION_SERVICE = "enEncryptionService";
        public static final String EXCEPTION_MESSAGING_SERVICE = "exceptionMessagingService";
        public static final String JAVA_SECURITY_MANAGEMENT_SERVICE = "ksbJavaSecurityManagementService";
        public static final String JTA_TRANSACTION_MANAGER = "jtaTransactionManager";
        public static final String MESSAGE_DATASOURCE = "ksbMessageDataSource";
        public static final String MESSAGE_ENTITY_MANAGER_FACTORY = "ksbMessageEntityManagerFactory";
        public static final String MESSAGE_HELPER = "enMessageHelper";
        public static final String MESSAGE_NON_TRANSACTIONAL_DATASOURCE = "ksbMessageNonTransactionalDataSource";
    	public static final String OBJECT_REMOTER = "ObjectRemoterService";
    	public static final String REGISTRY_DATASOURCE = "ksbRegistryDataSource";
    	public static final String REGISTRY_ENTITY_MANAGER_FACTORY = "ksbRegistryEntityManagerFactory";
    	public static final String REMOTED_SERVICE_REGISTRY = "enServiceInvoker";
        public static final String REPEAT_TOPIC_INVOKING_QUEUE = "enRepeatTopicInvokerQueue";
        public static final String ROUTE_QUEUE_SERVICE = "enRouteQueueService";
        public static final String SERVICE_REGISTRY = "serviceRegistry";
        public static final String SCHEDULED_THREAD_POOL_SERVICE = "enScheduledThreadPool";
        public static final String SCHEDULER = "ksbScheduler";
        public static final String SERVICE_REMOVER_SERVICE = "RemoteClassRemoverService";
        public static final String THREAD_POOL_SERVICE = "enThreadPool";
        public static final String TRANSACTION_MANAGER = "transactionManager";
        public static final String TRANSACTION_TEMPLATE = "transactionTemplate";
    }
    
    // Deprecated configuration Parameters, please use the Config inner class when referencing these.
    
    @Deprecated
    public static final String SERVICE_NAMESPACE = KSBConstants.Config.SERVICE_NAMESPACE;
    @Deprecated
    public static final String MESSAGE_PERSISTENCE = KSBConstants.Config.MESSAGE_PERSISTENCE;
    @Deprecated
    public static final String MESSAGING_OFF = KSBConstants.Config.MESSAGING_OFF;
    @Deprecated
    public static final String MESSAGE_DELIVERY = KSBConstants.Config.MESSAGE_DELIVERY;
    @Deprecated
    public static final String INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY = KSBConstants.Config.INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY;
    @Deprecated
    public static final String FIXED_POOL_SIZE = KSBConstants.Config.FIXED_POOL_SIZE;
    @Deprecated
    public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY = KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY;
    @Deprecated
    public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY = KSBConstants.Config.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY;
    @Deprecated
    public static final String ROUTE_QUEUE_TIME_INCREMENT_KEY = KSBConstants.Config.ROUTE_QUEUE_TIME_INCREMENT_KEY;
    @Deprecated
    public static final String IMMEDIATE_EXCEPTION_ROUTING = KSBConstants.Config.IMMEDIATE_EXCEPTION_ROUTING;
    @Deprecated
    public static final String ALLOW_SYNC_EXCEPTION_ROUTING = KSBConstants.Config.ALLOW_SYNC_EXCEPTION_ROUTING;
    
    @Deprecated
    public static final String KSB_ALLOW_SELF_SIGNED_SSL = KSBConstants.Config.KSB_ALLOW_SELF_SIGNED_SSL;
    @Deprecated
    public static final String KSB_MESSAGE_DATASOURCE = KSBConstants.Config.KSB_MESSAGE_DATASOURCE;
    @Deprecated
    public static final String KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE = KSBConstants.Config.KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE;
    @Deprecated
    public static final String KSB_REGISTRY_DATASOURCE = KSBConstants.Config.KSB_REGISTRY_DATASOURCE;
    @Deprecated
    public static final String KSB_MESSAGE_DATASOURCE_JNDI = KSBConstants.Config.KSB_MESSAGE_DATASOURCE_JNDI;
    @Deprecated
    public static final String KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE_JNDI = KSBConstants.Config.KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE_JNDI;
    @Deprecated
    public static final String KSB_REGISTRY_DATASOURCE_JNDI = KSBConstants.Config.KSB_REGISTRY_DATASOURCE_JNDI;
    @Deprecated
    public static final String USE_QUARTZ_DATABASE = KSBConstants.Config.USE_QUARTZ_DATABASE;
    @Deprecated
    public static final String KSB_ALTERNATE_ENDPOINTS = KSBConstants.Config.KSB_ALTERNATE_ENDPOINTS;
    @Deprecated
    public static final String KSB_ALTERNATE_ENDPOINT_LOCATIONS = KSBConstants.Config.KSB_ALTERNATE_ENDPOINT_LOCATIONS;
    @Deprecated
    public static final String LOAD_KNS_MODULE_CONFIGURATION = KSBConstants.Config.LOAD_KNS_MODULE_CONFIGURATION;

}