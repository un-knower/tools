package com.xiafei.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiafei.tools.common.check.CheckUtils;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/11</P>
 * <P>UPDATE DATE: 2018/4/11</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Service
public class Test implements Serializable {

    private static ExecutorService POOL = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws UnknownHostException, SocketException {
//        String json = "{\"systemId\":\"AGENCY_RM\",\"data\":{\"applyNo\":\"2018041114214100797C0A882DF00017\",\"confirmContractPath\":\"/files/apply/2018041114214100797C0A882DF00017/GOODS_CONFIRM_CONTRACT/GOODS_CONFIRM_CONTRACT.pdf\",\"goodsList\":[{\"goodsModel\":\"EP901\",\"serialNo\":\"1,2\",\"softVersion\":\"高级版\"}]},\"service\":\"lease_apply_goods_confirm\",\"sign\":\"aQYaFa1m1pvFZsu+Qa4dQH/PWdb9YV2cTgdgvSN29fjDwq4MBHTEiS5+0l3N1IFyyI8EKpMOSTFqnfDHNaQXjFJg2LRdidbiILKJGA1u6/cVtlMFsamI+qmprPmDjUnR0HxdLrIwHCIJu2jQaTCxMNRoxgagfU/BfMVeXBQhBIE\\u003d\",\"version\":\"1.0\",\"serialNo\":\"2018041118473700204C0A882DF00048\"}";
//        OuterReqVo reqVo = JsonUtil.fromJson(json, OuterReqVo.class);
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            OuterReqVo reqVo1 = JsonUtil.fromJson(json, OuterReqVo.class);
//            json = JsonUtil.toJson(reqVo1);
//        }
//        System.out.println(System.currentTimeMillis() - start);
//        System.out.println(Arrays.toString("\u003d".getBytes()));
////        System.out.println(Arrays.toString("=".getBytes()));
//        final NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
//        final byte[] hardwareAddress = byInetAddress.getHardwareAddress();
//        System.out.println(ArrayUtils.toString(hardwareAddress));
//        System.out.println(Arrays.toString(hardwareAddress));
//        final Test obj = new Test();
//        long start = System.currentTimeMillis();
//        final int times = 1000000;
//        for (int i = 0; i < times; i++) {
//            SerializationUtils.clone(obj);
//        }
//        System.out.println("SerializationUtils 耗时:" + (System.currentTimeMillis() - start));
//        start = System.currentTimeMillis();
//        for (int i = 0; i < times; i++) {
//            CloneUtil.clone(obj);
//        }
//        System.out.println("CloneUtil 耗时:" + (System.currentTimeMillis() - start));

//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000000; i++) {
//            final String code = String.format("%06d", new Random().nextInt(999999));
////            System.out.println(code);
//            if (code.length() != 6) {
//                throw new RuntimeException();
//            }
//        }
//        System.out.println("CloneUtil 耗时:" + (System.currentTimeMillis() - start));

        CountDownLatch c = new CountDownLatch(2);
        POOL.execute(new Runnable() {
            @Override
            public void run() {
                c.countDown();
            }
        });

        POOL.execute(new Runnable() {
            @Override
            public void run() {
                c.countDown();
            }
        });
        try {
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("执行完了");
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void testSchedule() {
        System.out.println("task1");
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void testSchedule2() {
        System.out.println("task2");
    }


    /**
     * <P>Description: 外部调用请求vo（通用）. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:   齐霞飞 </P>
     * <P>CREATE DATE: 2018/3/2</P>
     * <P>UPDATE DATE: 2018/3/2</P>
     *
     * @author qixiafei
     * @version 1.0
     * @since java 1.8.0
     */
    @Data
    public static class OuterReqVo {

        /**
         * 接口名称.
         */
        private String service;

        /**
         * 接口版本号.
         */
        private String version;

        /**
         * 签名.
         */
        private String sign;

        /**
         * 平台为调用者分配的合作方编码.
         */
        private String systemId;

        /**
         * 幂等标识序列号.
         */
        private String serialNo;

        /**
         * 业务数据map.
         */
        private ApplySubmitVo data;
    }


    @Data
    public static class ApplySubmitVo {
        /**
         * 销售员手机号.
         */
        private String salesmanPhone;

        /**
         * 渠道.
         */
        private String channel;

        /**
         * 融资金额.
         */
        private String amount;

        /**
         * 租赁申请单号.
         */
        private String applyNo;

        /**
         * 选择的租赁期限.
         */
        private String period;

        /**
         * 银行卡号.
         */
        private String cardNo;

        /**
         * 开户行支行名称.
         */
        private String bankBranchName;

        /**
         * 银行卡照片保存路径.
         */
        @CheckUtils.PropertySkipCheck
        private String cardImgPath;

        /**
         * 营业执照保存路径.
         */
        @JsonProperty(value = "bLImgPath")
        private String bLImgPath;

        /**
         * 营业执照信息.
         */
        @JsonProperty(value = "bLInfo")
        private B_LInfo bLInfo;

        /**
         * 经营场所性质.
         */
        private Byte siteType;

        /**
         * 餐饮服务许可证图片保存路径.
         */
        @JsonProperty(value = "cSLImgPath")
        private String cSLImgPath;

        /**
         * 餐饮服务许可证信息.
         */
        @JsonProperty(value = "cLInfo")
        private C_LInfo cLInfo;

        /**
         * 申请人身份证信息.
         */
        private IdCardInfo idCardInfo;

        /**
         * 婚姻状况.
         */
        private String marStatus;

        /**
         * 现居住状况.
         */
        private String residentStatus;

        /**
         * 现居住年限.
         */
        private String residentYear;

        /**
         * 最高学历.
         */
        private String highestEdu;

        /**
         * 毕业时间yyyyMMdd.
         */
        private String gradTime;

        /**
         * 税前年收入.
         */
        private Long preTaxIncome;

        /**
         * 手机号码.
         */
        private String phone;

        /**
         * 家庭住址.
         */
        private String homeAddr;

        /**
         * 户籍所在地.
         */
        private String domicileAddr;

        /**
         * 户籍类型.
         */
        private String domicileType;

        /**
         * 用户签名base64-svg图片.
         */
        private String userSignSvgBase64;

        /**
         * 固定电话.
         */
        @CheckUtils.PropertySkipCheck
        private String fixedPhone;

        /**
         * 电子邮箱.
         */
        private String email;

        /**
         * 配偶身份证信息.
         */
        @CheckUtils.PropertySkipCheck
        private IdCardInfo mateIdCardInfo;

        /**
         * 配偶电话.
         */
        @CheckUtils.PropertySkipCheck
        private String matePhone;

        /**
         * 配偶工作单位.
         */
        @CheckUtils.PropertySkipCheck
        private String mateWork;

        /**
         * 场所房产证保存路径.
         */
        @CheckUtils.PropertySkipCheck
        private List<String> sitePrtyCertPathList;

        /**
         * 场所租赁合同保存路径.
         */
        @CheckUtils.PropertySkipCheck
        private List<String> siteContractPathList;

        /**
         * 场景照片保存路径.
         */
        @CheckUtils.PropertySkipCheck
        private List<String> sitePhotePathList;

        /**
         * 第一联系人姓名.
         */
        @JsonProperty(value = "fLinkmanName")
        private String fLinkmanName;

        /**
         * 第一联系人关系.
         */
        @JsonProperty(value = "fLinkmanRel")
        private String fLinkmanRel;

        /**
         * 第一联系人电话.
         */
        @JsonProperty(value = "fLinkmanPhone")
        private String fLinkmanPhone;

        /**
         * 第二联系人姓名.
         */
        @CheckUtils.PropertySkipCheck
        @JsonProperty(value = "sLinkmanName")
        private String sLinkmanName;

        /**
         * 第二联系人关系.
         */
        @CheckUtils.PropertySkipCheck
        @JsonProperty(value = "sLinkmanRel")
        private String sLinkmanRel;

        /**
         * 第二联系人电话.
         */
        @CheckUtils.PropertySkipCheck
        @JsonProperty(value = "sLinkmanPhone")
        private String sLinkmanPhone;

    }

    /**
     * <P>Description: 营业执照信息. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:   齐霞飞 </P>
     * <P>CREATE DATE: 2018/3/7</P>
     * <P>UPDATE DATE: 2018/3/7</P>
     *
     * @author qixiafei
     * @version 1.0
     * @since java 1.8.0
     */
    @Data
    public static class B_LInfo {

        /**
         * 统一社会信用代码.
         */
        private String creditCode;

        /**
         * 公司名称.
         */
        private String coName;

        /**
         * 经营状态描述.
         */
        private String opState;

        /**
         * 注册地址.
         */
        private String regAddr;

        /**
         * 法定代表人.
         */
        private String legalPerson;

        /**
         * 注册资本.
         */
        private String regCapital;

        /**
         * 成立日期yyyyMMdd.
         */
        private String regDate;

        /**
         * 营业期限开始yyyyMMdd.
         */
        private String termStart;

        /**
         * 营业期限结束yyyyMMdd.
         */
        private String termEnd;

        /**
         * 经营范围.
         */
        private String opScope;

        /**
         * 企业类型.
         */
        private String coType;

        /**
         * 行业.
         */
        private String trade;

        /**
         * 核准日期yyyyMMdd.
         */
        private String apprDate;

        /**
         * 人员数量.
         */
        private String staff;
    }


    /**
     * <P>Description: 餐饮服务许可证信息. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:   齐霞飞 </P>
     * <P>CREATE DATE: 2018/3/7</P>
     * <P>UPDATE DATE: 2018/3/7</P>
     *
     * @author qixiafei
     * @version 1.0
     * @since java 1.8.0
     */
    @Data
    public static class C_LInfo {


        /**
         * 餐饮许可证编号.
         */
        private String licenceNo;

        /**
         * 单位名称.
         */
        private String coName;

        /**
         * 法定代表人.
         */
        private String legalPerson;

        /**
         * 地址.
         */
        private String addr;

        /**
         * 发证日期yyyyMMdd.
         */
        private String issueDate;

        /**
         * 发证机关.
         */
        private String issueAuthority;

        /**
         * 有效日期开始yyyyMMdd.
         */
        private String termStart;

        /**
         * 有效日期结束yyyyMMdd.
         */
        private String termEnd;

        /**
         * 经营范围.
         */
        private String opScope;
    }


    /**
     * <P>Description: 身份证信息. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:   齐霞飞 </P>
     * <P>CREATE DATE: 2018/3/7</P>
     * <P>UPDATE DATE: 2018/3/7</P>
     *
     * @author qixiafei
     * @version 1.0
     * @since java 1.8.0
     */
    @Data
    public class IdCardInfo {

        /**
         * 姓名.
         */
        private String name;

        /**
         * 性别: 0-男，1-女.
         */
        private String sex;

        /**
         * 民族.
         */
        private String nation;

        /**
         * 住址.
         */
        private String addr;

        /**
         * 身份证号.
         */
        private String idNo;

        /**
         * 有效期从yyyyMMdd.
         */
        private String termStart;

        /**
         * 有效期至yyyyMMdd.
         */
        private String termEnd;

        /**
         * 发证机关.
         */
        private String issueAuthority;
    }

}
