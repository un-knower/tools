package com.xiafei.tools.pdf;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>Description: 生成pdf工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/1</P>
 * <P>UPDATE DATE: 2018/3/1</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class PdfUtils {
    public static final String KEYSTORE = "./temp/dlt.p12";
    public static final char[] PASSWORD = "111111".toCharArray();//keystory密码

    private static MyFontsProvider PRI_FONT;

    static {
        PRI_FONT = new MyFontsProvider();
        PRI_FONT.addFontSubstitute("lowagie", "garamond");
        PRI_FONT.setUseUnicode(true);
    }


    public static void main(String[] args) throws Exception {


//        html2Pdf(new FileInputStream(new File("./temp/contract.vm")), new FileOutputStream(new File("./temp/test.pdf")));
//        try {
//            //读取keystore ，获得私钥和证书链
//            KeyStore ks = KeyStore.getInstance("PKCS12");
//            ks.load(new FileInputStream(KEYSTORE), PASSWORD);
//            String alias = (String) ks.aliases().nextElement();
//            PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
//            Certificate[] chain = ks.getCertificateChain(alias);
//            long start = System.currentTimeMillis();
//            signPdf(new FileInputStream(new File("./temp/test.pdf")),
//                    new FileOutputStream(new File("./temp/test-signed.pdf")),
//                    StreamUtil.getBytes(new FileInputStream("./temp/Chrysanthemum.jpg")),
//                    chain, pk, DigestAlgorithms.SHA1,
//                    null,
//                    MakeSignature.CryptoStandard.CMS,
//                    "qixiafei ceshi",
//                    "beijing",
//                    "jxjzsignhere", true);
//            System.out.println("elapse time " + (System.currentTimeMillis() - start));
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

//        pdf2Img(new FileInputStream(new File("./temp/APPLY_CONTRACT.pdf")), new FileOutputStream(new File("./temp/APPLY_CONTRACT-img.jpg")));
    }


    public static byte[] html2Pdf(final String html) {
        final InputStream is = IOUtils.toInputStream(html);
        return html2Pdf(is);
    }

    public static byte[] html2Pdf(final InputStream in) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        html2Pdf(in, os);
        return os.toByteArray();
    }


    /**
     * 使用 iText XML Worker实现HTML转PDF
     * itextpdf-5.5.6.jar
     *
     * @param htmlIn html输入流
     * @param pdfOut pdf输出流
     */
    public static void html2Pdf(final InputStream htmlIn, final OutputStream pdfOut) {

        // 声明文档
        final Document document = new Document();
        try {
            final PdfWriter pdfwriter = PdfWriter.getInstance(document, pdfOut);
            pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
            // open一定要在pdfwriter声明之后，否则不生效
            document.open();

            final CssAppliers cssAppliers = new CssAppliersImpl(PRI_FONT);
            final HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            // 输出到文档
            XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
            XMLWorkerHelper.getInstance().parseXHtml(pdfwriter, document, htmlIn, null,
                    Charset.forName("UTF-8"), PRI_FONT);
            // close会将流刷新到文档，一定要有
            document.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 给pdf加签章.
     *
     * @param pdfIn           待签名pdf输入流
     * @param signedPdfOut    完成签名pdf输出流
     * @param chain           证书链
     * @param pk              签名私钥
     * @param digestAlgorithm 摘要算法名称，例如SHA-1
     * @param provider        密钥算法提供者，可以为null
     * @param subfilter       数字签名格式，itext有2种
     * @param reason          签名的原因，显示在pdf签名属性中，随便填
     * @param location        签名的地点，显示在pdf签名属性中，随便填
     * @param keyword         签名要签在这个关键词所在的位置
     * @throws IOException
     * @throws DocumentException
     */
    public static void signPdf(final InputStream pdfIn, final OutputStream signedPdfOut, final byte[] signedImg,
                               final Certificate[] chain, final PrivateKey pk, final String digestAlgorithm,
                               final String provider, final MakeSignature.CryptoStandard subfilter,
                               final String reason, final String location, final String keyword, final boolean append)
            throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(pdfIn);
        final List<Location> locs = findKeyWord(reader, keyword);
        final int locSum = locs.size();
        if (locSum == 0) {
            // 如果没有找到标志位，直接在第一页上盖上签章完事儿
            final Location loc = new Location();
            loc.setX(0.0f);
            loc.setY(0.0f);
            loc.setPage(1);
            signInner(null, signedPdfOut, signedImg, chain, pk, digestAlgorithm, provider, subfilter, reason, location, loc, append, "sign0", reader);
        } else if (locSum == 1) {
            // 如果只有一个标志位，签完就完事儿
            signInner(null, signedPdfOut, signedImg, chain, pk, digestAlgorithm, provider, subfilter, reason, location, locs.get(0), append, "sign0", reader);
        } else {
            // 如果有多个标志位，需要循环处理
            ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
            // 首先第一次签名将结果放入管道输出流，写死可以
            signInner(null, tempOut, signedImg, chain, pk, digestAlgorithm, provider, subfilter, reason, location, locs.get(0), true, "sign0", reader);
            byte[] tempBytes = tempOut.toByteArray();
            tempOut.close();
            tempOut = new ByteArrayOutputStream();
            for (int i = 1; i < locs.size(); i++) {
                if (i == locs.size() - 1) {
                    signInner(tempBytes, signedPdfOut, signedImg, chain, pk, digestAlgorithm, provider, subfilter, reason, location, locs.get(i), append, "sign" + i, null);
                } else {
                    signInner(tempBytes, tempOut, signedImg, chain, pk, digestAlgorithm, provider, subfilter, reason, location, locs.get(i), true, "sign" + i, null);
                    tempBytes = tempOut.toByteArray();
                    tempOut.close();
                    tempOut = new ByteArrayOutputStream();
                }
            }

        }

    }

    /**
     * pdf转图片（多张会转成一张长图）.
     *
     * @param pdfIn  pdf输入流
     * @param imgOut 图片输出流
     * @throws Exception 各种异常
     */
    public static void pdf2Img(final InputStream pdfIn, final OutputStream imgOut) {
        try {
            final org.icepdf.core.pobjects.Document document = new org.icepdf.core.pobjects.Document();
            document.setInputStream(pdfIn, "");
            final float scale = 1.1f;// 缩放比例（大图）
            // final float scale = 0.2f;// 缩放比例（小图）
            final float rotation = 0f;// 旋转角度
            final int num = document.getNumberOfPages();
            final List<BufferedImage> images = new ArrayList<>(num);
            for (int i = 0; i < num; i++) {
                final BufferedImage image = (BufferedImage) document.getPageImage(i,
                        GraphicsRenderingHints.SCREEN,
                        org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX,
                        rotation, scale);
                images.add(image);
            }
            toOnePic(images, imgOut);
            document.dispose();
            IOUtils.closeQuietly(pdfIn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 将多张图片合并成一张长图.
     *
     * @param images 多张图片
     * @param imgOut 图片输出流
     */
    private static void toOnePic(final List<BufferedImage> images, final OutputStream imgOut) {
        if (images == null || images.isEmpty()) {
            throw new RuntimeException("图片为空");
        }

        int height = 0;
        int width = 0;
        for (BufferedImage image : images) {
            if (width < image.getWidth()) {
                width = image.getWidth();
            }
            height += image.getHeight();
        }
        // 将以上图片合并为一张
        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int heightOffset = 0;
        for (BufferedImage image : images) {
            final int oneHeight = image.getHeight();
            final int[] rgb = new int[width * oneHeight];
            image.getRGB(0, 0, width, oneHeight, rgb, 0, width);
            result.setRGB(0, heightOffset, width, oneHeight, rgb, 0, width);
            heightOffset += oneHeight;
        }
        try {
            ImageIO.write(result, "png", imgOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void signInner(final byte[] pdfIn, final OutputStream signedPdfOut, final byte[] signedImg,
                                  final Certificate[] chain, final PrivateKey pk, final String digestAlgorithm,
                                  final String provider, final MakeSignature.CryptoStandard subfilter,
                                  final String reason, final String locationNote, final Location location,
                                  final boolean append, final String serialName,
                                  PdfReader reader) throws IOException, DocumentException {
        if (reader == null) {
            reader = new PdfReader(pdfIn);
        }
        //创建签章工具PdfStamper ，最后一个boolean参数
        //false的话，pdf文件只允许被签名一次，多次签名，最后一次有效
        //true的话，pdf可以被追加签名，验签工具可以识别出每次签名之后文档是否被修改
        PdfStamper stamper = PdfStamper.createSignature(reader, signedPdfOut, '\0', null, append);
        // 获取数字签章属性对象，设定数字签章的属性
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(locationNote);
        //设置签名的位置，页码，签名域名称，多次追加签名的时候，签名预名称不能一样
        //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
        //四个参数的分别是，图章左下角x，图章左下角y，图章右上角x，图章右上角y
        final float diam = 50; // 正方形边长
        final float radius = diam / 2; // 正方形边长的一半
        // 这样定义签章图片就在定位符起始位置开始,让定位符穿过中心，上下各露出一些的位置
        final float llx = location.getX();
        final float lly = location.getY() - radius > 0 ? location.getY() - radius : 0;
        appearance.setVisibleSignature(new Rectangle(llx, lly, llx + diam, lly + diam), 1, serialName);
        //读取图章图片，这个image是itext包的image
        Image image = Image.getInstance(signedImg);
        appearance.setSignatureGraphic(image);
        appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
        //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        // 这里的itext提供了2个用于签名的接口，可以自己实现，后边着重说这个实现
        // 摘要算法
        ExternalDigest digest = new BouncyCastleDigest();
        // 签名算法
        ExternalSignature signature = new PrivateKeySignature(pk, digestAlgorithm, null);
        // 调用itext签名方法完成pdf签章
        try {
            MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, subfilter);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在pdf中查找关键字.
     *
     * @param pdfReader pdfReader
     * @param keyword   关键字，中文尚未支持
     * @return 找到的关键字位置列表
     * @throws IOException
     */
    private static List<Location> findKeyWord(final PdfReader pdfReader, final String keyword) throws IOException {
        final List<Location> result = new ArrayList<>();
        final int pageNum = pdfReader.getNumberOfPages();
        final PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);
        for (int i = 1; i <= pageNum; i++) {
            final int page = i;
            parser.processContent(i, new RenderListener() {
                @Override
                public void renderText(final TextRenderInfo textInfo) {
                    String text = textInfo.getText();
                    if (null != text && text.contains(keyword)) {
                        Rectangle2D.Float boundingRectange = textInfo.getBaseline().getBoundingRectange();
                        final Location item = new Location();
                        item.setX(boundingRectange.x);
                        item.setY(boundingRectange.y);
                        item.setPage(page);
                        result.add(item);
                    }
                }

                @Override
                public void beginTextBlock() {

                }

                @Override
                public void endTextBlock() {

                }

                @Override
                public void renderImage(final ImageRenderInfo renderInfo) {

                }
            });

        }
        return result;
    }


    @Data
    private static class Location {
        private float x;
        private float y;
        private int page;
    }

    /**
     * 重写 字符设置方法，解决中文乱码问题
     */
    private static class MyFontsProvider extends XMLWorkerFontProvider {

        private MyFontsProvider() {
            super(null, null);
        }

        @Override
        public Font getFont(final String fontname, String encoding, float size, final int style) {
            String fntname = fontname;
            if (fntname == null) {
                fntname = "宋体";
            }
            if (size == 0) {
                size = 4;
            }
            return super.getFont(fntname, encoding, size, style);
        }
    }
}
