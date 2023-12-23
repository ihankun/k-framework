package io.ihankun.framework.poi.pdf.pdfbox.doc;

import org.apache.pdfbox.cos.COSName;

/**
 * pdf文档签名过滤器
 *
 * @author hankun
 */
public class XEasyPdfDocumentSignFilter {
    /**
     * 过滤器
     */
    public enum Filter {

        /**
         * Adobe.PPKLite
         */
        FILTER_ADOBE_PPKLITE(COSName.ADOBE_PPKLITE),
        /**
         * Entrust.PPKEF
         */
        FILTER_ENTRUST_PPKEF(COSName.ENTRUST_PPKEF),
        /**
         * CICI.SignIt
         */
        FILTER_CICI_SIGNIT(COSName.CICI_SIGNIT),
        /**
         * VeriSign.PPKVS
         */
        FILTER_VERISIGN_PPKVS(COSName.VERISIGN_PPKVS);

        /**
         * 过滤器
         */
        private final COSName filter;

        /**
         * 有参构造
         *
         * @param filter 过滤器
         */
        Filter(COSName filter) {
            this.filter = filter;
        }

        /**
         * 获取过滤器
         *
         * @return 返回过滤器
         */
        public COSName getFilter() {
            return filter;
        }
    }

    /**
     * 子过滤器
     */
    public enum SubFilter {

        /**
         * adbe.x509.rsa_sha1
         */
        SUBFILTER_ADBE_X509_RSA_SHA1(COSName.ADBE_X509_RSA_SHA1),
        /**
         * adbe.pkcs7.detached
         */
        SUBFILTER_ADBE_PKCS7_DETACHED(COSName.ADBE_PKCS7_DETACHED),
        /**
         * ETSI.CAdES.detached
         */
        SUBFILTER_ETSI_CADES_DETACHED(COSName.getPDFName("ETSI.CAdES.detached")),
        /**
         * adbe.pkcs7.sha1
         */
        SUBFILTER_ADBE_PKCS7_SHA1(COSName.ADBE_PKCS7_SHA1);

        /**
         * 过滤器
         */
        private final COSName filter;

        /**
         * 有参构造
         *
         * @param filter 过滤器
         */
        SubFilter(COSName filter) {
            this.filter = filter;
        }

        /**
         * 获取过滤器
         *
         * @return 返回过滤器
         */
        public COSName getFilter() {
            return filter;
        }
    }
}
