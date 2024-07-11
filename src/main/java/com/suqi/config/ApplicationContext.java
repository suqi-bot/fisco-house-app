package com.suqi.config;


import lombok.Data;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 配置类
 */
@Configuration
@Data
public class ApplicationContext {

    @Value("${fisco.nodeList}")
    private String nodeLists;

    @Value("${fisco.groupId}")
    private Integer groupId;

    @Value("${fisco.certPath}")
    private String certPath;





    @Bean(name = "configProperty")
    public ConfigProperty defaultConfigProperty() throws ConfigException {
        ConfigProperty property = new ConfigProperty();
        // 配置cryptoMaterial
        Map<String, Object> cryptoMaterialMap = new HashMap<>();
        String[] possibilities = certPath.split(",");
        for (int i=0;i<possibilities.length;i++) {
            try{
                cryptoMaterialMap.put("certPath", possibilities[i]);
                property.setCryptoMaterial(cryptoMaterialMap);

                // 配置network
                Map<String, Object> networkMap = new HashMap<>();
                String[] split = nodeLists.split(",");
                List<String> nodeList = Arrays.asList(split);
                networkMap.put("peers", nodeList);
                property.setNetwork(networkMap);

//               Map<String, Object> accountMap = new HashMap<>();
//               accountMap.put("keyStoreDir", "account");
//               accountMap.put("accountAddress", "");
//               accountMap.put("accountFileFormat", "pem");
//               accountMap.put("password", "");
//               accountMap.put("accountFilePath", possibilities2[i]);
//               property.setAccount(accountMap);

                //配置 threadPool
                Map<String, Object> threadPoolMap = new HashMap<>();
                threadPoolMap.put("channelProcessorThreadSize", "16");
                threadPoolMap.put("receiptProcessorThreadSize", "16");
                threadPoolMap.put("maxBlockingQueueSize", "102400");
                property.setThreadPool(threadPoolMap);
                return property;
            }catch (Exception ex){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw  new ConfigException("failed to connected peers");

    }

    @Bean(name = "configOption")
    public ConfigOption defaultConfigOption(ConfigProperty configProperty) throws ConfigException {
        return new ConfigOption(configProperty);
    }

    @Bean(name = "bcosSDK")
    public BcosSDK bcosSDK(ConfigOption configOption) {
        return new BcosSDK(configOption);
    }

    @Bean(name = "client")
    public Client getClient(BcosSDK bcosSDK) {
        // 为群组初始化client
        Client client = bcosSDK.getClient(groupId);
        return client;
    }

    @Bean
    public CryptoKeyPair getCryptoKeyPair(Client client) {
        // 如果有密钥文件 那么每次读取的就不再是随机的
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        CryptoKeyPair cryptoKeyPair = cryptoSuite.getCryptoKeyPair();
        return cryptoKeyPair;
    }
}

