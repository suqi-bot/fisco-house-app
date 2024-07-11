package com.suqi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.suqi.mapper.HouseMapper;
import com.suqi.mapper.LeaseContractMapper;
import com.suqi.pojo.House;
import com.suqi.pojo.HouseData;
import com.suqi.pojo.LeaseContract;
import com.suqi.pojo.Result;
import com.suqi.utils.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.transaction.manager.TransactionProcessorFactory;

import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/house")
public class IndexController {

    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private LeaseContractMapper leaseContractMapper;

    @Value("${fisco.contractAddress.HouseLease}")
    private String HouseLease;

    @Value("${fisco.contractAddress.HouseInformation_CONTRACT}")
    private String HouseInformation_CONTRACT;


    @Value("${fisco.account.accountFilePaths}")
    private String accountFilePaths;

    @Autowired
    private Client client;
    @Autowired
    private CryptoKeyPair keyPair;

    int cryptoType = CryptoType.ECDSA_TYPE;

    public static String abiHouseInformation = IOUtil.readResourceAsString("abi/HouseInformation.abi");
    public static String binHouseInformation = IOUtil.readResourceAsString("bin/ecc/HouseInformation.bin");

    public static String abiHouseLease = IOUtil.readResourceAsString("abi/HouseLease.abi");
    public static String binHouseLease = IOUtil.readResourceAsString("bin/ecc/HouseLease.bin");





    /**
     * 查询房产信息
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getOwnedHouses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<HouseData>>  getOwnedHouses(@RequestBody JSONObject jsonParam) throws Exception {
        String houseOwner = (String) jsonParam.get("address");

        AssembleTransactionProcessor sdk = sdk("HouseInformation",
                abiHouseInformation, binHouseInformation);
        List array = new ArrayList();

        array.add(houseOwner);
        //选手填写部分
        //1. 获取当前账户拥有的房产编号
        //String to, String abi, String functionName, List<Object> params
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader
                ("HouseInformation", HouseInformation_CONTRACT,
                        "getOwnedHouses", array);
        JSONArray jsonArray = JSONArray.parseArray(transactionResponse.getValues());
        List<HouseData> houseDatas = new ArrayList<HouseData>();
        JSONArray jsonArray1 = JSONArray.parseArray(jsonArray.get(0).toString());
        //调用根据房产编号查询房产信息
        //2. 循环变量房产编号数组
        for (int i = 0; i < jsonArray1.size(); i++) {
            //3. 根据编号获取房产数据
            array = new JSONArray();
            array.add((int) jsonArray1.get(i));
            TransactionResponse result1 = sdk.sendTransactionAndGetResponseByContractLoader
                    ("HouseInformation", HouseInformation_CONTRACT,
                            "getHouseData", array);
            JSONArray jsonArray2 = JSONArray.parseArray(result1.getValues());
            TransactionResponse result2 = sdk.sendTransactionAndGetResponseByContractLoader
                    ("HouseInformation", HouseInformation_CONTRACT,
                            "getApproved", array);
            JSONArray jsonArray3 = JSONArray.parseArray(result2.getValues());
            //4. 获取房产授权数据
            HouseData data = new HouseData();
            data.setHourseId((int) jsonArray1.get(i));
            data.setDescription(jsonArray2.get(0).toString());
            data.setPrice((int) jsonArray2.get(1));
            data.setAppoveAddress(jsonArray3.get(0).toString());
            houseDatas.add(data);
        }

        return Result.success(houseDatas);
    }

    /**
     * 创建房产
     */
    @ResponseBody
    @CrossOrigin
    @PostMapping("/createHourse")
    public Result<JSONObject> createHourse(House house) throws Exception {
        JSONObject outPut = new JSONObject();
        String address = house.getOwnerAddress();
        String description = house.getHouseName();
        Integer price = house.getHousePrice();

        AssembleTransactionProcessor sdk = sdk("HouseInformation",
                abiHouseInformation, binHouseInformation);

        JSONArray array = new JSONArray();
        //选手填写部分
        //1. 封装创建房产参数
        array.add(address);
        array.add(description);
        array.add(price);
        //2. 调用创建房产合约函数
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader
                ("HouseInformation", HouseInformation_CONTRACT,
                        "createHouse", array);
        outPut.put("message", transactionResponse.getReceiptMessages());
        //3. 存入数据库
        houseMapper.set(house);

        return Result.success(outPut);
    }

    /**
     * 授权房产
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> approve(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        int houseId = (Integer) jsonParam.get("houseId");
        AssembleTransactionProcessor sdk = sdk("HouseInformation",
                abiHouseInformation, binHouseInformation);
        JSONArray array = new JSONArray();
        String spender = HouseLease;
        //选手填写部分：封装授权房产参数
        array.add(spender);
        array.add(houseId);
        //调用授权合约
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader
                ("HouseInformation", HouseInformation_CONTRACT,
                        "approve", array);
        outPut.put("message", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }

    //合同列表
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getLandlordContracts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<LeaseContract>> getLandlordContracts(@RequestBody JSONObject jsonParam) throws Exception {
        String _landlord = (String) jsonParam.get("landlord");
        JSONArray array = new JSONArray();
        array.add(_landlord);
        AssembleTransactionProcessor sdk = sdk("HouseLease",
                abiHouseLease, binHouseLease);
        //获取合同编号
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader
                ("HouseLease", HouseLease, "getLandlordAgreementIds", array);
        String values = transactionResponse.getValues();
        List<LeaseContract> leaseContracts = new ArrayList<>();
        if (values != null) {
            JSONArray array1 = JSONArray.parseArray(values);
            JSONArray array2 = JSONArray.parseArray(array1.get(0).toString());
            for (int i = 0; i < array2.size(); i++) {
                List<Object> list = new ArrayList<>();
                list.add((int) array2.get(i));
                //根据合同编号获取合同详情
                TransactionResponse transactionResponse1 = sdk.sendTransactionAndGetResponseByContractLoader
                        ("HouseLease", HouseLease, "getAgreement", list);
                JSONArray jsonArray = JSONArray.parseArray(transactionResponse1.getValues());
                LeaseContract leaseContract = new LeaseContract();
                //选手填写部分
                //读取合同相关数据
                leaseContract.setHourseId(Long.valueOf(jsonArray.get(0).toString()));
                leaseContract.setLandlord(jsonArray.get(1).toString());
                leaseContract.setTenant(jsonArray.get(2).toString());
                leaseContract.setSign(Long.valueOf(jsonArray.get(3).toString()));
                leaseContract.setRentAmount(Long.valueOf(jsonArray.get(4).toString()));
                leaseContract.setLeaseStartDate(Long.valueOf(jsonArray.get(5).toString()));
                leaseContract.setLeaseEndDate(Long.valueOf(jsonArray.get(6).toString()));
                leaseContract.setAgreementId(Long.valueOf(jsonArray.get(7).toString()));
                leaseContracts.add(leaseContract);
            }
        }
        return Result.success(leaseContracts);
    }

    /**
     * 创建合同
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/addRentalContract", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> addRentalContract(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String _tenant = (String) jsonParam.get("tenant");
        String _rentAmount = jsonParam.get("rentAmount").toString();
        String _leaseStartDate = jsonParam.get("leaseStartDate").toString();
        String _leaseEndDate = jsonParam.get("leaseEndDate").toString();
        //获取房屋编号
        Integer houseId = (Integer) jsonParam.get("hourseId");

        List<Object> list = new ArrayList<>();
        //选手填写部分
        //封装参数
        list.add(houseId);
        list.add(_tenant);
        list.add(_rentAmount);
        list.add(_leaseStartDate);
        list.add(_leaseEndDate);
        AssembleTransactionProcessor sdk = sdk("HouseLease",
                abiHouseLease, binHouseLease);
        //调用创建合同
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader
                ("HouseLease", HouseLease, "createAgreement", list);
        if (transactionResponse.getReceiptMessages().equals("Success")) {
            LeaseContract leaseContract = new LeaseContract();
            //选手填写部分
            //将合同数据写入数据库中
            leaseContract.setHourseId(Long.valueOf(houseId));
            leaseContract.setTenant(_tenant);
            leaseContract.setRentAmount(Long.valueOf(_rentAmount));
            leaseContract.setLeaseStartDate(Long.valueOf(_leaseStartDate));
            leaseContract.setLeaseEndDate(Long.valueOf(_leaseEndDate));

            leaseContractMapper.insert(leaseContract);
        }
        outPut.put("message", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }


    /**
     * 签署合同
     *
     * @param jsonParam
     * @return
     * @throws ContractException
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/signContract", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> signContract(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String _agreementId = jsonParam.get("agreementId").toString();
        String type = jsonParam.getString("type");
        List<Object> list = new ArrayList<>();
        list.add(new BigInteger(_agreementId));
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        //房东签名
        if (type.equals("1")) {
            TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "landlordSigning", list);
            outPut.put("status", transactionResponse.getReceiptMessages());
        } else if (type.equals("2")) {
            //租户签名
            TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "tenantSigning", list);
            outPut.put("status", transactionResponse.getReceiptMessages());
        }
        return Result.success(outPut);
    }


    //待签署列表
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getLandlordContractsnosign", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<LeaseContract>> getLandlordContractsnosign(@RequestBody JSONObject jsonParam) throws Exception {
        String _landlord = (String) jsonParam.get("landlord");
        String type = (String) jsonParam.get("type");
        JSONArray array = new JSONArray();
        array.add(_landlord);
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "getLandlordAgreementIds", array);
        String values = transactionResponse.getValues();
        String substring = values.substring(0, values.length() - 2);
        String substring1 = substring.substring(2);
        String[] split = substring1.split(",");
        List<LeaseContract> leaseContracts = new ArrayList<>();
        for (String s : split) {
            if (!StringUtils.isEmpty(s)) {
                List<Object> list = new ArrayList<>();
                list.add(new BigInteger(s));
                TransactionResponse transactionResponse1 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreements", list);
                JSONArray jsonArray = JSONArray.parseArray(transactionResponse1.getValues());
                LeaseContract leaseContract = new LeaseContract();
                leaseContract.setAgreementId(Long.valueOf(s));
                leaseContract.setLandlord(_landlord);
                leaseContract.setTenant(jsonArray.get(2).toString());
                leaseContract.setRentAmount(Long.valueOf(jsonArray.get(3).toString()));
                leaseContract.setLeaseStartDate(Long.valueOf(jsonArray.get(4).toString()));
                leaseContract.setLeaseEndDate(Long.valueOf(jsonArray.get(5).toString()));
                leaseContract.setSign(Long.valueOf(jsonArray.get(6).toString()));
                TransactionResponse transactionResponse2 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreementAccounts", list);
                JSONArray jsonArray2 = JSONArray.parseArray(transactionResponse2.getValues());
                leaseContract.setPaidAmount(jsonArray2.getLong(0));
                leaseContract.setDepositAmount(jsonArray2.getLong(1));
                leaseContract.setSpendedToken(jsonArray2.getLong(2));
                //判断是否为带签署：0 都未签署、2 房东签署，租客未签署、3 租客签署，房东未签署
                //如果当前登录为房东1，则查询0，3情况
                //如果当前登录为租客2，则查询0，2情况
                if (type.equals("1")) {
                    if (leaseContract.getSign() == 0 || leaseContract.getSign() == 3) {
                        leaseContracts.add(leaseContract);
                    }
                } else if (type.equals("2")) {
                    if (leaseContract.getSign() == 0 || leaseContract.getSign() == 2) {
                        leaseContracts.add(leaseContract);
                    }
                } else {
                    if (leaseContract.getSign() == 0 || leaseContract.getSign() == 2 || leaseContract.getSign() == 3) {
                        leaseContracts.add(leaseContract);
                    }
                }
            }
        }
        return Result.success(leaseContracts);
    }

    //有效合同
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getLandlordContractsactive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<LeaseContract>> getLandlordContractsactive(@RequestBody JSONObject jsonParam) throws Exception {

        String _landlord = (String) jsonParam.get("landlord");
        JSONArray array = new JSONArray();
        array.add(_landlord);
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "getLandlordAgreementIds", array);
        String values = transactionResponse.getValues();
        String substring = values.substring(0, values.length() - 2);
        String substring1 = substring.substring(2);
        String[] split = substring1.split(",");
        List<LeaseContract> leaseContracts = new ArrayList<>();
        for (String s : split) {
            if (!StringUtils.isEmpty(s)) {
                List<Object> list = new ArrayList<>();
                list.add(new BigInteger(s));
                TransactionResponse transactionResponse1 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreements", list);
                JSONArray jsonArray = JSONArray.parseArray(transactionResponse1.getValues());
                LeaseContract leaseContract = new LeaseContract();
                leaseContract.setAgreementId(Long.valueOf(s));
                leaseContract.setLandlord(_landlord);
                leaseContract.setTenant(jsonArray.get(2).toString());
                leaseContract.setRentAmount(Long.valueOf(jsonArray.get(3).toString()));
                leaseContract.setLeaseStartDate(Long.valueOf(jsonArray.get(4).toString()));
                leaseContract.setLeaseEndDate(Long.valueOf(jsonArray.get(5).toString()));
                leaseContract.setSign(Long.valueOf(jsonArray.get(6).toString()));
                TransactionResponse transactionResponse2 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreementAccounts", list);
                JSONArray jsonArray2 = JSONArray.parseArray(transactionResponse2.getValues());
                leaseContract.setPaidAmount(jsonArray2.getLong(0));
                leaseContract.setDepositAmount(jsonArray2.getLong(1));
                leaseContract.setSpendedToken(jsonArray2.getLong(2));
                if (leaseContract.getSign() == 1) {
                    leaseContracts.add(leaseContract);
                }
            }
        }
        return Result.success(leaseContracts);
    }


    //无效合同
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getLandlordContractsnoactive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<LeaseContract>> getLandlordContractsnoactive(@RequestBody JSONObject jsonParam) throws Exception {
        String _landlord = jsonParam.get("landlord").toString();
        JSONArray array = new JSONArray();
        array.add(_landlord);
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        ;
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "getLandlordAgreementIds", array);
        String values = transactionResponse.getValues();
        String substring = values.substring(0, values.length() - 2);
        String substring1 = substring.substring(2);
        String[] split = substring1.split(",");
        List<LeaseContract> leaseContracts = new ArrayList<>();
        for (String s : split) {
            if (!StringUtils.isEmpty(s)) {
                List<Object> list = new ArrayList<>();
                list.add(new BigInteger(s));
                TransactionResponse transactionResponse1 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreements", list);
                JSONArray jsonArray = JSONArray.parseArray(transactionResponse1.getValues());
                LeaseContract leaseContract = new LeaseContract();
                leaseContract.setAgreementId(Long.valueOf(s));
                leaseContract.setLandlord(_landlord);
                leaseContract.setTenant(jsonArray.get(2).toString());
                leaseContract.setRentAmount(Long.valueOf(jsonArray.get(3).toString()));
                leaseContract.setLeaseStartDate(Long.valueOf(jsonArray.get(4).toString()));
                leaseContract.setLeaseEndDate(Long.valueOf(jsonArray.get(5).toString()));
                leaseContract.setSign(Long.valueOf(jsonArray.get(6).toString()));
                TransactionResponse transactionResponse2 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreementAccounts", list);
                JSONArray jsonArray2 = JSONArray.parseArray(transactionResponse2.getValues());
                leaseContract.setPaidAmount(jsonArray2.getLong(0));
                leaseContract.setDepositAmount(jsonArray2.getLong(1));
                leaseContract.setSpendedToken(jsonArray2.getLong(2));
                if (leaseContract.getSign() == 4 || leaseContract.getSign() == 5 || leaseContract.getSign() == 6) {
                    leaseContracts.add(leaseContract);
                }
            }
        }
        return Result.success(leaseContracts);
    }

    //租客查看合同列表
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getTenantContracts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<LeaseContract>> getTenantContracts(@RequestBody JSONObject jsonParam) throws Exception {
        String _tenant = (String) jsonParam.get("tenant");
        JSONArray array = new JSONArray();
        array.add(_tenant);
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "getTenantAgreementIds", array);
        String values = transactionResponse.getValues();
        String substring = values.substring(0, values.length() - 2);
        String substring1 = substring.substring(2);
        String[] split = substring1.split(",");
        List<LeaseContract> leaseContracts = new ArrayList<>();
        for (String s : split) {
            if (!StringUtils.isEmpty(s)) {
                List<Object> list = new ArrayList<>();
                list.add(new BigInteger(s));
                TransactionResponse transactionResponse1 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreements", list);
                JSONArray jsonArray = JSONArray.parseArray(transactionResponse1.getValues());
                LeaseContract leaseContract = new LeaseContract();
                leaseContract.setAgreementId(Long.valueOf(s));
                leaseContract.setLandlord(_tenant);
                leaseContract.setTenant(jsonArray.get(2).toString());
                leaseContract.setRentAmount(Long.valueOf(jsonArray.get(3).toString()));
                leaseContract.setLeaseStartDate(Long.valueOf(jsonArray.get(4).toString()));
                leaseContract.setLeaseEndDate(Long.valueOf(jsonArray.get(5).toString()));
                leaseContract.setSign(Long.valueOf(jsonArray.get(6).toString()));
                TransactionResponse transactionResponse2 = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "agreementAccounts", list);
                JSONArray jsonArray2 = JSONArray.parseArray(transactionResponse2.getValues());
                leaseContract.setPaidAmount(jsonArray2.getLong(0));
                leaseContract.setDepositAmount(jsonArray2.getLong(1));
                leaseContract.setSpendedToken(jsonArray2.getLong(2));
                leaseContracts.add(leaseContract);
            }
        }
        return Result.success(leaseContracts);
    }

    //缴纳房租
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/payRent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> payRent(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String _rentAmount = jsonParam.get("rentAmount").toString();
        String _agreementId = jsonParam.get("agreementId").toString();
        JSONArray array = new JSONArray();
        array.add(new BigInteger(_agreementId));
        array.add(new BigInteger(_rentAmount));
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "payment", array);
        outPut.put("message", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }

    //账户充值
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/transferAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> transferAccounts(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String _rentAmount = jsonParam.get("amount").toString();
        String address = jsonParam.get("address").toString();
        JSONArray array = new JSONArray();
        array.add(new BigInteger(_rentAmount));
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "transferAccounts", array);
        outPut.put("message", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }


    //房东取消合同
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/terminateContractByLandlord", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> terminateContractByLandlord(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String _agreementId = jsonParam.get("agreementId").toString();
        JSONArray array = new JSONArray();
        array.add(new BigInteger(_agreementId));
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "landlordTerminates", array);
        outPut.put("message", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }

    //租户取消合同
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/terminateContractByTenant", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<JSONObject> terminateContractByTenant(@RequestBody JSONObject jsonParam) throws Exception {
        JSONObject outPut = new JSONObject();
        String address = jsonParam.get("address").toString();
        String _agreementId = jsonParam.get("agreementId").toString();
        JSONArray array = new JSONArray();
        array.add(new BigInteger(_agreementId));
        AssembleTransactionProcessor sdk = sdk("HouseLease", abiHouseLease, binHouseLease);
        TransactionResponse transactionResponse = sdk.sendTransactionAndGetResponseByContractLoader("HouseLease", HouseLease, "tenantTerminates", array);
        outPut.put("msg", transactionResponse.getReceiptMessages());
        return Result.success(outPut);
    }


    public AssembleTransactionProcessor sdk(String contractName, String abi, String bin) throws Exception {
        String[] possibilities = accountFilePaths.split(",");
        for (int i = 0; i < possibilities.length; i++) {
            try {
                String filePath = possibilities[i] + "key.txt";
                String s = readFileByPath(filePath);
                if (s == null) {
                    continue;
                }
                CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
                CryptoKeyPair keyPair = cryptoSuite.getKeyPairFactory().createKeyPair(s);
                System.out.println("===============");
                System.out.println(keyPair);
                System.out.println(s);
                // AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, path+"/abi", path+"/bin/ecc");
                AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, contractName, abi, bin);
                return transactionProcessor;
            } catch (Exception ex) {
                System.out.println("EEEEEEror");
                Thread.sleep(5000);
            }
        }
        throw new IOException("failed to read file");
    }

    public static String readFileByPath(String fullFilePath) throws IOException {
        return IOUtil.readResourceAsString(fullFilePath);
    }

}
