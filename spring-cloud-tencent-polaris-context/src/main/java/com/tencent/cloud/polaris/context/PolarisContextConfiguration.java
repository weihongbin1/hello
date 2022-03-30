/*
 * Tencent is pleased to support the open source community by making Spring Cloud Tencent available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.tencent.cloud.polaris.context;

import java.util.List;

import com.tencent.cloud.common.constant.ContextConstant.ModifierOrder;
import com.tencent.cloud.common.util.AddressUtils;
import com.tencent.polaris.api.exception.PolarisException;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for Polaris {@link SDKContext}.
 *
 * @author Haotian Zhang
 */
@EnableConfigurationProperties({ PolarisContextProperties.class })
public class PolarisContextConfiguration {

	@Bean(name = "polarisContext", initMethod = "init", destroyMethod = "destroy")
	@ConditionalOnMissingBean
	public SDKContext polarisContext(PolarisContextProperties properties)
			throws PolarisException {
		return SDKContext.initContextByConfig(properties.configuration());
	}

	@Bean
	@ConditionalOnMissingBean
	public ModifyAddress polarisConfigModifier() {
		return new ModifyAddress();
	}

	private static class ModifyAddress implements PolarisConfigModifier {

		@Autowired
		private PolarisContextProperties properties;

		@Override
		public void modify(ConfigurationImpl configuration) {
			if (StringUtils.isBlank(properties.getAddress())) {
				return;
			}

			List<String> addresses = AddressUtils
					.parseAddressList(properties.getAddress());

			configuration.getGlobal().getServerConnector().setAddresses(addresses);
		}

		@Override
		public int getOrder() {
			return ModifierOrder.FIRST;
		}

	}

}