package com.xiafei.tools.html;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <P>Description: 编程式Velocity模板渲染器. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/18</P>
 * <P>UPDATE DATE: 2017/12/18</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class VmResolver {

    /**
     * 使用velocity引擎解析指定路径下的文件模板.
     *
     * @param inputStream 模板文件内容输入流
     * @param params      参数
     * @return 解析后的结果
     */
    public static String parse(InputStream inputStream, Map<String, Object> params) {

        // 初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        VelocityContext context = new VelocityContext(params);
        // 输出流
        StringWriter writer = new StringWriter();
        // 转换输出
        ve.evaluate(context, writer, "", new InputStreamReader(inputStream)); // 关键方法
        return writer.toString();
    }

    /**
     * 使用velocity引擎解析指定路径下的文件模板
     *
     * @param template 模板文件内容
     * @param params   参数
     * @return 解析后的结果
     */
    public static String parse(String template, Map<String, Object> params) {

        // 初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        VelocityContext context = new VelocityContext(params);
        // 输出流
        StringWriter writer = new StringWriter();
        // 转换输出
        ve.evaluate(context, writer, "", template); // 关键方法

        return writer.toString();
    }

    /**
     * 使用velocity引擎解析指定路径下的文件模板
     *
     * @param template 模板文件内容
     * @param params   参数
     * @return 解析后的结果
     */
    public static String parse(byte[] template, Map<String, Object> params) {

        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(template);
            return parse(bis, params);

        } finally {
            IOUtils.closeQuietly(bis);
        }

    }

    public static void main(String[] args) {
        Map<String, Object> context = new HashMap<>();
        final Map<String, String> loanAuditDetail = new HashMap<>();
        context.put("loanAuditDetail", loanAuditDetail);
        loanAuditDetail.put("loanApplyNo", "loanApplyNoXIXI");
        loanAuditDetail.put("userAccountId", "userAccountIdXIXI");

        System.out.println(parse("<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script type=\"text/javascript\" data-main=\"/static/js/page/audit/loanAudit\"  src=\"/static/js/modules/require.js\"></script>\n" +
                "    <style>\n" +
                "        .s_one label{float:none;display: inline-block;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"content\">\n" +
                "        <h2 style=\"text-align: center;\" class=\"aduit_title\"><span class=\"displayName_title\"></span><span class=\"op_tip\"></span></h2>\n" +
                "        <div class=\"search border\">\n" +
                "            <input type=\"hidden\" class=\"loanApplyNo\" value=\"$!{loanAuditDetail.loanApplyNo}\"/>\n" +
                "            <input type=\"hidden\" class=\"userAccountId\" value=\"$!{loanAuditDetail.userAccountId}\"/>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">借款订单号：</label>\n" +
                "                <span class=\"totalAmount\">$!{loanAuditDetail.loanApplyNo}</span>\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">合同信息：</label>\n" +
                "                <span class=\"displayName\">\n" +
                "                    #foreach ($contract in $loanAuditDetail.fileInfos)\n" +
                "                        <a href=\"javascript:void(0);\" fileUploadPath=\"$!{contract.fileAddress}\" class=\"contractInfo\" style=\"width:100px;cursor:pointer\">$!{contract.fileName}</a>\n" +
                "                    #end\n" +
                "                </span>\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">受托支付金额：</label>\n" +
                "                <span class=\"totalAmount\">$!BigDecimalUtils.roundingAnd2validData($!{loanAuditDetail.loanAmt})</span>元\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">客户姓名：</label>\n" +
                "                <span class=\"totalAmount\">$!{loanAuditDetail.userAccountName}</span>\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">客户身份证号：</label>\n" +
                "                <span class=\"marketId\">$!{loanAuditDetail.idCardNo}</span>\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">收款账户名称：</label>\n" +
                "                <span class=\"tagID\">$!{loanAuditDetail.loanAccountName}</span>\n" +
                "            </div>\n" +
                "            <div class=\"clear\"></div>\n" +
                "            <div class=\"s_one\">\n" +
                "                <label for=\"\">收款账号：</label>\n" +
                "                <span class=\"tagID\">$!{loanAuditDetail.loanAccount}</span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"border\">\n" +
                "            <form class=\"op-form\" id=\"aduit-form\">\n" +
                "                <div class=\"s_one\">\n" +
                "                    <label for=\"\">审核意见：</label>\n" +
                "                    <label class=\"lable\"><input type=\"radio\" class=\"pass\" name=\"pass\" value=\"201\">通过</label>\n" +
                "                    <label class=\"lable\"><input type=\"radio\" class=\"pass\" name=\"pass\" value=\"202\">拒绝</label>\n" +
                "                    #if(${isDataUpload})\n" +
                "                        <label class=\"lable\"><input type=\"radio\" class=\"pass\" name=\"pass\" value=\"102\">审核退回</label>\n" +
                "                    #end\n" +
                "                </div>\n" +
                "                <div class=\"clear\"></div>\n" +
                "                <div class=\"s_one\">\n" +
                "                    <label for=\"\" style=\"float:left\">备注：</label>\n" +
                "                    <textarea class=\"text decisionView\"></textarea>\n" +
                "                </div>\n" +
                "                <div class=\"clear\"></div>\n" +
                "                <div class=\"s_one\">\n" +
                "                    <div class=\"s_btn submit_btn\">提交</div>\n" +
                "                    <div class=\"s_btn cancel_btn\">取消</div>\n" +
                "                </div>\n" +
                "            </form>\n" +
                "            <div class=\"clear\"></div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <!-- 显示Base64图片的popup begin-->\n" +
                "    <div class=\"pop\" id=\"barcode-base64\" style=\"display:none;\">\n" +
                "        <div class=\"pop_bg\"></div><!--遮罩-->\n" +
                "        <div class=\"pop_box\">\n" +
                "            <h2 class=\"pop_title\">影像信息</h2>\n" +
                "            <div >\n" +
                "                <img class=\"middleImage\"  src=\"\" alt=\"没有找到图片\" />\n" +
                "            </div>\n" +
                "            <div class=\"s_one center-wrapper mar20\" style=\"margin-top:30px;\">\n" +
                "                <div class=\"s_btn pop_close\" style=\"width: 200px;\">关闭</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>", context));
    }
}
