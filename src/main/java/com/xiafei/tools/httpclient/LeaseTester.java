package com.xiafei.tools.httpclient;

import com.xiafei.tools.common.JsonUtil;
import com.xiafei.tools.common.encrypt.rsa.RSAUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * <P>Description: 测试融资租赁中介平台工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/22</P>
 * <P>UPDATE DATE: 2018/3/22</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class LeaseTester {

    private static final String URL = "http://39.106.38.32/lease/service/toFund";
//    private static final String URL = "http://localhost:9888/lease/service/toFund";

    private static final String APPLY_NO = "2018032618100100844C0A882DF00048";

    public static void main(String[] args) {
        // 状态回调测试
        statusTest();
        // 还款计划推送测试
//        pushPlan();
        // 划扣记录推送测试
//        pushDeduct();
        // 划扣失败记录推送测试
//        pushFailedDeduct();
    }

    /**
     * 状态回调测试.
     */
    private static void statusTest() {
        // 申请单号列表，如果状态是1审核通过或4签名设备合同已回调，则只能传递一个申请单号.
        final ArrayList<String> applyNoList = new ArrayList<>();
        applyNoList.add(APPLY_NO);
        // 做幂等判断的这批申请单状态变更唯一序号
        final String batchNo = "8d314b8d52bc48359c361698cedc633a";
//        // 租赁贷款开始日期
//        final String startDate = "20180420";
//        // 租赁贷款结束日期
//        final String endDate = "20190420";
        // 签名合同文件路径
        final String contractPath = "/uploadfile/20170832/3018032110372800275000100009/FJDL103100/1/sourceFile/APPLY_CONTRACT.pdf";
        // 0-创建成功-审核中,1-审核通过,2-审核拒绝,3-设备安装已确认,4-签名设备合同已回调,5-待放款,6-放款成功,7-放款失败
        final String status = "1";

        final TreeMap<String, Object> data = new TreeMap<>();
        data.put("status", status);
        data.put("applyNoList", applyNoList);
        data.put("signedFilePath", contractPath);
//        data.put("startDate", startDate);
//        data.put("endDate", endDate);
        data.put("batchNo", batchNo);
        // 调用请求
        sendToPt(data, "lease_apply_callback");

    }

    /**
     * 推送还款计划测试.
     */
    private static void pushPlan() {
        final TreeMap<String, Object> data = new TreeMap<>();
        data.put("infoList", buildPlanInfoList());
        sendToPt(data, "plan_push");
    }


    /**
     * 推送划扣记录测试.
     */
    private static void pushDeduct() {
        final TreeMap<String, Object> data = new TreeMap<>();
        data.put("infoList", buildDeductInfoList());
        data.put("serialNo", UUID.randomUUID().toString().replace("-", ""));
        sendToPt(data, "deduct_push");
    }

    /**
     * 推送划扣失败记录测试.
     */
    private static void pushFailedDeduct() {
        final TreeMap<String, Object> data = new TreeMap<>();
        data.put("infoList", buildFailedDeductInfoList());
        data.put("serialNo", UUID.randomUUID().toString().replace("-", ""));
        sendToPt(data, "deduct_fail_push");
    }

    /**
     * 构建划扣信息列表.
     *
     * @return
     */
    private static List<Map<String, Object>> buildDeductInfoList() {
        final List<Map<String, Object>> result = new ArrayList<>();

        final Map<String, Object> infoMap0 = new TreeMap<>();
        result.add(infoMap0);
        final String deductNo = UUID.randomUUID().toString().replace("-", "");
        infoMap0.put("applyNo", APPLY_NO);
        infoMap0.put("deductNo", deductNo);
        //0-客户银行卡，1-保证金账户
        infoMap0.put("acctType", "1");
//        infoMap0.put("bankNo", RSAUtil.encryptTemp("yhk", "LEASE"));
        infoMap0.put("amount", "10000");
        infoMap0.put("repayDate", "20180322");
        infoMap0.put("subject", "0");
        final Map<String, Object> infoMap1 = new TreeMap<>();
        result.add(infoMap1);

        infoMap1.put("applyNo", APPLY_NO);
        infoMap1.put("deductNo", deductNo);
        //0-客户银行卡，1-保证金账户
        infoMap1.put("acctType", "0");
//        infoMap1.put("bankNo", RSAUtil.encryptTemp("yhk", "LEASE"));
        infoMap1.put("amount", "10000");
        infoMap1.put("repayDate", "20180322");
        infoMap1.put("subject", "0");
        final Map<String, Object> infoMap2 = new TreeMap<>();
        result.add(infoMap0);

        infoMap2.put("applyNo", APPLY_NO);
        infoMap2.put("deductNo", deductNo);
        //0-客户银行卡，1-保证金账户
        infoMap2.put("acctType", "1");
//        infoMap2.put("bankNo", RSAUtil.encryptTemp("yhk", "LEASE"));
        infoMap2.put("amount", "10000");
        infoMap2.put("repayDate", "20180322");
        infoMap2.put("subject", "0");
        return result;

    }

    /**
     * 构建划扣失败信息列表.
     *
     * @return
     */
    private static List<Map<String, Object>> buildFailedDeductInfoList() {
        final List<Map<String, Object>> result = new ArrayList<>();

        final Map<String, Object> infoMap0 = new TreeMap<>();
        result.add(infoMap0);
        infoMap0.put("applyNo", APPLY_NO);
        infoMap0.put("failNo", "1");
        //0-客户银行卡，1-保证金账户
        infoMap0.put("acctType", "1");
//        infoMap0.put("bankNo", RSAUtil.encryptTemp("yhk", "LEASE"));
        infoMap0.put("amount", "10000");
        infoMap0.put("repayDate", "20180322");
        //0-本金，1-利息，2-罚息
        infoMap0.put("subject", "0");
        infoMap0.put("reason", "很任性的失败了");
        return result;

    }

    /**
     * 构建还款计划信息列表.
     *
     * @return
     */
    private static List<Map<String, Object>> buildPlanInfoList() {
        final List<Map<String, Object>> result = new ArrayList<>();
        // 还款期限字段名
        final String period = "period";
        // 月还款额字段名
        final String monthAmount = "normalAmount";

        // ===========每一个还款计划信息map对应一个申请单号
        final TreeMap<String, Object> infoMap0 = new TreeMap<>();
        result.add(infoMap0);
        infoMap0.put("applyNo", APPLY_NO);
        infoMap0.put(period, "12");
        infoMap0.put(monthAmount, "1000000");
        // 开始日期
        final Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, 2018);
        startDate.set(Calendar.MONTH, 4);
        startDate.set(Calendar.DAY_OF_MONTH, 20);
        infoMap0.put("planList", buildPlanList(Integer.parseInt(infoMap0.get(period).toString()),
                Integer.parseInt(infoMap0.get(monthAmount).toString()), startDate));
        return result;
    }

    /**
     * 构建一个还款计划信息的所有还款计划列表
     *
     * @param period
     * @param monthAmount
     * @param startDate
     * @return
     */
    private static List<Map<String, Object>> buildPlanList(final Integer period, final Integer monthAmount, final Calendar startDate) {
        final List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < period; i++) {

            result.add(getPlanInfoMap(monthAmount, startDate, i == 0 ? "1" : "0"));
        }

        return result;
    }

    /**
     * 获取一条还款计划信息.
     *
     * @param monthAmount
     * @param startDate
     * @param status
     * @return
     */
    private static Map<String, Object> getPlanInfoMap(final Integer monthAmount, final Calendar startDate, final String status) {
        final TreeMap<String, Object> result = new TreeMap<>();
        result.put("startDate", getYyyyMmDdStr(startDate));
        startDate.add(Calendar.MONTH, 1);
        result.put("dueDate", getYyyyMmDdStr(startDate));
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        result.put("endDate", getYyyyMmDdStr(startDate));
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        // 0-待执行，1-正在执行，2-逾期执行中，3-已结清
        result.put("status", status);
//        result.put("overdueDays", "");
        result.put("subjectList", buildSubjectList(monthAmount));
        return result;
    }

    /**
     * 生成科目列表.
     *
     * @param monthAmount 月还款额
     * @return
     */
    private static List<Map<String, Object>> buildSubjectList(final Integer monthAmount) {
        final List<Map<String, Object>> result = new ArrayList<>();
        final TreeMap<String, Object> map0 = new TreeMap<>();
        final TreeMap<String, Object> map1 = new TreeMap<>();
//        final TreeMap<String, Object> map2 = new TreeMap<>();
        result.add(map0);
        result.add(map1);
//        result.add(map2);
        // 0-本金，1-利息，2-罚息
        map0.put("subject", "0");
        map0.put("amount", monthAmount);
        map0.put("status", "1");
        map1.put("subject", "1");
        map1.put("amount", "1");
        map1.put("status", "1");
//        map2.put("subject", "2");
//        map2.put("amount", "0");
//        map2.put("status", "3");
        return result;
    }

    /**
     * 根据calendar对象拿到yyyyMMDD格式的字符串.
     *
     * @param startDate
     * @return
     */
    private static String getYyyyMmDdStr(final Calendar startDate) {

        return String.format("%04d", startDate.get(Calendar.YEAR))
                + String.format("%02d", startDate.get(Calendar.MONTH))
                + String.format("%02d", startDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 发送请求到平台.
     *
     * @param data    数据map
     * @param service 服务名
     */
    private static void sendToPt(final TreeMap<String, Object> data, final String service) {
        final Map<String, Object> reqMap = new HashMap<>();
        final String plain = JsonUtil.toJson(data);
        final String sign = RSAUtil.sign(plain);
        reqMap.put("sign", sign);
        reqMap.put("service", service);
        reqMap.put("version", "1.0");
        reqMap.put("data", data);
        reqMap.put("systemId", "FUND_JX");
        final String reqJson = JsonUtil.toJson(reqMap);
//        System.out.println("服务器返回：" + PostUtil.postJson(URL, reqJson));
    }
}
