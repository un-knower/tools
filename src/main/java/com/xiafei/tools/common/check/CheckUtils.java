package com.xiafei.tools.common.check;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiafei.tools.common.JsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <P>Description: 检查工具类，与业务耦合. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/11/23</P>
 * <P>UPDATE DATE: 2017/11/23</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.7.0
 */
@Slf4j
public class CheckUtils {

    private static final String nullDesc = "必填参数为空";

    private CheckUtils() {

    }

    /**
     * 在需要跳过检查的属性上加这个注解.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PropertySkipCheck {

    }

    /**
     * 在需要跳过检查的参数上加这个注解.
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamSkipCheck {

    }


    /**
     * 检查对象及字段是否为空，最多遍历两层.
     *
     * @param objs 要检查的对象，可变.
     */
    public static void checkNull(Object... objs) throws Exception {
        if (objs == null) {
            throw new Exception(nullDesc);
        }
        for (Object obj : objs) {
            checkObj(obj, null);
        }
    }

    /**
     * 检查对象字段是否为空,最多向下遍历两层..
     *
     * @param obj   要检查的对象
     * @param clazz 要检查的对象class
     * @param name
     */
    private static void checkPojo(final Object obj, final Class clazz, final String name) throws Exception {
        final List<Field> fieldList = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class tempClass = clazz;
        while (true) {
            tempClass = tempClass.getSuperclass();
            if (tempClass == null || tempClass == Object.class) {
                break;
            }
            Field[] fields = tempClass.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                fieldList.addAll(new ArrayList<>(Arrays.asList(fields)));
            }
        }
        final Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);

        for (Field field : fields) {
            field.setAccessible(true);
            // 如果有跳过注解的字段放过检查
            if (field.getAnnotation(PropertySkipCheck.class) != null) {
                continue;
            }
            final Object propertyValue;
            try {
                propertyValue = field.get(obj);
            } catch (IllegalAccessException e) {
                log.error("反射获取属性值对象报错", e);
                throw new Exception(field.getName() + "校验失败");
            }
            checkObj(propertyValue, name == null ? field.getName() : name.concat(".").concat(field.getName()));
        }
    }


    private static void checkObj(final Object obj, final String name) throws Exception {
        final String errorMsg;
        if (name != null) {
            errorMsg = name.concat("不能为空");
        } else {
            errorMsg = nullDesc;
        }
        // 如果对象为空，抛出异常
        if (obj == null) {
            throw new Exception(errorMsg);
        }
        final Class clazz = obj.getClass();
        // 字符串判断是否是空串
        if (clazz == String.class) {
            if (obj.toString().trim().equals("")) {
                throw new Exception(errorMsg);
            }
        }

        // 如果是可以遍历的类型，比如list，set，遍历处理参数
        if (Iterable.class.isAssignableFrom(clazz)) {
            int count = 0;
            for (Object property : (Iterable) obj) {
                count++;
                checkObj(property, name);
            }
            if (count == 0) {
                throw new Exception(errorMsg);
            }
            return;
        }

        // 如果是pojo，判断属性是否含有空的
        if (isPojo(clazz)) {
            // 如果不是基本类型，利用内省机制检查字段
            checkPojo(obj, clazz, name);
        }
    }

    /**
     * 判断对象是否是jdk中定义之外的对象.
     *
     * @param clazz 对象的class
     * @return
     */
    private static boolean isPojo(final Class clazz) {
        return clazz != Date.class && clazz != Character.class && clazz != Boolean.class
                && !Number.class.isAssignableFrom(clazz) && !Iterable.class.isAssignableFrom(clazz)
                && String.class != clazz && !InputStream.class.isAssignableFrom(clazz)
                && !OutputStream.class.isAssignableFrom(clazz);
    }

    public static void main(String[] args) throws Exception {

        checkNull(new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test";
            }

            @Override
            public String getContentType() {
                return "test";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(new File("./temp/APPLY_CONTRACT.pdf"));
            }

            @Override
            public void transferTo(final File dest) throws IOException, IllegalStateException {

            }
        });
    }


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
        @CheckUtils.PropertySkipCheck
        private String termStart;

        /**
         * 营业期限结束yyyyMMdd.
         */
        @CheckUtils.PropertySkipCheck
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


    @Getter
    @Setter
    public static class KryApplyReqVo extends BaseReqVo {

        /**
         * 四要素手机验证码.
         */
        private String validateCode;

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
         * 银行卡预留手机号.
         */
        private String bankCardPhone;

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
         * 设备选择列表,key为物资编码,value为数量.
         */
        private List<K_VInfo> goodsList;

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
         * 从业年限.
         */
        private Integer workYears;

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


        @Override
        public String toString() {
            return "KryApplyReqVo{" +
                    "validateCode = '" + validateCode + '\'' +
                    ", salesmanPhone='" + salesmanPhone + '\'' +
                    ", channel='" + channel + '\'' +
                    ", amount='" + amount + '\'' +
                    ", applyNo='" + applyNo + '\'' +
                    ", period='" + period + '\'' +
                    ", cardNo='" + cardNo + '\'' +
                    ", bankCardPhone='" + bankCardPhone + '\'' +
                    ", bankBranchName='" + bankBranchName + '\'' +
                    ", cardImgPath='" + cardImgPath + '\'' +
                    ", goodsList=" + goodsList +
                    ", bLImgPath='" + bLImgPath + '\'' +
                    ", bLInfo=" + bLInfo +
                    ", siteType=" + siteType +
                    ", cSLImgPath='" + cSLImgPath + '\'' +
                    ", cLInfo=" + cLInfo +
                    ", idCardInfo=" + idCardInfo +
                    ", marStatus='" + marStatus + '\'' +
                    ", residentStatus='" + residentStatus + '\'' +
                    ", residentYear='" + residentYear + '\'' +
                    ", highestEdu='" + highestEdu + '\'' +
                    ", gradTime='" + gradTime + '\'' +
                    ", preTaxIncome=" + preTaxIncome +
                    ", phone='" + phone + '\'' +
                    ", homeAddr='" + homeAddr + '\'' +
                    ", domicileAddr='" + domicileAddr + '\'' +
                    ", domicileType='" + domicileType + '\'' +
                    ", userSignSvgBase64=太长隐藏" +
                    ", fixedPhone='" + fixedPhone + '\'' +
                    ", email='" + email + '\'' +
                    ", mateIdCardInfo=" + mateIdCardInfo +
                    ", matePhone='" + matePhone + '\'' +
                    ", mateWork='" + mateWork + '\'' +
                    ", sitePrtyCertPathList=" + sitePrtyCertPathList +
                    ", siteContractPathList=" + siteContractPathList +
                    ", sitePhotePathList=" + sitePhotePathList +
                    ", fLinkmanName='" + fLinkmanName + '\'' +
                    ", fLinkmanRel='" + fLinkmanRel + '\'' +
                    ", fLinkmanPhone='" + fLinkmanPhone + '\'' +
                    ", sLinkmanName='" + sLinkmanName + '\'' +
                    ", sLinkmanRel='" + sLinkmanRel + '\'' +
                    ", sLinkmanPhone='" + sLinkmanPhone + '\'' +
                    '}';
        }
    }


    @Data
    public static class IdCardInfo {

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

    public static class BaseReqVo {

        /**
         * 操作用户id，前端不需要传.
         */
        @CheckUtils.PropertySkipCheck
        private String userAccountId;

        public final String getUserAccountId() {
            return userAccountId;
        }

        public final void setUserAccountId(String userAccountId) {
            this.userAccountId = userAccountId;
        }

        @Override
        public String toString() {
            return "BaseReqVo{" +
                    ", userAccountId='" + userAccountId + '\'' +
                    '}';
        }
    }

    @Data
    public static class K_VInfo {

        private String key;
        private String value;
    }

}
