package com.xiafei.tools;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.xiafei.tools.common.JsonUtil;
import com.xiafei.tools.common.encrypt.rsa.RSAUtil;
import com.xiafei.tools.sftp.Sftp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/26</P>
 * <P>UPDATE DATE: 2017/12/26</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    //    private static final JvmExCache<Integer> CACHE = new JvmExCache<>(10000L, true, false);
//    private static int COUNT = 0;
//    private static int COUNT_POOL = 0;
    @Resource
    private Sftp sftp;
//    @Resource
//    private JvmCachePool cachePool;
//    private static byte[] fileBytes;
//    @Resource
//    private SftpProperties sftpProperties;

//    @Value("${from}")
//    private String from;

    //    static {
//        final ClassPathResource classPathResource = new ClassPathResource("IMG_0004.JPG");
//        try (InputStream fis = classPathResource.getInputStream();
//             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//
//            byte[] buffer = new byte[1024 << 3];
//            int remain = 0;
//            while ((remain = fis.read(buffer)) > 0) {
//                baos.write(buffer, 0, remain);
//            }
//            fileBytes = baos.toByteArray();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
    @GetMapping("/sftp")
    public String sftpTest() throws JSchException, SftpException, IOException {
        String randomStr = UUID.randomUUID().toString();
//        final byte[] bytes = sftp.getBytes("/uploadfile/20170832/3018032110372800275000100009/FJDL103100/1/sourceFile/APPLY_CONTRACT.pdf");
        return "complete";
    }


//    @GetMapping("jvmCache")
//    public Integer jvmCacheTest() throws Exception {
//        return CACHE.getAndRefreshIfEx(() -> ++COUNT);
//    }
//
//
//    @GetMapping("jvmCachePool")
//    public Integer jvmCachePoolTest() throws Exception {
//        return cachePool.getAndRefreshIfExpire("testController", this, () -> ++COUNT_POOL);
//    }

//    @GetMapping("config")
//    public String getConfig() {
////        return from;
//    }


//    @GetMapping("sftPro")
//    public String sftpProperties() {
//        return JSONUtil.toJson(sftpProperties);
//    }


    /**
     * 金融租赁中介平台mock
     *
     * @return
     */
    @PostMapping("lease")
    public Map<String, Object> lease(@RequestBody final LeaseReqVo reqVo) {
        log.info("lease()，收到请求，内容={}", reqVo);
        final Map<String, Object> result = new TreeMap<>();
        final String service = reqVo.getService();
        if (service == null) {
            throw new RuntimeException("service空");
        }
        try {
            final boolean verify = RSAUtil.verify(reqVo.getSign(), JsonUtil.toJson(reqVo.getData()), "LEASE");
            if (!verify) {
                log.error("toFundPost(),验签失败,param={}", reqVo);
                return errorResult(RetCodeEnum.SIGN_VERIFY_FAIL.code, RetCodeEnum.SIGN_VERIFY_FAIL.desc);
            }
            switch (service) {
                case "lease_apply":
                    result.put("retCode", RetCodeEnum.SUCCESS.code);
                    result.put("resMsg", RetCodeEnum.SUCCESS.desc);
                    return result;
                case "lease_apply_query":
                    result.put("retCode", RetCodeEnum.SUCCESS.code);
                    result.put("resMsg", RetCodeEnum.SUCCESS.desc);
                    /**
                     * 0	创建成功-审核中
                     1	审核通过
                     2	审核拒绝
                     3	设备安装已确认
                     4	签名设备合同已回调
                     5	待放款
                     6	放款成功
                     7	放款失败
                     */
                    result.put("status", "0");
                    return result;
                case "lease_apply_goods_confirm":
                    result.put("retCode", RetCodeEnum.SUCCESS.code);
                    result.put("resMsg", RetCodeEnum.SUCCESS.desc);
                    return result;
                case "lease_apply_confirm":
                    result.put("retCode", RetCodeEnum.SUCCESS.code);
                    result.put("resMsg", RetCodeEnum.SUCCESS.desc);
                    return result;
                default:
                    result.put("retCode", RetCodeEnum.ERROR_SERVICE.code);
                    result.put("resMsg", RetCodeEnum.ERROR_SERVICE.desc);
                    return result;

            }
        } catch (Exception e) {
            return errorResult(RetCodeEnum.SYSTEM_ERROR.code, RetCodeEnum.ERROR_SERVICE.desc);
        }

    }


    @Data
    private static class LeaseReqVo {
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
         * 业务数据map.
         */
        private TreeMap<String, Object> data;
    }


    /**
     * 组装失败的结果.
     *
     * @param retCode
     * @return
     */
    private Map<String, Object> errorResult(final String retCode, final String retMsg) {
        final Map<String, Object> retMap = new HashMap<>();
        retMap.put("retCode", retCode);
        retMap.put("retMsg", retMsg);
        return retMap;

    }

    /**
     * <P>Description: 外部系统返回码枚举. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:   齐霞飞 </P>
     * <P>CREATE DATE: 2018/3/2</P>
     * <P>UPDATE DATE: 2018/3/2</P>
     *
     * @author qixiafei
     * @version 1.0
     * @since java 1.8.0
     */
    public enum RetCodeEnum {

        SUCCESS("000000", "操作成功"),
        SIGN_VERIFY_FAIL("000001", "验签失败"),
        ERROR_SERVICE("000002", "调用的接口不存在"),
        PARAM_ILLEGAL("000003", "参数校验失败"),
        APPLY_ORDER_NX("000004", "申请单不存在"),
        SYSTEM_ERROR("000005", "系统异常");

        /**
         * 外部系统返回编码.
         */
        public final String code;

        /**
         * 外部系统返回码描述.
         */
        public final String desc;

        RetCodeEnum(final String code, final String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

}
